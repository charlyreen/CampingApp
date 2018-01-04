package de.hs_ulm.campingapp;

import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;

import android.os.Looper;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.widget.Toast;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback,
        GoogleMap.OnInfoWindowClickListener
{


    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;
    private static final int REQUEST_CHECK_SETTINGS = 0x1;
    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;
    private static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS =
            UPDATE_INTERVAL_IN_MILLISECONDS / 2;
    private FusedLocationProviderClient mFusedLocationClient;
    private final static String KEY_REQUESTING_LOCATION_UPDATES = "requesting-location-updates";
    private final static String KEY_LOCATION = "location";
    private final static String KEY_LAST_UPDATED_TIME_STRING = "last-updated-time-string";
    private SettingsClient mSettingsClient;
    private LocationRequest mLocationRequest;
    private LocationSettingsRequest mLocationSettingsRequest;
    private LocationCallback mLocationCallback;
    private Location mCurrentLocation;
    private Boolean mRequestingLocationUpdates;
    private String mLastUpdateTime;
    private Boolean mWasActive = false;

    private GoogleMap gMap;
    private HashMap<Spot, Marker> markers = new HashMap<Spot, Marker>();


    /*Firebase Data Reference*/
    DatabaseReference mRootRef;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //get Firebase Ref
        mRootRef = FirebaseDatabase.getInstance().getReference();

        //myLocation.setVisible(false);

        //FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        //fab.setOnClickListener(new View.OnClickListener()
        //{
        //    @Override
        //    public void onClick(View view)
        //    {
        //        addNewDummySpot();
        //        Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
        //                .setAction("Action", null)
        //                .show();
        //    }
        //});

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        /*Init GoogleMaps mapFragment, included in activity_main.xml through app_bar_main to activity_maps*/
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync((OnMapReadyCallback) this);


        mRequestingLocationUpdates = true;
        mLastUpdateTime = "";

        updateValuesFromBundle(savedInstanceState);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mSettingsClient = LocationServices.getSettingsClient(this);

        createLocationCallback();
        createLocationRequest();
        buildLocationSettingsRequest();
    }


    /*Hier werden die Marker geladen*/
    /*FIREBASE INTEGRATION*/
    /*TODO: lade nur die Spots, die im aktuellen Sichtfeld zu sehen sind
    (über longitude und latitude irgendwie)*/
    @Override
    public void onMapReady(final GoogleMap googleMap)
    {

        mRootRef.child("spots").addChildEventListener(new ChildEventListener()
        {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s)
            {
                /*das is quasi wie eine schleife , die die werte ausliest*/
                Spot location = dataSnapshot.getValue(Spot.class);
                double lati = location.getLatitude();
                double longi = location.getLongitude();
                LatLng gpsdata = new LatLng(lati, longi);

                CustomInfoWindowAdapter adapter =
                        new CustomInfoWindowAdapter(MainActivity.this);

                googleMap.setInfoWindowAdapter(adapter);

                Marker newMarker = googleMap.addMarker(new MarkerOptions()
                        .position(gpsdata)
                        .title(location.getName() + " Type: " + location.getType())
                        .snippet(dataSnapshot.getKey()));
                newMarker.setTag(location);

                Log.e("addMarkers",
                        "Name: " + location.getName() + ", Key:" + dataSnapshot.getKey());

                markers.put(location, newMarker);

                //Toast.makeText(getApplicationContext(), dataSnapshot.getKey(), Toast.LENGTH_LONG).show();


            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s)
            {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot)
            {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s)
            {

            }

            @Override
            public void onCancelled(DatabaseError databaseError)
            {

            }
        });
        /*BSPCODE um einen marker hinzuzufügen:
        LatLng sydney = new LatLng(-33.852, 151.211);
        googleMap.addMarker(new MarkerOptions().position(sydney).title("Marker in sydney"));
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));*/

        CustomInfoWindowAdapter adapter = new CustomInfoWindowAdapter(MainActivity.this);
        googleMap.setInfoWindowAdapter(adapter);

        //Wenn man auf Marker klickt:
        googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener()
        {
            @Override
            public boolean onMarkerClick(Marker marker)
            {
                //Toast toast = Toast.makeText(getApplicationContext(), "okokok", Toast.LENGTH_LONG);
                //toast.show();
                marker.showInfoWindow();

                //GoogleMap
                return false;
            }
        });

        googleMap.setOnInfoWindowClickListener(this);

        gMap = googleMap;
        //googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(test, 12));

    }

    @Override
    public void onInfoWindowClick(Marker marker)
    {
        Intent showComment;
        String spotkey;
        Spot currSpot;

        currSpot = (Spot) marker.getTag();
        spotkey = marker.getSnippet();
        showComment = new Intent(this.getApplicationContext(), ShowComments.class);


        //Um DB Traffic zu sparen wird der jeweilige Spot "gebundlet" komplett übergeben!
        Bundle b = new Bundle();
        b = currSpot.toBundle();
        showComment.putExtras(b);
        //Key wird extra übergeben, weil er "eigentlich" nicht zum Spot Objekt gehört
        showComment.putExtra("key", spotkey);
        showComment.putExtra("currLocLatitude", (float) mCurrentLocation.getLatitude());
        showComment.putExtra("currLocLongitude", (float) mCurrentLocation.getLongitude());
        //Toast.makeText(getApplicationContext(), spotkey, Toast.LENGTH_LONG).show();
        startActivity(showComment);
    }

    @Override
    public void onBackPressed()
    {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START))
        {
            drawer.closeDrawer(GravityCompat.START);
        }
        else
        {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings)
        {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item)
    {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);


        if (id == R.id.nav_camera)
        {
            drawer.closeDrawer(GravityCompat.START);
        }
        else if (id == R.id.nav_gallery)
        {
            drawer.closeDrawer(GravityCompat.START);
        }
        else if (id == R.id.nav_slideshow)
        {
            drawer.closeDrawer(GravityCompat.START);
        }
        else if (id == R.id.nav_manage)
        {
            drawer.closeDrawer(GravityCompat.START);
        }
        else if (id == R.id.nav_filt_none)
        {
            Toast.makeText(getApplicationContext(), "testeroni" , Toast.LENGTH_LONG).show();
            //drawer.openDrawer(GravityCompat.START);
            for (Map.Entry<Spot,Marker> entry : markers.entrySet())
            {
                entry.getValue().setVisible(true);
            }
        }
        else if (id == R.id.nav_filt_sleep)
        {
            //Toast.makeText(getApplicationContext(), "testeroni" , Toast.LENGTH_LONG).show();
            //drawer.openDrawer(GravityCompat.START);
            for (Map.Entry<Spot,Marker> entry : markers.entrySet())
            {
                Spot key = entry.getKey();
                Marker value = entry.getValue();

                    if(key.getType().equalsIgnoreCase(getString(R.string.filter_sleep)))
                        value.setVisible(true);
                    else
                        value.setVisible(false);
                    //Toast.makeText(getApplicationContext(), key.getType()
                    //        + "\n" + key.getName() + "\n"
                    //        + value.getTitle(), Toast.LENGTH_SHORT).show();
            }
        }
        else if (id == R.id.nav_filt_scene)
        {
            //drawer.openDrawer(GravityCompat.START);
            for (Map.Entry<Spot,Marker> entry : markers.entrySet())
            {
                Spot key = entry.getKey();
                Marker value = entry.getValue();

                if(key.getType().equalsIgnoreCase(getString(R.string.filter_scene)))
                    value.setVisible(true);
                else
                    value.setVisible(false);
            }
        }
        else if (id == R.id.nav_filt_action)
        {
            //drawer.openDrawer(GravityCompat.START);
            for (Map.Entry<Spot,Marker> entry : markers.entrySet())
            {
                Spot key = entry.getKey();
                Marker value = entry.getValue();

                if(key.getType().equalsIgnoreCase(getString(R.string.filter_action)))
                    value.setVisible(true);
                else
                    value.setVisible(false);
            }
        }

        return true;
    }

    private void addNewDummySpot()
    {
        Spot dummy;
        dummy = new Spot("yodawg", (double) 48.5887, (double) 10.2058, "Giengen",
                "Hier liegt Giengen an der Brenz :)",
                "https://upload.wikimedia.org/wikipedia/commons/1/11/Giengen_an_der_Brenz_001.jpg",
                1511978087, "sleep", true);
        //mRootRef.child("spots").push().setValue(dummy);
    }


    private void updateValuesFromBundle(Bundle savedInstanceState)
    {
        if (savedInstanceState != null)
        {
            // Update the value of mRequestingLocationUpdates from the Bundle, and make sure that
            // the Start Updates and Stop Updates buttons are correctly enabled or disabled.
            if (savedInstanceState.keySet().contains(KEY_REQUESTING_LOCATION_UPDATES))
            {
                mRequestingLocationUpdates = savedInstanceState.getBoolean(
                        KEY_REQUESTING_LOCATION_UPDATES);
            }

            // Update the value of mCurrentLocation from the Bundle and update the UI to show the
            // correct latitude and longitude.
            if (savedInstanceState.keySet().contains(KEY_LOCATION))
            {
                // Since KEY_LOCATION was found in the Bundle, we can be sure that mCurrentLocation
                // is not null.
                mCurrentLocation = savedInstanceState.getParcelable(KEY_LOCATION);
            }

            // Update the value of mLastUpdateTime from the Bundle and update the UI.
            if (savedInstanceState.keySet().contains(KEY_LAST_UPDATED_TIME_STRING))
            {
                mLastUpdateTime = savedInstanceState.getString(KEY_LAST_UPDATED_TIME_STRING);
            }
            //updateUI();
        }
    }

    private void createLocationRequest()
    {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    private void createLocationCallback()
    {
        mLocationCallback = new LocationCallback()
        {
            @Override
            public void onLocationResult(LocationResult locationResult)
            {
                super.onLocationResult(locationResult);

                mCurrentLocation = locationResult.getLastLocation();
                mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
                updateLocationMarker();
            }
        };
    }

    private void updateLocationMarker()
    {
        if (mRequestingLocationUpdates)
        {
            if (ActivityCompat.checkSelfPermission(this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION) !=
                    PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(this,
                            android.Manifest.permission.ACCESS_COARSE_LOCATION)
                            != PackageManager.PERMISSION_GRANTED)
            {
                return;
            }

            if(!mWasActive)
            {
                mWasActive = true;
                gMap.setMyLocationEnabled(true);
                gMap.animateCamera(CameraUpdateFactory
                        .newLatLngZoom(new LatLng(mCurrentLocation.getLatitude(),
                                mCurrentLocation.getLongitude()), 15));
                gMap.getUiSettings().setMyLocationButtonEnabled(true);
            }

        }
        else
        {
            //gMap.setMyLocationEnabled(false);
            mWasActive = false;
        }
    }

    private void buildLocationSettingsRequest()
    {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        mLocationSettingsRequest = builder.build();
    }

    private void startLocationUpdates()
    {
        // Begin by checking if the device has the necessary location settings.
        mSettingsClient.checkLocationSettings(mLocationSettingsRequest)
                .addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>()
                {
                    @Override
                    public void onSuccess(LocationSettingsResponse locationSettingsResponse)
                    {
                        Log.i(TAG, "All location settings are satisfied.");

                        //TODO: Maybe add permission check???
                        //noinspection MissingPermission
                        mFusedLocationClient.requestLocationUpdates(mLocationRequest,
                                mLocationCallback, Looper.myLooper());

                        //updateUI();
                    }
                })
                .addOnFailureListener(this, new OnFailureListener()
                {
                    @Override
                    public void onFailure(@NonNull Exception e)
                    {
                        int statusCode = ((ApiException) e).getStatusCode();
                        switch (statusCode)
                        {
                            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                                Log.i(TAG, "Location settings are not satisfied. Attempting to upgrade " +
                                        "location settings ");
                                try
                                {
                                    // Show the dialog by calling startResolutionForResult(), and check the
                                    // result in onActivityResult().
                                    ResolvableApiException rae = (ResolvableApiException) e;
                                    rae.startResolutionForResult(MainActivity.this, REQUEST_CHECK_SETTINGS);
                                }
                                catch (IntentSender.SendIntentException sie)
                                {
                                    Log.i(TAG, "PendingIntent unable to execute request.");
                                }
                                break;
                            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                String errorMessage = "Location settings are inadequate, and cannot be " +
                                        "fixed here. Fix in Settings.";
                                Log.e(TAG, errorMessage);
                                Toast.makeText(MainActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                                mRequestingLocationUpdates = false;
                        }

                        //updateUI();
                    }
                });
    }

    private void stopLocationUpdates()
    {
        if (!mRequestingLocationUpdates)
        {
            Log.d(TAG, "stopLocationUpdates: updates never requested, no-op.");
            return;
        }

        // It is a good practice to remove location requests when the activity is in a paused or
        // stopped state. Doing so helps battery performance and is especially
        // recommended in applications that request frequent location updates.
        mFusedLocationClient.removeLocationUpdates(mLocationCallback)
                .addOnCompleteListener(this, new OnCompleteListener<Void>()
                {
                    @Override
                    public void onComplete(@NonNull Task<Void> task)
                    {
                        mRequestingLocationUpdates = false;
                        //setButtonsEnabledState();
                    }
                });
    }

    @Override
    public void onResume()
    {
        super.onResume();
        // Within {@code onPause()}, we remove location updates. Here, we resume receiving
        // location updates if the user has requested them.
        if (mRequestingLocationUpdates && checkPermissions())
        {
            startLocationUpdates();
        }
        else if (!checkPermissions())
        {
            requestPermissions();
        }

        //updateUI();
    }

    @Override
    protected void onPause()
    {
        super.onPause();

        // Remove location updates to save battery.
        stopLocationUpdates();

    }

    public void onSaveInstanceState(Bundle savedInstanceState)
    {
        savedInstanceState.putBoolean(KEY_REQUESTING_LOCATION_UPDATES, mRequestingLocationUpdates);
        savedInstanceState.putParcelable(KEY_LOCATION, mCurrentLocation);
        savedInstanceState.putString(KEY_LAST_UPDATED_TIME_STRING, mLastUpdateTime);
        super.onSaveInstanceState(savedInstanceState);
    }

    private void showSnackbar(final int mainTextStringId, final int actionStringId,
                              View.OnClickListener listener)
    {
        Snackbar.make(
                findViewById(android.R.id.content),
                getString(mainTextStringId),
                Snackbar.LENGTH_INDEFINITE)
                .setAction(getString(actionStringId), listener).show();
    }

    private boolean checkPermissions()
    {
        int permissionState = ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION);
        return permissionState == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermissions()
    {
        boolean shouldProvideRationale =
                ActivityCompat.shouldShowRequestPermissionRationale(this,
                        android.Manifest.permission.ACCESS_FINE_LOCATION);

        // Provide an additional rationale to the user. This would happen if the user denied the
        // request previously, but didn't check the "Don't ask again" checkbox.
        if (shouldProvideRationale) {
            Log.i(TAG, "Displaying permission rationale to provide additional context.");
            showSnackbar(R.string.permission_rationale,
                    android.R.string.ok, new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View view)
                        {
                            // Request permission
                            ActivityCompat.requestPermissions(MainActivity.this,
                                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                                    REQUEST_PERMISSIONS_REQUEST_CODE);
                        }
                    });
        }
        else
        {
            Log.i(TAG, "Requesting permission");
            // Request permission. It's possible this can be auto answered if device policy
            // sets the permission in a given state or the user denied the permission
            // previously and checked "Never ask again".
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_PERMISSIONS_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults)
    {
        Log.i(TAG, "onRequestPermissionResult");
        if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE)
        {
            if (grantResults.length <= 0)
            {
                // If user interaction was interrupted, the permission request is cancelled and you
                // receive empty arrays.
                Log.i(TAG, "User interaction was cancelled.");
            }
            else if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                if (mRequestingLocationUpdates)
                {
                    Log.i(TAG, "Permission granted, updates requested, starting location updates");
                    startLocationUpdates();
                }
            }
            else
            {
                // Permission denied.

                // Notify the user via a SnackBar that they have rejected a core permission for the
                // app, which makes the Activity useless. In a real app, core permissions would
                // typically be best requested during a welcome-screen flow.

                // Additionally, it is important to remember that a permission might have been
                // rejected without asking the user for permission (device policy or "Never ask
                // again" prompts). Therefore, a user interface affordance is typically implemented
                // when permissions are denied. Otherwise, your app could appear unresponsive to
                // touches or interactions which have required permissions.
                showSnackbar(R.string.permission_denied_explanation,
                        R.string.settings, new View.OnClickListener()
                        {
                            @Override
                            public void onClick(View view)
                            {
                                // Build intent that displays the App settings screen.
                                Intent intent = new Intent();
                                intent.setAction(
                                        Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                Uri uri = Uri.fromParts("package",
                                        BuildConfig.APPLICATION_ID, null);
                                intent.setData(uri);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }
                        });
            }
        }
    }

}
