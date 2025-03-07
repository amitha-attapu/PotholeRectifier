package com.major.potholerectifier.model;

import java.io.Serializable;

public class PotHoleLocation implements Serializable {

    double lat;
    double lng;
    String address;

    public PotHoleLocation() {
    }

    public PotHoleLocation(double lat, double lng, String address) {
        this.lat = lat;
        this.lng = lng;
        this.address = address;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
