package com.example.p4_mareunion;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.MultiAutoCompleteTextView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import com.example.p4_mareunion.databinding.ActivityAddNewReunionBinding;
import com.example.p4_mareunion.model.Reunion;
import com.example.p4_mareunion.repository.ReunionRepository;
import com.example.p4_mareunion.viewmodel.ReunionViewModel;
//mport com.example.p4_mareunion.viewmodel.ViewModelFactory;

import java.sql.Time;
import java.util.List;

public class AddNewReunionActivity extends AppCompatActivity {
    private ActivityAddNewReunionBinding binding;
    private ReunionViewModel reunionViewModel;
    private TimePicker timePicker;
    TextView buttonSaveReunion;
    MultiAutoCompleteTextView mutliSelection, multiSelectionRooms;
    private DatePicker datePicker;


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

        // View
        buttonSaveReunion = findViewById(R.id.saveButton);

        // TimePicker 24h Format
        timePicker = findViewById(R.id.timePickerReunion);
        timePicker.setIs24HourView(true);

        // DatePicker minDate
        datePicker = findViewById(R.id.datePickerReunion);
        datePicker.setMinDate(System.currentTimeMillis() - 1000);

        // MutliSeleciton rooms
        multiSelectionRooms = findViewById(R.id.multiSelectionRooms);
        ArrayAdapter<String> adapterRooms = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        reunionViewModel.getFreeRooms().observe(this, new Observer<List<String>>() {
            @Override
            public void onChanged(List<String> rooms) {
                for (String room : rooms){
                    adapterRooms.add(room);
                    adapterRooms.notifyDataSetChanged();
                }
            }
        });
        multiSelectionRooms.setAdapter(adapterRooms);
        multiSelectionRooms.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());

        // MultiSelection participants
        mutliSelection = findViewById(R.id.multiAutoCompleteTextView);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, reunionViewModel.getParticipantsChoice());
        mutliSelection.setAdapter(adapter);
        mutliSelection.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());
        mutliSelection.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String emailsText = charSequence.toString().trim();
                String[] emails = emailsText.split(",");

                for (String email : emails) {
                    if (!reunionViewModel.isValidEmail(email.trim())) {
                        mutliSelection.setError("Format requis : xxx@lamazone.com");
                        break;
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    public void addNewReunion(View view){
        reunionViewModel.addReunion();
        Intent backToHomeList = new Intent(this, MainActivity.class);
        this.startActivity(backToHomeList);
    }
}