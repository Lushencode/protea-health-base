package com.proteahealth.data;


public class PharmacyInventoryItem {

    private int id;
    private int pharmacy_id;
    private String medication_name;
    private double price;
    private int stock_quantity;
    private String availability;
    private String created_at;

    public int getId() {
        return id;
    }

    public int getPharmacyId() {
        return pharmacy_id;
    }

    public String getMedicationName() {
        return medication_name;
    }

    public double getPrice() {
        return price;
    }

    public int getStockQuantity() {
        return stock_quantity;
    }

    public String getAvailability() {
        return availability;
    }

    public String getCreatedAt() {
        return created_at;
    }
}

