package com.example.rajk.leasingmanagers.services;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.NotificationCompat;
import android.widget.Toast;

import com.example.rajk.leasingmanagers.R;
import com.example.rajk.leasingmanagers.model.ChatMessage;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;

import static com.example.rajk.leasingmanagers.LeasingManagers.DBREF;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class UploadPhotoAndFile extends IntentService {
    StorageReference mStorageRef = FirebaseStorage.getInstance().getReference();

    private NotificationManager notificationManager;
    private NotificationCompat.Builder mBuilder;

    public UploadPhotoAndFile() {
        super("UploadPhotoAndFile");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int icon = R.mipmap.ic_upload;
        mBuilder = new NotificationCompat.Builder(
                getApplicationContext());
        mBuilder.setSmallIcon(icon)
                .setLargeIcon(BitmapFactory.decodeResource(getApplicationContext().getResources(), R.mipmap.ic_upload))
                .setContentTitle(getString(R.string.app_name))
                .setOngoing(true)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setColor(getApplicationContext().getResources().getColor(R.color.white))
                .setContentText("Uploading Quotation...");
        synchronized (this) {
            notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(0, mBuilder.build());
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            String filePath = intent.getStringExtra("filePath");
            String type = intent.getStringExtra("type");
            String mykey = intent.getStringExtra("mykey");
            String otheruserkey = intent.getStringExtra("otheruserkey");
            String receiverToken = intent.getStringExtra("receiverToken");
            String dbTableKey = intent.getStringExtra("dbTableKey");
            String timestamp = intent.getStringExtra("timestamp");
            long id = intent.getLongExtra("id", 0L);
            DatabaseReference dbChat = DBREF.child("Chats").child(dbTableKey).child("ChatMessages");
            uploadFile(filePath, type, mykey, otheruserkey, receiverToken, dbTableKey, dbChat, timestamp, id);
        }
    }

    public void uploadFile(final String path, final String type, final String mykey, final String otheruserkey, final String receiverToken, final String dbTableKey, final DatabaseReference dbChat, final String timestamp, final long id) {
        //if there is a file to upload
        //put case
//        System.out.println("uri found" + Uri.fromFile(new File(path)));
        if (Uri.fromFile(new File(path)) != null) {
            //displaying a progress dialog while upload is going on
            StorageReference riversRef = mStorageRef.child(dbTableKey).child("files");

            switch (type) {
                case "photo":
                    //create msg with 2 extra nodes

                    riversRef.putFile(Uri.fromFile(new File(path)))
                            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    Uri downloadUrl = taskSnapshot.getDownloadUrl();
                                    ChatMessage cm = new ChatMessage(mykey, otheruserkey, timestamp, type, id + "", "0", downloadUrl.toString(), receiverToken, dbTableKey, 100, path, "");
                                    dbChat.child(String.valueOf(id)).setValue(cm);
                                    Toast.makeText(getApplicationContext(), "File Uploaded ", Toast.LENGTH_LONG).show();
                                    DBREF.child("Chats").child(dbTableKey).child("lastMsg").setValue(id);
                                    updateNotification("Upload Successful");

                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception) {
                                    dbChat.child(String.valueOf(id)).removeValue();
                                    Toast.makeText(getApplicationContext(), exception.getMessage(), Toast.LENGTH_LONG).show();
                                    updateNotification("Upload failed");
                                }
                            })
                            .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                                    int progress = (int) ((100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount());
                                    dbChat.child(String.valueOf(id)).child("percentUploaded").setValue(progress);

                                }
                            });
                    break;
                //if there is not any file
                case "doc":
                    //create msg with 2 extra nodes
                    riversRef.putFile(Uri.fromFile(new File(path)))
                            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    Uri downloadUrl = taskSnapshot.getDownloadUrl();
                                    ChatMessage cm = new ChatMessage(mykey, otheruserkey, timestamp, type, id + "", "0", downloadUrl.toString(), receiverToken, dbTableKey, 100, path, "");
                                    dbChat.child(String.valueOf(id)).setValue(cm);
                                    Toast.makeText(getApplicationContext(), "File Uploaded ", Toast.LENGTH_LONG).show();
                                    DBREF.child("Chats").child(dbTableKey).child("lastMsg").setValue(id);
                                    updateNotification("Upload Successful");
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception) {
                                    dbChat.child(String.valueOf(id)).removeValue();
                                    Toast.makeText(getApplicationContext(), exception.getMessage(), Toast.LENGTH_LONG).show();
                                    updateNotification("Upload failed");
                                }
                            })
                            .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                                    int progress = (int) ((100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount());
                                    dbChat.child(String.valueOf(id)).child("percentUploaded").setValue(progress);
                                }
                            });

                    break;
            }

        }
    }

    private void updateNotification(String information) {
        notificationManager.cancel(0);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
                getApplicationContext());
        int icon = R.mipmap.ic_launcher;
        mBuilder.setSmallIcon(icon)
                .setLargeIcon(BitmapFactory.decodeResource(getApplicationContext().getResources(), R.mipmap.ic_launcher))
                .setContentTitle(getString(R.string.app_name))
                .setOngoing(false)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setColor(getApplicationContext().getResources().getColor(R.color.white))
                .setContentText(information)
                .setAutoCancel(true)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(information));

        synchronized (this) {
            notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(0, mBuilder.build());
            stopSelf();
        }
    }
}
