package com.example.rajk.leasingmanagers.ForwardTask;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import com.example.rajk.leasingmanagers.MainViews.TaskDetail;
import com.example.rajk.leasingmanagers.R;
import com.example.rajk.leasingmanagers.customer.UploadQuotationActivity;
import com.example.rajk.leasingmanagers.employee.Employee;
import com.example.rajk.leasingmanagers.employee.RecAdapter_emp;
import com.example.rajk.leasingmanagers.listener.ClickListener;
import com.example.rajk.leasingmanagers.listener.RecyclerTouchListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import static com.example.rajk.leasingmanagers.LeasingManagers.DBREF;

public class forwardTask extends AppCompatActivity {
    RecyclerView recview;
    RecAdapter_emp adapter;
    List<Employee> list = new ArrayList<Employee>();
    Employee emp;
    ProgressDialog pDialog;
    String task_id,custId;
    ArrayList<String> taskIds;
    Boolean forQuotation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forward_task);
        new net().execute();
        Intent intent = getIntent();

        forQuotation = intent.getBooleanExtra("forQuotation",false);
        if(forQuotation==true)
        {
            taskIds=intent.getStringArrayListExtra("taskIds");
            custId = intent.getStringExtra("custId");
            //get the list of taskIds
        }
        else
        {
            task_id = intent.getStringExtra("task_id");
        }

        recview = (RecyclerView)findViewById(R.id.recview);
        recview.setLayoutManager(new LinearLayoutManager(this));
        recview.setItemAnimator(new DefaultItemAnimator());
        recview.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));

        adapter = new RecAdapter_emp(list, getApplicationContext());
        recview.setAdapter(adapter);

        recview.addOnItemTouchListener(new RecyclerTouchListener(this, recview, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Employee item = list.get(position);
                Intent i = new Intent(forwardTask.this, forwardTaskScreen2.class);
                i.putExtra("id", item.getUsername());
                i.putExtra("name",item.getName());
                i.putExtra("designation",item.getDesignation());
                if(forQuotation==false)
                i.putExtra("task_id",task_id);

                else
                {
                    i.putExtra("forQuotation",forQuotation);
                    i.putStringArrayListExtra("taskIds",taskIds);
                    i.putExtra("custId",custId);
                }

                startActivity(i);
                finish();
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
            final DatabaseReference db = FirebaseDatabase.getInstance().getReference().child("MeChat").child("Employee").getRef();

            final int[] n = {0};
            db.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    n[0] = (int) dataSnapshot.getChildrenCount();
                    if (n[0]>0) {
                        db.addChildEventListener(new ChildEventListener() {
                            @Override
                            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                                if (!dataSnapshot.hasChildren()) {
                                    pDialog.dismiss();
                                }
                                emp = dataSnapshot.getValue(Employee.class);
                                list.add(emp);
                                adapter.notifyDataSetChanged();

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
                    }
                    else
                    {
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
        if (forQuotation == false)
        {
            Intent intent = new Intent(forwardTask.this, TaskDetail.class);
            intent.putExtra("task_id", task_id);
            startActivity(intent);
            finish();
        }
        else
        {
            Intent intent =new Intent(this, UploadQuotationActivity.class);
            intent.putExtra("custId",custId);
            intent.putExtra("forQuotation",forQuotation);
            startActivity(intent);
            finish();
        }
    }
}
