package com.proteahealth.Patient;
// Change this to YOUR actual package name.

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


import androidx.appcompat.app.AppCompatActivity;

import com.proteahealth.ProfileActivity;
import com.proteahealth.R;

import java.util.Calendar;

public class Appointment extends AppCompatActivity {

    // Appointment fields
    private Spinner appointmentTypeSpinner;
    private EditText facilityInput;
    private EditText dateInput;
    private EditText timeInput;
    private EditText reasonInput;

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
            startActivity(new Intent(this, PharmacyActivity.class));
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

        pharmacySpinner = findViewById(R.id.pharmacySpinner);
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
                            Appointment.this,
                            (view, selectedYear, selectedMonth, selectedDay) -> {

                                String selectedDate =
                                        selectedDay + "/" +
                                                (selectedMonth + 1) + "/" +
                                                selectedYear;

                                dateInput.setText(selectedDate);

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

        timeInput.setOnClickListener(v -> {

            Calendar calendar = Calendar.getInstance();

            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);

            TimePickerDialog timePickerDialog =
                    new TimePickerDialog(
                            Appointment.this,
                            (view, selectedHour, selectedMinute) -> {

                                String formattedTime =
                                        String.format(
                                                "%02d:%02d",
                                                selectedHour,
                                                selectedMinute
                                        );

                                timeInput.setText(formattedTime);

                            },
                            hour,
                            minute,
                            true
                    );

            timePickerDialog.show();
        });
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


    // BACK BUTTON

    private void setupBackButton() {

        if (backButton == null) return;

        backButton.setOnClickListener(v -> {

            finish();

        });
    }

    // CONFIRM BUTTON

    private void setupConfirmButton() {

        if (confirmButton == null) return;

        confirmButton.setOnClickListener(v -> {

            submitAppointment();

        });
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


    private void submitAppointment() {

        String appointmentType = appointmentTypeSpinner.getSelectedItem().toString();

        String facility = facilityInput.getText().toString().trim();

        String date = dateInput.getText().toString().trim();

        String time = timeInput.getText().toString().trim();

        String reason = reasonInput.getText().toString().trim();


        // APPOINTMENT VALIDATION


        if (appointmentType.equals("Select appointment type")) {

            Toast.makeText(
                    this,
                    "Please select an appointment type.",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        if (facility.isEmpty()) {

            facilityInput.setError("Please enter a healthcare facility.");
            facilityInput.requestFocus();

            return;
        }

        if (date.isEmpty()) {

            dateInput.setError("Please select a date.");
            dateInput.requestFocus();

            return;
        }

        if (time.isEmpty()) {

            timeInput.setError("Please select a time.");
            timeInput.requestFocus();

            return;
        }

        if (reason.isEmpty()) {

            reasonInput.setError("Please enter a reason.");
            reasonInput.requestFocus();

            return;
        }

        // MEDICATION VALIDATION

        if (medicationMethodGroup != null && medicationMethodGroup.getCheckedRadioButtonId() == R.id.deliveryRadio) {

            if (addressInput != null) {
                String address = addressInput.getText().toString().trim();

                if (address.isEmpty()) {

                    addressInput.setError(
                            "Please enter your delivery address."
                    );

                    addressInput.requestFocus();

                    return;
                }
            }
        }

        // ---------------------------------------------
        // SUCCESS MESSAGE
        // ---------------------------------------------

        Toast.makeText(
                this,
                "Appointment confirmed successfully!",
                Toast.LENGTH_LONG
        ).show();

        // Clear the form
        clearForm();
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
    }


}