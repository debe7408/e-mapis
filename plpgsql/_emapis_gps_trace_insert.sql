CREATE OR REPLACE FUNCTION _emapis_gps_trace_insert(trip_id BIGINT, points DOUBLE PRECISION[])
RETURNS TEXT AS 
$$
DECLARE
	x integer := 2;
	y integer := 1;
	z integer := 3;
	array_is_read BOOLEAN := 'f';
	generated_geom GEOMETRY;
	last_geom GEOMETRY;
	route_length DOUBLE PRECISION;
	all_distance DOUBLE PRECISION := 0;
BEGIN

	WHILE array_is_read = 'f' LOOP
	
		IF $2[x] IS NULL THEN
			RETURN '2';
		END IF;
		
		IF $2[x] != 0 THEN
		
			generated_geom = ( SELECT ST_SetSRID(ST_MakePoint($2[x], $2[y]), 4326) );
		
			IF last_geom IS NULL THEN 
				INSERT INTO trip_log (trip_id, latitude, longitude, altitude, geom)
				VALUES ($1, $2[x], $2[y], $2[z], generated_geom);
			ELSE 
				route_length = ( SELECT ST_DistanceSphere(last_geom, generated_geom) );
				all_distance = all_distance + route_length;
				INSERT INTO trip_log (trip_id, latitude, longitude, altitude, geom, var_dis)
				VALUES ($1, $2[x], $2[y], $2[z], generated_geom, route_length);
			END IF;
		
			last_geom = generated_geom;
			
		END IF;
		
		x := x + 3;
		y := y + 3;
		z := z + 3;
		
		IF $2[x] IS NULL THEN
			array_is_read = 't';
			RETURN all_distance::TEXT;
		END IF;
	
	END LOOP;
END
$$
LANGUAGE plpgsql;