package com.example.p4_mareunion.api;

import static com.example.p4_mareunion.api.FakeApiServiceGenerator.generateReunions;

import com.example.p4_mareunion.model.Reunion;

import java.util.List;

public class FakeApiService implements ApiService {

    private List<Reunion> reunions = generateReunions();

    // TODO : Voir avec Romain pourquoi les méthodes sont OverRide pour FakeApiService
    @Override
    public List<Reunion> getReunions(){
        return reunions;
    }
 }
