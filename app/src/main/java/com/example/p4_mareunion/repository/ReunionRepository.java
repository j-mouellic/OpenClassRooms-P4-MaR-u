package com.example.p4_mareunion.repository;

import static org.apache.commons.lang3.time.DateUtils.parseDate;

import androidx.lifecycle.MutableLiveData;

import com.example.p4_mareunion.api.ApiService;
import com.example.p4_mareunion.model.Reunion;

import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ReunionRepository {

    private final ApiService apiService;
    private static ReunionRepository instance;
    private MutableLiveData<List<Reunion>> reunionList = new MutableLiveData<>();
    private List<String> participants = new ArrayList<>();
    private List<Reunion> currentList, filteredList;


    public ReunionRepository(ApiService apiService) {
        this.apiService = apiService;
    }

    public static ReunionRepository getInstance(ApiService apiService){
        if (instance == null){
            instance = new ReunionRepository(apiService);
        }
        return instance;
    }

    public MutableLiveData<List<Reunion>> getReunions(){
        if (reunionList.getValue() == null){
            reunionList.setValue(apiService.getReunions());
        }
        return reunionList;
    }

    public List<String> getParticipants() {
        return participants = apiService.getParticipants();
    }

    public void deleteReunion(Reunion reunion){
        currentList = reunionList.getValue();
        currentList.remove(reunion);
        reunionList.setValue(currentList);
    }

    public void addReunion(Reunion reunion){
        currentList = reunionList.getValue();
        currentList.add(reunion);
        reunionList.setValue(currentList);
    }

    public List<String> findAllReunionsRoom() {
        currentList = reunionList.getValue();
        Set<String> uniqueRooms = new HashSet<>();
        for (Reunion r : currentList) {
            uniqueRooms.add(r.getLocalisation());
        }
        return new ArrayList<>(uniqueRooms);
    }

    public List<Reunion> filterReunionByHourAndRoom(String startDate, String endDate, int minHour, int maxHour, List<String> rooms){
        filteredList = new ArrayList<>();
        currentList = reunionList.getValue();

        for (Reunion reunion : currentList){
            String reunionRoom = reunion.getLocalisation();
            String reunionDate = reunion.getDate();
            int reunionHour = reunion.getTime().getHours();

            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            Date reunionDateFormat, startDateFormat, endDateFormat;

            try {
                reunionDateFormat = sdf.parse(reunionDate);
                startDateFormat = sdf.parse(startDate);
                endDateFormat = sdf.parse(endDate);
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }

            if (reunionDateFormat.after(startDateFormat) && reunionDateFormat.before(endDateFormat)){
                if (rooms.contains(reunionRoom)){
                    if (reunionHour >= minHour && reunionHour <= maxHour){
                        filteredList.add(reunion);
                    }
                }
            }
        }
        return filteredList;
    }

    public List<String> findOccupiedRooms(String date, int hour, int min){
        List<Reunion> currentList = reunionList.getValue();
        List<String> occupiedRooms = new ArrayList<>();
        Time time = new Time(hour,min,0);
        for (Reunion reunion : currentList){
            if (reunion.getDate().equals(date) && reunion.getTime().equals(time)){
                occupiedRooms.add(reunion.getLocalisation());
            }
        }
        return occupiedRooms;
    }
}
