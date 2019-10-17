package com.uenr.pentatek.Adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.uenr.pentatek.Accessories;
import com.uenr.pentatek.Automobile_MapsActivity;
import com.uenr.pentatek.Models.Problems;
import com.uenr.pentatek.Models.Recent_Problems;
import com.uenr.pentatek.R;

import java.util.ArrayList;

public class Recent_ProblemAdapter extends RecyclerView.Adapter<Recent_ProblemAdapter.ViewHolder>{
    ArrayList<Recent_Problems> itemList;
    Context context;

    public class ViewHolder extends RecyclerView.ViewHolder{
        View view;

        public ViewHolder(View v) {
            super(v);
            view = v;
        }
    }

    public Recent_ProblemAdapter(ArrayList<Recent_Problems> itemList, Context context){
        this.itemList  = itemList;
        this.context  = context;
    }

    @Override
    public Recent_ProblemAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.recent_problem_attachement,parent,false);
        ViewHolder vh = new ViewHolder(layoutView);
        return vh;
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final TextView problem_description = holder.view.findViewById(R.id.problem);
        TextView prize = holder.view.findViewById(R.id.the_prize);
        TextView status  =  holder.view.findViewById(R.id.status);
//        Typeface lovelo =Typeface.createFromAsset(context.getAssets(),  "fonts/lovelo.ttf");
//        title.setTypeface(lovelo);
//        message.setTypeface(lovelo);

        problem_description.setText(itemList.get(position).getProblem_description());
        prize.setText("Ghc " + itemList.get(position).getPrize());
        status.setText(itemList.get(position).getStatus());
        }

    @Override
    public int getItemCount() {
        return itemList.size();
    }
}
