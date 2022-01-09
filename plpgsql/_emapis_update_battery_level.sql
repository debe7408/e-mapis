CREATE OR REPLACE FUNCTION _emapis_update_battery_level(trip_id BIGINT, input_key TEXT, input_value INTEGER)
RETURNS VOID AS
$$
    DECLARE
	    trip_log_id_var BIGINT;
    BEGIN 
	    trip_log_id_var = ( SELECT trip_log_id FROM trip_log WHERE trip_log.trip_id = $1 ORDER BY TIME DESC LIMIT 1 );
	    INSERT INTO trip_log_meta (trip_id, trip_log_id, input_key, input_value)
	    VALUES ($1, trip_log_id_var, $2, $3);
    END
$$
LANGUAGE plpgsql;