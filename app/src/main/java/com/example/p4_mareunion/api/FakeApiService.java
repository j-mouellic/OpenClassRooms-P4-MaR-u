package com.example.p4_mareunion.api;

import static com.example.p4_mareunion.api.FakeApiServiceGenerator.generateReunions;
import static com.example.p4_mareunion.api.FakeApiServiceGenerator.generateRooms;
import static com.example.p4_mareunion.api.FakeApiServiceGenerator.generateSampleParticipants;

import com.example.p4_mareunion.model.Reunion;

import java.util.List;

public class FakeApiService implements ApiService {

    private List<Reunion> reunions = generateReunions();
    private List<String> participants = generateSampleParticipants();
    private List<String> rooms = generateRooms();

    @Override
    public List<Reunion> getReunions(){
        return reunions;
    }

    @Override
    public List<String> getParticipants(){
        return participants;
    }

    @Override
    public List<String> getRooms(){ return rooms; };
 }
