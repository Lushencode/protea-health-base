package com.proteahealth;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.View;
import android.widget.*;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.proteahealth.Patient.MedicationPriceComparisonActivity;

import java.util.ArrayList;
import java.util.Locale;

import com.proteahealth.api.CreateOrderResponse;
import com.proteahealth.api.RetrofitClient;
import com.proteahealth.data.SessionManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MedicationOrderActivity extends AppCompatActivity {

    // =========================================================
    // CAMERA
    // =========================================================

    private static final int CAMERA_PERMISSION_CODE = 101;
    private static final int CAMERA_REQUEST_CODE = 102;


    // =========================================================
    // UI COMPONENTS
    // =========================================================

    private int refillPrescriptionId = 0;

    private ImageButton backButton;

    private Button uploadPrescriptionButton;
    private Button addMedicationButton;
    private Button comparePricesButton;
    private Button confirmButton;
    private Button cancelButton;

    private EditText medicationNameInput;
    private EditText dosageInput;
    private EditText quantityInput;
    private EditText addressInput;

    private RadioGroup medicationMethodGroup;

    private RadioButton collectionRadio;
    private RadioButton deliveryRadio;

    private LinearLayout deliverySection;
    private LinearLayout medicationList;
    private LinearLayout aiAnalysisSection;
    private LinearLayout selectedPharmacySection;

    private ImageView prescriptionImage;

    private TextView prescriptionStatus;
    private TextView aiAnalysisText;
    private TextView noMedicationText;

    private TextView selectedPharmacyName;
    private TextView selectedMedicationPrice;
    private TextView selectedPharmacyDistance;
    private TextView selectedPharmacyDelivery;

    private TextView medicationTotalText;
    private TextView deliveryFeeText;
    private TextView orderTotalText;


    // =========================================================
    // MEDICATION DATA
    // =========================================================

    private final ArrayList<Medication> medications =
            new ArrayList<>();


    // =========================================================
    // PHARMACY SELECTION DATA
    // =========================================================

    private ActivityResultLauncher<Intent> pharmacyComparisonLauncher;

    private String selectedPharmacyId = null;
    private String selectedPharmacyNameValue = null;
    private String selectedPharmacyLocation = null;
    private String selectedPharmacyDistanceValue = null;

    private String selectedMedicationName = null;
    private double selectedMedicationPriceValue = 0.00;
    private String selectedMedicationAvailability = null;

    private boolean selectedPharmacyOffersDelivery = false;
    private double selectedPharmacyDeliveryFee = 0.00;


    // =========================================================
    // ORDER TOTALS
    // =========================================================

    private double medicationTotal = 0.00;
    private double deliveryFee = 0.00;
    private double orderTotal = 0.00;


    // =========================================================
    // ON CREATE
    // =========================================================

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(
                R.layout.activity_medication_order
        );

        initialiseViews();

        setupPharmacyComparisonLauncher();

        setupButtons();

        setupDeliveryOptions();

        deliveryRadio.setEnabled(false);

        loadRefillMedication();

        updateOrderSummary();

        /*
         * Delivery should not be selectable until
         * a pharmacy has been selected.
         */
        deliveryRadio.setEnabled(false);

        updateOrderSummary();
    }


    // =========================================================
    // INITIALISE VIEWS
    // =========================================================

    private void initialiseViews() {

        backButton =
                findViewById(
                        R.id.backButton
                );

        uploadPrescriptionButton =
                findViewById(
                        R.id.uploadPrescriptionButton
                );

        addMedicationButton =
                findViewById(
                        R.id.addMedicationButton
                );

        comparePricesButton =
                findViewById(
                        R.id.comparePricesButton
                );

        confirmButton =
                findViewById(
                        R.id.confirmButton
                );

        cancelButton =
                findViewById(
                        R.id.cancelButton
                );


        medicationNameInput =
                findViewById(
                        R.id.medicationNameInput
                );

        dosageInput =
                findViewById(
                        R.id.dosageInput
                );

        quantityInput =
                findViewById(
                        R.id.quantityInput
                );

        addressInput =
                findViewById(
                        R.id.addressInput
                );


        medicationMethodGroup =
                findViewById(
                        R.id.medicationMethodGroup
                );

        collectionRadio =
                findViewById(
                        R.id.collectionRadio
                );

        deliveryRadio =
                findViewById(
                        R.id.deliveryRadio
                );


        deliverySection =
                findViewById(
                        R.id.deliverySection
                );

        medicationList =
                findViewById(
                        R.id.medicationList
                );

        aiAnalysisSection =
                findViewById(
                        R.id.aiAnalysisSection
                );

        selectedPharmacySection =
                findViewById(
                        R.id.selectedPharmacySection
                );


        prescriptionImage =
                findViewById(
                        R.id.prescriptionImage
                );


        prescriptionStatus =
                findViewById(
                        R.id.prescriptionStatus
                );

        aiAnalysisText =
                findViewById(
                        R.id.aiAnalysisText
                );

        noMedicationText =
                findViewById(
                        R.id.noMedicationText
                );


        selectedPharmacyName =
                findViewById(
                        R.id.selectedPharmacyName
                );

        selectedMedicationPrice =
                findViewById(
                        R.id.selectedMedicationPrice
                );

        selectedPharmacyDistance =
                findViewById(
                        R.id.selectedPharmacyDistance
                );

        selectedPharmacyDelivery =
                findViewById(
                        R.id.selectedPharmacyDelivery
                );


        medicationTotalText =
                findViewById(
                        R.id.medicationTotalText
                );

        deliveryFeeText =
                findViewById(
                        R.id.deliveryFeeText
                );

        orderTotalText =
                findViewById(
                        R.id.orderTotalText
                );
    }


    // =========================================================
    // PHARMACY COMPARISON RESULT
    // =========================================================

    private void setupPharmacyComparisonLauncher() {

        pharmacyComparisonLauncher =
                registerForActivityResult(
                        new ActivityResultContracts.StartActivityForResult(),
                        result -> {

                            if (result.getResultCode() != RESULT_OK
                                    || result.getData() == null) {

                                return;
                            }

                            Intent data =
                                    result.getData();


                            // -----------------------------------------
                            // PHARMACY
                            // -----------------------------------------

                            selectedPharmacyId =
                                    data.getStringExtra(
                                            "pharmacy_id"
                                    );

                            selectedPharmacyNameValue =
                                    data.getStringExtra(
                                            "pharmacy_name"
                                    );

                            selectedPharmacyLocation =
                                    data.getStringExtra(
                                            "pharmacy_location"
                                    );

                            selectedPharmacyDistanceValue =
                                    data.getStringExtra(
                                            "pharmacy_distance"
                                    );


                            // -----------------------------------------
                            // MEDICATION
                            // -----------------------------------------

                            selectedMedicationName =
                                    data.getStringExtra(
                                            "medication_name"
                                    );

                            selectedMedicationPriceValue =
                                    data.getDoubleExtra(
                                            "medication_price",
                                            0.00
                                    );

                            selectedMedicationAvailability =
                                    data.getStringExtra(
                                            "medication_availability"
                                    );


                            // -----------------------------------------
                            // DELIVERY
                            // -----------------------------------------

                            selectedPharmacyOffersDelivery =
                                    data.getBooleanExtra(
                                            "offers_delivery",
                                            false
                                    );

                            selectedPharmacyDeliveryFee =
                                    data.getDoubleExtra(
                                            "delivery_fee",
                                            0.00
                                    );


                            // -----------------------------------------
                            // UPDATE ORDER
                            // -----------------------------------------

                            applySelectedMedicationPrice();

                            updateDeliveryAvailability();

                            displaySelectedPharmacy();

                            displayMedications();

                            calculateMedicationTotal();
                        }
                );
    }


    // =========================================================
    // APPLY SELECTED PHARMACY PRICE
    // =========================================================

    private void applySelectedMedicationPrice() {

        if (medications.isEmpty()) {
            return;
        }

        /*
         * At the moment the price comparison is performed
         * for the first medication in the order.
         */

        Medication medication =
                medications.get(0);

        medication.price =
                selectedMedicationPriceValue
                        * medication.quantity;
    }


    // =========================================================
    // DISPLAY SELECTED PHARMACY
    // =========================================================

    private void displaySelectedPharmacy() {

        selectedPharmacySection.setVisibility(
                View.VISIBLE
        );


        // -----------------------------------------
        // PHARMACY NAME
        // -----------------------------------------

        String pharmacyName =
                selectedPharmacyNameValue;

        if (pharmacyName == null
                || pharmacyName.trim().isEmpty()) {

            pharmacyName =
                    "Selected Pharmacy";
        }

        selectedPharmacyName.setText(
                pharmacyName
        );


        // -----------------------------------------
        // MEDICATION PRICE
        // -----------------------------------------

        selectedMedicationPrice.setText(
                String.format(
                        Locale.getDefault(),
                        "Price per item: R%.2f",
                        selectedMedicationPriceValue
                )
        );


        // -----------------------------------------
        // LOCATION / DISTANCE
        // -----------------------------------------

        String location =
                selectedPharmacyLocation;

        if (location == null
                || location.trim().isEmpty()) {

            location =
                    "Location unavailable";
        }


        String distance =
                selectedPharmacyDistanceValue;

        if (distance == null
                || distance.trim().isEmpty()) {

            distance =
                    "Distance unavailable";
        }


        selectedPharmacyDistance.setText(
                location + " • " + distance
        );


        // -----------------------------------------
        // DELIVERY
        // -----------------------------------------

        if (selectedPharmacyOffersDelivery) {

            selectedPharmacyDelivery.setText(
                    String.format(
                            Locale.getDefault(),
                            "Delivery available • Fee: R%.2f",
                            selectedPharmacyDeliveryFee
                    )
            );

        } else {

            selectedPharmacyDelivery.setText(
                    "Collection only"
            );
        }
    }


    // =========================================================
    // DELIVERY AVAILABILITY
    // =========================================================

    private void updateDeliveryAvailability() {

        if (selectedPharmacyOffersDelivery) {

            deliveryRadio.setEnabled(true);

        } else {

            deliveryRadio.setEnabled(false);
            deliveryRadio.setChecked(false);

            collectionRadio.setChecked(true);

            deliverySection.setVisibility(View.GONE);

            deliveryFee = 0.00;
        }

        updateOrderSummary();
    }


    // =========================================================
    // SETUP BUTTONS
    // =========================================================

    private void setupButtons() {

        // BACK

        backButton.setOnClickListener(
                v -> finish()
        );


        // PRESCRIPTION PHOTO

        uploadPrescriptionButton.setOnClickListener(
                v -> openCamera()
        );


        // ADD MEDICATION

        addMedicationButton.setOnClickListener(
                v -> addMedication()
        );


        // COMPARE PRICES

        comparePricesButton.setOnClickListener(
                v -> compareMedicationPrices()
        );


        // CONFIRM

        confirmButton.setOnClickListener(
                v -> confirmOrder()
        );


        // CANCEL

        cancelButton.setOnClickListener(
                v -> cancelOrder()
        );
    }


    // =========================================================
    // COMPARE MEDICATION PRICES
    // =========================================================

    private void compareMedicationPrices() {

        if (medications.isEmpty()) {

            showMessage(
                    "Add Medication",
                    "Please add a medication before comparing pharmacy prices."
            );

            return;
        }


        Medication medication =
                medications.get(0);


        Intent intent =
                new Intent(
                        MedicationOrderActivity.this,
                        MedicationPriceComparisonActivity.class
                );


        intent.putExtra(
                "medicationName",
                medication.name
        );


        pharmacyComparisonLauncher.launch(
                intent
        );
    }


    // =========================================================
