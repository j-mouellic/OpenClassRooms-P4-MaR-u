package com.example.p4_mareunion.viewmodel;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.p4_mareunion.api.FakeApiService;
import com.example.p4_mareunion.model.Reunion;
import com.example.p4_mareunion.repository.ReunionRepository;

import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ReunionViewModel extends ViewModel {


    private Date rDate;
    private ReunionRepository repository;

    // LiveData LISTS
    public LiveData<List<String>> participants;
    private LiveData<List<Reunion>> reunions;

    // MutableLiveData LISTS
    private MediatorLiveData<List<Reunion>> filteredReunions;
    private MutableLiveData<List<String>> freeRoomsLiveData = new MutableLiveData<>();

    // Reunion Filter
    private MutableLiveData<ReunionFilter> reunion_filter = new MutableLiveData<>();


    //region ---------------------- INIT ---------------------
    public ReunionViewModel(ReunionRepository repository) {
        this.repository = repository;
        reunions = repository.getReunions();
        filteredReunions = new MediatorLiveData<>();
        observeReunionsChanges();
    }
    //endregion

    //region ---------------------- GET DATA ----------------------
    public LiveData<List<Reunion>> getReunions() {
        return filteredReunions;
    }

    public LiveData<List<String>> getParticipants() {
        return repository.getParticipants();
    }

    public List<String> getTimeSlots() {
        return repository.getTimeSlots();
    }

    public List<String> getRooms() {
        return repository.getRooms();
    }
    //endregion


    //region ------------------- CRUD METHODS ------------------
    public void deleteReunion(Reunion reunion) {
        repository.deleteReunion(reunion);
    }

    public void addReunion(String room, Time time, String subject, List<String> participants, Date date) {
        repository.addReunion(room, time, subject, participants, date);
    }
    //endregion


    //region ------------------- SPECIFIC METHODS ------------------

    /** ---------------- OBSERVE REUNIONS CHANGES ----------------
     * Observes changes in reunion data and applies filters accordingly.
     *
     * This method observes changes in reunion data and applies filters based on date, time, and room.
     * It updates the filtered list of reunions accordingly.
     */
    private void observeReunionsChanges() {

        // No Filter
        filteredReunions.addSource(reunions, reunionsList -> {
            List<Reunion> filtreredList = applyFilter(reunionsList);
            filteredReunions.setValue(filtreredList);
        });

        //Reunion Filter
        filteredReunions.addSource(reunion_filter, reunionFilter -> {
            List<Reunion> reunionList = reunions.getValue();
            if (reunionList != null) {
                List<Reunion> filteredList = applyFilter(reunionList);
                filteredReunions.setValue(filteredList);
            }
        });
    }

    /** ------------------------- APPLY FILTER --------------------------
     * Applies filters to a list of reunions and returns the filtered list.
     *
     * This method takes a list of reunions and applies filters based on minimum and maximum date,
     * minimum and maximum hour, and selected rooms. It returns the filtered list of reunions.
     *
     * @param reunionsList The list of reunions to filter.
     * @return The filtered list of reunions.
     */
    private List<Reunion> applyFilter(List<Reunion> reunionsList) {
        ReunionFilter reunionFilter = reunion_filter.getValue();

        Log.i("DEBUG", "Size list : " + reunionsList.size());

        List<Reunion> result = new ArrayList<>();

        if (reunionFilter != null) {
            for (Reunion r : reunionsList) {
                String rRoom = r.getRoom();
                int rHour = r.getTime().getHours();

                rDate = r.getDate();

                if (rDate.after(reunionFilter.min_date) && rDate.before(reunionFilter.max_date)) {
                    if (reunionFilter.rooms.contains(rRoom)) {
                        if (rHour >= reunionFilter.min_hour && rHour <= reunionFilter.max_hour) {
                            result.add(r);
                        }
                    }
                }
            }
        } else {
            Log.i("DEBUG", "Reinitialise liste");
            result.addAll(reunionsList);
        }
        return result;
    }

    /**
     * Setter method to update reunion filter value
     **/
    public void setReunion_filter(ReunionFilter reunionFilter){
        reunion_filter.setValue(reunionFilter);
    }


    /** ------------------------- GET FREE ROOMS -------------------------
     * Retrieves a LiveData object containing a list of free rooms for a given time and date.
     *
     * This method fetches the list of free rooms from the repository based on the provided time and date,
     * updates the LiveData object with the result, and returns the LiveData object.
     *
     * @param time The time for which to check room availability.
     * @param date The date for which to check room availability.
     * @return A LiveData object containing a list of free rooms.
     */
    public LiveData<List<String>> getFreeRooms(Time time, Date date) {
        List<String> freeRooms = repository.getFreeRooms(time, date);
        freeRoomsLiveData.setValue(freeRooms);
        return freeRoomsLiveData;
    }
    //endregion
}

