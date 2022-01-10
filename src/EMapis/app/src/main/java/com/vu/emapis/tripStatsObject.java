package com.vu.emapis;

public class tripStatsObject {

    private int trip_id;
    private String date;
    private String make;
    private String model;
    private double trip_distance;
    private String trip_total_time;
    private double consumed_energy;
    private double avg_consumption;
    private double declared_consumption;
    private double accumulated_consumption;
    private double trip_temp;
    private boolean stats_ready;

    public tripStatsObject() {

    }

    public tripStatsObject(int trip_id, String date, String make, String model, double trip_distance, String trip_total_time, double consumed_energy, double avg_consumption, double declared_consumption, double accumulated_consumption, double trip_temp, boolean stats_ready) {
        this.trip_id = trip_id;
        this.date = date;
        this.make = make;
        this.model = model;
        this.trip_distance = trip_distance;
        this.trip_total_time = trip_total_time;
        this.consumed_energy = consumed_energy;
        this.avg_consumption = avg_consumption;
        this.declared_consumption = declared_consumption;
        this.accumulated_consumption = accumulated_consumption;
        this.trip_temp = trip_temp;
        this.stats_ready = stats_ready;
    }

    @Override
    public String toString() {
        return "tripStatsObject{" +
                "trip_id=" + trip_id +
                ", date='" + date + '\'' +
                ", make='" + make + '\'' +
                ", model='" + model + '\'' +
                ", trip_distance=" + trip_distance +
                ", trip_total_time='" + trip_total_time + '\'' +
                ", consumed_energy=" + consumed_energy +
                ", avg_consumption=" + avg_consumption +
                ", declared_consumption=" + declared_consumption +
                ", accumulated_consumption=" + accumulated_consumption +
                ", trip_temp=" + trip_temp +
                ", stats_ready=" + stats_ready +
                '}';
    }

    public int getTrip_id() {
        return trip_id;
    }

    public void setTrip_id(int trip_id) {
        this.trip_id = trip_id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getMake() {
        return make;
    }

    public void setMake(String make) {
        this.make = make;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public double getTrip_distance() {
        return trip_distance;
    }

    public void setTrip_distance(double trip_distance) {
        this.trip_distance = trip_distance;
    }

    public String getTrip_total_time() {
        return trip_total_time;
    }

    public void setTrip_total_time(String trip_total_time) {
        this.trip_total_time = trip_total_time;
    }

    public double getConsumed_energy() {
        return consumed_energy;
    }

    public void setConsumed_energy(double consumed_energy) {
        this.consumed_energy = consumed_energy;
    }

    public double getAvg_consumption() {
        return avg_consumption;
    }

    public void setAvg_consumption(double avg_consumption) {
        this.avg_consumption = avg_consumption;
    }

    public double getDeclared_consumption() {
        return declared_consumption;
    }

    public void setDeclared_consumption(double declared_consumption) {
        this.declared_consumption = declared_consumption;
    }

    public double getAccumulated_consumption() {
        return accumulated_consumption;
    }

    public void setAccumulated_consumption(double accumulated_consumption) {
        this.accumulated_consumption = accumulated_consumption;
    }

    public double getTrip_temp() {
        return trip_temp;
    }

    public void setTrip_temp(double trip_temp) {
        this.trip_temp = trip_temp;
    }

    public boolean isStats_ready() {
        return stats_ready;
    }

    public void setStats_ready(boolean stats_ready) {
        this.stats_ready = stats_ready;
    }
}