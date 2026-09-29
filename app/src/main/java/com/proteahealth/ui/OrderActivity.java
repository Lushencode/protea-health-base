package com.proteahealth.ui;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.proteahealth.R;
import com.proteahealth.adapter.OrderAdapter;
import com.proteahealth.adapter.PrescriptionAdapter;
import com.proteahealth.data.MockDataProvider;
import com.proteahealth.model.Order;
import com.proteahealth.model.Prescription;

import java.util.List;

public class OrderActivity extends AppCompatActivity {

    private OrderAdapter orderAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);

        RecyclerView rvPrescriptions = findViewById(R.id.rvPrescriptions);
        RecyclerView rvOrders = findViewById(R.id.rvOrders);
        EditText etSearchOrders = findViewById(R.id.etSearchOrders);
        TextView tvEmptyState = findViewById(R.id.tvEmptyState);

        List<Prescription> prescriptions = MockDataProvider.getPrescriptions();
        PrescriptionAdapter prescriptionAdapter = new PrescriptionAdapter(this, prescriptions);
        rvPrescriptions.setLayoutManager(new LinearLayoutManager(this));
        rvPrescriptions.setAdapter(prescriptionAdapter);

        List<Order> orders = MockDataProvider.getOrderHistory();
        orderAdapter = new OrderAdapter(this, orders);
        rvOrders.setLayoutManager(new LinearLayoutManager(this));
        rvOrders.setAdapter(orderAdapter);

        tvEmptyState.setVisibility(orders.isEmpty() ? android.view.View.VISIBLE : android.view.View.GONE);

        etSearchOrders.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                orderAdapter.filterByMedication(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }
}