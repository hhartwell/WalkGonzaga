package com.example.schwartz.myapplication;

/**
 * Imports
 */
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

/**
 * Activity class that gets the Father Desmet's information
 */
public class FatherDesmetActivity extends AppCompatActivity {

    /**
     * Creates actions for this Activity
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_father_desmet);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("");
    }

    /**
     * Grabs the intent for the DesmetActivity.
     */
    private void gallery(){
        Intent intent = new Intent(this, DesmetActivity.class);
        startActivity(intent);
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
                gallery();
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
