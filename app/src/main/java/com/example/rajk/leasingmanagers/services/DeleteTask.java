package com.example.rajk.leasingmanagers.services;
import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.NonNull;
import com.example.rajk.leasingmanagers.CoordinatorLogin.CoordinatorSession;
import com.example.rajk.leasingmanagers.model.measurement;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import static com.example.rajk.leasingmanagers.LeasingManagers.DBREF;
import static com.example.rajk.leasingmanagers.LeasingManagers.sendNotif;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions and extra parameters.
 */

public class DeleteTask extends IntentService {
    // TODO: Rename actions, choose action names that describe tasks that this
    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
    String task_id;
    public DeleteTask() {
        super("DeleteTask");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        if (intent != null) {
            task_id =intent.getStringExtra("task_id");
            final CoordinatorSession coordinatorSession = new CoordinatorSession(this);
            final String mykey = coordinatorSession.getUsername();
            final String taskName=intent.getStringExtra("taskName");
            final String custId = intent.getStringExtra("custId");
            DatabaseReference dbAssignedTask = DBREF.child("Task").child(task_id).child("AssignedTo").getRef();
            dbAssignedTask.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {

                        for(DataSnapshot ds:dataSnapshot.getChildren()) {
                            DatabaseReference dbEmployee = DBREF.child("Employee").child(ds.getKey()).child("AssignedTask").child(task_id);
                            dbEmployee.removeValue(); //for employee
                            String contentforother = "Coordinator " + coordinatorSession.getName() + " relieved you of " + taskName;
                            sendNotif(mykey, dataSnapshot.getKey(), "cancelJob", contentforother, task_id);
                        }
                        }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
            DatabaseReference dbCompletedTask = DBREF.child("Task").child(task_id).child("CompletedBy").getRef();
            dbCompletedTask.addListenerForSingleValueEvent(new ValueEventListener() {

                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            DatabaseReference dbEmployee = DBREF.child("Employee").child(ds.getKey()).child("CompletedTask").child(task_id);
                            dbEmployee.removeValue(); //for employee
                        }
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
            DatabaseReference dbDescImages = DBREF.child("Task").child(task_id).child("DescImages").getRef();
            dbDescImages.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        for(DataSnapshot ds:dataSnapshot.getChildren()) {
                            final String item = ds.getValue(String.class);
                            StorageReference storageRef = FirebaseStorage.getInstance().getReferenceFromUrl(item);
                            storageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
          //                          Toast.makeText(DeleteTask.this, item + " deleted successfully", Toast.LENGTH_SHORT).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception) {
          //                          Toast.makeText(DeleteTask.this, item + " does not exist", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
            DatabaseReference dbMeasurement = DBREF.child("Task").child(task_id).child("Measurement").getRef();
            dbMeasurement.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            measurement item = ds.getValue(measurement.class);
                            final String url = item.getFleximage();
                            StorageReference storageRef = FirebaseStorage.getInstance().getReferenceFromUrl(url);
                            StorageReference imageref = storageRef.child(url);
                            imageref.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
            //                        Toast.makeText(DeleteTask.this, url + " deleted successfully", Toast.LENGTH_SHORT).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception) {
            //                        Toast.makeText(DeleteTask.this, url + " does not exist", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
            DBREF.child("Customer").child(custId).child("Task").child(task_id).removeValue();
            sendNotif(mykey,custId,"deleteJob","Your Job "+taskName+ " was deleted",task_id);
        }
            }

    @Override
    public void onDestroy() {
        super.onDestroy();
        DBREF.child("Task").child(task_id).removeValue();
        final DatabaseReference dbDelete = DBREF.child("DeleteTask").child(task_id).getRef();
        dbDelete.setValue(Boolean.TRUE);
    }
}

