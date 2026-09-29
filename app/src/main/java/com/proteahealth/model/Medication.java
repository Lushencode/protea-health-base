package com.proteahealth.model;

import java.io.Serializable;

public class Medication implements Serializable {

    private final String name;
    private final double price;
    private final String availability;

    public Medication(String name, double price, String availability) {
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
}