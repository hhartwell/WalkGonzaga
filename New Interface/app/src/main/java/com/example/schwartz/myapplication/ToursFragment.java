package com.example.schwartz.myapplication;

/**
 * Imports
 */
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * Fragment class that holds all of the interactions with the tours
 */
public class ToursFragment extends Fragment {

    /**
     * Empty Constructor
     */
    public ToursFragment() { }

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
     * @return rootView
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_tours, container, false);

        /**
         * Initiates the button: desmet
         */
        Button desmet = (Button) rootView.findViewById(R.id.tour1);

        /**
         * desmet button listener
         */
        desmet.setOnClickListener(new View.OnClickListener() {
            Fragment fragment;

            /**
             * Loads the MapFragment when clicked.
             * @param view
             */
            @Override
            public void onClick(View view) {
                fragment = new MapFragment();
                loadFragment(fragment);

            }
        });
        return rootView;
    }

    /**
     * Loads the fragment
     * @param fragment
     */
    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

}

