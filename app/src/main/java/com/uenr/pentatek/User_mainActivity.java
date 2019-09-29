package com.uenr.pentatek;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Random;

public class User_mainActivity extends AppCompatActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        com.google.android.gms.location.LocationListener {

    private TextView welcome_message;
    private Button distress_button;
    private Dialog view_confirmation_dialogue;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private List<Address> address;
    private Geocoder geocoder;
    private LocationRequest mLocationRequest;
    final int LOCATION_REQUEST_CODE = 1;
    protected static final int REQUEST_CHECK_SETTINGS = 0x1;
    private GoogleMap mMap;
    private String vehicle_problem_, problem_text;
    private DatabaseReference reference;
    private FirebaseAuth mauth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_main);

        getSupportActionBar().setTitle("PentaTek | User");

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        mauth = FirebaseAuth.getInstance();

        welcome_message = findViewById(R.id.welcome_message);
        distress_button = findViewById(R.id.distress_button);

        geocoder = new Geocoder(User_mainActivity.this, Locale.getDefault());

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(User_mainActivity.this,new String[] {Manifest.permission.ACCESS_FINE_LOCATION},LOCATION_REQUEST_CODE);
        }else{
            mapFragment.getMapAsync(this);
        }

        view_confirmation_dialogue = new Dialog(User_mainActivity.this);

        distress_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder logout = new AlertDialog.Builder(User_mainActivity.this, R.style.Myalert);
                logout.setTitle("Confirmation");
                logout.setMessage("By clicking on YES, you confirm that you are in distress and want help from your automobile company");
                logout.setNegativeButton("YES", new DialogInterface.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
//                        logout here
                        if(isNetworkAvailable()){
                            showDistress_info_dialogue(User_mainActivity.this);
                            }else{
                                Toast.makeText(User_mainActivity.this,"No internet connection",Toast.LENGTH_LONG).show();
                        }
                    }
                });

                logout.setPositiveButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                logout.show();
            }
        });
    }

    protected synchronized void buildGoogleApiClient(){
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this).addApi(LocationServices.API
                ).build();
        mGoogleApiClient.connect();
    }

    private void displayLocationSettingsRequest(Context context) {
        GoogleApiClient googleApiClient = new GoogleApiClient.Builder(context)
                .addApi(LocationServices.API).build();
        googleApiClient.connect();
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(10000 / 2);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);

        PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            public static final String TAG = "";

            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        Log.i(TAG, "All location settings are satisfied.");
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        Log.i(TAG, "Location settings are not satisfied. Show the user a dialog to upgrade location settings ");

                        try {
                            // Show the dialog by calling startResolutionForResult(), and check the result
                            // in onActivityResult().
                            status.startResolutionForResult(User_mainActivity.this, REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException e) {
                            Log.i(TAG, "PendingIntent unable to execute request.");
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        Log.i(TAG, "Location settings are inadequate, and cannot be fixed here. Dialog not created.");
                        break;
                }
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void showDistress_info_dialogue(FragmentActivity activity) {
        final TextView cancelpopup,success_message, problem_description_text, select_text, or_text;
        final EditText problem_statement_editText;
        final Spinner problem_selection_spinner;
        final Button done_button;
        final ProgressBar loading;

        final String[] car_problems = {"Car won't start", "Flat tyre", "Over heating engine"};

        view_confirmation_dialogue.setContentView(R.layout.custom_confirmation_popup);
        cancelpopup = (TextView)view_confirmation_dialogue.findViewById(R.id.cancel);
        success_message = (TextView)view_confirmation_dialogue.findViewById(R.id.success_message);
        problem_description_text = (TextView) view_confirmation_dialogue.findViewById(R.id.problem_description_text);
        select_text = (TextView) view_confirmation_dialogue.findViewById(R.id.select_problem_type_text);
        problem_selection_spinner = (Spinner) view_confirmation_dialogue.findViewById(R.id.select_problem_type_spinner);
        or_text = (TextView) view_confirmation_dialogue.findViewById(R.id.or_message_text);
        problem_statement_editText = (EditText) view_confirmation_dialogue.findViewById(R.id.or_message_editText);
        done_button = (Button)view_confirmation_dialogue.findViewById(R.id.done_button);
        loading = (ProgressBar) view_confirmation_dialogue.findViewById(R.id.loading);

//        cancelpopup.setTypeface(lovelo);
//        verification_message.setTypeface(lovelo);
//        verify_button.setTypeface(lovelo);
//        success_message.setTypeface(lovelo);

        cancelpopup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                view_confirmation_dialogue.dismiss();
            }
        });

        //        setting the adapater for the vehicle spinner
        problem_selection_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                vehicle_problem_ = car_problems[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                vehicle_problem_ = "Car won't start";
            }
        });
        ArrayAdapter<String> the_vehicle = new ArrayAdapter<String>(User_mainActivity.this,android.R.layout.simple_list_item_1,car_problems);
        problem_selection_spinner.setAdapter(the_vehicle);

        done_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isNetworkAvailable()){
                   problem_text = problem_statement_editText.getText().toString().trim();
                   if(!problem_text.equals("")){
                        loading.setVisibility(View.VISIBLE);
                        saveToDatbase(problem_text);
                        loading.setVisibility(View.GONE);
                        success_message.setText("Problem reported");
                        success_message.setTextColor(getResources().getColor(R.color.red));
                        success_message.setVisibility(View.VISIBLE);
                        view_confirmation_dialogue.dismiss();
                   }else{
                       loading.setVisibility(View.VISIBLE);
                       saveToDatbase(vehicle_problem_);
                       loading.setVisibility(View.GONE);
                       success_message.setText("Problem reported");
                       success_message.setTextColor(getResources().getColor(R.color.red));
                       success_message.setVisibility(View.VISIBLE);
                       view_confirmation_dialogue.dismiss();
                   }
                }else{
                    success_message.setText("No internet connection");
                    success_message.setTextColor(getResources().getColor(R.color.red));
                    success_message.setVisibility(View.VISIBLE);
                }
            }
        });


        Objects.requireNonNull(view_confirmation_dialogue.getWindow()).setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.ash_1)));
        view_confirmation_dialogue.show();
    }

    private void saveToDatbase(String vehicle_problem_) {
        Random g = new Random();
        int number = g.nextInt(999999);
        String problem_id = "prob_"+number;
        reference = FirebaseDatabase.getInstance().getReference("problems").child("company1")
                .child(mauth.getCurrentUser().getUid());
        reference.child("problem_description").setValue(vehicle_problem_);
    }

    @Override
    protected void onStart() {
        super.onStart();
        displayLocationSettingsRequest(User_mainActivity.this);
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(User_mainActivity.this,new String[] {Manifest.permission.ACCESS_FINE_LOCATION},LOCATION_REQUEST_CODE);
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;
        LatLng latLng = new LatLng(location.getLatitude(),location.getLongitude());
        try {
            address = geocoder.getFromLocation(location.getLatitude(),location.getLongitude(),1);
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
//            userlocation.setText(address.get(0).getAddressLine(0));

            //        update location of driver
            try{
                String  userid = "4";//FirebaseAuth.getInstance().getCurrentUser().getUid();
                DatabaseReference drivers = FirebaseDatabase.getInstance().getReference("user_location");
                GeoFire geoFireAvailable = new GeoFire(drivers);

                geoFireAvailable.setLocation(userid, new GeoLocation(location.getLatitude(), location.getLongitude()), new GeoFire.CompletionListener() {
                    @Override
                    public void onComplete(String s, DatabaseError databaseError) {
                    }
                });
            }catch (NullPointerException e){
                e.printStackTrace();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
        }
        buildGoogleApiClient();
        mMap.setMyLocationEnabled(true);
    }
}
