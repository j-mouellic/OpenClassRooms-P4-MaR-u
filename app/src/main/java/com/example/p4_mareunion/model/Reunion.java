package com.example.p4_mareunion.model;

import java.sql.Time;
import java.util.List;

public class Reunion {
    private String localisation;
    private Time time;
    private String subject;
    private List<String> participants;

    public Reunion(String localisation, Time time, String subject, List<String> participants) {
        this.localisation = localisation;
        this.time = time;
        this.subject = subject;
        this.participants = participants;
    }

    public String getLocalisation() {
        return localisation;
    }

    public void setLocalisation(String localisation) {
        this.localisation = localisation;
    }

    public Time getTime() {
        return time;
    }

    public void setTime(Time time) {
        this.time = time;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public List<String> getParticipants() {
        return participants;
    }

    public void setParticipants(List<String> participants) {
        this.participants = participants;
    }
}
