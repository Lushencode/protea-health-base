package com.proteahealth.Patient;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewpager2.widget.ViewPager2;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import android.content.Intent;

import com.proteahealth.NotificationActivity;
import com.proteahealth.api.PatientAppointment;
import com.proteahealth.api.PatientAppointmentsResponse;

import com.proteahealth.BlogActivity;
import com.proteahealth.ProfileActivity;
import com.proteahealth.R;
import com.proteahealth.data.SessionManager;
import com.proteahealth.model.Featurebanner;
import android.view.View;

import com.proteahealth.api.Medication;
import com.proteahealth.api.MedicationResponse;
import com.proteahealth.api.RetrofitClient;
import com.proteahealth.api.MedicationUpdateResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.widget.ImageButton;

public class Patient_Home extends AppCompatActivity {

    private Button btnTakeMedicine;

    private TextView tvVisitDate;
    private TextView tvVisitType;
    private TextView tvVisitReason;
    private TextView tvVisitDetails;
    private View cardVisit;

    private TextView tvDoseStatus;
    private TextView tvMedicineName;
    private TextView tvMedicineDosage;

    private View cardMedicine;

    private Medication currentMedication;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);



        Button btnAppointment = findViewById(R.id.btnAppointment);

        btnAppointment.setOnClickListener(v -> {
            Intent intent = new Intent(
                    Patient_Home.this,
                    AppointmentActivity.class
            );

            startActivity(intent);
        });

        SessionManager sessionManager = new SessionManager(this);
        String id = sessionManager.getUserId();
        String name = sessionManager.getName();
        String surname = sessionManager.getSurname();
        String email = sessionManager.getEmail();
        String role = sessionManager.getRole();

        // Keep the page clear of the phone's status and navigation bars.
        ViewCompat.setOnApplyWindowInsetsListener(
                findViewById(R.id.main),
                (view, windowInsets) -> {

                    Insets bars = windowInsets.getInsets(
                            WindowInsetsCompat.Type.systemBars()
                    );

                    view.setPadding(
                            bars.left,
                            bars.top,
                            bars.right,
                            0
                    );

                    return windowInsets;
                }
        );

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }


        btnTakeMedicine = findViewById(R.id.btnTakeMedicine);

        tvDoseStatus = findViewById(R.id.tvDoseStatus);
        tvMedicineName = findViewById(R.id.tvMedicineName);
        tvMedicineDosage = findViewById(R.id.tvMedicineDosage);

        tvVisitDate = findViewById(R.id.tvVisitDate);
        tvVisitType = findViewById(R.id.tvVisitType);
        tvVisitReason = findViewById(R.id.tvVisitReason);
        tvVisitDetails = findViewById(R.id.tvVisitDetails);
        cardVisit = findViewById(R.id.cardVisit);

        btnTakeMedicine.setOnClickListener(view -> markMedicationAsTaken());

        cardMedicine = findViewById(R.id.cardMedicine);
        TextView tvToday = findViewById(R.id.tvToday);
        TextView tvVisitDate = findViewById(R.id.tvVisitDate);

        ImageButton navProfile = findViewById(R.id.navProfile);
        ImageButton navSetting = findViewById(R.id.navSetting);
        ImageButton navHome = findViewById(R.id.navhome);
        ImageButton navEmergency = findViewById(R.id.navEmergency);
        ImageButton navMed = findViewById(R.id.navMed);



        btnTakeMedicine = findViewById(R.id.btnTakeMedicine);

        tvDoseStatus = findViewById(R.id.tvDoseStatus);
        tvMedicineName = findViewById(R.id.tvMedicineName);
        tvMedicineDosage = findViewById(R.id.tvMedicineDosage);

        cardMedicine = findViewById(R.id.cardMedicine);

        String today = new SimpleDateFormat(
                "EEEE, d MMM",
                Locale.ENGLISH
        ).format(new Date());

        tvToday.setText(
                "Welcome back " + name + " " + surname +
                        "\n\nToday is " + today.toUpperCase(Locale.ENGLISH)
        );


        // Banner
        String[] titles = {
                "Medication Reminders",
                "Book Your Appointments",
                "Easy Prescription Refills"
        };
        String[] descriptions = {
                "Never forget your medication with personalised reminders.",
                "Keep track of your doctor appointments and healthcare schedule.",
                "Upload your prescription and manage your pharmacy refills with ease."
        };
        ViewPager2 featureViewPager = findViewById(R.id.featureViewPager);
        featureViewPager.setAdapter(new Featurebanner(titles, descriptions));



        // Demo appointment: always four days from today.
        Calendar visit = Calendar.getInstance();
        visit.add(Calendar.DAY_OF_YEAR, 4);

        String visitDate = new SimpleDateFormat(
                "dd\nMMM",
                Locale.ENGLISH
        ).format(visit.getTime());

        tvVisitDate.setText(visitDate.toUpperCase(Locale.ENGLISH));

        tvVisitDate.setText(visitDate + " · Clinic visit");

        findViewById(R.id.btnNotifications).setOnClickListener(v ->
                startActivity(new Intent(this, NotificationActivity.class)));


        findViewById(R.id.btnSeeMedicines).setOnClickListener(
                view -> startActivity(new Intent(this, MedicationsActivity.class)));

        findViewById(R.id.btnViewVisits).setOnClickListener(v -> {

            Intent intent = new Intent(
                    Patient_Home.this,
                    All_Appointments_PatientsActivity.class
            );

            startActivity(intent);
        });


        findViewById(R.id.cardPrices).setOnClickListener(
                view -> startActivity(new Intent(this, MedicationPriceComparisonActivity.class)));

        findViewById(R.id.cardRefill).setOnClickListener(
                view -> startActivity(new Intent(this, MedicationsActivity.class)));

        findViewById(R.id.cardBlog).setOnClickListener(view -> {
            Intent intent = new Intent(Patient_Home.this, BlogActivity.class);
            startActivity(intent);
        });

        // Tapping the appointment card triggers "View visits".
        findViewById(R.id.cardVisit).setOnClickListener(view ->
                findViewById(R.id.btnViewVisits).performClick()
        );



        navProfile.setOnClickListener(v ->
                startActivity(new Intent(this, ProfileActivity.class)));

        navSetting.setOnClickListener(v ->
                startActivity(new Intent(this, MedicationPriceComparisonActivity.class)));

        navEmergency.setOnClickListener(v ->
                startActivity(new Intent(this, EmergencyActivity.class)));

        navMed.setOnClickListener(v ->
                startActivity(new Intent(this, MedicationsActivity.class)));

        navHome.setBackgroundResource(R.drawable.nav_icon_glow);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadHomeMedication();
        loadUpcomingAppointment();
    }

    private void loadUpcomingAppointment() {

        SessionManager sessionManager =
                new SessionManager(this);

        String patientIdString =
                sessionManager.getUserId();

        if (patientIdString == null ||
                patientIdString.isEmpty()) {
            return;
        }

        int patientId;

        try {
            patientId = Integer.parseInt(patientIdString);
        } catch (NumberFormatException e) {
            return;
        }

        RetrofitClient.INSTANCE
                .getApiService()
                .getPatientAppointments(patientId)
                .enqueue(new Callback<PatientAppointmentsResponse>() {

                    @Override
                    public void onResponse(
                            Call<PatientAppointmentsResponse> call,
                            Response<PatientAppointmentsResponse> response
                    ) {

                        if (!response.isSuccessful()
                                || response.body() == null
                                || !response.body().getSuccess()) {

                            showNoUpcomingAppointment();
                            return;
                        }

                        PatientAppointment nextAppointment =
                                findNextAppointment(
                                        response.body().getAppointments()
                                );

                        if (nextAppointment == null) {
                            showNoUpcomingAppointment();
                            return;
                        }

                        displayUpcomingAppointment(nextAppointment);
                    }

                    @Override
                    public void onFailure(
                            Call<PatientAppointmentsResponse> call,
                            Throwable t
                    ) {
                        showNoUpcomingAppointment();
                    }
                });
    }

    private PatientAppointment findNextAppointment(
            List<PatientAppointment> appointments
    ) {

        SimpleDateFormat format =
                new SimpleDateFormat(
                        "yyyy-MM-dd HH:mm:ss",
                        Locale.US
                );

        Date now = new Date();

        PatientAppointment nextAppointment = null;
        Date nextDate = null;

        for (PatientAppointment appointment : appointments) {

            String status = appointment.getStatus();

            // These should not appear as upcoming
            if (status.equalsIgnoreCase("rejected")
                    || status.equalsIgnoreCase("cancelled")
                    || status.equalsIgnoreCase("completed")) {
                continue;
            }

            try {

                Date appointmentDate =
                        format.parse(
                                appointment.getAppointment_date()
                                        + " "
                                        + appointment.getAppointment_time()
                        );

                if (appointmentDate == null) {
                    continue;
                }

                if (appointmentDate.before(now)) {
                    continue;
                }

                if (nextDate == null ||
                        appointmentDate.before(nextDate)) {

                    nextDate = appointmentDate;
                    nextAppointment = appointment;
                }

            } catch (Exception ignored) {
            }
        }

        return nextAppointment;
    }

    private void displayUpcomingAppointment(
            PatientAppointment appointment
    ) {

        cardVisit.setVisibility(View.VISIBLE);

        tvVisitDate.setText(
                formatHomeAppointmentDate(
                        appointment.getAppointment_date(),
                        appointment.getAppointment_time()
                )
        );

        tvVisitType.setText(
                appointment.getAppointment_type()
        );

        tvVisitReason.setText(
                "Reason: " + appointment.getReason()
        );

        String status = appointment.getStatus();

        String statusText;

        if (status.equalsIgnoreCase("pending")) {

            statusText = "Pending doctor confirmation";

        } else if (status.equalsIgnoreCase("confirmed")) {

            statusText = "Confirmed";

        } else {

            statusText = status;
        }

        tvVisitDetails.setText(
                appointment.getDoctorDisplayName()
                        + " • "
                        + statusText
        );
    }

    private String formatHomeAppointmentDate(
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

    private void showNoUpcomingAppointment() {

        cardVisit.setVisibility(View.GONE);
    }
    private Medication findNextMedication(
            List<Medication> medications
    ) {

        Medication nextMedication = null;

        int currentMinutes =
                Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
                        * 60
                        + Calendar.getInstance().get(Calendar.MINUTE);

        int smallestDifference =
                Integer.MAX_VALUE;


        for (Medication medication : medications) {

            if (medication.getRemaining_doses() <= 0) {
                continue;
            }

            String time =
                    medication.getScheduled_time();

            if (time == null || time.isEmpty()) {
                continue;
            }

            try {

                String[] parts =
                        time.split(":");

                int hour =
                        Integer.parseInt(parts[0]);

                int minute =
                        Integer.parseInt(parts[1]);

                int medicationMinutes =
                        hour * 60 + minute;


                int difference =
                        medicationMinutes - currentMinutes;


                // If today's time already passed,
                // treat it as later in the daily cycle.
                if (difference < 0) {
                    difference += 24 * 60;
                }


                if (difference < smallestDifference) {

                    smallestDifference =
                            difference;

                    nextMedication =
                            medication;
                }

            } catch (Exception e) {

                e.printStackTrace();
            }
        }


        // Fallback if medication has no valid time
        if (nextMedication == null) {

            for (Medication medication : medications) {

                if (medication.getRemaining_doses() > 0) {

                    return medication;
                }
            }
        }


        return nextMedication;
    }

    private void loadHomeMedication() {

        SessionManager sessionManager =
                new SessionManager(this);

        String patientId =
                sessionManager.getUserId();

        if (patientId == null || patientId.isEmpty()) {

            showMessage("Unable to find patient ID");
            return;
        }

        RetrofitClient.INSTANCE
                .getApiService()
                .getMedications(patientId)
                .enqueue(new Callback<MedicationResponse>() {

                    @Override
                    public void onResponse(
                            Call<MedicationResponse> call,
                            Response<MedicationResponse> response
                    ) {

                        if (!response.isSuccessful()
                                || response.body() == null
                                || !response.body().getSuccess()) {

                            showMessage(
                                    "Unable to load today's medication"
                            );

                            return;
                        }

                        List<Medication> medications =
                                response.body().getMedications();

                        if (medications == null
                                || medications.isEmpty()) {

                            showNoMedication();
                            return;
                        }

                        Medication medication =
                                findNextMedication(medications);

                        if (medication == null) {

                            showNoMedication();
                            return;
                        }

                        currentMedication = medication;

                        displayMedication(medication);
                    }

                    @Override
                    public void onFailure(
                            Call<MedicationResponse> call,
                            Throwable t
                    ) {

                        showMessage(
                                "Medication connection error: "
                                        + t.getMessage()
                        );
                    }
                });
    }

    private void displayMedication(Medication medication) {

        cardMedicine.setVisibility(View.VISIBLE);

        tvMedicineName.setText(
                medication.getMedication_name()
        );

        String dosage = medication.getDosage();

        if (dosage == null || dosage.isEmpty()) {

            tvMedicineDosage.setText(
                    medication.getRemaining_doses()
                            + " doses remaining"
            );

        } else {

            tvMedicineDosage.setText(
                    dosage
                            + " · "
                            + medication.getRemaining_doses()
                            + " doses remaining"
            );
        }


        String scheduledTime =
                medication.getScheduled_time();

        boolean takenToday =
                isTakenToday(
                        medication.getLast_taken_date()
                );


        if (takenToday) {

            tvDoseStatus.setText(
                    "Taken today"
            );

            btnTakeMedicine.setText(
                    "Taken ✓"
            );

            btnTakeMedicine.setEnabled(false);

        } else {

            if (scheduledTime == null
                    || scheduledTime.isEmpty()) {

                tvDoseStatus.setText(
                        "No scheduled time"
                );

            } else {

                tvDoseStatus.setText(
                        "Due at "
                                + formatMedicationTime(
                                scheduledTime
                        )
                );
            }


            if (medication.getRemaining_doses() <= 0) {

                btnTakeMedicine.setText(
                        "No doses remaining"
                );

                btnTakeMedicine.setEnabled(false);

            } else {

                btnTakeMedicine.setText(
                        "I took it"
                );

                btnTakeMedicine.setEnabled(true);
            }
        }
    }

    private boolean isTakenToday(String lastTakenDate) {

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

    private void showNoMedication() {

        currentMedication = null;

        tvMedicineName.setText(
                "No medication due"
        );

        tvMedicineDosage.setText(
                "You're all caught up"
        );

        tvDoseStatus.setText(
                "No scheduled medication"
        );

        btnTakeMedicine.setText(
                "Nothing due"
        );

        btnTakeMedicine.setEnabled(false);
    }

    private String formatMedicationTime(
            String time
    ) {

        try {

            SimpleDateFormat input =
                    new SimpleDateFormat(
                            "HH:mm:ss",
                            Locale.ENGLISH
                    );

            SimpleDateFormat output =
                    new SimpleDateFormat(
                            "HH:mm",
                            Locale.ENGLISH
                    );

            Date parsed =
                    input.parse(time);

            if (parsed != null) {
                return output.format(parsed);
            }

        } catch (Exception ignored) {

        }


        // Handles HH:mm values too
        if (time.length() >= 5) {
            return time.substring(0, 5);
        }

        return time;
    }

    private void markMedicationAsTaken() {

        if (currentMedication == null) {

            showMessage("No medication selected");
            return;
        }


        SessionManager sessionManager =
                new SessionManager(this);

        String patientIdString =
                sessionManager.getUserId();


        if (patientIdString == null
                || patientIdString.isEmpty()) {

            showMessage("Unable to find patient ID");
            return;
        }


        int patientId;

        try {

            patientId =
                    Integer.parseInt(patientIdString);

        } catch (NumberFormatException e) {

            showMessage("Invalid patient ID");
            return;
        }


        // Prevent multiple taps while request is running
        btnTakeMedicine.setEnabled(false);
        btnTakeMedicine.setText("Updating...");


        RetrofitClient.INSTANCE
                .getApiService()
                .updateMedicationTaken(
                        currentMedication.getPrescription_id(),
                        patientId,
                        "taken"
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

                                    showMessage(
                                            "Medication marked as taken"
                                    );

                                    // Reload from MySQL so the dashboard
                                    // shows the updated remaining doses.
                                    loadHomeMedication();

                                } else {

                                    btnTakeMedicine.setEnabled(true);
                                    btnTakeMedicine.setText("I took it");

                                    String message =
                                            response.body() != null
                                                    ? response.body().getMessage()
                                                    : "Unable to update medication";

                                    showMessage(message);
                                }
                            }


                            @Override
                            public void onFailure(
                                    Call<MedicationUpdateResponse> call,
                                    Throwable t
                            ) {

                                btnTakeMedicine.setEnabled(true);
                                btnTakeMedicine.setText("I took it");

                                showMessage(
                                        "Connection error: "
                                                + t.getMessage()
                                );
                            }
                        }
                );
    }

    private void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
