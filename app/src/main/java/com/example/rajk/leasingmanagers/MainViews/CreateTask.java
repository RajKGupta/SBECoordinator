package com.example.rajk.leasingmanagers.MainViews;

import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.rajk.leasingmanagers.R;
import com.example.rajk.leasingmanagers.customer.Cust_details;
import com.example.rajk.leasingmanagers.model.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class CreateTask extends AppCompatActivity {
    DatabaseReference dbRef;
    EditText taskName,startDate,endDate,quantity,description,custId;
    String customerId,customerName,curdate;
    Button submit_task;
    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

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
        submit_task = (Button)findViewById(R.id.submit_task);
        Calendar c = Calendar.getInstance();
        curdate = dateFormat.format(c.getTime());
        startDate.setText(curdate);


        submit_task.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createTask();
            }
        });
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

        if(TextUtils.isEmpty(taskname)||TextUtils.isEmpty(qty)||TextUtils.isEmpty(desc)||TextUtils.isEmpty(enddate)||TextUtils.isEmpty(startdate)) {
            Toast.makeText(CreateTask.this,"Fill all the details",Toast.LENGTH_SHORT).show();
        }
        else
            {
            Task newTask = new Task("task" + curTime, taskname, startdate, enddate, qty, desc, customerId, getRandomMaterialColor("400"));
            dbRef.child("Task").child("task" + curTime).setValue(newTask);
            dbRef.child("Customer").child(customerId).child("Task").child("task" + curTime).setValue("pending");
                Toast.makeText(CreateTask.this,"Task Created",Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(CreateTask.this, Cust_details.class);
                intent.putExtra("id",customerId);
                startActivity(intent);
                finish();
            }
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

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(CreateTask.this, Cust_details.class);
        intent.putExtra("id",customerId);
        startActivity(intent);
        finish();
    }
}
