package com.example.rajk.leasingmanagers.MainViews;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.example.rajk.leasingmanagers.CoordinatorLogin.CoordinatorSession;
import com.example.rajk.leasingmanagers.ForwardTask.forwardTask;
import com.example.rajk.leasingmanagers.R;
import com.example.rajk.leasingmanagers.adapter.assignedto_adapter;
import com.example.rajk.leasingmanagers.adapter.bigimage_adapter;
import com.example.rajk.leasingmanagers.adapter.measurement_adapter;
import com.example.rajk.leasingmanagers.adapter.taskdetailDescImageAdapter;
import com.example.rajk.leasingmanagers.customer.Cust_details;
import com.example.rajk.leasingmanagers.helper.MarshmallowPermissions;
import com.example.rajk.leasingmanagers.model.CompletedBy;
import com.example.rajk.leasingmanagers.model.Quotation;
import com.example.rajk.leasingmanagers.model.Task;
import com.example.rajk.leasingmanagers.model.measurement;
import com.example.rajk.leasingmanagers.services.DeleteTask;
import com.example.rajk.leasingmanagers.services.DownloadFileService;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;
import static com.example.rajk.leasingmanagers.LeasingManagers.DBREF;
import static com.example.rajk.leasingmanagers.LeasingManagers.sendNotif;

public class TaskDetail extends AppCompatActivity implements taskdetailDescImageAdapter.ImageAdapterListener, assignedto_adapter.assignedto_adapterListener, bigimage_adapter.bigimage_adapterListener{
    
