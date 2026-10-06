package com.proteahealth.model;

import java.io.Serializable;
import java.util.List;

public class Pharmacy implements Serializable {

    private String id;
    private String name;
    private String location;
    private String distance;
    private String openHours;

    private boolean offersDelivery;
    private double deliveryFee;

    private List<Medication> medications;


    public Pharmacy(
            String id,
            String name,
            String location,
            String distance,
            String openHours,
            boolean offersDelivery,
            double deliveryFee,
            List<Medication> medications) {

        this.id = id;
        this.name = name;
        this.location = location;
        this.distance = distance;
        this.openHours = openHours;
        this.offersDelivery = offersDelivery;
        this.deliveryFee = deliveryFee;
        this.medications = medications;
    }


    public String getId() {
        return id;
    }


    public String getName() {
        return name;
    }


    public String getLocation() {
        return location;
    }


    public String getDistance() {
        return distance;
    }


    public String getOpenHours() {
        return openHours;
    }


    public boolean isOffersDelivery() {
        return offersDelivery;
    }


    public double getDeliveryFee() {
        return deliveryFee;
    }


    public List<Medication> getMedications() {
        return medications;
    }
}