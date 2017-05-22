package com.example.rajk.leasingmanagers;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.rajk.leasingmanagers.R;
import com.example.rajk.leasingmanagers.helper.CircleTransform;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class NewUser extends AppCompatActivity {
    private AutoCompleteTextView address;
    private EditText name;
    private TextView email;
    private Button submitButton;
    private ImageButton profPic;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    DatabaseReference database = FirebaseDatabase.getInstance().getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_user);
        sharedPreferences = getSharedPreferences("",MODE_PRIVATE);
        editor =sharedPreferences.edit();
        String signInStatus = sharedPreferences.getString("Status","SIGN_UP");
        if (signInStatus.equals("SIGN_IN"))
        {
            startActivity(new Intent());
        }
        address = (AutoCompleteTextView)findViewById(R.id.address);
        profPic = (ImageButton)findViewById(R.id.prof_pic);
        name = (EditText) findViewById(R.id.name);
        name.setText(MainActivity.currentUser.getDisplayName());

        email = (TextView)findViewById(R.id.email);
        email.setText(MainActivity.currentUser.getEmail());

        Uri profpicUrl =MainActivity.currentUser.getPhotoUrl();
        if (!TextUtils.isEmpty(profpicUrl.toString())) {
            Glide.with(NewUser.this).load(profpicUrl)
                    .thumbnail(0.5f)
                    .crossFade()
                    .transform(new CircleTransform(NewUser.this))
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(profPic);
            } else

            {

            }

        submitButton = (Button) findViewById(R.id.submitButton);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String Name = name.getText().toString().trim();
                final String Email = email.getText().toString().trim();
                final String Address =address.getText().toString().trim();
                if(TextUtils.isEmpty(Name)||TextUtils.isEmpty(Address))
                {
                    Toast.makeText(NewUser.this,"Fields cannot be left empty",Toast.LENGTH_SHORT).show();
                }
                else
                {
                    DatabaseReference dbr = database.child("Users").child(MainActivity.currentUser.getUid());
                    dbr.child("Name").setValue(Name);
                    dbr.child("Email").setValue(Email);
                    dbr.child("Address").setValue(Address);

                }
            }
        });

    }
}
