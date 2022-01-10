package com.vu.emapis;

public class statisticsObject {

    private int trip_id;
    private int user_id;
    private int user_vehicle_id;
    private int battery_at_start;
    private int battery_at_end;
    private double average_consumption;
    private double trip_distance;
    private boolean stats_ready;
    private double avg_temp;
    private String trip_start_time;

    public statisticsObject() {

    }

    public statisticsObject(int trip_id, int user_id, int user_vehicle_id, int battery_at_start, int battery_at_end, double average_consumption,
                            double trip_distance, boolean stats_ready, double avg_temp, String trip_start_time) {
        this.trip_id = trip_id;
        this.user_id = user_id;
        this.user_vehicle_id = user_vehicle_id;
        this.battery_at_start = battery_at_start;
        this.battery_at_end = battery_at_end;
        this.average_consumption = average_consumption;
        this.trip_distance = trip_distance;
        this.stats_ready = stats_ready;
        this.avg_temp = avg_temp;
        this.trip_start_time = trip_start_time;
    }

    @Override
    public String toString() {
        return "statisticsObject{" +
                "trip_id=" + trip_id +
                ", user_id=" + user_id +
                ", user_vehicle_id=" + user_vehicle_id +
                ", battery_at_start=" + battery_at_start +
                ", battery_at_end=" + battery_at_end +
                ", average_consumption=" + average_consumption +
                ", trip_distance=" + trip_distance +
                ", stats_ready=" + stats_ready +
                ", avg_temp=" + avg_temp +
                '}';
    }

    public int getTrip_id() {
        return trip_id;
    }

    public void setTrip_id(int trip_id) {
        this.trip_id = trip_id;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public int getUser_vehicle_id() {
        return user_vehicle_id;
    }

    public void setUser_vehicle_id(int user_vehicle_id) {
        this.user_vehicle_id = user_vehicle_id;
    }

    public int getBattery_at_start() {
        return battery_at_start;
    }

    public void setBattery_at_start(int battery_at_start) {
        this.battery_at_start = battery_at_start;
    }

    public int getBattery_at_end() {
        return battery_at_end;
    }

    public void setBattery_at_end(int battery_at_end) {
        this.battery_at_end = battery_at_end;
    }

    public double getAverage_consumption() {
        return average_consumption;
    }

    public void setAverage_consumption(double average_consumption) {
        this.average_consumption = average_consumption;
    }

    public double getTrip_distance() {
        return trip_distance;
    }

    public void setTrip_distance(double trip_distance) {
        this.trip_distance = trip_distance;
    }

    public boolean isStats_ready() {
        return stats_ready;
    }

    public void setStats_ready(boolean stats_ready) {
        this.stats_ready = stats_ready;
    }

    public double getAvg_temp() {
        return avg_temp;
    }

    public void setAvg_temp(double avg_temp) {
        this.avg_temp = avg_temp;
    }

    public String getTrip_start_time() {
        return trip_start_time;
    }

    public void setTrip_start_time(String trip_start_time) {
        this.trip_start_time = trip_start_time;
    }
}