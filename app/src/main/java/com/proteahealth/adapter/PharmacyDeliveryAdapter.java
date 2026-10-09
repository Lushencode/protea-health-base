package com.proteahealth.adapter;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.proteahealth.R;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.proteahealth.data.PharmacyOrder;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class PharmacyDeliveryAdapter
        extends RecyclerView.Adapter<PharmacyDeliveryAdapter.DeliveryViewHolder> {

    public interface OnDeliveryActionListener {
        void onUpdateStatus(PharmacyOrder order);
    }

    private final List<PharmacyOrder> orders = new ArrayList<>();
    private final OnDeliveryActionListener listener;

    public PharmacyDeliveryAdapter(OnDeliveryActionListener listener) {
        this.listener = listener;
    }

    public void setOrders(List<PharmacyOrder> newOrders) {
        orders.clear();

        if (newOrders != null) {
            orders.addAll(newOrders);
        }

        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public DeliveryViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_pharmacy_delivery, parent, false);

        return new DeliveryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull DeliveryViewHolder holder, int position) {

        PharmacyOrder order = orders.get(position);

        holder.tvOrderId.setText("Order #" + order.id);
        holder.tvPatient.setText("Patient: " + safe(order.patientName));
        holder.tvMedication.setText(
                "Medication: " + safe(order.medicationName)
        );
        holder.tvQuantity.setText("Quantity: " + order.quantity);
        holder.tvAddress.setText(
                "Delivery address: " + safe(order.deliveryAddress)
        );
        holder.tvDate.setText(
                "Ordered: " + safe(order.orderDate)
        );

        try {
            double fee = Double.parseDouble(order.deliveryFee);
            holder.tvFee.setText(
                    String.format(Locale.US, "Delivery fee: R%.2f", fee)
            );
        } catch (Exception e) {
            holder.tvFee.setText("Delivery fee: Not available");
        }

        String status = safe(order.status);

        holder.tvStatus.setText(
                status.replace("_", " ").toUpperCase(Locale.ROOT)
        );

        boolean canUpdate =
                status.equalsIgnoreCase("pending")
                        || status.equalsIgnoreCase("confirmed")
                        || status.equalsIgnoreCase("processing")
                        || status.equalsIgnoreCase("shipped");

        holder.btnUpdate.setVisibility(
                canUpdate ? View.VISIBLE : View.GONE
        );

        holder.btnUpdate.setOnClickListener(v -> {
            if (listener != null) {
                listener.onUpdateStatus(order);
            }
        });
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    private String safe(String value) {
        return value == null || value.trim().isEmpty()
                ? "Not available"
                : value;
    }

    static class DeliveryViewHolder extends RecyclerView.ViewHolder {

        TextView tvOrderId;
        TextView tvStatus;
        TextView tvPatient;
        TextView tvMedication;
        TextView tvQuantity;
        TextView tvAddress;
        TextView tvFee;
        TextView tvDate;
        Button btnUpdate;

        DeliveryViewHolder(@NonNull View itemView) {
            super(itemView);

            tvOrderId = itemView.findViewById(R.id.tvDeliveryOrderId);
            tvStatus = itemView.findViewById(R.id.tvDeliveryStatus);
            tvPatient = itemView.findViewById(R.id.tvDeliveryPatient);
            tvMedication = itemView.findViewById(R.id.tvDeliveryMedication);
            tvQuantity = itemView.findViewById(R.id.tvDeliveryQuantity);
            tvAddress = itemView.findViewById(R.id.tvDeliveryAddress);
            tvFee = itemView.findViewById(R.id.tvDeliveryFee);
            tvDate = itemView.findViewById(R.id.tvDeliveryDate);
            btnUpdate = itemView.findViewById(R.id.btnUpdateDeliveryStatus);
        }
    }
}