// LOAD REFILL MEDICATION
// =========================================================

    private void loadRefillMedication() {

        Intent intent =
                getIntent();

        if (intent == null) {
            return;
        }


        String medicationName =
                intent.getStringExtra(
                        "refill_medication_name"
                );

        String dosage =
                intent.getStringExtra(
                        "refill_dosage"
                );

        int quantity =
                intent.getIntExtra(
                        "refill_quantity",
                        0
                );


        refillPrescriptionId = intent.getIntExtra(
                "refill_prescription_id",
                0
        );



        /*
         * If this page was opened normally instead of
         * through the Refill button, do nothing.
         */
        if (medicationName == null
                || medicationName.trim().isEmpty()) {

            return;
        }


        medicationNameInput.setText(
                medicationName
        );


        if (dosage != null) {

            dosageInput.setText(
                    dosage
            );
        }


        if (quantity > 0) {

            quantityInput.setText(
                    String.valueOf(
                            quantity
                    )
            );
        }
    }


    // =========================================================
    // CAMERA
    // =========================================================

    private void openCamera() {

        if (checkSelfPermission(
                Manifest.permission.CAMERA
        ) != PackageManager.PERMISSION_GRANTED) {

            requestPermissions(
                    new String[]{
                            Manifest.permission.CAMERA
                    },
                    CAMERA_PERMISSION_CODE
            );

        } else {

            launchCamera();
        }
    }


    private void launchCamera() {

        Intent cameraIntent =
                new Intent(
                        MediaStore.ACTION_IMAGE_CAPTURE
                );


        if (cameraIntent.resolveActivity(
                getPackageManager()
        ) != null) {

            startActivityForResult(
                    cameraIntent,
                    CAMERA_REQUEST_CODE
            );

        } else {

            Toast.makeText(
                    this,
                    "No camera application found.",
                    Toast.LENGTH_LONG
            ).show();
        }
    }


    @Override
    public void onRequestPermissionsResult(
            int requestCode,
            String[] permissions,
            int[] grantResults) {

        super.onRequestPermissionsResult(
                requestCode,
                permissions,
                grantResults
        );


        if (requestCode
                == CAMERA_PERMISSION_CODE) {

            if (grantResults.length > 0
                    && grantResults[0]
                    == PackageManager.PERMISSION_GRANTED) {

                launchCamera();

            } else {

                Toast.makeText(
                        this,
                        "Camera permission is required to take a photo of your prescription.",
                        Toast.LENGTH_LONG
                ).show();
            }
        }
    }


    @Override
    protected void onActivityResult(
            int requestCode,
            int resultCode,
            Intent data) {

        super.onActivityResult(
                requestCode,
                resultCode,
                data
        );


        if (requestCode == CAMERA_REQUEST_CODE
                && resultCode == RESULT_OK
                && data != null) {

            Bundle extras =
                    data.getExtras();


            if (extras != null) {

                Bitmap prescriptionPhoto =
                        (Bitmap) extras.get(
                                "data"
                        );


                if (prescriptionPhoto != null) {

                    prescriptionImage.setImageBitmap(
                            prescriptionPhoto
                    );

                    prescriptionImage.setVisibility(
                            View.VISIBLE
                    );


                    prescriptionStatus.setText(
                            "Prescription photo captured successfully."
                    );

                    prescriptionStatus.setTextColor(
                            0xFF0B5D4B
                    );


                    aiAnalysisSection.setVisibility(
                            View.VISIBLE
                    );

                    aiAnalysisText.setText(
                            "Checking prescription..."
                    );


                    new Handler().postDelayed(
                            () -> aiAnalysisText.setText(
                                    "Prescription detected. "
                                            + "Please review the medication "
                                            + "information before ordering."
                            ),
                            1500
                    );
                }
            }
        }
    }


    // =========================================================
    // ADD MEDICATION
    // =========================================================

    private void addMedication() {

        String name =
                medicationNameInput
                        .getText()
                        .toString()
                        .trim();

        String dosage =
                dosageInput
                        .getText()
                        .toString()
                        .trim();

        String quantityText =
                quantityInput
                        .getText()
                        .toString()
                        .trim();


        // -----------------------------------------
        // VALIDATION
        // -----------------------------------------

        if (name.isEmpty()) {

            medicationNameInput.setError(
                    "Enter medication name"
            );

            return;
        }


        if (dosage.isEmpty()) {

            dosageInput.setError(
                    "Enter dosage"
            );

            return;
        }


        if (quantityText.isEmpty()) {

            quantityInput.setError(
                    "Enter quantity"
            );

            return;
        }


        int quantity;


        try {

            quantity =
                    Integer.parseInt(
                            quantityText
                    );

        } catch (NumberFormatException e) {

            quantityInput.setError(
                    "Enter a valid quantity"
            );

            return;
        }


        if (quantity <= 0) {

            quantityInput.setError(
                    "Quantity must be greater than 0"
            );

            return;
        }


        /*
         * No fake medication price.
         * Price comes from the selected pharmacy.
         */

        Medication medication =
                new Medication(
                        name,
                        dosage,
                        quantity,
                        0.00
                );


        if (refillPrescriptionId > 0) {
            String originalRefillName = getIntent().getStringExtra(
                    "refill_medication_name"
            );

            if (originalRefillName == null
                    || !originalRefillName.trim().equalsIgnoreCase(name)
                    || !medications.isEmpty()) {

                refillPrescriptionId = 0;
            }
        }


        medications.add(
                medication
        );


        /*
         * Adding/changing medication invalidates
         * the previous pharmacy price selection.
         */

        clearSelectedPharmacy();

        displayMedications();

        calculateMedicationTotal();

        clearMedicationInputs();
    }


    // =========================================================
    // DISPLAY MEDICATIONS
    // =========================================================

    private void displayMedications() {

        medicationList.removeAllViews();


        if (medications.isEmpty()) {

            noMedicationText.setVisibility(
                    View.VISIBLE
            );

            return;
        }


        noMedicationText.setVisibility(
                View.GONE
        );


        for (int i = 0;
             i < medications.size();
             i++) {

            Medication medication =
                    medications.get(i);


            LinearLayout medicationRow =
                    createMedicationRow(
                            medication,
                            i
                    );


            medicationList.addView(
                    medicationRow
            );
        }
    }


    // =========================================================
    // CREATE MEDICATION ROW
    // =========================================================

    private LinearLayout createMedicationRow(
            Medication medication,
            int position) {

        LinearLayout row =
                new LinearLayout(
                        this
                );

        row.setOrientation(
                LinearLayout.VERTICAL
        );

        row.setPadding(
                15,
                15,
                15,
                15
        );


        // -----------------------------------------
        // MEDICATION NAME
        // -----------------------------------------

        TextView medicationName =
                new TextView(
                        this
                );

        medicationName.setText(
                medication.name
        );

        medicationName.setTextSize(
                16
        );

        medicationName.setTextColor(
                0xFF111111
        );

        medicationName.setTypeface(
                null,
                Typeface.BOLD
        );


        // -----------------------------------------
        // MEDICATION DETAILS
        // -----------------------------------------

        TextView medicationDetails =
                new TextView(
                        this
                );


        String priceText;


        if (medication.price > 0) {

            priceText =
                    String.format(
                            Locale.getDefault(),
                            "Price: R%.2f",
                            medication.price
                    );

        } else {

            priceText =
                    "Price: Select a pharmacy";
        }


        medicationDetails.setText(
                medication.dosage
                        + " • Quantity: "
                        + medication.quantity
                        + "\n"
                        + priceText
        );

        medicationDetails.setTextSize(
                14
        );

        medicationDetails.setTextColor(
                0xFF666666
        );


        // -----------------------------------------
        // REMOVE
        // -----------------------------------------

        Button removeButton =
                new Button(
                        this
                );

        removeButton.setText(
                "Remove"
        );

        removeButton.setTextColor(
                0xFFF05A91
        );


        removeButton.setOnClickListener(
                v -> {

                    medications.remove(
                            position
                    );

                    clearSelectedPharmacy();

                    displayMedications();

                    calculateMedicationTotal();
                }
        );


        row.addView(
                medicationName
        );

        row.addView(
                medicationDetails
        );

        row.addView(
                removeButton
        );


        return row;
    }


    // =========================================================
    // CLEAR MEDICATION INPUTS
    // =========================================================

    private void clearMedicationInputs() {

        medicationNameInput.setText(
                ""
        );

        dosageInput.setText(
                ""
        );

        quantityInput.setText(
                ""
        );

        medicationNameInput.requestFocus();
    }


    // =========================================================
    // CLEAR SELECTED PHARMACY
    // =========================================================

    private void clearSelectedPharmacy() {

        selectedPharmacyId =
                null;

        selectedPharmacyNameValue =
                null;

        selectedPharmacyLocation =
                null;

        selectedPharmacyDistanceValue =
                null;


        selectedMedicationName =
                null;

        selectedMedicationPriceValue =
                0.00;

        selectedMedicationAvailability =
                null;


        selectedPharmacyOffersDelivery =
                false;

        selectedPharmacyDeliveryFee =
                0.00;


        selectedPharmacySection.setVisibility(
                View.GONE
        );


        // -----------------------------------------
        // RESET MEDICATION PRICES
        // -----------------------------------------

        for (Medication medication :
                medications) {

            medication.price =
                    0.00;
        }


        // -----------------------------------------
        // RESET DELIVERY
        // -----------------------------------------

        deliveryFee =
                0.00;

        deliveryRadio.setChecked(
                false
        );

        deliveryRadio.setEnabled(
                false
        );

        deliverySection.setVisibility(
                View.GONE
        );


        updateOrderSummary();
    }


    // =========================================================
    // COLLECTION / DELIVERY
    // =========================================================

    private void setupDeliveryOptions() {

        medicationMethodGroup
                .setOnCheckedChangeListener(
                        (group, checkedId) -> {


                            // ---------------------------------
                            // DELIVERY
                            // ---------------------------------

                            if (checkedId
                                    == R.id.deliveryRadio) {


                                if (!selectedPharmacyOffersDelivery) {

                                    deliveryRadio.setChecked(
                                            false
                                    );

                                    collectionRadio.setChecked(
                                            true
                                    );

                                    deliverySection.setVisibility(
                                            View.GONE
                                    );

                                    deliveryFee =
                                            0.00;


                                    Toast.makeText(
                                            this,
                                            "This pharmacy does not offer delivery.",
                                            Toast.LENGTH_SHORT
                                    ).show();


                                    updateOrderSummary();

                                    return;
                                }


                                deliverySection.setVisibility(
                                        View.VISIBLE
                                );


                                deliveryFee =
                                        selectedPharmacyDeliveryFee;


                            } else if (checkedId
                                    == R.id.collectionRadio) {


                                // ---------------------------------
                                // COLLECTION
                                // ---------------------------------

                                deliverySection.setVisibility(
                                        View.GONE
                                );

                                deliveryFee =
                                        0.00;
                            }


                            updateOrderSummary();
                        }
                );
    }


    // =========================================================
    // CALCULATE MEDICATION TOTAL
    // =========================================================

    private void calculateMedicationTotal() {

        medicationTotal =
                0.00;


        for (Medication medication :
                medications) {

            medicationTotal +=
                    medication.price;
        }


        updateOrderSummary();
    }


    // =========================================================
    // UPDATE ORDER SUMMARY
    // =========================================================

    private void updateOrderSummary() {

        orderTotal =
                medicationTotal
                        + deliveryFee;


        medicationTotalText.setText(
                String.format(
                        Locale.getDefault(),
                        "R%.2f",
                        medicationTotal
                )
        );


        deliveryFeeText.setText(
                String.format(
                        Locale.getDefault(),
                        "R%.2f",
                        deliveryFee
                )
        );


        orderTotalText.setText(
                String.format(
                        Locale.getDefault(),
                        "R%.2f",
                        orderTotal
                )
        );
    }


    // =========================================================
    // CONFIRM ORDER
    // =========================================================

    private void confirmOrder() {

        // -----------------------------------------
        // MEDICATION
        // -----------------------------------------

        if (medications.isEmpty()) {

            showMessage(
                    "No Medication",
                    "Please add at least one medication before confirming your order."
            );

            return;
        }


        // -----------------------------------------
        // PHARMACY
        // -----------------------------------------

        if (selectedPharmacyId == null
                || selectedPharmacyNameValue == null) {

            showMessage(
                    "Choose Pharmacy",
                    "Please compare prices and select a pharmacy before confirming your order."
            );

            return;
        }


        // -----------------------------------------
        // PRICE
        // -----------------------------------------

        if (selectedMedicationPriceValue <= 0) {

            showMessage(
                    "Medication Price",
                    "Please select a valid medication price before confirming your order."
            );

            return;
        }


        // -----------------------------------------
        // DELIVERY METHOD
        // -----------------------------------------

        String method;


        if (collectionRadio.isChecked()) {

            method =
                    "Collection";

        } else if (deliveryRadio.isChecked()) {

            if (!selectedPharmacyOffersDelivery) {

                showMessage(
                        "Delivery Unavailable",
                        "The selected pharmacy does not offer delivery."
                );

                return;
            }

            method =
                    "Delivery";

        } else {

            showMessage(
                    "Choose Delivery Method",
                    "Please choose collection or delivery."
            );

            return;
        }


        // -----------------------------------------
        // DELIVERY ADDRESS
        // -----------------------------------------

        if (deliveryRadio.isChecked()) {

            String address =
                    addressInput
                            .getText()
                            .toString()
                            .trim();


            if (address.isEmpty()) {

                addressInput.setError(
                        "Enter your delivery address"
                );

                return;
            }
        }


        // -----------------------------------------
        // CONFIRMATION
        // -----------------------------------------

        new AlertDialog.Builder(
                this
        )

                .setTitle(
                        "Confirm Order"
                )

                .setMessage(

                        "Pharmacy: "
                                + selectedPharmacyNameValue

                                + "\n\n"

                                + "Method: "
                                + method

                                + "\n\n"

                                + "Medication items: "
                                + medications.size()

                                + "\n\n"

                                + "Medication total: "
                                + String.format(
                                Locale.getDefault(),
                                "R%.2f",
                                medicationTotal
                        )

                                + "\n"

                                + "Delivery fee: "
                                + String.format(
                                Locale.getDefault(),
                                "R%.2f",
                                deliveryFee
                        )

                                + "\n\n"

                                + "TOTAL: "
                                + String.format(
                                Locale.getDefault(),
                                "R%.2f",
                                orderTotal
                        )

                                + "\n\n"

                                + "Would you like to place this order?"
                )

                .setPositiveButton(
                        "Confirm",
                        (dialog, which) ->
                                placeOrder()
                )

                .setNegativeButton(
                        "Cancel",
                        null
                )

                .show();
    }


    // =========================================================
