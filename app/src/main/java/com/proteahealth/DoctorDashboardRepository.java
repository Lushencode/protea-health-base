package com.proteahealth;

import java.util.List;

/** Data contract for the doctor dashboard. An API implementation can replace the mock one. */
public interface DoctorDashboardRepository {

    enum AppointmentStatus {
        PENDING, CONFIRMED, RESCHEDULE_REQUESTED
    }

    class AppointmentRequest {
        public final String id;
        public final String providerId;
        public final String patientId;
        public final String patientName;
        public final long slotMillis;
        public final String reason;
        /** Assigned by a clinician or an agreed clinical workflow, never inferred by this screen. */
        public final int priorityCategory;
        public final AppointmentStatus status;
        public final String rescheduleMessage;

        public AppointmentRequest(String id, String providerId, String patientId,
                                  String patientName, long slotMillis, String reason,
                                  int priorityCategory, AppointmentStatus status,
                                  String rescheduleMessage) {
            this.id = id;
            this.providerId = providerId;
            this.patientId = patientId;
            this.patientName = patientName;
            this.slotMillis = slotMillis;
            this.reason = reason;
            this.priorityCategory = priorityCategory;
            this.status = status;
            this.rescheduleMessage = rescheduleMessage;
        }
    }

    class FollowUp {
        public final String id;
        public final String patientId;
        public final String patientName;
        public final int priorityCategory;
        public final String reason;
        public final boolean reviewed;

        public FollowUp(String id, String patientId, String patientName,
                        int priorityCategory, String reason, boolean reviewed) {
            this.id = id;
            this.patientId = patientId;
            this.patientName = patientName;
            this.priorityCategory = priorityCategory;
            this.reason = reason;
            this.reviewed = reviewed;
        }
    }

    class PatientQuestion {
        public final String id;
        public final String category;
        public final String authorLabel;
        public final String question;
        public final String answer;

        public PatientQuestion(String id, String category, String authorLabel,
                               String question, String answer) {
            this.id = id;
            this.category = category;
            this.authorLabel = authorLabel;
            this.question = question;
            this.answer = answer;
        }
    }

    class HealthTip {
        public final String id;
        public final String title;
        public final String body;
        public final long publishedAtMillis;

        public HealthTip(String id, String title, String body, long publishedAtMillis) {
            this.id = id;
            this.title = title;
            this.body = body;
            this.publishedAtMillis = publishedAtMillis;
        }
    }

    class Dashboard {
        public final List<AppointmentRequest> appointments;
        public final List<FollowUp> followUps;
        public final List<PatientQuestion> questions;
        public final List<HealthTip> healthTips;

        public Dashboard(List<AppointmentRequest> appointments, List<FollowUp> followUps,
                         List<PatientQuestion> questions, List<HealthTip> healthTips) {
            this.appointments = appointments;
            this.followUps = followUps;
            this.questions = questions;
            this.healthTips = healthTips;
        }
    }

    interface Callback<T> {
        void onSuccess(T value);
        void onError(String message);
    }

    void load(String providerId, Callback<Dashboard> callback);

    void changeAppointmentStatus(String providerId, String appointmentId,
                                 AppointmentStatus newStatus, String rescheduleMessage,
                                 Callback<AppointmentRequest> callback);

    void markFollowUpReviewed(String providerId, String followUpId,
                              Callback<FollowUp> callback);

    void answerQuestion(String providerId, String questionId, String answer,
                        Callback<PatientQuestion> callback);

    void publishHealthTip(String providerId, String title, String body,
                          Callback<HealthTip> callback);
}
