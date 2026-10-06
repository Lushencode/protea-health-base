package com.proteahealth;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.proteahealth.api.ProfileResponse;
import com.proteahealth.api.RetrofitClient;
import com.proteahealth.data.SessionManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import com.proteahealth.api.UpdateProfileResponse;

import java.util.HashMap;
import java.util.Map;

public class EditProfileActivity extends AppCompatActivity {

    private SessionManager sessionManager;

    private String userId;
    private String role;

    // Common
    private TextView tvEditProfileRole;
    private EditText etName;
    private EditText etSurname;
    private EditText etEmail;

    // Sections
    private LinearLayout patientEditSection;
    private LinearLayout doctorEditSection;
    private LinearLayout pharmacistEditSection;

    // Patient
    private EditText etPatientAge;
    private EditText etPatientPhone;
    private EditText etPatientGender;
    private EditText etPatientAddress;
    private EditText etEmergencyContactName;
    private EditText etEmergencyContactNumber;
    private EditText etEmergencyContactRelationship;
    private EditText etAllergies;
    private EditText etMedicalConditions;

    // Doctor
    private EditText etSpecialization;
    private EditText etDoctorLocation;
    private EditText etClinicName;
    private EditText etDoctorExperience;
    private EditText etConsultationPrice;

    // Pharmacist
    private EditText etPharmacistPhone;
    private EditText etPharmacyLocation;
    private EditText etPharmacistExperience;

