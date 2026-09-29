package com.proteahealth.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.proteahealth.R;
import com.proteahealth.model.Medication;
import com.proteahealth.model.Pharmacy;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class PharmacyAdapter extends RecyclerView.Adapter<PharmacyAdapter.PharmacyViewHolder> {

    private final List<Pharmacy> allPharmacies;
    private List<Pharmacy> visiblePharmacies;
    private String currentQuery = "";

    public PharmacyAdapter(List<Pharmacy> pharmacies) {
        this.allPharmacies = pharmacies;
        this.visiblePharmacies = new ArrayList<>(pharmacies);
    }

    @NonNull
    @Override
    public PharmacyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_pharmacy, parent, false);
        return new PharmacyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PharmacyViewHolder holder, int position) {
        Pharmacy pharmacy = visiblePharmacies.get(position);
        holder.tvPharmacyName.setText(pharmacy.getName());
        holder.tvDistance.setText(pharmacy.getDistance());

        Medication matchedMedication = findMatchingMedication(pharmacy, currentQuery);
        if (matchedMedication != null) {
            holder.tvPrice.setText(String.format(Locale.getDefault(), "R%.2f", matchedMedication.getPrice()));
            holder.tvAvailability.setText(matchedMedication.getAvailability());
        } else {
            holder.tvPrice.setText("");
            holder.tvAvailability.setText("");
        }
    }

    @Override
    public int getItemCount() {
        return visiblePharmacies.size();
    }

    private Medication findMatchingMedication(Pharmacy pharmacy, String query) {
        if (pharmacy.getMedications().isEmpty()) {
            return null;
        }
        if (query.isEmpty()) {
            return pharmacy.getMedications().get(0);
        }
        for (Medication medication : pharmacy.getMedications()) {
            if (medication.getName().toLowerCase(Locale.getDefault()).contains(query)) {
                return medication;
            }
        }
        return null;
    }

    public void filterByMedication(String query) {
        currentQuery = query.trim().toLowerCase(Locale.getDefault());
        visiblePharmacies.clear();

        if (currentQuery.isEmpty()) {
            visiblePharmacies.addAll(allPharmacies);
        } else {
            for (Pharmacy pharmacy : allPharmacies) {
                if (findMatchingMedication(pharmacy, currentQuery) != null) {
                    visiblePharmacies.add(pharmacy);
                }
            }
        }
        notifyDataSetChanged();
    }

    static class PharmacyViewHolder extends RecyclerView.ViewHolder {
        TextView tvPharmacyName;
        TextView tvPrice;
        TextView tvDistance;
        TextView tvAvailability;

        PharmacyViewHolder(@NonNull View itemView) {
            super(itemView);
            tvPharmacyName = itemView.findViewById(R.id.tvPharmacyName);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            tvDistance = itemView.findViewById(R.id.tvDistance);
            tvAvailability = itemView.findViewById(R.id.tvAvailability);
        }
    }
}