package com.proteahealth.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.proteahealth.R;
import com.proteahealth.model.Prescription;

import java.util.List;
import java.util.Locale;

public class MedicationAdapter extends RecyclerView.Adapter<MedicationAdapter.MedicationViewHolder> {

    private static final int LOW_SUPPLY_THRESHOLD = 7;

    private final List<Prescription> prescriptions;

    public MedicationAdapter(List<Prescription> prescriptions) {
        this.prescriptions = prescriptions;
    }

    @NonNull
    @Override
    public MedicationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_medication, parent, false);
        return new MedicationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MedicationViewHolder holder, int position) {
        Prescription prescription = prescriptions.get(position);

        holder.tvMedicationName.setText(prescription.getMedicationName());
        holder.tvDosage.setText(prescription.getDosage());
        holder.tvPurpose.setText(prescription.getPurpose());
        holder.tvScheduledTime.setText(prescription.getScheduledTime());
        holder.tvPillCounter.setText(String.format(Locale.getDefault(), "%d of %d doses remaining",
                prescription.getRemainingDoses(), prescription.getTotalDoses()));

        holder.tvLowSupplyWarning.setVisibility(
                prescription.getRemainingDoses() <= LOW_SUPPLY_THRESHOLD ? View.VISIBLE : View.GONE);

        if (prescription.isTakenToday()) {
            holder.btnLogTaken.setText("Taken");
            holder.btnLogTaken.setEnabled(false);
        } else {
            holder.btnLogTaken.setText("Log as Taken");
            holder.btnLogTaken.setEnabled(true);
        }

        holder.btnLogTaken.setOnClickListener(v -> {
            prescription.setTakenToday(true);
            if (prescription.getRemainingDoses() > 0) {
                prescription.setRemainingDoses(prescription.getRemainingDoses() - 1);
            }
            notifyItemChanged(holder.getAdapterPosition());
            Toast.makeText(v.getContext(), prescription.getMedicationName() + " logged as taken", Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public int getItemCount() {
        return prescriptions.size();
    }

    static class MedicationViewHolder extends RecyclerView.ViewHolder {
        TextView tvMedicationName;
        TextView tvDosage;
        TextView tvPurpose;
        TextView tvScheduledTime;
        TextView tvPillCounter;
        TextView tvLowSupplyWarning;
        Button btnLogTaken;

        MedicationViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMedicationName = itemView.findViewById(R.id.tvMedicationName);
            tvDosage = itemView.findViewById(R.id.tvDosage);
            tvPurpose = itemView.findViewById(R.id.tvPurpose);
            tvScheduledTime = itemView.findViewById(R.id.tvScheduledTime);
            tvPillCounter = itemView.findViewById(R.id.tvPillCounter);
            tvLowSupplyWarning = itemView.findViewById(R.id.tvLowSupplyWarning);
            btnLogTaken = itemView.findViewById(R.id.btnLogTaken);
        }
    }
}