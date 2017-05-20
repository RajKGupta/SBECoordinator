package com.example.rajk.leasingmanagers;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;

import com.example.rajk.leasingmanagers.R;

public class NewUser extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_user);
        String[] states = getResources().getStringArray(R.array.list_of_countries);
        ArrayAdapter<String> adapterstate = new ArrayAdapter<String>
                (this,android.R.layout.simple_dropdown_item_1line,states);
        state.setAdapter(adapterstate);
        state.setThreshold(1);//will start working from first character

    }
}
