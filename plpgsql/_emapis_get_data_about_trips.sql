CREATE OR REPLACE VIEW _emapis_get_data_about_trip AS (  
    SELECT 
        t.trip_id,
        t.stats_ready,
        date(t.trip_start_time) AS date,
        v.make,
        v.model,
        COALESCE(t.trip_distance, 0::double precision) AS trip_distance,
        age(t.trip_end_time, t.trip_start_time) AS trip_total_time,
        COALESCE(t.consumed_energy, 0::double precision) AS consumed_energy,
        COALESCE(t.average_consumption, 0::double precision) AS avg_consumption,
        COALESCE(v.declared_consumption, 0::double precision) AS declared_consumption,
        COALESCE(v.average_consumption, 0::double precision) AS accumulated_consumption,
        COALESCE(t.avg_temp, 999.0::double precision) AS trip_temp
    FROM trips t
        JOIN vehicles v ON t.vehicle_id = v.vehicle_id );