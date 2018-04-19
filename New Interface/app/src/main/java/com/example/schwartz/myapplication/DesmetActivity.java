package com.example.schwartz.myapplication;

/**
 * Imports
 */
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;

/**
 * Activity class that gets Desmet images
 */
public class DesmetActivity extends AppCompatActivity {
    final private String TAG = "DESMETACTIVITY";
    /**
     * Creates actions for this Activity
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_desmet);

        /**
         * Gets the back button
         */
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("");

        /**
         * Retrieves the image button resources.
         */
        ImageButton fatherDesmet = (ImageButton) findViewById(R.id.desmet1);

        /**
         * Looks for action for the image button: fatherDesmet
         */
        fatherDesmet.setOnClickListener(new View.OnClickListener() {
            /**
             * Opens up the FatherDesmetActivity with a click.
             * @param view
             */
            @Override
            public void onClick(View view) {
                // needs to be fixed to be a true back button
                Intent intent = new Intent(view.getContext(), FatherDesmetActivity.class);
                startActivity(intent);
            }
        });
    }

    /**
     * Grabs the intent for the GalleryFragment.
     */
    private void gallery(){
        Intent intent = new Intent(this, GalleryFragment.class);
    }

    /**
     * Initiates the back button action.
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Log.d(TAG, "back button?");
                Intent i = new Intent(this, MainActivity.class);
                i.putExtra("dormVisited", "Desmet Hall");
                startActivity(i);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        moveTaskToBack(true);
    }
}