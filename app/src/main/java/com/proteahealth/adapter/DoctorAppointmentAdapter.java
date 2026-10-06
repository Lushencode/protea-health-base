package com.proteahealth.adapter;

import android.app.TimePickerDialog;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.proteahealth.R;
import com.proteahealth.DoctorAppointmentsActivity;
import com.proteahealth.model.DoctorAppointment;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class DoctorAppointmentAdapter
        extends RecyclerView.Adapter<DoctorAppointmentAdapter.ViewHolder> {

    private final Context context;
    private final List<DoctorAppointment> appointmentList;

    public DoctorAppointmentAdapter(
            Context context,
            List<DoctorAppointment> appointmentList) {

        this.context = context;
        this.appointmentList = appointmentList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType) {

        View view = LayoutInflater.from(context).inflate(
                R.layout.item_doctor_appointment,
                parent,
                false
        );

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull ViewHolder holder,
            int position) {

        DoctorAppointment appointment = appointmentList.get(position);
        if (appointment == null) return;

        holder.tvTime.setText(appointment.getTime());
        holder.tvPatient.setText(appointment.getPatientName());
        holder.tvType.setText(appointment.getAppointmentType());
        holder.tvStatus.setText(appointment.getStatus());

        String status = appointment.getStatus() != null ? appointment.getStatus().toUpperCase() : "AVAILABLE";

        // Color coding & button visibility for Doctor Timetable
        switch (status) {
            case "CONFIRMED":
                holder.tvStatus.setTextColor(Color.parseColor("#D81B60"));
                holder.vStatusIndicator.setBackgroundColor(Color.parseColor("#D81B60"));
                holder.layoutActions.setVisibility(View.VISIBLE);
                if (holder.btnConfirm != null) holder.btnConfirm.setVisibility(View.GONE);
                if (holder.btnReschedule != null) holder.btnReschedule.setVisibility(View.VISIBLE);
                if (holder.btnDecline != null) holder.btnDecline.setVisibility(View.VISIBLE);
                break;

            case "RESCHEDULED":
                holder.tvStatus.setTextColor(Color.parseColor("#159EA5"));
                holder.vStatusIndicator.setBackgroundColor(Color.parseColor("#159EA5"));
                holder.layoutActions.setVisibility(View.VISIBLE);
                if (holder.btnConfirm != null) holder.btnConfirm.setVisibility(View.GONE);
                if (holder.btnReschedule != null) holder.btnReschedule.setVisibility(View.VISIBLE);
                if (holder.btnDecline != null) holder.btnDecline.setVisibility(View.VISIBLE);
                break;

            case "PENDING":
                holder.tvStatus.setTextColor(Color.parseColor("#FF9800"));
                holder.vStatusIndicator.setBackgroundColor(Color.parseColor("#FF9800"));
                holder.layoutActions.setVisibility(View.VISIBLE);
                if (holder.btnConfirm != null) holder.btnConfirm.setVisibility(View.VISIBLE);
                if (holder.btnReschedule != null) holder.btnReschedule.setVisibility(View.GONE);
                if (holder.btnDecline != null) holder.btnDecline.setVisibility(View.VISIBLE);
                break;

            case "AVAILABLE":
                holder.tvStatus.setTextColor(Color.parseColor("#4CAF50"));
                holder.vStatusIndicator.setBackgroundColor(Color.parseColor("#4CAF50"));
                holder.layoutActions.setVisibility(View.GONE);
                break;

            case "UNAVAILABLE":
            case "DECLINED":
            case "CANCELLED":
                holder.tvStatus.setTextColor(Color.parseColor("#9E9E9E"));
                holder.vStatusIndicator.setBackgroundColor(Color.parseColor("#9E9E9E"));
                holder.layoutActions.setVisibility(View.GONE);
                break;

            default:
                holder.tvStatus.setTextColor(Color.parseColor("#0C514A"));
                holder.vStatusIndicator.setBackgroundColor(Color.parseColor("#0C514A"));
                holder.layoutActions.setVisibility(View.GONE);
                break;
        }

        // Confirm Action
        if (holder.btnConfirm != null) {
            holder.btnConfirm.setOnClickListener(v -> {
                appointment.setStatus("CONFIRMED");
                notifyItemChanged(position);
                Toast.makeText(context, "Confirmed appointment for " + appointment.getPatientName(), Toast.LENGTH_SHORT).show();
            });
        }

        // Reschedule Action
        if (holder.btnReschedule != null) {
            holder.btnReschedule.setOnClickListener(v -> {
                Calendar now = Calendar.getInstance();
                TimePickerDialog dialog = new TimePickerDialog(
                        context,
                        (view, selectedHour, selectedMinute) -> {
                            String newTime;
                            if (selectedHour >= 12) {
                                int hr = selectedHour > 12 ? selectedHour - 12 : selectedHour;
                                newTime = String.format(Locale.ENGLISH, "%02d:%02d PM", hr, selectedMinute);
                            } else {
                                int hr = selectedHour == 0 ? 12 : selectedHour;
                                newTime = String.format(Locale.ENGLISH, "%02d:%02d AM", hr, selectedMinute);
                            }

                            appointment.setTime(newTime);
                            appointment.setStatus("RESCHEDULED");
                            notifyItemChanged(position);
                            Toast.makeText(context, "Rescheduled appointment with " + appointment.getPatientName() + " to " + newTime, Toast.LENGTH_LONG).show();
                        },
                        now.get(Calendar.HOUR_OF_DAY),
                        now.get(Calendar.MINUTE),
                        false
                );
                dialog.setTitle("Reschedule Appointment");
                dialog.show();
            });
        }

        // Decline Action
        if (holder.btnDecline != null) {
            holder.btnDecline.setOnClickListener(v -> {
                appointment.setStatus("DECLINED");
                notifyItemChanged(position);
                Toast.makeText(context, "Declined appointment for " + appointment.getPatientName(), Toast.LENGTH_SHORT).show();
            });
        }
    }

    @Override
    public int getItemCount() {
        return appointmentList != null ? appointmentList.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        View vStatusIndicator;
        TextView tvTime;
        TextView tvPatient;
        TextView tvType;
        TextView tvStatus;
        View layoutActions;
        Button btnConfirm;
        Button btnReschedule;
        Button btnDecline;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            vStatusIndicator = itemView.findViewById(R.id.vStatusIndicator);
            tvTime = itemView.findViewById(R.id.tvAppointmentTime);
            tvPatient = itemView.findViewById(R.id.tvAppointmentPatient);
            tvType = itemView.findViewById(R.id.tvAppointmentType);
            tvStatus = itemView.findViewById(R.id.tvAppointmentStatus);
            layoutActions = itemView.findViewById(R.id.layoutDoctorActions);
            btnConfirm = itemView.findViewById(R.id.btnConfirmAppointment);
            btnReschedule = itemView.findViewById(R.id.btnRescheduleAppointment);
            btnDecline = itemView.findViewById(R.id.btnDeclineAppointment);
        }
    }
}