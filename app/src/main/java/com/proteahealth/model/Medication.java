package com.proteahealth.model;

import java.io.Serializable;

public class Medication implements Serializable {

    private String name;
    private double price;
    private String availability;

    public Medication(
            String name,
            double price,
            String availability) {

        this.name = name;
        this.price = price;
        this.availability = availability;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public String getAvailability() {
        return availability;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setAvailability(String availability) {
        this.availability = availability;
    }
}