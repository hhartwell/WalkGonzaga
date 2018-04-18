package com.example.schwartz.myapplication;

/**
 * Imports
 */
import android.content.Intent;
import android.content.SharedPreferences;
import android.icu.text.SimpleDateFormat;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import java.util.Calendar;
import java.util.Date;

/**
 * Activity class that displays the history of steps
 */
public class HistoryDisplayActivity extends AppCompatActivity {

    /**
     * Creates actions for this Activity
     * @param savedInstanceState
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_display);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getGraph();
    }

    /**
     * Creates a graph and displays points based on the date and number of steps taken
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void getGraph(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar cal = Calendar.getInstance();
        String dateArr[] = new String[7];
        Date arrDate[] = new Date[7];

        /**
         * Gets the starting date
         */
        cal.add(Calendar.DATE, -7);

        /**
         * Loop that adds 1 day in each iteration
         */
        for(int i = 0; i< 7; i++){
            cal.add(Calendar.DATE, 1);
            dateArr[i] = sdf.format(cal.getTime());//key for shared preferences
            arrDate[i] = cal.getTime();
        }

        GraphView graph = findViewById(R.id.graph);

        /**
         * Steps for the last 7 days
         */
        LineGraphSeries<DataPoint> series = new LineGraphSeries<>(new DataPoint[] {
                new DataPoint(arrDate[0], getPreferences(dateArr[0])),//label date and steps by date
                new DataPoint(arrDate[1], getPreferences(dateArr[1])),
                new DataPoint(arrDate[2], getPreferences(dateArr[2])),
                new DataPoint(arrDate[3], getPreferences(dateArr[3])),
                new DataPoint(arrDate[4], getPreferences(dateArr[4])),
                new DataPoint(arrDate[5], getPreferences(dateArr[5])),
                new DataPoint(arrDate[6], getPreferences(dateArr[6]))
        });

        graph.addSeries(series);

        /**
         * Sets date label formatter
         */
        graph.getGridLabelRenderer().setLabelFormatter(new DateAsXAxisLabelFormatter(this));
        graph.getGridLabelRenderer().setNumHorizontalLabels(7);
        graph.getGridLabelRenderer().setHorizontalLabelsAngle(135);


        /**
         * Sets manual x bounds to have nice steps
         */
        graph.getViewport().setMinX(arrDate[0].getTime());
        graph.getViewport().setMaxX(arrDate[6].getTime());
        graph.getViewport().setMinY(0);
        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setYAxisBoundsManual(true);


        /**
         * As we use dates as labels, the human rounding to nice readable numbers is not necessary
         */
        graph.getGridLabelRenderer().setHumanRounding(false);
    }

    /**
     * Gets the shared preferences from the previous activity based on date
     * @param key the date that needs to be accessed
     * @return the number of steps taken on the specific day accessed
     */
    public int getPreferences(String key){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        return sharedPreferences.getInt(key, 0);
    }

    /**
     * Grabs the intent for the ProfileFragment.
     */
    public void toActivity(){
        Intent i= new Intent(this, ProfileFragment.class);
    }

    /**
     * Gets the functionality of the back button
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()){
            case android.R.id.home:
                toActivity();
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }




}