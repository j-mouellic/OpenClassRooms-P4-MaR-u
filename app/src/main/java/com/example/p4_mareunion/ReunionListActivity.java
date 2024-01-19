package com.example.p4_mareunion;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.util.Pair;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ReunionListActivity extends AppCompatActivity implements ItemClickListener {

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
    public void onClickDeleteReunion(Reunion reunion) {
        reunionViewModel.deleteReunion(reunion);
    }


    //region ----- Fonctions liées au Menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_filter_options, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.sub_item_filtrer) {
            createAndShowPopUpFilter();
            return true;
        } else if (item.getItemId() == R.id.sub_item_reinitialiser) {
            reunionViewModel.resetFilterShowFullReunionList();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    //endregion



    //region ------ Popup de filtrage des réunions
    /**
     * Crée et affiche une fenêtre contextuelle (popup) pour filtrer les réunions.
     *
     * Cette méthode utilise un layout défini dans le fichier R.layout.popup_filter_reunion pour créer
     * une fenêtre contextuelle affichant des options de filtrage telles que la sélection de dates,
     * le choix d'heures, la sélection de salles de réunion, et des boutons d'action pour appliquer ou annuler le filtre.
     *
     * La fenêtre contextuelle est créée en utilisant un PopupWindow et est affichée en bas de l'écran.
     * Un dialogue de sélection de plage de dates est inclus, ainsi que des spinners pour choisir les heures
     * minimales et maximales, et une zone de texte multisélection pour choisir les salles de réunion.
     * Les options de filtrage sont ensuite appliquées à l'aide des méthodes du ViewModel (reunionViewModel).
     */
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
        ArrayAdapter<String> adapterMultiSelect = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, reunionViewModel.getUniqueMeetingRooms());
        multiSelectRoom.setAdapter(adapterMultiSelect);
        multiSelectRoom.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());

        // ----------------------- BUTTONS ----------------------
        TextView btnFiltrer, btnRetour;
        btnFiltrer = popupView.findViewById(R.id.buttonFilterAction);
        btnRetour = popupView.findViewById(R.id.buttonBackAction);

        btnFiltrer.setOnClickListener(view -> {
            String startDate = startDateString;
            String endDate = endDateString;

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

    /**
     * Affiche une boîte de dialogue de sélection de plage de dates avec le composant MaterialDatePicker.
     *
     * Cette méthode crée et affiche une boîte de dialogue permettant à l'utilisateur
     * de sélectionner une plage de dates. La plage sélectionnée est ensuite affichée dans
     * un TextView avec le format "Du [date début] au [date fin]".
     *
     * Note : La méthode utilise le format de date "dd/MM/yyyy" en français.
     **/
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
    //endregion
}