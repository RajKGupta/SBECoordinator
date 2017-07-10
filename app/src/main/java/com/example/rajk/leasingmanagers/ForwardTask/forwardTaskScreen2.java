package com.example.rajk.leasingmanagers.ForwardTask;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.codetroopers.betterpickers.calendardatepicker.CalendarDatePickerDialogFragment;
import com.codetroopers.betterpickers.calendardatepicker.MonthAdapter;
import com.example.rajk.leasingmanagers.MainViews.TaskDetail;
import com.example.rajk.leasingmanagers.R;
import com.example.rajk.leasingmanagers.customer.Cust_details;
import com.example.rajk.leasingmanagers.model.CompletedBy;
import com.example.rajk.leasingmanagers.model.QuotationBatch;
import com.example.rajk.leasingmanagers.tablayout.Tabs;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class forwardTaskScreen2 extends FragmentActivity implements CalendarDatePickerDialogFragment.OnDateSetListener {
    Button submit;
    EditText name,designation,enddate,note,startDate;
    String empId,empName,empDesig;
    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
    String curdate,task_id,custId;
    ArrayList<String> taskIds;
    Boolean forQuotation;

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
        forQuotation = intent.getBooleanExtra("forQuotation",false);

        if(forQuotation==true)
        {
            taskIds = intent.getStringArrayListExtra("taskIds");
            custId= intent.getStringExtra("custId");
        }

        else
        task_id = intent.getStringExtra("task_id");

        name.setText(empName);
        designation.setText(empDesig);

        Calendar c = Calendar.getInstance();
        curdate = dateFormat.format(c.getTime());
        startDate.setText(curdate);
        enddate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Date now = new Date();
                Calendar calendar = new GregorianCalendar();
                calendar.setTime(now);
                MonthAdapter.CalendarDay minDate = new MonthAdapter.CalendarDay(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

                CalendarDatePickerDialogFragment cdp = new CalendarDatePickerDialogFragment()
                        .setOnDateSetListener(forwardTaskScreen2.this)
                        .setFirstDayOfWeek(Calendar.SUNDAY)
                        .setDateRange( minDate,null)
                        .setDoneText("Ok")
                        .setCancelText("Cancel").setThemeLight();
                cdp.show(getSupportFragmentManager(), "Select Day, Month and Year.");

            }
        });
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String deadline = enddate.getText().toString().trim();
                String cooordnote = note.getText().toString().trim();
                if (TextUtils.isEmpty(deadline)) {
                    Toast.makeText(forwardTaskScreen2.this, "Enter details...", Toast.LENGTH_SHORT).show();
                }
                else {
                    if (forQuotation == false) {
                        CompletedBy completedBy = new CompletedBy(empId, curdate, deadline, cooordnote);
                        DatabaseReference dbAssigned = FirebaseDatabase.getInstance().getReference().child("MeChat").child("Task").child(task_id).child("AssignedTo").child(empId);
                        dbAssigned.setValue(completedBy);

                        DatabaseReference dbEmployee = FirebaseDatabase.getInstance().getReference().child("MeChat").child("Employee").child(empId).child("AssignedTask").child(task_id);
                        dbEmployee.setValue("pending"); //for employee

                        Toast.makeText(forwardTaskScreen2.this, "Task Assigned to " + empName, Toast.LENGTH_SHORT).show();
                        Intent intent1 = new Intent(forwardTaskScreen2.this, TaskDetail.class);
                        intent1.putExtra("task_id", task_id);
                        startActivity(intent1);
                        finish();
                    } else {
                        for (String taskid : taskIds) {
                            CompletedBy completedBy = new CompletedBy(empId, curdate, deadline, cooordnote);
                            DatabaseReference dbAssigned = FirebaseDatabase.getInstance().getReference().child("MeChat").child("Task").child(taskid).child("AssignedTo").child(empId);
                            dbAssigned.setValue(completedBy);
                            DatabaseReference dbEmployee = FirebaseDatabase.getInstance().getReference().child("MeChat").child("Employee").child(empId).child("AssignedTask").child(custId).child("listoftasks");
                            dbEmployee.child(taskid).setValue("pending"); //for employee
                        }
                        DatabaseReference dbEmployee = FirebaseDatabase.getInstance().getReference().child("MeChat").child("Employee").child(empId).child("AssignedTask").child(custId);
                        dbEmployee.child("deadline").setValue(deadline);
                        dbEmployee.child("startDate").setValue(curdate);
                        dbEmployee.child("coordnote").setValue(cooordnote);
                        Toast.makeText(forwardTaskScreen2.this, "Task Assigned to " + empName, Toast.LENGTH_SHORT).show();
                        Intent intent1 = new Intent(forwardTaskScreen2.this, Cust_details.class);
                        intent1.putExtra("id", custId);
                        startActivity(intent1);
                        finish();

                    }
                }
            }
        });


    }

    @Override
    public void onBackPressed() {
        if(forQuotation==false)
        {
            Intent intent = new Intent(forwardTaskScreen2.this, forwardTask.class);
            intent.putExtra("task_id", task_id);
            startActivity(intent);
            finish();
        }
        else
        {
            //TODO goto customer details activity

        }
    }

    @Override
    public void onDateSet(CalendarDatePickerDialogFragment dialog, int year, int monthOfYear, int dayOfMonth) {
        enddate.setText(dayOfMonth+"/"+(monthOfYear+1)+"/"+year);

    }
}
