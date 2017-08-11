package com.example.rajk.leasingmanagers.customer;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.rajk.leasingmanagers.MainViews.TaskDetail;
import com.example.rajk.leasingmanagers.R;
import com.example.rajk.leasingmanagers.employee.Emp_add;
import com.example.rajk.leasingmanagers.tablayout.Tabs;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.apache.commons.lang3.text.WordUtils;

import java.util.Calendar;

import static com.example.rajk.leasingmanagers.LeasingManagers.DBREF;

public class Cust_add extends AppCompatActivity {

    EditText Name, Add, Num,Password;
    String name, add, num,password;
    Button submit;
    DatabaseReference db = DBREF.child("Customer").getRef();
    DatabaseReference database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cust_add);
        getSupportActionBar().setTitle("Add New Customer");
        getSupportActionBar().setIcon(R.mipmap.ic_new_person_white);

        Name = (EditText) findViewById(R.id.name);
        Add = (EditText) findViewById(R.id.add);
        Num = (EditText) findViewById(R.id.num);
        Password =(EditText)findViewById(R.id.password);
        submit = (Button) findViewById(R.id.submit);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                name = Name.getText().toString().trim();
                name = WordUtils.capitalizeFully(name);
                add = Add.getText().toString().trim();
                add = WordUtils.capitalizeFully(add);
                num = Num.getText().toString().trim();
                password = Password.getText().toString().trim();
                if (TextUtils.isEmpty(name) || TextUtils.isEmpty(add) || TextUtils.isEmpty(num))
                    Toast.makeText(Cust_add.this, "Enter Details", Toast.LENGTH_LONG).show();
                else {
                            Customer customer = new Customer(name,num,add,num,getRandomMaterialColor("400"),password);

                            database = db.child(num);
                            database.setValue(customer);
                            database.child(num).child("pendingTask").setValue(1000);
                    DBREF.child("Users").child("Usersessions").child(num).child("name").setValue(name);
                    DBREF.child("Users").child("Usersessions").child(num).child("online").setValue(Boolean.FALSE);
                    DBREF.child("Users").child("Usersessions").child(num).child("num").setValue(num);
                    DBREF.child("Fcmtokens").child(num).child("token").setValue("nil");

                    Intent intent = new Intent(Cust_add.this, Tabs.class);
                    intent.putExtra("page",0);
                    startActivity(intent);
                    finish();

                }
            }
        });

    }
    private int getRandomMaterialColor(String typeColor) {
        int returnColor = Color.GRAY;
        int arrayId = getResources().getIdentifier("mdcolor_" + typeColor, "array", getPackageName());

        if (arrayId != 0) {
            TypedArray colors = getResources().obtainTypedArray(arrayId);
            int index = (int) (Math.random() * colors.length());
            returnColor = colors.getColor(index, Color.GRAY);
            colors.recycle();
        }
        return returnColor;
    }

    @Override
    public void onBackPressed() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to exit?")
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, int id) {
                        Intent intent = new Intent(Cust_add.this,Tabs.class);
                        intent.putExtra("page",0);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
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


    }
}
