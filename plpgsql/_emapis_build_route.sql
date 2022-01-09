CREATE OR REPLACE FUNCTION _emapis_build_route( trip_id bigint )
RETURNS VOID AS
$$
    DECLARE 
    -- FIP SECTION VARIABLES
    fpi_p1 GEOMETRY;
	fpi_p2 GEOMETRY;
	fpi_p3 GEOMETRY;
	fpi_gid1 BIGINT;
	fpi_gid2 BIGINT;
	fpi_gid3 BIGINT;
	fpi_dis1 FLOAT;
	fpi_dis2 FLOAT;
    fpi_trip_log_row trip_log%rowtype; 
    fpi_offset INTEGER := 0;
    fpi_done BOOLEAN := 'f';
    fpi_iterator INTEGER;
    fpi_trip_log_id_1 BIGINT;
    fpi_trip_log_id_2 BIGINT;
    fpi_trip_log_id_3 BIGINT;

    -- PI SECTION VARIABLES
    previous_row trip_log%rowtype; -- Previous rightfully updated row
    current_row trip_log%rowtype; -- Current processing row
    rm_ways_row ways_for_matching%ROWTYPE; -- Currently needed ways table row

    -- RM SECTION VARIABLES
	rm_uncut_route GEOMETRY; -- Unsnapped OSM route
	rm_osm_route GEOMETRY; -- Snapped OSM route
	rm_osm_route_length FLOAT; -- OSM route length
	rm_non_osm_route GEOMETRY; -- Non OSM route
    rm_non_osm_route_length FLOAT; -- Non OSM route length
	rm_length_difference FLOAT; -- Legth difference between routes
	rm_route_type TEXT; -- Determine geometry type of a route

    total_route_distance FLOAT; --Total distance of whole route
    whole_route GEOMETRY; -- Whole route linestring
	
	-- STATISTICS
	st_first_input INTEGER;
	st_last_input INTEGER;
	st_before_recharge INTEGER;
	st_after_recharge INTEGER;
	st_used_car_id BIGINT;
	st_user_car_id BIGINT;
	st_car BIGINT;
	st_battery_size FLOAT; -- Car battery size in KWh
	st_consumption FLOAT; -- Consumption on this trip in Wh/km
	st_temperature FLOAT;
	st_used_wh FLOAT; -- Used energy on this trip in wh
	st_no_of_trips INTEGER;
	st_all_trips_distance FLOAT;
	st_total_consumed_energy FLOAT;
	st_trip_start_time TIMESTAMP;
	st_trip_end_time TIMESTAMP;
	st_total_model_distance FLOAT;
	st_total_model_energy FLOAT;
	st_total_model_trips INTEGER;

