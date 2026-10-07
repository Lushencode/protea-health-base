package com.proteahealth.Patient;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import android.util.Log;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.AdapterView;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;

import com.proteahealth.api.DoctorAvailabilityResponse;

import com.proteahealth.api.AppointmentDoctor;
import com.proteahealth.api.DoctorResponse;
import com.proteahealth.api.RetrofitClient;
import com.proteahealth.api.AppointmentCreateResponse;
import com.proteahealth.data.SessionManager;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


import androidx.appcompat.app.AppCompatActivity;

import com.proteahealth.ProfileActivity;
import com.proteahealth.R;

import java.util.Calendar;

public class AppointmentActivity extends AppCompatActivity {

    // Appointment fields
    private List<String> availableTimeSlots = new ArrayList<>();
    private Spinner appointmentTypeSpinner;
    private EditText facilityInput;
    private EditText dateInput;
    private EditText timeInput;
    private EditText reasonInput;

    private Spinner doctorSpinner;
    private TextView tvDoctorSpecialization;
    private TextView tvDoctorLocation;

    private List<AppointmentDoctor> doctors =
            new ArrayList<>();

    private AppointmentDoctor selectedDoctor = null;

    // Medication fields
    private LinearLayout medicationCard;
    private Spinner pharmacySpinner;
    private RadioGroup medicationMethodGroup;
    private LinearLayout deliverySection;
    private EditText addressInput;

