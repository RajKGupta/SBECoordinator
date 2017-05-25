package com.example.rajk.leasingmanagers;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.rajk.leasingmanagers.DiscussionActivity.Home;
import com.example.rajk.leasingmanagers.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class NewUser extends AppCompatActivity {

    CircleImageView prof_pic;
    TextView name,email;
    EditText username;
    AutoCompleteTextView address;
    Button submit;
    FirebaseAuth auth;
    FirebaseUser currentuser;
    public static HashMap<String,String> hashMap = new HashMap<>();
    DatabaseReference mDatabase, adduser, Usernames_list, addusername, user_exists;
    session s;
    String place,user_name;
    String [] list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();

        setContentView(R.layout.activity_new_user);
        hashMap.put("449 Palo Verde Road, Gainesville, FL","449");
        hashMap.put("6731 Thompson Street, Gainesville, FL","6731");
        hashMap.put("8771 Thomas Boulevard, Orlando, FL","8771");
        hashMap.put("1234 Verano Place, Orlando, FL","1234");
        auth = FirebaseAuth.getInstance();
        currentuser = auth.getCurrentUser();

        mDatabase = FirebaseDatabase.getInstance().getReference().child("MeChat");

        s= new session(NewUser.this);

        prof_pic = (CircleImageView)findViewById(R.id.prof_pic);
        name = (TextView)findViewById(R.id.name);
        email = (TextView)findViewById(R.id.email);
        username = (EditText)findViewById(R.id.username);
        address= (AutoCompleteTextView)findViewById(R.id.address);
        submit = (Button) findViewById(R.id.submit_button);

        list = getResources().getStringArray(R.array.list);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>
                (this, android.R.layout.select_dialog_item, list);
        //Getting the instance of AutoCompleteTextView
        address.setThreshold(1);//will start working from first character
        address.setAdapter(adapter);//setting the adapter data into the AutoCompleteTextView

        setcredentials(currentuser);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {

                if (!Arrays.asList(list).contains(address.getText().toString().trim()))
                {
                    address.setText("");
                }

                if ((username.getText().toString().equals(""))&&(address.getText().toString().equals("")))
                {
                    Toast.makeText(NewUser.this, "Fill in all the credentials", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    user_name = username.getText().toString();

                    Usernames_list = mDatabase.child("Usernames").child(user_name).getRef();

                    Usernames_list.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot)
                        {
                                if (!dataSnapshot.exists())
                                {
                                    // TODO: 5/21/2017 Testing of App
                                    place = address.getText().toString();
                                    addusername = mDatabase.child("Usernames");

                                    addusername.child(user_name).setValue(user_name);

                                    adduser = mDatabase.child("User").child(currentuser.getUid());

                                    adduser.child("name").setValue(name.getText().toString().trim());
                                    adduser.child("username").setValue(username.getText().toString().trim());
                                    adduser.child("place_id").setValue(hashMap.get(place));

                                    s.create_oldusersession(place,hashMap.get(place),username.getText().toString().trim());

                                    Intent intent = new Intent(NewUser.this, Home.class);
                                    intent.putExtra("place_id",hashMap.get(place));
                                    startActivity(intent);
                                    finish();
                                }
                                else
                                {
                                    username.setText("");
                                    Toast.makeText(NewUser.this, "Not Available", Toast.LENGTH_SHORT).show();
                                }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            }
        });
    }

    private void setcredentials(FirebaseUser currentuser)
    {
        if (currentuser.getDisplayName() != null) {
            name.setText(currentuser.getDisplayName());
        }

        if (currentuser.getPhotoUrl() != null)
        {
            Picasso.with(this).load(currentuser.getPhotoUrl().toString()).into(prof_pic);
        }

        if (currentuser.getEmail() != null)
        {
            email.setText(currentuser.getEmail());
        }

    }

}
