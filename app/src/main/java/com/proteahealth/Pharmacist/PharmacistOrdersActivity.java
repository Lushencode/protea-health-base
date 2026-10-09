package com.proteahealth.Pharmacist;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.proteahealth.ProfileActivity;
import com.proteahealth.R;
import com.proteahealth.adapter.PharmacyOrdersAdapter;
import com.proteahealth.api.PharmacyOrderStatusUpdater;
import com.proteahealth.api.PharmacyOrdersLoader;
import com.proteahealth.data.PharmacyOrder;
import com.proteahealth.api.PharmacyOrdersResponse;

import java.util.ArrayList;

public class PharmacistOrdersActivity extends AppCompatActivity {

    private RecyclerView recyclerOrders;
    private ProgressBar progressBar;
    private TextView tvNoOrders;
    private TextView tvOrderCount;
    private TextView tvPharmacyName;

    private PharmacyOrdersAdapter adapter;
    private final ArrayList<PharmacyOrder> orders = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pharmacist_orders);

        recyclerOrders = findViewById(R.id.recyclerPharmacyOrders);
        progressBar = findViewById(R.id.ordersProgressBar);
        tvNoOrders = findViewById(R.id.tvNoOrders);
        tvOrderCount = findViewById(R.id.tvOrderCount);
        tvPharmacyName = findViewById(R.id.tvPharmacyName);


        findViewById(R.id.btnBackOrders)
                .setOnClickListener(v -> finish());

        adapter = new PharmacyOrdersAdapter(orders);

        recyclerOrders.setLayoutManager(
                new LinearLayoutManager(this)
        );

        recyclerOrders.setAdapter(adapter);


        adapter.setOnStatusUpdateClickListener((order, nextStatus) -> {

            boolean isCancellation = "cancelled".equals(nextStatus);

            String readableStatus = nextStatus.replace("_", " ");

            String confirmationMessage = isCancellation
                    ? "Are you sure you want to cancel Order #" + order.id
                      + "? This action cannot be undone."
                    : "Are you sure you want to mark this order as "
                      + readableStatus + "?";

            new androidx.appcompat.app.AlertDialog.Builder(this)
                    .setTitle("Update Order #" + order.id)
                    .setMessage(confirmationMessage)
                    .setNegativeButton("Cancel", null)
                    .setPositiveButton(
                            isCancellation ? "Cancel Order" : "Confirm",
                            (dialog, which) -> {

                        PharmacyOrderStatusUpdater.update(
                                this,
                                order.id,
                                nextStatus,
                                new PharmacyOrderStatusUpdater.Callback() {

                                    @Override
                                    public void onSuccess(String message) {

                                        Toast.makeText(
                                                PharmacistOrdersActivity.this,
                                                message,
                                                Toast.LENGTH_SHORT
                                        ).show();

                                        // Reload updated orders from MySQL
                                        loadOrders();
                                    }

                                    @Override
                                    public void onError(String message) {

                                        Toast.makeText(
                                                PharmacistOrdersActivity.this,
                                                message,
                                                Toast.LENGTH_LONG
                                        ).show();
                                    }
                                }
                        );
                    })
                    .show();
        });


        loadOrders();

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

        navHome.setOnClickListener(v ->
                startActivity(
                        new android.content.Intent(
                                PharmacistOrdersActivity.this,
                                PharmacistDashboardActivity.class
                        )
                ));


        navInventory.setOnClickListener(v ->
                startActivity(
                        new android.content.Intent(
                                PharmacistOrdersActivity.this,
                                PharmacyInventoryActivity.class
                        )
                ));

        navOrders.setBackgroundResource(R.drawable.nav_icon_glow);

        navDeliveries.setOnClickListener(v ->
                startActivity(
                        new android.content.Intent(
                                PharmacistOrdersActivity.this,
                                PharmacyDeliveryActivity.class
                        )
                )
        );

        navProfile.setOnClickListener(v ->
                startActivity(
                        new android.content.Intent(
                                PharmacistOrdersActivity.this,
                                ProfileActivity.class
                        )
                )
        );
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Future order updates can be refreshed here.

    }

    private void loadOrders() {
        progressBar.setVisibility(View.VISIBLE);
        tvNoOrders.setVisibility(View.GONE);

        PharmacyOrdersLoader.load(
                this,
                new PharmacyOrdersLoader.Callback() {

                    @Override
                    public void onSuccess(PharmacyOrdersResponse response) {
                        progressBar.setVisibility(View.GONE);

                        orders.clear();

                        if (response.getOrders() != null) {
                            orders.addAll(response.getOrders());
                        }

                        adapter.notifyDataSetChanged();

                        tvPharmacyName.setText(
                                response.getPharmacyName() != null
                                        ? response.getPharmacyName()
                                        : "My Pharmacy"
                        );

                        tvOrderCount.setText(
                                "Orders (" + orders.size() + ")"
                        );

                        tvNoOrders.setVisibility(
                                orders.isEmpty() ? View.VISIBLE : View.GONE
                        );
                    }

                    @Override
                    public void onError(String message) {
                        progressBar.setVisibility(View.GONE);

                        Toast.makeText(
                                PharmacistOrdersActivity.this,
                                message,
                                Toast.LENGTH_LONG
                        ).show();

                        tvNoOrders.setText(message);
                        tvNoOrders.setVisibility(View.VISIBLE);
                    }
                }
        );
    }
}
