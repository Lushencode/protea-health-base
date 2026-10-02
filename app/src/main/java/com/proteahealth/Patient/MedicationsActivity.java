package com.proteahealth.Patient;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.proteahealth.ProfileActivity;
import com.proteahealth.R;
import com.proteahealth.adapter.MedicationAdapter;
import com.proteahealth.data.MockDataProvider;
import com.proteahealth.model.Prescription;

import java.util.ArrayList;
import java.util.List;

public class MedicationsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medications);

        // -----------------------------------------
        // MEDICATIONS
        // -----------------------------------------

        List<Prescription> allPrescriptions =
                MockDataProvider.getPrescriptions();

        setupSection(
                allPrescriptions,
                "Morning",
                R.id.sectionMorning,
                R.id.rvMorning
        );

        setupSection(
                allPrescriptions,
                "Afternoon",
                R.id.sectionAfternoon,
                R.id.rvAfternoon
        );

        setupSection(
                allPrescriptions,
                "Evening",
                R.id.sectionEvening,
                R.id.rvEvening
        );

        setupSection(
                allPrescriptions,
                "Bedtime",
                R.id.sectionBedtime,
                R.id.rvBedtime
        );


        // -----------------------------------------
        // BOTTOM NAVIGATION
        // -----------------------------------------

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

        navSetting.setOnClickListener(v -> {
            startActivity(new Intent(this, PharmacyActivity.class));
        });

        navEmergency.setOnClickListener(v -> {
            startActivity(new Intent(this, EmergencyActivity.class));
        });

        navMed.setBackgroundResource(R.drawable.nav_icon_glow);

    }


    // -----------------------------------------
    // SETUP MEDICATION SECTION
    // -----------------------------------------

    private void setupSection(
            List<Prescription> allPrescriptions,
            String timeOfDay,
            int sectionViewId,
            int recyclerViewId
    ) {

        List<Prescription> matching =
                new ArrayList<>();

        for (Prescription prescription : allPrescriptions) {

            if (timeOfDay.equalsIgnoreCase(
                    prescription.getScheduledTime()
            )) {

                matching.add(prescription);
            }
        }


        View section =
                findViewById(sectionViewId);


        if (matching.isEmpty()) {

            section.setVisibility(View.GONE);

            return;
        }


        section.setVisibility(View.VISIBLE);


        RecyclerView recyclerView =
                findViewById(recyclerViewId);

        recyclerView.setLayoutManager(
                new LinearLayoutManager(this)
        );

        recyclerView.setAdapter(
                new MedicationAdapter(matching)
        );
    }
}