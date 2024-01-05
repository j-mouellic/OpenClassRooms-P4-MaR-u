package com.example.p4_mareunion.api;

import android.util.Log;

import com.example.p4_mareunion.R;
import com.example.p4_mareunion.model.Reunion;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
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

    static List<Reunion> generateReunions(){
        List<Reunion> meetings = new ArrayList<>();

        String[] roomNames = {"Salle A", "Salle B", "Salle C", "Salle D", "Salle E", "Salle F", "Salle G"};
        String[] subjects = {"Analyse Concurrence", "Marketing", "Design Thinking", "Comptabilité", "Réunion client"};
        List<String> participants = generateSampleParticipants();

        Date currentDate = new Date();
        Random random = new Random();

        for (int i = 0; i < 15; i++){
            String randomRoom = roomNames[random.nextInt(roomNames.length)];

            int randomHour = random.nextInt(10) + 8;
            int randomMinutes = random.nextInt(12) * 5;
            Time time = new Time(randomHour,randomMinutes,0);

            String subject = subjects[random.nextInt(subjects.length)];

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
            String date = new SimpleDateFormat("dd/MM/yyyy", Locale.FRENCH).format(randomDate);

            Reunion meeting = new Reunion(randomRoom, time, subject, selectedParticipants, date);
            meetings.add(meeting);
        }

        return meetings;
    }

    static List<String> generateSampleParticipants(){
        List<String> sampleParticpants = new ArrayList<>();
        int quantityOfParticipants = 100;
        for (int j = 0; j < quantityOfParticipants; j++){
            String participantName = faker.name().firstName()+"@lamzone.com";
            sampleParticpants.add(participantName);
        }
        return sampleParticpants;
    }



}
