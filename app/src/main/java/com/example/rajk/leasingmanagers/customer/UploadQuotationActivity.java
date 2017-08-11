package com.example.rajk.leasingmanagers.customer;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;

import com.example.rajk.leasingmanagers.MainViews.TaskHome;
import com.example.rajk.leasingmanagers.R;

public class UploadQuotationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_upload_quotation);
        Intent intent = getIntent();
        String custId = intent.getStringExtra("custId");
        String custName = intent.getStringExtra("custName");

        getSupportActionBar().setTitle(custName);

        Bundle bundle = new Bundle();
        bundle.putString("custId", custId);
        bundle.putString("custName", custName);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Fragment fragment = new TaskHome();
        fragment.setArguments(bundle);
        fragmentTransaction.add(R.id.fragment_container, fragment);
        fragmentTransaction.commit();
    }

}