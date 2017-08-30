package com.example.rajk.leasingmanagers.ForwardTask;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.codetroopers.betterpickers.calendardatepicker.CalendarDatePickerDialogFragment;
import com.codetroopers.betterpickers.calendardatepicker.MonthAdapter;
import com.example.rajk.leasingmanagers.CoordinatorLogin.CoordinatorSession;
import com.example.rajk.leasingmanagers.LeasingManagers;
import com.example.rajk.leasingmanagers.MainViews.TaskDetail;
import com.example.rajk.leasingmanagers.R;
import com.example.rajk.leasingmanagers.customer.Cust_details;
import com.example.rajk.leasingmanagers.model.CompletedBy;
import com.example.rajk.leasingmanagers.model.QuotationBatch;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import static com.example.rajk.leasingmanagers.LeasingManagers.DBREF;
import static com.example.rajk.leasingmanagers.LeasingManagers.sendNotif;
import static com.example.rajk.leasingmanagers.LeasingManagers.simpleDateFormat;

public class forwardTaskScreen2 extends FragmentActivity implements CalendarDatePickerDialogFragment.OnDateSetListener {
    Button submit;
    EditText name, designation, enddate, note, startDate;
    String empId, empName, empDesig;
    String curdate, task_id, custId, mykey, myname;
    ArrayList<String> taskIds;
    Boolean forQuotation;
    CoordinatorSession coordinatorSession;
    DatabaseReference quotation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forward_task_screen2);
        name = (EditText) findViewById(R.id.name);
        designation = (EditText) findViewById(R.id.designation);
        enddate = (EditText) findViewById(R.id.deadline);
        note = (EditText) findViewById(R.id.note);
        submit = (Button) findViewById(R.id.submit);
        startDate = (EditText) findViewById(R.id.startDate);
        coordinatorSession = new CoordinatorSession(this);
        mykey = coordinatorSession.getUsername();
        myname = coordinatorSession.getName();
        Intent intent = getIntent();
        empId = intent.getStringExtra("id");
        empName = intent.getStringExtra("name");
        empDesig = intent.getStringExtra("designation");
        forQuotation = intent.getBooleanExtra("forQuotation", false);

        if (forQuotation == true) {
            taskIds = intent.getStringArrayListExtra("taskIds");
            custId = intent.getStringExtra("custId");
        } else {
            task_id = intent.getStringExtra("task_id");
        }

        name.setText(empName);
        designation.setText(empDesig);

        quotation = DBREF.child("Quotation").getRef();


        Calendar c = Calendar.getInstance();
        curdate = simpleDateFormat.format(c.getTime());
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
                        .setDateRange(minDate, null)
                        .setDoneText("Ok")
                        .setCancelText("Cancel").setThemeLight();
                cdp.show(getSupportFragmentManager(), "Select Day, Month and Year.");

            }
        });
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String deadline = enddate.getText().toString().trim();
                final String cooordnote = note.getText().toString().trim();
                if (forQuotation == false) {
                    DBREF.child("Task").child(task_id).child("name").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if(dataSnapshot.exists()) {
                                String taskName = dataSnapshot.getValue(String.class);
                                CompletedBy completedBy = new CompletedBy(empId, curdate, deadline, cooordnote, mykey, myname);
                                DatabaseReference dbAssigned = DBREF.child("Task").child(task_id).child("AssignedTo").child(empId);
                                dbAssigned.setValue(completedBy);

                                DatabaseReference dbEmployee = DBREF.child("Employee").child(empId).child("AssignedTask").child(task_id);
                                dbEmployee.setValue("pending"); //for employee

                                Toast.makeText(forwardTaskScreen2.this, "Task Assigned to " + empName, Toast.LENGTH_SHORT).show();
                                Intent intent1 = new Intent(forwardTaskScreen2.this, TaskDetail.class);
                                intent1.putExtra("task_id", task_id);
                                String contentforme = "You assigned " + taskName + " task to " + empName;
                                sendNotif(mykey, mykey, "assignment", contentforme, task_id);
                                String contentforother = "Coordinator " + coordinatorSession.getName() + " assigned " + taskName + " to you";
                                sendNotif(mykey, empId, "assignment", contentforother, task_id);
                                startActivity(intent1);
                                finish();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                } else {
                    DBREF.child("Users").child("Usersessions").child(custId).child("name").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                String customername = dataSnapshot.getValue(String.class);
                                final long timestamp = Calendar.getInstance().getTimeInMillis();
                                for (String taskid : taskIds) {
                                    CompletedBy completedBy = new CompletedBy(empId, curdate, deadline, cooordnote, mykey, myname);
                                    DatabaseReference dbAssigned = DBREF.child("Task").child(taskid).child("AssignedTo").child(empId);
                                    dbAssigned.setValue(completedBy);
                                    DatabaseReference dbEmployee = DBREF.child("Employee").child(empId).child("AssignedTask").child(timestamp + "").child("listoftasks");
                                    dbEmployee.child(taskid).setValue("pending"); //for employee
                                }
                                DatabaseReference dbEmployee = DBREF.child("Employee").child(empId).child("AssignedTask").child(timestamp + "");
                                dbEmployee.child("deadline").setValue(deadline);
                                dbEmployee.child("startDate").setValue(curdate);
                                dbEmployee.child("coordnote").setValue(cooordnote);
                                String contentforme = "You assigned " + customername + " tasks for quotation to " + empName;
                                sendNotif(mykey, mykey, "forquotation", contentforme, timestamp + "");
                                String contentforother = "Coordinator " + coordinatorSession.getName() + "forquotation" + " " + customername + " tasks for quotation";
                                sendNotif(mykey, empId, "assignment", contentforother, timestamp + "");


                                QuotationBatch temp = new QuotationBatch();
                                String qId = "quote" + timestamp;
                                temp.setEndDate(deadline);
                                temp.setId(qId);
                                temp.setNote(cooordnote);
                                temp.setStartDate(curdate);
                                temp.setColor(-1);
                                temp.setEmpId(empId);


                                quotation.child(qId).setValue(temp);
                                for (String taskid : taskIds) {
                                    quotation.child(qId).child("tasks").child(taskid).setValue("pending");
                                }

                                Toast.makeText(forwardTaskScreen2.this, "Task Assigned to " + empName, Toast.LENGTH_SHORT).show();
                                Intent intent1 = new Intent(forwardTaskScreen2.this, Cust_details.class);
                                intent1.putExtra("id", custId);
                                startActivity(intent1);
                                finish();

                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                }
            }
        });


    }

    @Override
    public void onBackPressed() {
        if (forQuotation == false) {
            Intent intent = new Intent(forwardTaskScreen2.this, forwardTask.class);
            intent.putExtra("task_id", task_id);
            startActivity(intent);
            finish();
        } else {
            Intent intent = new Intent(forwardTaskScreen2.this, forwardTask.class);
            intent.putStringArrayListExtra("taskIds", taskIds);
            intent.putExtra("custId", custId);
            intent.putExtra("forQuotation", forQuotation);
            startActivity(intent);
            finish();

        }
    }

    @Override
    public void onDateSet(CalendarDatePickerDialogFragment dialog, int year, int monthOfYear, int dayOfMonth) {
        String day=String.valueOf(dayOfMonth);
        if(dayOfMonth<10)
        {
            day= "0"+String.valueOf(dayOfMonth);
        }
        if(monthOfYear<9)
        enddate.setText(day + "-0" + (monthOfYear + 1) + "-" + year);
        else
        enddate.setText(day + "-" + (monthOfYear + 1) + "-" + year);

    }
}
