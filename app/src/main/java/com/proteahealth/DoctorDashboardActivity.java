package com.proteahealth;

import android.content.Intent;

import android.os.Bundle;

import android.text.TextUtils;

import android.view.View;

import android.widget.Button;

import android.widget.EditText;

import android.widget.ImageButton;

import android.widget.LinearLayout;

import android.widget.TextView;

import android.widget.Toast;

import androidx.activity.EdgeToEdge;

import androidx.appcompat.app.AlertDialog;

import androidx.appcompat.app.AppCompatActivity;

import androidx.core.graphics.Insets;

import androidx.core.view.ViewCompat;

import androidx.core.view.WindowInsetsCompat;

import androidx.viewpager2.widget.ViewPager2;

import java.text.SimpleDateFormat;

import java.util.ArrayList;

import java.util.Calendar;

import java.util.Comparator;

import java.util.Date;

import java.util.List;

import java.util.Locale;

import android.view.LayoutInflater;

import com.proteahealth.model.Featurebanner;

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

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.doctorRoot), (view, insets) -> {

            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());

            view.setPadding(bars.left, bars.top, bars.right, 0);

            return insets;

        });

        if (getSupportActionBar() != null) {

            getSupportActionBar().hide();

        }

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

        repository = new MockDoctorDashboardRepository(this);

        findViewById(R.id.btnSearchPatients).setOnClickListener(view -> searchVisiblePatients());

        findViewById(R.id.btnPostTip).setOnClickListener(view -> showPostTipDialog());

        setupNavigation();

        setupBanner();

    }

    private void setupNavigation() {

        ImageButton navDoctorProfile = findViewById(R.id.navDoctorProfile);

        ImageButton navDoctorPatients = findViewById(R.id.navDoctorPatients);

        ImageButton navDoctorHome = findViewById(R.id.navDoctorHome);

        ImageButton navDoctorAppointments = findViewById(R.id.navDoctorAppointments);

        ImageButton navDoctorQuestions = findViewById(R.id.navDoctorQuestions);

        navDoctorHome.setBackgroundResource(R.drawable.nav_icon_glow);

        navDoctorProfile.setOnClickListener(v -> {

            Intent intent = new Intent(DoctorDashboardActivity.this, ProfileActivity.class);

            startActivity(intent);

        });

        navDoctorPatients.setOnClickListener(v ->

                Toast.makeText(DoctorDashboardActivity.this, "Coming soon", Toast.LENGTH_SHORT).show()

        );

        navDoctorAppointments.setOnClickListener(v ->{

                Intent intent = new Intent(DoctorDashboardActivity.this, DoctorAppointmentsActivity.class);

        startActivity(intent);


    });

        navDoctorQuestions.setOnClickListener(v ->

                Toast.makeText(DoctorDashboardActivity.this, "Coming soon", Toast.LENGTH_SHORT).show()

        );

    }

    @Override

    protected void onResume() {

        super.onResume();

        if (repository != null) {

            refreshDashboard();

        }

    }

    private void refreshDashboard() {

        repository.load(providerId, new DoctorDashboardRepository.Callback<DoctorDashboardRepository.Dashboard>() {

            @Override

            public void onSuccess(DoctorDashboardRepository.Dashboard data) {

                runOnUiThread(() -> {

                    if (isFinishing() || isDestroyed()) return;

                    currentData = data;

                    render(data);

                });

            }

            @Override

            public void onError(String message) {

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
                    && date.get(Calendar.DAY_OF_YEAR) == now.get(Calendar.DAY_OF_YEAR)) {

                today++;

            }

        }

        int openFollowUps = 0;

        for (DoctorDashboardRepository.FollowUp item : data.followUps) {

            if (!item.reviewed) openFollowUps++;

        }

        appointmentsCount.setText(today + "\nRequests today");

        followUpsCount.setText(openFollowUps + "\nOpen follow-ups");

        // Retain the XML empty-state TextView at index 0 and clear dynamic views

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


    private View questionCard(

            DoctorDashboardRepository.PatientQuestion item) {

        View card = LayoutInflater.from(this).inflate(

                R.layout.item_docpatient_question, questionsContainer, false);

        TextView category =

                card.findViewById(R.id.tvQuestionCategory);

        TextView question =

                card.findViewById(R.id.tvPatientQuestion);

        Button answerButton =

                card.findViewById(R.id.btnAnswerQuestion);

        TextView savedAnswer =

                card.findViewById(R.id.tvSavedAnswer);

        TextView answeredLabel =

                card.findViewById(R.id.tvQuestionAnsweredLabel);

        category.setText(item.category + " · " + item.authorLabel);

        question.setText(item.question);

        if (item.answer == null || item.answer.trim().isEmpty()) {

            answerButton.setVisibility(View.VISIBLE);

            savedAnswer.setVisibility(View.GONE);

            answeredLabel.setVisibility(View.GONE);

            answerButton.setOnClickListener(view -> showAnswerDialog(item));

        } else {

            answerButton.setVisibility(View.GONE);

            savedAnswer.setText("Your answer: " + item.answer);

            savedAnswer.setVisibility(View.VISIBLE);

            answeredLabel.setText("Answered in this preview");

            answeredLabel.setVisibility(View.VISIBLE);

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

                                @Override

                                public void onSuccess(DoctorDashboardRepository.PatientQuestion value) {

                                    runOnUiThread(() -> {

                                        Toast.makeText(DoctorDashboardActivity.this,

                                                "Answer saved locally for preview.", Toast.LENGTH_SHORT).show();

                                        refreshDashboard();

                                    });

                                }

                                @Override

                                public void onError(String message) {

                                    runOnUiThread(() -> showError(message));

                                }

                            });

                }));

        dialog.show();

    }


    private View tipCard(DoctorDashboardRepository.HealthTip item) {

        View card = LayoutInflater.from(this).inflate(

                R.layout.item_docdash_healthtip, tipsContainer, false);

        TextView title = card.findViewById(R.id.tvHealthTipTitle);

        TextView body = card.findViewById(R.id.tvHealthTipBody);

        title.setText(item.title);

        body.setText(item.body);

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

                                @Override

                                public void onSuccess(DoctorDashboardRepository.HealthTip value) {

                                    runOnUiThread(() -> {

                                        Toast.makeText(DoctorDashboardActivity.this,

                                                "Health tip saved locally for preview.", Toast.LENGTH_SHORT).show();

                                        refreshDashboard();

                                    });

                                }

                                @Override

                                public void onError(String message) {

                                    runOnUiThread(() -> showError(message));

                                }

                            });

                }));

        dialog.show();

    }


    private View appointmentCard(

            DoctorDashboardRepository.AppointmentRequest item) {

        View card = LayoutInflater.from(this).inflate(

                R.layout.item_docdash_appointment, appointmentsContainer, false);

        TextView patientName = card.findViewById(R.id.tvPatientName);

        TextView appointmentTime = card.findViewById(R.id.tvAppointmentTime);

        TextView reason = card.findViewById(R.id.tvAppointmentReason);

        TextView status = card.findViewById(R.id.tvAppointmentStatus);

        TextView rescheduleMessage = card.findViewById(R.id.tvRescheduleMessage);

        Button confirm = card.findViewById(R.id.btnConfirmAppointment);

        Button reschedule = card.findViewById(R.id.btnRescheduleAppointment);

        patientName.setText(item.patientName);

        String when = new SimpleDateFormat(

                "EEE, d MMM · HH:mm", Locale.getDefault())

                .format(new Date(item.slotMillis));

        appointmentTime.setText(

                when + "    •    Category " + item.priorityCategory);

        appointmentTime.setTextColor(categoryColor(item.priorityCategory));

        reason.setText(item.reason);

        String statusText;

        if (item.status == DoctorDashboardRepository.AppointmentStatus.PENDING) {

            statusText = "Pending doctor decision";

        } else if (item.status ==

                DoctorDashboardRepository.AppointmentStatus.CONFIRMED) {

            statusText = "Confirmed";

        } else {

            statusText = "Asked to choose another time";

        }

        status.setText(statusText);

        if (item.status == DoctorDashboardRepository.AppointmentStatus.PENDING) {

            confirm.setVisibility(View.VISIBLE);

            reschedule.setVisibility(View.VISIBLE);

            confirm.setOnClickListener(view ->

                    new AlertDialog.Builder(this)

                            .setTitle("Confirm appointment?")

                            .setMessage(item.patientName + " · " + when)

                            .setNegativeButton("Cancel", null)

                            .setPositiveButton("Confirm", (dialog, which) ->

                                    updateAppointment(

                                            item.id,

                                            DoctorDashboardRepository.AppointmentStatus.CONFIRMED,

                                            ""))

                            .show());

            reschedule.setOnClickListener(view -> requestAnotherTime(item));

        } else {

            confirm.setVisibility(View.GONE);

            reschedule.setVisibility(View.GONE);

        }

        if (item.status ==

                DoctorDashboardRepository.AppointmentStatus.RESCHEDULE_REQUESTED
                && !item.rescheduleMessage.isEmpty()) {

            rescheduleMessage.setText("Message: " + item.rescheduleMessage);

            rescheduleMessage.setVisibility(View.VISIBLE);

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

                    @Override

                    public void onSuccess(DoctorDashboardRepository.AppointmentRequest value) {

                        runOnUiThread(() -> {

                            Toast.makeText(DoctorDashboardActivity.this,

                                    "Decision saved on this device for preview.", Toast.LENGTH_LONG).show();

                            refreshDashboard();

                        });

                    }

                    @Override

                    public void onError(String error) {

                        runOnUiThread(() -> showError(error));

                    }

                });

    }


    private View followUpCard(DoctorDashboardRepository.FollowUp item) {

        View card = LayoutInflater.from(this).inflate(

                R.layout.item_docdash_followup, followUpsContainer, false);

        TextView patientName =

                card.findViewById(R.id.tvFollowUpPatientName);

        TextView details =

                card.findViewById(R.id.tvFollowUpDetails);

        Button review =

                card.findViewById(R.id.btnMarkReviewed);

        patientName.setText(item.patientName);

        details.setText(

                "Category " + item.priorityCategory + " · " + item.reason);

        review.setOnClickListener(view ->

                repository.markFollowUpReviewed(

                        providerId,

                        item.id,

                        new DoctorDashboardRepository.Callback<DoctorDashboardRepository.FollowUp>() {

                            @Override

                            public void onSuccess(

                                    DoctorDashboardRepository.FollowUp value) {

                                runOnUiThread(() -> {

                                    Toast.makeText(

                                            DoctorDashboardActivity.this,

                                            "Reviewed locally for preview.",

                                            Toast.LENGTH_SHORT

                                    ).show();

                                    refreshDashboard();

                                });

                            }

                            @Override

                            public void onError(String error) {

                                runOnUiThread(() -> showError(error));

                            }

                        }));

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

                                    : TextUtils.join("\n", matches))

                            .setPositiveButton("Close", null).show();

                }).show();

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

        new AlertDialog.Builder(this)

                .setTitle("Could not update dashboard")

                .setMessage(message)

                .setPositiveButton("OK", null)

                .show();

    }

    private void setupBanner() {

        ViewPager2 banner3 = findViewById(R.id.banner3);

        if (banner3 != null) {

            String[] titles = {

                    "Health Care Tips",

                    "Appointments",

                    "Patient Dashboards",

                    "Patient Follow-ups",

                    "Q & A"

            };

            String[] descriptions = {

                    "Add patient care tips",

                    "Manage your daily schedule efficiently",

                    "Access your Patients Profiles easily",

                    "Follow up with patients",

                    "Answer Patient questions"

            };

            banner3.setAdapter(new Featurebanner(titles, descriptions));

        }

    }

}
