package com.example.p4_mareunion.repository;


import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.p4_mareunion.api.ApiService;
import com.example.p4_mareunion.model.Reunion;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;

public class ReunionRepository {

    private final ApiService apiService;
    private static ReunionRepository instance;
    private MutableLiveData<List<Reunion>> reunions = new MutableLiveData<>();
    private MutableLiveData<List<String>> participants = new MutableLiveData<>();
    private List<Reunion> currentList;
    private Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("Europe/Paris"));


    //region ---------------------- CONSTRUCTOR ---------------------
    public ReunionRepository(ApiService apiService) {
        this.apiService = apiService;
    }

    //endregion


    //region ------------------ SINGLETON -----------------------------
    public static ReunionRepository getInstance(ApiService apiService){
        if (instance == null){
            instance = new ReunionRepository(apiService);
        }
        return instance;
    }

    //region ------------------- CRUD METHODS ------------------
    public LiveData<List<Reunion>> getReunions(){
        if (reunions.getValue() == null){
            reunions.setValue(apiService.getReunions());
        }
        return reunions;
    }
    public LiveData<List<String>> getParticipants() {
        if (participants.getValue() == null){
            participants.setValue(apiService.getParticipants());
        }
        return participants;
    }
    public List<String> getRooms(){ return apiService.getRooms(); }
    public List<String> getTimeSlots(){ return apiService.getTimeSlots(); }

    public void deleteReunion(Reunion reunion){
        apiService.deleteReunion(reunion);
        reunions.setValue(apiService.getReunions());
    }

    public void addReunion(String room, Time time, String subject, List<String> participants,Date date){
        if (time == null) {
            time = getActualTime();
        }
        if (date == null){
            date = getActualDate();
        }

        findNewParticipantEmail(participants);

        Reunion reunion = new Reunion(room, time, subject, participants, date);
        apiService.addReunion(reunion);
        reunions.setValue(apiService.getReunions());
    }


    //endregion


    //region ------------------- SPECIFIC METHODS ------------------
    /**
     * Initialise la récupération des données de réunion, si elle n'a pas déjà été initialisée.
     *
     * Cette méthode vérifie si la LiveData (reunionListLiveData) a déjà été initialisée.
     * Si elle ne l'est pas, elle crée une instance du Repository (reunionRepository)
     * en utilisant un service API factice (FakeApiService) et obtient la LiveData contenant la liste des réunions.
     * Cette LiveData est ensuite assignée à la variable reunionListLiveData pour être observée par les composants de l'interface utilisateur.
     */
    public List<String> filterUniqueMeetingRooms() {
        currentList = reunions.getValue();
        Set<String> uniqueRooms = new HashSet<>();
        for (Reunion r : currentList) {
            uniqueRooms.add(r.getRoom());
        }
        return new ArrayList<>(uniqueRooms);
    }


    /** --------------------- FIND NEW PARTICIPANT EMAIL -----------------------
     * Finds and adds new participant email addresses to the current list.
     *
     * This method checks a provided list of email addresses against the current list of participants.
     * Any new email addresses found are added to the current list of participants.
     *
     * @param listToCheck The list of email addresses to check for new participants.
     */
    private void findNewParticipantEmail(List<String> listToCheck) {
        List<String> currentList = participants.getValue();
        List<String> newEmails = new ArrayList<>();
        for (String email : listToCheck){
            if (!currentList.contains(email)){
                newEmails.add(email);
            }
        }
        currentList.addAll(newEmails);
        participants.setValue(currentList);
    }


    /** --------------------- GET FREE ROOMS -----------------------
     * Retrieves a list of available rooms for a given time and date.
     *
     * If the time or date is not provided, the current time and date are used.
     *
     * @param time The time for which to check room availability.
     * @param date The date for which to check room availability.
     * @return A list of available rooms.
     */
    public List<String> getFreeRooms(Time time, Date date){
        if (time == null) {
            time = getActualTime();
        }
        if (date == null){
            date = getActualDate();
        }

        List<Reunion> currentList = reunions.getValue();
        List<String> rooms = apiService.getRooms();
        List<String> occupiedRooms = new ArrayList<>();

        Calendar maxHour = Calendar.getInstance();
        maxHour.set(Calendar.HOUR_OF_DAY, time.getHours());
        maxHour.set(Calendar.MINUTE, time.getMinutes());
        maxHour.set(Calendar.SECOND, time.getSeconds());

        maxHour.add(Calendar.HOUR_OF_DAY, 1);

        for (Reunion reunion : currentList){
            if (reunion.getDate().equals(date) && reunion.getTime().after(time) && reunion.getTime().before(maxHour.getTime())){
                Log.i("ROOM", "salle occupée : " + reunion.getRoom());
                occupiedRooms.add(reunion.getRoom());
            }
        }
        rooms.removeAll(occupiedRooms);
        return rooms;
    }
    //endregion


    //region ------------ GET ACTUAL TIME && DATE ------------
    private Time getActualTime(){
        int h = calendar.get(Calendar.HOUR_OF_DAY);
        int m = calendar.get(Calendar.MINUTE);
        return new Time(h, m, 0);
    }

    private Date getActualDate(){
        return new Date();
    }
    //endregion
}
