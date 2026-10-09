package com.proteahealth.data;

import com.google.gson.annotations.SerializedName;

public class PharmacyOrder {

    @SerializedName("id")
    public int id;

    @SerializedName("patient_id")
    public int patientId;

    @SerializedName("medication_name")
    public String medicationName;

    @SerializedName("quantity")
    public int quantity;

    @SerializedName("patient_name")
    public String patientName;

    @SerializedName("payment_method")
    public String paymentMethod;

    @SerializedName("amount")
    public String amount;

    @SerializedName("fulfillment_method")
    public String fulfillmentMethod;

    @SerializedName("order_date")
    public String orderDate;

    @SerializedName("status")
    public String status;

    @SerializedName("delivery_fee")
    public String deliveryFee;


    @SerializedName("delivery_address")
    public String deliveryAddress;

}
