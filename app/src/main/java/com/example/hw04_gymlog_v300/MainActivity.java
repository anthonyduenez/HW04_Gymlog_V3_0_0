package com.example.hw04_gymlog_v300;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hw04_gymlog_v300.database.GymLogRepository;
import com.example.hw04_gymlog_v300.database.entities.GymLog;
import com.example.hw04_gymlog_v300.database.entities.User;
import com.example.hw04_gymlog_v300.databinding.ActivityMainBinding;
import com.example.hw04_gymlog_v300.viewHolders.GymLogAdapter;
import com.example.hw04_gymlog_v300.viewHolders.GymLogViewHolder;
import com.example.hw04_gymlog_v300.viewHolders.GymLogViewModel;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String MAIN_ACTIVITY_USER_ID = "com.daclink.gymlogpractice.MAIN_ACTIVITY_USER_ID";
    static final String SHARED_PREFERENCE_USER_ID_KEY = "com.daclink.gymlogpractice.SHARED_PREFERENCE_USER_ID_KEY";
    private static final int LOGGEDOUT = -1;
    private ActivityMainBinding binding;
    private GymLogRepository repository;
    private GymLogViewModel gymLogViewModel;
    public  static final String TAG = "DAC_GYMLOG";
    String exerciseName;
    double weight;
    int reps;
    private int loggedInUserId = -1;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
            binding = ActivityMainBinding.inflate(getLayoutInflater());
            setContentView(binding.getRoot());

            gymLogViewModel = new ViewModelProvider(this).get(GymLogViewModel.class);


            RecyclerView recyclerView = binding.logDisplayRecyclerView;
            final GymLogAdapter adapter = new GymLogAdapter(new GymLogAdapter.GymLogDiff());
            recyclerView.setAdapter(adapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));

            repository = GymLogRepository.getRepository(getApplication());
            loginUser(savedInstanceState);

            gymLogViewModel.getAllLogsById(loggedInUserId).observe(this, gymLogs -> {
                    adapter.submitList(gymLogs);
            });

            if(loggedInUserId == LOGGEDOUT){
                Intent intent = LoginActivity.loginIntentFactory(getApplicationContext());
                startActivity(intent);
            }

            updateSharedPreference();
            binding.logButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    getInformationFromDisplay();
                    insertGymLogRecord();
            }});
    }

    private void loginUser(Bundle savedInstanceState) {
        SharedPreferences sharedPreferences = getApplication().getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        loggedInUserId = sharedPreferences.getInt(getString(R.string.preference_userid_key), LOGGEDOUT);

        if(loggedInUserId == LOGGEDOUT && savedInstanceState != null && savedInstanceState.containsKey(SHARED_PREFERENCE_USER_ID_KEY)){
            loggedInUserId = savedInstanceState.getInt(SHARED_PREFERENCE_USER_ID_KEY, LOGGEDOUT);
        }
        if(loggedInUserId == LOGGEDOUT){
            loggedInUserId = getIntent().getIntExtra(MAIN_ACTIVITY_USER_ID, LOGGEDOUT);
        }
        if(loggedInUserId == LOGGEDOUT){
            return;
        }

        LiveData<User> userObserver = repository.findUserById(loggedInUserId);
        userObserver.observe(this, observedUser -> {
            if (observedUser != null) {
                user = observedUser;
                invalidateOptionsMenu();
            }
        });
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(SHARED_PREFERENCE_USER_ID_KEY, loggedInUserId);
        updateSharedPreference();
    }
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem item = menu.findItem(R.id.logoutMenuItem);
        item.setVisible(true);
        if(user == null){
            return false;
        }
        item.setTitle(user.getUsername());
        item.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(@NonNull MenuItem menuItem) {
                showLogOutDialog();
                return false;
            }
        });
        return true;
    }

    private void showLogOutDialog(){
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(MainActivity.this);
        final AlertDialog alertDialog = alertBuilder.create();
        alertBuilder.setMessage("Logout?");
        alertBuilder.setPositiveButton("Logout", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                logout();
            }
        });

        alertBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                alertDialog.dismiss();
            }
        });

        alertBuilder.create().show();
    }

    private void logout() {

        loggedInUserId = LOGGEDOUT;
        updateSharedPreference();

        getIntent().putExtra(MAIN_ACTIVITY_USER_ID, loggedInUserId);
        startActivity(LoginActivity.loginIntentFactory(getApplicationContext()));
    }

    private void updateSharedPreference(){
        SharedPreferences sharedPreferences = getApplication().getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(getString(R.string.preference_userid_key), loggedInUserId);
        editor.apply();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    static Intent mainIntentFactory(Context context, int userId){
        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra(MAIN_ACTIVITY_USER_ID, userId);
        return intent;
    }

    private void insertGymLogRecord(){
        if(exerciseName.isEmpty()){
            return;
        }
        GymLog log = new GymLog(exerciseName, weight, reps, loggedInUserId);
        repository.insertGymLog(log);
    }

    @Deprecated
    private void updateDisplay(){
        ArrayList<GymLog> logs = repository.getAllLogsByUserId(loggedInUserId);
        if(logs == null || logs.isEmpty()){
            return;
        }
        StringBuilder sb = new StringBuilder();
        for(GymLog log : logs){
            sb.append(log).append("\n\n");
        }
    }

    private void getInformationFromDisplay() {
        exerciseName = binding.exerciseInputEditText.getText().toString();
        try {
            weight = Double.parseDouble(binding.weightInputEditText.getText().toString());
        } catch (NumberFormatException e) {
            Log.d(TAG, "Error reading from weight edit text");
        }
        try {
            reps = Integer.parseInt(binding.repsInputEditText.getText().toString());
        } catch (NumberFormatException e) {
            Log.d(TAG, "Error reading from reps edit text");
        }
    }
}