package com.example.p4_mareunion.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.p4_mareunion.api.FakeApiService;
import com.example.p4_mareunion.repository.ReunionRepository;

public class ViewModelFactory implements ViewModelProvider.Factory{
    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(ReunionViewModel.class)){

            ReunionRepository repository = ReunionRepository.getInstance(new FakeApiService());
            return (T) new ReunionViewModel(repository);
        }
        throw new IllegalArgumentException("Classe de ViewModel inconnue : " + modelClass.getName());
    }
}
