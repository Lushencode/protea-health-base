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

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.HashMap;

public class Collection extends AppCompatActivity {

    // CAMERA PERMISSION / REQUEST CODES
    private static final int CAMERA_PERMISSION_CODE = 101;
    private static final int CAMERA_REQUEST_CODE = 102;


    // UI COMPONENTS

    ImageButton backButton;

    Button uploadPrescriptionButton;
    Button addMedicationButton;
    Button confirmButton;
    Button cancelButton;

    EditText medicationNameInput;
    EditText dosageInput;
    EditText quantityInput;
    EditText addressInput;

    Spinner pharmacySpinner;

    RadioGroup medicationMethodGroup;

    RadioButton collectionRadio;
    RadioButton deliveryRadio;

    LinearLayout deliverySection;
    LinearLayout medicationList;

    LinearLayout aiAnalysisSection;

    ImageView prescriptionImage;

    TextView prescriptionStatus;
    TextView aiAnalysisText;
    TextView noMedicationText;

    TextView pharmacyAvailability;

    TextView medicationTotalText;
    TextView deliveryFeeText;
    TextView orderTotalText;


    // =========================================================
    // DATA
    // =========================================================

    ArrayList<Medication> medications =
            new ArrayList<>();

    HashMap<String, Double> pharmacyPrices =
            new HashMap<>();


    // =========================================================
    // ORDER TOTALS
    // =========================================================

    double medicationTotal = 0.00;

    double deliveryFee = 0.00;

    double orderTotal = 0.00;


    // =========================================================
    // ON CREATE
    // =========================================================

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(
                R.layout.activity_collection
        );


        // Connect XML components

        initialiseViews();


        // Setup pharmacy spinner

        setupPharmacies();


        // Setup buttons

        setupButtons();


        // Setup collection / delivery

        setupDeliveryOptions();


        // Initial order summary