BEGIN
    -- Finding first right point, it's GID ( FIP SECTION )

    -- Check if right point is found
    WHILE fpi_done = 'f' LOOP 

        fpi_iterator = 1; 
        
        -- Iterate through 3 points to select 3 for checking
        FOR fpi_trip_log_row in SELECT * FROM trip_log WHERE trip_log.trip_id = $1 ORDER BY trip_log.trip_log_id ASC OFFSET fpi_offset LIMIT 3 LOOP
            
            

            IF fpi_iterator = 1 THEN
                fpi_p1 = fpi_trip_log_row.geom;
                fpi_trip_log_id_1 = fpi_trip_log_row.trip_log_id;
            ELSIF fpi_iterator = 2 THEN
                fpi_p2 = fpi_trip_log_row.geom;
                fpi_trip_log_id_1 = fpi_trip_log_row.trip_log_id;
            ELSE 
                fpi_p3 = fpi_trip_log_row.geom;
                fpi_trip_log_id_1 = fpi_trip_log_row.trip_log_id;
            END IF;

            fpi_iterator = fpi_iterator + 1;
			

        END LOOP;

        -- Set offset for next 3 points
        fpi_offset = fpi_offset + 3;

        -- Calculate distance between 3 points
        fpi_dis1 = ST_DistanceSphere( fpi_p1, fpi_p2 );
	    fpi_dis2 = ST_DistanceSphere( fpi_p2, fpi_p3 );

        -- Check if distance between any 2 points is greater than 12 meters (Accuracy of our GPS)
        IF fpi_dis1 > 12 OR fpi_dis2 > 12 THEN
            fpi_gid1 = ( SELECT gid FROM public.ways_for_matching ORDER BY the_geom <-> fpi_p1 LIMIT 1 );
	        fpi_gid2 = ( SELECT gid FROM public.ways_for_matching ORDER BY the_geom <-> fpi_p2 LIMIT 1 );
	        fpi_gid3 = ( SELECT gid FROM public.ways_for_matching ORDER BY the_geom <-> fpi_p3 LIMIT 1 );

			
            -- Check if any points match 
            IF fpi_gid1 = fpi_gid2 OR fpi_gid1 = fpi_gid3 THEN 
			
                UPDATE trip_log 
                SET osm_id = ( SELECT osm_id FROM public.ways_for_matching WHERE ways_for_matching.gid = fpi_gid1 ),
                    source = ( SELECT source FROM public.ways_for_matching WHERE ways_for_matching.gid = fpi_gid1 )
                WHERE trip_log.trip_log_id = fpi_trip_log_id_1;
				
				

                -- Select row which has normal first point
                SELECT * INTO previous_row FROM trip_log WHERE trip_log.trip_log_id = fpi_trip_log_id_1;
				
			
				
                fpi_done = 't';

            ELSIF fpi_gid2 = fpi_gid3 THEN
                UPDATE trip_log 
                SET osm_id = ( SELECT osm_id FROM public.ways_for_matching WHERE ways_for_matching.gid = fpi_gid2 ),
                    source = ( SELECT source FROM public.ways_for_matching WHERE ways_for_matching.gid = fpi_gid2 )
                WHERE trip_log.trip_log_id = fpi_trip_log_id_2;
				
			
                -- Select row which has normal first point
                SELECT * INTO previous_row FROM trip_log WHERE trip_log.trip_log_id = fpi_trip_log_id_2;

                fpi_done = 't';

            END IF;
        
        END IF;

    END LOOP;


    -- Route matching sectio (RM SECTION)

    -- Loop to iterate through all inserted points with given trip_id
    FOR current_row in SELECT * FROM trip_log WHERE trip_log.trip_id = $1 ORDER BY trip_log.trip_log_id ASC OFFSET fpi_offset LOOP
		
		
        -- Get a row from ways table 
        SELECT * INTO rm_ways_row FROM ways_for_matching ORDER BY ways_for_matching.the_geom <-> current_row.geom ASC LIMIT 1;
		
        -- Build a route from previous point to current one with OSM data
        rm_uncut_route = ( SELECT ST_LineMerge(( SELECT st_union from ST_UNION( array (SELECT osm_ways.the_geom FROM pgr_astar(
    				  	'SELECT gid as id, source, target, cost, reverse_cost, x1, y1, x2, y2 FROM ways_for_matching',
         				 previous_row.source, rm_ways_row.target) as route
						inner join ways_for_matching as osm_ways on route.edge = osm_ways.gid
						)))));
						
		rm_route_type = ( SELECT ST_GeometryType(rm_uncut_route) );	
		
		IF rm_uncut_route IS NOT NULL AND rm_route_type != 'ST_MultiLineString' THEN
					
        	-- Snap route to start and finish at exact points
        	rm_osm_route = ( SELECT
                            (ST_LineSubstring( 
                                    line,
                                    least(ST_LineLocatePoint(line, pta), ST_LineLocatePoint(line, ptb)),
                                    greatest(ST_LineLocatePoint(line, pta), ST_LineLocatePoint(line, ptb))))
                            FROM (
                                SELECT
                                ST_LineMerge(rm_uncut_route) line,
                                previous_row.geom pta,
                                current_row.geom ptb
                            ) data );
		
        	-- Calculate snapped OSM route length
        	rm_osm_route_length = ( SELECT ST_Length( ST_Transform( rm_osm_route, 26986) ) );

        	-- Build route from previous to current one without OSM data
        	rm_non_osm_route = ( SELECT ST_MakeLine( previous_row.geom,  current_row.geom ) );
        
        	-- Calculate non_osm_route length
        	rm_non_osm_route_length = ( SELECT ST_Length( ST_Transform( rm_non_osm_route, 26986) ) );
		
        	IF rm_osm_route_length != 0 AND rm_non_osm_route_length != 0 AND rm_osm_route_length IS NOT NULL THEN
				
				
                	-- Calculate 2 routes lengths difference
					rm_length_difference = abs ( (rm_osm_route_length - rm_non_osm_route_length) / ((rm_osm_route_length + rm_non_osm_route_length) / 2) ) * 100;
			
						IF rm_length_difference <= 40 THEN
						
                        	UPDATE trip_log
                        	SET gid = rm_ways_row.gid,
                            	source = rm_ways_row.source,
                            	target = rm_ways_row.target,
                            	route_from_prev = rm_osm_route,
                            	osm_route_length = rm_osm_route_length
                        	WHERE trip_log.trip_log_id = current_row.trip_log_id;

                        	-- Select this row to be previous for next iteration
                        	SELECT * INTO previous_row FROM trip_log WHERE trip_log.trip_log_id = current_row.trip_log_id;

						END IF;
					
	    	END IF;
		
		END IF;

    END LOOP;

    -- Statistics section

    -- Calculate total trip distance
    total_route_distance = ( SELECT SUM ( COALESCE( osm_route_length,0) ) FROM trip_log WHERE trip_log.trip_id = $1 );

    -- Get and merge all route pieces into one linestring
    whole_route = ( SELECT ST_LineMerge( ST_Collect (route_from_prev)) FROM trip_log WHERE trip_log.trip_id = $1 AND route_from_prev IS NOT NULL);
	rm_route_type = ( SELECT ST_GeometryType(whole_route) );
	
	st_first_input = ( SELECT SUM(input_value) FROM trip_log_meta WHERE trip_log_meta.trip_id = $1 AND input_key = 'first_input' );
	st_last_input = ( SELECT SUM(input_value) FROM trip_log_meta WHERE trip_log_meta.trip_id = $1 AND input_key = 'last_input' );
	st_before_recharge = ( SELECT SUM(input_value) FROM trip_log_meta WHERE trip_log_meta.trip_id = $1 AND input_key = 'before_recharge ' );
	st_after_recharge = ( SELECT SUM(input_value) FROM trip_log_meta WHERE trip_log_meta.trip_id = $1 AND input_key = 'after_recharge ' );
	
	st_used_car_id = ( SELECT vehicle_id FROM trips WHERE trips.trip_id = $1 );
	st_battery_size = ( SELECT battery_size FROM vehicles WHERE vehicles.vehicle_id = st_used_car_id ) * 1000;
	
	IF st_before_recharge IS NULL OR st_after_recharge IS NULL THEN 
		st_consumption = ( ( st_first_input - st_last_input )::FLOAT / 100 ) * ( st_battery_size::FLOAT ) / (total_route_distance / 1000);
		st_used_wh = ( (( st_first_input - st_last_input )::FLOAT / 100 ) * ( st_battery_size::FLOAT ) );
	ELSE
		st_consumption = ( (( st_first_input - st_last_input )::FLOAT + ( st_after_recharge - st_before_recharge )::FLOAT) / 100 ) * ( st_battery_size::FLOAT ) / ( total_route_distance / 1000 );
		st_used_wh = ( ((( st_first_input - st_last_input )::FLOAT + ( st_after_recharge - st_before_recharge )::FLOAT) / 100 ) * ( st_battery_size::FLOAT ) );
	
	END IF;
	
	st_temperature = ( SELECT AVG(input_value) FROM trip_log_meta WHERE trip_log_meta.trip_id = $1 AND input_key = 'temperature_input' );
	
	st_trip_start_time = ( SELECT time AS start FROM trip_log WHERE trip_log.trip_id = $1 ORDER BY time ASC LIMIT 1 );
	st_trip_end_time = ( SELECT time AS end FROM trip_log WHERE trip_log.trip_id = $1 ORDER BY time DESC LIMIT 1 );
	
	
	IF rm_route_type = 'ST_MultiLineString' THEN
		UPDATE trips
    	SET trip_distance = total_route_distance, 
        trip_multi_geometry = whole_route,
        stats_ready = true,
		average_consumption = st_consumption,
		avg_temp = st_temperature,
		consumed_energy = st_used_wh,
		trip_start_time = st_trip_start_time,
		trip_end_time = st_trip_end_time
    	WHERE trips.trip_id = $1;
	ELSE
		UPDATE trips
    	SET trip_distance = total_route_distance, 
        trip_geometry = whole_route,
        stats_ready = true,
		average_consumption = st_consumption,
		avg_temp = st_temperature,
		consumed_energy = st_used_wh,
		trip_start_time = st_trip_start_time,
		trip_end_time = st_trip_end_time
    	WHERE trips.trip_id = $1;
	END IF;
	
	st_user_car_id = ( SELECT user_vehicle_id FROM trips WHERE trips.trip_id = $1 ); 
	
	st_no_of_trips = ( SELECT COUNT(*) FROM trips WHERE user_vehicle_id = st_user_car_id );
	st_all_trips_distance = ( SELECT SUM( COALESCE( trip_distance, 0 ) ) FROM trips WHERE user_vehicle_id = st_user_car_id );
	st_total_consumed_energy = ( SELECT SUM ( COALESCE( consumed_energy, 0 )) FROM trips WHERE user_vehicle_id = st_user_car_id );
	
	UPDATE user_vehicles
	SET no_of_trips = st_no_of_trips,
	total_distance = st_all_trips_distance,
	total_consumed_energy = st_total_consumed_energy
	WHERE user_vehicle_id = st_user_car_id;
	
	st_total_model_distance = ( SELECT SUM( COALESCE( trip_distance, 0 ) ) FROM trips WHERE trips.vehicle_id = st_used_car_id );
	st_total_model_energy = ( SELECT SUM( COALESCE( consumed_energy, 0 ) ) FROM trips WHERE trips.vehicle_id = st_used_car_id );
	st_total_model_trips = ( SELECT COALESCE( COUNT( * ), 0 ) FROM trips WHERE trips.vehicle_id = st_used_car_id );
	
	UPDATE vehicles
	SET traveled_distance = st_total_model_distance,
	consumed_energy = st_total_model_energy,
	total_no_of_trips = st_total_model_trips
	WHERE vehicle_id = st_used_car_id;
	
END
$$
LANGUAGE plpgsql;