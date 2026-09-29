package com.proteahealth.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.proteahealth.R;
import com.proteahealth.model.Order;
import com.proteahealth.ui.OrderDetailActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {

    private static final String DATE_FORMAT = "yyyy-MM-dd";
    private static final int REFILL_DUE_AFTER_DAYS = 30;

    private final Context context;
    private final List<Order> allOrders;
    private final List<Order> visibleOrders;
    private String currentQuery = "";

    public OrderAdapter(Context context, List<Order> orders) {
        this.context = context;
        this.allOrders = orders;
        this.visibleOrders = new ArrayList<>(orders);
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_order, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Order order = visibleOrders.get(position);

        holder.tvPharmacyName.setText(order.getPharmacyName());
        holder.tvMedicationName.setText(order.getMedicationName());
        holder.tvQuantity.setText(String.format(Locale.getDefault(), "Qty: %d", order.getQuantity()));
        holder.tvDistance.setText(order.getDistance());
        holder.tvAvailability.setText(order.getAvailability());
        holder.tvStatus.setText(order.getStatus());
        holder.tvOrderDate.setText(order.getOrderDate());
        holder.tvAmount.setText(String.format(Locale.getDefault(), "R%.2f", order.getAmount()));

        holder.tvRefillBanner.setVisibility(isDueForRefill(order) ? View.VISIBLE : View.GONE);

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, OrderDetailActivity.class);
            intent.putExtra("order", order);
            context.startActivity(intent);
        });

        holder.btnReorder.setOnClickListener(v -> placeReorder(order));
    }

    @Override
    public int getItemCount() {
        return visibleOrders.size();
    }

    private boolean isDueForRefill(Order order) {
        if (!"Collected".equalsIgnoreCase(order.getStatus())) {
            return false;
        }
        try {
            SimpleDateFormat format = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());
            Date orderDate = format.parse(order.getOrderDate());
            if (orderDate == null) {
                return false;
            }
            long diffMillis = new Date().getTime() - orderDate.getTime();
            long diffDays = diffMillis / (1000L * 60 * 60 * 24);
            return diffDays >= REFILL_DUE_AFTER_DAYS;
        } catch (ParseException e) {
            return false;
        }
    }

    private void placeReorder(Order originalOrder) {
        String todayDate = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault()).format(new Date());
        String newId = "ord-" + System.currentTimeMillis();
        String newReceiptNumber = "RCT-" + System.currentTimeMillis();

        Order newOrder = new Order(
                newId,
                originalOrder.getPharmacyName(),
                originalOrder.getMedicationName(),
                originalOrder.getQuantity(),
                originalOrder.getDistance(),
                originalOrder.getAvailability(),
                originalOrder.getAmount(),
                todayDate,
                "Pending",
                originalOrder.getPaymentMethod(),
                newReceiptNumber,
                false
        );

        allOrders.add(0, newOrder);
        filterByMedication(currentQuery);

        Toast.makeText(context, "Reorder placed for " + originalOrder.getMedicationName(), Toast.LENGTH_SHORT).show();
    }

    public void filterByMedication(String query) {
        currentQuery = query.trim().toLowerCase(Locale.getDefault());
        visibleOrders.clear();

        if (currentQuery.isEmpty()) {
            visibleOrders.addAll(allOrders);
        } else {
            for (Order order : allOrders) {
                if (order.getMedicationName().toLowerCase(Locale.getDefault()).contains(currentQuery)) {
                    visibleOrders.add(order);
                }
            }
        }
        notifyDataSetChanged();
    }

    static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView tvPharmacyName;
        TextView tvMedicationName;
        TextView tvQuantity;
        TextView tvDistance;
        TextView tvAvailability;
        TextView tvStatus;
        TextView tvOrderDate;
        TextView tvAmount;
        TextView tvRefillBanner;
        android.widget.Button btnReorder;

        OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            tvPharmacyName = itemView.findViewById(R.id.tvPharmacyName);
            tvMedicationName = itemView.findViewById(R.id.tvMedicationName);
            tvQuantity = itemView.findViewById(R.id.tvQuantity);
            tvDistance = itemView.findViewById(R.id.tvDistance);
            tvAvailability = itemView.findViewById(R.id.tvAvailability);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvOrderDate = itemView.findViewById(R.id.tvOrderDate);
            tvAmount = itemView.findViewById(R.id.tvAmount);
            tvRefillBanner = itemView.findViewById(R.id.tvRefillBanner);
            btnReorder = itemView.findViewById(R.id.btnReorder);
        }
    }
}