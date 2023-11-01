package com.example.p4_mareunion.api;

import com.example.p4_mareunion.model.Reunion;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import com.github.javafaker.Faker;
public class FakeApiServiceGenerator {

    private static Faker faker = new Faker();

    static List<Reunion> generateReunions(){
        List<Reunion> meetings = new ArrayList<>();

        String[] roomNames = {"Salle A", "Salle B", "Salle C", "Salle D", "Salle E", "Salle F", "Salle G"};
        String[] subjects = {"Stratégie de vente", "Réunion Marketing", "Développement produit", "Analyse des performances", "Réunion client"};
        List<String> participants = generateSampleParticipants();

        Random random = new Random();

        for (int i = 0; i < 15; i++){
            String randomRoom = roomNames[random.nextInt(roomNames.length)];

            int randomHour = random.nextInt(10) + 8;
            Time time = new Time(randomHour, 0, 0);

            String subject = subjects[random.nextInt(subjects.length)];


            int NumberOfPartipantsPerReunion = 5;
            List<String> selectedParticipants = new ArrayList<>();

            for (int k = 0; k < NumberOfPartipantsPerReunion; k++) {
                int randomIndex = random.nextInt(participants.size());
                selectedParticipants.add(participants.get(randomIndex));
            }

            Reunion meeting = new Reunion(randomRoom, time, subject, selectedParticipants);
            meetings.add(meeting);
        }

        return meetings;
    }

    private static List<String> generateSampleParticipants(){
        List<String> sampleParticpants = new ArrayList<>();
        int quantityOfParticipants = 100;
        for (int j = 0; j < quantityOfParticipants; j++){
            String participantName = faker.name().fullName();
            sampleParticpants.add(participantName);
        }
        return sampleParticpants;
    }
}
