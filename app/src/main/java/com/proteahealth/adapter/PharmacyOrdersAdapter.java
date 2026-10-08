package com.proteahealth.adapter;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import android.widget.Button;
import androidx.recyclerview.widget.RecyclerView;

import com.proteahealth.R;
import com.proteahealth.data.PharmacyOrder;

import java.util.List;
import java.util.Locale;

public class PharmacyOrdersAdapter
        extends RecyclerView.Adapter<PharmacyOrdersAdapter.OrderViewHolder> {

    private final List<PharmacyOrder> orders;
    private OnStatusUpdateClickListener statusUpdateListener;

    public PharmacyOrdersAdapter(List<PharmacyOrder> orders) {
        this.orders = orders;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType
    ) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_pharmacy_order, parent, false);

        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull OrderViewHolder holder,
            int position
    ) {
        PharmacyOrder order = orders.get(position);

        holder.orderId.setText("Order #" + order.id);
        holder.medication.setText(order.medicationName);
        holder.quantity.setText("Quantity: " + order.quantity);
        holder.patient.setText("Patient: " + order.patientName);
        if ("delivery".equalsIgnoreCase(order.fulfillmentMethod)) {

            holder.paymentMethod.setVisibility(View.VISIBLE);

            holder.paymentMethod.setText(
                    "Payment on Delivery: " + capitalize(order.paymentMethod)
            );

        } else {

            holder.paymentMethod.setVisibility(View.GONE);

        }
        holder.fulfillment.setText(
                "Fulfilment: " + capitalize(order.fulfillmentMethod)
        );

        String nextStatus = getNextStatus(order);

        if (nextStatus != null) {

            holder.updateStatusButton.setVisibility(View.VISIBLE);

            holder.updateStatusButton.setText(
                    "Mark as " + formatStatus(nextStatus)
            );

            holder.updateStatusButton.setOnClickListener(v -> {
                if (statusUpdateListener != null) {
                    statusUpdateListener.onStatusUpdateClick(
                            order,
                            nextStatus
                    );
                }
            });

        } else {

            holder.updateStatusButton.setVisibility(View.GONE);
            holder.updateStatusButton.setOnClickListener(null);

        }

        boolean canCancel =
                "pending".equalsIgnoreCase(order.status)
                        || "confirmed".equalsIgnoreCase(order.status)
                        || "processing".equalsIgnoreCase(order.status)
                        || ("ready_for_collection".equalsIgnoreCase(order.status)
                        && "collection".equalsIgnoreCase(order.fulfillmentMethod));

        if (canCancel) {
            holder.cancelOrderButton.setVisibility(View.VISIBLE);

            holder.cancelOrderButton.setOnClickListener(v -> {
                if (statusUpdateListener != null) {
                    statusUpdateListener.onStatusUpdateClick(
                            order,
                            "cancelled"
                    );
                }
            });
        } else {
            holder.cancelOrderButton.setVisibility(View.GONE);
            holder.cancelOrderButton.setOnClickListener(null);
        }

        holder.date.setText(order.orderDate);

        holder.status.setText(formatStatus(order.status));
        try {
            double subtotal = Double.parseDouble(order.amount);
            boolean isDelivery =
                    "delivery".equalsIgnoreCase(order.fulfillmentMethod);

            double deliveryFee = isDelivery
                    ? Double.parseDouble(order.deliveryFee)
                    : 0.0;

            double total = subtotal + deliveryFee;

            holder.subtotal.setText(
                    String.format(Locale.US, "Medication: R%.2f", subtotal)
            );

            if (isDelivery) {
                holder.deliveryFee.setVisibility(View.VISIBLE);
                holder.deliveryFee.setText(
                        String.format(Locale.US, "Delivery fee: R%.2f", deliveryFee)
                );
            } else {
                holder.deliveryFee.setVisibility(View.GONE);
            }

            holder.amount.setText(
                    String.format(Locale.US, "Total: R%.2f", total)
            );

        } catch (NumberFormatException e) {
            holder.subtotal.setText("Medication: R" + order.amount);
            holder.deliveryFee.setVisibility(View.GONE);
            holder.amount.setText("Total unavailable");
        }
    }

    private String formatStatus(String status) {
        if (status == null || status.isEmpty()) {
            return "Unknown";
        }

        String formatted = status.replace("_", " ");

        String[] words = formatted.split(" ");
        StringBuilder result = new StringBuilder();

        for (String word : words) {
            if (!word.isEmpty()) {
                if (result.length() > 0) {
                    result.append(" ");
                }

                result.append(
                        word.substring(0, 1).toUpperCase(Locale.ROOT)
                ).append(word.substring(1));
            }
        }

        return result.toString();
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    private String capitalize(String value) {
        if (value == null || value.isEmpty()) {
            return "";
        }

        return value.substring(0, 1).toUpperCase(Locale.ROOT)
                + value.substring(1);
    }

    static class OrderViewHolder extends RecyclerView.ViewHolder {

        TextView orderId;
        TextView medication;
        TextView quantity;
        TextView patient;
        TextView fulfillment;
        TextView paymentMethod;
        TextView date;
        TextView subtotal;
        TextView deliveryFee;
        TextView amount;
        TextView status;
        Button updateStatusButton;
        Button cancelOrderButton;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);

            orderId = itemView.findViewById(R.id.tvOrderId);
            medication = itemView.findViewById(R.id.tvOrderMedication);
            quantity = itemView.findViewById(R.id.tvOrderQuantity);
            patient = itemView.findViewById(R.id.tvOrderPatient);
            fulfillment = itemView.findViewById(R.id.tvOrderFulfillment);
            paymentMethod = itemView.findViewById(R.id.tvOrderPaymentMethod);
            date = itemView.findViewById(R.id.tvOrderDate);
            subtotal = itemView.findViewById(R.id.tvOrderSubtotal);
            deliveryFee = itemView.findViewById(R.id.tvOrderDeliveryFee);
            amount = itemView.findViewById(R.id.tvOrderAmount);
            status = itemView.findViewById(R.id.tvOrderStatus);
            updateStatusButton = itemView.findViewById(R.id.btnUpdateOrderStatus);
            cancelOrderButton = itemView.findViewById(R.id.btnCancelOrder);
        }
    }
    public interface OnStatusUpdateClickListener {
        void onStatusUpdateClick(PharmacyOrder order, String nextStatus);
    }
    public void setOnStatusUpdateClickListener(
            OnStatusUpdateClickListener listener
    ) {
        this.statusUpdateListener = listener;
    }

    private String getNextStatus(PharmacyOrder order) {

        if (order.status == null) {
            return null;
        }

        switch (order.status) {

            case "pending":
                return "confirmed";

            case "confirmed":
                return "processing";

            case "processing":
                if ("collection".equalsIgnoreCase(order.fulfillmentMethod)) {
                    return "ready_for_collection";
                }

                if ("delivery".equalsIgnoreCase(order.fulfillmentMethod)) {
                    return "shipped";
                }

                return null;

            case "ready_for_collection":
                return "collection".equalsIgnoreCase(order.fulfillmentMethod)
                        ? "collected" : null;

            case "shipped":
                return "delivery".equalsIgnoreCase(order.fulfillmentMethod)
                        ? "delivered" : null;

            default:
                return null;
        }
    }
}

