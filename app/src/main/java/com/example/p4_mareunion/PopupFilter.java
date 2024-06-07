package com.example.p4_mareunion;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

import android.app.Activity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.MultiAutoCompleteTextView;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.util.Pair;
import androidx.fragment.app.FragmentManager;

import com.example.p4_mareunion.viewmodel.ReunionFilter;
import com.example.p4_mareunion.viewmodel.ReunionViewModel;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.MaterialDatePicker;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class PopupFilter {
    private Activity activity;
    private ReunionViewModel reunionViewModel;
    private FragmentManager fragmentManager;
    private ConstraintLayout layout;
    private TextView textResultDate, btnSelectDate;
    private String startDateString, endDateString;
    private final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

    public PopupFilter(Activity activity, ReunionViewModel reunionViewModel, FragmentManager fragmentManager, ConstraintLayout layout) {
        this.activity = activity;
        this.reunionViewModel = reunionViewModel;
        this.fragmentManager = fragmentManager;
        this.layout = layout;
    }

    /** --------------------------- INFLATE ---------------------------
     * Inflates a popup window for filtering meetings.
     *
     * This method inflates a popup window containing date pickers, spinners for selecting hours,
     * a multi-selection text view for selecting meeting rooms, and buttons for filtering or canceling.
     * It sets up listeners to handle user interactions and updates the ViewModel with selected filters.
     */
    public void inflate(){
        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.popup_filter_reunion, null);

        int width = ViewGroup.LayoutParams.MATCH_PARENT;
        int heigth = ViewGroup.LayoutParams.MATCH_PARENT;

        PopupWindow popupWindow = new PopupWindow(popupView, width, heigth, true);
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
        List<String> time_slots = reunionViewModel.getTimeSlots();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(activity, android.R.layout.simple_spinner_item, time_slots);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        minHourSpinner.setAdapter(adapter);
        maxHourSpinner.setAdapter(adapter);

        // ----------------------- MULTISELECTION SALLE REUNION ----------------------
        MultiAutoCompleteTextView multiSelectRoom = popupView.findViewById(R.id.multiSelectRoom);
        ArrayAdapter<String> adapterMultiSelect = new ArrayAdapter<>(activity, android.R.layout.simple_list_item_1, reunionViewModel.getRooms());
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

            ReunionFilter reunionFilter = new ReunionFilter();

            reunionFilter.setRooms(selectedRooms);
            reunionFilter.setMin_date(startDate);
            reunionFilter.setMax_date(endDate);
            reunionFilter.setMin_hour(minHourSelected);
            reunionFilter.setMax_hour(maxHourSelected);

            reunionViewModel.setReunion_filter(reunionFilter);
            popupWindow.dismiss();
        });

        btnRetour.setOnClickListener(view -> {
            popupWindow.dismiss();
        });
    }


    /** ---------------------- GET DATE PICKER DIALOG ----------------------
     * Displays a date picker dialog for selecting a date range.
     *
     * This method displays a MaterialDatePicker dialog for selecting a date range.
     * It restricts the date range to the current date to two months ahead.
     * When a date range is selected, it updates the selected date range in a text view.
     */
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

            startDateString = sdf.format(new Date(startDate));
            endDateString = sdf.format(new Date(endDate));

            String selectedDateRange = "Du " + startDateString + " au " + endDateString;

            textResultDate.setText(selectedDateRange);
        });

        datePicker.show(fragmentManager, "DATE_PICKER");
    }
}
