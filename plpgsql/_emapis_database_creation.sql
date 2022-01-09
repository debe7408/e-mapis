CREATE TABLE "user_vehicles"(
    "user_vehicle_id" SERIAL,
    "user_id" BIGINT NOT NULL,
    "vehicle_alias" VARCHAR(255) NULL,
    "real_consumption" DOUBLE PRECISION NOT NULL,
    "vehicle_id" BIGINT NOT NULL
);
CREATE TABLE "trips"(
    "trip_id" SERIAL,
    "user_id" BIGINT NOT NULL,
    "user_vehicle_id" BIGINT NULL,
    "battery_at_start" INTEGER NULL,
    "battery_at_end" INTEGER NULL,
    "average_consumption" DOUBLE PRECISION NULL,
    "trip_distance" DOUBLE PRECISION NULL,
    "trip_geometry" Geometry(LINESTRING, 4326)
);
ALTER TABLE
    "trips" ADD PRIMARY KEY("trip_id");
CREATE TABLE "vehicles"(
    "vehicle_id" SERIAL,
    "make" TEXT NOT NULL,
    "model" TEXT NOT NULL,
    "year" INTEGER NOT NULL,
    "declared_consumption" DOUBLE PRECISION NOT NULL,
    "battery_size" DOUBLE PRECISION NULL
);
ALTER TABLE
    "vehicles" ADD PRIMARY KEY("vehicle_id");
CREATE TABLE "trip_log"(
    "trip_id" BIGINT NOT NULL,
    "latitude" DOUBLE PRECISION NULL,
    "longitude" DOUBLE PRECISION NULL,
    "time" TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    "osm_id" BIGINT NULL
);
CREATE TABLE "users"(
    "user_id" SERIAL,
    "email" TEXT NULL,
    "username" TEXT NULL,
    "password" VARCHAR(255) NULL
);
ALTER TABLE
    "users" ADD PRIMARY KEY("user_id");
CREATE TABLE "consumption_updates"(
    "input_id" SERIAL,
    "user_id" BIGINT NOT NULL,
    "battery_at_point" DOUBLE PRECISION NOT NULL,
    "time" TIMESTAMPTZ NOT NULL DEFAULT NOW()
);
ALTER TABLE
    "consumption_updates" ADD PRIMARY KEY("input_id");
ALTER TABLE
    "user_vehicles" ADD CONSTRAINT "user_vehicles_vehicle_id_foreign" FOREIGN KEY("vehicle_id") REFERENCES "vehicles"("vehicle_id");
ALTER TABLE
    "trip_log" ADD CONSTRAINT "trip_log_trip_id_foreign" FOREIGN KEY("trip_id") REFERENCES "trips"("trip_id");
ALTER TABLE
    "trips" ADD CONSTRAINT "trips_user_id_foreign" FOREIGN KEY("user_id") REFERENCES "users"("user_id");
ALTER TABLE
    "user_vehicles" ADD CONSTRAINT "user_vehicles_user_id_foreign" FOREIGN KEY("user_id") REFERENCES "users"("user_id");
ALTER TABLE 
    "consumption_updates" ADD CONSTRAINT
    "consumption_update_user_id" FOREIGN KEY("user_id")
    REFERENCES "users"("user_id");


CREATE TABLE "trip_log_meta"(
    "trip_id" BIGINT NOT NULL,
    "battery_level" INTEGER NULL,
    "time" TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

ALTER TABLE 
    "trip_log_meta" ADD CONSTRAINT
    "trip_log_meta_trips" FOREIGN KEY("trip_id")
    REFERENCES "trips"("trip_id");