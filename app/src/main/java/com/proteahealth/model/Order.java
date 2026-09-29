package com.proteahealth.model;

import java.io.Serializable;

public class Order implements Serializable {

    private final String id;
    private final String pharmacyName;
    private final String medicationName;
    private final int quantity;
    private final String distance;
    private final String availability;
    private final double amount;
    private final String orderDate;
    private String status;
    private final String paymentMethod;
    private final String receiptNumber;
    private final boolean dueForRefill;

    public Order(String id, String pharmacyName, String medicationName, int quantity,
                 String distance, String availability, double amount, String orderDate,
                 String status, String paymentMethod, String receiptNumber, boolean dueForRefill) {
        this.id = id;
        this.pharmacyName = pharmacyName;
        this.medicationName = medicationName;
        this.quantity = quantity;
        this.distance = distance;
        this.availability = availability;
        this.amount = amount;
        this.orderDate = orderDate;
        this.status = status;
        this.paymentMethod = paymentMethod;
        this.receiptNumber = receiptNumber;
        this.dueForRefill = dueForRefill;
    }

    public String getId() {
        return id;
    }

    public String getPharmacyName() {
        return pharmacyName;
    }

    public String getMedicationName() {
        return medicationName;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getDistance() {
        return distance;
    }

    public String getAvailability() {
        return availability;
    }

    public double getAmount() {
        return amount;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public String getReceiptNumber() {
        return receiptNumber;
    }

    public boolean isDueForRefill() {
        return dueForRefill;
    }
}