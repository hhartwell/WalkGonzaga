package com.example.schwartz.myapplication;

import android.util.Log;
import android.widget.ImageButton;

import java.util.ArrayList;
import android.content.Context;

/**
 * Imports
 */

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.GridLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.concurrent.ExecutionException;

/**
 * Fragment class that displays the images for the gallery screen.
 */
public class GalleryFragment extends Fragment {

    final private String TAG = "GALLERYFRAGMENT";

    /**
     * Empty Constructor
     */
    public GalleryFragment() { }

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
        View rootView = inflater.inflate(R.layout.fragment_gallery, container, false);
        if (getActivity().getIntent().hasExtra("dormVisited")) {
            // the fragment has recognized that the intent has the dormvisited Stringextra attatched to it
            if (getActivity().getIntent().getExtras().getString("dormVisited") != null) {
                // the dormVisited string extra has been successfully filled and the gallery can display a gallery that corresponds to the correct dorm.
                Log.d(TAG, "intent recieved and dorm visited not null");

                // if we are currently visiting a dorm
                // the galleryFragment needs to display that dorm's gallery
                ArrayList<ImageButton> imageButtons = assignAllPicturesToGalleryFragment(determineDormResouceValue(savedInstanceState));
                GridLayout gridLayout = reformatView(rootView);
                for (int i = 0; i < imageButtons.size(); i++) {
                    gridLayout.addView(imageButtons.get(i));
                }
                assignOnClickListeners(imageButtons, getActivity().getIntent().getExtras().getString("dormVisited"));
                // remove the dormVisited field from the intent so that it wont crash when the fragment is started again.
                getActivity().getIntent().removeExtra("dormVisited");
                return rootView;
            }
            else{
                return rootView;
            }
        }
        else {
            Log.d(TAG, "saved instance state was null");
            // add all images into the fragment
            return rootView;
        }
    }
    private void assignOnClickListeners(ArrayList<ImageButton> imageButtons, String dormVisited){
        final String dorm = dormVisited;
        switch(dormVisited){
            case "Desmet Hall":
                imageButtons.get(0).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(getActivity(), DesmetActivity.class);
                        i.putExtra("dormVisited", dorm);
                        getActivity().startActivity(i);
                    }
                });
        }
    }
    /**
     * function used to reformat the gallery fragment view so that it can show a gallery.
     * Should only be running when coming from the ar camera scene.
     * @param rootView
     */
    private GridLayout reformatView(View rootView){
        // gets all the views we will be dealing with
        ScrollView scrollView = (ScrollView) rootView.findViewById(R.id.scroll_view);
        TextView textView = (TextView) rootView.findViewById(R.id.textView);
        GridLayout gridLayout = (GridLayout) rootView.findViewById(R.id.grid_layout);

        // remove the text view and gridlayout
        ((ViewGroup) textView.getParent()).removeView(textView);
        ((ViewGroup) gridLayout.getParent()).removeView(gridLayout);

        // create the new grid layout
        GridLayout newGridLayout = new GridLayout(getContext());
        newGridLayout.setColumnCount(2);
        newGridLayout.setColumnOrderPreserved(true);
        newGridLayout.setOrientation(GridLayout.HORIZONTAL);

        // format the existing scroll view to fill the whole screen
        scrollView.addView(newGridLayout);
        ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) scrollView.getLayoutParams();
        p.setMargins(0, 0, 0, 0);
        scrollView.setLayoutParams(p);

        // return the new gridlayout where images can be placed
        return newGridLayout;
    }

    /**
     * helper funciton to determine which dorm urls to pull based on the bundle that is recieved.
     * @param bundle
     * @return
     */
    private int determineDormResouceValue(Bundle bundle){
        Bundle bundle1 = this.getArguments();
        String dormVisited = bundle1.getString("dormVisited");
        Log.d(TAG, "dormVisited: " + dormVisited);
        switch(dormVisited){
            case "Desmet Hall":
                return R.array.desmet_gallery_url;
            default:
                return R.array.dorm_gallery_urls;
        }
    }
    /**
     * Helper function that adds all images from the WalkGU server to all
     * Gallery image buttons in the fragment
     */

    private ArrayList<ImageButton> assignAllPicturesToGalleryFragment(int dormurlsresouce) {
            ArrayList<ImageButton> imageButtons = new ArrayList<ImageButton>();
            ImageButton ib;
            for (int i = 0; i < getContext().getResources().getStringArray(dormurlsresouce).length; i++) {
                imageButtons.add(new ImageButton(getContext()));
            }
            assignDrawablesToImageButtons(imageButtons, getDormDrawables(getContext().getResources().getStringArray(dormurlsresouce)));
            return imageButtons;
    }

    /**
     * assigns drawables from the drawable array to the image buttons from the imagebuttons array
     * @param imageButtons
     * @param drawables
     */
    private void assignDrawablesToImageButtons(ArrayList<ImageButton> imageButtons, ArrayList<Drawable> drawables){
       ImageButton imageButton;
       Drawable drawable;
       for (int i = 0; i < drawables.size(); i++){
           imageButton = imageButtons.get(i);
           drawable = drawables.get(i);
           imageButton.setImageDrawable(drawable);
       }
    }

    /**
     * helper function to retrieve all the drawables for the dorms given an array of urls
     * returns a list of drawables
     * @param urls
     * @return
     */
    private ArrayList<Drawable> getDormDrawables(String ... urls){
        ArrayList<Drawable> drawables = new ArrayList<>();
        ProgressBar p = (ProgressBar) getActivity().findViewById(R.id.progressBar);
        try {
            drawables = new HTMLDrawableGetter(p).execute(urls).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return resizeDrawables(drawables);
    }

    /**
     * helper function that resizes all of the drawables.
     * resizing occures in the for loop.
     * this is necessary because the image that is retrieved from the server needs to be resized
     * @param drawables
     * @return
     */
    private ArrayList<Drawable> resizeDrawables(ArrayList<Drawable> drawables){
        ArrayList<Drawable> newDrawables = new ArrayList<Drawable>();
        Bitmap bitmap;
        WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int dimensions = size.x/2 - 65;
        for (int i = 0; i < drawables.size(); i++){
            bitmap = ((BitmapDrawable) drawables.get(i)).getBitmap();
            newDrawables.add(new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(bitmap, dimensions, dimensions, true)));
        }
        return newDrawables;
    }
}