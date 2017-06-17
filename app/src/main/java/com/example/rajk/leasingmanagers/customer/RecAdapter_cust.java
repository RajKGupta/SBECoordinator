package com.example.rajk.leasingmanagers.customer;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import com.example.rajk.leasingmanagers.R;

import java.util.List;

public class RecAdapter_cust extends RecyclerView.Adapter<RecAdapter_cust.RecHolder>{

    public List<Customer> list;
    Context context;

    RecAdapter_cust(List<Customer> list ,Context c){
        this.list = list;
        this.context = c;
    }

    @Override
    public RecHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cust_row,parent,false);
        return new RecHolder(view);
    }

    @Override
    public void onBindViewHolder(RecHolder holder, int position) {

        Customer item = list.get(position);
        holder.name.setText(item.getName());
        String iconText = item.getName().toUpperCase();
        holder.icon_text.setText(iconText.charAt(0) + "");
        holder.imgProfile.setImageResource(R.drawable.bg_circle);
        holder.imgProfile.setColorFilter(item.getColor());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void setItem(Customer item , int p){
        this.list.set(p,item);
    }

    // holder class
    public class RecHolder extends RecyclerView.ViewHolder{

        TextView name,icon_text;
        ImageView imgProfile;

        public RecHolder(View itemView) {
            super(itemView);

            name = (TextView) itemView.findViewById(R.id.name);
            icon_text =(TextView)itemView.findViewById(R.id.icon_text);
            imgProfile = (ImageView)itemView.findViewById(R.id.icon_profile);
        }
    }
    
}
