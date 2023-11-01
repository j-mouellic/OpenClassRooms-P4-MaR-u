package com.example.p4_mareunion.di;

import com.example.p4_mareunion.api.FakeApiService;
import com.example.p4_mareunion.repository.ReunionRepository;

public class Injection {

    public static ReunionRepository createReunionRepository(){
        return new ReunionRepository(new FakeApiService());
    }
}
