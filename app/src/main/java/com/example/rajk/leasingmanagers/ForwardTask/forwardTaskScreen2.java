package com.example.rajk.leasingmanagers.ForwardTask;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.rajk.leasingmanagers.MainViews.TaskDetail;
import com.example.rajk.leasingmanagers.R;
import com.example.rajk.leasingmanagers.model.CompletedBy;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class forwardTaskScreen2 extends AppCompatActivity {
    Button submit;
    EditText name,designation,enddate,note,startDate;
    String empId,empName,empDesig;
    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
    String curdate,task_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forward_task_screen2);
        name = (EditText)findViewById(R.id.name);
        designation = (EditText)findViewById(R.id.designation);
        enddate = (EditText)findViewById(R.id.deadline);
        note = (EditText)findViewById(R.id.note);
        submit = (Button)findViewById(R.id.submit);
        startDate = (EditText)findViewById(R.id.startDate);

        Intent intent =getIntent();
        empId = intent.getStringExtra("id");
        empName = intent.getStringExtra("name");
        empDesig=intent.getStringExtra("designation");
        task_id = intent.getStringExtra("task_id");

        name.setText(empName);
        designation.setText(empDesig);

        Calendar c = Calendar.getInstance();
        curdate = dateFormat.format(c.getTime());
        startDate.setText(curdate);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String deadline  = enddate.getText().toString().trim();
                String cooordnote = note.getText().toString().trim();
                String id = Calendar.getInstance().getTimeInMillis()+"";
                CompletedBy completedBy = new CompletedBy(empId,curdate,deadline,cooordnote,id);
                DatabaseReference dbAssigned = FirebaseDatabase.getInstance().getReference().child("MeChat").child("Task").child(task_id).child("AssignedTo").child(id);
                dbAssigned.setValue(completedBy);

                DatabaseReference dbEmployee = FirebaseDatabase.getInstance().getReference().child("MeChat").child("Employee").child(empId).child("AssignedTask").child(task_id);
                dbEmployee.setValue(task_id); //for employee

                Toast.makeText(forwardTaskScreen2.this,"Task Assigned to "+empName,Toast.LENGTH_SHORT).show();
                Intent intent1 =new Intent(forwardTaskScreen2.this, TaskDetail.class);
                intent1.putExtra("task_id",task_id);
                startActivity(intent1);
                finish();
            }
        });


    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(forwardTaskScreen2.this,forwardTask.class);
        intent.putExtra("task_id",task_id);
        startActivity(intent);
        finish();

    }
}