    // Buttons
    private Button btnSaveProfile;
    private Button btnCancelEdit;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_edit_profile);

        sessionManager = new SessionManager(this);

        userId = sessionManager.getUserId();
        role = sessionManager.getRole()
                .trim()
                .toLowerCase();

        bindViews();
        setupRole();
        setupButtons();
        loadProfile();
    }


    // ------------------------------------------------
    // BIND XML VIEWS
    // ------------------------------------------------

    private void bindViews() {

        tvEditProfileRole =
                findViewById(R.id.tvEditProfileRole);

        etName =
                findViewById(R.id.etName);

        etSurname =
                findViewById(R.id.etSurname);

        etEmail =
                findViewById(R.id.etEmail);


        // Sections

        patientEditSection =
                findViewById(R.id.patientEditSection);

        doctorEditSection =
                findViewById(R.id.doctorEditSection);

        pharmacistEditSection =
                findViewById(R.id.pharmacistEditSection);


        // Patient

        etPatientAge =
                findViewById(R.id.etPatientAge);

        etPatientPhone =
                findViewById(R.id.etPatientPhone);

        etPatientGender =
                findViewById(R.id.etPatientGender);

        etPatientAddress =
                findViewById(R.id.etPatientAddress);

        etEmergencyContactName =
                findViewById(R.id.etEmergencyContactName);

        etEmergencyContactNumber =
                findViewById(R.id.etEmergencyContactNumber);

        etEmergencyContactRelationship =
                findViewById(R.id.etEmergencyContactRelationship);

        etAllergies =
                findViewById(R.id.etAllergies);

        etMedicalConditions =
                findViewById(R.id.etMedicalConditions);


        // Doctor

        etSpecialization =
                findViewById(R.id.etSpecialization);

        etDoctorLocation =
                findViewById(R.id.etDoctorLocation);

        etClinicName =
                findViewById(R.id.etClinicName);

        etDoctorExperience =
                findViewById(R.id.etDoctorExperience);

        etConsultationPrice =
                findViewById(R.id.etConsultationPrice);


        // Pharmacist

        etPharmacistPhone =
                findViewById(R.id.etPharmacistPhone);

        etPharmacyLocation =
                findViewById(R.id.etPharmacyLocation);

        etPharmacistExperience =
                findViewById(R.id.etPharmacistExperience);


        // Buttons

        btnSaveProfile =
                findViewById(R.id.btnSaveProfile);

        btnCancelEdit =
                findViewById(R.id.btnCancelEdit);
    }


    // ------------------------------------------------
    // SHOW CORRECT ROLE SECTION
    // ------------------------------------------------

    private void setupRole() {

        patientEditSection.setVisibility(View.GONE);
        doctorEditSection.setVisibility(View.GONE);
        pharmacistEditSection.setVisibility(View.GONE);

        switch (role) {

            case "patient":

                tvEditProfileRole.setText("Patient");

                patientEditSection.setVisibility(View.VISIBLE);

                break;


            case "doctor":

                tvEditProfileRole.setText("Doctor");

                doctorEditSection.setVisibility(View.VISIBLE);

                break;


            case "pharmacist":

                tvEditProfileRole.setText("Pharmacist");

                pharmacistEditSection.setVisibility(View.VISIBLE);

                break;


            default:

                tvEditProfileRole.setText(role);
                break;
        }
    }


    // ------------------------------------------------
    // LOAD PROFILE
    // ------------------------------------------------

    private void loadProfile() {

        if (userId == null || userId.isEmpty()) {

            Toast.makeText(
                    this,
                    "Unable to identify logged-in user",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }


        RetrofitClient.INSTANCE
                .getApiService()
                .getProfileCall(userId, role)
                .enqueue(new Callback<ProfileResponse>() {

                    @Override
                    public void onResponse(
                            Call<ProfileResponse> call,
                            Response<ProfileResponse> response
                    ) {

                        if (!response.isSuccessful()) {

                            Toast.makeText(
                                    EditProfileActivity.this,
                                    "Server error: " + response.code(),
                                    Toast.LENGTH_SHORT
                            ).show();

                            return;
                        }


                        ProfileResponse profileResponse =
                                response.body();


                        if (
                                profileResponse == null ||
                                        !profileResponse.getSuccess() ||
                                        profileResponse.getUser() == null
                        ) {

                            String message =
                                    profileResponse != null
                                            ? profileResponse.getMessage()
                                            : "Unable to load profile";

                            Toast.makeText(
                                    EditProfileActivity.this,
                                    message,
                                    Toast.LENGTH_SHORT
                            ).show();

                            return;
                        }


                        // -----------------------------
                        // USER DATA
                        // -----------------------------

                        com.proteahealth.api.ProfileUser user =
                                profileResponse.getUser();


                        // COMMON

                        etName.setText(
                                safe(user.getName())
                        );

                        etSurname.setText(
                                safe(user.getSurname())
                        );

                        etEmail.setText(
                                safe(user.getEmail())
                        );


                        // -----------------------------
                        // PATIENT
                        // -----------------------------

                        if (role.equals("patient")) {

                            etPatientAge.setText(
                                    safe(user.getAge())
                            );

                            etPatientPhone.setText(
                                    safe(user.getPhone())
                            );

                            etPatientGender.setText(
                                    safe(user.getGender())
                            );

                            etPatientAddress.setText(
                                    safe(user.getHome_address())
                            );

                            etEmergencyContactName.setText(
                                    safe(user.getEmergency_contact_name())
                            );

                            etEmergencyContactNumber.setText(
                                    safe(user.getEmergency_contact_number())
                            );

                            etEmergencyContactRelationship.setText(
                                    safe(user.getEmergency_contact_relationship())
                            );

                            etAllergies.setText(
                                    safe(user.getAllergies())
                            );

                            etMedicalConditions.setText(
                                    safe(user.getMedical_conditions())
                            );
                        }


                        // -----------------------------
                        // DOCTOR
                        // -----------------------------

                        if (role.equals("doctor")) {

                            etSpecialization.setText(
                                    safe(user.getSpecialization())
                            );

                            etDoctorLocation.setText(
                                    safe(user.getLocation())
                            );

                            etClinicName.setText(
                                    safe(user.getClinic_name())
                            );

                            etDoctorExperience.setText(
                                    safe(user.getYears_in_professional())
                            );

                            etConsultationPrice.setText(
                                    safe(user.getConsultation_price())
                            );
                        }


                        // -----------------------------
                        // PHARMACIST
                        // -----------------------------

                        if (role.equals("pharmacist")) {

                            etPharmacistPhone.setText(
                                    safe(user.getPhone())
                            );

                            etPharmacyLocation.setText(
                                    safe(user.getPharmacy_location())
                            );

                            etPharmacistExperience.setText(
                                    safe(user.getWork_experience_years())
                            );
                        }
                    }


                    @Override
                    public void onFailure(
                            Call<ProfileResponse> call,
                            Throwable t
                    ) {

                        Toast.makeText(
                                EditProfileActivity.this,
                                "Connection error: " + t.getMessage(),
                                Toast.LENGTH_LONG
                        ).show();
                    }
                });
    }

    private void saveProfile() {

        String name =
                etName.getText().toString().trim();

        String surname =
                etSurname.getText().toString().trim();

        String email =
                etEmail.getText().toString().trim();


        // Basic validation
        if (name.isEmpty()) {

            etName.setError("Name is required");
            etName.requestFocus();
            return;
        }

        if (surname.isEmpty()) {

            etSurname.setError("Surname is required");
            etSurname.requestFocus();
            return;
        }

        if (email.isEmpty()) {

            etEmail.setError("Email is required");
            etEmail.requestFocus();
            return;
        }


        Map<String, String> fields =
                new HashMap<>();


        // Required for every role
        fields.put("id", userId);
        fields.put("role", role);

        fields.put("name", name);
        fields.put("surname", surname);
        fields.put("email", email);


        // --------------------------------
        // PATIENT
        // --------------------------------

        if (role.equals("patient")) {

            fields.put(
                    "age",
                    etPatientAge.getText().toString().trim()
            );

            fields.put(
                    "phone",
                    etPatientPhone.getText().toString().trim()
            );

            fields.put(
                    "gender",
                    etPatientGender.getText().toString().trim()
            );

            fields.put(
                    "home_address",
                    etPatientAddress.getText().toString().trim()
            );

            fields.put(
                    "emergency_contact_name",
                    etEmergencyContactName.getText().toString().trim()
            );

            fields.put(
                    "emergency_contact_number",
                    etEmergencyContactNumber.getText().toString().trim()
            );

            fields.put(
                    "emergency_contact_relationship",
                    etEmergencyContactRelationship.getText().toString().trim()
            );

            fields.put(
                    "allergies",
                    etAllergies.getText().toString().trim()
            );

            fields.put(
                    "medical_conditions",
                    etMedicalConditions.getText().toString().trim()
            );
        }


        // --------------------------------
        // DOCTOR
        // --------------------------------

        else if (role.equals("doctor")) {

            fields.put(
                    "specialization",
                    etSpecialization.getText().toString().trim()
            );

            fields.put(
                    "location",
                    etDoctorLocation.getText().toString().trim()
            );

            fields.put(
                    "clinic_name",
                    etClinicName.getText().toString().trim()
            );

            fields.put(
                    "years_in_professional",
                    etDoctorExperience.getText().toString().trim()
            );

            fields.put(
                    "consultation_price",
                    etConsultationPrice.getText().toString().trim()
            );
        }


        // --------------------------------
        // PHARMACIST
        // --------------------------------

        else if (role.equals("pharmacist")) {

            fields.put(
                    "phone",
                    etPharmacistPhone.getText().toString().trim()
            );

            fields.put(
                    "pharmacy_location",
                    etPharmacyLocation.getText().toString().trim()
            );

            fields.put(
                    "work_experience_years",
                    etPharmacistExperience.getText().toString().trim()
            );
        }


        btnSaveProfile.setEnabled(false);


        RetrofitClient.INSTANCE
                .getApiService()
                .updateProfile(fields)
                .enqueue(new Callback<UpdateProfileResponse>() {

                    @Override
                    public void onResponse(
                            Call<UpdateProfileResponse> call,
                            Response<UpdateProfileResponse> response
                    ) {

                        btnSaveProfile.setEnabled(true);

                        if (!response.isSuccessful()) {

                            Toast.makeText(
                                    EditProfileActivity.this,
                                    "Server error: " + response.code(),
                                    Toast.LENGTH_LONG
                            ).show();

                            return;
                        }


                        UpdateProfileResponse result =
                                response.body();


                        if (result != null && result.getSuccess()) {

                            // Update locally stored common information
                            sessionManager.saveUser(
                                    userId,
                                    name,
                                    surname,
                                    email,
                                    role
                            );


                            Toast.makeText(
                                    EditProfileActivity.this,
                                    "Profile updated successfully",
                                    Toast.LENGTH_SHORT
                            ).show();

                            setResult(RESULT_OK);
                            // Close Edit Profile and return
                            finish();

                        } else {

                            String message =
                                    result != null
                                            ? result.getMessage()
                                            : "Unable to update profile";

                            Toast.makeText(
                                    EditProfileActivity.this,
                                    message,
                                    Toast.LENGTH_LONG
                            ).show();
                        }
                    }


                    @Override
                    public void onFailure(
                            Call<UpdateProfileResponse> call,
                            Throwable t
                    ) {

                        btnSaveProfile.setEnabled(true);

                        Toast.makeText(
                                EditProfileActivity.this,
                                "Connection error: " + t.getMessage(),
                                Toast.LENGTH_LONG
                        ).show();
                    }
                });
    }


    // ------------------------------------------------
    // BUTTONS
    // ------------------------------------------------

    private void setupButtons() {

        btnCancelEdit.setOnClickListener(v -> finish());

        btnSaveProfile.setOnClickListener(v -> saveProfile());
    }


    // ------------------------------------------------
    // NULL-SAFE TEXT
    // ------------------------------------------------

    private String safe(Object value) {

        if (value == null) {
            return "";
        }

        return String.valueOf(value);
    }
}