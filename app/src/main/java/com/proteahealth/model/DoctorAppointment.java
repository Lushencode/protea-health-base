package com.proteahealth.model;

public class DoctorAppointment {
    private String time;
    private String patientName;
    private String appointmentType;
    private String status;
    private String notes;

    public DoctorAppointment(String time, String patientName, String appointmentType, String status) {
        this(time, patientName, appointmentType, status, "");
    }

    public DoctorAppointment(String time, String patientName, String appointmentType, String status, String notes) {
        this.time = time;
        this.patientName = patientName;
        this.appointmentType = appointmentType;
        this.status = status;
        this.notes = notes;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getPatientName() {
        return patientName;
    }

    public String getAppointmentType() {
        return appointmentType;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}

