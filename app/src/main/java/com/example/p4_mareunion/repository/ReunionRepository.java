package com.example.p4_mareunion.repository;

import static org.apache.commons.lang3.time.DateUtils.parseDate;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.example.p4_mareunion.api.ApiService;
import com.example.p4_mareunion.model.Reunion;

import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ReunionRepository {

    private final ApiService apiService;
    private static ReunionRepository instance;
    private MutableLiveData<List<Reunion>> reunionList = new MutableLiveData<>();
    private MutableLiveData<List<Reunion>> updatedListForReset = new MutableLiveData<>();
    MutableLiveData<List<String>> participants = new MutableLiveData<>();
    private List<Reunion> currentList, filteredList, savedList;


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

    public MutableLiveData<List<Reunion>> getReunions(){
        if (reunionList.getValue() == null){
            reunionList.setValue(apiService.getReunions());
        }
        return reunionList;
    }

    public MutableLiveData<List<String>> getParticipants() {
        if (participants.getValue() == null){
            participants.setValue(apiService.getParticipants());
        }
        return participants;
    }



    //region ------------------- CRUD METHODS ------------------
    public void deleteReunion(Reunion reunion){
        currentList = reunionList.getValue();
        currentList.remove(reunion);
        reunionList.setValue(currentList);
        updatedListForReset.setValue(currentList);
    }

    public List<Reunion> resetListReunion(){
        savedList = updatedListForReset.getValue();
        return savedList;
    }

    public void addReunion(Reunion reunion){
        currentList = reunionList.getValue();
        List<String> listToCheck = reunion.getParticipants();
        findNewParticipantEmail(listToCheck);
        currentList.add(reunion);
        reunionList.setValue(currentList);
        updatedListForReset.setValue(currentList);
    }
    public List<String> getAllMeetingRooms(){
        return apiService.getRooms();
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
        currentList = reunionList.getValue();
        Set<String> uniqueRooms = new HashSet<>();
        for (Reunion r : currentList) {
            uniqueRooms.add(r.getLocalisation());
        }
        Log.i("DEBUG ROOM", "" + uniqueRooms);
        return new ArrayList<>(uniqueRooms);
    }


    /**
     * Recherche de nouveaux emails parmi la liste spécifiée et les ajoute à la liste actuelle des participants.
     *
     * Cette méthode prend en paramètre une liste d'emails à vérifier (listToCheck), puis récupère la liste actuelle
     * des participants à partir de la LiveData (participants). Elle compare chaque email de la liste à vérifier avec
     * la liste actuelle et ajoute les nouveaux emails (ceux qui ne sont pas déjà présents) à une nouvelle liste (newEmails).
     * Enfin, la liste actuelle des participants est mise à jour en ajoutant les nouveaux emails, et la LiveData est mise à jour avec la nouvelle liste.
     *
     * @param listToCheck Liste d'emails à vérifier et à ajouter à la liste des participants.
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


    /**
     * Filtre les réunions en fonction des critères spécifiés tels que la plage de dates, les heures minimales et maximales,
     * et les salles de réunion sélectionnées.
     *
     * Cette méthode prend en paramètres une plage de dates, des heures minimales et maximales, et une liste de salles de réunion.
     * Elle parcourt la liste actuelle des réunions à partir de la LiveData (reunionList) et filtre les réunions en fonction
     * des critères fournis. Les réunions qui correspondent aux critères sont ajoutées à une nouvelle liste (filteredList)
     * qui est renvoyée en tant que résultat.
     *
     * @param startDate Date de début de la plage de dates au format "dd/MM/yyyy".
     * @param endDate Date de fin de la plage de dates au format "dd/MM/yyyy".
     * @param minHour Heure minimale de la journée.
     * @param maxHour Heure maximale de la journée.
     * @param rooms Liste des salles de réunion sélectionnées.
     * @return Liste des réunions filtrées en fonction des critères spécifiés.
     */
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

    /**
     * Recherche les salles de réunion occupées à un moment et une date spécifiques.
     *
     * Cette méthode prend en paramètres une heure, une date, et récupère la liste actuelle des réunions
     * à partir de la LiveData (reunionList). Elle parcourt ensuite la liste des réunions et identifie les salles
     * de réunion qui sont occupées à l'heure spécifiée et à la date donnée. Les noms des salles de réunion occupées
     * sont ajoutés à une nouvelle liste (occupiedRooms) qui est renvoyée en tant que résultat.
     *
     * @param time Objet Time représentant l'heure pour laquelle la recherche de salles occupées est effectuée.
     * @param date Date pour laquelle la recherche de salles occupées est effectuée au format "dd/MM/yyyy".
     * @return Liste des noms des salles de réunion occupées à l'heure et la date spécifiées.
     */
    public List<String> findOccupiedRooms(Time time, String date){
        List<Reunion> currentList = reunionList.getValue();
        List<String> occupiedRooms = new ArrayList<>();

        Calendar maxHour = Calendar.getInstance();
        maxHour.set(Calendar.HOUR_OF_DAY, time.getHours());
        maxHour.set(Calendar.MINUTE, time.getMinutes());
        maxHour.set(Calendar.SECOND, time.getSeconds());

        maxHour.add(Calendar.HOUR_OF_DAY, 1);

        for (Reunion reunion : currentList){
            if (reunion.getDate().equals(date) && reunion.getTime().after(time) && reunion.getTime().before(maxHour.getTime())){
                Log.i("ROOM", "salle occupée : " + reunion.getLocalisation());
                occupiedRooms.add(reunion.getLocalisation());
            }
        }
        return occupiedRooms;
    }
    //endregion
}
