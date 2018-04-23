package com.example.schwartz.myapplication;

/**
 * Imports
 */
import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import org.apache.commons.io.FileUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class MapFragment extends Fragment implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener, DirectionFinderListener,View.OnClickListener{
    private String TAG = "MapFragment";
    private GoogleMap mMap;


    // The entry point to the Fused Location Provider.
    private FusedLocationProviderClient mFusedLocationProviderClient;

    // A default location (Sydney, Australia) and default zoom to use when location permission is
    // not granted.
    private final LatLng mDefaultLocation = new LatLng(47.655, -117.455);//Herak Hall
    private static final int DEFAULT_ZOOM = 15;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private boolean mLocationPermissionGranted;

    // The geographical location where the device is currently located. That is, the last-known
    // location retrieved by the Fused Location Provider.
    private Location mLastKnownLocation;

    // Keys for storing activity state.
    private static final String KEY_LOCATION = "location";

    private LatLng currLatLng;
    private LatLng destLatLng;
    private ProgressDialog progressDialog;
    private List<Marker> originMarkers = new ArrayList<>();

    private ArrayList<LatLng> destinationPoint;
    private List<Polyline> polylinePaths = new ArrayList<>();
    private Marker mCurrLocationMarker;
    private String geoStr;
    private float closest = 1000000000;
    private LatLng closeLatLng;

    //variables for step counter
    private TextView count;
    private SensorManager manager;
    private Boolean zeroSteps;
    private float stepsDontCount;
    private float steps;
    private Geofence geofence;
    private PendingIntent pendingIntent;
    private ArrayList<Geofence> geofenceList;
    private String[] values = new String[]{"Library", "Alliance House", "Campion House", "Catherine Monica Hall",
            "Crimont Hall", "Desmet Hall", "Madonna Hall", "Rebmann",
            "Robinson", "Welch Hall"};

    // number picker variables
    Button numberPicker;
    SupportMapFragment mapFragment;

    public MapFragment() {
        // Required empty public constructor

    }

    @Override
    public void onStart(){
        super.onStart();
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this.getActivity());
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            mLastKnownLocation = savedInstanceState.getParcelable(KEY_LOCATION);
        }
        Log.d(TAG, "onCreate: RUNNING");
        destinationPoint = new ArrayList<>();
        //destinationPoint.add(new LatLng(47.667246,-117.401390)); // crosby
        //destinationPoint.add(new LatLng(47.655256, -117.463520));//Dani's house
        destinationPoint.add(new LatLng(47.666480, -117.400895));//Library
        destinationPoint.add(new LatLng(47.668670, -117.400111));//Alliance
        destinationPoint.add(new LatLng(47.668663, -117.401090));//Campion
        destinationPoint.add(new LatLng(47.665921, -117.397811));//Catherine/Monica
        destinationPoint.add(new LatLng(47.670186, -117.401682));//Crimont
        destinationPoint.add(new LatLng(47.667834, -117.401336));//DeSmet
        destinationPoint.add(new LatLng(47.666774, -117.397601));//Madonna
        destinationPoint.add(new LatLng(47.668509, -117.404707));//Rebmann
        destinationPoint.add(new LatLng(47.668507, -117.404350));//Robinson
        destinationPoint.add(new LatLng(47.667649, -117.400308));//Welsh
        super.onCreate(savedInstanceState);

    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_map, container, false);
        mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);

        Button btnFindNearest = view.findViewById(R.id.btnFindNearest);
        btnFindNearest.setOnClickListener(this);
