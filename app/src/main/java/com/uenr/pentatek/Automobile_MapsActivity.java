package com.uenr.pentatek;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Automobile_MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private String customer_fname,customer_lname, customer_phone, customer_email,
    getCustomer_id, car_type, car_brand, car_model, car_plate, the_problem;

    private TextView user_name_textview, user_phone_textview,user_email_textview,
    car_type_textview, car_brand_textview,car_model_textview, car_plate_textview,problem_textview;
    private Accessories automobile_accessor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        automobile_accessor = new Accessories(Automobile_MapsActivity.this);
        getSupportActionBar().setTitle("PentaTek | Map");

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

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

        user_name_textview = findViewById(R.id.u_name);
        user_phone_textview = findViewById(R.id.u_phone);
        user_email_textview = findViewById(R.id.u_email);
        car_type_textview = findViewById(R.id.car_type);
        car_brand_textview = findViewById(R.id.car_brand);
        car_model_textview = findViewById(R.id.car_model);
        car_plate_textview = findViewById(R.id.car_plate);
        problem_textview = findViewById(R.id.problem);

        user_name_textview.setText(customer_fname + " " + customer_lname);
        user_phone_textview.setText(customer_phone);
        user_email_textview.setText(customer_email);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(isNetworkAvailable()){
            Fetch_Vehicle_Info(getCustomer_id);
            Fetch_Vehicle_Problem(getCustomer_id);
        }else{
            Toast.makeText(Automobile_MapsActivity.this,"No interner connection",Toast.LENGTH_LONG).show();
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
}
