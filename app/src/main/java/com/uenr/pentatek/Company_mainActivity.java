package com.uenr.pentatek;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.uenr.pentatek.Adapters.ProblemAdapter;
import com.uenr.pentatek.Models.Problems;

import java.util.ArrayList;

public class Company_mainActivity extends AppCompatActivity {

    private ArrayList problemsArray = new ArrayList<Problems>();
    private RecyclerView problems_RecyclerView;
    private RecyclerView.Adapter problems_Adapter;
    private String customer_fname,customer_lname, customer_email,customer_number;
    private Accessories company_accessor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_company_main);
        getSupportActionBar().setTitle("PentaTek | Company");
        company_accessor = new Accessories(Company_mainActivity.this);

        problems_RecyclerView = findViewById(R.id.car_problem_recyclerView);
        //reviews adapter settings starts here
        if(isNetworkAvailable()){
            getProblem_IDs();
        }else{
            Toast.makeText(Company_mainActivity.this,"No Internet Connection",Toast.LENGTH_LONG).show();
        }
        problems_RecyclerView.setHasFixedSize(true);

        problems_Adapter = new ProblemAdapter(getFromDatabase(),Company_mainActivity.this);
        problems_RecyclerView.setAdapter(problems_Adapter);
    }

    private void getProblem_IDs() {
        try{
            DatabaseReference getProblem = FirebaseDatabase.getInstance().getReference("problems")
                    .child("company1");

            getProblem.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()){
                        for(DataSnapshot child : dataSnapshot.getChildren()){
                            Fetch_user_details(child.getKey());
                        }
                    }else{
//                    Toast.makeText(getActivity(),"Cannot get ID",Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(Company_mainActivity.this,"Cancelled",Toast.LENGTH_LONG).show();
                }
            });
        }catch (NullPointerException e){

        }
    }

    private void Fetch_user_details(final String key) {
        DatabaseReference getUserDetials = FirebaseDatabase.getInstance().getReference("users").child(key);
        getUserDetials.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    for(DataSnapshot child : dataSnapshot.getChildren()){
                        if(child.getKey().equals("first_name")){
                            customer_fname = child.getValue().toString();
                        }

                        if(child.getKey().equals("last_name")){
                            customer_lname = child.getValue().toString();
                        }

                        if(child.getKey().equals("phone_number")){
                            customer_number = child.getValue().toString();
                        }

                        if(child.getKey().equals("email")){
                            customer_email = child.getValue().toString();
                        }

                        else{
//                            Toast.makeText(getActivity(),"Couldn't fetch posts",Toast.LENGTH_LONG).show();

                        }
                    }
                    String customer_key = key;
                    Problems obj = new Problems(customer_key,customer_fname,customer_lname,customer_number,customer_email);
                    problemsArray.add(obj);
                    problems_RecyclerView.setAdapter(problems_Adapter);
                    problems_Adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(Company_mainActivity.this,"Cancelled",Toast.LENGTH_LONG).show();

            }
        });
    }

    public ArrayList<Problems> getFromDatabase(){
        return problemsArray;
    }


    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

}
