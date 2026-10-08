package com.proteahealth.Pharmacist;


import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.proteahealth.R;
import com.proteahealth.adapter.PharmacyInventoryAdapter;
import com.proteahealth.data.PharmacyInventoryItem;
import com.proteahealth.api.PharmacyInventoryResponse;
import com.proteahealth.api.RetrofitClient;

import android.app.AlertDialog;
import android.widget.EditText;
import java.util.Locale;
import java.math.BigDecimal;


import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PharmacyInventoryActivity extends AppCompatActivity {

    private RecyclerView rvInventory;
    private ProgressBar progressBar;
    private TextView tvEmptyInventory;
    private Button btnAddMedication;

    private PharmacyInventoryAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pharmacy_inventory);

        rvInventory = findViewById(R.id.rvPharmacyInventory);
        progressBar = findViewById(R.id.inventoryProgressBar);
        tvEmptyInventory = findViewById(R.id.tvEmptyInventory);
        btnAddMedication = findViewById(R.id.btnAddMedication);

        adapter = new PharmacyInventoryAdapter(
                new PharmacyInventoryAdapter.OnMedicationActionListener() {

                    @Override
                    public void onEdit(PharmacyInventoryItem medication) {
                        showEditMedicationDialog(medication);
                    }

                    @Override
                    public void onRemove(PharmacyInventoryItem medication) {
                        Toast.makeText(
                                PharmacyInventoryActivity.this,
                                "Remove: " + medication.getMedicationName(),
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                }
        );

        rvInventory.setLayoutManager(
                new LinearLayoutManager(this)
        );
        rvInventory.setAdapter(adapter);

        btnAddMedication.setOnClickListener(v -> showAddMedicationDialog());

        loadInventory();
    }

    private void loadInventory() {
        progressBar.setVisibility(View.VISIBLE);
        tvEmptyInventory.setVisibility(View.GONE);

        RetrofitClient.INSTANCE.getApiService()
                .getPharmacyInventory()
                .enqueue(new Callback<PharmacyInventoryResponse>() {

                    @Override
                    public void onResponse(
                            @NonNull Call<PharmacyInventoryResponse> call,
                            @NonNull Response<PharmacyInventoryResponse> response) {

                        progressBar.setVisibility(View.GONE);

                        PharmacyInventoryResponse body = response.body();

                        if (response.isSuccessful()
                                && body != null
                                && body.getSuccess()) {

                            adapter.setMedications(body.getMedications());

                            boolean empty = body.getMedications() == null
                                    || body.getMedications().isEmpty();

                            tvEmptyInventory.setText("No medications found");
                            tvEmptyInventory.setVisibility(
                                    empty ? View.VISIBLE : View.GONE
                            );

                        } else {
                            adapter.setMedications(null);
                            tvEmptyInventory.setText(
                                    "Unable to load inventory (HTTP "
                                            + response.code() + ")"
                            );
                            tvEmptyInventory.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onFailure(
                            @NonNull Call<PharmacyInventoryResponse> call,
                            @NonNull Throwable t) {

                        progressBar.setVisibility(View.GONE);
                        adapter.setMedications(null);

                        tvEmptyInventory.setText(
                                "Connection error. Check your PHP server."
                        );
                        tvEmptyInventory.setVisibility(View.VISIBLE);
                    }
                });
    }

    private void showAddMedicationDialog() {

        View dialogView = getLayoutInflater().inflate(
                R.layout.dialog_add_pharmacy_medication,
                null
        );

        EditText etName = dialogView.findViewById(R.id.etMedicationName);
        EditText etPrice = dialogView.findViewById(R.id.etMedicationPrice);
        EditText etStock = dialogView.findViewById(R.id.etMedicationStock);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(dialogView)
                .setNegativeButton("Cancel", (d, which) -> d.dismiss())
                .setPositiveButton("Add Medication", null)
                .create();

        dialog.setOnShowListener(d -> {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                    .setOnClickListener(v -> {

                        String name = etName.getText().toString().trim();
                        String priceText = etPrice.getText().toString().trim();
                        String stockText = etStock.getText().toString().trim();

                        if (name.isEmpty()) {
                            etName.setError("Medication name is required");
                            return;
                        }

                        BigDecimal price;
                        int stock;

                        try {
                            price = new BigDecimal(priceText);

                            if (price.signum() < 0
                                    || price.scale() > 2
                                    || price.compareTo(
                                    new BigDecimal("99999999.99")
                            ) > 0) {
                                etPrice.setError("Enter a valid price");
                                return;
                            }
                        } catch (NumberFormatException e) {
                            etPrice.setError("Enter a valid price");
                            return;
                        }

                        try {
                            stock = Integer.parseInt(stockText);

                            if (stock < 0) {
                                etStock.setError("Stock cannot be negative");
                                return;
                            }
                        } catch (NumberFormatException e) {
                            etStock.setError("Enter a valid stock quantity");
                            return;
                        }

                        dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                                .setEnabled(false);

                        RetrofitClient.INSTANCE.getApiService()
                                .addPharmacyMedication(
                                        name,
                                        price.toPlainString(),
                                        stock
                                )
                                .enqueue(new Callback<com.proteahealth.api.AddPharmacyMedicationResponse>() {

                                    @Override
                                    public void onResponse(
                                            @NonNull Call<com.proteahealth.api.AddPharmacyMedicationResponse> call,
                                            @NonNull Response<com.proteahealth.api.AddPharmacyMedicationResponse> response) {

                                        if (response.isSuccessful()
                                                && response.body() != null
                                                && response.body().getSuccess()) {

                                            Toast.makeText(
                                                    PharmacyInventoryActivity.this,
                                                    "Medication added successfully",
                                                    Toast.LENGTH_SHORT
                                            ).show();

                                            dialog.dismiss();
                                            loadInventory();

                                        } else {
                                            dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                                                    .setEnabled(true);

                                            Toast.makeText(
                                                    PharmacyInventoryActivity.this,
                                                    "Unable to add medication (HTTP "
                                                            + response.code() + ")",
                                                    Toast.LENGTH_LONG
                                            ).show();
                                        }
                                    }

                                    @Override
                                    public void onFailure(
                                            @NonNull Call<com.proteahealth.api.AddPharmacyMedicationResponse> call,
                                            @NonNull Throwable t) {

                                        dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                                                .setEnabled(true);

                                        Toast.makeText(
                                                PharmacyInventoryActivity.this,
                                                "Connection error: " + t.getMessage(),
                                                Toast.LENGTH_LONG
                                        ).show();
                                    }
                                });
                    });
        });

        dialog.show();
    }

    private void showEditMedicationDialog(PharmacyInventoryItem medication) {

        View dialogView = getLayoutInflater().inflate(
                R.layout.dialog_add_pharmacy_medication,
                null
        );

        EditText etName = dialogView.findViewById(R.id.etMedicationName);
        EditText etPrice = dialogView.findViewById(R.id.etMedicationPrice);
        EditText etStock = dialogView.findViewById(R.id.etMedicationStock);

        // Display existing medication information
        etName.setText(medication.getMedicationName());
        etName.setEnabled(false);

        etPrice.setText(String.format(
                Locale.US,
                "%.2f",
                medication.getPrice()
        ));

        etStock.setText(
                String.valueOf(medication.getStockQuantity())
        );

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Edit Medication")
                .setView(dialogView)
                .setNegativeButton("Cancel", (d, which) -> d.dismiss())
                .setPositiveButton("Save Changes", null)
                .create();

        dialog.setOnShowListener(d -> {

            dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                    .setOnClickListener(v -> {

                        String priceText = etPrice.getText().toString().trim();
                        String stockText = etStock.getText().toString().trim();

                        BigDecimal price;
                        int stock;

                        try {
                            price = new BigDecimal(priceText);

                            if (price.signum() < 0
                                    || price.scale() > 2
                                    || price.compareTo(
                                    new BigDecimal("99999999.99")
                            ) > 0) {

                                etPrice.setError("Enter a valid price");
                                return;
                            }

                        } catch (NumberFormatException e) {
                            etPrice.setError("Enter a valid price");
                            return;
                        }

                        try {
                            stock = Integer.parseInt(stockText);

                            if (stock < 0) {
                                etStock.setError("Stock cannot be negative");
                                return;
                            }

                        } catch (NumberFormatException e) {
                            etStock.setError("Enter a valid stock quantity");
                            return;
                        }

                        dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                                .setEnabled(false);

                        RetrofitClient.INSTANCE.getApiService()
                                .updatePharmacyMedication(
                                        medication.getId(),
                                        price.toPlainString(),
                                        stock
                                )
                                .enqueue(new Callback<com.proteahealth.api.UpdatePharmacyMedicationResponse>() {

                                    @Override
                                    public void onResponse(
                                            @NonNull Call<com.proteahealth.api.UpdatePharmacyMedicationResponse> call,
                                            @NonNull Response<com.proteahealth.api.UpdatePharmacyMedicationResponse> response) {

                                        if (response.isSuccessful()
                                                && response.body() != null
                                                && response.body().getSuccess()) {

                                            Toast.makeText(
                                                    PharmacyInventoryActivity.this,
                                                    "Medication updated successfully",
                                                    Toast.LENGTH_SHORT
                                            ).show();

                                            dialog.dismiss();
                                            loadInventory();

                                        } else {

                                            dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                                                    .setEnabled(true);

                                            Toast.makeText(
                                                    PharmacyInventoryActivity.this,
                                                    "Unable to update medication (HTTP "
                                                            + response.code() + ")",
                                                    Toast.LENGTH_LONG
                                            ).show();
                                        }
                                    }

                                    @Override
                                    public void onFailure(
                                            @NonNull Call<com.proteahealth.api.UpdatePharmacyMedicationResponse> call,
                                            @NonNull Throwable t) {

                                        dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                                                .setEnabled(true);

                                        Toast.makeText(
                                                PharmacyInventoryActivity.this,
                                                "Connection error: " + t.getMessage(),
                                                Toast.LENGTH_LONG
                                        ).show();
                                    }
                                });
                    });
        });

        dialog.show();
    }

}

