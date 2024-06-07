package com.example.p4_mareunion;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;
import static org.hamcrest.core.IsCollectionContaining.hasItems;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import com.example.p4_mareunion.api.FakeApiService;
import com.example.p4_mareunion.api.FakeApiServiceGenerator;
import com.example.p4_mareunion.model.Reunion;

import org.hamcrest.collection.IsIterableContainingInAnyOrder;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;

@RunWith(JUnit4.class)
public class UnitTest {

    private FakeApiService service;

    @Before
    public void setup() {
        service = new FakeApiService();
    }

    //region Test Method GET
    @Test
    public void getReunionsWithSuccess(){
        List<Reunion> reunions = service.getReunions();
        List<Reunion> expectedReunions = FakeApiServiceGenerator.getReunions();
        assertThat(reunions, containsInAnyOrder(expectedReunions.toArray()));
    }

    @Test
    public void getParticipantsWithSuccess(){
        List<String> participants = service.getParticipants();
        List<String> expectedParticipants= FakeApiServiceGenerator.getParticipants();
        assertThat(participants, containsInAnyOrder(expectedParticipants.toArray()));
    }

    @Test
    public void getRoomsWithSuccess(){
        List<String> rooms = service.getRooms();
        List<String> expectedRooms = FakeApiServiceGenerator.getRooms();
        assertThat(rooms, containsInAnyOrder(expectedRooms.toArray()));
    }

    @Test
    public void getSubjectsWithSuccess(){
        List<String> subjects = service.getSubjects();
        List<String> expectedSubjects = FakeApiServiceGenerator.getSubjects();
        assertThat(subjects, containsInAnyOrder(expectedSubjects.toArray()));
    }
    //endregion


    //region Test Method ADD
    @Test
    public void addReunionWithSuccess(){
        List<String> participants = new ArrayList<>();
        participants.add("john@lamzone.com");
        participants.add("mayble@lamzone.com");
        participants.add("claudia@lamzone.com");

        Random random = new Random();
        Date currentDate = new Date();

        int randomDays = random.nextInt(4);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentDate);
        calendar.add(Calendar.DAY_OF_MONTH, randomDays);
        Date randomDate = calendar.getTime();

        Reunion reunionToAdd = new Reunion("Salle A", new Time(10, 30, 0), "Réunion de projet", participants, randomDate);
        service.addReunion(reunionToAdd);
        assertTrue(service.getReunions().contains(reunionToAdd));
    }

    @Test
    public void addOnePartcipantWithSuccess(){
        String participantToAdd = "mattew@lamzone.com";
        service.addParticipant(participantToAdd);
        assertTrue(service.getParticipants().contains(participantToAdd));
    }

    @Test
    public void addMultiplePartcipantsWithSuccess(){
        List<String> participantsToAdd = new ArrayList<>();
        participantsToAdd.add("john@lamzone.com");
        participantsToAdd.add("mayble@lamzone.com");
        participantsToAdd.add("claudia@lamzone.com");

        int initialSize = service.getParticipants().size();

        service.addParticipants(participantsToAdd);

        List<String> allParticipants = service.getParticipants();
        assertThat(allParticipants.size(), equalTo(initialSize + participantsToAdd.size()));
    }

    @Test
    public void addSubjectWithSuccess(){
        String subjectToAdd = "Revue de code";
        service.addSubject(subjectToAdd);
        assertTrue(service.getSubjects().contains(subjectToAdd));
    }
    //endregion


    //region Test Method DELETE
    @Test
    public void deleteReunionWithSuccess() {
        Reunion reunionToDelete = service.getReunions().get(0);
        service.deleteReunion(reunionToDelete);
        assertFalse(service.getReunions().contains(reunionToDelete));
    }
    //endregion
}