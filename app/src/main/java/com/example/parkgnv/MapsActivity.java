package com.example.parkgnv;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.Random;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final String TAG = MapsActivity.class.getSimpleName();
    private GoogleMap map;
    private CameraPosition cameraPosition;

    private FusedLocationProviderClient fusedLocationProviderClient;

    private final LatLng defaultLocation = new LatLng(-33.8523341, 151.2106085);
    private static final int DEFAULT_ZOOM = 15;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private boolean locationPermissionGranted;

    private Location lastKnownLocation;

    private static final String KEY_CAMERA_POSITION = "camera_position";
    private static final String KEY_LOCATION = "location";

    private boolean isParked = false;
    Button parkedBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            lastKnownLocation = savedInstanceState.getParcelable(KEY_LOCATION);
            cameraPosition = savedInstanceState.getParcelable(KEY_CAMERA_POSITION);
        }

        setContentView(R.layout.activity_maps);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (map != null) {
            outState.putParcelable(KEY_CAMERA_POSITION, map.getCameraPosition());
            outState.putParcelable(KEY_LOCATION, lastKnownLocation);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onMapReady(GoogleMap _map) {
        final Intent intent = new Intent(this, AfterParking.class);
        parkedBtn = (Button) findViewById(R.id.parkedBtn);
        parkedBtn.setVisibility(View.INVISIBLE);
        Random rand = new Random();
        map = _map;

        getLocationPermission();

        updateLocationUI();

        getDeviceLocation();

        //8 demo parking spots
        addParkingSpot(28.190729 + (28.29 - 28.19) * rand.nextDouble() , -81.428177 + (-81.5 - -81.428) * rand.nextDouble(), map);
        addParkingSpot(28.190729 + (28.29 - 28.19) * rand.nextDouble() , -81.428177 + (-81.5 - -81.428) * rand.nextDouble(), map);
        addParkingSpot(28.190729 + (28.29 - 28.19) * rand.nextDouble() , -81.428177 + (-81.5 - -81.428) * rand.nextDouble(), map);
        addParkingSpot(28.190729 + (28.29 - 28.19) * rand.nextDouble() , -81.428177 + (-81.5 - -81.428) * rand.nextDouble(), map);
        addParkingSpot(28.190729 + (28.29 - 28.19) * rand.nextDouble() , -81.428177 + (-81.5 - -81.428) * rand.nextDouble(), map);
        addParkingSpot(28.190729 + (28.29 - 28.19) * rand.nextDouble() , -81.428177 + (-81.5 - -81.428) * rand.nextDouble(), map);
        addParkingSpot(28.190729 + (28.29 - 28.19) * rand.nextDouble() , -81.428177 + (-81.5 - -81.428) * rand.nextDouble(), map);

        //kissimmee civic center
        addParkingSpot(28.293189, -81.404038, map);

        //Harbor Freight Gainesville
        addParkingSpot(29.675889,-82.321435,map);

        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                Location locationB = new Location("Point B");
                locationB.setLatitude(marker.getPosition().latitude);
                locationB.setLongitude(marker.getPosition().longitude);

                //makes parking button visible if location to parking button is less or equal to than 250,
                // if another marker outside that range is clicked, the parking button becomes invisible
                if(lastKnownLocation.distanceTo(locationB) <= 250f) {
                    parkedBtn.setVisibility(View.VISIBLE);
                    return true;
                }
                parkedBtn.setVisibility(View.INVISIBLE);
                return false;
            }
        });

        //handles click of parking button
        parkedBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!isParked){
                    isParked = true;
                    parkedBtn.setText("I'm Leaving");
                } else {
                    isParked = false;
                    parkedBtn.setText("I'm Parked");
                    parkedBtn.setVisibility(View.INVISIBLE);
                    startActivity(intent);
                }
            }
        });

    }

    private void getLocationPermission() {
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            locationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    private void updateLocationUI() {
        if (map == null) {
            return;
        }
        try {
            if (locationPermissionGranted) {
                map.setMyLocationEnabled(true);
                map.getUiSettings().setMyLocationButtonEnabled(true);
            } else {
                map.setMyLocationEnabled(false);
                map.getUiSettings().setMyLocationButtonEnabled(false);
                lastKnownLocation = null;
                getLocationPermission();
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    private void getDeviceLocation() {
        try {
            if (locationPermissionGranted) {
                Task<Location> locationResult = fusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(this, new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful()) {
                            // Set the map's camera position to the current location of the device.
                            lastKnownLocation = task.getResult();
                            if (lastKnownLocation != null) {
                                map.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                        new LatLng(lastKnownLocation.getLatitude(),
                                                lastKnownLocation.getLongitude()), DEFAULT_ZOOM));
                            }
                        } else {
                            Log.d(TAG, "Current location is null. Using defaults.");
                            Log.e(TAG, "Exception: %s", task.getException());
                            map.moveCamera(CameraUpdateFactory
                                    .newLatLngZoom(defaultLocation, DEFAULT_ZOOM));
                            map.getUiSettings().setMyLocationButtonEnabled(false);
                        }
                    }
                });
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage(), e);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults){
        locationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    locationPermissionGranted = true;
                }
            }
        }
        updateLocationUI();
    }

    public static void addParkingSpot(double lat, double lon, GoogleMap map){
        LatLng parkingDemoLocation = new LatLng(lat, lon);
        map.addMarker(new MarkerOptions().position(parkingDemoLocation).title("Free parking!!"));
    }
}