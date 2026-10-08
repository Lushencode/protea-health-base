package com.proteahealth.Pharmacist;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.proteahealth.LoginActivity;
import com.proteahealth.R;
import com.proteahealth.data.SessionManager;

public class PharmacistDashboardActivity extends AppCompatActivity {

    private SessionManager sessionManager;

    private TextView tvPharmacistName;
    private TextView tvPharmacyName;
    private TextView btnViewAllOrders;

    private ImageButton btnPharmacyNotifications;

    private CardView cardDemand;
    private CardView cardSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        sessionManager = new SessionManager(this);

        if (!sessionManager.isLoggedIn()) {
            goToLogin();
            return;
        }

        setContentView(R.layout.activity_pharmacist_dashboard);

        initializeViews();
        loadUserInformation();
        setupDashboardActions();
        setupBottomNavigation();
    }

    private void initializeViews() {

        tvPharmacistName = findViewById(R.id.tvPharmacistName);
        tvPharmacyName = findViewById(R.id.tvPharmacyName);

        btnViewAllOrders = findViewById(R.id.btnViewAllOrders);
        btnPharmacyNotifications =
                findViewById(R.id.btnPharmacyNotifications);

        cardDemand = findViewById(R.id.cardDemand);
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

        // Placeholder until the pharmacy API is connected.
        tvPharmacyName.setText("Pharmacy Dashboard");
    }

    private void setupDashboardActions() {

        btnViewAllOrders.setOnClickListener(v ->
                showComingSoon("Orders")
        );

        btnPharmacyNotifications.setOnClickListener(v ->
                showComingSoon("Notifications")
        );

        cardDemand.setOnClickListener(v ->
                showComingSoon("Medication Demand")
        );

        cardSettings.setOnClickListener(v ->
                showComingSoon("Settings")
        );
    }

    private void setupBottomNavigation() {

        ImageButton navOrders =
                findViewById(R.id.navPharmacyOrders);

        ImageButton navInventory =
                findViewById(R.id.navPharmacyInventory);

        ImageButton navHome =
                findViewById(R.id.navPharmacyHome);

        ImageButton navDeliveries =
                findViewById(R.id.navPharmacyDeliveries);

        ImageButton navProfile =
                findViewById(R.id.navPharmacyProfile);

        navOrders.setOnClickListener(v ->
                startActivity(
                        new android.content.Intent(
                                PharmacistDashboardActivity.this,
                                PharmacistOrdersActivity.class
                        )
        ));

        navInventory.setOnClickListener(v ->
                startActivity(
                        new android.content.Intent(
                                PharmacistDashboardActivity.this,
                                PharmacyInventoryActivity.class
                        )
                ));

        navHome.setBackgroundResource(R.drawable.nav_icon_glow);

        navDeliveries.setOnClickListener(v ->
                showComingSoon("Deliveries")
        );

        navProfile.setOnClickListener(v ->
                showComingSoon("Profile")
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

        Intent intent = new Intent(
                this,
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
