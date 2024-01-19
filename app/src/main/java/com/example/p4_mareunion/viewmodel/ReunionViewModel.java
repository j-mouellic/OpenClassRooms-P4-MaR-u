package com.example.p4_mareunion.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.p4_mareunion.api.FakeApiService;
import com.example.p4_mareunion.model.Reunion;
import com.example.p4_mareunion.repository.ReunionRepository;

import java.sql.Time;
import java.util.ArrayList;
import java.util.List;

public class ReunionViewModel extends ViewModel {
    private ReunionRepository reunionRepository;
    public String[] trancheHoraires = {
            "7h00", "8h00", "9h00", "10h00", "11h00", "12h00",
            "13h00", "14h00", "15h00", "16h00", "17h00", "18h00", "19h00"
    };
    private boolean reunionListChanged = false;
    public LiveData<List<String>> participantsList;
    private MutableLiveData<List<Reunion>> reunionListLiveData;
    private MutableLiveData<List<String>> freeRoomsLiveData = new MutableLiveData<>();


    //region ---------------------- INIT ---------------------
    /**
     * Initialise la récupération des données de réunion, si elle n'a pas déjà été initialisée.
     *
     * Cette méthode vérifie si la LiveData (reunionListLiveData) a déjà été initialisée.
     * Si elle ne l'est pas, elle crée une instance du Repository (reunionRepository)
     * en utilisant un service API factice (FakeApiService) et obtient la LiveData contenant la liste des réunions.
     * Cette LiveData est ensuite assignée à la variable reunionListLiveData pour être observée par les composants de l'interface utilisateur.
     */
    public void init(){
        if ( reunionListLiveData != null){
            return;
        }
        reunionRepository = ReunionRepository.getInstance(new FakeApiService());
        reunionListLiveData = reunionRepository.getReunions();
    }
    //endregion


    //region ---------------------- GET DATA ----------------------
    public LiveData<List<Reunion>> getAllReunions(){
        return reunionListLiveData;
    }

    public LiveData<List<String>> getAllParticipants() {
        return participantsList = reunionRepository.getParticipants();
    }
    //endregion


    //region ------------------- CRUD METHODS ------------------
    public void deleteReunion(Reunion reunion) {
        reunionListChanged = true;
        reunionRepository.deleteReunion(reunion);
    }
    public void addReunion(String room, Time time, String subject, List<String> participants,String date){
        reunionListChanged = true;
        Reunion newReunion = new Reunion(room, time, subject, participants, date);
        reunionRepository.addReunion(newReunion);
    }
    //endregion


    //region ------------------- SPECIFIC METHODS ------------------

    public LiveData<List<Reunion>> resetFilterShowFullReunionList(){
        if(reunionListChanged){
           reunionListLiveData.setValue(reunionRepository.resetListReunion());
           return reunionListLiveData;
        }else{
            return reunionRepository.getReunions();
        }
    }
    public List<String> getUniqueMeetingRooms(){
        return reunionRepository.filterUniqueMeetingRooms();
    }
    /**
     * Filtre les réunions en fonction des critères spécifiés tels que la plage de dates, les heures minimales et maximales,
     * et les salles de réunion sélectionnées.
     *
     * Cette méthode prend en compte les critères de filtre fournis en paramètres et utilise le Repository (reunionRepository)
     * pour obtenir la liste filtrée des réunions. La liste résultante est ensuite stockée dans un objet LiveData
     * (reunionListLiveData) et renvoyée pour être observée par les composants de l'interface utilisateur.
     *
     * @param startDate Date de début de la plage de dates au format "dd/MM/yyyy".
     * @param endDate Date de fin de la plage de dates au format "dd/MM/yyyy".
     * @param minHour Heure minimale de la journée au format "hh".
     * @param maxHour Heure maximale de la journée au format "hh".
     * @param roomInput Chaîne de salles de réunion séparées par des virgules.
     * @return LiveData<List<Reunion>> contenant la liste filtrée des réunions.
     */
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

    /**
     * Vérifie si une adresse e-mail est valide en fonction d'un motif prédéfini.
     *
     * Cette méthode utilise un motif de validation spécifique qui permet uniquement
     * les adresses e-mail avec le domaine "lamzone.com". Elle renvoie true si l'adresse
     * e-mail correspond au motif, sinon elle renvoie false.
     *
     * @param email L'adresse e-mail à vérifier.
     * @return true si l'adresse e-mail est valide, false sinon.
     */
    public boolean isValidEmail(String email) {
        String emailPattern = "^[a-zA-Z0-9]+@lamzone\\.com$";
        return email.matches(emailPattern);
    }

    /**
     * Obtient la liste des salles de réunion disponibles à un certain moment et une certaine date.
     *
     * Cette méthode utilise le Repository (reunionRepository) pour récupérer la liste complète
     * de toutes les salles de réunion, ainsi que la liste des salles occupées à un moment et une date spécifiques.
     * Elle calcule ensuite les salles de réunion disponibles en soustrayant les salles occupées de la liste complète.
     * La liste résultante est stockée dans un objet LiveData (freeRoomsLiveData) et renvoyée pour être observée par les composants de l'interface utilisateur.
     *
     * @param time Objet Time représentant l'heure pour laquelle la disponibilité des salles est vérifiée.
     * @param date Date pour laquelle la disponibilité des salles est vérifiée au format "dd/MM/yyyy".
     * @return LiveData<List<String>> contenant la liste des salles de réunion disponibles.
     */
    public LiveData<List<String>> getFreeRooms(Time time, String date){
        List<String> allRooms = reunionRepository.getAllMeetingRooms();
        List<String> occupiedRooms = reunionRepository.findOccupiedRooms(time, date);
        List<String> freeRooms = new ArrayList<>(allRooms);
        freeRooms.removeAll(occupiedRooms);
        freeRoomsLiveData.setValue(freeRooms);
        return freeRoomsLiveData;
    }
    //endregion
}

