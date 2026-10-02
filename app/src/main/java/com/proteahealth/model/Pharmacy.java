package com.proteahealth.model;

import java.io.Serializable;
import java.util.List;

public class Pharmacy implements Serializable {

    private String id;
    private String name;
    private String location;
    private String distance;
    private String openHours;
    private List<Medication> medications;

    public Pharmacy(
            String id,
            String name,
            String location,
            String distance,
            String openHours,
            List<Medication> medications) {

        this.id = id;
        this.name = name;
        this.location = location;
        this.distance = distance;
        this.openHours = openHours;
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

    public List<Medication> getMedications() {
        return medications;
    }
}