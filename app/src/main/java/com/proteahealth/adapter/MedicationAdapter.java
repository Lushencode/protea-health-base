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
import com.proteahealth.api.Medication;
import com.proteahealth.api.MedicationUpdateResponse;
import com.proteahealth.api.RetrofitClient;
import com.proteahealth.data.SessionManager;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import android.content.Intent;

import com.proteahealth.MedicationOrderActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MedicationAdapter
        extends RecyclerView.Adapter<MedicationAdapter.MedicationViewHolder> {

    private static final int LOW_SUPPLY_THRESHOLD = 7;

    private final List<Medication> medications;

    public MedicationAdapter(List<Medication> medications) {
        this.medications = medications;
    }

    @NonNull
    @Override
    public MedicationViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType
    ) {

        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.item_medication, parent, false);

        return new MedicationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull MedicationViewHolder holder,
            int position
    ) {

        Medication medication = medications.get(position);

        // -----------------------------------------
        // BASIC MEDICATION INFORMATION
        // -----------------------------------------

        holder.tvMedicationName.setText(
                medication.getMedication_name()
        );

        holder.tvDosage.setText(
                medication.getDosage() != null
                        ? medication.getDosage()
                        : "-"
        );

        holder.tvPurpose.setText(
                medication.getPurpose() != null
                        ? medication.getPurpose()
                        : "-"
        );

        holder.tvScheduledTime.setText(
                "Take at " +
                        formatTime(
                                medication.getScheduled_time()
                        )
        );

        // -----------------------------------------
        // DOSE COUNTER
        // -----------------------------------------

        holder.tvPillCounter.setText(
                String.format(
                        Locale.getDefault(),
                        "%d of %d doses remaining",
                        medication.getRemaining_doses(),
                        medication.getQuantity()
                )
        );

        // -----------------------------------------
        // LOW SUPPLY
        // -----------------------------------------

        holder.tvLowSupplyWarning.setVisibility(
                medication.getRemaining_doses()
                        <= LOW_SUPPLY_THRESHOLD
                        ? View.VISIBLE
                        : View.GONE
        );

        // -----------------------------------------
        // CHECK IF TAKEN TODAY
        // -----------------------------------------

        boolean takenToday =
                isTakenToday(
                        medication.getLast_taken_date()
                );

        updateButtonState(
                holder,
                takenToday
        );

        // -----------------------------------------
        // LOG AS TAKEN
        // -----------------------------------------

        holder.btnLogTaken.setOnClickListener(v -> {

            if (medication.getRemaining_doses() <= 0) {

                Toast.makeText(
                        v.getContext(),
                        "No doses remaining",
                        Toast.LENGTH_SHORT
                ).show();

                return;
            }

            holder.btnLogTaken.setEnabled(false);

            updateMedication(
                    holder,
                    medication,
                    "taken"
            );
        });

        // -----------------------------------------
        // UNDO
        // -----------------------------------------

        holder.btnUndoTaken.setOnClickListener(v -> {

            holder.btnUndoTaken.setEnabled(false);

            updateMedication(
                    holder,
                    medication,
                    "undo"
            );
        });

        // -----------------------------------------
// REFILL MEDICATION
// -----------------------------------------

        holder.btnRefill.setOnClickListener(v -> {

            Intent intent =
                    new Intent(
                            v.getContext(),
                            MedicationOrderActivity.class
                    );

            intent.putExtra(
                    "refill_medication_name",
                    medication.getMedication_name()
            );

            intent.putExtra(
                    "refill_dosage",
                    medication.getDosage()
            );

            /*
             * Refill using the originally prescribed quantity.
             */
            intent.putExtra(
                    "refill_quantity",
                    medication.getQuantity()
            );

            v.getContext().startActivity(
                    intent
            );
        });
    }

    // =============================================
    // SEND UPDATE TO PHP / MYSQL
    // =============================================

    private void updateMedication(
            MedicationViewHolder holder,
            Medication medication,
            String action
    ) {

        SessionManager sessionManager =
                new SessionManager(
                        holder.itemView.getContext()
                );

        String patientIdString =
                sessionManager.getUserId();

        if (patientIdString.isEmpty()) {

            Toast.makeText(
                    holder.itemView.getContext(),
                    "Unable to find patient ID",
                    Toast.LENGTH_SHORT
            ).show();

            resetButtonsAfterFailure(
                    holder,
                    medication
            );

            return;
        }

        int patientId;

        try {

            patientId =
                    Integer.parseInt(
                            patientIdString
                    );

        } catch (NumberFormatException e) {

            Toast.makeText(
                    holder.itemView.getContext(),
                    "Invalid patient ID",
                    Toast.LENGTH_SHORT
            ).show();

            resetButtonsAfterFailure(
                    holder,
                    medication
            );

            return;
        }

        RetrofitClient.INSTANCE
                .getApiService()
                .updateMedicationTaken(
                        medication.getPrescription_id(),
                        patientId,
                        action
                )
                .enqueue(
                        new Callback<MedicationUpdateResponse>() {

                            @Override
                            public void onResponse(
                                    Call<MedicationUpdateResponse> call,
                                    Response<MedicationUpdateResponse> response
                            ) {

                                if (response.isSuccessful()
                                        && response.body() != null
                                        && response.body().getSuccess()) {

                                    if (action.equals("taken")) {

                                        medication.setRemaining_doses(
                                                Math.max(
                                                        0,
                                                        medication.getRemaining_doses() - 1
                                                )
                                        );

                                        medication.setLast_taken_date(
                                                getToday()
                                        );

                                        Toast.makeText(
                                                holder.itemView.getContext(),
                                                "Medication logged as taken",
                                                Toast.LENGTH_SHORT
                                        ).show();

                                    } else {

                                        medication.setRemaining_doses(
                                                Math.min(
                                                        medication.getQuantity(),
                                                        medication.getRemaining_doses() + 1
                                                )
                                        );

                                        medication.setLast_taken_date(
                                                null
                                        );

                                        Toast.makeText(
                                                holder.itemView.getContext(),
                                                "Medication log undone",
                                                Toast.LENGTH_SHORT
                                        ).show();
                                    }

                                    int adapterPosition =
                                            holder.getBindingAdapterPosition();

                                    if (adapterPosition
                                            != RecyclerView.NO_POSITION) {

                                        notifyItemChanged(
                                                adapterPosition
                                        );
                                    }

                                } else {

                                    String message =
                                            "Unable to update medication";

                                    if (response.body() != null) {

                                        message =
                                                response.body()
                                                        .getMessage();
                                    }

                                    Toast.makeText(
                                            holder.itemView.getContext(),
                                            message,
                                            Toast.LENGTH_SHORT
                                    ).show();

                                    resetButtonsAfterFailure(
                                            holder,
                                            medication
                                    );
                                }
                            }

                            @Override
                            public void onFailure(
                                    Call<MedicationUpdateResponse> call,
                                    Throwable t
                            ) {

                                Toast.makeText(
                                        holder.itemView.getContext(),
                                        "Connection error: "
                                                + t.getMessage(),
                                        Toast.LENGTH_LONG
                                ).show();

                                resetButtonsAfterFailure(
                                        holder,
                                        medication
                                );
                            }
                        }
                );
    }

    // =============================================
    // BUTTON STATE
    // =============================================

    private void updateButtonState(
            MedicationViewHolder holder,
            boolean takenToday
    ) {

        if (takenToday) {

            holder.btnLogTaken.setText("Taken ✓");

            holder.btnLogTaken.setEnabled(false);

            holder.btnUndoTaken.setVisibility(
                    View.VISIBLE
            );

            holder.btnUndoTaken.setEnabled(true);

        } else {

            holder.btnLogTaken.setText(
                    "Log as Taken"
            );

            holder.btnLogTaken.setEnabled(true);

            holder.btnUndoTaken.setVisibility(
                    View.GONE
            );

            holder.btnUndoTaken.setEnabled(true);
        }
    }

    private void resetButtonsAfterFailure(
            MedicationViewHolder holder,
            Medication medication
    ) {

        updateButtonState(
                holder,
                isTakenToday(
                        medication.getLast_taken_date()
                )
        );
    }

    // =============================================
    // DATE CHECK
    // =============================================

    private boolean isTakenToday(
            String lastTakenDate
    ) {

        if (lastTakenDate == null
                || lastTakenDate.isEmpty()) {

            return false;
        }

        return lastTakenDate.equals(
                getToday()
        );
    }

    private String getToday() {

        SimpleDateFormat format =
                new SimpleDateFormat(
                        "yyyy-MM-dd",
                        Locale.getDefault()
                );

        return format.format(
                new Date()
        );
    }

    // =============================================
    // FORMAT MYSQL TIME
    // =============================================

    private String formatTime(
            String time
    ) {

        if (time == null
                || time.isEmpty()) {

            return "Time not set";
        }

        try {

            String[] parts =
                    time.split(":");

            int hour =
                    Integer.parseInt(
                            parts[0]
                    );

            int minute =
                    Integer.parseInt(
                            parts[1]
                    );

            String amPm =
                    hour >= 12
                            ? "PM"
                            : "AM";

            int displayHour =
                    hour % 12;

            if (displayHour == 0) {
                displayHour = 12;
            }

            return String.format(
                    Locale.getDefault(),
                    "%d:%02d %s",
                    displayHour,
                    minute,
                    amPm
            );

        } catch (Exception e) {

            return time;
        }
    }

    // =============================================
    // ITEM COUNT
    // =============================================

    @Override
    public int getItemCount() {
        return medications.size();
    }

    // =============================================
    // VIEW HOLDER
    // =============================================

    static class MedicationViewHolder
            extends RecyclerView.ViewHolder {

        TextView tvMedicationName;
        TextView tvDosage;
        TextView tvPurpose;
        TextView tvScheduledTime;
        TextView tvPillCounter;
        TextView tvLowSupplyWarning;

        Button btnLogTaken;
        Button btnUndoTaken;

        Button btnRefill;

        MedicationViewHolder(
                @NonNull View itemView
        ) {

            super(itemView);

            tvMedicationName =
                    itemView.findViewById(
                            R.id.tvMedicationName
                    );

            tvDosage =
                    itemView.findViewById(
                            R.id.tvDosage
                    );

            tvPurpose =
                    itemView.findViewById(
                            R.id.tvPurpose
                    );

            tvScheduledTime =
                    itemView.findViewById(
                            R.id.tvScheduledTime
                    );

            tvPillCounter =
                    itemView.findViewById(
                            R.id.tvPillCounter
                    );

            tvLowSupplyWarning =
                    itemView.findViewById(
                            R.id.tvLowSupplyWarning
                    );

            btnLogTaken =
                    itemView.findViewById(
                            R.id.btnLogTaken
                    );

            btnUndoTaken =
                    itemView.findViewById(
                            R.id.btnUndoTaken
                    );

            btnRefill =
                    itemView.findViewById(
                            R.id.btnRefill
                    );
        }
    }
}