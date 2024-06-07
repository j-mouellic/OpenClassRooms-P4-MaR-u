package com.example.p4_mareunion;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.util.Pair;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.Choreographer;
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
import com.example.p4_mareunion.viewmodel.ViewModelFactory;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ReunionListActivity extends AppCompatActivity implements ItemClickListener {

    private ActivityMainBinding binding;
    private RecyclerView recyclerView;
    private FloatingActionButton fabAddNewReunion;
    private ConstraintLayout layout;
    private ReunionListAdapter reunionAdapter;
    private ReunionViewModel reunionViewModel;
    private FragmentManager fragmentManager;
    private PopupFilter popupFilter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Configuration Locale
        Configuration config = this.getResources().getConfiguration();
        config.setLocale(Locale.FRANCE);

        // ViewModel
        ViewModelFactory factory = new ViewModelFactory();
        reunionViewModel = new ViewModelProvider(this, factory).get(ReunionViewModel.class);

        // View
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        layout = binding.constraintLayoutMainActivity;

        // PopupFilter
        fragmentManager = getSupportFragmentManager();
        popupFilter = new PopupFilter(this, reunionViewModel, fragmentManager, layout);

        // Toolbar
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Ma Réu");

        // RecyclerView
        recyclerView = binding.reunionRecyclerView;
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        reunionAdapter = new ReunionListAdapter(this);
        recyclerView.setAdapter(reunionAdapter);

        // Live Data
        reunionViewModel.getReunions().observe(this, new Observer<List<Reunion>>() {
            @Override
            public void onChanged(List<Reunion> reunions) {
                reunionAdapter.setReunions(reunions);
                reunionAdapter.notifyDataSetChanged();
            }
        });

        // Activity AddNewReunion on click
        fabAddNewReunion = binding.floatingActionButton;
        fabAddNewReunion.setOnClickListener(view -> {
            Intent addNewReunionActivity = new Intent(this, AddNewReunionActivity.class);
            this.startActivity(addNewReunionActivity);
        });
    }

    @Override
    public void onClickDeleteReunion(Reunion reunion) {
        reunionViewModel.deleteReunion(reunion);
    }


    //region ----- MENU OPTIONS -----
    /** -
     * Handles menu item selection.
     *
     * This method is called whenever an item in the options menu is selected.
     * It checks which item is selected and performs the appropriate action.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_filter_options, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.sub_item_filtrer) {
            popupFilter.inflate();
            return true;
        } else if (item.getItemId() == R.id.sub_item_reinitialiser) {
            reunionViewModel.setReunion_filter(null);
            reunionViewModel.getReunions();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    //endregion
}