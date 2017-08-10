package com.example.rajk.leasingmanagers.ForwardTask;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import com.example.rajk.leasingmanagers.helper.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.example.rajk.leasingmanagers.CoordinatorLogin.CoordinatorSession;
import com.example.rajk.leasingmanagers.MainViews.TaskDetail;
import com.example.rajk.leasingmanagers.R;
import com.example.rajk.leasingmanagers.customer.UploadQuotationActivity;
import com.example.rajk.leasingmanagers.employee.Employee;
import com.example.rajk.leasingmanagers.employee.RecAdapter_emp;
import com.example.rajk.leasingmanagers.listener.ClickListener;
import com.example.rajk.leasingmanagers.listener.RecyclerTouchListener;
import com.example.rajk.leasingmanagers.model.CompletedBy;
import com.example.rajk.leasingmanagers.model.Coordinator;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static com.example.rajk.leasingmanagers.LeasingManagers.DBREF;
import static com.example.rajk.leasingmanagers.LeasingManagers.sendNotif;

public class forwardTask extends AppCompatActivity {
    RecyclerView recview;
    RecAdapter_emp adapter;
    List<Employee> list = new ArrayList<Employee>();
    Employee emp;
    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
    ProgressDialog pDialog;
    String task_id, custId, mykey, custName;
    ArrayList<String> taskIds;
    Boolean forQuotation;
    String swaping_id = "", curdate;
    CoordinatorSession session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forward_task);
        new net().execute();
        Intent intent = getIntent();

        session = new CoordinatorSession(getApplicationContext());
        mykey = session.getUsername();
        if (intent.hasExtra("swaping_id")) {
            swaping_id = intent.getStringExtra("swaping_id");
        }
        final Calendar c = Calendar.getInstance();
        curdate = dateFormat.format(c.getTime());

        forQuotation = intent.getBooleanExtra("forQuotation", false);
        if (forQuotation == true) {
            taskIds = intent.getStringArrayListExtra("taskIds");
            custId = intent.getStringExtra("custId");
            custName = intent.getStringExtra("custName");
            //get the list of taskIds
        } else {
            task_id = intent.getStringExtra("task_id");
        }

        recview = (RecyclerView) findViewById(R.id.recview);
        recview.setLayoutManager(new LinearLayoutManager(this));
        recview.setItemAnimator(new DefaultItemAnimator());
        recview.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));

        adapter = new RecAdapter_emp(list, getApplicationContext());
        recview.setAdapter(adapter);

        recview.addOnItemTouchListener(new RecyclerTouchListener(this, recview, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                final Employee item = list.get(position);

                if (!swaping_id.equals("")) {
                    DBREF.child("Task").child(task_id).child("name").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            final String taskName = dataSnapshot.getValue(String.class);

                            //cancel job
                            final DatabaseReference dbCancelJob = DBREF.child("Task").child(task_id).child("AssignedTo").child(swaping_id).getRef();

                            dbCancelJob.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {

                                    CompletedBy completedBy = dataSnapshot.getValue(CompletedBy.class);
                                    completedBy.setEmpId(item.getUsername());
                                    /// /reassign job
                                    DatabaseReference dbAssigned = DBREF.child("Task").child(task_id).child("AssignedTo").child(item.getUsername());
                                    dbAssigned.setValue(completedBy);

                                    dbCancelJob.removeValue();
                                    DatabaseReference dbEmployee = DBREF.child("Employee").child(swaping_id).child("AssignedTask").child(task_id);
                                    dbEmployee.removeValue(); //for employee

                                    dbEmployee = DBREF.child("Employee").child(item.getUsername()).child("AssignedTask").child(task_id);
                                    dbEmployee.setValue("pending"); //for employee

                                    String contentforme = "You swapped " + taskName + " task to " + item.getName();
                                    sendNotif(mykey, mykey, "swapJob", contentforme, task_id);
                                    String contentforother = "Coordinator " + session.getName() + " relieved you of " + taskName;
                                    sendNotif(mykey, swaping_id, "cancelJob", contentforother, task_id);
                                    contentforother = "Coordinator " + session.getName() + " assigned " + taskName + " to you";
                                    sendNotif(mykey, item.getUsername(), "assignment", contentforother, task_id);
                                    Intent intent1 = new Intent(forwardTask.this, TaskDetail.class);
                                    intent1.putExtra("task_id", task_id);
                                    startActivity(intent1);
                                    finish();
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                } else {
                    Intent i = new Intent(forwardTask.this, forwardTaskScreen2.class);
                    i.putExtra("id", item.getUsername());
                    i.putExtra("name", item.getName());
                    i.putExtra("designation", item.getDesignation());
                    if (forQuotation == false) {
                        i.putExtra("task_id", task_id);

                    } else {
                        i.putExtra("forQuotation", forQuotation);
                        i.putStringArrayListExtra("taskIds", taskIds);
                        i.putExtra("custId", custId);
                        i.putExtra("custName", custName);
                    }

                    startActivity(i);
                    finish();
                }
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
    }

    class net extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(forwardTask.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(true);
            pDialog.show();

        }

        @Override
        protected Void doInBackground(Void... params) {
            final DatabaseReference db = DBREF.child("Employee").getRef();

            final int[] n = {0};
            db.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    n[0] = (int) dataSnapshot.getChildrenCount();
                    if (n[0] > 0) {
                        db.addChildEventListener(new ChildEventListener() {
                            @Override
                            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                                if (!dataSnapshot.hasChildren()) {
                                    pDialog.dismiss();
                                }
                                emp = dataSnapshot.getValue(Employee.class);
                                if (forQuotation == true) {
                                    if (emp.getDesignation().equals("Quotation")) {
                                        list.add(emp);
                                        adapter.notifyDataSetChanged();
                                    }
                                } else {
                                    list.add(emp);
                                    adapter.notifyDataSetChanged();
                                }
                                // Dismiss the progress dialog
                                if (pDialog.isShowing())
                                    pDialog.dismiss();
                            }

                            @Override
                            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                            }

                            @Override
                            public void onChildRemoved(DataSnapshot dataSnapshot) {

                            }

                            @Override
                            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    } else {
                        pDialog.dismiss();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
            return null;
        }


    }

    @Override
    public void onBackPressed() {
        if (forQuotation == false) {
            Intent intent = new Intent(forwardTask.this, TaskDetail.class);
            intent.putExtra("task_id", task_id);
            startActivity(intent);
            finish();
        } else {
            Intent intent = new Intent(this, UploadQuotationActivity.class);
            intent.putExtra("custId", custId);
            intent.putExtra("custName", custName);
            intent.putExtra("forQuotation", forQuotation);
            startActivity(intent);
            finish();
        }
    }
}
