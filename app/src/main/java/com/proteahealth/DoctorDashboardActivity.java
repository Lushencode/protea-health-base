package com.proteahealth;

import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class DoctorDashboardActivity extends AppCompatActivity {

    private TextView tvTodayAppointmentsCount;
    private TextView tvFollowUpsCount;
    private LinearLayout appointmentsContainer;
    private LinearLayout followUpsContainer;
    private TextView tvAppointmentsEmpty;
    private TextView tvFollowUpsEmpty;
    private Button btnSearchPatients;
    private Button btnHealthHub;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_doctor_dashboard);

        ViewCompat.setOnApplyWindowInsetsListener(
                findViewById(android.R.id.content),
                (view, windowInsets) -> {
                    Insets bars = windowInsets.getInsets(
                            WindowInsetsCompat.Type.systemBars()
                    );
                    view.setPadding(
                            bars.left, bars.top, bars.right, bars.bottom
                    );
                    return windowInsets;
                }
        );

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        initializeViews();
        populateMockData();
        setupClickListeners();
    }

    private void initializeViews() {
        tvTodayAppointmentsCount =
                findViewById(R.id.tvTodayAppointmentsCount);
        tvFollowUpsCount =
                findViewById(R.id.tvFollowUpsCount);
        appointmentsContainer =
                findViewById(R.id.appointmentsContainer);
        followUpsContainer =
                findViewById(R.id.followUpsContainer);
        tvAppointmentsEmpty =
                findViewById(R.id.tvAppointmentsEmpty);
        tvFollowUpsEmpty =
                findViewById(R.id.tvFollowUpsEmpty);
        btnSearchPatients =
                findViewById(R.id.btnSearchPatients);
        btnHealthHub =
                findViewById(R.id.btnHealthHub);
    }

    private void populateMockData() {
        tvTodayAppointmentsCount.setText("3\nAppointments today");
        tvFollowUpsCount.setText("2\nFollow-ups needed");

        tvAppointmentsEmpty.setVisibility(View.GONE);
        addAppointmentItem(
                "09:00 AM - Thabo Mokoena",
                "Initial diabetes consultation & HbA1c review"
        );
        addAppointmentItem(
                "10:30 AM - Sarah Jenkins",
                "Blood pressure check & medication adjustment"
        );
        addAppointmentItem(
                "02:00 PM - Sipho Dlamini",
                "Routine wellness check-up"
        );

        tvFollowUpsEmpty.setVisibility(View.GONE);
        addFollowUpItem(
                "Lerato Khumalo",
                "Review lab results (Cholesterol panel)"
        );
        addFollowUpItem(
                "Michael van der Merwe",
                "Check post-op recovery status"
        );
    }

    // Each appointment gets its own pale teal card.
    private void addAppointmentItem(String title, String subtitle) {
        LinearLayout card =
                createListCard(0xFFE8F5F3, 0xFFB8DEDA);

        addCardText(card, title, 16, true, 0xFF0C514A);
        addCardText(card, subtitle, 14, false, 0xFF333333);

        appointmentsContainer.addView(card);
    }

    // Each follow-up gets its own pale warm card.
    private void addFollowUpItem(String patient, String task) {
        LinearLayout card =
                createListCard(0xFFFFF4E8, 0xFFF1D5B5);

        addCardText(card, patient, 16, true, 0xFF5C391A);
        addCardText(
                card,
                "Follow-up: " + task,
                14,
                false,
                0xFF333333
        );

        followUpsContainer.addView(card);
    }

    private LinearLayout createListCard(
            int fillColor,
            int borderColor
    ) {
        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(dp(16), dp(14), dp(16), dp(14));

        GradientDrawable background = new GradientDrawable();
        background.setColor(fillColor);
        background.setCornerRadius(dp(14));
        background.setStroke(dp(1), borderColor);
        card.setBackground(background);

        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
        params.bottomMargin = dp(10);
        card.setLayoutParams(params);

        return card;
    }

    private void addCardText(
            LinearLayout card,
            String text,
            int sizeSp,
            boolean bold,
            int textColor
    ) {
        TextView textView = new TextView(this);
        textView.setText(text);
        textView.setTextSize(sizeSp);
        textView.setTextColor(textColor);

        if (bold) {
            textView.setTypeface(null, Typeface.BOLD);
        } else {
            textView.setPadding(0, dp(5), 0, 0);
        }

        card.addView(textView);
    }

    private int dp(int value) {
        return Math.round(
                value * getResources().getDisplayMetrics().density
        );
    }

    private void setupClickListeners() {
        btnSearchPatients.setOnClickListener(v ->
                Toast.makeText(
                        this,
                        "Search patients feature coming soon",
                        Toast.LENGTH_SHORT
                ).show()
        );

        btnHealthHub.setOnClickListener(v ->
                Toast.makeText(
                        this,
                        "Health hub management coming soon",
                        Toast.LENGTH_SHORT
                ).show()
        );
    }
}