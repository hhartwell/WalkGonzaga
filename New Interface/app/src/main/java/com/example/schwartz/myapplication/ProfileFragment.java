package com.example.schwartz.myapplication;

/**
 * Imports
 */
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.icu.text.SimpleDateFormat;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.view.View;
import android.widget.TextView;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import java.util.Calendar;

/**
 * Fragment class that holds all of the interactions with the tours.
 */
public class ProfileFragment extends Fragment {
    /**
     * Initiations
     */
    protected TextView stepsTxt;
    private SensorManager manager;
    private int newSteps;

    /**
     * Empty Constructor
     */
    public ProfileFragment() { }

    /**
     * Creates a new instance of the fragment
     * @param param1
     * @param param2
     * @return
     */
    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Creates actions for this Activity
     * @param savedInstanceState
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    /**
     * Creates actions for this Activity
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return view
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        /**
         * Initiates text view: stepsTxt
         */
        stepsTxt = (TextView) view.findViewById(R.id.stepsText);
        manager = (SensorManager)getActivity().getSystemService(Context.SENSOR_SERVICE);
        getLast7Days();
        sensorRegister();

        /**
         * Initiates the button: fab
         */
        FloatingActionButton fab = (FloatingActionButton)view.findViewById(R.id.fab);

        /**
         * fab button listener
         */
        fab.setOnClickListener(new View.OnClickListener() {
            /**
             * Loads the HistoryDisplayActivity when clicked.
             * @param view
             */
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), HistoryDisplayActivity.class);
                startActivity(intent);
            }
        });
        return view;
    }

    /**
     * Registers the step counter and calls to shared preferences to store the steps
     */
    public void sensorRegister(){

        manager.registerListener(new SensorEventListener() {
            /**
             * WHAT DOES THIS DO? ***********************************************************************
             * @param event
             */
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onSensorChanged(SensorEvent event) {
                /**
                 * Steps saved
                 */
                int todayStep = getPreferences(dateTime());

                /**
                 * From previous session
                 */
                newSteps = todayStep + 1;
                writePreferenceSet(dateTime(), newSteps);

                /**
                 * Steps updated
                 */
                stepsTxt.setText(getPreferences(dateTime()) + "");
            }

            /**
             * WHAT DOES THIS DO? ***********************************************************************
             * @param sensor
             * @param accuracy
             */
            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

                }
                }, manager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER),
                SensorManager.SENSOR_DELAY_UI);
    }

    /**
     * Writes steps to shared preferences file
     * @param key the current date
     * @param value the number of steps counted for the date
     */
    public void writePreferenceSet(String key, int value){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(key, value);
        editor.apply();

    }

    /**
     * Gets the steps for today to shared preferences
     * @param key is the current date
     * @return returns the steps for the current date
     */
    public int getPreferences(String key){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        if(sharedPreferences.contains(key)) {
            return sharedPreferences.getInt(key, 0);
        } else return -1;
    }

    /**
     * Deletes all SharedPreferences
     */
    public void deletePreferences(){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }

    /**
     * Computes the current date
     * @return the current date
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    public String dateTime(){
        SimpleDateFormat date = new SimpleDateFormat("yyyy-MM-dd");
        return date.format(Calendar.getInstance().getTime());
    }

    /**
     * Saves the last 7 days keys and values to arrays
     * Deletes old preferences
     * Saves last 7 days to shared preferences
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void getLast7Days(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        /**
         * Today
         */
        Calendar cal = Calendar.getInstance();

        /**
         * Tomorrow
         */
        Calendar tomorrow = Calendar.getInstance();
        tomorrow.add(Calendar.DATE, 1);
        String tomorrowStr = sdf.format(tomorrow.getTime());

        String stepArr[]= new String[7];
        Integer intArr[] = new Integer[7];

        /**
         * Get starting date
         */
        cal.add(Calendar.DATE, -7);

        /**
         * Loop adding one day in each iteration
         * Assigns dates as preference keys to array
         * Gets preferences (steps) based on keys
         */
        for(int i = 0; i< 7; i++){
            cal.add(Calendar.DATE, 1);
            stepArr[i] = (sdf.format(cal.getTime()));//key for shared preferences
            intArr[i] = getPreferences(stepArr[i]);
        }

        /**
         * Delete all shared preferences and rewrite them
         */
        deletePreferences();
        for(int i = 0; i < 7; i++){
            writePreferenceSet(stepArr[i], intArr[i]);
        }
    }
}

