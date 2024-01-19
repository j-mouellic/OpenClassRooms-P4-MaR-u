package com.example.p4_mareunion.api;

import androidx.lifecycle.LiveData;

import com.example.p4_mareunion.model.Reunion;

import java.util.List;

public interface ApiService {
    List<Reunion> getReunions();

    List<String> getParticipants();

    List<String> getRooms();
}
