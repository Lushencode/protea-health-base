package com.proteahealth.Patient;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.proteahealth.R;
import com.proteahealth.api.PatientAppointment;
import com.proteahealth.api.PatientAppointmentsResponse;
import com.proteahealth.api.RetrofitClient;
import com.proteahealth.data.SessionManager;
import com.proteahealth.adapter.AppointmentAdapter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class All_Appointments_PatientsActivity extends AppCompatActivity {

    private RecyclerView rvUpcomingAppointments;
    private RecyclerView rvPastAppointments;

    private TextView tvNoUpcoming;
    private TextView tvNoPast;

    private AppointmentAdapter upcomingAdapter;
    private AppointmentAdapter pastAdapter;

    private final List<PatientAppointment> upcomingAppointments =
            new ArrayList<>();

    private final List<PatientAppointment> pastAppointments =
            new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_all_appointments_patients);

        initializeViews();
        setupRecyclerViews();
        loadAppointments();
    }

    private void initializeViews() {

        rvUpcomingAppointments =
                findViewById(R.id.rvUpcomingAppointments);

        rvPastAppointments =
                findViewById(R.id.rvPastAppointments);

        tvNoUpcoming =
                findViewById(R.id.tvNoUpcoming);

        tvNoPast =
                findViewById(R.id.tvNoPast);

        findViewById(R.id.btnBack)
                .setOnClickListener(v -> finish());
    }

    private void setupRecyclerViews() {

        upcomingAdapter =
                new AppointmentAdapter(upcomingAppointments);

        pastAdapter =
                new AppointmentAdapter(pastAppointments);

        rvUpcomingAppointments.setLayoutManager(
                new LinearLayoutManager(this)
        );

        rvPastAppointments.setLayoutManager(
                new LinearLayoutManager(this)
        );

        rvUpcomingAppointments.setAdapter(upcomingAdapter);
        rvPastAppointments.setAdapter(pastAdapter);

        // RecyclerViews are inside a ScrollView
        rvUpcomingAppointments.setNestedScrollingEnabled(false);
        rvPastAppointments.setNestedScrollingEnabled(false);
    }

    private void loadAppointments() {

        SessionManager sessionManager =
                new SessionManager(this);

        String patientIdString =
                sessionManager.getUserId();

        if (patientIdString == null ||
                patientIdString.isEmpty()) {

            Toast.makeText(
                    this,
                    "Unable to identify patient.",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        int patientId;

        try {

            patientId =
                    Integer.parseInt(patientIdString);

        } catch (NumberFormatException e) {

            Toast.makeText(
                    this,
                    "Invalid patient ID.",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        RetrofitClient.INSTANCE
                .getApiService()
                .getPatientAppointments(patientId)
                .enqueue(
                        new Callback<PatientAppointmentsResponse>() {

                            @Override
                            public void onResponse(
                                    Call<PatientAppointmentsResponse> call,
                                    Response<PatientAppointmentsResponse> response
                            ) {

                                if (!response.isSuccessful()
                                        || response.body() == null) {

                                    Toast.makeText(
                                            All_Appointments_PatientsActivity.this,
                                            "Unable to load appointments.",
                                            Toast.LENGTH_SHORT
                                    ).show();

                                    return;
                                }

                                PatientAppointmentsResponse result =
                                        response.body();

                                if (!result.getSuccess()) {

                                    Toast.makeText(
                                            All_Appointments_PatientsActivity.this,
                                            result.getMessage(),
                                            Toast.LENGTH_SHORT
                                    ).show();

                                    return;
                                }

                                separateAppointments(
                                        result.getAppointments()
                                );
                            }

                            @Override
                            public void onFailure(
                                    Call<PatientAppointmentsResponse> call,
                                    Throwable t
                            ) {

                                Toast.makeText(
                                        All_Appointments_PatientsActivity.this,
                                        "Connection error: "
                                                + t.getMessage(),
                                        Toast.LENGTH_LONG
                                ).show();
                            }
                        }
                );
    }

    private void separateAppointments(
            List<PatientAppointment> appointments
    ) {

        upcomingAppointments.clear();
        pastAppointments.clear();

        SimpleDateFormat format =
                new SimpleDateFormat(
                        "yyyy-MM-dd HH:mm:ss",
                        Locale.US
                );

        Date now = new Date();

        for (PatientAppointment appointment : appointments) {

            try {

                String dateTime =
                        appointment.getAppointment_date()
                                + " "
                                + appointment.getAppointment_time();

                Date appointmentDate =
                        format.parse(dateTime);

                if (appointmentDate == null) {
                    continue;
                }

                String status =
                        appointment.getStatus();

                boolean finishedStatus =
                        status.equalsIgnoreCase("completed")
                                || status.equalsIgnoreCase("cancelled")
                                || status.equalsIgnoreCase("rejected");

                if (appointmentDate.after(now)
                        && !finishedStatus) {

                    upcomingAppointments.add(appointment);

                } else {

                    pastAppointments.add(appointment);
                }

            } catch (Exception e) {

                pastAppointments.add(appointment);
            }
        }

        upcomingAdapter.notifyDataSetChanged();
        pastAdapter.notifyDataSetChanged();

        tvNoUpcoming.setVisibility(
                upcomingAppointments.isEmpty()
                        ? View.VISIBLE
                        : View.GONE
        );

        rvUpcomingAppointments.setVisibility(
                upcomingAppointments.isEmpty()
                        ? View.GONE
                        : View.VISIBLE
        );

        tvNoPast.setVisibility(
                pastAppointments.isEmpty()
                        ? View.VISIBLE
                        : View.GONE
        );

        rvPastAppointments.setVisibility(
                pastAppointments.isEmpty()
                        ? View.GONE
                        : View.VISIBLE
        );
    }
}
