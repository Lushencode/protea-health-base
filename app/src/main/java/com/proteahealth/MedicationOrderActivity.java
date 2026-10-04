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

import java.util.ArrayList;
import java.util.Locale;

public class MedicationOrderActivity extends AppCompatActivity {

    // =========================================================
    // CAMERA
    // =========================================================

    private static final int CAMERA_PERMISSION_CODE = 101;
    private static final int CAMERA_REQUEST_CODE = 102;


    // =========================================================
    // UI COMPONENTS
    // =========================================================

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

    private double selectedMedicationPriceValue = 0.00;

    private String selectedMedicationAvailability = null;


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

        setContentView(R.layout.activity_medication_order);

        initialiseViews();

        setupPharmacyComparisonLauncher();

        setupButtons();

        setupDeliveryOptions();

        updateOrderSummary();
    }


    // =========================================================
    // INITIALISE VIEWS
    // =========================================================

    private void initialiseViews() {

        backButton =
                findViewById(R.id.backButton);

        uploadPrescriptionButton =
                findViewById(R.id.uploadPrescriptionButton);

        addMedicationButton =
                findViewById(R.id.addMedicationButton);

        comparePricesButton =
                findViewById(R.id.comparePricesButton);

        confirmButton =
                findViewById(R.id.confirmButton);

        cancelButton =
                findViewById(R.id.cancelButton);


        medicationNameInput =
                findViewById(R.id.medicationNameInput);

        dosageInput =
                findViewById(R.id.dosageInput);

        quantityInput =
                findViewById(R.id.quantityInput);

        addressInput =
                findViewById(R.id.addressInput);


        medicationMethodGroup =
                findViewById(R.id.medicationMethodGroup);

        collectionRadio =
                findViewById(R.id.collectionRadio);

        deliveryRadio =
                findViewById(R.id.deliveryRadio);


        deliverySection =
                findViewById(R.id.deliverySection);

        medicationList =
                findViewById(R.id.medicationList);

        aiAnalysisSection =
                findViewById(R.id.aiAnalysisSection);

        selectedPharmacySection =
                findViewById(R.id.selectedPharmacySection);


        prescriptionImage =
                findViewById(R.id.prescriptionImage);


        prescriptionStatus =
                findViewById(R.id.prescriptionStatus);

        aiAnalysisText =
                findViewById(R.id.aiAnalysisText);

        noMedicationText =
                findViewById(R.id.noMedicationText);


        selectedPharmacyName =
                findViewById(R.id.selectedPharmacyName);

        selectedMedicationPrice =
                findViewById(R.id.selectedMedicationPrice);

        selectedPharmacyDistance =
                findViewById(R.id.selectedPharmacyDistance);

        selectedPharmacyDelivery =
                findViewById(R.id.selectedPharmacyDelivery);


        medicationTotalText =
                findViewById(R.id.medicationTotalText);

        deliveryFeeText =
                findViewById(R.id.deliveryFeeText);

        orderTotalText =
                findViewById(R.id.orderTotalText);
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

                            Intent data = result.getData();

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

                            selectedMedicationPriceValue =
                                    data.getDoubleExtra(
                                            "medication_price",
                                            0.00
                                    );

                            selectedMedicationAvailability =
                                    data.getStringExtra(
                                            "medication_availability"
                                    );


                            // Apply the selected pharmacy price
                            // to the medication being ordered.

                            applySelectedMedicationPrice();


                            // Display selected pharmacy information.

                            displaySelectedPharmacy();


                            // Refresh medication display and totals.

                            displayMedications();

                            calculateMedicationTotal();
                        }
                );
    }


    // =========================================================
    // APPLY REAL PHARMACY PRICE
    // =========================================================

    private void applySelectedMedicationPrice() {

        if (medications.isEmpty()) {
            return;
        }

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

        selectedPharmacyName.setText(
                selectedPharmacyNameValue
        );


        selectedMedicationPrice.setText(
                String.format(
                        Locale.getDefault(),
                        "Price per item: R%.2f",
                        selectedMedicationPriceValue
                )
        );


        String distance =
                selectedPharmacyDistanceValue;

        if (distance == null
                || distance.trim().isEmpty()) {

            distance = "Not available";
        }

        selectedPharmacyDistance.setText(
                "Distance: " + distance
        );


        String location =
                selectedPharmacyLocation;

        if (location == null
                || location.trim().isEmpty()) {

            location = "Not available";
        }

        selectedPharmacyDelivery.setText(
                "Location: " + location
        );
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


        // COMPARE PHARMACY PRICES

        comparePricesButton.setOnClickListener(v -> {

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
                            com.proteahealth.Patient
                                    .MedicationPriceComparisonActivity.class
                    );

            intent.putExtra(
                    "medicationName",
                    medication.name
            );

            pharmacyComparisonLauncher.launch(
                    intent
            );
        });


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
                        (Bitmap) extras.get("data");

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


        // No fake price.
        // Price will be added after pharmacy selection.

        double price = 0.00;


        Medication medication =
                new Medication(
                        name,
                        dosage,
                        quantity,
                        price
                );


        medications.add(
                medication
        );


        // A new medication means the previous pharmacy
        // selection should no longer be considered valid.

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
                new LinearLayout(this);

        row.setOrientation(
                LinearLayout.VERTICAL
        );

        row.setPadding(
                15,
                15,
                15,
                15
        );


        TextView medicationName =
                new TextView(this);

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


        TextView medicationDetails =
                new TextView(this);


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


        Button removeButton =
                new Button(this);

        removeButton.setText(
                "Remove"
        );

        removeButton.setTextColor(
                0xFFF05A91
        );


        removeButton.setOnClickListener(v -> {

            medications.remove(
                    position
            );

            // Pharmacy price was associated with the
            // current medication selection.

            clearSelectedPharmacy();

            displayMedications();

            calculateMedicationTotal();
        });


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

        medicationNameInput.setText("");

        dosageInput.setText("");

        quantityInput.setText("");

        medicationNameInput.requestFocus();
    }


    // =========================================================
    // CLEAR SELECTED PHARMACY
    // =========================================================

    private void clearSelectedPharmacy() {

        selectedPharmacyId = null;
        selectedPharmacyNameValue = null;
        selectedPharmacyLocation = null;
        selectedPharmacyDistanceValue = null;

        selectedMedicationPriceValue =
                0.00;

        selectedMedicationAvailability =
                null;


        selectedPharmacySection.setVisibility(
                View.GONE
        );


        // Reset medication prices because they must
        // come from a selected pharmacy.

        for (Medication medication :
                medications) {

            medication.price =
                    0.00;
        }


        // Until delivery information is connected
        // to the selected pharmacy, reset delivery.

        deliveryFee =
                0.00;

        deliveryRadio.setChecked(
                false
        );

        deliverySection.setVisibility(
                View.GONE
        );
    }


    // =========================================================
    // COLLECTION / DELIVERY
    // =========================================================

    private void setupDeliveryOptions() {

        medicationMethodGroup
                .setOnCheckedChangeListener(
                        (group, checkedId) -> {

                            if (checkedId
                                    == R.id.deliveryRadio) {

                                deliverySection.setVisibility(
                                        View.VISIBLE
                                );

                                /*
                                 * TEMPORARY.
                                 *
                                 * Next step:
                                 * this will come from the selected
                                 * pharmacy's delivery_fee field.
                                 */
                                deliveryFee =
                                        35.00;

                            } else {

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

        // MEDICATION

        if (medications.isEmpty()) {

            showMessage(
                    "No Medication",
                    "Please add at least one medication before confirming your order."
            );

            return;
        }


        // PHARMACY

        if (selectedPharmacyId == null
                || selectedPharmacyNameValue == null) {

            showMessage(
                    "Choose Pharmacy",
                    "Please compare prices and select a pharmacy before confirming your order."
            );

            return;
        }


        // MAKE SURE A REAL PRICE WAS SELECTED

        if (selectedMedicationPriceValue <= 0) {

            showMessage(
                    "Medication Price",
                    "Please select a valid medication price before confirming your order."
            );

            return;
        }


        // DELIVERY METHOD

        String method;

        if (collectionRadio.isChecked()) {

            method =
                    "Collection";

        } else if (deliveryRadio.isChecked()) {

            method =
                    "Delivery";

        } else {

            showMessage(
                    "Choose Delivery Method",
                    "Please choose collection or delivery."
            );

            return;
        }


        // DELIVERY ADDRESS

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


        // CONFIRMATION

        new AlertDialog.Builder(this)

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

        /*
         * This is still temporary.
         *
         * Next stage will send the order to PHP/MySQL
         * and use the real database order ID.
         */

        String orderId =
                "PH"
                        + System.currentTimeMillis();


        showMessage(
                "Order Confirmed",

                "Your medication order has been placed."

                        + "\n\n"

                        + "Order ID: "
                        + orderId

                        + "\n\n"

                        + "Pharmacy: "
                        + selectedPharmacyNameValue

                        + "\n\n"

                        + "Total: "
                        + String.format(
                        Locale.getDefault(),
                        "R%.2f",
                        orderTotal
                )
        );
    }


    // =========================================================
    // CANCEL ORDER
    // =========================================================

    private void cancelOrder() {

        new AlertDialog.Builder(this)

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

                            addressInput.setText("");


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

        new AlertDialog.Builder(this)

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