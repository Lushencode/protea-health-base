package com.proteahealth.Patient;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.proteahealth.ProfileActivity;
import com.proteahealth.R;
import com.proteahealth.adapter.PharmacyAdapter;
import com.proteahealth.data.MockDataProvider;
import com.proteahealth.model.Pharmacy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MedicationPriceComparisonActivity extends AppCompatActivity {

    private static final String DEFAULT_LABEL =
            "Showing prices for: Metformin 500mg";

    private PharmacyAdapter pharmacyAdapter;
    private List<Pharmacy> allPharmacies;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_medication_price_comparison);

        // -----------------------------------------
        // CONNECT XML COMPONENTS
        // -----------------------------------------

        RecyclerView rvPharmacies =
                findViewById(R.id.rvPharmacies);

        EditText etSearchMedication =
                findViewById(R.id.etSearchMedication);

        TextView tvShowingLabel =
                findViewById(R.id.tvShowingLabel);


        // -----------------------------------------
        // GET MOCK PHARMACY DATA
        // -----------------------------------------

        allPharmacies =
                new ArrayList<>(
                        MockDataProvider.getPharmacies()
                );


        // -----------------------------------------
        // SORT CLOSEST PHARMACY FIRST
        // -----------------------------------------

        sortByClosest(allPharmacies);


        // -----------------------------------------
        // CREATE ADAPTER
        // -----------------------------------------

        pharmacyAdapter =
                new PharmacyAdapter(
                        allPharmacies,
                        (pharmacy, medication) -> {

                            Intent resultIntent = new Intent();

                            resultIntent.putExtra(
                                    "pharmacy_id",
                                    pharmacy.getId()
                            );

                            resultIntent.putExtra(
                                    "pharmacy_name",
                                    pharmacy.getName()
                            );

                            resultIntent.putExtra(
                                    "pharmacy_location",
                                    pharmacy.getLocation()
                            );

                            resultIntent.putExtra(
                                    "pharmacy_distance",
                                    pharmacy.getDistance()
                            );

                            resultIntent.putExtra(
                                    "medication_name",
                                    medication.getName()
                            );

                            resultIntent.putExtra(
                                    "medication_price",
                                    medication.getPrice()
                            );

                            resultIntent.putExtra(
                                    "medication_availability",
                                    medication.getAvailability()
                            );

                            setResult(
                                    RESULT_OK,
                                    resultIntent
                            );

                            finish();
                        }
                );


        // -----------------------------------------
        // SET UP RECYCLER VIEW
        // -----------------------------------------

        rvPharmacies.setLayoutManager(
                new LinearLayoutManager(this)
        );

        rvPharmacies.setAdapter(
                pharmacyAdapter
        );


        // -----------------------------------------
        // CHECK IF MEDICATION WAS PASSED
        // FROM ANOTHER SCREEN
        // -----------------------------------------

        String passedMedicationName =
                getIntent().getStringExtra(
                        "medicationName"
                );


        if (passedMedicationName != null
                && !passedMedicationName.trim().isEmpty()) {

            etSearchMedication.setText(
                    passedMedicationName
            );

            pharmacyAdapter.filterByMedication(
                    passedMedicationName
            );

            tvShowingLabel.setText(
                    "Showing prices for: "
                            + passedMedicationName
            );

        } else {

            tvShowingLabel.setText(
                    DEFAULT_LABEL
            );
        }


        // -----------------------------------------
        // SEARCH MEDICATION
        // -----------------------------------------

        etSearchMedication.addTextChangedListener(
                new TextWatcher() {

                    @Override
                    public void beforeTextChanged(
                            CharSequence s,
                            int start,
                            int count,
                            int after) {
                    }


                    @Override
                    public void onTextChanged(
                            CharSequence s,
                            int start,
                            int before,
                            int count) {

                        String searchText =
                                s.toString().trim();


                        // Filter pharmacies
                        pharmacyAdapter.filterByMedication(
                                searchText
                        );


                        // Update label
                        if (searchText.isEmpty()) {

                            tvShowingLabel.setText(
                                    DEFAULT_LABEL
                            );

                        } else {

                            tvShowingLabel.setText(
                                    "Showing prices for: "
                                            + searchText
                            );
                        }
                    }


                    @Override
                    public void afterTextChanged(
                            Editable s) {
                    }
                }
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

        navMed.setOnClickListener(v -> {
            startActivity(new Intent(this, MedicationsActivity.class));
        });

        navEmergency.setOnClickListener(v -> {
            startActivity(new Intent(this, EmergencyActivity.class));
        });

        navSetting.setBackgroundResource(R.drawable.nav_icon_glow);

    }


    // =================================================
    // SORT PHARMACIES FROM CLOSEST TO FURTHEST
    // =================================================

    private void sortByClosest(
            List<Pharmacy> pharmacies) {

        Collections.sort(
                pharmacies,
                new Comparator<Pharmacy>() {

                    @Override
                    public int compare(
                            Pharmacy p1,
                            Pharmacy p2) {

                        double distance1 =
                                getDistanceInKm(
                                        p1.getDistance()
                                );

                        double distance2 =
                                getDistanceInKm(
                                        p2.getDistance()
                                );

                        return Double.compare(
                                distance1,
                                distance2
                        );
                    }
                }
        );
    }


    // =================================================
    // CONVERT DISTANCE TO KM
    // =================================================

    private double getDistanceInKm(
            String distance) {

        if (distance == null
                || distance.trim().isEmpty()) {

            return Double.MAX_VALUE;
        }


        try {

            String value =
                    distance
                            .trim()
                            .toLowerCase();


            // -----------------------------------------
            // METRES
            // Example: 800m
            // -----------------------------------------

            if (value.endsWith("m")) {

                value =
                        value.replace("m", "")
                                .trim();

                double metres =
                        Double.parseDouble(value);

                return metres / 1000.0;
            }


            // -----------------------------------------
            // KILOMETRES
            // Example: 1.2km
            // Example: 3 km
            // -----------------------------------------

            if (value.endsWith("km")) {

                value =
                        value.replace("km", "")
                                .trim();

                return Double.parseDouble(value);
            }


            // -----------------------------------------
            // IF THERE IS NO UNIT
            // ASSUME KM
            // -----------------------------------------

            return Double.parseDouble(value);

        } catch (Exception e) {

            return Double.MAX_VALUE;
        }
    }
}