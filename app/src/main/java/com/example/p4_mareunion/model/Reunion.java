package com.example.p4_mareunion.model;

import java.sql.Time;
import java.util.Date;
import java.util.List;

public class Reunion {
    private String room;
    private Time time;
    private String subject;
    private Date date;
    private List<String> participants;

    public Reunion(String room, Time time, String subject, List<String> participants, Date date) {
        this.room = room;
        this.time = time;
        this.subject = subject;
        this.participants = participants;
        this.date = date;
    }

    //region --------------------- Getters && Setters ------------------------

    public String getRoom() {
        return room;
    }

    public void setRoom(String localisation) {
        this.room = room;
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

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    //endregion ---------------------------------------------------------------

}
