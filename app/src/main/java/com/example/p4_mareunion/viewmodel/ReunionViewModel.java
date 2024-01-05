package com.example.p4_mareunion.viewmodel;

import android.database.Observable;
import android.util.Log;

import androidx.databinding.ObservableField;
import androidx.databinding.ObservableInt;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.p4_mareunion.api.FakeApiService;
import com.example.p4_mareunion.model.Reunion;
import com.example.p4_mareunion.repository.ReunionRepository;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ReunionViewModel extends ViewModel {
    private ReunionRepository reunionRepository;
    public List<String> participantsChoice = new ArrayList<>();
    public String[] trancheHoraires = {
            "7h00", "8h00", "9h00", "10h00", "11h00", "12h00",
            "13h00", "14h00", "15h00", "16h00", "17h00", "18h00", "19h00"
    };

    private MutableLiveData<List<Reunion>> reunionListLiveData;
    private MutableLiveData<List<String>> freeRoomsLiveData = new MutableLiveData<>();

    // --------------------------- OBSERVABLES FOR ADD REUNION --------------------------
    public final ObservableField<String> participantsInput = new ObservableField<>("");
    public final ObservableInt dayInput = new ObservableInt();
    public final ObservableInt monthInput = new ObservableInt();
    public final ObservableInt yearInput = new ObservableInt();
    public final ObservableField<String> localisationInput = new ObservableField<>("");
    public final ObservableField<String> subjectInput = new ObservableField<>("");
    public final ObservableInt hourInput = new ObservableInt();
    public final ObservableInt minuteInput = new ObservableInt();


    public void init(){
        if ( reunionListLiveData != null){
            return;
        }
        reunionRepository = ReunionRepository.getInstance(new FakeApiService());
        reunionListLiveData = reunionRepository.getReunions();
    }


    //region ---------------------- GET DATA ----------------------
    public LiveData<List<Reunion>> getAllReunions(){
        return reunionListLiveData;
    }
    public List<String> getParticipantsChoice() {
        return participantsChoice = reunionRepository.getParticipants();
    }
    public List<String> findAllReunionsRoom(){
       return reunionRepository.findAllReunionsRoom();
    }
    //endregion

    //region ------------------- CRUD METHODS ------------------
    public void deleteReunion(Reunion reunion) {
        reunionRepository.deleteReunion(reunion);
    }
    public void addReunion(){
        String room = localisationInput.get();
        String subject = subjectInput.get();

        int dateDay = dayInput.get();
        int monthDay = monthInput.get();
        int yearDay = yearInput.get();

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, yearDay);
        calendar.set(Calendar.MONTH, monthDay - 1);
        calendar.set(Calendar.DAY_OF_MONTH, dateDay);

        String date = new SimpleDateFormat("dd/MM/yyyy", Locale.FRENCH).format(calendar.getTime());

        int timeHour = hourInput.get();
        int timeMinute = minuteInput.get();
        Time time = new Time(timeHour, timeMinute, 0);

        String[] participantsFromInput = participantsInput.get().split(",");
        List<String> participants = new ArrayList<>();
        for (String participant : participantsFromInput){
            participants.add(participant.trim());
        }

        Reunion newReunion = new Reunion(room, time, subject, participants, date);
        reunionRepository.addReunion(newReunion);
    }

    public LiveData<List<Reunion>> filterReunionByHourAndRoom(String startDate, String endDate, String minHour, String maxHour, String roomInput){
        String[] roomsFromInput = roomInput.split(",");
        List<String> rooms = new ArrayList<>();
        for (String room : roomsFromInput){
            rooms.add(room.trim());
        }

        int minHourInt = Integer.parseInt(minHour.split("h")[0]);
        int maxHourInt = Integer.parseInt(maxHour.split("h")[0]);

        reunionListLiveData.setValue(reunionRepository.filterReunionByHourAndRoom(startDate, endDate ,minHourInt, maxHourInt, rooms));
        return reunionListLiveData;
    }
    //endregion

    public boolean isValidEmail(String email) {
        String emailPattern = "^[a-zA-Z0-9]+@lamzone\\.com$";
        return email.matches(emailPattern);
    }

    public LiveData<List<String>> getFreeRooms(){
        int hour = hourInput.get();
        int minute = minuteInput.get();

        int day = dayInput.get();
        int month = monthInput.get();
        int year = yearInput.get();

        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);

        String date = new SimpleDateFormat("dd/MM/yyyy", Locale.FRENCH).format(calendar.getTime());

        List<String> allRooms = reunionRepository.findAllReunionsRoom();
        List<String> occupiedRooms = reunionRepository.findOccupiedRooms(date, hour, minute);

        List<String> freeRooms = new ArrayList<>(allRooms);
        freeRooms.removeAll(occupiedRooms);
        freeRoomsLiveData.setValue(freeRooms);
        return freeRoomsLiveData;
    }

}

