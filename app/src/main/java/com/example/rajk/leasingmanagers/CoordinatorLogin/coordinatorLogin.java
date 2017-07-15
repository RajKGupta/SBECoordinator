package com.example.rajk.leasingmanagers.CoordinatorLogin;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.example.rajk.leasingmanagers.LeasingManagers;
import com.example.rajk.leasingmanagers.R;
import com.example.rajk.leasingmanagers.tablayout.Tabs;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import static com.example.rajk.leasingmanagers.LeasingManagers.DBREF;

public class coordinatorLogin extends AppCompatActivity {

    EditText username, password;
    Button button;
    String Username,Password;
    DatabaseReference database;
    CoordinatorSession session;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    TextInputLayout input_email, input_password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.emp_login);

        sharedPreferences = getSharedPreferences("myFCMToken",MODE_PRIVATE);
        editor = sharedPreferences.edit();
        if(FirebaseInstanceId.getInstance().getToken()!=null)
        {
            editor.putString("myFCMToken",FirebaseInstanceId.getInstance().getToken());
            editor.commit();
        }

        session = new CoordinatorSession(getApplicationContext());
        if(session.isolduser()==true)
        {
            goToTabLayout();
        }
        username = (EditText) findViewById(R.id.editText2);
        password = (EditText) findViewById(R.id.editText3);
        button = (Button) findViewById(R.id.login);
        input_email = (TextInputLayout)findViewById(R.id.input_emaillogin);
        input_password = (TextInputLayout)findViewById(R.id.input_passwordlogin);
        database = DBREF.child("Coordinator").getRef();

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Username = username.getText().toString().trim();
                Password = password.getText().toString().trim();

                if (TextUtils.isEmpty(Username)) {
                    input_email.setError("Enter Email");
                    if (input_email.requestFocus()) {
                        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                    }
                }

                if (TextUtils.isEmpty(Password)) {
                    input_password.setError("Enter Password");
                    if (input_password.requestFocus()) {
                        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);

                    }
                }

                if(!TextUtils.isEmpty(Username) && !TextUtils.isEmpty(Password)){
                    login();
                }
                else
                    Toast.makeText(getBaseContext(),"Enter Complete Details", Toast.LENGTH_SHORT).show();

            }
        });

    }

    private void login() {

            database.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists())
                    {

                        String password = dataSnapshot.child("password").getValue(String.class);
                        if (!password.equals(Password)) {
                            Toast.makeText(getBaseContext(), "Wrong Password", Toast.LENGTH_SHORT).show();
                        } else
                        {
                            session.create_oldusersession(Username);
                            LeasingManagers.setOnlineStatus(Username);
                            String name = dataSnapshot.getValue(String.class);
                            if(name!=null)
                                DBREF.child("Users").child("Usersessions").child(Username).child("name").setValue(name);
                            String myFCMToken;
                            if(FirebaseInstanceId.getInstance().getToken()==null)
                            {
                                myFCMToken =sharedPreferences.getString("myFCMToken","");
                            }
                            else
                                myFCMToken = FirebaseInstanceId.getInstance().getToken();

                            if(!myFCMToken.equals(""))
                            DBREF.child("Fcmtokens").child(Username).child("token").setValue(myFCMToken);
                            else
                            {
                                Toast.makeText(coordinatorLogin.this,"You will need to clear the app data or reinstall the app to make it work properly",Toast.LENGTH_LONG).show();
                                goToTabLayout();
                            }
                        }
                    }
                    else
                    {
                        Toast.makeText(getBaseContext(), "Coordinator Not Registered", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
    }
    private void goToTabLayout()
    {
        Intent intent = new Intent(this, Tabs.class);
        startActivity(intent);
        finish();

    }
}
