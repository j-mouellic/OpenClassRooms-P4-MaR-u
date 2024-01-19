package com.example.p4_mareunion;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.MultiAutoCompleteTextView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import com.example.p4_mareunion.databinding.ActivityAddNewReunionBinding;
import com.example.p4_mareunion.viewmodel.ReunionViewModel;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class AddNewReunionActivity extends AppCompatActivity {
    private ActivityAddNewReunionBinding binding;
    private ReunionViewModel reunionViewModel;
    TimePicker timePicker;
    DatePicker datePicker;
    Spinner roomSpinner;
    EditText subjectInput;
    MultiAutoCompleteTextView multiSelectionParticipants;
    TextView saveButton, backButton;
    String meetingRoom;
    Boolean subjectVerified = false;
    Boolean participantsVerfied = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // ViewModel
        reunionViewModel = new ViewModelProvider(this).get(ReunionViewModel.class);
        reunionViewModel.init();

        // Binding
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_new_reunion);
        binding.setViewModel(reunionViewModel);
        binding.setLifecycleOwner(this);

        // ViewBinding
        backButton = binding.backButton;
        saveButton = binding.saveButton;
        subjectInput = binding.subjectReunionText;
        timePicker = binding.timePickerReunion;
        datePicker = binding.datePickerReunion;
        roomSpinner = binding.spinnerRoomsSelection;
        multiSelectionParticipants = binding.multiSelectionParticipant;

        // Button
        backButton.setOnClickListener(view -> {
            backToMainActivity();
        });



        //region TimePicker / DatePicker Configuration
        timePicker.setIs24HourView(true);
        timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker timePicker, int i, int i1) {
                reunionViewModel.getFreeRooms(getTimeInput(), getDateInput());
            }
        });

        // DatePicker Configuration
        datePicker.setMinDate(System.currentTimeMillis() - 1000);
        datePicker.init(
                datePicker.getYear(),
                datePicker.getMonth(),
                datePicker.getDayOfMonth(),
                new DatePicker.OnDateChangedListener() {
                    @Override
                    public void onDateChanged(DatePicker datePicker, int i, int i1, int i2) {
                        reunionViewModel.getFreeRooms(getTimeInput(), getDateInput());
                    }
                }
        );
        //endregion

        //region ----- SpinnerRoom Configuration
        ArrayAdapter<String> adapterRooms = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        reunionViewModel.getFreeRooms(getTimeInput(), getDateInput()).observe(this, new Observer<List<String>>() {
            @Override
            public void onChanged(List<String> rooms) {
                Log.i("ROOM", "liste transmise : " + rooms.toString());
                adapterRooms.clear();
                for (String room : rooms){
                    adapterRooms.add(room);
                }
                adapterRooms.notifyDataSetChanged();
            }
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

        //region ----- Subject Input Configuration
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
                   enableSavingMeeting();
               }else {
                   subjectVerified = false;
                   enableSavingMeeting();
                   subjectInput.setError("Indiquer un sujet de réunion");
               }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        //endregion


        //region ----- MultiSelection Participants Configuration
        ArrayAdapter<String> adapterParticipants = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        reunionViewModel.getAllParticipants().observe(this, new Observer<List<String>>() {
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

                if (charSequence.length() <= 13){
                    multiSelectionParticipants.setError("Notez des emails Lamzone");
                    participantsVerfied = false;
                    enableSavingMeeting();
                }else{
                    multiSelectionParticipants.setError(null, null);
                    for (String email : emails) {
                        if (!reunionViewModel.isValidEmail(email.trim())) {
                            multiSelectionParticipants.setError("Format requis : xxx@lamzone.com");
                            participantsVerfied = false;
                            enableSavingMeeting();
                        }else{
                            participantsVerfied = true;
                            enableSavingMeeting();
                        }
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        //endregion
    }

    public void backToMainActivity(){
        Intent backActivity = new Intent(this, ReunionListActivity.class);
        this.startActivity(backActivity);
    }

    /** ------------------------------------ addNewReunion ------------------------------------
     * Ajoute une nouvelle réunion en utilisant les données fournies par les entrées de l'utilisateur.
     *
     * Cette méthode récupère l'heure, le sujet, la date, les participants et la salle de réunion à partir des entrées de l'utilisateur,
     * puis utilise le ViewModel de la réunion (reunionViewModel) pour ajouter la nouvelle réunion.
     *
     * Enfin, elle lance une intention (Intent) pour retourner à l'écran principal (MainActivity).
     **/

    public void addNewReunion(){
        Time time = getTimeInput();

        String subject = subjectInput.getText().toString();

        String date = getDateInput();

        String[] participantsFromInput = multiSelectionParticipants.getText().toString().split(",");
        List<String> participants = new ArrayList<>();
        for (String participant : participantsFromInput){
            participants.add(participant.trim());
        }

        String room = meetingRoom;

        reunionViewModel.addReunion(room, time, subject, participants, date);

        Intent backToHomeList = new Intent(this, ReunionListActivity.class);
        this.startActivity(backToHomeList);
    }

    /** -------------------------- enableSavingMeeting --------------------------------
     * Active ou désactive l'enregistrement d'une réunion en fonction de la vérification
     * de la présence des participants dans l'input et de la présence d'un sujet dans l'input.
     *
     * La réunion peut être enregistrée uniquement si les participants et le sujet sont vérifiés.
     *
     * Une fois les deux conditions vérifiées, le bouton de sauvegarde devient actif.
     * **/
    private void enableSavingMeeting(){
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

    /**
     * Récupère les informations d'heure à partir des composants de sélection de l'heure.
     *
     * Cette méthode extrait l'heure et les minutes actuelles du TimePicker et retourne un objet Time correspondant.
     *
     * @return Un objet Time représentant l'heure sélectionnée.
     */
    private Time getTimeInput(){
        int timeHour = timePicker.getCurrentHour();
        int timeMinute = timePicker.getCurrentMinute();
        return new Time(timeHour, timeMinute, 0);
    }

    /**
     * Récupère les informations de date à partir des composants de sélection de la date.
     *
     * Cette méthode extrait le jour, le mois et l'année actuels du DatePicker,
     * puis les utilise pour créer une chaîne de date au format "dd/MM/yyyy" en français.
     *
     * @return Une chaîne de date au format "dd/MM/yyyy".
     */
    private String getDateInput(){
        int dayDate = datePicker.getDayOfMonth();
        int monthDate = datePicker.getMonth();
        int yearDate = datePicker.getYear();

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, yearDate);
        calendar.set(Calendar.MONTH, monthDate);
        calendar.set(Calendar.DAY_OF_MONTH, dayDate);

        return new SimpleDateFormat("dd/MM/yyyy", Locale.FRENCH).format(calendar.getTime());
    }
}