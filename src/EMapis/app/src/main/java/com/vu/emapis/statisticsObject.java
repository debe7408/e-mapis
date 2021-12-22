package com.vu.emapis;

public class statisticsObject {

    private String average_consumption;
    private String trip_distance;
    private boolean stats_ready;

    public statisticsObject() {

    }

    public statisticsObject(String average_consumption, String trip_distance, Boolean stats_ready) {
        this.average_consumption = average_consumption;
        this.trip_distance = trip_distance;
        this.stats_ready = stats_ready;
    }

    @Override
    public String toString() {
        return "VehicleObject{" +
                "average_consumption='" + average_consumption + '\'' +
                ", trip_distance='" + trip_distance + '\'' +
                ", stats_ready='" + stats_ready + '\'' +
                '}';
    }

    public String getAverage_consumption() {
        return average_consumption;
    }

    public void setAverage_consumption(String average_consumption) {
        this.average_consumption = average_consumption;
    }

    public String getTrip_distance() {
        return trip_distance;
    }

    public void setTrip_distance(String trip_distance) {
        this.trip_distance = trip_distance;
    }

    public boolean isStats_ready() {
        return stats_ready;
    }

    public void setStats_ready(boolean stats_ready) {
        this.stats_ready = stats_ready;
    }
}
