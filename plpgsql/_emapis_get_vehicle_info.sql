CREATE OR REPLACE VIEW _emapis_get_vehicle_info AS (
    SELECT 
        vehicles.vehicle_id,
        vehicles.make,
        vehicles.model,
        COALESCE(vehicles.year, 0) AS year,
        vehicles.declared_consumption,
        COALESCE(vehicles.average_consumption, 0::double precision) AS real_consumption,
        COALESCE(vehicles.traveled_distance, 0::double precision) AS total_distance,
        COALESCE(vehicles.total_no_of_trips, 0) AS total_no_of_trips
    FROM vehicles;
)