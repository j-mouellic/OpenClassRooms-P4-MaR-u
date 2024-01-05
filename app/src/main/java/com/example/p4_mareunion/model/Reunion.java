package com.example.p4_mareunion.model;

import android.graphics.Color;
import android.widget.ImageView;

import androidx.databinding.BindingAdapter;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class Reunion {
    private String localisation;
    private Time time;
    private String subject;
    private String date;
    private List<String> participants;

    public Reunion(String localisation, Time time, String subject, List<String> participants, String date) {
        this.localisation = localisation;
        this.time = time;
        this.subject = subject;
        this.participants = participants;
        this.date = date;
    }

    //region --------------------- Specific Methods ------------------------

    public String getTimeString(){
        String timeString = new SimpleDateFormat("HH'H'mm", Locale.getDefault()).format(time);
        return timeString;
    }

    public String showStringParticipants(){
        if (participants != null){
            StringBuilder builder = new StringBuilder();
            for (String participant : participants){
                builder.append(participant).append(", ");
            }
            String participantsString = builder.toString();
            return participantsString;
        }else{
            return "";
        }
    }
    //endregion ---------------------------------------------------------------



    //region --------------------- Generic Methods ------------------------

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

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    //endregion ---------------------------------------------------------------

}
