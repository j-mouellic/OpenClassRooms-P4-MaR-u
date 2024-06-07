package com.example.p4_mareunion.api;

import com.example.p4_mareunion.model.Reunion;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import com.github.javafaker.Faker;
public class FakeApiServiceGenerator {

    private static Faker faker = new Faker();
    private static List<String> timeSlot = new ArrayList<>(Arrays.asList("7h00", "8h00", "9h00", "10h00", "11h00", "12h00", "13h00", "14h00", "15h00", "16h00", "17h00", "18h00", "19h00"));
    private static List<String> rooms = new ArrayList<>(Arrays.asList("Salle A", "Salle B", "Salle C", "Salle D", "Salle E", "Salle F", "Salle G", "Salle H", "Salle I", "Salle J"));
    private static List<String> subjects = new ArrayList<>(Arrays.asList("Analyse Concurrence", "Marketing", "Design Thinking", "Comptabilité", "Réunion client"));
    private static List<String> participants;
    private static List<Reunion> reunions;

    static {
        participants = generateSampleParticipants();
        reunions = generateReunions();
    }

    public static List<String> getRooms(){
        return rooms;
    }
    public static List<String> getSubjects(){ return subjects; }
    public static List<String> getParticipants(){ return participants; }
    public static List<Reunion> getReunions(){ return reunions; }
    public static List<String> getTimeSlots(){ return timeSlot; }


    /**
     * Generates a list of Reunion objects with random attributes.
     *
     * This method creates 15 reunions with random details such as room, time, subject,
     * participants, and date, and adds them to a list.
     *
     * @return A List of 15 randomly generated Reunion objects.
     */
    private static List<Reunion> generateReunions(){
        List<Reunion> meetings = new ArrayList<>();

        List<String> participants = generateSampleParticipants();

        Date currentDate = new Date();
        Random random = new Random();

        for (int i = 0; i < 15; i++){
            String randomRoom = rooms.get(random.nextInt(rooms.size()));

            int randomHour = random.nextInt(10) + 8;
            int randomMinutes = random.nextInt(12) * 5;
            Time time = new Time(randomHour,randomMinutes,0);

            String subject = subjects.get(random.nextInt(subjects.size()));

            int NumberOfPartipantsPerReunion = 5;
            List<String> selectedParticipants = new ArrayList<>();

            for (int k = 0; k < NumberOfPartipantsPerReunion; k++) {
                int randomIndex = random.nextInt(participants.size());
                selectedParticipants.add(participants.get(randomIndex));
            }

            int randomDays = random.nextInt(4);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(currentDate);
            calendar.add(Calendar.DAY_OF_MONTH, randomDays);
            Date randomDate = calendar.getTime();
            // String date = new SimpleDateFormat("dd/MM/yyyy", Locale.FRENCH).format(randomDate);

            Reunion meeting = new Reunion(randomRoom, time, subject, selectedParticipants, randomDate);
            meetings.add(meeting);
        }

        return meetings;
    }

    /**
     * Generates a list of sample participant email addresses.
     *
     * This method creates 100 sample participant email addresses using random first names
     * and adds them to a list.
     *
     * @return A List of 100 randomly generated participant email addresses.
     */
    private static List<String> generateSampleParticipants(){
        List<String> sampleParticpants = new ArrayList<>();
        int quantityOfParticipants = 350;
        for (int j = 0; j < quantityOfParticipants; j++){
            String participantName = faker.name().firstName()+"@lamzone.com";
            sampleParticpants.add(participantName);
        }
        return sampleParticpants;
    }
}
