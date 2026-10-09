
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
import com.proteahealth.adapter.PharmacyDeliveryAdapter;
import com.proteahealth.data.PharmacyOrder;
import com.proteahealth.api.PharmacyDeliveriesLoader;
import com.proteahealth.api.PharmacyOrdersResponse;


public class PharmacyDeliveryActivity extends AppCompatActivity {

    private RecyclerView recyclerDeliveries;
    private ProgressBar progressBar;
    private TextView tvEmptyDeliveries;
    private Button btnRefresh;

    private PharmacyDeliveryAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_pharmacy_delivery);

        recyclerDeliveries = findViewById(R.id.rvPharmacyDeliveries);
        progressBar = findViewById(R.id.deliveryProgressBar);
        tvEmptyDeliveries = findViewById(R.id.tvEmptyDeliveries);
        btnRefresh = findViewById(R.id.btnRefreshDeliveries);

        recyclerDeliveries.setLayoutManager(
                new LinearLayoutManager(this)
        );

        adapter = new PharmacyDeliveryAdapter(
                new PharmacyDeliveryAdapter.OnDeliveryActionListener() {
                    @Override
                    public void onUpdateStatus(PharmacyOrder order) {
                        Toast.makeText(
                                PharmacyDeliveryActivity.this,
                                "Update Order #" + order.id,
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                }
        );

        recyclerDeliveries.setAdapter(adapter);

        btnRefresh.setOnClickListener(v -> loadDeliveries());


        loadDeliveries();

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
                                PharmacyDeliveryActivity.this,
                                PharmacistDashboardActivity.class
                        )
                ));


        navInventory.setOnClickListener(v ->
                startActivity(
                        new android.content.Intent(
                                PharmacyDeliveryActivity.this,
                                PharmacyInventoryActivity.class
                        )
                ));

        navDeliveries.setBackgroundResource(R.drawable.nav_icon_glow);

        navOrders.setOnClickListener(v ->
                startActivity(
                        new android.content.Intent(
                                PharmacyDeliveryActivity.this,
                                PharmacistOrdersActivity.class
                        )
                )
        );


        navProfile.setOnClickListener(v ->
                startActivity(
                        new android.content.Intent(
                                PharmacyDeliveryActivity.this,
                                ProfileActivity.class
                        )
                )
        );
    }


    private void loadDeliveries() {

        progressBar.setVisibility(View.VISIBLE);
        tvEmptyDeliveries.setVisibility(View.GONE);
        btnRefresh.setEnabled(false);

        PharmacyDeliveriesLoader.load(
                this,
                new PharmacyDeliveriesLoader.Callback() {

                    @Override
                    public void onSuccess(PharmacyOrdersResponse response) {

                        progressBar.setVisibility(View.GONE);
                        btnRefresh.setEnabled(true);

                        adapter.setOrders(response.getOrders());

                        boolean isEmpty = response.getOrders() == null
                                || response.getOrders().isEmpty();

                        tvEmptyDeliveries.setText(
                                "No delivery orders found"
                        );

                        tvEmptyDeliveries.setVisibility(
                                isEmpty ? View.VISIBLE : View.GONE
                        );
                    }

                    @Override
                    public void onError(String message) {

                        progressBar.setVisibility(View.GONE);
                        btnRefresh.setEnabled(true);

                        Toast.makeText(
                                PharmacyDeliveryActivity.this,
                                message,
                                Toast.LENGTH_LONG
                        ).show();

                        tvEmptyDeliveries.setText(message);
                        tvEmptyDeliveries.setVisibility(View.VISIBLE);
                    }
                }
        );
    }


    private void showEmptyState() {
        progressBar.setVisibility(View.GONE);
        tvEmptyDeliveries.setVisibility(View.VISIBLE);
    }
}
