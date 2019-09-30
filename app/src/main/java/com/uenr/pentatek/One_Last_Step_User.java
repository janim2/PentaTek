package com.uenr.pentatek;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Date;
import java.util.Random;

public class One_Last_Step_User extends AppCompatActivity {

    private Accessories one_last_accessor;
    private ImageView goback;
    private Spinner cart_type_spinner;
    private String[] car_types = {"Saloon","SUV","Hatchback","Pickup","Van","Supercar","Campervan","Minivan","Track"};
    private String car_types_string, car_model_string,car_brand_string, car_number_plate_string;
    private EditText car_brand_editText,car_model_editText,number_plate_editText;
    private TextView success_message,one_last_step_text, car_details_text, car_type_text,
    car_model_text, car_brand_text,car_number_plate_text;
    private ProgressBar loading;
    private Button addVehicle_button;
    private DatabaseReference add_car_reference;
    private FirebaseAuth mauth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_one__last__step__user);

        one_last_accessor = new Accessories(One_Last_Step_User.this);

        //        initializing the fonts
        Typeface breezed_cap =Typeface.createFromAsset(getAssets(),  "fonts/BreezedcapsBoldoblique-Epvj.ttf");
        Typeface quicksand_light =Typeface.createFromAsset(getAssets(),  "fonts/Quicksand-Light.ttf");
        Typeface quicksand_regular =Typeface.createFromAsset(getAssets(),  "fonts/Quicksand-Regular.ttf");

        mauth = FirebaseAuth.getInstance();

        //the texts that do nothing
        one_last_step_text  = findViewById(R.id.one_last_step_text);;
        car_details_text  = findViewById(R.id.car_details_text);;
        car_type_text  = findViewById(R.id.car_type_text);;
        car_model_text  = findViewById(R.id.car_model_text);;
        car_brand_text = findViewById(R.id.car_brand_text);;
        car_number_plate_text  = findViewById(R.id.number_plate_text);;

        goback = findViewById(R.id.goBack);
        cart_type_spinner = findViewById(R.id.car_type_spinner);
        car_brand_editText = findViewById(R.id.car_brand_editText);
        car_model_editText = findViewById(R.id.car_model_editText);
        number_plate_editText = findViewById(R.id.number_plate_editText);
        success_message = findViewById(R.id.success_message);
        loading = findViewById(R.id.loading);
        addVehicle_button = findViewById(R.id.add_vehicle_button);

        //setting the font styles
        one_last_step_text.setTypeface(quicksand_regular);
        car_details_text.setTypeface(quicksand_light);
        car_type_text.setTypeface(quicksand_regular);
        car_model_text.setTypeface(quicksand_regular);
        car_brand_text.setTypeface(quicksand_regular);
        car_number_plate_text.setTypeface(quicksand_regular);
        success_message.setTypeface(quicksand_regular);
        addVehicle_button.setTypeface(quicksand_regular);

        //        setting the adapater for the car type spinner
        cart_type_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                car_types_string = car_types[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                car_types_string = "Saloon";
            }
        });
        ArrayAdapter<String> the_vehicle = new ArrayAdapter<String>(One_Last_Step_User.this,android.R.layout.simple_list_item_1,car_types);
        cart_type_spinner.setAdapter(the_vehicle);

        goback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mauth.getCurrentUser() != null){
                    Intent intent = new Intent(Intent.ACTION_MAIN);
                    intent.addCategory(Intent.CATEGORY_HOME);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }else{
                    finish();
                }
            }
        });

        addVehicle_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                car_brand_string = car_brand_editText.getText().toString().trim();
                car_model_string = car_model_editText.getText().toString().trim();
                car_number_plate_string = number_plate_editText.getText().toString().trim();
                if(isNetworkAvailable()){
                    if(!car_brand_string.equals("") && !car_model_string.equals("")
                            && !car_number_plate_string.equals("")){
                        AddToDatabase(car_types_string,car_brand_string,car_model_string,car_number_plate_string);
                    }else{
                        loading.setVisibility(View.GONE);
                        success_message.setVisibility(View.VISIBLE);
                        success_message.setTextColor(getResources().getColor(R.color.red));
                        success_message.setText("Fields required");
                    }
                }else{
                    loading.setVisibility(View.GONE);
                    success_message.setVisibility(View.VISIBLE);
                    success_message.setTextColor(getResources().getColor(R.color.red));
                    success_message.setText("No internet connection");
                }
            }
        });
    }

    private void AddToDatabase(String car_types_string, String car_brand_string, String car_model_string, String car_number_plate_string) {
        loading.setVisibility(View.VISIBLE);
        try {
            add_car_reference = FirebaseDatabase.getInstance().getReference("cars").child(mauth.getCurrentUser().getUid());
            add_car_reference.child("type").setValue(car_types_string);
            add_car_reference.child("brand").setValue(car_brand_string);
            add_car_reference.child("model").setValue(car_model_string);
            add_car_reference.child("number_plate").setValue(car_number_plate_string);
            loading.setVisibility(View.GONE);
            success_message.setVisibility(View.VISIBLE);
            success_message.setTextColor(getResources().getColor(R.color.green));
            success_message.setText("Car added successfully");
            one_last_accessor.put("added_car",true);
            Intent gotouserPage = new Intent(One_Last_Step_User.this,User_mainActivity.class);
            gotouserPage.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(gotouserPage);
        }catch (NullPointerException e){

        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(one_last_accessor.getBoolean("added_car")){
            Intent gotouserPage = new Intent(One_Last_Step_User.this,User_mainActivity.class);
            gotouserPage.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(gotouserPage);
        }else{
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
