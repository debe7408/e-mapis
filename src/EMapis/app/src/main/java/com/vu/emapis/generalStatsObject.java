package com.vu.emapis;

public class generalStatsObject {

    private int vehicle_id;
    private String make;
    private String model;
    private int year;
    private double declared_consumption;
    private double real_consumption;
    private double total_distance;
    private int total_no_of_trips;

    generalStatsObject(int vehicle_id, String make, String model, int year, double declared_consumption, double real_consumption, double total_distance, int total_no_of_trips) {
        this.year = year;
        this.real_consumption = real_consumption;
        this.total_no_of_trips = total_no_of_trips;
        this.vehicle_id = vehicle_id;
        this.total_distance = total_distance;
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

    public double getReal_consumption() {
        return real_consumption;
    }

    public void setReal_consumption(double real_consumption) {
        this.real_consumption = real_consumption;
    }

    public double getTotal_distance() {
        return total_distance;
    }

    public void setTotal_distance(double total_distance) {
        this.total_distance = total_distance;
    }

    public int getTotal_no_of_trips() {
        return total_no_of_trips;
    }

    public void setTotal_no_of_trips(int total_no_of_trips) {
        this.total_no_of_trips = total_no_of_trips;
    }
}
