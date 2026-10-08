package com.proteahealth.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.proteahealth.R;
import com.proteahealth.model.PatientMedicationInventory;

import java.util.List;

public class ReceivedMedicationAdapter
        extends RecyclerView.Adapter<ReceivedMedicationAdapter.ViewHolder> {

    private final List<PatientMedicationInventory> medications;

    public ReceivedMedicationAdapter(List<PatientMedicationInventory> medications) {
        this.medications = medications;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType
    ) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_received_medication, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull ViewHolder holder,
            int position
    ) {
        PatientMedicationInventory medication = medications.get(position);

        holder.tvMedicationName.setText(
                medication.getMedicationName()
        );

        holder.tvPharmacyName.setText(
                "Pharmacy: " + medication.getPharmacyName()
        );

        holder.tvQuantity.setText(
                "Quantity received: " + medication.getQuantityReceived()
        );

        holder.tvOrderId.setText(
                "Order #" + medication.getOrderId()
        );

        holder.tvReceivedDate.setText(
                "Received: " + medication.getReceivedAt()
        );
    }

    @Override
    public int getItemCount() {
        return medications == null ? 0 : medications.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvMedicationName;
        TextView tvPharmacyName;
        TextView tvQuantity;
        TextView tvOrderId;
        TextView tvReceivedDate;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvMedicationName = itemView.findViewById(
                    R.id.tvReceivedMedicationName
            );

            tvPharmacyName = itemView.findViewById(
                    R.id.tvReceivedPharmacyName
            );

            tvQuantity = itemView.findViewById(
                    R.id.tvReceivedQuantity
            );

            tvOrderId = itemView.findViewById(
                    R.id.tvReceivedOrderId
            );

            tvReceivedDate = itemView.findViewById(
                    R.id.tvReceivedDate
            );
        }
    }
}
