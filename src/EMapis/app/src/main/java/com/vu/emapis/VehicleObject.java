package com.vu.emapis;

public class VehicleObject {

    String vehicle_id;
    String make;
    String model;
    String year;
    String battery_size;
    String declared_consumption;

    public VehicleObject() {

    }

    public VehicleObject(String vehicle_id, String make, String model, String year, String battery_size, String declared_consumption) {
        this.vehicle_id = vehicle_id;
        this.make = make;
        this.model = model;
        this.year = year;
        this.battery_size = battery_size;
        this.declared_consumption = declared_consumption;
    }

    @Override
    public String toString() {
        return "VehicleObject{" +
                "vehicle_id='" + vehicle_id + '\'' +
                ", make='" + make + '\'' +
                ", model='" + model + '\'' +
                ", year='" + year + '\'' +
                ", battery_size='" + battery_size + '\'' +
                ", declared_consumption='" + declared_consumption + '\'' +
                '}';
    }

    public String toMake() {
        return "Make=" + make;
    }

    public String getMake() {
        return make;
    }

    public void setMake(String make) {
        this.make = make;
    }

    public String getVehicle_id() {
        return vehicle_id;
    }

    public void setVehicle_id(String vehicle_id) {
        this.vehicle_id = vehicle_id;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getBattery_size() {
        return battery_size;
    }

    public void setBattery_size(String battery_size) {
        this.battery_size = battery_size;
    }

    public String getDeclared_consumption() {
        return declared_consumption;
    }

    public void setDeclared_consumption(String declared_consumption) {
        this.declared_consumption = declared_consumption;
    }
}
