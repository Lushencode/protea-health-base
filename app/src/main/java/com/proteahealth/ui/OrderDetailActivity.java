package com.proteahealth.ui;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.proteahealth.R;
import com.proteahealth.model.Order;

import java.util.Locale;

public class OrderDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);

        Order order = (Order) getIntent().getSerializableExtra("order");

        TextView tvDetailMedicationName = findViewById(R.id.tvDetailMedicationName);
        TextView tvDetailStatus = findViewById(R.id.tvDetailStatus);
        TextView tvDetailPharmacyName = findViewById(R.id.tvDetailPharmacyName);
        TextView tvDetailDistance = findViewById(R.id.tvDetailDistance);
        TextView tvDetailQuantity = findViewById(R.id.tvDetailQuantity);
        TextView tvDetailAvailability = findViewById(R.id.tvDetailAvailability);
        TextView tvDetailCollectionMethod = findViewById(R.id.tvDetailCollectionMethod);
        TextView tvDetailOrderDate = findViewById(R.id.tvDetailOrderDate);
        TextView tvDetailPaymentMethod = findViewById(R.id.tvDetailPaymentMethod);
        TextView tvDetailReceiptNumber = findViewById(R.id.tvDetailReceiptNumber);
        TextView tvDetailAmount = findViewById(R.id.tvDetailAmount);

        if (order != null) {
            tvDetailMedicationName.setText(order.getMedicationName());
            tvDetailStatus.setText(order.getStatus());
            tvDetailPharmacyName.setText(order.getPharmacyName());
            tvDetailDistance.setText(order.getDistance());
            tvDetailQuantity.setText(String.format(Locale.getDefault(), "Quantity: %d", order.getQuantity()));
            tvDetailAvailability.setText(order.getAvailability());
            tvDetailCollectionMethod.setText("Collection method: Pickup");
            tvDetailOrderDate.setText(String.format(Locale.getDefault(), "Ordered on %s", order.getOrderDate()));
            tvDetailPaymentMethod.setText(String.format(Locale.getDefault(), "Paid via %s", order.getPaymentMethod()));
            tvDetailReceiptNumber.setText(String.format(Locale.getDefault(), "Receipt: %s", order.getReceiptNumber()));
            tvDetailAmount.setText(String.format(Locale.getDefault(), "Total: R%.2f", order.getAmount()));
        }
    }
}