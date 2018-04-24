package com.example.schwartz.myapplication;

/**
 * Imports
 */

import android.app.Activity;
import android.app.Application;
import android.app.IntentService;
import android.content.Context;
import android.content.Intent;

import android.content.SharedPreferences;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingEvent;
import com.google.android.gms.location.LocationServices;


import junit.runner.BaseTestRunner;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import java.util.ArrayList;
import java.util.List;

import static android.app.PendingIntent.getActivity;


/**
 * Class that helps with the geofence
 */
public class GeoFenceHelperService extends IntentService {

    /**
     * Initiations
     */
    private final static String TAG = "GeoFenceHelperService";
    private final static String DEBUG_TAG = "GEOFENCEHELPERSERVICE";
    private File geoStrFile = new File("geoStrFile.txt");
    private String geoStr;

    /**
     * @param name
     */
    public GeoFenceHelperService(String name) {
        super(name);
    }
    public GeoFenceHelperService(){ super("DEFAULT");}
    /**
     * @param intent
     */
    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Log.d(TAG, "onHandleIntent: INSIDE GEOFENCEHELPERSERVICE");
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        if (geofencingEvent.hasError()) {
            return;
        }

        /**
         * Gets the transition types
         */
        int geofenceTransition = geofencingEvent.getGeofenceTransition();
        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER || geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT || geofenceTransition == Geofence.GEOFENCE_TRANSITION_DWELL) {
            Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            v.vibrate(500);


            Toast.makeText(this, "Geofence triggered", Toast.LENGTH_SHORT).show();

            Log.d(TAG, "onHandleIntent: " + geofencingEvent.getTriggeringGeofences().get(0).getRequestId());
            String dormVisited  = geofencingEvent.getTriggeringGeofences().get(0).getRequestId();
            //Log.d(TAG, "Shared Preferences: " + dormVisited);

//            SharedPreferences sharedPref = getSharedPreferences("isVisited",Context.MODE_PRIVATE);
//            SharedPreferences.Editor editor = sharedPref.edit();
//            editor.putString("dormVisited", dormVisited);
//            editor.apply();
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("dormVisited", dormVisited);
            editor.apply();
            Log.d(TAG,"GetSharedPreferences " + dormVisited);
            //getSharedPrefs("dormVisited");

            Intent i = new Intent(this, ARCameraActivity.class);
            i.putExtra("dormVisited", dormVisited);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            Log.d(TAG, i.toString());
            List<Geofence> triggeredFences = geofencingEvent.getTriggeringGeofences();
            List<String> fenceStr = new ArrayList<>();
            GeofencingClient geofencingClient = LocationServices.getGeofencingClient(this);
            for(int j = 0; j < triggeredFences.size(); j++) {
                fenceStr.add(triggeredFences.get(j).getRequestId());


            }
            geofencingClient.removeGeofences(fenceStr);
            startActivity(i);
        } else {
            Log.e(TAG, "ERROR IN ONHANDLEINTENT");

            /**
             * Gets the geofences that were triggered
             */
            List<Geofence> triggeredFences = geofencingEvent.getTriggeringGeofences();

        }

//            Intent i = new Intent(this, ARCameraActivity.class);
//            i.putExtra("dormVisited", geoStr);
//            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            startActivity(i);

        }


}
