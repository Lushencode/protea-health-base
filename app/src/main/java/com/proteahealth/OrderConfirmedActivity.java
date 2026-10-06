package com.proteahealth;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.proteahealth.Patient.Patient_Home;

import java.util.Locale;

public class OrderConfirmedActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(
                R.layout.activity_order_confirmed
        );


        TextView tvOrderId =
                findViewById(R.id.tvOrderId);

        TextView tvMedication =
                findViewById(R.id.tvMedication);

        TextView tvMedicationDetails =
                findViewById(R.id.tvMedicationDetails);

        TextView tvPharmacy =
                findViewById(R.id.tvPharmacy);

        TextView tvPharmacyLocation =
                findViewById(R.id.tvPharmacyLocation);

        TextView tvFulfillmentMethod =
                findViewById(R.id.tvFulfillmentMethod);

        TextView tvDeliveryAddress =
                findViewById(R.id.tvDeliveryAddress);

        TextView tvMedicationTotal =
                findViewById(R.id.tvMedicationTotal);

        TextView tvDeliveryFee =
                findViewById(R.id.tvDeliveryFee);

        TextView tvOrderTotal =
                findViewById(R.id.tvOrderTotal);

        Button btnBackHome =
                findViewById(R.id.btnBackHome);


        // ---------------------------------------------------------
        // RECEIVE ORDER DETAILS
        // ---------------------------------------------------------

        Intent intent =
                getIntent();

        int orderId =
                intent.getIntExtra(
                        "order_id",
                        0
                );

        String medicationName =
                intent.getStringExtra(
                        "medication_name"
                );

        String dosage =
                intent.getStringExtra(
                        "dosage"
                );

        int quantity =
                intent.getIntExtra(
                        "quantity",
                        0
                );

        String pharmacyName =
                intent.getStringExtra(
                        "pharmacy_name"
                );

        String pharmacyLocation =
                intent.getStringExtra(
                        "pharmacy_location"
                );

        String fulfillmentMethod =
                intent.getStringExtra(
                        "fulfillment_method"
                );

        String deliveryAddress =
                intent.getStringExtra(
                        "delivery_address"
                );

        double medicationTotal =
                intent.getDoubleExtra(
                        "medication_total",
                        0.00
                );

        double deliveryFee =
                intent.getDoubleExtra(
                        "delivery_fee",
                        0.00
                );

        double orderTotal =
                intent.getDoubleExtra(
                        "order_total",
                        0.00
                );


        // ---------------------------------------------------------
        // DISPLAY ORDER DETAILS
        // ---------------------------------------------------------

        tvOrderId.setText(
                "Order #" + orderId
        );

        tvMedication.setText(
                medicationName
        );

        tvMedicationDetails.setText(
                dosage
                        + " • Quantity: "
                        + quantity
        );

        tvPharmacy.setText(
                pharmacyName
        );

        tvPharmacyLocation.setText(
                pharmacyLocation
        );


        // ---------------------------------------------------------
        // DELIVERY / COLLECTION
        // ---------------------------------------------------------

        if ("delivery".equalsIgnoreCase(
                fulfillmentMethod
        )) {

            tvFulfillmentMethod.setText(
                    "Delivery"
            );

            tvDeliveryAddress.setVisibility(
                    View.VISIBLE
            );

            tvDeliveryAddress.setText(
                    "Deliver to: "
                            + deliveryAddress
            );

        } else {

            tvFulfillmentMethod.setText(
                    "Collection"
            );

            tvDeliveryAddress.setVisibility(
                    View.GONE
            );
        }


        // ---------------------------------------------------------
        // TOTALS
        // ---------------------------------------------------------

        tvMedicationTotal.setText(
                String.format(
                        Locale.getDefault(),
                        "Medication: R%.2f",
                        medicationTotal
                )
        );

        tvDeliveryFee.setText(
                String.format(
                        Locale.getDefault(),
                        "Delivery: R%.2f",
                        deliveryFee
                )
        );

        tvOrderTotal.setText(
                String.format(
                        Locale.getDefault(),
                        "Total: R%.2f",
                        orderTotal
                )
        );


        // ---------------------------------------------------------
        // BACK TO PATIENT HOME
        // ---------------------------------------------------------

        btnBackHome.setOnClickListener(
                v -> {

                    Intent homeIntent =
                            new Intent(
                                    OrderConfirmedActivity.this,
                                    Patient_Home.class
                            );

                    homeIntent.addFlags(
                            Intent.FLAG_ACTIVITY_CLEAR_TOP
                                    | Intent.FLAG_ACTIVITY_NEW_TASK
                    );

                    startActivity(
                            homeIntent
                    );

                    finish();
                }
        );
    }


    // -------------------------------------------------------------
    // PREVENT RETURNING TO COMPLETED ORDER
    // -------------------------------------------------------------

    @Override
    public void onBackPressed() {

        Intent homeIntent =
                new Intent(
                        OrderConfirmedActivity.this,
                        Patient_Home.class
                );

        homeIntent.addFlags(
                Intent.FLAG_ACTIVITY_CLEAR_TOP
                        | Intent.FLAG_ACTIVITY_NEW_TASK
        );

        startActivity(
                homeIntent
        );

        finish();
    }
}
