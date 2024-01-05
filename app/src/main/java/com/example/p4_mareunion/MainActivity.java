package com.example.p4_mareunion;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.util.Pair;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.MultiAutoCompleteTextView;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.p4_mareunion.databinding.ActivityMainBinding;
import com.example.p4_mareunion.eventListener.ItemClickListener;
import com.example.p4_mareunion.model.Reunion;
import com.example.p4_mareunion.ui.ReunionListAdapter;
import com.example.p4_mareunion.viewmodel.ReunionViewModel;
//import com.example.p4_mareunion.viewmodel.ViewModelFactory;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements ItemClickListener {

    private ActivityMainBinding mainBinding;
    private RecyclerView recyclerView;
    private FloatingActionButton fabAddNewReunion;
    private ConstraintLayout layout;
    private ReunionListAdapter reunionAdapter;
    private ReunionViewModel reunionViewModel;
    private TextView textResultDate, btnSelectDate;

    private String startDateString, endDateString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Configuration Locale
        Configuration config = this.getResources().getConfiguration();
        config.setLocale(Locale.FRANCE);

        // Toolbar
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Ma Réu");

        // Binding
        mainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        // View
        layout = findViewById(R.id.constraintLayout);
        recyclerView = mainBinding.reunionRecyclerView;
        recyclerView.setLayoutManager(new LinearLayoutManager(this));



        // ViewModel
        reunionViewModel = new ViewModelProvider(this).get(ReunionViewModel.class);
        reunionViewModel.init();

        // Adapter
        reunionAdapter = new ReunionListAdapter(this);
        recyclerView.setAdapter(reunionAdapter);

        // Live Data
        reunionViewModel.getAllReunions().observe(this, new Observer<List<Reunion>>() {
            @Override
            public void onChanged(List<Reunion> reunions) {
                reunionAdapter.setReunions(reunions);
                reunionAdapter.notifyDataSetChanged();
            }
        });

        // Activity AddNewReunion on click
        fabAddNewReunion = findViewById(R.id.floatingActionButton);
        fabAddNewReunion.setOnClickListener(view -> {
            Intent addNewReunionActivity = new Intent(this, AddNewReunionActivity.class);
            this.startActivity(addNewReunionActivity);
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_filter_options, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        createAndShowPopUpFilter();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClickDeleteReunion(Reunion reunion) {
        reunionViewModel.deleteReunion(reunion);
    }


    private void createAndShowPopUpFilter(){
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.popup_filter_reunion, null);

        int width = ViewGroup.LayoutParams.MATCH_PARENT;
        int heigth = ViewGroup.LayoutParams.MATCH_PARENT;
        boolean focusable = true;

        PopupWindow popupWindow = new PopupWindow(popupView, width, heigth, focusable);
        layout.post(()->{
            popupWindow.showAtLocation(layout, Gravity.BOTTOM, 0, 0);
        });

        // --------------------- CUSTOM DATE PICKER -------------
        textResultDate = popupView.findViewById(R.id.textResultDate);
        btnSelectDate = popupView.findViewById(R.id.btnSelectDate);

        // TODO : ajouter un click listener
        btnSelectDate.setOnClickListener(view -> {
            getDatePickerDialog();
        });

        // ----------------------- SPINNERS ----------------------
        Spinner minHourSpinner = popupView.findViewById(R.id.minHourSpinner);
        Spinner maxHourSpinner = popupView.findViewById(R.id.maxHourSpinner);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, reunionViewModel.trancheHoraires);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        minHourSpinner.setAdapter(adapter);
        maxHourSpinner.setAdapter(adapter);

        // ----------------------- MULTISELECTION SALLE REUNION ----------------------
        MultiAutoCompleteTextView multiSelectRoom = popupView.findViewById(R.id.multiSelectRoom);
        ArrayAdapter<String> adapterMultiSelect = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, reunionViewModel.findAllReunionsRoom());
        multiSelectRoom.setAdapter(adapterMultiSelect);
        multiSelectRoom.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());

        // ----------------------- BUTTONS ----------------------
        TextView btnFiltrer, btnRetour;
        btnFiltrer = popupView.findViewById(R.id.buttonFilterAction);
        btnRetour = popupView.findViewById(R.id.buttonBackAction);

        btnFiltrer.setOnClickListener(view -> {
            // récupérer la valeur de la date
            String startDate = startDateString;
            String endDate = endDateString;

            Log.i("DEBUG", "intern clic filter, start = " + startDate);
            Log.i("DEBUG", "intern calendar, end = " + endDate);

            String selectedRooms = String.valueOf(multiSelectRoom.getText());
            String minHourSelected = minHourSpinner.getSelectedItem().toString();
            String maxHourSelected = maxHourSpinner.getSelectedItem().toString();
            reunionViewModel.filterReunionByHourAndRoom(startDate, endDate, minHourSelected, maxHourSelected, selectedRooms);
            popupWindow.dismiss();
        });

        btnRetour.setOnClickListener(view -> {
            popupWindow.dismiss();
        });
    }

    private void getDatePickerDialog() {

        MaterialDatePicker.Builder<Pair<Long, Long>> builder = MaterialDatePicker.Builder.dateRangePicker();
        builder.setTitleText("Selectionnez une date :");

        Calendar currentDate = Calendar.getInstance();
        Calendar endDateCalendar = (Calendar) currentDate.clone();
        endDateCalendar.add(Calendar.MONTH, 2);

        builder.setCalendarConstraints(new CalendarConstraints.Builder()
                .setStart(new Date().getTime())
                .setEnd(endDateCalendar.getTimeInMillis()).build());
        MaterialDatePicker<Pair<Long, Long>> datePicker = builder.build();

        Locale.setDefault(Locale.FRANCE);

        datePicker.addOnPositiveButtonClickListener(selection -> {

            Long startDate = selection.first;
            Long endDate = selection.second;

            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            startDateString = sdf.format(new Date(startDate));
            endDateString = sdf.format(new Date(endDate));

            String selectedDateRange = "Du " + startDateString + " au " + endDateString;

            textResultDate.setText(selectedDateRange);
        });

        datePicker.show(getSupportFragmentManager(), "DATE_PICKER");
    }
}