package com.proteahealth.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.proteahealth.R;
import com.proteahealth.api.PatientAppointment;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AppointmentAdapter
        extends RecyclerView.Adapter<AppointmentAdapter.AppointmentViewHolder> {

    private final List<PatientAppointment> appointments;

    public AppointmentAdapter(List<PatientAppointment> appointments) {
        this.appointments = appointments;
    }

    @NonNull
    @Override
    public AppointmentViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType
    ) {

        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(
                        R.layout.item_all_appointments_patient,
                        parent,
                        false
                );

        return new AppointmentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull AppointmentViewHolder holder,
            int position
    ) {

        PatientAppointment appointment =
                appointments.get(position);

        // Doctor
        holder.tvDoctorName.setText(
                appointment.getDoctorDisplayName()
        );

        String specialization =
                appointment.getSpecialization();

        if (specialization != null &&
                !specialization.trim().isEmpty()) {

            holder.tvSpecialization.setText(specialization);
            holder.tvSpecialization.setVisibility(View.VISIBLE);

        } else {

            holder.tvSpecialization.setVisibility(View.GONE);
        }


        // Appointment type
        holder.tvAppointmentType.setText(
                appointment.getAppointment_type()
        );


        // Date + time
        holder.tvAppointmentDateTime.setText(
                formatAppointmentDateTime(
                        appointment.getAppointment_date(),
                        appointment.getAppointment_time()
                )
        );


        // Clinic
        String clinic =
                appointment.getClinic_name();

        if (clinic != null &&
                !clinic.trim().isEmpty()) {

            holder.tvClinic.setText(clinic);
            holder.tvClinic.setVisibility(View.VISIBLE);

        } else {

            holder.tvClinic.setVisibility(View.GONE);
        }


        // Location
        String location =
                appointment.getLocation();

        if (location != null &&
                !location.trim().isEmpty()) {

            holder.tvLocation.setText(location);
            holder.tvLocation.setVisibility(View.VISIBLE);

        } else {

            holder.tvLocation.setVisibility(View.GONE);
        }


        // Reason
        holder.tvReason.setText(
                "Reason: " + appointment.getReason()
        );


        // Doctor response
        String doctorResponse =
                appointment.getDoctor_response();

        if (doctorResponse != null &&
                !doctorResponse.trim().isEmpty()) {

            holder.tvDoctorResponse.setText(
                    "Doctor response: " + doctorResponse
            );

            holder.tvDoctorResponse.setVisibility(View.VISIBLE);

        } else {

            holder.tvDoctorResponse.setVisibility(View.GONE);
        }


        // Status
        String status =
                appointment.getStatus();

        holder.tvStatus.setText(
                formatStatus(status)
        );
    }


    private String formatAppointmentDateTime(
            String date,
            String time
    ) {

        try {

            SimpleDateFormat input =
                    new SimpleDateFormat(
                            "yyyy-MM-dd HH:mm:ss",
                            Locale.US
                    );

            SimpleDateFormat output =
                    new SimpleDateFormat(
                            "dd MMM yyyy • HH:mm",
                            Locale.ENGLISH
                    );

            Date parsed =
                    input.parse(date + " " + time);

            if (parsed != null) {
                return output.format(parsed);
            }

        } catch (Exception ignored) {

        }

        return date + " • " + time;
    }


    private String formatStatus(String status) {

        if (status == null ||
                status.trim().isEmpty()) {

            return "Unknown";
        }

        switch (status.toLowerCase(Locale.US)) {

            case "pending":
                return "Pending doctor confirmation";

            case "confirmed":
                return "Confirmed";

            case "rejected":
                return "Rejected";

            case "completed":
                return "Completed";

            case "cancelled":
                return "Cancelled";

            default:

                return status.substring(0, 1).toUpperCase()
                        + status.substring(1);
        }
    }


    @Override
    public int getItemCount() {
        return appointments.size();
    }


    static class AppointmentViewHolder
            extends RecyclerView.ViewHolder {

        TextView tvDoctorName;
        TextView tvSpecialization;
        TextView tvAppointmentType;
        TextView tvAppointmentDateTime;
        TextView tvClinic;
        TextView tvLocation;
        TextView tvReason;
        TextView tvDoctorResponse;
        TextView tvStatus;

        public AppointmentViewHolder(
                @NonNull View itemView
        ) {

            super(itemView);

            tvDoctorName =
                    itemView.findViewById(
                            R.id.tvDoctorName
                    );

            tvSpecialization =
                    itemView.findViewById(
                            R.id.tvSpecialization
                    );

            tvAppointmentType =
                    itemView.findViewById(
                            R.id.tvAppointmentType
                    );

            tvAppointmentDateTime =
                    itemView.findViewById(
                            R.id.tvAppointmentDateTime
                    );

            tvClinic =
                    itemView.findViewById(
                            R.id.tvClinic
                    );

            tvLocation =
                    itemView.findViewById(
                            R.id.tvLocation
                    );

            tvReason =
                    itemView.findViewById(
                            R.id.tvReason
                    );

            tvDoctorResponse =
                    itemView.findViewById(
                            R.id.tvDoctorResponse
                    );

            tvStatus =
                    itemView.findViewById(
                            R.id.tvStatus
                    );
        }
    }
}
