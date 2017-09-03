package com.example.rajk.leasingmanagers.employee;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;

import com.example.rajk.leasingmanagers.helper.DividerItemDecoration;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.rajk.leasingmanagers.R;
import com.example.rajk.leasingmanagers.listener.ClickListener;
import com.example.rajk.leasingmanagers.listener.RecyclerTouchListener;
import com.example.rajk.leasingmanagers.tablayout.Tabs;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import static com.example.rajk.leasingmanagers.LeasingManagers.DBREF;

public class Emp_Tab extends Fragment {

    RecyclerView recview;
    RecAdapter_emp adapter;
    List<Employee> list = new ArrayList<Employee>();
    Employee emp;
    ProgressDialog pDialog;
    FloatingActionButton emp_add;

    DatabaseReference db;
    ChildEventListener dbChe;

    public Emp_Tab() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.activity_emp_tab, container, false);
        setHasOptionsMenu(true);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        new net().execute();
        emp_add = (FloatingActionButton) getView().findViewById(R.id.add_emp);

        recview = (RecyclerView) getView().findViewById(R.id.recview);
        recview.setLayoutManager(new LinearLayoutManager(getContext()));
        recview.setItemAnimator(new DefaultItemAnimator());
        recview.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));

        adapter = new RecAdapter_emp(list, getContext());
        recview.setAdapter(adapter);

        emp_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), Emp_add.class));
                getActivity().finish();
            }
        });
        recview.addOnItemTouchListener(new RecyclerTouchListener(getActivity(), recview, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Employee item = list.get(position);
                Intent i = new Intent(getContext(), Emp_details.class);
                i.putExtra("id", item.getUsername());
                startActivity(i);
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu1, menu);
        super.onCreateOptionsMenu(menu, inflater);

        final MenuItem item2 = menu.findItem(R.id.refresh);
        item2.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Intent intent = new Intent(getActivity(), Tabs.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("page", 1);
                startActivity(intent);
                getActivity().finish();
                return false;
            }
        });
    }

    class net extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(getContext());
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
                                list.add(emp);
                                adapter.notifyDataSetChanged();

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