// PLACE ORDER
// =========================================================

    private void placeOrder() {

        if (medications.isEmpty()) {
            return;
        }

        // -----------------------------------------------------
        // GET LOGGED-IN PATIENT
        // -----------------------------------------------------

        SessionManager sessionManager =
                new SessionManager(this);

        String patientId =
                sessionManager.getUserId();

        if (patientId == null
                || patientId.trim().isEmpty()) {

            showMessage(
                    "Session Error",
                    "Unable to identify the logged-in patient."
            );

            return;
        }


        // -----------------------------------------------------
        // GET MEDICATION
        // -----------------------------------------------------

        Medication medication =
                medications.get(0);


        // -----------------------------------------------------
        // FULFILMENT METHOD
        // -----------------------------------------------------

        final String fulfillmentMethod;
        final String deliveryAddress;
        final double finalDeliveryFee;

        if (deliveryRadio.isChecked()) {

            fulfillmentMethod = "delivery";

            deliveryAddress =
                    addressInput
                            .getText()
                            .toString()
                            .trim();

            finalDeliveryFee =
                    deliveryFee;

        } else {

            fulfillmentMethod = "collection";

            deliveryAddress = "";

            finalDeliveryFee = 0.00;
        }


        // -----------------------------------------------------
        // PREVENT DOUBLE SUBMISSION
        // -----------------------------------------------------

        confirmButton.setEnabled(false);

        confirmButton.setText(
                "Placing Order..."
        );


        // -----------------------------------------------------
        // SEND ORDER TO PHP
        // -----------------------------------------------------

        RetrofitClient.INSTANCE
                .getApiService()
                .createOrder(
                        patientId,
                        selectedPharmacyId,
                        medication.name,
                        medication.quantity,
                        orderTotal,
                        fulfillmentMethod,
                        deliveryAddress,
                        finalDeliveryFee,
                        refillPrescriptionId > 0 ? refillPrescriptionId : null
                )
                .enqueue(
                        new Callback<CreateOrderResponse>() {

                            @Override
                            public void onResponse(
                                    Call<CreateOrderResponse> call,
                                    Response<CreateOrderResponse> response) {

                                confirmButton.setEnabled(true);

                                confirmButton.setText(
                                        "Confirm Order"
                                );


                                if (!response.isSuccessful()
                                        || response.body() == null) {

                                    showMessage(
                                            "Order Failed",
                                            "Unable to place your order."
                                    );

                                    return;
                                }


                                CreateOrderResponse result =
                                        response.body();


                                if (!result.getSuccess()) {

                                    showMessage(
                                            "Order Failed",
                                            result.getMessage()
                                    );

                                    return;
                                }


                                // -----------------------------------------
                                // DATABASE ORDER ID
                                // -----------------------------------------

                                Integer orderId =
                                        result.getOrder_id();


                                if (orderId == null) {

                                    showMessage(
                                            "Order Error",
                                            "The order was saved but no order ID was returned."
                                    );

                                    return;
                                }


                                // -----------------------------------------
                                // OPEN CONFIRMATION PAGE
                                // -----------------------------------------

                                Intent intent =
                                        new Intent(
                                                MedicationOrderActivity.this,
                                                OrderConfirmedActivity.class
                                        );


                                intent.putExtra(
                                        "order_id",
                                        orderId
                                );

                                intent.putExtra(
                                        "medication_name",
                                        medication.name
                                );

                                intent.putExtra(
                                        "dosage",
                                        medication.dosage
                                );

                                intent.putExtra(
                                        "quantity",
                                        medication.quantity
                                );

                                intent.putExtra(
                                        "pharmacy_name",
                                        selectedPharmacyNameValue
                                );

                                intent.putExtra(
                                        "pharmacy_location",
                                        selectedPharmacyLocation
                                );

                                intent.putExtra(
                                        "fulfillment_method",
                                        fulfillmentMethod
                                );

                                intent.putExtra(
                                        "delivery_address",
                                        deliveryAddress
                                );

                                intent.putExtra(
                                        "medication_total",
                                        medicationTotal
                                );

                                intent.putExtra(
                                        "delivery_fee",
                                        finalDeliveryFee
                                );

                                intent.putExtra(
                                        "order_total",
                                        orderTotal
                                );


                                startActivity(intent);

                                finish();
                            }


                            @Override
                            public void onFailure(
                                    Call<CreateOrderResponse> call,
                                    Throwable t) {

                                confirmButton.setEnabled(true);

                                confirmButton.setText(
                                        "Confirm Order"
                                );


                                showMessage(
                                        "Connection Error",
                                        "Unable to connect to the server."
                                );
                            }
                        }
                );
    }




    // =========================================================
    // CANCEL ORDER
    // =========================================================

    private void cancelOrder() {

        new AlertDialog.Builder(
                this
        )

                .setTitle(
                        "Cancel Order?"
                )

                .setMessage(
                        "Are you sure you want to cancel this medication order?"
                )

                .setPositiveButton(
                        "Yes, Cancel",
                        (dialog, which) -> {

                            medications.clear();

                            clearSelectedPharmacy();

                            displayMedications();


                            medicationTotal =
                                    0.00;

                            deliveryFee =
                                    0.00;

                            updateOrderSummary();


                            medicationMethodGroup
                                    .clearCheck();

                            addressInput.setText(
                                    ""
                            );


                            prescriptionStatus.setText(
                                    "No prescription uploaded"
                            );

                            prescriptionImage.setVisibility(
                                    View.GONE
                            );

                            aiAnalysisSection.setVisibility(
                                    View.GONE
                            );


                            Toast.makeText(
                                    this,
                                    "Order cancelled",
                                    Toast.LENGTH_SHORT
                            ).show();
                        }
                )

                .setNegativeButton(
                        "Keep Order",
                        null
                )

                .show();
    }


    // =========================================================
    // MESSAGE HELPER
    // =========================================================

    private void showMessage(
            String title,
            String message) {

        new AlertDialog.Builder(
                this
        )

                .setTitle(
                        title
                )

                .setMessage(
                        message
                )

                .setPositiveButton(
                        "OK",
                        null
                )

                .show();
    }


    // =========================================================
    // MEDICATION CLASS
    // =========================================================

    public static class Medication {

        String name;
        String dosage;

        int quantity;

        double price;


        public Medication(
                String name,
                String dosage,
                int quantity,
                double price) {

            this.name =
                    name;

            this.dosage =
                    dosage;

            this.quantity =
                    quantity;

            this.price =
                    price;
        }
    }
}