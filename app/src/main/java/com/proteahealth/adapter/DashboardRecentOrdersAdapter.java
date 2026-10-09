package com.proteahealth.adapter;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.proteahealth.R;
import com.proteahealth.data.PharmacyOrder;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class DashboardRecentOrdersAdapter
        extends RecyclerView.Adapter<DashboardRecentOrdersAdapter.OrderViewHolder> {

    private final List<PharmacyOrder> orders = new ArrayList<>();

    public void setOrders(List<PharmacyOrder> newOrders) {
        orders.clear();

        if (newOrders != null) {
            orders.addAll(newOrders);
        }

        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_dashboard_recent_order, parent, false);

        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull OrderViewHolder holder, int position) {

        PharmacyOrder order = orders.get(position);

        holder.tvOrderId.setText("Order #" + order.id);

        holder.tvMedication.setText(
                "Medication: " + safeText(order.medicationName)
        );

        holder.tvPatient.setText(
                "Patient: " + safeText(order.patientName)
        );

        holder.tvDate.setText(safeText(order.orderDate));

        holder.tvStatus.setText(
                safeText(order.status).toUpperCase(Locale.ROOT)
        );
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    private String safeText(String value) {
        return value == null || value.trim().isEmpty()
                ? "Not available"
                : value;
    }

    static class OrderViewHolder extends RecyclerView.ViewHolder {

        TextView tvOrderId;
        TextView tvMedication;
        TextView tvPatient;
        TextView tvDate;
        TextView tvStatus;

        OrderViewHolder(@NonNull View itemView) {
            super(itemView);

            tvOrderId = itemView.findViewById(R.id.tvRecentOrderId);
            tvMedication = itemView.findViewById(R.id.tvRecentOrderMedication);
            tvPatient = itemView.findViewById(R.id.tvRecentOrderPatient);
            tvDate = itemView.findViewById(R.id.tvRecentOrderDate);
            tvStatus = itemView.findViewById(R.id.tvRecentOrderStatus);
        }
    }
}
