package com.example.schwartz.myapplication;

/**
 * Imports
 */
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Fragment class that explains how to use the app to the user
 */
public class InstructionsFragment extends Fragment {

    /**
     * Empty Constructor
     */
    public InstructionsFragment() { }

    /**
     * Creates a new instance of the fragment
     * @param param1
     * @param param2
     * @return fragment
     */
    public static InstructionsFragment newInstance(String param1, String param2) {
        InstructionsFragment fragment = new InstructionsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Creates actions for this Activity
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * Creates actions for this Activity
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return inflater
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_instructions, container, false);
    }
}
