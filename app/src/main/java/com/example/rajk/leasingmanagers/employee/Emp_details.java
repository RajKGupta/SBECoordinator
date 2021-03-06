package com.example.rajk.leasingmanagers.employee;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.example.rajk.leasingmanagers.CoordinatorLogin.CoordinatorSession;
import com.example.rajk.leasingmanagers.ForwardTask.forwardTask;
import com.example.rajk.leasingmanagers.MainViews.TaskDetail;
import com.example.rajk.leasingmanagers.R;
import com.example.rajk.leasingmanagers.adapter.EmployeeTask_Adapter;
import com.example.rajk.leasingmanagers.chat.ChatActivity;
import com.example.rajk.leasingmanagers.customer.Cust_details;
import com.example.rajk.leasingmanagers.helper.DividerItemDecoration;
import com.example.rajk.leasingmanagers.tablayout.Tabs;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import org.apache.commons.lang3.text.WordUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.example.rajk.leasingmanagers.LeasingManagers.CustomerAppLink;
import static com.example.rajk.leasingmanagers.LeasingManagers.DBREF;
import static com.example.rajk.leasingmanagers.LeasingManagers.EmployeeAppLink;
import static com.example.rajk.leasingmanagers.LeasingManagers.sendNotif;

public class Emp_details extends AppCompatActivity implements EmployeeTask_Adapter.EmployeeTask_AdapterListener{//}, QAdapter.QAdapterListener {