//        Button btnFindPath = view.findViewById(R.id.btnFindPath);
//        btnFindPath.setOnClickListener(this);
//        getDeviceLocation();
        Log.d(TAG, "onCreateView: RUNNING");
        //sensor pedometer
        //count = view.findViewById(R.id.stepText);
        manager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
        zeroSteps = true;
        pedometer();
        CreateGeofenceToComplete();

        if(mapFragment == null)
        {
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            mapFragment = SupportMapFragment.newInstance();
            fragmentTransaction.replace(R.id.map, mapFragment).commit();
        }
        mapFragment.getMapAsync(this);

        // number picker
        numberPicker = view.findViewById(R.id.numberPicker);
        numberPicker.setOnClickListener(this);


        // fab used for usability testing purposes only
        FloatingActionButton fab = view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FloatingActionButton fab = view.findViewById(R.id.fab);
                fab.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        System.out.println("FAB CLICKED");
                        Intent i = new Intent(getActivity(), ARCameraActivity.class);
                        MapFragment.this.startActivity(i);
                        Log.d(TAG, i.toString());
                    }
                });
            }
        });

        return view;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        //Log.d(TAG, "onMapReady: ");
        /*
        //if we are keeping these markers, convert them to a for loop
        mMap.addMarker(new MarkerOptions().position(new LatLng(47.668670, -117.400111))
                .title("Alliance"));
        mMap.addMarker(new MarkerOptions()
                .position(new LatLng(47.668663, -117.401090))
                .title("Campion"));
        mMap.addMarker(new MarkerOptions()
                .position(new LatLng(47.665921, -117.397811))
                .title("Catherine Monica"));
        mMap.addMarker(new MarkerOptions()
                .position(new LatLng(47.670186, -117.401682))
                .title("Crimont"));
        mMap.addMarker(new MarkerOptions()
                .position(new LatLng(47.667834, -117.401336))
                .title("Desmet"));
        mMap.addMarker(new MarkerOptions()
                .position(new LatLng(47.666774, -117.397601))
                .title("Madonna"));
        mMap.addMarker(new MarkerOptions()
                .position(new LatLng(47.668509, -117.404707))
                .title("Rebmann"));
        mMap.addMarker(new MarkerOptions()
                .position(new LatLng(47.668507, -117.404350))
                .title("Robinson"));
        mMap.addMarker(new MarkerOptions()
                .position(new LatLng(47.667649, -117.400308))
                .title("Welch"));*/
        getLocationPermission();
        // Turn on the My Location layer and the related control on the map.
        updateLocationUI();
        getDeviceLocation();

        if (ActivityCompat.checkSelfPermission(this.getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this.getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(true);
    }
    // Container Activity must implement this interface
    public interface GeoFenceListener {

        public String onFragmentGetDestinations();
    }
    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.btnFindNearest:
                destLatLng = closeLatLng;
                sendRequest();
                break;
            case R.id.numberPicker:
                numberPickerDialog();

            default:
                break;
        }
    }
    private void numberPickerDialog()
    {
        NumberPicker myNumberPicker = new NumberPicker(getActivity());
        myNumberPicker.setMaxValue(values.length-1);
        myNumberPicker.setMinValue(0);
        myNumberPicker.setDisplayedValues(values);
        myNumberPicker.setWrapSelectorWheel(true);
        NumberPicker.OnValueChangeListener myValChangedListener = new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                numberPicker.setText(values[newVal]);
                // this is where we update the map
                destLatLng = destinationPoint.get(newVal);
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(destLatLng, 18));
                originMarkers.add(mMap.addMarker(new MarkerOptions().title(values[newVal]).position(destLatLng)));
                //Log.d(TAG, "onItemSelected: type" + values[newVal] + " Coordinates: " + destinationPoint.get(newVal).toString());
            }
        };
        myNumberPicker.setOnValueChangedListener(myValChangedListener);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity()).setView(myNumberPicker);
        builder.setTitle("Residence Halls");

        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                sendRequest();
            }
        });

        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.show();
    }

    private void getDeviceLocation() {

        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        try {
            if (mLocationPermissionGranted) {
                Task<Location> locationResult = mFusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener( new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful()) {
                            // Set the map's camera position to the current location of the device.
                            mLastKnownLocation = task.getResult();
                            currLatLng = new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude());
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                    new LatLng(mLastKnownLocation.getLatitude(),
                                            mLastKnownLocation.getLongitude()), DEFAULT_ZOOM));

                            //get nearest location
                            for(int i = 0; i < destinationPoint.size(); i++) {
                                // location and always closest
                                float[]results = new float[1];
                                Location.distanceBetween(mLastKnownLocation.getLatitude(),
                                        mLastKnownLocation.getLongitude(), destinationPoint.get(i).latitude,
                                        destinationPoint.get(i).longitude, results);

                                if(results[0] < closest){

                                    closest = results[0];
                                    closeLatLng = (destinationPoint.get(i));

                                }
                            }

                        } else {

                            currLatLng = mDefaultLocation;
                            mMap.moveCamera(CameraUpdateFactory
                                    .newLatLngZoom(mDefaultLocation, DEFAULT_ZOOM));
                            mMap.getUiSettings().setMyLocationButtonEnabled(false);
                        }
                    }
                });
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }
    /**
     * Updates the map's UI settings based on whether the user has granted location permission.
     */
    private void updateLocationUI() {
        if (mMap == null) {
            return;
        }
        try {
            if (mLocationPermissionGranted) {
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
            } else {
                mMap.setMyLocationEnabled(false);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);
                mLastKnownLocation = null;
                getLocationPermission();
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    private void sendRequest() {
        SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        String dormDefault = "none";
        geoStr = sharedPref.getString("isVisited", dormDefault);
        Log.d(TAG, "sendRequest: " + geoStr);
        double origLat = currLatLng.latitude;
        double origLong = currLatLng.longitude;
        double destLat = destLatLng.latitude;
        double destLong = destLatLng.longitude;
        String origin = origLat + "," + origLong;
        String destination = destLat + "," + destLong;
        //Log.d(TAG, "sendRequest: origin LatLong- "  + " destination LaatLng- " + destLatLng);
        try {
            new DirectionFinder(this, origin, destination).execute();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

    }
    @Override
    public void onDirectionFinderStart() {
        progressDialog = ProgressDialog.show(this.getActivity(), "Please wait.",
                "Finding direction..!", true);

        if (originMarkers != null) {
            for (Marker marker : originMarkers) {
                marker.remove();
            }
        }

        if (polylinePaths != null) {
            for (Polyline polyline:polylinePaths ) {
                polyline.remove();
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onDirectionFinderSuccess(List<Route> routes) {
        progressDialog.dismiss();
        polylinePaths = new ArrayList<>();

        for (Route route : routes) {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(route.startLocation, 18));

            PolylineOptions polylineOptions = new PolylineOptions().
                    geodesic(true).
                    color(Color.BLUE).
                    width(10);

            for (int i = 0; i < route.points.size(); i++)
                polylineOptions.add(route.points.get(i));

            polylinePaths.add(mMap.addPolyline(polylineOptions));
        }


    }
    /**
     * Prompts the user for permission to use the device location.
     */
    private void getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(this.getActivity().getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this.getActivity(),
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }
    /**
     * Handles the result of the request for location permissions.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                }
            }
        }
        updateLocationUI();
    }

    @Override
    public void onConnected(Bundle bundle) {
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    @Override
    public void onConnectionSuspended(int i) {}

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {}

    @Override
    public void onLocationChanged(Location location)
    {
        if (mCurrLocationMarker != null) {
            mCurrLocationMarker.remove();
        }
        if(polylinePaths!=null){
            sendRequest();
        }

        //Place current location marker
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        currLatLng = latLng;
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("Current Position");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
        mCurrLocationMarker = mMap.addMarker(markerOptions);

        //move map camera
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,15));

    }
    private void CreateGeofenceToComplete() {
        //TODO shared preferences will probably persist for as long as the app exists. it needs to be reset when app is closed
        SharedPreferences sharedPreferences = getActivity().getPreferences(Context.MODE_PRIVATE);
        String dormDefault = "none";
        String dormVisited = sharedPreferences.getString("isVisited", dormDefault);
        if (dormVisited != dormDefault){
            Log.d(TAG, "CreateGeofenceToComplete: GEOFENCES ALREADY EXIST");
            return;
        }
        //create the geofence list
        geofenceList = new ArrayList<>();
        Toast.makeText(this.getActivity(), "from create geofence", Toast.LENGTH_SHORT).show();
        // create the client
        /*
      objects used for the geofence
      to create and use a geo fence we need four parts
      We need a geofencingclient to access api
      We need a geofence for obvious reasons
      We need a pending intent to add or remove a geofence
      We need a geofencing request to add geofence to geofencingClient
     */
        GeofencingClient geofencingClient = LocationServices.getGeofencingClient(this.getActivity());

        // build the geofence
        // this uses a builder to assign all attributes.
        // the builder is a lot like a layout manager
        for(int i = 0; i < values.length; i++){
            geofence = new Geofence.Builder()
                    // string used to refer to the geo fence
                    .setRequestId(values[i])
                    // bounds of the fence
                    // double longitude
                    // double latitude
                    // float radius in meters
                    // currently set to crosby. replace first and second arg with geoLat and geoLong respectively
                    .setCircularRegion(
                            destinationPoint.get(i).latitude, destinationPoint.get(i).longitude,
                            50)
                    // how long the geo fence stays active
                    .setExpirationDuration(Geofence.NEVER_EXPIRE)
                    // how the geo fence will be triggered
                    .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_DWELL)
                    .setLoiteringDelay(10)
                    // create it
                    .build();

            // add geofence to geofence list
            geofenceList.add(geofence);
        }
        // permissions check
        if (ActivityCompat.checkSelfPermission(this.getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //Log.d(TAG, "permisions denied fine location");
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        geofencingClient.addGeofences(getGeofencingRequest(), getGeofencingPendingIntent())
                .addOnSuccessListener(this.getActivity(), new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "successfully added geofence: " + geofence.getRequestId());
                    }
                })
                .addOnFailureListener(this.getActivity(), new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //Log.e(TAG, e.getMessage().toString());
                        Log.d(TAG, "failed to add geofence: " + geofence.getRequestId());
                    }
                });
        Log.d(TAG, "CREATE_GEOFENCE_TO_COMPLETE DONE RUNNING");

        if (LocationServices.getFusedLocationProviderClient(this.getActivity()).getLastLocation() == null) {
            Log.d(TAG, "it was null!");
        }
    }
     /**
     * builds and returns a geofending request. Specifies the list of geofences to be monitored.
     * also specifies how the geofence notifications are initially triggered
     * @return geofence request
     */
    private GeofencingRequest getGeofencingRequest() {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();

        // the INITIAL_TRIGGER_ENTER flag indicates that geofencing service should trigger a
        // GEOFENCE_TRANSITION_ENTER notification when the geofence is added and if the device
        // is already inside that geofence
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);

        // add the geofences to be monitered by geofencing services.
        builder.addGeofences(geofenceList);
        assert (geofenceList != null);
        // build and return the request
        return builder.build();
    }

    private PendingIntent getGeofencingPendingIntent() {
        // reuse old intent if it exists
        if (pendingIntent != null) {
            return pendingIntent;
        }
        // need to send this to unity scene when it is imported into the project
        Intent intent = new Intent(this.getActivity(), GeoFenceHelperService.class);
        // we need to use FLAG_UPDATE_CURRENT so that we get the same pending intent back when
        // calling addGeofences() and removeGeofences()
        Log.d(TAG, intent.toString());
        pendingIntent = PendingIntent.getService(this.getActivity(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        Log.d(TAG, "INSIDE PENDING INTENT");

        return pendingIntent;
    }

    /**
     * sensor is activated and steps from sensor assigned to the textview
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void pedometer(){
        manager.registerListener(new SensorEventListener() {

                                     //@SuppressLint("SetTextI18n")
                                     @Override
                                     public void onSensorChanged(SensorEvent event) {
                                         //determines if the activity is just launched
                                         if(zeroSteps){
                                             stepsDontCount = event.values[0];//records steps since device reboot
                                             zeroSteps = false;//activity will no loner be considered new
                                         }
                                         steps = event.values[0] - stepsDontCount;//subtracts the values stored in the
                                         // phone so only the steps taken since the app show

                                         //count.setText(steps + "");
                                     }
                                     @Override
                                     public void onAccuracyChanged(Sensor sensor, int accuracy) {

                                     }
                                 }, manager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER),
                SensorManager.SENSOR_DELAY_UI);
    }
    @Override
    public void onDestroy(){
        super.onDestroy();
        Log.d(TAG, "ONDESTROY");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "ONPAUSE");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "ONRESUME");
    }
}

