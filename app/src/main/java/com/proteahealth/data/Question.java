package com.proteahealth.data;

public class Question {

    private String patientName;
    private String question;
    private String date;
    private int responseCount;

    public Question(String patientName, String question,
                    String date, int responseCount) {

        this.patientName = patientName;
        this.question = question;
        this.date = date;
        this.responseCount = responseCount;
    }

    public String getPatientName() {
        return patientName;
    }

    public String getQuestion() {
        return question;
    }

    public String getDate() {
        return date;
    }

    public int getResponseCount() {
        return responseCount;
    }
}
