package com.uenr.pentatek;

import android.Manifest;
import android.app.Dialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.VectorDrawable;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.content.res.AppCompatResources;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;
import java.util.Objects;
import java.util.Random;

public class Automobile_MapsActivity extends AppCompatActivity implements OnMapReadyCallback,GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener{

    private GoogleMap mMap;
    private String customer_fname,customer_lname, customer_phone, customer_email,
    getCustomer_id, car_type, car_brand, car_model, car_plate, the_problem, prize_text;

    private TextView client_location_do_nothing_textview,user_name_textview, user_phone_textview,user_email_textview,
    car_type_textview, car_brand_textview,car_model_textview, car_plate_textview,problem_textview,
    clent_details_do_nothing,user_name_do_nothing,email_do_nothing,phone_do_nothing,cartype_do_nothing,car_brand_do_nothing,
    car_model_do_nothing,car_plate_do_nothing, car_details_do_nothing, problem_do_nothing, dismissRequest_textview;
    private Accessories automobile_accessor;
    private DatabaseReference userlocation_reference;
    private Marker mDriverMarker;
    private final int LOCATION_REQUEST_CODE = 1;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private SupportMapFragment mapFragment;
    private Button processRequestButton;
    private DatabaseReference reference;
    private FirebaseAuth mAuth;
    private Dialog view_process_dialogue;
    private Typeface quicksand_light,quicksand_regular;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        //firebase authentication
        mAuth = FirebaseAuth.getInstance();

        //initializing the dialogue
        view_process_dialogue = new Dialog(Automobile_MapsActivity.this);

        //        initializing the fonts
        Typeface breezed_cap =Typeface.createFromAsset(getAssets(),  "fonts/BreezedcapsBoldoblique-Epvj.ttf");
        quicksand_light =Typeface.createFromAsset(getAssets(),  "fonts/Quicksand-Light.ttf");
        quicksand_regular =Typeface.createFromAsset(getAssets(),  "fonts/Quicksand-Regular.ttf");

        automobile_accessor = new Accessories(Automobile_MapsActivity.this);
        getSupportActionBar().setTitle("PentaTek | Map");

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(Automobile_MapsActivity.this,new String[] {Manifest.permission.ACCESS_FINE_LOCATION},LOCATION_REQUEST_CODE);
        }else{
            mapFragment.getMapAsync(this);
        }
        customer_fname = automobile_accessor.getString("customer_fname");
        customer_lname = automobile_accessor.getString("customer_lname");
        customer_phone = automobile_accessor.getString("customer_phone");
        customer_email = automobile_accessor.getString("customer_email");
        getCustomer_id = automobile_accessor.getString("customer_id");
