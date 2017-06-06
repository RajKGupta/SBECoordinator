package com.example.rajk.leasingmanagers;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.CursorLoader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.VideoView;

import com.example.rajk.leasingmanagers.model.CommentModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import static android.R.attr.path;

public class ImageComment extends AppCompatActivity {

    Uri imageuri,downloadUrl,videouri,thumburl;
    String place_id,topic_id,commmetString,comment_type;
    FloatingActionButton sendImage;
    EditText caption;
    ImageView imagecomment;
    VideoView videocomment;
    SimpleDateFormat formatter;
    DatabaseReference dbTopic;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_comment);

        sendImage = (FloatingActionButton)findViewById(R.id.sendImage);
        imagecomment = (ImageView)findViewById(R.id.imagecomment);
        caption = (EditText)findViewById(R.id.caption);
        videocomment = (VideoView)findViewById(R.id.videocomment);

        topic_id = getIntent().getStringExtra("topic_id");
        place_id = getIntent().getStringExtra("place_id");
        comment_type = getIntent().getStringExtra("comment_type");

        formatter = new SimpleDateFormat("dd-MMM-yyyy hh:mm aa");
        dbTopic = FirebaseDatabase.getInstance().getReference().child("MeChat").child("Topic").child(place_id).child(topic_id).child("Comment");
        final String timestamp = formatter.format(Calendar.getInstance().getTime());

        switch (comment_type)
        {
            case "photo":
                imagecomment.setVisibility(View.VISIBLE);
                videocomment.setVisibility(View.GONE);
                imageuri = Uri.parse(getIntent().getStringExtra("URI"));
                Picasso.with(this).load(imageuri).into(imagecomment);
                break;
            case "video":
                imagecomment.setVisibility(View.GONE);
                videocomment.setVisibility(View.VISIBLE);
                videouri = Uri.parse(getIntent().getStringExtra("URI"));
                videocomment.setVideoURI(videouri);
                videocomment.start();
                videocomment.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        mp.setLooping(true);
                    }
                });
                break;
        }


        sendImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                switch (comment_type)
                {
                    case "photo":
                        commmetString = caption.getText().toString();
                        //Store image
                        StorageReference ref = FirebaseStorage.getInstance().getReference().child("topic/"+place_id+"/"+topic_id+"/"+timestamp);
                        final UploadTask uploadTask = ref.putFile(imageuri);

                        uploadTask.addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                // Handle unsuccessful uploads
                            }
                        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                                downloadUrl = taskSnapshot.getDownloadUrl();
                                long curTime = Calendar.getInstance().getTimeInMillis();
                                long id = curTime;

                                SharedPreferences sharedPreferences = getSharedPreferences("SESSION", MODE_PRIVATE);
                                String sender = sharedPreferences.getString("username","username");

                                CommentModel cm = new CommentModel(commmetString,sender,timestamp, comment_type,String.valueOf(id),"0",downloadUrl.toString());
                                dbTopic.child(String.valueOf(id)).setValue(cm);
                            }
                        });
                        break;
                    case "video":
                        commmetString = caption.getText().toString();

                        //Store video
                        StorageReference ref2 = FirebaseStorage.getInstance().getReference().child("topic/"+place_id+"/"+topic_id+"/"+timestamp);
                        final UploadTask uploadTask2 = ref2.putFile(videouri);

                        uploadTask2.addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                // Handle unsuccessful uploads
                            }
                        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                                downloadUrl = taskSnapshot.getDownloadUrl();
                                long curTime = Calendar.getInstance().getTimeInMillis();
                                long id = curTime;

                                SharedPreferences sharedPreferences = getSharedPreferences("SESSION", MODE_PRIVATE);
                                String sender = sharedPreferences.getString("username","username");

                                // Find thumbnail uri
                                File myFile = new File(videouri.getPath());
                                myFile.getAbsolutePath();
                                try {
                                    Bitmap thumb = ThumbnailUtils.createVideoThumbnail(getPath(videouri), MediaStore.Video.Thumbnails.MINI_KIND);
                                    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                                    thumb.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
                                    String path = MediaStore.Images.Media.insertImage(getApplicationContext().getContentResolver(), thumb, "Title", null);
                                    imageuri = Uri.parse(path);

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                                StorageReference ref = FirebaseStorage.getInstance().getReference().child("topic/"+place_id+"/"+topic_id+"/justvideos/"+timestamp);
                                        final UploadTask uploadTask = ref.putFile(imageuri);

                                        uploadTask.addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception exception) {
                                                // Handle unsuccessful uploads
                                            }
                                        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                            @Override
                                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                                                thumburl = taskSnapshot.getDownloadUrl();
                                            }
                                        });

                                        CommentModel cm2 = new CommentModel(commmetString,sender,timestamp, comment_type,String.valueOf(id),"0",thumburl.toString(),downloadUrl.toString());
                                        dbTopic.child(String.valueOf(id)).setValue(cm2);
                            }
                        });

                        break;
                }

                onBackPressed();
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public String getPath(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        String document_id = cursor.getString(0);
        document_id = document_id.substring(document_id.lastIndexOf(":") + 1);
        cursor = getContentResolver().query(
                android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                null, MediaStore.Images.Media._ID + " = ? ", new String[]{document_id}, null);
        cursor.moveToFirst();
        String path = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA));
        cursor.close();

        return path;
    }
}
