
package com.proteahealth.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.proteahealth.R;
import com.proteahealth.data.PharmacyInventoryItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class PharmacyInventoryAdapter
        extends RecyclerView.Adapter<PharmacyInventoryAdapter.InventoryViewHolder> {

    private final List<PharmacyInventoryItem> medications = new ArrayList<>();

    public interface OnMedicationActionListener {
        void onEdit(PharmacyInventoryItem medication);
        void onRemove(PharmacyInventoryItem medication);
    }

    private final OnMedicationActionListener listener;

    public PharmacyInventoryAdapter(OnMedicationActionListener listener) {
        this.listener = listener;
    }

    public void setMedications(List<PharmacyInventoryItem> newMedications) {
        medications.clear();

        if (newMedications != null) {
            medications.addAll(newMedications);
        }

        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public InventoryViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_pharmacy_inventory, parent, false);

        return new InventoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull InventoryViewHolder holder, int position) {

        PharmacyInventoryItem medication = medications.get(position);

        holder.tvName.setText(medication.getMedicationName());

        holder.tvPrice.setText(String.format(
                Locale.getDefault(),
                "Price: R%.2f",
                medication.getPrice()
        ));

        holder.tvStock.setText(
                "Stock: " + medication.getStockQuantity()
        );

        boolean available = "available".equalsIgnoreCase(
                medication.getAvailability()
        );

        holder.tvAvailability.setText(
                available ? "Available" : "Unavailable"
        );

        holder.tvAvailability.setTextColor(
                Color.parseColor(available ? "#2E7D32" : "#C62828")
        );

        holder.btnEdit.setOnClickListener(v -> {
            if (listener != null) {
                listener.onEdit(medication);
            }
        });

        holder.btnRemove.setOnClickListener(v -> {
            if (listener != null) {
                listener.onRemove(medication);
            }
        });
    }

    @Override
    public int getItemCount() {
        return medications.size();
    }

    static class InventoryViewHolder extends RecyclerView.ViewHolder {

        TextView tvName, tvPrice, tvStock, tvAvailability;
        Button btnEdit, btnRemove;

        InventoryViewHolder(@NonNull View itemView) {
            super(itemView);

            tvName = itemView.findViewById(
                    R.id.tvInventoryMedicationName
            );

            tvPrice = itemView.findViewById(
                    R.id.tvInventoryPrice
            );

            tvStock = itemView.findViewById(
                    R.id.tvInventoryStock
            );

            tvAvailability = itemView.findViewById(
                    R.id.tvInventoryAvailability
            );

            btnEdit = itemView.findViewById(
                    R.id.btnEditInventoryMedication
            );

            btnRemove = itemView.findViewById(
                    R.id.btnRemoveInventoryMedication
            );
        }
    }
}
