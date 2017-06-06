package com.example.rajk.leasingmanagers.employee;

import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.rajk.leasingmanagers.R;
import com.example.rajk.leasingmanagers.tablayout.Tabs;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Emp_add extends AppCompatActivity {

    String name, num, add, desig,username,password;
    EditText Name, Num, Add, Desig,Username,Password;
    Button submit;
    DatabaseReference db = FirebaseDatabase.getInstance().getReference().child("MeChat").child("Employee").getRef();
    long n;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emp_add);

        Name = (EditText) findViewById(R.id.name);
        Num = (EditText) findViewById(R.id.num);
        Add = (EditText) findViewById(R.id.add);
        Desig = (EditText) findViewById(R.id.desig);
        Username=(EditText) findViewById(R.id.username);
        Password = (EditText)findViewById(R.id.password);
        submit = (Button) findViewById(R.id.submit);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name = Name.getText().toString().trim();
                num = Num.getText().toString().trim();
                add = Add.getText().toString().trim();
                desig = Desig.getText().toString().trim();
                username = Username.getText().toString().trim();
                password = Password.getText().toString().trim();


                if(!((TextUtils.isEmpty(name) || TextUtils.isEmpty(num) ||TextUtils.isEmpty(add) ||TextUtils.isEmpty(desig)|| (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)))))
                {
                    // upload info to database

                    final DatabaseReference dbnewEmp = db.child(username).getRef();
                    dbnewEmp.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if(dataSnapshot.exists())
                            {
                                Toast.makeText(Emp_add.this,"Username Already Exists!!",Toast.LENGTH_SHORT).show();
                            }
                            else
                            {
                                Employee employee = new Employee(getRandomMaterialColor("400"),name,num,add,desig,username,password);
                                dbnewEmp.setValue(employee);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                    Intent intent = new Intent(Emp_add.this,Tabs.class);
                    intent.putExtra("page",2);
                    startActivity(intent);
                    finish();
                }
                else
                    Toast.makeText(Emp_add.this,"Please enter all details",Toast.LENGTH_SHORT).show();
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

}
