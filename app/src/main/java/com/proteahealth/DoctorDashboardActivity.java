package com.proteahealth;

import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import android.content.Intent;
import android.widget.ImageButton;
import android.widget.Toast;

public class DoctorDashboardActivity extends AppCompatActivity {
    public static final String EXTRA_PROVIDER_ID = "PROVIDER_ID";
    public static final String EXTRA_USER_ROLE = "USER_ROLE";

    private DoctorDashboardRepository repository;
    private String providerId;
    private DoctorDashboardRepository.Dashboard currentData;
    private TextView appointmentsCount;
    private TextView followUpsCount;
    private TextView appointmentsEmpty;
    private TextView followUpsEmpty;
    private TextView questionsEmpty;
    private TextView tipsEmpty;
    private LinearLayout appointmentsContainer;
    private LinearLayout followUpsContainer;
    private LinearLayout questionsContainer;
    private LinearLayout tipsContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String role = getIntent().getStringExtra(EXTRA_USER_ROLE);
        if (role != null && !"DOCTOR".equalsIgnoreCase(role)) {
            finish();
            return;
        }

        providerId = getIntent().getStringExtra(EXTRA_PROVIDER_ID);
        if (providerId == null || providerId.trim().isEmpty()) {
            providerId = MockDoctorDashboardRepository.DEMO_PROVIDER_ID;
        }

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_doctor_dashboard);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.doctorRoot),
                (view, insets) -> {
                    Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                    view.setPadding(bars.left, bars.top, bars.right, 0);
                    return insets;
                });
        if (getSupportActionBar() != null) getSupportActionBar().hide();

        appointmentsCount = findViewById(R.id.tvTodayAppointmentsCount);
        followUpsCount = findViewById(R.id.tvFollowUpsCount);
        appointmentsContainer = findViewById(R.id.appointmentsContainer);
        followUpsContainer = findViewById(R.id.followUpsContainer);
        appointmentsEmpty = findViewById(R.id.tvAppointmentsEmpty);
        followUpsEmpty = findViewById(R.id.tvFollowUpsEmpty);
        questionsContainer = findViewById(R.id.questionsContainer);
        tipsContainer = findViewById(R.id.tipsContainer);
        questionsEmpty = findViewById(R.id.tvQuestionsEmpty);
        tipsEmpty = findViewById(R.id.tvTipsEmpty);
        TextView previewLabel = findViewById(R.id.tvPreviewLabel);
        // This build always uses the local mock repository, even when a provider ID is supplied.
        previewLabel.setVisibility(View.VISIBLE);

        repository = new MockDoctorDashboardRepository(this);
        findViewById(R.id.btnSearchPatients).setOnClickListener(view -> searchVisiblePatients());
        findViewById(R.id.btnPostTip).setOnClickListener(view -> showPostTipDialog());


        ImageButton navDoctorProfile =
                findViewById(R.id.navDoctorProfile);

        ImageButton navDoctorPatients =
                findViewById(R.id.navDoctorPatients);

        ImageButton navDoctorHome =
                findViewById(R.id.navDoctorHome);

        ImageButton navDoctorAppointments =
                findViewById(R.id.navDoctorAppointments);

        ImageButton navDoctorQuestions =
                findViewById(R.id.navDoctorQuestions);


// PROFILE
        navDoctorProfile.setOnClickListener(v -> {

            Intent intent = new Intent(
                    DoctorDashboardActivity.this,
                    ProfileActivity.class
            );

            startActivity(intent);
        });


// PATIENTS
        navDoctorPatients.setOnClickListener(v -> {

            Toast.makeText(
                    DoctorDashboardActivity.this,
                    "Coming soon",
                    Toast.LENGTH_SHORT
            ).show();
        });


        navDoctorHome.setBackgroundResource(
                R.drawable.nav_icon_glow
        );


