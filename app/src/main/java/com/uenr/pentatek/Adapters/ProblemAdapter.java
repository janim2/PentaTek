package com.uenr.pentatek.Adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.uenr.pentatek.Accessories;
import com.uenr.pentatek.Automobile_MapsActivity;
import com.uenr.pentatek.Company_mainActivity;
import com.uenr.pentatek.Models.Problems;
import com.uenr.pentatek.R;

import java.util.ArrayList;

public class ProblemAdapter extends RecyclerView.Adapter<ProblemAdapter.ViewHolder>{
    ArrayList<Problems> itemList;
    Context context;
    Accessories problem_accessor;
    String problem_,car_type, car_brand, car_model, car_plate;

    public class ViewHolder extends RecyclerView.ViewHolder{
        View view;

        public ViewHolder(View v) {
            super(v);
            view = v;
        }
    }

    public ProblemAdapter(ArrayList<Problems> itemList, Context context){
        this.itemList  = itemList;
        this.context  = context;
    }

    @Override
    public ProblemAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.problem_attachment,parent,false);
        ViewHolder vh = new ViewHolder(layoutView);
        return vh;
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final TextView customer_name = holder.view.findViewById(R.id.customer_name);
        TextView phone_number = holder.view.findViewById(R.id.phone_number);
        LinearLayout attachment_layout  =  holder.view.findViewById(R.id.attachment_layout);
//        Typeface lovelo =Typeface.createFromAsset(context.getAssets(),  "fonts/lovelo.ttf");
//        title.setTypeface(lovelo);
//        message.setTypeface(lovelo);

        customer_name.setText(itemList.get(position).getF_name() + " " + itemList.get(position).getL_name());
        phone_number.setText(itemList.get(position).getPhone_number());

        attachment_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gotoProblem_page = new Intent(v.getContext(), Automobile_MapsActivity.class);
                problem_accessor = new Accessories(context);
                problem_accessor.put("customer_fname",itemList.get(position).getF_name());
                problem_accessor.put("customer_lname",itemList.get(position).getL_name());
                problem_accessor.put("customer_phone",itemList.get(position).getPhone_number());
                problem_accessor.put("customer_email",itemList.get(position).getEmail());
                problem_accessor.put("customer_id",itemList.get(position).getCustomer_id());
                v.getContext().startActivity(gotoProblem_page);
                Fetch_Vehicle_Problem(itemList.get(position).getCustomer_id());
                Fetch_Vehicle_Info(itemList.get(position).getCustomer_id());
            }
        });
        }

    private void Fetch_Vehicle_Info(String customer_id) {
        problem_accessor = new Accessories(context);
        DatabaseReference getProblem = FirebaseDatabase.getInstance().getReference("cars")
                .child(customer_id);
        getProblem.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    for(DataSnapshot child : dataSnapshot.getChildren()){
                        if(child.getKey().equals("type")){
                            car_type = child.getValue().toString();
                            problem_accessor.put("the_car_type", car_type);
                        }
                        if(child.getKey().equals("brand")){
                            car_brand = child.getValue().toString();
                            problem_accessor.put("the_car_brand", car_brand);
                        }
                        if(child.getKey().equals("model")){
                            car_model = child.getValue().toString();
                            problem_accessor.put("the_car_model", car_model);
                        }
                        if(child.getKey().equals("number_plate")){
                            car_plate = child.getValue().toString();
                            problem_accessor.put("the_car_plate", car_plate);
                        }

                        else{
//                            Toast.makeText(getActivity(),"Couldn't fetch posts",Toast.LENGTH_LONG).show();

                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(context,"Cancelled",Toast.LENGTH_LONG).show();

            }
        });
    }


    @Override
    public int getItemCount() {
        return itemList.size();
    }

    private void Fetch_Vehicle_Problem(String key) {
        problem_accessor = new Accessories(context);
        DatabaseReference getProblem = FirebaseDatabase.getInstance().getReference("problems")
                .child("company1").child(key);
        getProblem.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    for(DataSnapshot child : dataSnapshot.getChildren()){
                        if(child.getKey().equals("problem_description")){
                            problem_ = child.getValue().toString();
                            problem_accessor.put("the_problem", problem_);
                        }

                        else{
//                            Toast.makeText(getActivity(),"Couldn't fetch posts",Toast.LENGTH_LONG).show();

                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(context,"Cancelled",Toast.LENGTH_LONG).show();

            }
        });

    }


}
