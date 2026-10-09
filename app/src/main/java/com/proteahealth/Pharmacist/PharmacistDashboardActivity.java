package com.proteahealth.Pharmacist;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;


import com.proteahealth.LoginActivity;
import com.proteahealth.NotificationActivity;
import com.proteahealth.ProfileActivity;
import com.proteahealth.R;
import com.proteahealth.data.PharmacyInventoryItem;
import com.proteahealth.data.SessionManager;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;


import com.proteahealth.api.PharmacyOrdersLoader;
import com.proteahealth.api.PharmacyOrdersResponse;
import com.proteahealth.api.PharmacyInventoryResponse;
import com.proteahealth.data.PharmacyOrder;
import com.proteahealth.adapter.DashboardRecentOrdersAdapter;

import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.LinearLayoutManager;




import com.proteahealth.api.PharmacyInventoryResponse;
import com.proteahealth.api.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;



public class PharmacistDashboardActivity extends AppCompatActivity {

    private SessionManager sessionManager;

    private TextView tvPharmacistName;
    private TextView tvPharmacyName;
    private TextView btnViewAllOrders;


    private TextView tvPendingOrders;

    private TextView tvProcessingOrders;

    private TextView tvDeliveryOrders;

    private TextView tvLowStock;

    private TextView tvRecentOrdersEmpty;


    private RecyclerView rvRecentOrders;
    private DashboardRecentOrdersAdapter recentOrdersAdapter;

