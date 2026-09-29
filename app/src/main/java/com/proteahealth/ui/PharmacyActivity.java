package com.proteahealth.ui;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.proteahealth.R;
import com.proteahealth.adapter.PharmacyAdapter;
import com.proteahealth.data.MockDataProvider;
import com.proteahealth.model.Pharmacy;

import java.util.List;

public class PharmacyActivity extends AppCompatActivity {

    private static final String DEFAULT_LABEL = "Showing prices for: Metformin 500mg";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pharmacy);

        RecyclerView rvPharmacies = findViewById(R.id.rvPharmacies);
        EditText etSearchMedication = findViewById(R.id.etSearchMedication);
        TextView tvShowingLabel = findViewById(R.id.tvShowingLabel);

        List<Pharmacy> pharmacies = MockDataProvider.getPharmacies();
        PharmacyAdapter pharmacyAdapter = new PharmacyAdapter(pharmacies);

        rvPharmacies.setLayoutManager(new LinearLayoutManager(this));
        rvPharmacies.setAdapter(pharmacyAdapter);

        String passedMedicationName = getIntent().getStringExtra("medicationName");

        if (passedMedicationName != null && !passedMedicationName.isEmpty()) {
            etSearchMedication.setText(passedMedicationName);
            pharmacyAdapter.filterByMedication(passedMedicationName);
            tvShowingLabel.setText("Showing prices for: " + passedMedicationName);
        } else {
            tvShowingLabel.setText(DEFAULT_LABEL);
        }

        etSearchMedication.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                pharmacyAdapter.filterByMedication(s.toString());
                if (s.toString().trim().isEmpty()) {
                    tvShowingLabel.setText(DEFAULT_LABEL);
                } else {
                    tvShowingLabel.setText("Showing prices for: " + s.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }
}