    private DatabaseReference dbRef, dbTask,dbCompleted,dbAssigned,dbMeasurement,dbDescImages;
    ValueEventListener dbTaskVle;
    ImageButton download;
    ProgressBar progressBar;
    private String task_id,mykey;
    private Task task;
    private String customername;
    EditText startDate,endDate,quantity,description;
    RecyclerView rec_assignedto,rec_completedby,rec_measurement, rec_DescImages ;
    assignedto_adapter adapter_assignedto,adapter_completedby;
    taskdetailDescImageAdapter adapter_taskimages;
    ArrayList<String> DescImages = new ArrayList<>();
    List<CompletedBy> assignedtoList = new ArrayList<>();
    FloatingActionButton forward;
    List<CompletedBy> completedbyList = new ArrayList<>();
    ArrayList<measurement> measurementList = new ArrayList<>();
    measurement_adapter adapter_measurement;
    TextView measure_and_hideme, assign_and_hideme, complete_and_hideme, appByCustomer,uploadStatus;
    DatabaseReference dbQuotation;
    ProgressDialog progressDialog ;
    private MarshmallowPermissions marshmallowPermissions;
    private AlertDialog viewSelectedImages, open_options ;
    LinearLayoutManager linearLayoutManager;
    bigimage_adapter adapter;
    private CoordinatorSession coordinatorSession;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_detail);

        coordinatorSession = new CoordinatorSession(this);
        mykey = coordinatorSession.getUsername();
        marshmallowPermissions =new MarshmallowPermissions(this);
        dbRef = DBREF;
        progressDialog = new ProgressDialog(this);
        download = (ImageButton)findViewById(R.id.download);
        progressBar = (ProgressBar)findViewById(R.id.progress);
        uploadStatus = (TextView)findViewById(R.id.uploadStatus);
        appByCustomer = (TextView)findViewById(R.id.appByCustomer);
        measure_and_hideme = (TextView)findViewById(R.id.measure_and_hideme);
        assign_and_hideme = (TextView)findViewById(R.id.assign_and_hideme);
        complete_and_hideme = (TextView)findViewById(R.id.complete_and_hideme);

        forward = (FloatingActionButton)findViewById(R.id.forward);
        startDate = (EditText) findViewById(R.id.startDate);
        endDate = (EditText) findViewById(R.id.endDate);
        quantity=(EditText) findViewById(R.id.quantity);
        description = (EditText) findViewById(R.id.description);
        rec_assignedto = (RecyclerView)findViewById(R.id.rec_assignedto);
        rec_completedby = (RecyclerView)findViewById(R.id.rec_completedby);
        rec_measurement = (RecyclerView)findViewById(R.id.rec_measurement);
        rec_DescImages = (RecyclerView)findViewById(R.id.rec_DescImages);

        Intent intent = getIntent();
        task_id = intent.getStringExtra("task_id");

        rec_assignedto.setLayoutManager(new LinearLayoutManager(this));
        rec_assignedto.setItemAnimator(new DefaultItemAnimator());
        rec_assignedto.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        adapter_assignedto = new assignedto_adapter(assignedtoList, getApplicationContext(),"AssignedTo",task_id,this);
        rec_assignedto.setAdapter(adapter_assignedto);

        rec_completedby.setLayoutManager(new LinearLayoutManager(this));
        rec_completedby.setItemAnimator(new DefaultItemAnimator());
        rec_completedby.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        adapter_completedby = new assignedto_adapter(completedbyList, getApplicationContext(),"CompletedBy",task_id,this);
        rec_completedby.setAdapter(adapter_completedby);

        rec_measurement.setLayoutManager(new LinearLayoutManager(this));
        rec_measurement.setItemAnimator(new DefaultItemAnimator());
        rec_measurement.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        adapter_measurement = new measurement_adapter(measurementList, this);
        rec_measurement.setAdapter(adapter_measurement);

        rec_DescImages.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false));
        rec_DescImages.setItemAnimator(new DefaultItemAnimator());
        rec_DescImages.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.HORIZONTAL));
        adapter_taskimages = new taskdetailDescImageAdapter(DescImages, getApplicationContext(),this);
        rec_DescImages.setAdapter(adapter_taskimages);

        dbTask = dbRef.child("Task").child(task_id);
        dbQuotation = dbTask.child("Quotation").getRef();
        dbCompleted = dbTask.child("CompletedBy").getRef();
        dbAssigned = dbTask.child("AssignedTo").getRef();
        dbMeasurement = dbTask.child("Measurement").getRef();
        dbDescImages = dbTask.child("DescImages").getRef();

        prepareListData();

        dbTaskVle = dbTask.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                task = dataSnapshot.getValue(Task.class);
                setValue(task);
                getSupportActionBar().setTitle(task.getName());
                DatabaseReference dbCustomerName =DBREF.child("Customer").child(task.getCustomerId()).getRef();
                dbCustomerName.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        customername = dataSnapshot.child("name").getValue(String.class);
                        getSupportActionBar().setSubtitle(customername);
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
        forward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1  = new Intent(TaskDetail.this,forwardTask.class);
                intent1.putExtra("task_id",task_id);
                startActivity(intent1);
                finish();
            }
        });

        download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            if(!marshmallowPermissions.checkPermissionForCamera())
            {
                marshmallowPermissions.requestPermissionForExternalStorage();
                if(!marshmallowPermissions.checkPermissionForExternalStorage())
                showToast("Cannot Download because external storage permission not granted");
                else
                launchLibrary();
            } else {

                    launchLibrary();
                    }
            }
        });

    }

    private void showToast(String message) {
        Toast.makeText(this,message,Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    private void launchLibrary()
    {
        //TODO : loader aur completion of download show karna hai
        //download.setVisibility(View.GONE);
        //progressBar.setVisibility(View.VISIBLE);
        final String[] url = new String[1];
        dbQuotation.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Quotation quotation = dataSnapshot.getValue(Quotation.class);
                    url[0] = quotation.getUrl();
                    Intent serviceIntent = new Intent(getApplicationContext(), DownloadFileService.class);
                    serviceIntent.putExtra("TaskId", task_id);
                    serviceIntent.putExtra("url", url[0]);
                    startService(serviceIntent);
                }
                else
                {
                    Toast.makeText(TaskDetail.this, "No Quotation Uploaded Yet", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void prepareListData()
    {
        dbCompleted.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot.exists()) {
                    complete_and_hideme.setVisibility(View.GONE);
                    CompletedBy item = dataSnapshot.getValue(CompletedBy.class);
                    completedbyList.add(item);
                    adapter_completedby.notifyDataSetChanged();
                }
                else
                {
                    complete_and_hideme.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                CompletedBy item = dataSnapshot.getValue(CompletedBy.class);
                completedbyList.remove(item);
                adapter_completedby.notifyDataSetChanged();
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        dbAssigned.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot.exists()) {
                    assign_and_hideme.setVisibility(View.GONE);
                    CompletedBy item = dataSnapshot.getValue(CompletedBy.class);
                    assignedtoList.add(item);
                    adapter_assignedto.notifyDataSetChanged();
                }
                else
                {
                    assign_and_hideme.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                CompletedBy item = dataSnapshot.getValue(CompletedBy.class);
                assignedtoList.remove(item);
                adapter_assignedto.notifyDataSetChanged();
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        dbMeasurement.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s)
            {
                if (dataSnapshot.exists()) {
                    measure_and_hideme.setVisibility(View.GONE);
                    measurement item = dataSnapshot.getValue(measurement.class);
                    measurementList.add(item);
                    adapter_measurement.notifyDataSetChanged();
                }
                else
                {
                    measure_and_hideme.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                measurement item = dataSnapshot.getValue(measurement.class);
                measurementList.remove(item);
                adapter_measurement.notifyDataSetChanged();
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //TODO : image ko download karwana hai kya ?
        dbDescImages.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s)
            {
                if (dataSnapshot.exists()) {
                    rec_DescImages.setVisibility(View.VISIBLE);
                    String item = dataSnapshot.getValue(String.class);
                    DescImages.add(item);
                    adapter_taskimages.notifyDataSetChanged();
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                String item = dataSnapshot.getKey();
                DescImages.remove(item);
                adapter_taskimages.notifyDataSetChanged();
            }
            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    void setValue(Task task)
    {
        if(task.getStartDate()!=null)
        startDate.setText(task.getStartDate());

        if(task.getExpEndDate()!=null)
        endDate.setText(task.getExpEndDate());

        if(task.getQty()!=null)
        quantity.setText(task.getQty());
        if (!task.getDesc().equals("")&&task.getDesc()!=null) {
            description.setVisibility(View.VISIBLE);
            description.setText(task.getDesc());
        }
        dbQuotation.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                {
                    appByCustomer.setVisibility(View.VISIBLE);
                    Quotation quotation = dataSnapshot.getValue(Quotation.class);
                    if(quotation.getApprovedByCust()!=null)
                    appByCustomer.setText(" "+quotation.getApprovedByCust());
                    uploadStatus.setText(" Yes");
                }
                else
                {
                    appByCustomer.setVisibility(View.GONE);
                    uploadStatus.setText(" No");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onImageClicked(int position) {

        viewSelectedImages = new AlertDialog.Builder(TaskDetail.this)
                .setView(R.layout.view_image_on_click).create();
        viewSelectedImages.show();

        RecyclerView bigimage = (RecyclerView)viewSelectedImages.findViewById(R.id.bigimage);

        linearLayoutManager = new LinearLayoutManager(getApplicationContext(),LinearLayoutManager.HORIZONTAL,false);
        bigimage.setLayoutManager(linearLayoutManager);
        bigimage.setItemAnimator(new DefaultItemAnimator());
        bigimage.addItemDecoration(new DividerItemDecoration(getApplicationContext(), LinearLayoutManager.HORIZONTAL));

        adapter = new bigimage_adapter(DescImages, this,this);
        bigimage.setAdapter(adapter);

        bigimage.scrollToPosition(position);
    }


    @Override
    public void ondownloadButtonClicked(int position) {
        // TODO :download task image code here
    }

    @Override
    public void onOptionsButtonClicked(final int position, final assignedto_adapter.MyViewHolder holder)
    {
        open_options = new AlertDialog.Builder(TaskDetail.this)
                .setView(R.layout.options_forassignedtask).create();
        open_options.show();

        LinearLayout remove = (LinearLayout)open_options.findViewById(R.id.remove);
        LinearLayout remind = (LinearLayout)open_options.findViewById(R.id.remind);
        LinearLayout repeatedreminder =(LinearLayout)open_options.findViewById(R.id.repeatedreminder);
        LinearLayout swap = (LinearLayout)open_options.findViewById(R.id.swap);

        remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                final AlertDialog.Builder builder = new AlertDialog.Builder(TaskDetail.this);
                builder.setMessage("Are you sure you want to un-assign this task")
                        .setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(final DialogInterface dialog, int id) {
                                DatabaseReference dbCancelJob = DBREF.child("Task").child(task_id).child("AssignedTo").child(adapter_assignedto.emp.getEmpId()).getRef();
                                dbCancelJob.removeValue();

                                DatabaseReference dbEmployee = DBREF.child("Employee").child(adapter_assignedto.emp.getEmpId()).child("AssignedTask").child(task_id);
                                dbEmployee.removeValue(); //for employee
                                String contentforme = "You relieved "+holder.employeename.getText().toString().trim()+" of "+task.getName();
                                sendNotif(mykey,mykey,"cancelJob",contentforme,task_id);
                                String contentforother= "Coordinator "+coordinatorSession.getName()+" relieved you of "+task.getName();
                                sendNotif(mykey,adapter_assignedto.emp.getEmpId(),"cancelJob",contentforother,task_id);
                                Toast.makeText(TaskDetail.this, contentforme, Toast.LENGTH_SHORT).show();
                                assignedtoList.remove(position);
                                adapter_assignedto.notifyDataSetChanged();
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
                open_options.dismiss();
            }
        });

        remind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String contentforme = "You reminded "+holder.employeename.getText().toString().trim() +" for "+task.getName();
                sendNotif(mykey,mykey,"remindJob",contentforme,task_id);
                String contentforother= "Coordinator "+coordinatorSession.getName()+" reminded you of "+task.getName();
                sendNotif(mykey,adapter_assignedto.emp.getEmpId(),"remindJob",contentforother,task_id);
                Toast.makeText(TaskDetail.this, contentforme, Toast.LENGTH_SHORT).show();
                open_options.dismiss();
            }
        });

        swap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(TaskDetail.this);
                builder.setMessage("Are you sure you want to swap this task")
                        .setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(final DialogInterface dialog, int id) {
                                Intent intent1  = new Intent(TaskDetail.this,forwardTask.class);
                                intent1.putExtra("task_id",task_id);
                                intent1.putExtra("swaping_id",adapter_assignedto.emp.getEmpId());
                                startActivity(intent1);
                                finish();
                            }
                        })
                        .setNegativeButton("Cancel",new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
                open_options.dismiss();
            }
        });

        repeatedreminder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater layoutInflaterAndroid = LayoutInflater.from(TaskDetail.this);
                View mView = layoutInflaterAndroid.inflate(R.layout.repeatedreminderlayout, null);
                AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(TaskDetail.this);
                alertDialogBuilderUserInput.setView(mView);

                final EditText userInputDialogEditText = (EditText) mView.findViewById(R.id.userInputDialog);
                alertDialogBuilderUserInput
                        .setCancelable(false)
                        .setPositiveButton("SET", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogBox, int id) {
                                String minutes = userInputDialogEditText.getText().toString().trim();
                                if(minutes!=null){
                                Integer Minutes = Integer.parseInt(minutes);
                                if(Minutes>0)
                                {
                                    String contentforme = "You reminded "+holder.employeename.getText().toString().trim() +" for "+task.getName();
                                    sendNotif(mykey,mykey,"repeatedReminder",contentforme,task_id);
                                    String contentforother= "Coordinator "+coordinatorSession.getName()+" reminded you of "+task.getName();
                                    sendNotif(mykey,adapter_assignedto.emp.getEmpId(),"repeatedReminder"+" "+minutes,contentforother,task_id);
                                    Toast.makeText(TaskDetail.this, contentforme, Toast.LENGTH_SHORT).show();
                                }
                            }}
                        })

                        .setNegativeButton("CANCEL",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialogBox, int id) {
                                        dialogBox.cancel();
                                    }
                                });

                AlertDialog alertDialogAndroid = alertDialogBuilderUserInput.create();
                alertDialogAndroid.show();
            }
        });

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.task_details_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item1:

                break;

            case R.id.item2:
                final AlertDialog.Builder builder = new AlertDialog.Builder(TaskDetail.this);
                builder.setMessage("Are you sure you want to delete this task??")
                        .setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(final DialogInterface dialog, final int id) {
                                if(dbTaskVle!=null)
                                    dbTask.removeEventListener(dbTaskVle);
                                final ProgressDialog progressDialog = new ProgressDialog(TaskDetail.this);
                                progressDialog.setMessage("Deleting Task");
                                progressDialog.setCancelable(false);
                                progressDialog.show();
                                Intent serviceIntent = new Intent(TaskDetail.this, DeleteTask.class);
                                serviceIntent.putExtra("task_id",task_id);
                                serviceIntent.putExtra("taskName",task.getName());
                                serviceIntent.putExtra("custId",task.getCustomerId());

                                startService(serviceIntent);
                                final DatabaseReference dbDelete = DBREF.child("DeleteTask").child(task_id).getRef();
                                dbDelete.setValue(Boolean.FALSE);
                                dbDelete.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        if(dataSnapshot.exists())
                                        {
                                            Boolean status = dataSnapshot.getValue(Boolean.class);
                                            if(status==true)
                                            {
                                                dbDelete.removeEventListener(this);
                                                dbDelete.removeValue();
                                                progressDialog.dismiss();
                                                sendNotif(mykey,mykey,"deleteTask","You deleted the "+task.getName()+" task of "+customername,task_id);
                                                Intent intent = new Intent(TaskDetail.this,Cust_details.class);
                                                intent.putExtra("id",task.getCustomerId());
                                                startActivity(intent);
                                                finish();
                                            }
                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                            }
                        })
                        .setNegativeButton("Cancel",new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
                break;

        }
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(dbTaskVle!=null)
            dbTask.removeEventListener(dbTaskVle);
    }
}
