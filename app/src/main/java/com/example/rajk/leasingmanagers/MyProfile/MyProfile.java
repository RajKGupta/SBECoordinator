package com.example.rajk.leasingmanagers.MyProfile;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;

import com.example.rajk.leasingmanagers.CoordinatorLogin.CoordinatorSession;
import com.example.rajk.leasingmanagers.R;
import com.example.rajk.leasingmanagers.model.Coordinator;
import com.example.rajk.leasingmanagers.tablayout.Tabs;

public class MyProfile extends AppCompatActivity {

    EditText name, num, add;
    CoordinatorSession session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);

        name = (EditText)findViewById(R.id.name);
        num = (EditText)findViewById(R.id.num);
        add = (EditText)findViewById(R.id.add);

        session = new CoordinatorSession(getApplicationContext());


    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, Tabs.class);
        startActivity(intent);
        finish();
    }
}
