package com.vu.emapis;

public class byUserVehicleObject {

    private int user_vehicle_id;
    private String vehicle_alias;
    private int no_of_trips;
    private int vehicle_id;
    private double total_distance;
    private double total_consumed_energy;
    private double average_consumption;
    private String make;
    private String model;
    private double declared_consumption;
    private double accumulated_consumption;

    byUserVehicleObject(int user_vehicle_id, String vehicle_alias, int no_of_trips, int vehicle_id, double total_distance, double total_consumed_energy,
                        double average_consumption, String make, String model, double declared_consumption, double accumulated_consumption) {
        this.user_vehicle_id = user_vehicle_id;
        this.vehicle_alias = vehicle_alias;
        this.no_of_trips = no_of_trips;
        this.vehicle_id = vehicle_id;
        this.total_distance = total_distance;
        this.total_consumed_energy = total_consumed_energy;
        this.average_consumption = average_consumption;
        this.make = make;
        this.model = model;
        this.declared_consumption = declared_consumption;
        this.accumulated_consumption = accumulated_consumption;
    }

    @Override
    public String toString() {
        return "byUserVehicleObject{" +
                ", user_vehicle_id=" + user_vehicle_id +
                ", vehicle_alias=" + vehicle_alias +
                ", no_of_trips=" + no_of_trips +
                ", no_of_trips=" + vehicle_id +
                ", total_distance=" + total_distance +
                ", total_consumed_energy=" + total_consumed_energy +
                ", average_consumption=" + average_consumption +
                ", declared_consumption=" + declared_consumption +
                '}';
    }

    public byUserVehicleObject() {

    }

    public int getUser_vehicle_id() {
        return user_vehicle_id;
    }

    public void setUser_vehicle_id(int user_vehicle_id) {
        this.user_vehicle_id = user_vehicle_id;
    }

    public String getVehicle_alias() {
        return vehicle_alias;
    }

    public void setVehicle_alias(String vehicle_alias) {
        this.vehicle_alias = vehicle_alias;
    }

    public int getNo_of_trips() {
        return no_of_trips;
    }

    public void setNo_of_trips(int no_of_trips) {
        this.no_of_trips = no_of_trips;
    }

    public int getVehicle_id() {
        return vehicle_id;
    }

    public void setVehicle_id(int vehicle_id) {
        this.vehicle_id = vehicle_id;
    }

    public double getTotal_distance() {
        return total_distance;
    }

    public void setTotal_distance(double total_distance) {
        this.total_distance = total_distance;
    }

    public double getTotal_consumed_energy() {
        return total_consumed_energy;
    }

    public void setTotal_consumed_energy(double total_consumed_energy) {
        this.total_consumed_energy = total_consumed_energy;
    }

    public double getAverage_consumption() {
        return average_consumption;
    }

    public void setAverage_consumption(double average_consumption) {
        this.average_consumption = average_consumption;
    }

    public double getDeclared_consumption() {
        return declared_consumption;
    }

    public void setDeclared_consumption(double declared_consumption) {
        this.declared_consumption = declared_consumption;
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

    public double getAccumulated_consumption() {
        return accumulated_consumption;
    }

    public void setAccumulated_consumption(double accumulated_consumption) {
        this.accumulated_consumption = accumulated_consumption;
    }
}