//        car_type = automobile_accessor.getString("the_car_type");
//        car_brand = automobile_accessor.getString("the_car_brand");
//        car_model = automobile_accessor.getString("the_car_model");
//        car_plate = automobile_accessor.getString("the_car_plate");
//        the_problem = automobile_accessor.getString("the_problem");

        //the textviews that do nothing
        client_location_do_nothing_textview = findViewById(R.id.client_location_do_nothing);
        clent_details_do_nothing = findViewById(R.id.client_details_do_nothing);
        user_name_do_nothing = findViewById(R.id.name_do_nothing);
        email_do_nothing = findViewById(R.id.email_do_nothing);
        phone_do_nothing = findViewById(R.id.phone_number_do_nothing);
        car_details_do_nothing = findViewById(R.id.cart_details_do_nothing);
        cartype_do_nothing = findViewById(R.id.car_type_do_nothing);
        car_brand_do_nothing = findViewById(R.id.car_brand_do_nothing);
        car_model_do_nothing = findViewById(R.id.car_model_do_nothing);
        car_plate_do_nothing = findViewById(R.id.car_plate_do_nothing);
        problem_do_nothing = findViewById(R.id.problem_do_nothing);

        //the textvies that actually so something
        user_name_textview = findViewById(R.id.u_name);
        user_phone_textview = findViewById(R.id.u_phone);
        user_email_textview = findViewById(R.id.u_email);
        car_type_textview = findViewById(R.id.car_type);
        car_brand_textview = findViewById(R.id.car_brand);
        car_model_textview = findViewById(R.id.car_model);
        car_plate_textview = findViewById(R.id.car_plate);
        problem_textview = findViewById(R.id.problem);
        dismissRequest_textview = findViewById(R.id.dismiss_request);
        processRequestButton = findViewById(R.id.process_request_button);

        user_name_textview.setText(customer_fname + " " + customer_lname);
        user_phone_textview.setText(customer_phone);
        user_email_textview.setText(customer_email);

        //setting the font style
        client_location_do_nothing_textview.setTypeface(quicksand_light);
        clent_details_do_nothing.setTypeface(quicksand_light);
        user_name_do_nothing.setTypeface(quicksand_regular);
        email_do_nothing.setTypeface(quicksand_regular);
        phone_do_nothing.setTypeface(quicksand_regular);
        car_details_do_nothing.setTypeface(quicksand_light);
        cartype_do_nothing.setTypeface(quicksand_regular);
        car_brand_do_nothing.setTypeface(quicksand_regular);
        car_model_do_nothing.setTypeface(quicksand_regular);
        car_plate_do_nothing.setTypeface(quicksand_regular);
        problem_do_nothing.setTypeface(quicksand_light);
        dismissRequest_textview.setTypeface(quicksand_light);
        processRequestButton.setTypeface(quicksand_regular);

        if (isNetworkAvailable()){
            findUserLocation();
        }


        //what happens when you click on the process button
        processRequestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showOrocess_dialogue(Automobile_MapsActivity.this);
            }
        });


        //what happens when you click on the dismiss request button.
        dismissRequest_textview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    final AlertDialog.Builder dismiss_ = new AlertDialog.Builder(Automobile_MapsActivity.this, R.style.Myalert);
                dismiss_.setTitle("Dismiss Request?");
                dismiss_.setIcon(getResources().getDrawable(R.drawable.sad_24dp));
                dismiss_.setMessage("Dismissing a request means that this user problem would be considered " +
                            "as a false request.");
                dismiss_.setNegativeButton("Dismiss", new DialogInterface.OnClickListener() {
                        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
//                        logout here
                            if(isNetworkAvailable()){
                                RemoveProblem_();
                            }else{
                                Toast.makeText(Automobile_MapsActivity.this,"No internet connection",Toast.LENGTH_LONG).show();
                            }
                        }
                    });

                dismiss_.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                dismiss_.show();
                }
        });
    }


    //this removes the problem from the database
    private void RemoveProblem_() {
        reference = FirebaseDatabase.getInstance().getReference("problems").child("company1")
                .child(getCustomer_id);
        reference.removeValue();
        Intent goto_company_mainActivity = new Intent(Automobile_MapsActivity.this, Company_mainActivity.class);
        goto_company_mainActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(goto_company_mainActivity);
        Toast.makeText(Automobile_MapsActivity.this, "Problem Dismissed", Toast.LENGTH_LONG).show();
    }

    //this removes from problems node and moves it to the recent problems node
    private void RemoveProblem_and_record(String prize_text) {
        Random generate_id = new Random();
        int no = generate_id.nextInt(455);
        String problem_id = "recent"+getCustomer_id+no+"";
        reference = FirebaseDatabase.getInstance().getReference("problems").child("company1")
                .child(getCustomer_id);
        DatabaseReference recent_problems = FirebaseDatabase.getInstance().getReference("recent_problems")
                .child(getCustomer_id).child(problem_id);
        recent_problems.child("problem_description").setValue(the_problem);
        recent_problems.child("problem_status").setValue("complete");
        recent_problems.child("prize").setValue(prize_text);
        reference.removeValue();
        Intent goto_company_mainActivity = new Intent(Automobile_MapsActivity.this, Company_mainActivity.class);
        goto_company_mainActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(goto_company_mainActivity);
        Toast.makeText(Automobile_MapsActivity.this, "Transaction Complete", Toast.LENGTH_LONG).show();
    }

    private void showOrocess_dialogue(FragmentActivity activity) {
        final TextView cancelpopup,success_message, problem_description_text, select_text, or_text;
        final EditText problem_statement_editText;
        final Button done_button;
        final ProgressBar loading;

        view_process_dialogue.setContentView(R.layout.custom_processing_popup);
        cancelpopup = (TextView)view_process_dialogue.findViewById(R.id.cancel);
        success_message = (TextView)view_process_dialogue.findViewById(R.id.success_message);
        or_text = (TextView) view_process_dialogue.findViewById(R.id.or_message_text);
        problem_statement_editText = (EditText) view_process_dialogue.findViewById(R.id.or_message_editText);
        done_button = (Button)view_process_dialogue.findViewById(R.id.done_button);
        loading = (ProgressBar) view_process_dialogue.findViewById(R.id.loading);

        cancelpopup.setTypeface(quicksand_light);
        success_message.setTypeface(quicksand_regular);
        or_text.setTypeface(quicksand_light);
        done_button.setTypeface(quicksand_regular);

        cancelpopup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                view_process_dialogue.dismiss();
            }
        });


        done_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isNetworkAvailable()){
                    prize_text = problem_statement_editText.getText().toString().trim();
                    if(!prize_text.equals("")){
                        loading.setVisibility(View.VISIBLE);
                        loading.setVisibility(View.GONE);
                        success_message.setText("Waiting for payment");
                        RemoveProblem_and_record(prize_text);
                        success_message.setText("Payment Received.");
                        success_message.setTextColor(getResources().getColor(R.color.green));
                        success_message.setVisibility(View.VISIBLE);
                        view_process_dialogue.dismiss();
                    }else{
                        loading.setVisibility(View.VISIBLE);
                        loading.setVisibility(View.GONE);
                        success_message.setText("Failed");
                        success_message.setTextColor(getResources().getColor(R.color.red));
                        success_message.setVisibility(View.VISIBLE);
                        view_process_dialogue.dismiss();
                    }
                }else{
                    success_message.setText("No internet connection");
                    success_message.setTextColor(getResources().getColor(R.color.red));
                    success_message.setVisibility(View.VISIBLE);
                }
            }
        });


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Objects.requireNonNull(view_process_dialogue.getWindow()).setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.white)));
        }
        view_process_dialogue.show();
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
        }
        buildGoogleApiClient();
