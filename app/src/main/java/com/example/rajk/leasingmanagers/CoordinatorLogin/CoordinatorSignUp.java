package com.example.rajk.leasingmanagers.CoordinatorLogin;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.rajk.leasingmanagers.LeasingManagers;
import com.example.rajk.leasingmanagers.R;
import com.example.rajk.leasingmanagers.model.Coordinator;
import com.example.rajk.leasingmanagers.tablayout.Tabs;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import org.apache.commons.lang3.text.WordUtils;

import java.util.jar.Attributes;

import static com.example.rajk.leasingmanagers.LeasingManagers.DBREF;
import static com.example.rajk.leasingmanagers.LeasingManagers.firstLetterCaps;

public class CoordinatorSignUp extends AppCompatActivity {
    EditText username, password, name, contact, address;
    Button signUp;
    String Username, Password, Name, Contact, Address;
    CoordinatorSession session;
    SharedPreferences sharedPreferences;
    TextInputLayout input_email, input_password, input_name, input_address, input_contact;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coordinator_sign_up);

        sharedPreferences = getSharedPreferences("myFCMToken",MODE_PRIVATE);
        name = (EditText)findViewById(R.id.editTextName);
        username = (EditText) findViewById(R.id.editText2);
        password = (EditText) findViewById(R.id.editText3);
        contact = (EditText) findViewById(R.id.editText4);
        address = (EditText) findViewById(R.id.editText5);
        input_email = (TextInputLayout)findViewById(R.id.input_emaillogin);
        input_password = (TextInputLayout)findViewById(R.id.input_passwordlogin);
        input_name = (TextInputLayout)findViewById(R.id.input_name);
        input_contact = (TextInputLayout)findViewById(R.id.input_phonelogin);
        input_address = (TextInputLayout)findViewById(R.id.input_addresslogin);
        signUp = (Button)findViewById(R.id.signUpButton);
        session = new CoordinatorSession(getApplicationContext());

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Name = name.getText().toString().trim();
                Name = WordUtils.capitalizeFully(Name);
                Username = username.getText().toString().trim();
                Password = password.getText().toString().trim();
                Contact = contact.getText().toString().trim();
                Address = address.getText().toString().trim();
                Address = WordUtils.capitalizeFully(Address);

                if (TextUtils.isEmpty(Name)) {
                    input_name.setError("Enter Name");
                    if (input_name.requestFocus()) {
                        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                    }
                }
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

                if (TextUtils.isEmpty(Contact)) {
                    input_contact.setError("Enter Contact");
                    if (input_contact.requestFocus()) {
                        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);

                    }
                }

                if (TextUtils.isEmpty(Address)) {
                    input_address.setError("Enter Address");
                    if (input_address.requestFocus()) {
                        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);

                    }
                }

                if(!TextUtils.isEmpty(Username) && !TextUtils.isEmpty(Password)&& !TextUtils.isEmpty(Name) && !TextUtils.isEmpty(Contact) && !TextUtils.isEmpty(Address)){
                    DBREF.child("Coordinator").child(Username).getRef().addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if(dataSnapshot.exists())
                            {
                                Toast.makeText(getBaseContext(), "Coordinator Already Registered", Toast.LENGTH_SHORT).show();
                            }
                            else
                            {
                                login();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
                else
                    Toast.makeText(getBaseContext(),"Enter Complete Details", Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void login() {
        DBREF.child("Coordinator").child(Username).setValue(new Coordinator(Name,Username,Password,Contact,Address));
        session.create_oldusersession(Username,Name, Contact, Address);
        LeasingManagers.setOnlineStatus(Username);

            DBREF.child("Users").child("Usersessions").child(Username).child("name").setValue(Name);
            DBREF.child("Users").child("Usersessions").child(Username).child("num").setValue(Contact);
        String myFCMToken;
        if (FirebaseInstanceId.getInstance().getToken() == null)
            myFCMToken = sharedPreferences.getString("myFCMToken", "");

        else
            myFCMToken = FirebaseInstanceId.getInstance().getToken();

        if (!myFCMToken.equals("")) {
            DBREF.child("Fcmtokens").child(Username).child("token").setValue(myFCMToken);
            goToTabLayout();
            Toast.makeText(CoordinatorSignUp.this, "Coordinator Registered", Toast.LENGTH_LONG).show();
        } else
            Toast.makeText(CoordinatorSignUp.this, "You will need to clear the app data or reinstall the app to make it work properly", Toast.LENGTH_LONG).show();
    }
    private void goToTabLayout()
    {
        Intent intent = new Intent(this, Tabs.class);
        startActivity(intent);
        finish();

    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, coordinatorLogin.class);
        startActivity(intent);
        finish();
    }
}
