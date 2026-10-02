package com.proteahealth;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

/** Synthetic preview data. Decisions persist only on this device; no patient is notified. */
public class MockDoctorDashboardRepository implements DoctorDashboardRepository {
    public static final String DEMO_PROVIDER_ID = "provider-demo";
    private final SharedPreferences preferences;

    public MockDoctorDashboardRepository(Context context) {
        preferences = context.getSharedPreferences("doctor_dashboard_preview", Context.MODE_PRIVATE);
    }

    private long todayAt(int hour, int minute) {
        Calendar date = Calendar.getInstance();
        date.set(Calendar.HOUR_OF_DAY, hour);
        date.set(Calendar.MINUTE, minute);
        date.set(Calendar.SECOND, 0);
        date.set(Calendar.MILLISECOND, 0);
        return date.getTimeInMillis();
    }

    @Override
    public void load(String providerId, Callback<Dashboard> callback) {
        List<AppointmentRequest> appointments = new ArrayList<>();
        List<FollowUp> followUps = new ArrayList<>();
        List<PatientQuestion> questions = new ArrayList<>();
        List<HealthTip> healthTips = new ArrayList<>();
        if (DEMO_PROVIDER_ID.equals(providerId)) {
            appointments.add(appointment("demo-a1", "demo-p1", "Thabo Mokoena",
                    todayAt(14, 0), "Follow-up consultation", 1));
            appointments.add(appointment("demo-a2", "demo-p2", "Sarah Jenkins",
                    todayAt(14, 0), "Discuss low vitamin D and iron results", 3));
            appointments.add(appointment("demo-a3", "demo-p3", "Sipho Dlamini",
                    todayAt(15, 30), "Medication review", 2));

            followUps.add(followUp("demo-f1", "demo-p4", "Lerato Khumalo", 2,
                    "Review outstanding lab results"));
            followUps.add(followUp("demo-f2", "demo-p5", "Michael van der Merwe", 3,
                    "Discuss follow-up appointment"));

            questions.add(question("demo-q1", "Medication", "Anonymous patient",
                    "What should I do if I forget a dose of my medication?"));
            questions.add(question("demo-q2", "General health", "Lerato Khumalo",
                    "How can I prepare for my next blood pressure check?"));

            try {
                JSONArray saved = new JSONArray(preferences.getString("published_health_tips", "[]"));
                for (int i = saved.length() - 1; i >= 0; i--) {
                    JSONObject item = saved.getJSONObject(i);
                    healthTips.add(new HealthTip(item.getString("id"), item.getString("title"),
                            item.getString("body"), item.getLong("publishedAtMillis")));
                }
            } catch (JSONException exception) {
                callback.onError("Could not read saved preview tips. Clear app storage and try again.");
                return;
            }
        }
        callback.onSuccess(new Dashboard(appointments, followUps, questions, healthTips));
    }

    private PatientQuestion question(String id, String category, String authorLabel, String text) {
        return new PatientQuestion(id, category, authorLabel, text,
                preferences.getString("question_answer_" + id, ""));
    }

    private AppointmentRequest appointment(String id, String patientId, String name,
                                           long time, String reason, int category) {
        String raw = preferences.getString("appointment_status_" + id, "PENDING");
        AppointmentStatus status;
        try {
            status = AppointmentStatus.valueOf(raw);
        } catch (IllegalArgumentException exception) {
            status = AppointmentStatus.PENDING;
        }
        return new AppointmentRequest(id, DEMO_PROVIDER_ID, patientId, name, time,
                reason, category, status,
                preferences.getString("reschedule_message_" + id, ""));
    }

    private FollowUp followUp(String id, String patientId, String name,
                              int category, String reason) {
        return new FollowUp(id, patientId, name, category, reason,
                preferences.getBoolean("follow_up_reviewed_" + id, false));
    }

