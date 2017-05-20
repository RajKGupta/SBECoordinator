package com.example.rajk.leasingmanagers;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class NewTopic extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_topic);

        String[] states = getResources().getStringArray(R.array.list_of_address);
        ArrayAdapter<String> adapterstate = new ArrayAdapter<String>
                (this,android.R.layout.simple_dropdown_item_1line,states);
        state.setAdapter(adapterstate);
        state.setThreshold(1);
    }
}
