package com.proteahealth.Patient;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.proteahealth.ProfileActivity;
import com.proteahealth.R;
import com.proteahealth.adapter.PharmacyAdapter;
import com.proteahealth.api.MedicationPriceItem;
import com.proteahealth.api.MedicationPriceResponse;
import com.proteahealth.api.RetrofitClient;
import com.proteahealth.model.Medication;
import com.proteahealth.model.Pharmacy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MedicationPriceComparisonActivity
        extends AppCompatActivity {

    private PharmacyAdapter pharmacyAdapter;

    private final List<Pharmacy> allPharmacies =
            new ArrayList<>();

    private RecyclerView rvPharmacies;

    private EditText etSearchMedication;

    private TextView tvShowingLabel;


    // =========================================================
    // ON CREATE
    // =========================================================

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(
                R.layout.activity_medication_price_comparison
        );


        initialiseViews();

        setupRecyclerView();

        setupSearch();

        setupBottomNavigation();


        // Check whether MedicationOrderActivity
        // passed a medication name.

        String passedMedicationName =
                getIntent().getStringExtra(
                        "medicationName"
                );


        if (passedMedicationName != null
                && !passedMedicationName.trim().isEmpty()) {

            etSearchMedication.setText(
                    passedMedicationName
            );

            tvShowingLabel.setText(
                    "Showing prices for: "
                            + passedMedicationName
            );

            loadMedicationPrices(
                    passedMedicationName
            );

        } else {

            tvShowingLabel.setText(
                    "Search for a medication"
            );
        }
    }


    // =========================================================
    // INITIALISE VIEWS
    // =========================================================

    private void initialiseViews() {

        rvPharmacies =
                findViewById(
                        R.id.rvPharmacies
                );

        etSearchMedication =
                findViewById(
                        R.id.etSearchMedication
                );

        tvShowingLabel =
                findViewById(
                        R.id.tvShowingLabel
                );
    }


    // =========================================================
    // RECYCLER VIEW
    // =========================================================

    private void setupRecyclerView() {

        pharmacyAdapter =
                new PharmacyAdapter(
                        allPharmacies,
                        (pharmacy, medication) -> {

                            Intent resultIntent =
                                    new Intent();


                            // Pharmacy information

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


                            // Medication information

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


                            // Delivery information

                            resultIntent.putExtra(
                                    "offers_delivery",
                                    pharmacy.isOffersDelivery()
                            );

                            resultIntent.putExtra(
                                    "delivery_fee",
                                    pharmacy.getDeliveryFee()
                            );


                            setResult(
                                    RESULT_OK,
                                    resultIntent
                            );

                            finish();
                        }
                );


        rvPharmacies.setLayoutManager(
                new LinearLayoutManager(this)
        );

        rvPharmacies.setAdapter(
                pharmacyAdapter
        );
    }


    // =========================================================
    // LOAD REAL MEDICATION PRICES
    // =========================================================

    private void loadMedicationPrices(
            String medicationName) {

        String search =
                medicationName.trim();


        if (search.isEmpty()) {

            allPharmacies.clear();

            refreshAdapter();

            return;
        }


        tvShowingLabel.setText(
                "Showing prices for: "
                        + search
        );


        RetrofitClient.INSTANCE
                .getApiService()
                .getMedicationPrices(search)
                .enqueue(
                        new Callback<MedicationPriceResponse>() {

                            @Override
                            public void onResponse(
                                    Call<MedicationPriceResponse> call,
                                    Response<MedicationPriceResponse> response) {

                                if (!response.isSuccessful()
                                        || response.body() == null) {

                                    showApiError(
                                            "Unable to retrieve medication prices."
                                    );

                                    return;
                                }


                                MedicationPriceResponse apiResponse =
                                        response.body();


                                if (!apiResponse.getSuccess()) {

                                    showApiError(
                                            apiResponse.getMessage()
                                    );

                                    return;
                                }


                                convertApiResults(
                                        apiResponse.getPharmacies()
                                );
                            }


                            @Override
                            public void onFailure(
                                    Call<MedicationPriceResponse> call,
                                    Throwable t) {

                                showApiError(
                                        "Unable to connect to the server."
                                );
                            }
                        }
                );
    }


    // =========================================================
    // CONVERT API DATA INTO EXISTING PHARMACY MODEL
    // =========================================================

    private void convertApiResults(
            List<MedicationPriceItem> results) {

        allPharmacies.clear();


        if (results == null
                || results.isEmpty()) {

            refreshAdapter();

            Toast.makeText(
                    this,
                    "No pharmacies found for this medication.",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }


        for (MedicationPriceItem item : results) {

            // ---------------------------------------------
            // PRICE
            // ---------------------------------------------

            double medicationPrice =
                    parseDouble(
                            item.getPrice()
                    );


            // ---------------------------------------------
            // DELIVERY
            // ---------------------------------------------

            boolean offersDelivery =
                    item.getOffers_delivery() == 1;


            double deliveryFee =
                    parseDouble(
                            item.getDelivery_fee()
                    );


            // ---------------------------------------------
            // MEDICATION MODEL
            // ---------------------------------------------

            Medication medication =
                    new Medication(
                            item.getMedication_name(),
                            medicationPrice,
                            item.getAvailability()
                    );


            List<Medication> medications =
                    new ArrayList<>();

            medications.add(
                    medication
            );


            // ---------------------------------------------
            // OPENING HOURS
            // ---------------------------------------------

            String openHours =
                    formatOpenHours(
                            item.getOpen_hour(),
                            item.getClose_hours()
                    );


            /*
             * Distance is not currently stored in MySQL.
             *
             * We therefore do NOT invent a fake distance.
             * Later we can add latitude/longitude if you
             * want real distance calculations.
             */

            String distance =
                    "Distance unavailable";


            // ---------------------------------------------
            // PHARMACY MODEL
            // ---------------------------------------------

            Pharmacy pharmacy =
                    new Pharmacy(

                            String.valueOf(
                                    item.getPharmacy_id()
                            ),

                            item.getPharmacy_name(),

                            item.getPharmacy_location(),

                            distance,

                            openHours,

                            offersDelivery,

                            deliveryFee,

                            medications
                    );


            allPharmacies.add(
                    pharmacy
            );
        }


        // Cheapest pharmacy first makes more sense
        // now that this is a price comparison screen.

        sortByPrice(
                allPharmacies
        );


        refreshAdapter();
    }


    // =========================================================
    // REFRESH ADAPTER
    // =========================================================

    private void refreshAdapter() {

        /*
         * PharmacyAdapter keeps its own copy of the
         * pharmacy list, so create a fresh adapter
         * whenever the API results change.
         */

        pharmacyAdapter =
                new PharmacyAdapter(
                        allPharmacies,
                        (pharmacy, medication) -> {

                            Intent resultIntent =
                                    new Intent();


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


                            resultIntent.putExtra(
                                    "offers_delivery",
                                    pharmacy.isOffersDelivery()
                            );

                            resultIntent.putExtra(
                                    "delivery_fee",
                                    pharmacy.getDeliveryFee()
                            );


                            setResult(
                                    RESULT_OK,
                                    resultIntent
                            );

                            finish();
                        }
                );


        rvPharmacies.setAdapter(
                pharmacyAdapter
        );
    }


    // =========================================================
    // SEARCH
    // =========================================================

    private void setupSearch() {

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


                        if (searchText.isEmpty()) {

                            tvShowingLabel.setText(
                                    "Search for a medication"
                            );

                            allPharmacies.clear();

                            refreshAdapter();

                            return;
                        }


                        tvShowingLabel.setText(
                                "Showing prices for: "
                                        + searchText
                        );
                    }


                    @Override
                    public void afterTextChanged(
                            Editable s) {
                    }
                }
        );


        /*
         * Execute the API search when the user
         * presses the keyboard search/enter button.
         */

        etSearchMedication.setOnEditorActionListener(
                (v, actionId, event) -> {

                    String medication =
                            etSearchMedication
                                    .getText()
                                    .toString()
                                    .trim();


                    if (!medication.isEmpty()) {

                        loadMedicationPrices(
                                medication
                        );
                    }


                    return true;
                }
        );
    }


    // =========================================================
    // SORT CHEAPEST FIRST
    // =========================================================

    private void sortByPrice(
            List<Pharmacy> pharmacies) {

        Collections.sort(
                pharmacies,
                new Comparator<Pharmacy>() {

                    @Override
                    public int compare(
                            Pharmacy p1,
                            Pharmacy p2) {

                        double price1 =
                                getFirstMedicationPrice(
                                        p1
                                );

                        double price2 =
                                getFirstMedicationPrice(
                                        p2
                                );

                        return Double.compare(
                                price1,
                                price2
                        );
                    }
                }
        );
    }


    // =========================================================
    // GET FIRST MEDICATION PRICE
    // =========================================================

    private double getFirstMedicationPrice(
            Pharmacy pharmacy) {

        if (pharmacy.getMedications() == null
                || pharmacy.getMedications().isEmpty()) {

            return Double.MAX_VALUE;
        }

        return pharmacy
                .getMedications()
                .get(0)
                .getPrice();
    }


    // =========================================================
    // PARSE DOUBLE SAFELY
    // =========================================================

    private double parseDouble(
            String value) {

        if (value == null
                || value.trim().isEmpty()) {

            return 0.00;
        }

        try {

            return Double.parseDouble(
                    value
            );

        } catch (NumberFormatException e) {

            return 0.00;
        }
    }


    // =========================================================
    // FORMAT OPENING HOURS
    // =========================================================

    private String formatOpenHours(
            String open,
            String close) {

        if (open == null
                || close == null) {

            return "Hours unavailable";
        }

        return open
                + " - "
                + close;
    }


    // =========================================================
    // API ERROR
    // =========================================================

    private void showApiError(
            String message) {

        allPharmacies.clear();

        refreshAdapter();

        Toast.makeText(
                this,
                message,
                Toast.LENGTH_LONG
        ).show();
    }


    // =========================================================
    // BOTTOM NAVIGATION
    // =========================================================

    private void setupBottomNavigation() {

        ImageButton navProfile =
                findViewById(
                        R.id.navProfile
                );

        ImageButton navSetting =
                findViewById(
                        R.id.navSetting
                );

        ImageButton navHome =
                findViewById(
                        R.id.navhome
                );

        ImageButton navEmergency =
                findViewById(
                        R.id.navEmergency
                );

        ImageButton navMed =
                findViewById(
                        R.id.navMed
                );


        navProfile.setOnClickListener(
                v -> startActivity(
                        new Intent(
                                this,
                                ProfileActivity.class
                        )
                )
        );


        navHome.setOnClickListener(
                v -> startActivity(
                        new Intent(
                                this,
                                Patient_Home.class
                        )
                )
        );


        navMed.setOnClickListener(
                v -> startActivity(
                        new Intent(
                                this,
                                MedicationsActivity.class
                        )
                )
        );


        navEmergency.setOnClickListener(
                v -> startActivity(
                        new Intent(
                                this,
                                EmergencyActivity.class
                        )
                )
        );


        navSetting.setBackgroundResource(
                R.drawable.nav_icon_glow
        );
    }
}