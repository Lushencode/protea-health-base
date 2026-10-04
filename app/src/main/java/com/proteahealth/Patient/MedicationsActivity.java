package com.proteahealth.Patient;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.proteahealth.ProfileActivity;
import com.proteahealth.R;
import com.proteahealth.adapter.MedicationAdapter;
import com.proteahealth.api.Medication;
import com.proteahealth.api.MedicationResponse;
import com.proteahealth.api.RetrofitClient;
import com.proteahealth.data.SessionManager;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MedicationsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medications);

        loadMedications();

        // -----------------------------
        // BOTTOM NAVIGATION
        // -----------------------------

        ImageButton navProfile = findViewById(R.id.navProfile);
        ImageButton navSetting = findViewById(R.id.navSetting);
        ImageButton navHome = findViewById(R.id.navhome);
        ImageButton navEmergency = findViewById(R.id.navEmergency);
        ImageButton navMed = findViewById(R.id.navMed);

        navProfile.setOnClickListener(v ->
                startActivity(
                        new Intent(
                                this,
                                ProfileActivity.class
                        )
                )
        );

        navHome.setOnClickListener(v ->
                startActivity(
                        new Intent(
                                this,
                                Patient_Home.class
                        )
                )
        );

        navSetting.setOnClickListener(v ->
                startActivity(
                        new Intent(
                                this,
                                MedicationPriceComparisonActivity.class
                        )
                )
        );

        navEmergency.setOnClickListener(v ->
                startActivity(
                        new Intent(
                                this,
                                EmergencyActivity.class
                        )
                )
        );

        navMed.setBackgroundResource(
                R.drawable.nav_icon_glow
        );
    }

    // ==========================================
    // LOAD DATABASE MEDICATIONS
    // ==========================================

    private void loadMedications() {

        SessionManager sessionManager =
                new SessionManager(this);

        String patientId =
                sessionManager.getUserId();

        if (patientId.isEmpty()) {

            Toast.makeText(
                    this,
                    "Unable to find patient ID",
                    Toast.LENGTH_SHORT
            ).show();

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

                        if (response.isSuccessful()
                                && response.body() != null
                                && response.body().getSuccess()) {

                            List<Medication> medications =
                                    response.body()
                                            .getMedications();

                            setupMedicationSections(
                                    medications
                            );

                        } else {

                            Toast.makeText(
                                    MedicationsActivity.this,
                                    "Unable to load medications",
                                    Toast.LENGTH_SHORT
                            ).show();
                        }
                    }

                    @Override
                    public void onFailure(
                            Call<MedicationResponse> call,
                            Throwable t
                    ) {

                        Toast.makeText(
                                MedicationsActivity.this,
                                "Connection error: "
                                        + t.getMessage(),
                                Toast.LENGTH_LONG
                        ).show();
                    }
                });
    }

    // ==========================================
    // SEPARATE MEDICATIONS BY TIME
    // ==========================================

    private void setupMedicationSections(
            List<Medication> medications
    ) {

        List<Medication> morning =
                new ArrayList<>();

        List<Medication> afternoon =
                new ArrayList<>();

        List<Medication> evening =
                new ArrayList<>();

        List<Medication> bedtime =
                new ArrayList<>();


        for (Medication medication : medications) {

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


                // Morning
                if (hour >= 5 && hour < 12) {

                    morning.add(medication);

                }

                // Afternoon
                else if (hour >= 12 && hour < 17) {

                    afternoon.add(medication);

                }

                // Evening
                else if (hour >= 17 && hour < 21) {

                    evening.add(medication);

                }

                // Bedtime
                else {

                    bedtime.add(medication);
                }

            } catch (Exception e) {

                e.printStackTrace();
            }
        }


        setupSection(
                morning,
                R.id.sectionMorning,
                R.id.rvMorning
        );

        setupSection(
                afternoon,
                R.id.sectionAfternoon,
                R.id.rvAfternoon
        );

        setupSection(
                evening,
                R.id.sectionEvening,
                R.id.rvEvening
        );

        setupSection(
                bedtime,
                R.id.sectionBedtime,
                R.id.rvBedtime
        );
    }

    // ==========================================
    // SET UP INDIVIDUAL SECTION
    // ==========================================

    private void setupSection(
            List<Medication> medications,
            int sectionViewId,
            int recyclerViewId
    ) {

        View section =
                findViewById(sectionViewId);

        if (medications.isEmpty()) {

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
                new MedicationAdapter(medications)
        );
    }
}