    private ImageButton btnPharmacyNotifications;


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
        loadLowStock();
        setupDashboardActions();
        setupBottomNavigation();
    }

    private void initializeViews() {

        tvPharmacistName = findViewById(R.id.tvPharmacistName);
        tvPharmacyName = findViewById(R.id.tvPharmacyName);

        btnViewAllOrders = findViewById(R.id.btnViewAllOrders);
        btnPharmacyNotifications =
                findViewById(R.id.btnPharmacyNotifications);


        tvPendingOrders = findViewById(R.id.tvPendingOrders);

        tvProcessingOrders = findViewById(R.id.tvProcessingOrders);

        tvDeliveryOrders = findViewById(R.id.tvDeliveryOrders);

        tvLowStock = findViewById(R.id.tvLowStock);

        tvRecentOrdersEmpty = findViewById(R.id.tvRecentOrdersEmpty);

        rvRecentOrders = findViewById(R.id.rvRecentOrders);

        recentOrdersAdapter = new DashboardRecentOrdersAdapter();

        rvRecentOrders.setLayoutManager(
                new LinearLayoutManager(this)
        );

        rvRecentOrders.setNestedScrollingEnabled(false);
        rvRecentOrders.setAdapter(recentOrdersAdapter);



    }

    private void loadUserInformation() {

        String name = sessionManager.getName();
        String surname = sessionManager.getSurname();

        String fullName = (name + " " + surname).trim();

        if (fullName.isEmpty()) {
            fullName = "Pharmacist";
        }

        tvPharmacistName.setText("Welcome, " + fullName);

        tvPharmacyName.setText("Loading pharmacy...");

        loadPharmacyName();
    }


    private void updatePendingOrders(PharmacyOrdersResponse response) {

        int pendingCount = 0;

        if (response.getOrders() != null) {

            for (PharmacyOrder order : response.getOrders()) {

                if (order.status != null &&
                        order.status.equalsIgnoreCase("pending")) {

                    pendingCount++;
                }
            }
        }

        tvPendingOrders.setText(String.valueOf(pendingCount));
    }


    private void updateProcessingOrders(PharmacyOrdersResponse response) {

        int processingCount = 0;

        if (response.getOrders() != null) {

            for (PharmacyOrder order : response.getOrders()) {

                if (order.status != null &&
                        order.status.equalsIgnoreCase("processing")) {

                    processingCount++;
                }
            }
        }

        tvProcessingOrders.setText(String.valueOf(processingCount));
    }


    private void updateActiveDeliveries(PharmacyOrdersResponse response) {

        int activeDeliveries = 0;

        if (response.getOrders() != null) {

            for (PharmacyOrder order : response.getOrders()) {

                if (order.fulfillmentMethod != null
                        && order.fulfillmentMethod.equalsIgnoreCase("delivery")
                        && order.status != null
                        && !order.status.equalsIgnoreCase("delivered")
                        && !order.status.equalsIgnoreCase("cancelled")) {

                    activeDeliveries++;
                }
            }
        }

        tvDeliveryOrders.setText(String.valueOf(activeDeliveries));
    }



    private void updateRecentOrders(PharmacyOrdersResponse response) {

        List<PharmacyOrder> orders = response.getOrders();

        if (orders == null || orders.isEmpty()) {

            recentOrdersAdapter.setOrders(new ArrayList<>());

            tvRecentOrdersEmpty.setText("No recent orders found.");
            tvRecentOrdersEmpty.setVisibility(View.VISIBLE);
            rvRecentOrders.setVisibility(View.GONE);

            return;
        }

        List<PharmacyOrder> recentOrders = new ArrayList<>(orders);

        // Newest orders first, assuming IDs increase over time
        recentOrders.sort(
                Comparator.comparingInt(
                        (PharmacyOrder order) -> order.id
                ).reversed()
        );

        // Display a maximum of five orders
        int limit = Math.min(5, recentOrders.size());

        List<PharmacyOrder> latestFive =
                new ArrayList<>(recentOrders.subList(0, limit));

        recentOrdersAdapter.setOrders(latestFive);

        tvRecentOrdersEmpty.setVisibility(View.GONE);
        rvRecentOrders.setVisibility(View.VISIBLE);
    }



    private void updateLowStock(PharmacyInventoryResponse response) {

        int lowStockCount = 0;

        if (response.getMedications() != null) {

            for (PharmacyInventoryItem item : response.getMedications()) {

                if (item.getStockQuantity() <= 10) {
                    lowStockCount++;
                }
            }
        }

        tvLowStock.setText(String.valueOf(lowStockCount));
    }


    private void loadLowStock() {

        RetrofitClient.INSTANCE.getApiService()
                .getPharmacyInventory()
                .enqueue(new Callback<PharmacyInventoryResponse>() {

                    @Override
                    public void onResponse(
                            Call<PharmacyInventoryResponse> call,
                            Response<PharmacyInventoryResponse> response) {

                        if (response.isSuccessful()
                                && response.body() != null
                                && response.body().getSuccess()) {

                            updateLowStock(response.body());

                        } else {
                            tvLowStock.setText("—");
                        }
                    }

                    @Override
                    public void onFailure(
                            Call<PharmacyInventoryResponse> call,
                            Throwable t) {

                        tvLowStock.setText("—");
                    }
                });
    }







    private void loadPharmacyName() {

        PharmacyOrdersLoader.load(
                this,
                new PharmacyOrdersLoader.Callback() {

                    @Override
                    public void onSuccess(PharmacyOrdersResponse response) {

                        String pharmacyName = response.getPharmacyName();

                        if (pharmacyName != null
                                && !pharmacyName.trim().isEmpty()) {

                            tvPharmacyName.setText(
                                    pharmacyName + " Dashboard"
                            );



                        } else {
                            tvPharmacyName.setText("Pharmacy Dashboard");
                        }

                        updatePendingOrders(response);

                        updateProcessingOrders(response);

                        updateActiveDeliveries(response);

                        updateRecentOrders(response);



                    }

                    @Override
                    public void onError(String message) {

                        tvPharmacyName.setText("Pharmacy Dashboard");

                        Toast.makeText(
                                PharmacistDashboardActivity.this,
                                "Unable to load pharmacy name",
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                }
        );
    }


    private void setupDashboardActions() {


        btnViewAllOrders.setOnClickListener(v ->
                startActivity(
                        new Intent(
                                PharmacistDashboardActivity.this,
                                PharmacistOrdersActivity.class
                        )
                )
        );


        btnPharmacyNotifications.setOnClickListener(v ->
                startActivity(new Intent(this, NotificationActivity.class)));

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
                startActivity(
                        new android.content.Intent(
                                PharmacistDashboardActivity.this,
                                PharmacyDeliveryActivity.class
                        )
                )
        );

        navProfile.setOnClickListener(v ->
                startActivity(
                        new android.content.Intent(
                                PharmacistDashboardActivity.this,
                                ProfileActivity.class
                        )
                )
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