//         Add a marker in Sydney and move the camera
//        LatLng sydney = new LatLng(-34, 151);
//        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }

    protected synchronized void buildGoogleApiClient(){
        mGoogleApiClient = new GoogleApiClient.Builder(this).addConnectionCallbacks(this).addOnConnectionFailedListener(this).addApi(LocationServices.API
        ).build();
        mGoogleApiClient.connect();
    }


    private void findUserLocation() {
        userlocation_reference = FirebaseDatabase.getInstance().getReference().child("user_location").child(getCustomer_id).child("l");
        userlocation_reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    List<Object> map = (List<Object>) dataSnapshot.getValue();
                    double locationlat = 0;
                    double locationlong = 0;
                    if(map.get(0) != null){
                        locationlat = Double.parseDouble(map.get(0).toString());
                    }
                    if(map.get(1) != null){
                        locationlong = Double.parseDouble(map.get(1).toString());
                    }
                    LatLng userLocation = new LatLng(locationlat,locationlong);
                    if(mDriverMarker != null){
                        mDriverMarker.remove();
                    }

                    Location user  = new Location("");
                    user.setLatitude(userLocation.latitude);
                    user.setLongitude(userLocation.longitude);
                    Bitmap bitmap = getBitmapFromVectorDrawable(Automobile_MapsActivity.this,R.drawable.thecar);
                    BitmapDescriptor descriptor =BitmapDescriptorFactory.fromBitmap(bitmap);

                    mDriverMarker = mMap.addMarker(new MarkerOptions().position(userLocation).title("customer").icon(descriptor));
//                    mMap.animateCamera( CameraUpdateFactory.zoomTo( 12 ) );
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public static Bitmap getBitmapFromVectorDrawable(Context context, int drawableId) {
        Drawable drawable =  AppCompatResources.getDrawable(context, drawableId);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            drawable = (DrawableCompat.wrap(drawable)).mutate();
        }

        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }


    @Override
    protected void onStart() {
        super.onStart();
        if(isNetworkAvailable()){
            Fetch_Vehicle_Info(getCustomer_id);
            Fetch_Vehicle_Problem(getCustomer_id);
        }else{
            Toast.makeText(Automobile_MapsActivity.this,"No internet connection",Toast.LENGTH_LONG).show();
        }

    }

    private void Fetch_Vehicle_Info(String customer_id) {
        DatabaseReference getProblem = FirebaseDatabase.getInstance().getReference("cars")
                .child(customer_id);
        getProblem.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    for(DataSnapshot child : dataSnapshot.getChildren()){
                        if(child.getKey().equals("type")){
                            car_type = child.getValue().toString();
//                            problem_accessor.put("the_car_type", car_type);
                            car_type_textview.setText(car_type);

                        }
                        if(child.getKey().equals("brand")){
                            car_brand = child.getValue().toString();
//                            problem_accessor.put("the_car_brand", car_brand);
                            car_brand_textview.setText(car_brand);

                        }
                        if(child.getKey().equals("model")){
                            car_model = child.getValue().toString();
//                            problem_accessor.put("the_car_model", car_model);
                            car_model_textview.setText(car_model);
                        }
                        if(child.getKey().equals("number_plate")){
                            car_plate = child.getValue().toString();
//                            problem_accessor.put("the_car_plate", car_plate);
                            car_plate_textview.setText(car_plate);
                        }
                        else{
//                            Toast.makeText(getActivity(),"Couldn't fetch posts",Toast.LENGTH_LONG).show();

                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(Automobile_MapsActivity.this,"Cancelled",Toast.LENGTH_LONG).show();

            }
        });
    }

    private void Fetch_Vehicle_Problem(String key) {
        DatabaseReference getProblem = FirebaseDatabase.getInstance().getReference("problems")
                .child("company1").child(key);
        getProblem.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    for(DataSnapshot child : dataSnapshot.getChildren()){
                        if(child.getKey().equals("problem_description")){
                            the_problem = child.getValue().toString();
//                            problem_accessor.put("the_problem", problem_);
                            problem_textview.setText(the_problem);
                        }

                        else{
//                            Toast.makeText(getActivity(),"Couldn't fetch posts",Toast.LENGTH_LONG).show();

                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(Automobile_MapsActivity.this,"Cancelled",Toast.LENGTH_LONG).show();

            }
        });

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

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(Automobile_MapsActivity.this,new String[] {Manifest.permission.ACCESS_FINE_LOCATION},LOCATION_REQUEST_CODE);
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

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case LOCATION_REQUEST_CODE:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    mapFragment.getMapAsync(this);
                }
                else{
                    Toast.makeText(getApplicationContext(),"Turn Location On",Toast.LENGTH_LONG).show();
                }
                break;
        }
    }


}
