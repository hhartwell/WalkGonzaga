package com.example.schwartz.myapplication;

import android.app.Activity;
import com.gonzaga.walkgonzaga.UnityPlayerActivity;
/**
 * Imports
 */

import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;


import com.gonzaga.walkgonzaga.UnityPlayerActivity;

import java.lang.reflect.Field;

/**
 * Class that controls the AR Camera
 */
public class ARCameraActivity extends UnityPlayerActivity {
    final private String TAG = "UNITYPLAYERACTIVITY: ";
    private String dormVisited;
    /*
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        UnityFragment unityFragment = new UnityFragment();
        fragmentTransaction.add(R.id.linearLayout, unityFragment);
        fragmentTransaction.commit();
        setContentView(R.layout.activity_arcamera);
    */
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras.getString("dormVisited") == null) {
                Log.d(TAG, "activity launched without anything in the intent");
            } else {
                dormVisited = getIntent().getExtras().getString("dormVisited");
                Log.d(TAG, getIntent().getExtras().toString());
                Log.d(TAG, "extra received successfully by child: " + dormVisited);
            }
        }
        else{
            dormVisited = (String) savedInstanceState.getSerializable("dormVisited");
            Log.d(TAG, "saved instance state was null");
        }
    }



    /**
     * Opens up the GalleryFragment.
     */
    @Override
    protected void startNewActivity() {
        super.startNewActivity();
        Intent i = new Intent(this, MainActivity.class);
        i.putExtra("dormVisited", dormVisited);
        Log.d(TAG, i.toString());
        startActivity(i);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        finish();
    }
    @Override
    protected void onPause(){
        super.onPause();
        onDestroy();
    }
}