package com.example.p4_mareunion.repository;

import com.example.p4_mareunion.api.ApiService;
import com.example.p4_mareunion.model.Reunion;

import java.util.List;

public class ReunionRepository {

    private final ApiService apiService;

    public ReunionRepository(ApiService apiService) {
        this.apiService = apiService;
    }

    public List<Reunion> getReunions(){
        return apiService.getReunions();
    }

}
