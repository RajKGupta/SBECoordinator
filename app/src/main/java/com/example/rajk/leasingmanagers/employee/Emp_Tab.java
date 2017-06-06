package com.example.rajk.leasingmanagers.employee;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import com.example.rajk.leasingmanagers.R;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class Emp_Tab extends AppCompatActivity implements RecAdapter_emp.ItemClickCallback {

    RecyclerView recview;
    RecAdapter_emp adapter;
    List<Employee> list = new ArrayList<Employee>();
    Employee emp;
    ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emp_tab);


        new net().execute();

        recview = (RecyclerView) findViewById(R.id.recview);
        recview.setLayoutManager(new LinearLayoutManager(this));
        recview.setItemAnimator(new DefaultItemAnimator());
        recview.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));

        adapter = new RecAdapter_emp(list, this);
        recview.setAdapter(adapter);
        adapter.setItemClickCallback(this);

    }

    @Override
    public void onItemClick(int p) {
        Employee item = list.get(p);
        Intent i = new Intent(Emp_Tab.this, Emp_details.class);
        i.putExtra("id", item.getUsername());
        startActivity(i);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.emp_add, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.add) {
            startActivity(new Intent(Emp_Tab.this, Emp_add.class));
        }
        return true;
    }


    class net extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(Emp_Tab.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected Void doInBackground(Void... params) {

            DatabaseReference db = FirebaseDatabase.getInstance().getReference().child("MeChat").child("Employee").getRef();

            db.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
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

            return null;
        }

    }

}
