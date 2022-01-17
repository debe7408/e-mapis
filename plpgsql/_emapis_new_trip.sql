CREATE OR REPLACE FUNCTION _emapis_new_trip(user_id BIGINT, user_vehicle_id BIGINT)
RETURNS BIGINT AS
$$
DECLARE
    var_vehicle_id bigint;
    trip_id_return bigint;
BEGIN
    var_vehicle_id = ( SELECT vehicle_id FROM user_vehicles WHERE user_vehicles.user_vehicle_id = $2 );
    INSERT INTO trips (user_id, user_vehicle_id, vehicle_id)
    VALUES ($1, $2, var_vehicle_id) RETURNING trip_id INTO trip_id_return;
    RETURN trip_id_return;
END
$$
LANGUAGE plpgsql;