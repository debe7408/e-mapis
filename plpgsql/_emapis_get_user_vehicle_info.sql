CREATE OR REPLACE VIEW _emapis_get_user_vehicle_info AS (
    SELECT 
        uv.user_vehicle_id,
        uv.user_id,
        uv.vehicle_alias,
        uv.real_consumption,
        uv.vehicle_id,
        uv.no_of_trips,
        uv.total_distance,
        uv.total_consumed_energy,
        uv.average_consumption,
        v.make,
        v.model,
        v.declared_consumption,
        v.average_consumption AS accumulated_consumption
    FROM user_vehicles uv
        JOIN vehicles v ON uv.vehicle_id = v.vehicle_id;
)