// APPOINTMENTS
        navDoctorAppointments.setOnClickListener(v -> {

            Toast.makeText(
                    DoctorDashboardActivity.this,
                    "Coming soon",
                    Toast.LENGTH_SHORT
            ).show();
        });


// QUESTIONS
        navDoctorQuestions.setOnClickListener(v -> {

            Toast.makeText(
                    DoctorDashboardActivity.this,
                    "Coming soon",
                    Toast.LENGTH_SHORT
            ).show();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (repository != null) refreshDashboard();
    }

    private void refreshDashboard() {
        repository.load(providerId, new DoctorDashboardRepository.Callback<DoctorDashboardRepository.Dashboard>() {
            @Override public void onSuccess(DoctorDashboardRepository.Dashboard data) {
                runOnUiThread(() -> {
                    if (isFinishing() || isDestroyed()) return;
                    currentData = data;
                    render(data);
                });
            }
            @Override public void onError(String message) {
                runOnUiThread(() -> showError(message));
            }
        });
    }

    private void render(DoctorDashboardRepository.Dashboard data) {
        int today = 0;
        Calendar now = Calendar.getInstance();
        for (DoctorDashboardRepository.AppointmentRequest request : data.appointments) {
            Calendar date = Calendar.getInstance();
            date.setTimeInMillis(request.slotMillis);
            if (date.get(Calendar.YEAR) == now.get(Calendar.YEAR)
                    && date.get(Calendar.DAY_OF_YEAR) == now.get(Calendar.DAY_OF_YEAR)) today++;
        }
        int openFollowUps = 0;
        for (DoctorDashboardRepository.FollowUp item : data.followUps) {
            if (!item.reviewed) openFollowUps++;
        }
        appointmentsCount.setText(today + "\nRequests today");
        followUpsCount.setText(openFollowUps + "\nOpen follow-ups");

        // Keep the XML empty-state TextViews as the first child of each container.
        while (appointmentsContainer.getChildCount() > 1) appointmentsContainer.removeViewAt(1);
        while (followUpsContainer.getChildCount() > 1) followUpsContainer.removeViewAt(1);
        while (questionsContainer.getChildCount() > 1) questionsContainer.removeViewAt(1);
        while (tipsContainer.getChildCount() > 1) tipsContainer.removeViewAt(1);
        appointmentsEmpty.setVisibility(data.appointments.isEmpty() ? View.VISIBLE : View.GONE);

        List<DoctorDashboardRepository.AppointmentRequest> sorted = new ArrayList<>(data.appointments);
        sorted.sort(Comparator.comparingInt((DoctorDashboardRepository.AppointmentRequest a) ->
                a.status == DoctorDashboardRepository.AppointmentStatus.PENDING ? 0 : 1)
                .thenComparingInt(a -> a.priorityCategory)
                .thenComparingLong(a -> a.slotMillis));
        for (DoctorDashboardRepository.AppointmentRequest request : sorted) {
            appointmentsContainer.addView(appointmentCard(request));
        }

        followUpsEmpty.setVisibility(openFollowUps == 0 ? View.VISIBLE : View.GONE);
        for (DoctorDashboardRepository.FollowUp item : data.followUps) {
            if (!item.reviewed) followUpsContainer.addView(followUpCard(item));
        }

        questionsEmpty.setVisibility(data.questions.isEmpty() ? View.VISIBLE : View.GONE);
        List<DoctorDashboardRepository.PatientQuestion> questions = new ArrayList<>(data.questions);
        questions.sort(Comparator.comparingInt(q -> q.answer.isEmpty() ? 0 : 1));
        for (DoctorDashboardRepository.PatientQuestion item : questions) {
            questionsContainer.addView(questionCard(item));
        }

        tipsEmpty.setVisibility(data.healthTips.isEmpty() ? View.VISIBLE : View.GONE);
        for (DoctorDashboardRepository.HealthTip item : data.healthTips) {
            tipsContainer.addView(tipCard(item));
        }
    }

    private View questionCard(DoctorDashboardRepository.PatientQuestion item) {
        LinearLayout card = makeCard(0xFFFFFFFF, 0xFFD9E9E5);
        addText(card, item.category + " · " + item.authorLabel, 13, true, 0xFF0C514A);
        addText(card, item.question, 16, true, 0xFF143C38);
        if (item.answer.isEmpty()) {
            Button answerButton = makeButton("Answer question", 0xFF0C514A);
            answerButton.setOnClickListener(view -> showAnswerDialog(item));
            card.addView(answerButton);
        } else {
            addText(card, "Your answer: " + item.answer, 14, false, 0xFF334541);
            addText(card, "Answered in this preview", 12, true, 0xFF0C514A);
        }
        return card;
    }

    private void showAnswerDialog(DoctorDashboardRepository.PatientQuestion item) {
        EditText answer = new EditText(this);
        answer.setHint("Write your answer");
        answer.setSingleLine(false);
        answer.setMinLines(3);
        answer.setPadding(dp(16), dp(12), dp(16), dp(12));
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Answer patient question")
                .setMessage(item.question + "\n\nYour answer will be stored on this device until the database is connected.")
                .setView(answer)
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Save answer", null)
                .create();
        dialog.setOnShowListener(ignored -> dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                .setOnClickListener(view -> {
                    String text = answer.getText().toString().trim();
                    if (text.isEmpty()) {
                        answer.setError("Enter an answer.");
                        return;
                    }
                    dialog.dismiss();
                    repository.answerQuestion(providerId, item.id, text,
                            new DoctorDashboardRepository.Callback<DoctorDashboardRepository.PatientQuestion>() {
                                @Override public void onSuccess(DoctorDashboardRepository.PatientQuestion value) {
                                    runOnUiThread(() -> {
                                        Toast.makeText(DoctorDashboardActivity.this,
                                                "Answer saved locally for preview.", Toast.LENGTH_SHORT).show();
                                        refreshDashboard();
                                    });
                                }
                                @Override public void onError(String message) {
                                    runOnUiThread(() -> showError(message));
                                }
                            });
                }));
        dialog.show();
    }

    private View tipCard(DoctorDashboardRepository.HealthTip item) {
        LinearLayout card = makeCard(0xFFE8F5F3, 0xFFB8DEDA);
        addText(card, item.title, 16, true, 0xFF0C514A);
        addText(card, item.body, 14, false, 0xFF334541);
        return card;
    }

    private void showPostTipDialog() {
        LinearLayout fields = new LinearLayout(this);
        fields.setOrientation(LinearLayout.VERTICAL);
        fields.setPadding(dp(16), 0, dp(16), 0);
        EditText title = new EditText(this);
        title.setHint("Tip title");
        title.setSingleLine(true);
        fields.addView(title);
        EditText body = new EditText(this);
        body.setHint("Write a general health tip");
        body.setSingleLine(false);
        body.setMinLines(4);
        fields.addView(body);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Post a health tip")
                .setMessage("Preview only: this tip will be stored on this device, not published to patients yet.")
                .setView(fields)
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Save tip", null)
                .create();
        dialog.setOnShowListener(ignored -> dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                .setOnClickListener(view -> {
                    String cleanTitle = title.getText().toString().trim();
                    String cleanBody = body.getText().toString().trim();
                    if (cleanTitle.isEmpty()) {
                        title.setError("Enter a title.");
                        return;
                    }
                    if (cleanBody.isEmpty()) {
                        body.setError("Enter the tip text.");
                        return;
                    }
                    dialog.dismiss();
                    repository.publishHealthTip(providerId, cleanTitle, cleanBody,
                            new DoctorDashboardRepository.Callback<DoctorDashboardRepository.HealthTip>() {
                                @Override public void onSuccess(DoctorDashboardRepository.HealthTip value) {
                                    runOnUiThread(() -> {
                                        Toast.makeText(DoctorDashboardActivity.this,
                                                "Health tip saved locally for preview.", Toast.LENGTH_SHORT).show();
                                        refreshDashboard();
                                    });
                                }
                                @Override public void onError(String message) {
                                    runOnUiThread(() -> showError(message));
                                }
                            });
                }));
        dialog.show();
    }

    private View appointmentCard(DoctorDashboardRepository.AppointmentRequest item) {
        LinearLayout card = makeCard(0xFFFFFFFF, 0xFFD9E9E5);
        addText(card, item.patientName, 17, true, 0xFF143C38);
        String when = new SimpleDateFormat("EEE, d MMM · HH:mm", Locale.getDefault())
                .format(new Date(item.slotMillis));
        addText(card, when + "    •    Category " + item.priorityCategory, 14, true,
                categoryColor(item.priorityCategory));
        addText(card, item.reason, 14, false, 0xFF334541);
        String status = item.status == DoctorDashboardRepository.AppointmentStatus.PENDING
                ? "Pending doctor decision" : item.status == DoctorDashboardRepository.AppointmentStatus.CONFIRMED
                ? "Confirmed" : "Asked to choose another time";
        addText(card, status, 13, true, 0xFF0C514A);

        if (item.status == DoctorDashboardRepository.AppointmentStatus.PENDING) {
            Button confirm = makeButton("Confirm appointment", 0xFF0C514A);
            confirm.setOnClickListener(view -> new AlertDialog.Builder(this)
                    .setTitle("Confirm appointment?")
                    .setMessage(item.patientName + " · " + when)
                    .setNegativeButton("Cancel", null)
                    .setPositiveButton("Confirm", (dialog, which) ->
                            updateAppointment(item.id,
                                    DoctorDashboardRepository.AppointmentStatus.CONFIRMED, ""))
                    .show());
            card.addView(confirm);

            Button reschedule = makeButton("Ask to choose another time", 0xFF159EA5);
            reschedule.setOnClickListener(view -> requestAnotherTime(item));
            card.addView(reschedule);
        } else if (item.status == DoctorDashboardRepository.AppointmentStatus.RESCHEDULE_REQUESTED
                && !item.rescheduleMessage.isEmpty()) {
            addText(card, "Message: " + item.rescheduleMessage, 13, false, 0xFF52605D);
        }
        return card;
    }

    private void requestAnotherTime(DoctorDashboardRepository.AppointmentRequest item) {
        EditText message = new EditText(this);
        message.setSingleLine(false);
        message.setMinLines(2);
        message.setText("Please choose another available appointment time.");
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Request a different time")
                .setMessage("This message will be saved in the local preview. The database connection must deliver it to the patient.")
                .setView(message)
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Save request", null)
                .create();
        dialog.setOnShowListener(ignored -> dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                .setOnClickListener(view -> {
                    String note = message.getText().toString().trim();
                    if (note.isEmpty()) {
                        message.setError("Enter a message for the patient.");
                        return;
                    }
                    dialog.dismiss();
                    updateAppointment(item.id,
                            DoctorDashboardRepository.AppointmentStatus.RESCHEDULE_REQUESTED, note);
                }));
        dialog.show();
    }

    private void updateAppointment(String appointmentId,
                                   DoctorDashboardRepository.AppointmentStatus status,
                                   String message) {
        repository.changeAppointmentStatus(providerId, appointmentId, status, message,
                new DoctorDashboardRepository.Callback<DoctorDashboardRepository.AppointmentRequest>() {
                    @Override public void onSuccess(DoctorDashboardRepository.AppointmentRequest value) {
                        runOnUiThread(() -> {
                            Toast.makeText(DoctorDashboardActivity.this,
                                    "Decision saved on this device for preview.", Toast.LENGTH_LONG).show();
                            refreshDashboard();
                        });
                    }
                    @Override public void onError(String error) {
                        runOnUiThread(() -> showError(error));
                    }
                });
    }

    private View followUpCard(DoctorDashboardRepository.FollowUp item) {
        LinearLayout card = makeCard(0xFFFFF7ED, 0xFFF1DCC3);
        addText(card, item.patientName, 16, true, 0xFF5C391A);
        addText(card, "Category " + item.priorityCategory + " · " + item.reason,
                14, false, 0xFF554736);
        Button review = makeButton("Mark reviewed", 0xFF0C514A);
        review.setOnClickListener(view -> repository.markFollowUpReviewed(providerId, item.id,
                new DoctorDashboardRepository.Callback<DoctorDashboardRepository.FollowUp>() {
                    @Override public void onSuccess(DoctorDashboardRepository.FollowUp value) {
                        runOnUiThread(() -> {
                            Toast.makeText(DoctorDashboardActivity.this,
                                    "Reviewed locally for preview.", Toast.LENGTH_SHORT).show();
                            refreshDashboard();
                        });
                    }
                    @Override public void onError(String error) {
                        runOnUiThread(() -> showError(error));
                    }
                }));
        card.addView(review);
        return card;
    }

    private void searchVisiblePatients() {
        if (currentData == null) return;
        EditText query = new EditText(this);
        query.setSingleLine(true);
        query.setHint("Patient name");
        new AlertDialog.Builder(this)
                .setTitle("Find in dashboard")
                .setMessage("Search the patients shown in this preview. The full patient directory will use the database.")
                .setView(query)
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Search", (dialog, which) -> {
                    String term = query.getText().toString().trim().toLowerCase(Locale.ROOT);
                    if (term.isEmpty()) return;
                    List<String> matches = new ArrayList<>();
                    for (DoctorDashboardRepository.AppointmentRequest item : currentData.appointments) {
                        if (item.patientName.toLowerCase(Locale.ROOT).contains(term)) {
                            matches.add(item.patientName + " · Category " + item.priorityCategory);
                        }
                    }
                    for (DoctorDashboardRepository.FollowUp item : currentData.followUps) {
                        if (item.patientName.toLowerCase(Locale.ROOT).contains(term)) {
                            matches.add(item.patientName + " · Follow-up");
                        }
                    }
                    new AlertDialog.Builder(this)
                            .setTitle("Search results")
                            .setMessage(matches.isEmpty() ? "No matching patients on this dashboard."
                                    : android.text.TextUtils.join("\n", matches))
                            .setPositiveButton("Close", null).show();
                }).show();
    }

    private LinearLayout makeCard(int fill, int border) {
        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(dp(16), dp(12), dp(16), dp(16));
        GradientDrawable background = new GradientDrawable();
        background.setColor(fill);
        background.setCornerRadius(dp(16));
        background.setStroke(dp(1), border);
        card.setBackground(background);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(-1, -2);
        params.bottomMargin = dp(12);
        card.setLayoutParams(params);
        return card;
    }

    private void addText(LinearLayout parent, String text, int sp, boolean bold, int color) {
        TextView label = new TextView(this);
        label.setText(text);
        label.setTextSize(sp);
        label.setTextColor(color);
        if (bold) label.setTypeface(null, Typeface.BOLD);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(-1, -2);
        params.bottomMargin = dp(6);
        parent.addView(label, params);
    }

    private Button makeButton(String label, int tint) {
        Button button = new Button(this);
        button.setText(label);
        button.setAllCaps(false);
        button.setTextColor(0xFFFFFFFF);
        button.setBackgroundTintList(android.content.res.ColorStateList.valueOf(tint));
        button.setMinHeight(dp(48));
        return button;
    }

    private int categoryColor(int category) {
        if (category == 1) return 0xFF8E2A2A;
        if (category == 2) return 0xFF8A5B00;
        return 0xFF0C514A;
    }

    private int dp(int value) {
        return Math.round(value * getResources().getDisplayMetrics().density);
    }

    private void showError(String message) {
        new AlertDialog.Builder(this).setTitle("Could not update dashboard")
                .setMessage(message).setPositiveButton("OK", null).show();
    }
}
