package com.example.rajk.leasingmanagers.MainViews;

import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;

import com.example.rajk.leasingmanagers.R;
import com.example.rajk.leasingmanagers.model.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;

public class CreateTask extends AppCompatActivity {
    DatabaseReference dbRef;
    EditText taskName,startDate,endDate,quantity,description,custId;
    String customerId,customerName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_task);
        dbRef= FirebaseDatabase.getInstance().getReference().child("MeChat");
        Intent intent = getIntent();
        customerName = intent.getStringExtra("customerName");
        customerId = intent.getStringExtra("customerId");

        taskName = (EditText) findViewById(R.id.taskName);
        startDate = (EditText) findViewById(R.id.startDate);
        endDate = (EditText) findViewById(R.id.endDate);
        quantity=(EditText) findViewById(R.id.quantity);
        description = (EditText) findViewById(R.id.description);
        custId = (EditText) findViewById(R.id.custId);
        custId.setText(customerId+": "+customerName);



    }
    void createTask()
    {
        String taskname = taskName.getText().toString().trim();

        String qty = quantity.getText().toString().trim();

        String desc = description.getText().toString().trim();

        String enddate = endDate.getText().toString().trim();

        String startdate= startDate.getText().toString().trim();

        long curTime = Calendar.getInstance().getTimeInMillis();
        curTime=9999999999999L-curTime;

        Task newTask = new Task("task"+curTime,taskname,startdate,enddate,qty,desc,customerId,getRandomMaterialColor("400"));
        dbRef.child("Task").child("task"+curTime).setValue(newTask);

        dbRef.child("Customer").child(customerId).child("Task").child("task"+curTime).setValue("task"+curTime);

    }
    private int getRandomMaterialColor(String typeColor) {
        int returnColor = Color.GRAY;
        int arrayId = getResources().getIdentifier("mdcolor_" + typeColor, "array", getPackageName());

        if (arrayId != 0) {
            TypedArray colors = getResources().obtainTypedArray(arrayId);
            int index = (int) (Math.random() * colors.length());
            returnColor = colors.getColor(index, Color.GRAY);
            colors.recycle();
        }
        return returnColor;
    }

}
