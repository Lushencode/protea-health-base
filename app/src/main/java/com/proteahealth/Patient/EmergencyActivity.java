package com.proteahealth.Patient;


import android.content.Intent;

import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.proteahealth.ProfileActivity;
import com.proteahealth.data.EmergencyProfileLoader;
import com.proteahealth.data.SessionManager;
import com.proteahealth.R;

public class EmergencyActivity extends AppCompatActivity {

    private static final String EMERGENCY_NUMBER = "112";
    private static final String AMBULANCE_NUMBER = "10177";
    private String contactPhone = "";
    private String medications = "Metformin 500mg · Aspirin 75mg";
    private String conditions = "Type 2 diabetes · Asthma";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true);
            setTurnScreenOn(true);
        } else {
            getWindow().addFlags(
                    WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                            | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        }

        setContentView(R.layout.activity_emergency);

        loadPatientData();
        bindActions();


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

        navEmergency.setBackgroundResource(R.drawable.nav_icon_glow);
    }

    private void loadPatientData() {

        SessionManager sessionManager =
                new SessionManager(this);

        String patientId =
                sessionManager.getUserId();

        if (patientId == null || patientId.isEmpty()) {

            Toast.makeText(
                    this,
                    "Unable to identify patient",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        EmergencyProfileLoader.loadPatientProfile(
                patientId,

                user -> {

                    String contactName =
                            user.getEmergency_contact_name();

                    String contactRelation =
                            user.getEmergency_contact_relationship();

                    contactPhone =
                            user.getEmergency_contact_number();

                    String allergies =
                            user.getAllergies();

                    String medicalConditions =
                            user.getMedical_conditions();


                    if (contactName == null ||
                            contactName.isEmpty()) {

                        contactName =
                                "No emergency contact";
                    }

                    if (contactRelation == null ||
                            contactRelation.isEmpty()) {

                        contactRelation = "-";
                    }

                    if (contactPhone == null ||
                            contactPhone.isEmpty()) {

                        contactPhone = "-";
                    }


                    if (allergies == null || allergies.isEmpty()) {
                        allergies = "None recorded";
                    }

                    if (medicalConditions == null || medicalConditions.isEmpty()) {
                        medicalConditions = "None recorded";
                    }

                    ((TextView) findViewById(
                            R.id.tvAllergies
                    )).setText(allergies);

                    ((TextView) findViewById(
                            R.id.tvConditions
                    )).setText(medicalConditions);

                    ((TextView) findViewById(
                            R.id.tvContactName
                    )).setText(contactName);

                    ((TextView) findViewById(
                            R.id.tvContactDetails
                    )).setText(
                            contactRelation +
                                    " · " +
                                    contactPhone
                    );

                    return kotlin.Unit.INSTANCE;
                },

                error -> {

                    Toast.makeText(
                            this,
                            error,
                            Toast.LENGTH_LONG
                    ).show();

                    return kotlin.Unit.INSTANCE;
                }
        );
    }



    private void bindActions() {
        findViewById(R.id.cardCallEmergency).setOnClickListener(v -> dial(EMERGENCY_NUMBER));
        findViewById(R.id.cardAmbulance).setOnClickListener(v -> dial(AMBULANCE_NUMBER));
        findViewById(R.id.btnCallContact).setOnClickListener(v -> callContact());
        findViewById(R.id.tvEdit).setOnClickListener(v ->
                Toast.makeText(this, "Edit medical info", Toast.LENGTH_SHORT).show());
    }

    private void dial(String number) {
        startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + number)));
    }

    private void callContact() {

        if (contactPhone == null ||
                contactPhone.isEmpty() ||
                contactPhone.equals("-")) {

            Toast.makeText(
                    this,
                    "No emergency contact number available",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        dial(contactPhone);
    }


}