package com.example.p4_mareunion.api;


import com.example.p4_mareunion.model.Reunion;

import java.util.List;

public class FakeApiService implements ApiService {

    public FakeApiService() {
        /** KEEP EMPTY CONSTRUCTOR FOR UNIT TEST **/
    }

    private List<Reunion> reunions = FakeApiServiceGenerator.getReunions();
    private List<String> participants = FakeApiServiceGenerator.getParticipants();
    private List<String> rooms = FakeApiServiceGenerator.getRooms();
    private List<String> subjects = FakeApiServiceGenerator.getSubjects();
    private List<String> timeSlots = FakeApiServiceGenerator.getTimeSlots();


    //region ---------- 🟢   DATA GETTERS   🟢----------
    /**
     * Getter methods to retrieve various data:
     *
     * - getReunions() returns the list of reunions.
     * - getParticipants() returns the list of participants.
     * - getRooms() returns the list of available rooms.
     * - getSubjects() returns the list of available subjects.
     * - getTimeSlots() returns the list of available time slots.
     *
     * @return The requested data as described above.
     */
    @Override
    public List<Reunion> getReunions(){ return reunions; }
    @Override
    public List<String> getParticipants(){ return participants; }
    @Override
    public List<String> getRooms(){ return rooms; }
    @Override
    public List<String> getSubjects() { return subjects; }
    @Override
    public List<String> getTimeSlots() { return timeSlots; }
    //endregion



    //region ---------- 🟢   DATA CRUD METHODS   🟢 ----------
    /**
     * Methods to manipulate data:
     *
     * - deleteReunion(Reunion) removes a reunion from the list.
     * - addReunion(Reunion) adds a reunion to the list.
     * - addParticipants(List) adds a list of participants to the existing list.
     * - addParticipant(String) adds a participant to the existing list.
     * - addSubject(String) adds a subject to the existing list.
     */
    @Override
    public void deleteReunion(Reunion reunion){
        reunions.remove(reunion);
    }
    @Override
    public void addReunion(Reunion reunion){
        reunions.add(reunion);
    }
    @Override
    public void addParticipants(List<String> new_participants) { participants.addAll(new_participants); }
    @Override
    public void addParticipant(String participant) {
        participants.add(participant);
    }
    @Override
    public void addSubject(String subject) {
        subjects.add(subject);
    }
    //endregion
}
