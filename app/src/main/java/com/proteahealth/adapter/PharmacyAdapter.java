package com.proteahealth.adapter;

import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.proteahealth.R;
import com.proteahealth.model.Medication;
import com.proteahealth.model.Pharmacy;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class PharmacyAdapter
        extends RecyclerView.Adapter<PharmacyAdapter.PharmacyViewHolder> {

    // Original complete list
    private final List<Pharmacy> allPharmacies;

    // Pharmacies currently being displayed
    private final List<Pharmacy> displayedPharmacies;


    // ============================================================
    // CONSTRUCTOR
    // ============================================================

    public PharmacyAdapter(List<Pharmacy> pharmacies) {

        allPharmacies =
                new ArrayList<>(pharmacies);

        displayedPharmacies =
                new ArrayList<>(pharmacies);
    }


    // ============================================================
    // CREATE PHARMACY CARD
    // ============================================================

    @NonNull
    @Override
    public PharmacyViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType) {

        View view =
                LayoutInflater.from(parent.getContext())
                        .inflate(
                                R.layout.item_pharmacy,
                                parent,
                                false
                        );

        return new PharmacyViewHolder(view);
    }


    // ============================================================
    // DISPLAY PHARMACY INFORMATION
    // ============================================================

    @Override
    public void onBindViewHolder(
            @NonNull PharmacyViewHolder holder,
            int position) {

        Pharmacy pharmacy =
                displayedPharmacies.get(position);


        // --------------------------------------------------------
        // PHARMACY NAME
        // --------------------------------------------------------

        holder.tvPharmacyName.setText(
                pharmacy.getName()
        );


        // --------------------------------------------------------
        // LOCATION
        // --------------------------------------------------------

        holder.tvLocation.setText(
                pharmacy.getLocation()
        );


        // --------------------------------------------------------
        // DISTANCE
        // --------------------------------------------------------

        holder.tvDistance.setText(
                pharmacy.getDistance()
        );


        // --------------------------------------------------------
        // OPENING HOURS
        // --------------------------------------------------------

        holder.tvOpenHours.setText(
                "Open " + pharmacy.getOpenHours()
        );


        // --------------------------------------------------------
        // MEDICATION
        // --------------------------------------------------------

        if (pharmacy.getMedications() != null
                && !pharmacy.getMedications().isEmpty()) {

            Medication medication =
                    pharmacy.getMedications().get(0);


            holder.tvMedicationName.setText(
                    medication.getName()
            );


            holder.tvPrice.setText(
                    String.format(
                            Locale.getDefault(),
                            "R%.2f",
                            medication.getPrice()
                    )
            );

        } else {

            holder.tvMedicationName.setText(
                    "Medication unavailable"
            );

            holder.tvPrice.setText(
                    "Price unavailable"
            );
        }


        // ========================================================
        // VIEW PHARMACY BUTTON
        // ========================================================

        holder.btnViewPharmacy.setOnClickListener(
                v -> {

                    String pharmacyName =
                            pharmacy.getName();

                    String pharmacyAddress =
                            pharmacy.getLocation();


                    // Combine pharmacy name and address
                    String searchQuery =
                            pharmacyName
                                    + ", "
                                    + pharmacyAddress;


                    try {

                        // Encode the search text
                        String encodedQuery =
                                URLEncoder.encode(
                                        searchQuery,
                                        StandardCharsets.UTF_8.toString()
                                );


                        // Google Maps search URL
                        String googleMapsUrl =
                                "https://www.google.com/maps/search/?api=1&query="
                                        + encodedQuery;


                        // Open Google Maps
                        Intent intent =
                                new Intent(
                                        Intent.ACTION_VIEW,
                                        Uri.parse(googleMapsUrl)
                                );


                        v.getContext().startActivity(intent);


                    } catch (Exception e) {

                        Toast.makeText(
                                v.getContext(),
                                "Unable to open Google Maps",
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                }
        );
    }


    // ============================================================
    // NUMBER OF PHARMACIES
    // ============================================================

    @Override
    public int getItemCount() {

        return displayedPharmacies.size();
    }


    // ============================================================
    // SEARCH / FILTER
    // ============================================================

    public void filterByMedication(
            String searchText) {

        displayedPharmacies.clear();


        // --------------------------------------------------------
        // EMPTY SEARCH
        // --------------------------------------------------------

        if (searchText == null
                || searchText.trim().isEmpty()) {

            displayedPharmacies.addAll(
                    allPharmacies
            );

        } else {

            String search =
                    searchText
                            .trim()
                            .toLowerCase(
                                    Locale.getDefault()
                            );


            // ----------------------------------------------------
            // SEARCH THROUGH PHARMACIES
            // ----------------------------------------------------

            for (Pharmacy pharmacy :
                    allPharmacies) {

                if (pharmacy.getMedications()
                        == null) {

                    continue;
                }


                // ------------------------------------------------
                // SEARCH MEDICATIONS
                // ------------------------------------------------

                for (Medication medication :
                        pharmacy.getMedications()) {

                    if (medication.getName()
                            .toLowerCase(
                                    Locale.getDefault()
                            )
                            .contains(search)) {

                        displayedPharmacies.add(
                                pharmacy
                        );

                        // Prevent duplicate pharmacies
                        break;
                    }
                }
            }
        }


        // Refresh RecyclerView
        notifyDataSetChanged();
    }


    // ============================================================
    // VIEW HOLDER
    // ============================================================

    public static class PharmacyViewHolder
            extends RecyclerView.ViewHolder {

        TextView tvPharmacyName;
        TextView tvLocation;
        TextView tvDistance;
        TextView tvMedicationName;
        TextView tvPrice;
        TextView tvOpenHours;

        Button btnViewPharmacy;


        public PharmacyViewHolder(
                @NonNull View itemView) {

            super(itemView);


            // ----------------------------------------------------
            // CONNECT TEXT VIEWS
            // ----------------------------------------------------

            tvPharmacyName =
                    itemView.findViewById(
                            R.id.tvPharmacyName
                    );


            tvLocation =
                    itemView.findViewById(
                            R.id.tvLocation
                    );


            tvDistance =
                    itemView.findViewById(
                            R.id.tvDistance
                    );


            tvMedicationName =
                    itemView.findViewById(
                            R.id.tvMedicationName
                    );


            tvPrice =
                    itemView.findViewById(
                            R.id.tvPrice
                    );


            tvOpenHours =
                    itemView.findViewById(
                            R.id.tvOpenHours
                    );


            // ----------------------------------------------------
            // CONNECT VIEW PHARMACY BUTTON
            // ----------------------------------------------------

            btnViewPharmacy =
                    itemView.findViewById(
                            R.id.btnViewPharmacy
                    );
        }
    }
}