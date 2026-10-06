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


    // =========================================================
    // PHARMACY SELECTION LISTENER
    // =========================================================

    public interface OnPharmacySelectedListener {

        void onPharmacySelected(
                Pharmacy pharmacy,
                Medication medication
        );
    }


    private OnPharmacySelectedListener selectionListener;


    // Original complete list
    private final List<Pharmacy> allPharmacies;


    // Pharmacies currently displayed
    private final List<Pharmacy> displayedPharmacies;


    // =========================================================
    // CONSTRUCTOR WITHOUT LISTENER
    // =========================================================

    public PharmacyAdapter(
            List<Pharmacy> pharmacies) {

        allPharmacies =
                new ArrayList<>(
                        pharmacies
                );

        displayedPharmacies =
                new ArrayList<>(
                        pharmacies
                );
    }


    // =========================================================
    // CONSTRUCTOR WITH LISTENER
    // =========================================================

    public PharmacyAdapter(
            List<Pharmacy> pharmacies,
            OnPharmacySelectedListener selectionListener) {

        allPharmacies =
                new ArrayList<>(
                        pharmacies
                );

        displayedPharmacies =
                new ArrayList<>(
                        pharmacies
                );

        this.selectionListener =
                selectionListener;
    }


    // =========================================================
    // CREATE PHARMACY CARD
    // =========================================================

    @NonNull
    @Override
    public PharmacyViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType) {

        View view =
                LayoutInflater
                        .from(parent.getContext())
                        .inflate(
                                R.layout.item_pharmacy,
                                parent,
                                false
                        );

        return new PharmacyViewHolder(
                view
        );
    }


    // =========================================================
    // DISPLAY PHARMACY INFORMATION
    // =========================================================

    @Override
    public void onBindViewHolder(
            @NonNull PharmacyViewHolder holder,
            int position) {

        Pharmacy pharmacy =
                displayedPharmacies.get(
                        position
                );


        // =====================================================
        // PHARMACY NAME
        // =====================================================

        holder.tvPharmacyName.setText(
                pharmacy.getName()
        );


        // =====================================================
        // LOCATION
        // =====================================================

        String location =
                pharmacy.getLocation();

        if (location == null
                || location.trim().isEmpty()) {

            holder.tvLocation.setText(
                    "Location unavailable"
            );

        } else {

            holder.tvLocation.setText(
                    location
            );
        }


        // =====================================================
        // DISTANCE
        // =====================================================

        String distance =
                pharmacy.getDistance();

        if (distance == null
                || distance.trim().isEmpty()) {

            holder.tvDistance.setText(
                    "Distance unavailable"
            );

        } else {

            holder.tvDistance.setText(
                    distance
            );
        }

        if (pharmacy.isOffersDelivery()) {

            holder.tvDelivery.setText(
                    String.format(
                            java.util.Locale.getDefault(),
                            "Delivery available — R%.2f",
                            pharmacy.getDeliveryFee()
                    )
            );

        } else {

            holder.tvDelivery.setText(
                    "Collection only"
            );
        }


        // =====================================================
        // OPENING HOURS
        // =====================================================

        String openHours =
                pharmacy.getOpenHours();

        if (openHours == null
                || openHours.trim().isEmpty()) {

            holder.tvOpenHours.setText(
                    "Hours unavailable"
            );

        } else {

            holder.tvOpenHours.setText(
                    "Open " + openHours
            );
        }


        // =====================================================
        // MEDICATION INFORMATION
        // =====================================================

        if (pharmacy.getMedications() != null
                && !pharmacy.getMedications().isEmpty()) {

            Medication medication =
                    pharmacy
                            .getMedications()
                            .get(0);


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


        // =====================================================
        // SELECT PHARMACY
        // =====================================================

        holder.itemView.setOnClickListener(
                v -> {

                    if (selectionListener == null) {
                        return;
                    }


                    if (pharmacy.getMedications() == null
                            || pharmacy
                            .getMedications()
                            .isEmpty()) {

                        Toast.makeText(
                                v.getContext(),
                                "Medication unavailable at this pharmacy",
                                Toast.LENGTH_SHORT
                        ).show();

                        return;
                    }


                    Medication medication =
                            pharmacy
                                    .getMedications()
                                    .get(0);


                    selectionListener
                            .onPharmacySelected(
                                    pharmacy,
                                    medication
                            );
                }
        );


        // =====================================================
        // VIEW PHARMACY IN GOOGLE MAPS
        // =====================================================

        holder.btnViewPharmacy.setOnClickListener(
                v -> {

                    String pharmacyName =
                            pharmacy.getName();

                    String pharmacyAddress =
                            pharmacy.getLocation();


                    if (pharmacyName == null) {
                        pharmacyName = "";
                    }

                    if (pharmacyAddress == null) {
                        pharmacyAddress = "";
                    }


                    String searchQuery =
                            pharmacyName
                                    + ", "
                                    + pharmacyAddress;


                    try {

                        String encodedQuery =
                                URLEncoder.encode(
                                        searchQuery,
                                        StandardCharsets.UTF_8
                                                .toString()
                                );


                        String googleMapsUrl =
                                "https://www.google.com/maps/search/?api=1&query="
                                        + encodedQuery;


                        Intent intent =
                                new Intent(
                                        Intent.ACTION_VIEW,
                                        Uri.parse(
                                                googleMapsUrl
                                        )
                                );


                        v.getContext()
                                .startActivity(
                                        intent
                                );


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


    // =========================================================
    // NUMBER OF PHARMACIES
    // =========================================================

    @Override
    public int getItemCount() {

        return displayedPharmacies.size();
    }


    // =========================================================
    // FILTER BY MEDICATION
    // =========================================================

    public void filterByMedication(
            String searchText) {

        displayedPharmacies.clear();


        // =====================================================
        // EMPTY SEARCH
        // =====================================================

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


            // =================================================
            // SEARCH EACH PHARMACY
            // =================================================

            for (Pharmacy pharmacy :
                    allPharmacies) {


                if (pharmacy.getMedications()
                        == null) {

                    continue;
                }


                // =============================================
                // SEARCH MEDICATIONS
                // =============================================

                for (Medication medication :
                        pharmacy.getMedications()) {


                    if (medication.getName() == null) {
                        continue;
                    }


                    if (medication
                            .getName()
                            .toLowerCase(
                                    Locale.getDefault()
                            )
                            .contains(
                                    search
                            )) {

                        displayedPharmacies.add(
                                pharmacy
                        );

                        // Prevent duplicate pharmacy cards
                        break;
                    }
                }
            }
        }


        notifyDataSetChanged();
    }


    // =========================================================
    // VIEW HOLDER
    // =========================================================

    public static class PharmacyViewHolder
            extends RecyclerView.ViewHolder {


        TextView tvPharmacyName;
        TextView tvLocation;
        TextView tvDistance;
        TextView tvMedicationName;
        TextView tvPrice;
        TextView tvOpenHours;

        Button btnViewPharmacy;

        TextView tvDelivery;


        public PharmacyViewHolder(
                @NonNull View itemView) {

            super(
                    itemView
            );


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


            btnViewPharmacy =
                    itemView.findViewById(
                            R.id.btnViewPharmacy
                    );

            tvDelivery =
                    itemView.findViewById(
                            R.id.tvDelivery
                    );
        }
    }
}
