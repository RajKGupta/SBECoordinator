package com.example.rajk.leasingmanagers.customer;

import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.rajk.leasingmanagers.R;
import com.example.rajk.leasingmanagers.employee.Emp_add;
import com.example.rajk.leasingmanagers.tablayout.Tabs;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;

public class Cust_add extends AppCompatActivity {

    EditText Name, Add, Num;
    String name, add, num;
    Button submit;
    DatabaseReference db = FirebaseDatabase.getInstance().getReference().child("MeChat").child("Customer").getRef();
    Long n;
    DatabaseReference database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cust_add);

        Name = (EditText) findViewById(R.id.name);
        Add = (EditText) findViewById(R.id.add);
        Num = (EditText) findViewById(R.id.num);
        submit = (Button) findViewById(R.id.submit);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                name = Name.getText().toString();
                add = Add.getText().toString();
                num = Num.getText().toString();
                if (TextUtils.isEmpty(name) || TextUtils.isEmpty(add) || TextUtils.isEmpty(num))
                    Toast.makeText(Cust_add.this, "Enter Details", Toast.LENGTH_LONG).show();
                else {
                            long id = Calendar.getInstance().getTimeInMillis();
                            Customer customer = new Customer(name,num,add,id+"",getRandomMaterialColor("400"));
                            database = db.child(Calendar.getInstance().getTimeInMillis()+"");
                            database.setValue(customer);


                    Intent intent = new Intent(Cust_add.this, Cust_Tab.class);
                    intent.putExtra("page","0");
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

}
