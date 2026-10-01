package com.proteahealth;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import android.content.Intent;

import com.proteahealth.data.SessionManager;

public class Patient_Home extends AppCompatActivity {

    private Button btnTakeMedicine;
    private TextView tvDoseStatus;
    private SharedPreferences demoPreferences;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);

        demoPreferences = getSharedPreferences(
                "demo_preferences",
                MODE_PRIVATE
        );

        SessionManager sessionManager = new SessionManager(this);
        String id = sessionManager.getUserId();
        String name = sessionManager.getName();
        String surname = sessionManager.getSurname();
        String email = sessionManager.getEmail();
        String role = sessionManager.getRole();

        // Keep the page clear of the phone's status and navigation bars.
        ViewCompat.setOnApplyWindowInsetsListener(
                findViewById(R.id.main),
                (view, windowInsets) -> {
                    Insets bars = windowInsets.getInsets(
                            WindowInsetsCompat.Type.systemBars()
                    );

                    view.setPadding(
                            bars.left,
                            bars.top,
                            bars.right,
                            bars.bottom
                    );

                    return windowInsets;
                }
        );

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }



        TextView tvToday = findViewById(R.id.tvToday);
        TextView tvVisitDate = findViewById(R.id.tvVisitDate);



        btnTakeMedicine = findViewById(R.id.btnTakeMedicine);
        tvDoseStatus = findViewById(R.id.tvDoseStatus);

        String today = new SimpleDateFormat(
                "EEEE, d MMM",
                Locale.ENGLISH
        ).format(new Date());

        tvToday.setText(
                "Welcome back " + name + " " + surname +
                        "\n\nToday is " + today.toUpperCase(Locale.ENGLISH)
        );

        // Demo appointment: always four days from today.
        Calendar visit = Calendar.getInstance();
        visit.add(Calendar.DAY_OF_YEAR, 4);

        String visitDate = new SimpleDateFormat(
                "dd\nMMM",
                Locale.ENGLISH
        ).format(visit.getTime());

        tvVisitDate.setText(visitDate.toUpperCase(Locale.ENGLISH));

        tvVisitDate.setText(visitDate + " · Clinic visit");

        btnTakeMedicine.setOnClickListener(view -> {
            // Demo only: remember today's sample dose on this device.
            demoPreferences.edit()
                    .putString("sample_dose_taken_date", getTodayKey())
                    .apply();

            refreshDoseStatus();
            showMessage("Sample dose marked as taken.");
        });

        findViewById(R.id.btnNotifications).setOnClickListener(
                view -> showMessage("No new demo notifications.")
        );

        findViewById(R.id.btnSeeMedicines).setOnClickListener(
                view -> showMessage(
                        "This will connect to the group's medication page."
                )
        );

        findViewById(R.id.btnViewVisits).setOnClickListener(
                view -> showMessage(
                        "This will connect to the group's appointments page."
                )
        );

        findViewById(R.id.cardPrices).setOnClickListener(
                view -> showMessage(
                        "This will connect to the group's pharmacy page."
                )
        );

        findViewById(R.id.cardRefill).setOnClickListener(
                view -> showMessage(
                        "Demo refill reminder: about 7 days remaining."
                )
        );

        findViewById(R.id.cardBlog).setOnClickListener(view -> {
            Intent intent = new Intent(Patient_Home.this, BlogActivity.class);
            startActivity(intent);
        });

        // Tapping the appointment card triggers "View visits".
        findViewById(R.id.cardVisit).setOnClickListener(view ->
                findViewById(R.id.btnViewVisits).performClick()
        );
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshDoseStatus();
    }

    private String getTodayKey() {
        return new SimpleDateFormat(
                "yyyy-MM-dd",
                Locale.ENGLISH
        ).format(new Date());
    }

    private void refreshDoseStatus() {
        String savedDate = demoPreferences.getString(
                "sample_dose_taken_date",
                ""
        );

        boolean takenToday = getTodayKey().equals(savedDate);

        tvDoseStatus.setText(
                takenToday ? "Taken today · 08:00 dose" : "Due at 08:00"
        );

        btnTakeMedicine.setText(
                takenToday ? "Taken ✓" : "I took it"
        );

        btnTakeMedicine.setEnabled(!takenToday);
    }

    private void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