    // Buttons
    private Button confirmButton;
    private ImageButton backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_appointment);

        // Connect Java to XML
        initializeViews();

        // Load dropdown information
        setupAppointmentTypes();
        loadDoctors();
        setupPharmacies();

        // Date and time pickers
        setupDatePicker();
        setupTimePicker();

        // Collection / delivery selection
        setupMedicationMethod();

        // Back button
        setupBackButton();

        // Confirm button
        setupConfirmButton();

        // Check the user's role
        checkUserRole();

        //nav

        ImageButton navProfile = findViewById(R.id.navProfile);
        ImageButton navSetting = findViewById(R.id.navSetting);
        ImageButton navHome = findViewById(R.id.navhome);
        ImageButton navEmergency = findViewById(R.id.navEmergency);
        ImageButton navMed = findViewById(R.id.navMed);

        navProfile.setOnClickListener(v -> {
            startActivity(new Intent(this, ProfileActivity.class));
        });

        navHome.setOnClickListener(v -> {
            startActivity(new Intent(this, Patient_Home.class));
        });

        navMed.setOnClickListener(v -> {
            startActivity(new Intent(this, MedicationsActivity.class));
        });

        navSetting.setOnClickListener(v -> {
            startActivity(new Intent(this, MedicationPriceComparisonActivity.class));
        });

        navEmergency.setOnClickListener(v -> {
            startActivity(new Intent(this, EmergencyActivity.class));
        });
    }

    // ---------------------------------------------------------
    // CONNECT XML VIEWS
    // ---------------------------------------------------------

    private void initializeViews() {

        appointmentTypeSpinner = findViewById(R.id.appointmentTypeSpinner);
        facilityInput = findViewById(R.id.facilityInput);
        dateInput = findViewById(R.id.dateInput);
        timeInput = findViewById(R.id.timeInput);
        reasonInput = findViewById(R.id.reasonInput);
        doctorSpinner =
                findViewById(R.id.doctorSpinner);

        tvDoctorSpecialization =
                findViewById(R.id.tvDoctorSpecialization);

        tvDoctorLocation =
                findViewById(R.id.tvDoctorLocation);


        medicationMethodGroup = findViewById(R.id.medicationMethodGroup);
        deliverySection = findViewById(R.id.deliverySection);
        addressInput = findViewById(R.id.addressInput);

        confirmButton = findViewById(R.id.confirmButton);
        backButton = findViewById(R.id.backButton);
    }

    // ---------------------------------------------------------
    // APPOINTMENT TYPE DROPDOWN
    // ---------------------------------------------------------

    private void setupAppointmentTypes() {

        String[] appointmentTypes = {
                "Select appointment type",
                "Doctor consultation",
                "Clinic visit",
                "Follow-up appointment",
                "Medication review"
        };

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                appointmentTypes
        );

        adapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );

        appointmentTypeSpinner.setAdapter(adapter);
    }

    // ---------------------------------------------------------
    // PHARMACY DROPDOWN
    // ---------------------------------------------------------

    private void setupPharmacies() {

        if (pharmacySpinner == null) return;

        String[] pharmacies = {
                "Select pharmacy",
                "Dis-Chem",
                "Clicks",
                "Medirite",
                "Local pharmacy"
        };

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                pharmacies
        );

        adapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );

        pharmacySpinner.setAdapter(adapter);
    }

    // ---------------------------------------------------------
    // DATE PICKER
    // ---------------------------------------------------------

    private void setupDatePicker() {

        dateInput.setOnClickListener(v -> {

            Calendar calendar = Calendar.getInstance();

            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog =
                    new DatePickerDialog(
                            AppointmentActivity.this,
                            (view, selectedYear, selectedMonth, selectedDay) -> {

                                String selectedDate =
                                        selectedDay + "/" +
                                                (selectedMonth + 1) + "/" +
                                                selectedYear;

                                dateInput.setText(selectedDate);
                                timeInput.setText("");
                                availableTimeSlots.clear();


                            },
                            year,
                            month,
                            day
                    );

            // Do not allow dates in the past
            datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);

            datePickerDialog.show();
        });
    }

    // ---------------------------------------------------------
    // TIME PICKER
    // ---------------------------------------------------------

    private void setupTimePicker() {

        timeInput.setFocusable(false);
        timeInput.setClickable(true);

        timeInput.setHint("Select available time");

        timeInput.setOnClickListener(v -> {

            if (selectedDoctor == null) {

                Toast.makeText(
                        this,
                        "Please select a doctor first.",
                        Toast.LENGTH_SHORT
                ).show();

                return;
            }

            String selectedDate =
                    dateInput.getText().toString().trim();

            if (selectedDate.isEmpty()) {

                Toast.makeText(
                        this,
                        "Please select a date first.",
                        Toast.LENGTH_SHORT
                ).show();

                return;
            }

            loadAvailableTimes();
        });
    }

    private void loadAvailableTimes() {

        if (selectedDoctor == null) {
            return;
        }

        String displayedDate =
                dateInput.getText().toString().trim();

        if (displayedDate.isEmpty()) {
            return;
        }

        String selectedDate;

        try {

            SimpleDateFormat displayFormat =
                    new SimpleDateFormat(
                            "d/M/yyyy",
                            Locale.getDefault()
                    );

            SimpleDateFormat apiFormat =
                    new SimpleDateFormat(
                            "yyyy-MM-dd",
                            Locale.US
                    );

            Date parsedDate =
                    displayFormat.parse(displayedDate);

            if (parsedDate == null) {
                throw new Exception("Invalid date");
            }

            selectedDate =
                    apiFormat.format(parsedDate);

        } catch (Exception e) {

            Toast.makeText(
                    this,
                    "Invalid appointment date",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        if (selectedDate.isEmpty()) {
            return;
        }
        Log.d(
                "APPOINTMENT_API",
                "Date being sent to API: " + selectedDate
        );
        RetrofitClient.INSTANCE
                .getApiService()
                .getDoctorAvailability(
                        selectedDoctor.getId(),
                        selectedDate
                )
                .enqueue(
                        new Callback<DoctorAvailabilityResponse>() {

                            @Override
                            public void onResponse(
                                    Call<DoctorAvailabilityResponse> call,
                                    Response<DoctorAvailabilityResponse> response
                            ) {

                                if (!response.isSuccessful()
                                        || response.body() == null
                                        || !response.body().getSuccess()) {

                                    Toast.makeText(
                                            AppointmentActivity.this,
                                            "Unable to load available times.",
                                            Toast.LENGTH_SHORT
                                    ).show();

                                    return;
                                }

                                availableTimeSlots =
                                        response.body().getSlots();

                                if (availableTimeSlots.isEmpty()) {

                                    timeInput.setText("");

                                    Toast.makeText(
                                            AppointmentActivity.this,
                                            "Doctor has no available times on this date.",
                                            Toast.LENGTH_SHORT
                                    ).show();

                                    return;
                                }

                                showAvailableTimeDialog();
                            }

                            @Override
                            public void onFailure(
                                    Call<DoctorAvailabilityResponse> call,
                                    Throwable t
                            ) {

                                Toast.makeText(
                                        AppointmentActivity.this,
                                        "Connection error: " + t.getMessage(),
                                        Toast.LENGTH_LONG
                                ).show();
                            }
                        }
                );
    }

    private void showAvailableTimeDialog() {

        String[] slots =
                availableTimeSlots.toArray(new String[0]);

        new AlertDialog.Builder(this)
                .setTitle("Select available time")
                .setItems(slots, (dialog, which) -> {

                    String selectedTime =
                            availableTimeSlots.get(which);

                    timeInput.setText(selectedTime);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    // COLLECTION / DELIVERY


    private void setupMedicationMethod() {

        if (medicationMethodGroup == null) return;

        medicationMethodGroup.setOnCheckedChangeListener(
                (group, checkedId) -> {

                    if (checkedId == R.id.deliveryRadio) {

                        // Show delivery address
                        deliverySection.setVisibility(View.VISIBLE);

                    } else if (checkedId == R.id.collectionRadio) {

                        // Hide delivery address
                        deliverySection.setVisibility(View.GONE);

                        // Clear old address
                        addressInput.setText("");
                    }
                }
        );
    }

    private void loadDoctors() {

        RetrofitClient.INSTANCE
                .getApiService()
                .getDoctors()
                .enqueue(new Callback<DoctorResponse>() {

                    @Override
                    public void onResponse(
                            Call<DoctorResponse> call,
                            Response<DoctorResponse> response
                    ) {

                        if (!response.isSuccessful()
                                || response.body() == null
                                || !response.body().getSuccess()) {

                            Toast.makeText(
                                    AppointmentActivity.this,
                                    "Unable to load doctors",
                                    Toast.LENGTH_SHORT
                            ).show();

                            return;
                        }

                        doctors = response.body().getDoctors();

                        setupDoctorSpinner();
                    }

                    @Override
                    public void onFailure(
                            Call<DoctorResponse> call,
                            Throwable t
                    ) {

                        Toast.makeText(
                                AppointmentActivity.this,
                                "Connection error: " + t.getMessage(),
                                Toast.LENGTH_LONG
                        ).show();
                    }
                });
    }

    private void setupDoctorSpinner() {

        List<String> doctorNames =
                new ArrayList<>();

        doctorNames.add("Select doctor");

        for (AppointmentDoctor doctor : doctors) {

            doctorNames.add(
                    doctor.getDisplayName()
            );
        }

        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_spinner_item,
                        doctorNames
                );

        adapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );

        doctorSpinner.setAdapter(adapter);


        doctorSpinner.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {

                    @Override
                    public void onItemSelected(
                            AdapterView<?> parent,
                            View view,
                            int position,
                            long id
                    ) {

                        if (position == 0) {

                            selectedDoctor = null;

                            facilityInput.setText("");

                            tvDoctorSpecialization.setText(
                                    "Specialization will appear here"
                            );

                            tvDoctorLocation.setText(
                                    "Facility address will appear here"
                            );

                            return;
                        }

                        timeInput.setText("");
                        availableTimeSlots.clear();
                        // Position 0 is "Select doctor",
                        // so actual doctor index is position - 1.
                        selectedDoctor =
                                doctors.get(position - 1);

                        displaySelectedDoctor();
                    }


                    @Override
                    public void onNothingSelected(
                            AdapterView<?> parent
                    ) {

                    }
                }
        );
    }

    private void displaySelectedDoctor() {

        if (selectedDoctor == null) {
            return;
        }


        String specialization =
                selectedDoctor.getSpecialization();

        String clinicName =
                selectedDoctor.getClinic_name();

        String location =
                selectedDoctor.getLocation();


        tvDoctorSpecialization.setText(
                specialization != null
                        && !specialization.isEmpty()
                        ? specialization
                        : "Specialization not provided"
        );


        facilityInput.setText(
                clinicName != null
                        && !clinicName.isEmpty()
                        ? clinicName
                        : "Clinic not provided"
        );


        tvDoctorLocation.setText(
                location != null
                        && !location.isEmpty()
                        ? location
                        : "Location not provided"
        );
    }


    // BACK BUTTON

    private void setupBackButton() {

        if (backButton == null) return;

        backButton.setOnClickListener(v -> {

            finish();

        });
    }

    // CONFIRM BUTTON

    private void setupConfirmButton() {

        confirmButton.setOnClickListener(v -> {
            showConfirmationDialog();
        });
    }

    private void showConfirmationDialog() {

        // Make sure a doctor is selected
        if (selectedDoctor == null) {
            Toast.makeText(
                    this,
                    "Please select a doctor.",
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }

        // Make sure appointment type is selected
        if (appointmentTypeSpinner.getSelectedItemPosition() == 0) {
            Toast.makeText(
                    this,
                    "Please select an appointment type.",
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }

        String appointmentType =
                appointmentTypeSpinner.getSelectedItem().toString();

        String date =
                dateInput.getText().toString().trim();

        String time =
                timeInput.getText().toString().trim();

        String reason =
                reasonInput.getText().toString().trim();

        if (date.isEmpty()) {
            Toast.makeText(
                    this,
                    "Please select a date.",
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }

        if (time.isEmpty()) {
            Toast.makeText(
                    this,
                    "Please select an available time.",
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }

        if (reason.isEmpty()) {
            Toast.makeText(
                    this,
                    "Please enter a reason for the appointment.",
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }

        String doctorName =
                selectedDoctor.getDisplayName();

        String clinic =
                selectedDoctor.getClinic_name();

        String message =
                "Doctor: " + doctorName +
                        "\n\nAppointment: " + appointmentType +
                        "\nDate: " + date +
                        "\nTime: " + time +
                        "\nClinic: " + clinic +
                        "\nReason: " + reason;

        new AlertDialog.Builder(this)
                .setTitle("Confirm Appointment")
                .setMessage(message)

                .setNegativeButton(
                        "Cancel",
                        (dialog, which) -> dialog.dismiss()
                )

                .setPositiveButton(
                        "Confirm",
                        (dialog, which) -> {

                            dialog.dismiss();

                            // ONLY save after user confirms
                            submitAppointment();
                        }
                )

                .show();
    }
    // CHECK USER ROLE

    private void checkUserRole() {

        String userRole = getIntent().getStringExtra("USER_ROLE");

        // If no role was supplied, treat the user as a patient
        if (userRole == null || userRole.isEmpty()) {
            userRole = "PATIENT";
        }

        if (medicationCard == null) {
            return;
        }

        switch (userRole) {

            case "PATIENT":

                // Patients can book appointments
                // and arrange medication collection/delivery.
                medicationCard.setVisibility(View.VISIBLE);
                break;

            case "PHARMACY":

                // Pharmacy users can currently see
                // medication-related functionality.
                medicationCard.setVisibility(View.VISIBLE);
                break;

            case "DOCTOR":

                // Healthcare workers don't need
                // the medication request section here.
                medicationCard.setVisibility(View.GONE);
                break;

            default:

                medicationCard.setVisibility(View.VISIBLE);
                break;
        }
    }

    // VALIDATE AND SUBMIT

    private String convertDateForApi(String displayedDate) {

        try {

            SimpleDateFormat displayFormat =
                    new SimpleDateFormat(
                            "d/M/yyyy",
                            Locale.getDefault()
                    );

            SimpleDateFormat apiFormat =
                    new SimpleDateFormat(
                            "yyyy-MM-dd",
                            Locale.US
                    );

            Date date =
                    displayFormat.parse(displayedDate);

            if (date == null) {
                return null;
            }

            return apiFormat.format(date);

        } catch (Exception e) {

            Log.e(
                    "APPOINTMENT_API",
                    "Date conversion failed",
                    e
            );

            return null;
        }
    }


    private void submitAppointment() {

        String appointmentType =
                appointmentTypeSpinner.getSelectedItem().toString();

        String displayedDate =
                dateInput.getText().toString().trim();

        String appointmentTime =
                timeInput.getText().toString().trim();

        String reason =
                reasonInput.getText().toString().trim();

        // Check doctor
        if (selectedDoctor == null) {
            Toast.makeText(
                    this,
                    "Please select a doctor.",
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }

        // Check appointment type
        if (appointmentTypeSpinner.getSelectedItemPosition() == 0) {
            Toast.makeText(
                    this,
                    "Please select an appointment type.",
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }

        // Check date
        if (displayedDate.isEmpty()) {
            Toast.makeText(
                    this,
                    "Please select an appointment date.",
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }

        // Check preferred time
        if (appointmentTime.isEmpty()) {
            Toast.makeText(
                    this,
                    "Please select an available time.",
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }

        // Check reason
        if (reason.isEmpty()) {
            Toast.makeText(
                    this,
                    "Please enter a reason for the appointment.",
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }

        // Convert 8/10/2026 -> 2026-10-08
        String appointmentDate =
                convertDateForApi(displayedDate);

        if (appointmentDate == null) {
            Toast.makeText(
                    this,
                    "Invalid appointment date.",
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }

        // Get logged-in patient
        SessionManager sessionManager =
                new SessionManager(this);

        String patientIdString =
                sessionManager.getUserId();

        if (patientIdString == null || patientIdString.isEmpty()) {
            Toast.makeText(
                    this,
                    "Unable to identify logged-in patient.",
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }

        int patientId;

        try {
            patientId = Integer.parseInt(patientIdString);
        } catch (NumberFormatException e) {
            Toast.makeText(
                    this,
                    "Invalid patient ID.",
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }

        Log.d(
                "APPOINTMENT_API",
                "Creating appointment: patient=" + patientId
                        + ", doctor=" + selectedDoctor.getId()
                        + ", date=" + appointmentDate
                        + ", time=" + appointmentTime
        );

        // Send to PHP
        RetrofitClient.INSTANCE
                .getApiService()
                .createAppointment(
                        patientId,
                        selectedDoctor.getId(),
                        appointmentType,
                        appointmentDate,
                        appointmentTime,
                        reason
                )
                .enqueue(new Callback<AppointmentCreateResponse>() {

                    @Override
                    public void onResponse(
                            Call<AppointmentCreateResponse> call,
                            Response<AppointmentCreateResponse> response
                    ) {

                        if (response.isSuccessful()
                                && response.body() != null) {

                            AppointmentCreateResponse result =
                                    response.body();

                            Log.d(
                                    "APPOINTMENT_API",
                                    "Response: success="
                                            + result.getSuccess()
                                            + ", message="
                                            + result.getMessage()
                            );

                            Toast.makeText(
                                    AppointmentActivity.this,
                                    result.getMessage(),
                                    Toast.LENGTH_LONG
                            ).show();

                            if (result.getSuccess()) {
                                clearForm();
                            }

                        } else {

                            Log.e(
                                    "APPOINTMENT_API",
                                    "HTTP error: " + response.code()
                            );

                            Toast.makeText(
                                    AppointmentActivity.this,
                                    "Unable to create appointment.",
                                    Toast.LENGTH_LONG
                            ).show();
                        }
                    }

                    @Override
                    public void onFailure(
                            Call<AppointmentCreateResponse> call,
                            Throwable t
                    ) {

                        Log.e(
                                "APPOINTMENT_API",
                                "Create appointment failed",
                                t
                        );

                        Toast.makeText(
                                AppointmentActivity.this,
                                "Connection error: " + t.getMessage(),
                                Toast.LENGTH_LONG
                        ).show();
                    }
                });
    }

    // ---------------------------------------------------------
    // CLEAR FORM
    // ---------------------------------------------------------

    private void clearForm() {

        if (appointmentTypeSpinner != null) appointmentTypeSpinner.setSelection(0);

        if (facilityInput != null) facilityInput.setText("");
        if (dateInput != null) dateInput.setText("");
        if (timeInput != null) timeInput.setText("");
        if (reasonInput != null) reasonInput.setText("");

        if (pharmacySpinner != null) pharmacySpinner.setSelection(0);

        if (medicationMethodGroup != null) medicationMethodGroup.clearCheck();

        if (addressInput != null) addressInput.setText("");

        if (deliverySection != null) deliverySection.setVisibility(View.GONE);

        appointmentTypeSpinner.setSelection(0);

        doctorSpinner.setSelection(0);

        selectedDoctor = null;

        facilityInput.setText("");

        tvDoctorSpecialization.setText(
                "Specialization will appear here");
    }


}