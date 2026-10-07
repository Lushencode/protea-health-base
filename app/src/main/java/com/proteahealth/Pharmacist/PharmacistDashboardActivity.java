package com.proteahealth.pharmacist;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.proteahealth.LoginActivity;
import com.proteahealth.R;
import com.proteahealth.data.SessionManager;

public class PharmacistDashboardActivity extends AppCompatActivity {

    private TextView tvPharmacyName;
    private TextView tvPharmacistName;

    private CardView cardOrders;
    private CardView cardInventory;
    private CardView cardProfile;
    private CardView cardDemand;
    private CardView cardOutbox;
    private CardView cardSettings;

    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pharmacist_dashboard);

        sessionManager = new SessionManager(this);

        if (!sessionManager.isLoggedIn()) {
            goToLogin();
            return;
        }

        initializeViews();
        loadUserInformation();
        setupNavigation();
    }

    private void initializeViews() {

        tvPharmacyName = findViewById(R.id.tvPharmacyName);
        tvPharmacistName = findViewById(R.id.tvPharmacistName);

        cardOrders = findViewById(R.id.cardOrders);
        cardInventory = findViewById(R.id.cardInventory);
        cardProfile = findViewById(R.id.cardProfile);
        cardDemand = findViewById(R.id.cardDemand);
        cardOutbox = findViewById(R.id.cardOutbox);
        cardSettings = findViewById(R.id.cardSettings);
    }

    private void loadUserInformation() {

        String name = sessionManager.getName();
        String surname = sessionManager.getSurname();

        String fullName = (name + " " + surname).trim();

        if (fullName.isEmpty()) {
            fullName = "Pharmacist";
        }

        tvPharmacistName.setText("Welcome, " + fullName);

        // Later this will come from the pharmacy record in MySQL.
        tvPharmacyName.setText("ProteaHealth Pharmacy");
    }

    private void setupNavigation() {

        cardOrders.setOnClickListener(v ->
                showComingSoon("Orders")
        );

        cardInventory.setOnClickListener(v ->
                showComingSoon("Inventory")
        );

        cardProfile.setOnClickListener(v ->
                showComingSoon("Pharmacy Profile")
        );

        cardDemand.setOnClickListener(v ->
                showComingSoon("Medication Demand")
        );

        cardOutbox.setOnClickListener(v ->
                showComingSoon("Notifications")
        );

        cardSettings.setOnClickListener(v ->
                showComingSoon("Settings")
        );
    }

    private void showComingSoon(String page) {

        Toast.makeText(
                this,
                page + " page will be connected next.",
                Toast.LENGTH_SHORT
        ).show();
    }

    private void goToLogin() {

        Intent intent =
                new Intent(
                        PharmacistDashboardActivity.this,
                        LoginActivity.class
                );

        intent.addFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK |
                        Intent.FLAG_ACTIVITY_CLEAR_TASK
        );

        startActivity(intent);
        finish();
    }
}
