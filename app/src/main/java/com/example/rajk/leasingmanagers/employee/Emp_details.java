package com.example.rajk.leasingmanagers.employee;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.rajk.leasingmanagers.CoordinatorLogin.CoordinatorSession;
import com.example.rajk.leasingmanagers.MainViews.TaskDetail;
import com.example.rajk.leasingmanagers.Quotation.QAdapter;
import com.example.rajk.leasingmanagers.Quotation.QuotaionTasks;
import com.example.rajk.leasingmanagers.R;
import com.example.rajk.leasingmanagers.adapter.EmployeeTask_Adapter;
import com.example.rajk.leasingmanagers.chat.ChatActivity;
import com.example.rajk.leasingmanagers.model.CompletedBy;
import com.example.rajk.leasingmanagers.model.QuotationBatch;
import com.example.rajk.leasingmanagers.model.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.example.rajk.leasingmanagers.LeasingManagers.DBREF;
import static com.example.rajk.leasingmanagers.LeasingManagers.sendNotif;
import static java.security.AccessController.getContext;

public class Emp_details extends AppCompatActivity implements EmployeeTask_Adapter.EmployeeTask_AdapterListener, QAdapter.QAdapterListener {

    Dialog dialog;
    String id, name, num, add, desig, temp_name, temp_add, temp_num, temp_designation;
    EditText Name, Num, Add, Desig;
    DatabaseReference db;
    RecyclerView rec_employeetask;
    LinearLayoutManager linearLayoutManager;
    private RecyclerView.Adapter mAdapter;
    List<String> listoftasks;
    List<QuotationBatch> listofquotations;
    private AlertDialog open_options ;
    CoordinatorSession coordinatorSession;
    String mykey, dbTablekey;
    public static String emp_id;
    ImageButton callme, msgme;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emp_details);

        id = getIntent().getStringExtra("id");
        emp_id = id;

        Name = (EditText) findViewById(R.id.name);
        Num = (EditText) findViewById(R.id.num);
        Add = (EditText) findViewById(R.id.add);
        Desig = (EditText) findViewById(R.id.desig);
        callme = (ImageButton) findViewById(R.id.callme);
        msgme = (ImageButton) findViewById(R.id.msgme);

        rec_employeetask = (RecyclerView) findViewById(R.id.rec_employeetask);
        linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        rec_employeetask.setLayoutManager(linearLayoutManager);
        rec_employeetask.setItemAnimator(new DefaultItemAnimator());
        rec_employeetask.addItemDecoration(new DividerItemDecoration(getApplicationContext(), LinearLayoutManager.VERTICAL));

        coordinatorSession = new CoordinatorSession(this);
        mykey = coordinatorSession.getUsername();

        db = DBREF.child("Employee").child(id);

        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Map<String, String> map_new = (Map<String, String>) dataSnapshot.getValue();

                name = (map_new.get("name"));
                add = (map_new.get("address"));
                num = (map_new.get("phone_num"));
                desig = (map_new.get("designation"));

                Name.setText(name);
                Num.setText(num);
                Add.setText(add);
                Desig.setText(desig);
                getSupportActionBar().setTitle(name);
                getSupportActionBar().setSubtitle(desig);

                setAdapternlist();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        msgme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkChatref(mykey, id);
            }
        });

        callme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO phone call
            }
        });
    }

    private void setAdapternlist() {
        listoftasks = new ArrayList<>();
        listofquotations = new ArrayList<>();

        if (desig.toLowerCase().equals("quotation")) {
            mAdapter = new QAdapter(listofquotations, getApplicationContext(), this);
        } else
            mAdapter = new EmployeeTask_Adapter(listoftasks, getApplicationContext(), id, this);

        rec_employeetask.setAdapter(mAdapter);

        db = db.child("AssignedTask").getRef();

        if (desig.toLowerCase().equals("quotation")) {
            db.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    QuotationBatch m = new QuotationBatch();
                    Map<String, Object> map = (Map) dataSnapshot.getValue();

                    m.setEndDate((String) map.get("endDate"));
                    long c = (long) map.get("color");
                    m.setColor((int) c);
                    m.setStartDate((String) map.get("startDate"));
                    m.setNote((String) map.get("note"));
                    m.setId((String) map.get("id"));

                    listofquotations.add(m);
                    mAdapter.notifyDataSetChanged();
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
            db.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                        listoftasks.add(childSnapshot.getKey());
                        mAdapter.notifyDataSetChanged();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.emp_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item1:

                final EditText name_new, num_new, add_new, desig_new;
                Button sub;

                dialog = new Dialog(Emp_details.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.edit_emp);

                name_new = (EditText) dialog.findViewById(R.id.name);
                num_new = (EditText) dialog.findViewById(R.id.num);
                add_new = (EditText) dialog.findViewById(R.id.add);
                desig_new = (EditText) dialog.findViewById(R.id.desig);
                sub = (Button) dialog.findViewById(R.id.submit);

                name_new.setText(name);
                num_new.setText(num);
                add_new.setText(add);
                desig_new.setText(desig);

                sub.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // update database accordingly

                        temp_add = add_new.getText().toString().trim();
                        temp_name = name_new.getText().toString().trim();
                        temp_num = num_new.getText().toString().trim();
                        temp_designation = desig_new.getText().toString().trim();

                        if (TextUtils.isEmpty(temp_add) || TextUtils.isEmpty(temp_name) || TextUtils.isEmpty(temp_num) || TextUtils.isEmpty(temp_designation))
                            Toast.makeText(Emp_details.this, "Enter details...", Toast.LENGTH_SHORT).show();

                        else {

                            db = DBREF.child("Employee").child(id);
                            db.child("name").setValue(temp_name);
                            db.child("address").setValue(temp_add);
                            db.child("phone_num").setValue(temp_num);
                            db.child("designation").setValue(temp_designation);
                            dialog.dismiss();
                        }

                    }
                });

                dialog.show();
                break;
        }
        return true;
    }

    @Override
    public void onEmployeedotmenuButtonClicked(final int position, final EmployeeTask_Adapter.MyViewHolder holder) {
        open_options = new AlertDialog.Builder(Emp_details.this)
                .setView(R.layout.optionsfor_employeestask).create();
        open_options.show();

        LinearLayout remove = (LinearLayout)open_options.findViewById(R.id.remove);
        LinearLayout remind = (LinearLayout)open_options.findViewById(R.id.remind);
        LinearLayout info = (LinearLayout)open_options.findViewById(R.id.info);

        remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(Emp_details.this);
                builder.setMessage("Are you sure you want to un-assign this task")
                        .setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(final DialogInterface dialog, int i) {
                                DatabaseReference dbCancelJob = DBREF.child("Task").child(listoftasks.get(position)).child("AssignedTo").child(id).getRef();
                                dbCancelJob.removeValue();

                                DatabaseReference dbEmployee = DBREF.child("Employee").child(id).child("AssignedTask").child(listoftasks.get(position));
                                dbEmployee.removeValue(); //for employee
                                final String task_id = listoftasks.get(position);
                                String taskName = holder.employeename.getText().toString().trim();
                                String contentforme = "You relieved "+name+" of "+taskName;
                                sendNotif(mykey,mykey,"cancelJob",contentforme,task_id);
                                String contentforother= "Coordinator "+coordinatorSession.getName()+" relieved you of "+taskName;
                                sendNotif(mykey,id,"cancelJob",contentforother,task_id);
                                listoftasks.remove(position);
                                mAdapter.notifyDataSetChanged();
                                dialog.dismiss();


                            }
                        })
                        .setNegativeButton("Cancel",new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
            }
        });

        remind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String task_id = listoftasks.get(position);
                String taskName = holder.employeename.getText().toString().trim();
                String contentforme = "You reminder "+name+" for "+taskName;
                sendNotif(mykey,mykey,"remindJob",contentforme,task_id);
                String contentforother= "Coordinator "+coordinatorSession.getName()+" reminded you of "+taskName;
                sendNotif(mykey,id,"remindJob",contentforother,task_id);
            }
        });

        info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),TaskDetail.class);
                intent.putExtra("task_id",listoftasks.get(position));
                startActivity(intent);
            }
        });
    }

    private void checkChatref(final String mykey, final String otheruserkey) {
        DatabaseReference dbChat = DBREF.child("Chats").child(mykey + otheruserkey).getRef();
        dbChat.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                System.out.println("query1" + mykey + otheruserkey);
                System.out.println("datasnap 1" + dataSnapshot.toString());
                if (dataSnapshot.exists()) {
                    System.out.println("datasnap exists1" + dataSnapshot.toString());
                    dbTablekey = mykey + otheruserkey;
                    goToChatActivity();
                } else {
                    checkChatref2(mykey, otheruserkey);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private void checkChatref2(final String mykey, final String otheruserkey) {
        final DatabaseReference dbChat = DBREF.child("Chats").child(otheruserkey + mykey).getRef();
        dbTablekey = otheruserkey + mykey;
        dbChat.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    System.out.println("query1" + otheruserkey + mykey);
                    goToChatActivity();
                } else {
                    DBREF.child("Users").child("Userchats").child(mykey).child(otheruserkey).setValue(dbTablekey);
                    DBREF.child("Users").child("Userchats").child(otheruserkey).child(mykey).setValue(dbTablekey);
                    goToChatActivity();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void goToChatActivity() {
        Intent in = new Intent(this, ChatActivity.class);
        in.putExtra("dbTableKey", dbTablekey);
        in.putExtra("otheruserkey", id);
        startActivity(in);
    }

    @Override
    public void onTaskRowClicked(int position) {
        Intent intent = new Intent(Emp_details.this, QuotaionTasks.class);
        QuotationBatch batch = listofquotations.get(position);
        intent.putExtra("id", batch.getId());
        intent.putExtra("note", batch.getNote());
        intent.putExtra("end", batch.getEndDate());
        intent.putExtra("start", batch.getStartDate());
        startActivity(intent);

    }
}