    Dialog dialog;
    String id, name, num, add, desig, temp_name, temp_add, temp_num,password;
    EditText Name, Num, Add, Desig;
    DatabaseReference db;
    RecyclerView rec_employeetask;
    LinearLayoutManager linearLayoutManager;
    private RecyclerView.Adapter mAdapter;
    List<String> listoftasks = new ArrayList<>();
    private AlertDialog open_options;
    CoordinatorSession coordinatorSession;
    String mykey, dbTablekey;
    public static String emp_id;
    ImageButton callme, msgme;
    TextView jobs_and_hideme;
    EmployeeTask_Adapter.EmployeeTask_AdapterListener listener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emp_details);
        listener =this;
        DBREF.child("eal").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                EmployeeAppLink = dataSnapshot.getValue(String.class);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        id = getIntent().getStringExtra("id");
        emp_id = id;
        Name = (EditText) findViewById(R.id.name);
        Num = (EditText) findViewById(R.id.num);
        Add = (EditText) findViewById(R.id.add);
        Desig = (EditText) findViewById(R.id.desig);
        callme = (ImageButton) findViewById(R.id.callme);
        msgme = (ImageButton) findViewById(R.id.msgme);
        jobs_and_hideme = (TextView) findViewById(R.id.jobs_and_hideme);

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
                if (dataSnapshot.exists()) {
                    Map<String, String> map_new = (Map<String, String>) dataSnapshot.getValue();

                    name = (map_new.get("name"));
                    add = (map_new.get("address"));
                    num = (map_new.get("phone_num"));
                    desig = (map_new.get("designation"));
                    password= (map_new.get("password"));

                    Name.setText(name);
                    Num.setText(num);
                    Add.setText(add);
                    Desig.setText(desig);
                    getSupportActionBar().setTitle(name);
                    getSupportActionBar().setSubtitle(desig);

                    setAdapternlist();
                }
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
                Intent callIntent = new Intent(Intent.ACTION_DIAL);
                callIntent.setData(Uri.parse("tel:" + num));
                startActivity(callIntent);
            }
        });
    }

    private void setAdapternlist() {

            db.child("AssignedTask").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                listoftasks.clear();
                mAdapter = new EmployeeTask_Adapter(listoftasks, getApplicationContext(), id, listener);
                if (dataSnapshot.exists()) {
                    jobs_and_hideme.setVisibility(View.GONE);
                    for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                        listoftasks.add(childSnapshot.getKey());
                    }
                    rec_employeetask.setAdapter(mAdapter);
                    mAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
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

                final EditText name_new, num_new, add_new;
                Button sub;

                dialog = new AlertDialog.Builder(this)
                    .setView(R.layout.edit_emp)
                    .create();

                dialog.show();
                name_new = (EditText) dialog.findViewById(R.id.name);
                num_new = (EditText) dialog.findViewById(R.id.num);
                add_new = (EditText) dialog.findViewById(R.id.add);
                sub = (Button) dialog.findViewById(R.id.submit);

                name_new.setText(name);
                num_new.setText(num);
                add_new.setText(add);

                sub.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        temp_add = add_new.getText().toString().trim();
                        temp_add = WordUtils.capitalizeFully(temp_add);
                        temp_name = name_new.getText().toString().trim();
                        temp_name = WordUtils.capitalizeFully(temp_name);
                        temp_num = num_new.getText().toString().trim();

                        if (TextUtils.isEmpty(temp_add) || TextUtils.isEmpty(temp_name) || TextUtils.isEmpty(temp_num))
                            Toast.makeText(Emp_details.this, "Enter details...", Toast.LENGTH_SHORT).show();

                        else {

                            db = DBREF.child("Employee").child(id);
                            db.child("name").setValue(temp_name);
                            db.child("address").setValue(temp_add);
                            db.child("phone_num").setValue(temp_num);
                            DBREF.child("Users").child("Usersessions").child(emp_id).child("num").setValue(num);

                            dialog.dismiss();
                        }
                    }
                });

                dialog.show();
                break;

            case R.id.share:
                Intent smsIntent = new Intent(Intent.ACTION_SEND);
                String content = "Hi "+name+". Download the Tanu Enterprises App from "+EmployeeAppLink+".\nUsername: " +id+"\nPassword: "+password;
                smsIntent.setData(Uri.parse("smsto:"));
                smsIntent.setType("text/plain");
                smsIntent.putExtra("address"  ,num);
                smsIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Download the Tanu Enterprises App");
                smsIntent.putExtra(android.content.Intent.EXTRA_TEXT,content );
                smsIntent.putExtra("sms_body"  , content );

                try {
                    startActivity(Intent.createChooser(smsIntent,"Share Employee App Link"));
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(Emp_details.this,
                            "Your phone does not support this option. Contact manufacturer for details.", Toast.LENGTH_SHORT).show();
                }

                break;
        }
        return true;
    }

    @Override
    public void onEmployeedotmenuButtonClicked(final int position, final EmployeeTask_Adapter.MyViewHolder holder) {
        open_options = new AlertDialog.Builder(Emp_details.this)
                .setView(R.layout.optionsfor_employeestask).create();
        open_options.show();

        LinearLayout remove = (LinearLayout) open_options.findViewById(R.id.remove);
        LinearLayout remind = (LinearLayout) open_options.findViewById(R.id.remind);
        LinearLayout info = (LinearLayout) open_options.findViewById(R.id.info);
        LinearLayout swap = (LinearLayout) open_options.findViewById(R.id.swap);
        LinearLayout editNote = (LinearLayout) open_options.findViewById(R.id.editNote);
        LinearLayout repeatedreminder = (LinearLayout) open_options.findViewById(R.id.repeatedreminder);

        repeatedreminder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater layoutInflaterAndroid = LayoutInflater.from(Emp_details.this);
                View mView = layoutInflaterAndroid.inflate(R.layout.repeatedreminderlayout, null);
                AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(Emp_details.this);
                alertDialogBuilderUserInput.setView(mView);
                final String empId = id;
                final EditText userInputDialogEditText = (EditText) mView.findViewById(R.id.userInputDialog);
                alertDialogBuilderUserInput
                        .setCancelable(false)
                        .setPositiveButton("SET", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogBox, int id) {
                                final String task_id = listoftasks.get(position);
                                final String taskName = holder.employeename.getText().toString().trim();

                                String minutes = userInputDialogEditText.getText().toString().trim();
                                if (minutes != null && !minutes.equals("")) {
                                    Integer Minutes = Integer.parseInt(minutes);
                                    if (Minutes > 0) {
                                        String contentforme = "You reminded " + name + " for " + taskName;
                                        sendNotif(mykey, mykey, "repeatedReminder", contentforme, task_id);
                                        String contentforother = "Coordinator " + coordinatorSession.getName() + " reminded you of " + taskName;
                                        sendNotif(mykey, empId, "repeatedReminder" + " " + minutes, contentforother, task_id);
                                        Toast.makeText(Emp_details.this, contentforme, Toast.LENGTH_SHORT).show();
                                        dialogBox.dismiss();
                                    }
                                }
                            }
                        })

                        .setNegativeButton("CANCEL",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialogBox, int id) {
                                        dialogBox.cancel();
                                    }
                                });

                AlertDialog alertDialogAndroid = alertDialogBuilderUserInput.create();
                alertDialogAndroid.show();
                open_options.dismiss();

            }
        });

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
                                String contentforme = "You relieved " + name + " of " + taskName;
                                sendNotif(mykey, mykey, "cancelJob", contentforme, task_id);
                                String contentforother = "Coordinator " + coordinatorSession.getName() + " relieved you of " + taskName;
                                sendNotif(mykey, id, "cancelJob", contentforother, task_id);
                                listoftasks.remove(position);
                                mAdapter.notifyDataSetChanged();
                                dialog.dismiss();


                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
                open_options.dismiss();
            }
        });

        remind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String task_id = listoftasks.get(position);
                String taskName = holder.employeename.getText().toString().trim();
                String contentforme = "You reminded " + name + " for " + taskName;
                sendNotif(mykey, mykey, "remindJob", contentforme, task_id);
                String contentforother = "Coordinator " + coordinatorSession.getName() + " reminded you of " + taskName;
                sendNotif(mykey, id, "remindJob", contentforother, task_id);
                open_options.dismiss();
            }
        });

        info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                open_options.dismiss();
                Intent intent = new Intent(getApplicationContext(), TaskDetail.class);
                intent.putExtra("task_id", listoftasks.get(position));
                startActivity(intent);
            }
        });

        swap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(Emp_details.this);
                builder.setMessage("Are you sure you want to swap this task")
                        .setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(final DialogInterface dialog, int id) {
                                Intent intent1 = new Intent(Emp_details.this, forwardTask.class);
                                intent1.putExtra("task_id", listoftasks.get(position));
                                intent1.putExtra("swaping_id", emp_id);
                                startActivity(intent1);
                                finish();
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
                open_options.dismiss();
            }
        });

        editNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater layoutInflaterAndroid = LayoutInflater.from(Emp_details.this);
                View mView = layoutInflaterAndroid.inflate(R.layout.editcoordinatornote, null);
                AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(Emp_details.this);
                alertDialogBuilderUserInput.setView(mView);
                final String empId = id;
                final EditText userInputDialogEditText = (EditText) mView.findViewById(R.id.userInputDialog);
                userInputDialogEditText.setText(holder.noteString.getText().toString().trim());
                alertDialogBuilderUserInput
                        .setCancelable(false)
                        .setPositiveButton("SET", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogBox, int id) {
                                final String task_id = listoftasks.get(position);
                                final String taskName = holder.employeename.getText().toString().trim();

                                String note = userInputDialogEditText.getText().toString().trim();
                                if (note != null && !note.equals("")) {
                                    DBREF.child("Task").child(task_id).child("AssignedTo").child(empId).child("note").setValue(note);
                                    holder.noteString.setText(note);
                                    Toast.makeText(Emp_details.this, "Coordinator note changed successfully", Toast.LENGTH_SHORT).show();
                                    String contentforme = "You changed the coordinator note for " + taskName;
                                    sendNotif(mykey, mykey, "changedNote", contentforme, task_id);
                                    String contentforother = "Coordinator " + coordinatorSession.getName() + " changed the note of " + taskName;
                                    sendNotif(mykey, empId, "changedNote", contentforother, task_id);
                                    dialogBox.dismiss();
                                }
                                else
                                    dialogBox.dismiss();
                            }
                        })

                        .setNegativeButton("CANCEL",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialogBox, int id) {
                                        dialogBox.cancel();
                                    }
                                });

                AlertDialog alertDialogAndroid = alertDialogBuilderUserInput.create();
                alertDialogAndroid.show();
                open_options.dismiss();

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
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(Emp_details.this, Tabs.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("page",1);
        finish();

    }
}
