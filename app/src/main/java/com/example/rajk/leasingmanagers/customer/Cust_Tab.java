package com.example.rajk.leasingmanagers.customer;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.DefaultItemAnimator;

import com.example.rajk.leasingmanagers.helper.DividerItemDecoration;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.rajk.leasingmanagers.R;
import com.example.rajk.leasingmanagers.listener.ClickListener;
import com.example.rajk.leasingmanagers.listener.RecyclerTouchListener;
import com.example.rajk.leasingmanagers.notification.NotificationActivity;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import static com.example.rajk.leasingmanagers.LeasingManagers.DBREF;

public class Cust_Tab extends Fragment {

    RecyclerView recview;
    RecAdapter_cust adapter;
    List<Customer> list = new ArrayList<>();
    Customer cust = new Customer();
    ProgressDialog pDialog;
    FloatingActionButton cust_add;
    DatabaseReference db;
    ChildEventListener dbChe;

    public Cust_Tab() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.activity_cust_tab, container, false);
        setHasOptionsMenu(true);
        return view;

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        cust_add = (FloatingActionButton) getView().findViewById(R.id.add_cust);
        recview = (RecyclerView) getView().findViewById(R.id.recview);
        recview.setLayoutManager(new LinearLayoutManager(getContext()));
        recview.setItemAnimator(new DefaultItemAnimator());
        recview.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));

        adapter = new RecAdapter_cust(list, getContext());
        recview.setAdapter(adapter);

        cust_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), Cust_add.class));
            }
        });
        new net().execute();

        recview.addOnItemTouchListener(new RecyclerTouchListener(getActivity(), recview, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Customer item = list.get(position);
                Intent i = new Intent(getContext(), Cust_details.class);
                i.putExtra("id", item.getId());
                startActivity(i);
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu, menu);
        super.onCreateOptionsMenu(menu, inflater);

        final MenuItem item = menu.findItem(R.id.search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText)
            {
                final List<Customer> filteredModelList = filter(list, newText);

                adapter = new RecAdapter_cust(filteredModelList, getContext());
                recview.setAdapter(adapter);

//                adapter.setFilter(filteredModelList);
                return true;
            }
        });

        MenuItemCompat.setOnActionExpandListener(item,
                new MenuItemCompat.OnActionExpandListener() {
                    @Override
                    public boolean onMenuItemActionCollapse(MenuItem item) {
// Do something when collapsed
                        adapter.setFilter(list);
                        return true; // Return true to collapse action view
                    }

                    @Override
                    public boolean onMenuItemActionExpand(MenuItem item) {
// Do something when expanded
                        return true; // Return true to expand action view
                    }
                });
    }

    private List<Customer> filter(List<Customer> models, String query) {
        query = query.toLowerCase();
        final List<Customer> filteredModelList = new ArrayList<>();
        for (Customer model : models) {
            final String text = model.getName().toLowerCase();
            if (text.contains(query)) {
                filteredModelList.add(model);
            }
        }
        return filteredModelList;
    }

    class net extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(getContext());
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {

            final DatabaseReference db = DBREF.child("Customer").getRef();

            final int[] n = {0};
            db.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    n[0] = (int) dataSnapshot.getChildrenCount();

                    if (n[0] > 0) {
                        db.addChildEventListener(new ChildEventListener() {
                            @Override
                            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                                //pDialog.show();
                                if (!dataSnapshot.hasChildren()) {
                                    pDialog.dismiss();
                                }
                                cust = dataSnapshot.getValue(Customer.class);
                                cust.setId(dataSnapshot.getKey());
                                if (!list.contains(cust))
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
    public void onDestroy() {
        super.onDestroy();
        if (dbChe != null)
            db.removeEventListener(dbChe);

    }

}