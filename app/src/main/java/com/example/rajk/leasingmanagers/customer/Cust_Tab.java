package com.example.rajk.leasingmanagers.customer;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.example.rajk.leasingmanagers.R;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Cust_Tab extends AppCompatActivity implements RecAdapter_cust.ItemClickCallback {

    RecyclerView recview;
    RecAdapter_cust adapter;
    List<Customer> list = new ArrayList<>();
    Customer cust = new Customer();
    Customer temp_cust = new Customer();
    ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cust_tab);


        recview = (RecyclerView) findViewById(R.id.recview);
        recview.setLayoutManager(new LinearLayoutManager(this));
        recview.setItemAnimator(new DefaultItemAnimator());
        recview.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));

        adapter = new RecAdapter_cust(list, this);
        recview.setAdapter(adapter);
        adapter.setItemClickCallback(this);

        new net().execute();

    }

    @Override
    public void onItemClick(int p) {
        Customer item = list.get(p);
        Intent i = new Intent(Cust_Tab.this, Cust_details.class);
        i.putExtra("id", item.getId());
        startActivity(i);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.cust_add, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item1:
                startActivity(new Intent(Cust_Tab.this, Cust_add.class));
                break;
        }
        return false;
    }


    class net extends AsyncTask<Void, Void, Void> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(Cust_Tab.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected Void doInBackground(Void... params) {

            DatabaseReference db = FirebaseDatabase.getInstance().getReference().child("MeChat").child("Customer").getRef();

            db.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    cust = dataSnapshot.getValue(Customer.class);
                    cust.setId(dataSnapshot.getKey());
                    list.add(cust);
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