    @Override
    public void changeAppointmentStatus(String providerId, String appointmentId,
                                        AppointmentStatus newStatus, String rescheduleMessage,
                                        Callback<AppointmentRequest> callback) {
        if (!DEMO_PROVIDER_ID.equals(providerId)) {
            callback.onError("No preview appointments exist for this provider.");
            return;
        }
        if (newStatus != AppointmentStatus.CONFIRMED
                && newStatus != AppointmentStatus.RESCHEDULE_REQUESTED) {
            callback.onError("Unsupported decision.");
            return;
        }
        final AppointmentRequest[] selected = {null};
        final boolean[] conflict = {false};
        load(providerId, new Callback<Dashboard>() {
            @Override public void onSuccess(Dashboard data) {
                for (AppointmentRequest item : data.appointments) {
                    if (item.id.equals(appointmentId)) selected[0] = item;
                }
                if (selected[0] == null || selected[0].status != AppointmentStatus.PENDING) {
                    callback.onError("This request is no longer pending. Refresh the dashboard.");
                    return;
                }
                if (newStatus == AppointmentStatus.CONFIRMED) {
                    for (AppointmentRequest item : data.appointments) {
                        if (!item.id.equals(appointmentId)
                                && item.slotMillis == selected[0].slotMillis
                                && item.status == AppointmentStatus.CONFIRMED) {
                            conflict[0] = true;
                        }
                    }
                }
                if (conflict[0]) {
                    callback.onError("Another appointment is already confirmed at this time.");
                    return;
                }
                if (newStatus == AppointmentStatus.RESCHEDULE_REQUESTED
                        && (rescheduleMessage == null || rescheduleMessage.trim().isEmpty())) {
                    callback.onError("Enter a message asking the patient to choose another time.");
                    return;
                }
                preferences.edit()
                        .putString("appointment_status_" + appointmentId, newStatus.name())
                        .putString("reschedule_message_" + appointmentId,
                                rescheduleMessage == null ? "" : rescheduleMessage.trim())
                        .apply();
                AppointmentRequest old = selected[0];
                callback.onSuccess(new AppointmentRequest(old.id, old.providerId,
                        old.patientId, old.patientName, old.slotMillis, old.reason,
                        old.priorityCategory, newStatus,
                        rescheduleMessage == null ? "" : rescheduleMessage.trim()));
            }
            @Override public void onError(String message) { callback.onError(message); }
        });
    }

    @Override
    public void markFollowUpReviewed(String providerId, String followUpId,
                                     Callback<FollowUp> callback) {
        if (!DEMO_PROVIDER_ID.equals(providerId)) {
            callback.onError("No preview follow-ups exist for this provider.");
            return;
        }
        load(providerId, new Callback<Dashboard>() {
            @Override public void onSuccess(Dashboard data) {
                for (FollowUp item : data.followUps) {
                    if (item.id.equals(followUpId) && !item.reviewed) {
                        preferences.edit().putBoolean("follow_up_reviewed_" + followUpId, true).apply();
                        callback.onSuccess(new FollowUp(item.id, item.patientId,
                                item.patientName, item.priorityCategory, item.reason, true));
                        return;
                    }
                }
                callback.onError("This follow-up is not open. Refresh the dashboard.");
            }
            @Override public void onError(String message) { callback.onError(message); }
        });
    }

    @Override
    public void answerQuestion(String providerId, String questionId, String answer,
                               Callback<PatientQuestion> callback) {
        if (!DEMO_PROVIDER_ID.equals(providerId)) {
            callback.onError("No preview questions exist for this provider.");
            return;
        }
        String cleanAnswer = answer == null ? "" : answer.trim();
        if (cleanAnswer.isEmpty()) {
            callback.onError("Please enter an answer.");
            return;
        }
        load(providerId, new Callback<Dashboard>() {
            @Override public void onSuccess(Dashboard data) {
                for (PatientQuestion item : data.questions) {
                    if (item.id.equals(questionId)) {
                        if (!item.answer.isEmpty()) {
                            callback.onError("This question already has an answer.");
                            return;
                        }
                        preferences.edit().putString("question_answer_" + questionId, cleanAnswer).apply();
                        callback.onSuccess(new PatientQuestion(item.id, item.category,
                                item.authorLabel, item.question, cleanAnswer));
                        return;
                    }
                }
                callback.onError("Question not found. Refresh the dashboard.");
            }
            @Override public void onError(String message) { callback.onError(message); }
        });
    }

    @Override
    public void publishHealthTip(String providerId, String title, String body,
                                 Callback<HealthTip> callback) {
        if (!DEMO_PROVIDER_ID.equals(providerId)) {
            callback.onError("Preview tips are available only for the demo provider.");
            return;
        }
        String cleanTitle = title == null ? "" : title.trim();
        String cleanBody = body == null ? "" : body.trim();
        if (cleanTitle.isEmpty() || cleanBody.isEmpty()) {
            callback.onError("Enter both a title and the tip text.");
            return;
        }
        try {
            HealthTip tip = new HealthTip(UUID.randomUUID().toString(), cleanTitle,
                    cleanBody, System.currentTimeMillis());
            JSONArray saved = new JSONArray(preferences.getString("published_health_tips", "[]"));
            JSONObject item = new JSONObject();
            item.put("id", tip.id);
            item.put("title", tip.title);
            item.put("body", tip.body);
            item.put("publishedAtMillis", tip.publishedAtMillis);
            saved.put(item);
            preferences.edit().putString("published_health_tips", saved.toString()).apply();
            callback.onSuccess(tip);
        } catch (JSONException exception) {
            callback.onError("Could not save the preview tip.");
        }
    }
}
