package com.example.p4_mareunion.viewmodel;

import androidx.lifecycle.MutableLiveData;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ReunionFilter {

    Date min_date;
    Date max_date;
    int min_hour;
    int max_hour;
    List<String> rooms;
    private final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

    public ReunionFilter() {
        super();
    }

    public void setMin_date(String min_date) {
        Date minDate;
        try {
            minDate = sdf.parse(min_date);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        this.min_date = minDate;
    }

    public void setMax_date(String max_date) {
        Date maxDate;
        try {
            maxDate = sdf.parse(max_date);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        this.max_date = maxDate;
    }

    public void setMin_hour(String min_hour) {
        int minHour = Integer.parseInt(min_hour.split("h")[0]);
        this.min_hour = minHour;
    }

    public void setMax_hour(String max_hour) {
        int maxHour = Integer.parseInt(max_hour.split("h")[0]);
        this.max_hour = maxHour;
    }

    public void setRooms(String rooms) {
        String[] roomsFromInput = rooms.split(",");
        List<String> roomsList = new ArrayList<>();

        for (String room : roomsFromInput) {
            roomsList.add(room.trim());
        }

        this.rooms = roomsList;
    }
}
