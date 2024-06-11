package com.example.p4_mareunion.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.MultiAutoCompleteTextView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import com.example.p4_mareunion.R;
import com.example.p4_mareunion.viewmodel.ReunionViewModel;
import com.example.p4_mareunion.viewmodel.ViewModelFactory;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class AddNewReunionActivity extends AppCompatActivity {

    // DATA
    private ReunionViewModel reunionViewModel;

    // VIEWS
    Spinner roomSpinner;
    EditText subjectInput;
    MultiAutoCompleteTextView multiSelectionParticipants;
    TextView saveButton, backButton, timePicker, datePicker;

    // UTILS
    Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("Europe/Paris"));
    Time reunion_time;
    int year, month, day, currentHour, currentMinute;
    String meetingRoom;
    Date reunion_date;
    Boolean subjectVerified = false;
    Boolean participantsVerfied = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_reunion);

        // ViewModel
        ViewModelFactory factory = new ViewModelFactory();
        reunionViewModel = new ViewModelProvider(this, factory).get(ReunionViewModel.class);

        // ViewBinding
        backButton = findViewById(R.id.backButton);
        saveButton = findViewById(R.id.saveButton);
        subjectInput = findViewById(R.id.subjectReunionText);
        timePicker = findViewById(R.id.time);
        datePicker = findViewById(R.id.date);
        roomSpinner = findViewById(R.id.spinnerRoomsSelection);
        multiSelectionParticipants = findViewById(R.id.multiSelectionParticipant);

        // Current Date && Current Time
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);

        currentHour = calendar.get(Calendar.HOUR_OF_DAY);
        currentMinute = calendar.get(Calendar.MINUTE);

        String current_date = new SimpleDateFormat("dd/MM/yyyy", Locale.FRENCH).format(calendar.getTime());
        Time current_time = new Time(currentHour, currentMinute, 0);
        String timeString = new SimpleDateFormat("HH:mm", Locale.FRENCH).format(current_time);

        datePicker.setText(current_date);
        timePicker.setText(timeString);


        // Click Events
        timePicker.setOnClickListener(view -> {
            setTimePicker();
        });

        datePicker.setOnClickListener(view -> {
            setDatePicker();
        });

        backButton.setOnClickListener(view -> {
            backToMainActivity();
        });


        //region ---------- SpinnerRoom Configuration ----------

        ArrayAdapter<String> adapterRooms = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);

        reunionViewModel.getFreeRooms(reunion_time, reunion_date).observe(this, rooms -> {
            adapterRooms.clear();
            for (String room : rooms){
                adapterRooms.add(room);
            }
            adapterRooms.notifyDataSetChanged();
        });

        roomSpinner.setAdapter(adapterRooms);

        roomSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                meetingRoom = (String) adapterView.getItemAtPosition(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        //endregion



        //region ---------- Subject Input Configuration ----------

        subjectInput.setError("Indiquer un sujet de réunion");
        subjectInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
               if (charSequence.length() >= 3 ){
                   subjectInput.setError(null, null);
                   subjectVerified = true;
                   checkForenableSavingMeeting();
               }else {
                   subjectVerified = false;
                   checkForenableSavingMeeting();
                   subjectInput.setError("Indiquer un sujet de réunion");
               }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        //endregion


        //region ---------- MultiSelection Participants Configuration ----------

        ArrayAdapter<String> adapterParticipants = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        reunionViewModel.getParticipants().observe(this, new Observer<List<String>>() {
            @Override
            public void onChanged(List<String> participants) {
                adapterParticipants.clear();
                for (String email : participants){
                    adapterParticipants.add(email);
                }
                adapterParticipants.notifyDataSetChanged();
            }
        });
        multiSelectionParticipants.setAdapter(adapterParticipants);
        multiSelectionParticipants.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());
        multiSelectionParticipants.setError("Indiquez des participants");
        multiSelectionParticipants.addTextChangedListener(new TextWatcher() {
                @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String emailsText = charSequence.toString().trim();
                String[] emails = emailsText.split(",");

                for (String email : emails) {
                    if (!isValidEmail(email.trim())) {
                        multiSelectionParticipants.setError("Format requis : xxx@lamzone.com");
                        participantsVerfied = false;
                        checkForenableSavingMeeting();
                    }else{
                        participantsVerfied = true;
                        checkForenableSavingMeeting();
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }
    //endregion


    //region ---------- 📅 Zone Date et Time picker ⌚ ----------
    private void setDatePicker(){
        DatePickerDialog dialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker picker, int year, int month, int day) {

                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, day);

                Date reunion_date = calendar.getTime();

                reunionViewModel.getFreeRooms(reunion_time, reunion_date);

                String dateString = new SimpleDateFormat("dd/MM/yyyy", Locale.FRENCH).format(reunion_date);
                datePicker.setText(dateString);
            }
        }, year, month, day);
        dialog.show();
    }

    private void setTimePicker(){
        TimePickerDialog dialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker picker, int hour, int minute) {
                reunion_time =  new Time(hour, minute, 0);

                reunionViewModel.getFreeRooms(reunion_time, reunion_date);

                String reunion_hour = new SimpleDateFormat("HH:mm", Locale.FRENCH).format(reunion_time);
                timePicker.setText(reunion_hour);
            }
        }, currentHour, currentMinute, true);
        dialog.show();
    }
    //endregion


    /**-------------------------- ADD NEW REUNION --------------------------------
     * Adds a new reunion using user input data.
     *
     * This method retrieves subject, participants, and room information from user input,
     * adds the new reunion using the ViewModel, and navigates back to the home list.
     */
    public void addNewReunion(){
        String subject = subjectInput.getText().toString();

        String[] participantsFromInput = multiSelectionParticipants.getText().toString().split(",");
        List<String> participants = new ArrayList<>();
        for (String participant : participantsFromInput){
            participants.add(participant.trim());
        }

        String room = meetingRoom;

        reunionViewModel.addReunion(room, reunion_time, subject, participants, reunion_date);

        Intent backToHomeList = new Intent(this, ReunionListActivity.class);
        this.startActivity(backToHomeList);
    }

    /** -------------------------- ENABLE SAVING MEETING --------------------------------
     * Checks conditions for enabling saving a meeting.
     *
     * This method checks if participants and subject are verified.
     * If verified, it enables the save button and sets a click listener to add a new reunion.
     * If not verified, it disables the save button.
     * **/
    private void checkForenableSavingMeeting(){
        if (participantsVerfied && subjectVerified){
            saveButton.setBackgroundResource(R.drawable.button_valide_shape);
            saveButton.setClickable(true);
            saveButton.setOnClickListener(view -> {
                addNewReunion();
            });
        }else{
            saveButton.setBackgroundResource(R.drawable.button_unfocus_shape);
            saveButton.setClickable(false);
            saveButton.setOnClickListener(null);
        }
    }

    public boolean isValidEmail(String email) {
        String emailPattern = "^[a-zA-Z0-9]+@lamzone\\.com$";
        return email.matches(emailPattern);
    }

    private void backToMainActivity(){
        Intent backActivity = new Intent(this, ReunionListActivity.class);
        this.startActivity(backActivity);
    }

}