        updateOrderSummary();
    }


    // =========================================================
    // INITIALISE VIEWS
    // =========================================================

    private void initialiseViews() {

        backButton =
                findViewById(R.id.backButton);


        uploadPrescriptionButton =
                findViewById(
                        R.id.uploadPrescriptionButton
                );


        addMedicationButton =
                findViewById(
                        R.id.addMedicationButton
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


        pharmacySpinner =
                findViewById(
                        R.id.pharmacySpinner
                );


        medicationMethodGroup =
                findViewById(
                        R.id.medicationMethodGroup
                );


        collectionRadio =
                findViewById(
                        R.id.collectionRadio
                );


        deliveryRadio = findViewById(R.id.deliveryRadio);


        deliverySection = findViewById(R.id.deliverySection);


        medicationList = findViewById(R.id.medicationList);


        aiAnalysisSection = findViewById(R.id.aiAnalysisSection);


        prescriptionImage = findViewById(R.id.prescriptionImage);


        prescriptionStatus = findViewById(R.id.prescriptionStatus);


        aiAnalysisText = findViewById(R.id.aiAnalysisText);


        noMedicationText = findViewById(R.id.noMedicationText);


        pharmacyAvailability = findViewById(R.id.pharmacyAvailability);


        medicationTotalText = findViewById(R.id.medicationTotalText);


        deliveryFeeText = findViewById(R.id.deliveryFeeText);


        orderTotalText = findViewById(R.id.orderTotalText);
    }

    // SETUP PHARMACIES


    private void setupPharmacies() {

        String[] pharmacies = {

                "Select Pharmacy",

                "Dis-Chem",

                "Clicks",

                "Medirite",

                "Alpha Pharm"
        };


        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_spinner_dropdown_item,
                        pharmacies
                );


        pharmacySpinner.setAdapter(
                adapter
        );


        // Pharmacy selected

        pharmacySpinner.setOnItemSelectedListener(

                new AdapterView.OnItemSelectedListener() {

                    @Override
                    public void onItemSelected(
                            AdapterView<?> parent,
                            View view,
                            int position,
                            long id) {


                        if (position == 0) {

                            pharmacyAvailability.setText(
                                    "Select a pharmacy to check availability."
                            );

                        } else {

                            String pharmacy =
                                    parent
                                            .getItemAtPosition(position)
                                            .toString();


                            checkPharmacy(
                                    pharmacy
                            );
                        }
                    }


                    @Override
                    public void onNothingSelected(
                            AdapterView<?> parent) {

                    }
                }
        );
    }


    // =========================================================
    // CHECK PHARMACY
    // =========================================================

    private void checkPharmacy(
            String pharmacy) {


        if (medications.size() == 0) {

            pharmacyAvailability.setText(
                    "Add medication to check availability."
            );

            return;
        }


        /*
         * Currently simulated.
         *
         * Later this can connect to:
         *
         * MySQL
         * Firebase
         * Pharmacy API
         * Backend server
         */


        pharmacyAvailability.setText(

                medications.size()
                        + " medication(s) available at "
                        + pharmacy
                        + "."

        );


        calculateMedicationTotal();
    }


    // =========================================================
    // SETUP BUTTONS
    // =========================================================

    private void setupButtons() {


        // BACK

        backButton.setOnClickListener(
                v -> finish()
        );


        // TAKE PRESCRIPTION PHOTO

        uploadPrescriptionButton.setOnClickListener(
                v -> openCamera()
        );


        // ADD MEDICATION

        addMedicationButton.setOnClickListener(
                v -> addMedication()
        );


        // CONFIRM ORDER

        confirmButton.setOnClickListener(
                v -> confirmOrder()
        );


        // CANCEL ORDER

        cancelButton.setOnClickListener(
                v -> cancelOrder()
        );
    }


    // =========================================================
    // OPEN CAMERA
    // =========================================================

    private void openCamera() {


        // Check camera permission

        if (checkSelfPermission(
                Manifest.permission.CAMERA
        ) != PackageManager.PERMISSION_GRANTED) {


            // Ask user for permission

            requestPermissions(

                    new String[]{
                            Manifest.permission.CAMERA
                    },

                    CAMERA_PERMISSION_CODE
            );


        } else {

            // Permission already granted

            launchCamera();
        }
    }


    // =========================================================
    // LAUNCH CAMERA
    // =========================================================

    private void launchCamera() {


        Intent cameraIntent =
                new Intent(
                        MediaStore.ACTION_IMAGE_CAPTURE
                );


        if (
                cameraIntent.resolveActivity(
                        getPackageManager()
                ) != null
        ) {


            startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE);


        } else {

            Toast.makeText(

                    this,

                    "No camera application found.",

                    Toast.LENGTH_LONG

            ).show();
        }
    }

    // CAMERA PERMISSION RESULT

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


        if (
                requestCode ==
                        CAMERA_PERMISSION_CODE
        ) {


            if (
                    grantResults.length > 0
                            &&
                            grantResults[0]
                                    ==
                                    PackageManager.PERMISSION_GRANTED
            ) {


                // Permission granted

                launchCamera();


            } else {


                // Permission denied

                Toast.makeText(

                        this,

                        "Camera permission is required to take a photo of your prescription.",

                        Toast.LENGTH_LONG

                ).show();
            }
        }
    }

    // CAMERA RESULT

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


        if (

                requestCode ==
                        CAMERA_REQUEST_CODE

                        &&

                        resultCode ==
                                RESULT_OK

                        &&

                        data != null

        ) {


            Bundle extras =
                    data.getExtras();


            if (extras != null) {


                Bitmap prescriptionPhoto =
                        (Bitmap)
                                extras.get("data");


                if (
                        prescriptionPhoto != null
                ) {


                    // Display prescription

                    prescriptionImage.setImageBitmap(
                            prescriptionPhoto
                    );


                    prescriptionImage.setVisibility(
                            View.VISIBLE
                    );


                    // Update status

                    prescriptionStatus.setText(
                            "Prescription photo captured successfully."
                    );


                    prescriptionStatus.setTextColor(
                            0xFF0B5D4B
                    );


                    // Show AI section

                    aiAnalysisSection.setVisibility(
                            View.VISIBLE
                    );


                    aiAnalysisText.setText(
                            "Checking prescription..."
                    );


                    new Handler()
                            .postDelayed(

                                    () -> {

                                        aiAnalysisText.setText(

                                                "Prescription detected. "
                                                        + "Please review the medication "
                                                        + "information before ordering."

                                        );

                                    },

                                    1500
                            );
                }
            }
        }
    }

    // ADD MEDICATION

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


        // VALIDATION

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

        } catch (
                NumberFormatException e
        ) {

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


        // Temporary estimated price

        double price =
                calculateEstimatedPrice(

                        name,

                        quantity
                );


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


        // Update medication list

        displayMedications();


        // Recalculate total

        calculateMedicationTotal();


        // Clear inputs

        clearMedicationInputs();
    }


    // =========================================================
    // ESTIMATED MEDICATION PRICE
    // =========================================================

    private double calculateEstimatedPrice(

            String medicationName,

            int quantity) {
        //temporary pricing
        double basePrice =
                50.00;


        return basePrice * quantity;
    }

    // DISPLAY MEDICATIONS

    private void displayMedications() {


        medicationList.removeAllViews();


        if (medications.size() == 0) {


            noMedicationText.setVisibility(
                    View.VISIBLE
            );


            return;
        }


        noMedicationText.setVisibility(
                View.GONE
        );


        for (

                int i = 0;

                i < medications.size();

                i++

        ) {


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

    // CREATE MEDICATION ROW

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


        // =========================
        // MEDICATION NAME
        // =========================

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

        // MEDICATION DETAILS


        TextView medicationDetails =
                new TextView(this);


        medicationDetails.setText(

                medication.dosage

                        + " • Quantity: "

                        + medication.quantity

                        + "\nEstimated price: R"

                        + String.format(

                        "%.2f",

                        medication.price
                )

        );


        medicationDetails.setTextSize(
                14
        );


        medicationDetails.setTextColor(
                0xFF666666
        );

        // REMOVE BUTTON

        Button removeButton =
                new Button(this);


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


                    displayMedications();


                    calculateMedicationTotal();

                }

        );


        // Add views

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

    // CLEAR MEDICATION INPUTS

    private void clearMedicationInputs() {


        medicationNameInput.setText("");


        dosageInput.setText("");


        quantityInput.setText("");


        medicationNameInput.requestFocus();
    }

    // COLLECTION / DELIVERY

    private void setupDeliveryOptions() {


        medicationMethodGroup
                .setOnCheckedChangeListener(

                        (group, checkedId) -> {


                            if (
                                    checkedId ==
                                            R.id.deliveryRadio
                            ) {


                                // Show address

                                deliverySection
                                        .setVisibility(
                                                View.VISIBLE
                                        );


                                // Delivery fee

                                deliveryFee =
                                        35.00;


                            } else {


                                // Hide address

                                deliverySection
                                        .setVisibility(
                                                View.GONE
                                        );


                                // No delivery fee

                                deliveryFee =
                                        0.00;
                            }


                            updateOrderSummary();
                        }
                );
    }


    // CALCULATE MEDICATION TOTAL

    private void calculateMedicationTotal() {


        medicationTotal =
                0.00;


        for (
                Medication medication :
                medications
        ) {


            medicationTotal +=
                    medication.price;
        }


        updateOrderSummary();
    }

    // UPDATE ORDER SUMMARY

    private void updateOrderSummary() {


        orderTotal =
                medicationTotal
                        +
                        deliveryFee;


        medicationTotalText.setText(

                "R"

                        + String.format(

                        "%.2f",

                        medicationTotal
                )
        );


        deliveryFeeText.setText(

                "R"

                        + String.format(

                        "%.2f",

                        deliveryFee
                )
        );


        orderTotalText.setText(

                "R"

                        + String.format(

                        "%.2f",

                        orderTotal
                )
        );
    }

    // CONFIRM ORDER

    private void confirmOrder() {

        // CHECK MEDICATION

        if (medications.size() == 0) {


            showMessage(

                    "No Medication",

                    "Please add at least one medication before confirming your order."

            );


            return;
        }

        // CHECK PHARMACY

        if (

                pharmacySpinner
                        .getSelectedItemPosition()
                        == 0

        ) {


            showMessage(

                    "Choose Pharmacy",

                    "Please select a pharmacy before confirming your order."

            );


            return;
        }

        // CHECK DELIVERY ADDRESS

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

        // PHARMACY

        String pharmacy = pharmacySpinner.getSelectedItem().toString();

        // DELIVERY METHOD

        String method;


        if (collectionRadio.isChecked()) {


            method = "Collection";


        } else if (deliveryRadio.isChecked()) {


            method = "Delivery";


        } else {


            showMessage("Choose Delivery Method", "Please choose collection or delivery.");


            return;
        }

        // CONFIRMATION DIALOG

        new AlertDialog.Builder(this)

                .setTitle("Confirm Order")

                .setMessage(

                        "Pharmacy: "

                                + pharmacy

                                + "\n\n"

                                + "Method: "

                                + method

                                + "\n\n"

                                + "Medication items: "

                                + medications.size()

                                + "\n\n"

                                + "Medication total: R"

                                + String.format(

                                "%.2f",

                                medicationTotal
                        )

                                + "\n"

                                + "Delivery fee: R"

                                + String.format(

                                "%.2f",

                                deliveryFee
                        )

                                + "\n\n"

                                + "TOTAL: R"

                                + String.format(

                                "%.2f",

                                orderTotal
                        )

                                + "\n\n"

                                + "Would you like to place this order?"

                )


                .setPositiveButton(

                        "Confirm",

                        (dialog, which) -> {

                            placeOrder();

                        }

                )


                .setNegativeButton(

                        "Cancel",

                        null

                )


                .show();
    }

    // PLACE ORDER

    private void placeOrder() {


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

                        + "Total: R"

                        + String.format(

                        "%.2f",

                        orderTotal
                )

        );
    }

    // CANCEL ORDER

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


                            // Clear medication

                            medications.clear();


                            displayMedications();


                            // Reset totals

                            medicationTotal =
                                    0.00;


                            deliveryFee =
                                    0.00;


                            updateOrderSummary();


                            // Reset pharmacy

                            pharmacySpinner
                                    .setSelection(0);


                            // Reset delivery method

                            medicationMethodGroup
                                    .clearCheck();


                            // Clear address

                            addressInput.setText("");


                            // Reset prescription

                            prescriptionStatus.setText(

                                    "No prescription uploaded"

                            );


                            prescriptionImage
                                    .setVisibility(
                                            View.GONE
                                    );


                            aiAnalysisSection
                                    .setVisibility(
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


    // MESSAGE HELPER

    private void showMessage(

            String title,

            String message) {


        new AlertDialog.Builder(this)

                .setTitle(title)

                .setMessage(message)

                .setPositiveButton(

                        "OK",

                        null

                )

                .show();
    }

    // MEDICATION CLASS

    public static class Medication {


        String name;

        String dosage;

        int quantity;

        double price;


        public Medication(

                String name,

                String dosage,

                int quantity,

                double price

        ) {


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