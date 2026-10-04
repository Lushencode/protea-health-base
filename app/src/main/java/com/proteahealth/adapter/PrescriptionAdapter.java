package com.proteahealth.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.proteahealth.R;
import com.proteahealth.model.Prescription;
import com.proteahealth.Patient.MedicationPriceComparisonActivity;

import java.util.List;
import java.util.Locale;

public class PrescriptionAdapter extends RecyclerView.Adapter<PrescriptionAdapter.PrescriptionViewHolder> {

    private final Context context;
    private final List<Prescription> prescriptions;

    public PrescriptionAdapter(Context context, List<Prescription> prescriptions) {
        this.context = context;
        this.prescriptions = prescriptions;
    }

    @NonNull
    @Override
    public PrescriptionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_prescription, parent, false);
        return new PrescriptionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PrescriptionViewHolder holder, int position) {
        Prescription prescription = prescriptions.get(position);

        holder.tvMedicationName.setText(prescription.getMedicationName());
        holder.tvQuantity.setText(String.format(Locale.getDefault(), "Quantity: %d", prescription.getQuantity()));
        holder.tvPrescribingDoctor.setText(prescription.getPrescribingDoctor());

        holder.btnComparePrices.setOnClickListener(v -> {
            Intent intent = new Intent(context, MedicationPriceComparisonActivity.class);
            intent.putExtra("medicationName", prescription.getMedicationName());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return prescriptions.size();
    }

    static class PrescriptionViewHolder extends RecyclerView.ViewHolder {
        TextView tvMedicationName;
        TextView tvQuantity;
        TextView tvPrescribingDoctor;
        android.widget.Button btnComparePrices;

        PrescriptionViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMedicationName = itemView.findViewById(R.id.tvMedicationName);
            tvQuantity = itemView.findViewById(R.id.tvQuantity);
            tvPrescribingDoctor = itemView.findViewById(R.id.tvPrescribingDoctor);
            btnComparePrices = itemView.findViewById(R.id.btnComparePrices);
        }
    }
}