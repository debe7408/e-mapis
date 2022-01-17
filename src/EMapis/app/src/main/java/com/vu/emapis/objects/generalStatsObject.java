package com.vu.emapis.objects;

public class generalStatsObject {

    private int vehicle_id;
    private String make;
    private String model;
    private int year;
    private double declared_consumption;
    private double average_consumption;
    private double traveled_distance;
    private int total_no_of_trips;

    generalStatsObject(int vehicle_id, String make, String model, int year, double declared_consumption, double average_consumption, double traveled_distance, int total_no_of_trips) {
        this.year = year;
        this.average_consumption = average_consumption;
        this.total_no_of_trips = total_no_of_trips;
        this.vehicle_id = vehicle_id;
        this.traveled_distance = traveled_distance;
        this.make = make;
        this.model = model;
        this.declared_consumption = declared_consumption;

    }

    public generalStatsObject() {

    }

    public int getVehicle_id() {
        return vehicle_id;
    }

    public void setVehicle_id(int vehicle_id) {
        this.vehicle_id = vehicle_id;
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

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public double getDeclared_consumption() {
        return declared_consumption;
    }

    public void setDeclared_consumption(double declared_consumption) {
        this.declared_consumption = declared_consumption;
    }

    public double getAverage_consumption() {
        return average_consumption;
    }

    public void setAverage_consumption(double average_consumption) {
        this.average_consumption = average_consumption;
    }

    public double getTraveled_distance() {
        return traveled_distance;
    }

    public void setTraveled_distance(double traveled_distance) {
        this.traveled_distance = traveled_distance;
    }

    public int getTotal_no_of_trips() {
        return total_no_of_trips;
    }

    public void setTotal_no_of_trips(int total_no_of_trips) {
        this.total_no_of_trips = total_no_of_trips;
    }

    @Override
    public String toString() {
        return "generalStatsObject{" +
                "vehicle_id=" + vehicle_id +
                ", make='" + make + '\'' +
                ", model='" + model + '\'' +
                ", year=" + year +
                ", declared_consumption=" + declared_consumption +
                ", real_consumption=" + average_consumption +
                ", total_distance=" + traveled_distance +
                ", total_no_of_trips=" + total_no_of_trips +
                '}';
    }
}
