package com.example.schwartz.myapplication;

/**
 * Imports
 */
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.TextView;


/**
 * Fragment class that holds the image for the Home screen.
 */
public class HomeFragment extends Fragment implements View.OnClickListener{
    Fragment fragment;
    TextView number;
    Button numberPicker;

    /**
     * Empty Constructor
     */
    public HomeFragment() { }

    /**
     * Creates a new instance of the fragment
     * @param param1
     * @param param2
     * @return
     */
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
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
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
//        number = (TextView) view.findViewById(R.id.number);
        numberPicker = (Button) view.findViewById (R.id.numberPicker);
        numberPicker.setOnClickListener(this);
        return view;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        numberPickerDialog();
    }

    private void numberPickerDialog()
    {
        NumberPicker myNumberPicker = new NumberPicker(getActivity());
        final String[] values= {"Residence Hall", "Tour 2", "Tour 3", "Tour 4", "Tour 5"};
        myNumberPicker.setMaxValue(values.length-1);
        myNumberPicker.setMinValue(0);
        myNumberPicker.setDisplayedValues(values);
        myNumberPicker.setWrapSelectorWheel(true);
        NumberPicker.OnValueChangeListener myValChangedListener = new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                if(values[newVal] == "Residence Hall")
                {
                    fragment = new MapFragment();
                    loadFragment(fragment);
                } else {
                    fragment = new HomeFragment();
                    loadFragment(fragment);
                }
//                number.setText(values[newVal]);
            }
        };
        myNumberPicker.setOnValueChangedListener(myValChangedListener);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity()).setView(myNumberPicker);
        builder.setTitle("Please Select A Tour:");

        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.show();
    }


    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
