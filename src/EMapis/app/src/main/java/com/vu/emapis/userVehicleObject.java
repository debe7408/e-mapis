package com.vu.emapis;

public class userVehicleObject {

    int user_vehicle_id;
    int user_id;
    String vehicle_alias;
    int real_consumption;
    int vehicle_id;

    public userVehicleObject() {

    }

    public userVehicleObject(int user_vehicle_id, int user_id, String vehicle_alias, int real_consumption, int vehicle_id) {
        this.user_vehicle_id = user_vehicle_id;
        this.user_id = user_id;
        this.vehicle_alias = vehicle_alias;
        this.real_consumption = real_consumption;
        this.vehicle_id = vehicle_id;
    }

    @Override
    public String toString() {
        return "userVehicleObject{" +
                "user_vehicle_id=" + user_vehicle_id +
                ", user_id=" + user_id +
                ", vehicle_alias='" + vehicle_alias + '\'' +
                ", real_consumption=" + real_consumption +
                ", vehicle_id=" + vehicle_id +
                '}';
    }

    public int getUser_vehicle_id() {
        return user_vehicle_id;
    }

    public void setUser_vehicle_id(int user_vehicle_id) {
        this.user_vehicle_id = user_vehicle_id;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public String getVehicle_alias() {
        return vehicle_alias;
    }

    public void setVehicle_alias(String vehicle_alias) {
        this.vehicle_alias = vehicle_alias;
    }

    public int getReal_consumption() {
        return real_consumption;
    }

    public void setReal_consumption(int real_consumption) {
        this.real_consumption = real_consumption;
    }

    public int getVehicle_id() {
        return vehicle_id;
    }

    public void setVehicle_id(int vehicle_id) {
        this.vehicle_id = vehicle_id;
    }
}
