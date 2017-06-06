package com.example.rajk.leasingmanagers.customer;

import android.app.Dialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.rajk.leasingmanagers.MainViews.CreateTask;
import com.example.rajk.leasingmanagers.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;

public class Cust_details extends AppCompatActivity {

    Dialog dialog;
    String id,name,num,add,temp_name,temp_add,temp_num;
    TextView Name,Num,Add;
    DatabaseReference db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cust_details);

        id = getIntent().getStringExtra("id");

        Name = (TextView) findViewById(R.id.name);
        Num = (TextView) findViewById(R.id.num);
        Add = (TextView) findViewById(R.id.add);

        // get name, num and address from database using id and show them in activity

        db = FirebaseDatabase.getInstance().getReference().child("MeChat").child("Customer").child(id);
        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


                Map<String, String> map_new = (Map<String, String>) dataSnapshot.getValue();

                name = (map_new.get("name"));
                add = (map_new.get("address"));
                num = (map_new.get("phone_num"));

                Name.setText(name);
                Num.setText(num);
                Add.setText(add);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.cust_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.item1:

                final EditText name_new,num_new,add_new;
                Button sub;

                dialog = new Dialog(Cust_details.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.edit_cust);

                name_new = (EditText) dialog.findViewById(R.id.name);
                num_new = (EditText) dialog.findViewById(R.id.num);
                add_new = (EditText) dialog.findViewById(R.id.add);
                sub = (Button) dialog.findViewById(R.id.submit);

                name_new.setText(name);
                num_new.setText(num);
                add_new.setText(add);

                sub.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // update database accordingly
                        temp_add = add_new.getText().toString();
                        temp_name = name_new.getText().toString();
                        temp_num = num_new.getText().toString();

                        if(TextUtils.isEmpty(temp_add) || TextUtils.isEmpty(temp_name) || TextUtils.isEmpty(temp_num))
                            Toast.makeText(Cust_details.this,"Enter details...",Toast.LENGTH_SHORT).show();

                        else
                        {
                            db = FirebaseDatabase.getInstance().getReference().child("MeChat").child("Customer").child(id);
                            db.child("name").setValue(temp_name);
                            db.child("address").setValue(temp_add);
                            db.child("phone_num").setValue(temp_num);

                            dialog.dismiss();
                        }
                    }
                });

                dialog.show();
                break;

            case R.id.item2:
                Intent intent = new Intent(Cust_details.this, CreateTask.class);
                intent.putExtra("customerId",id);
                intent.putExtra("customerName",Name.getText());
                startActivity(intent);
                finish();
                break;
        }
        return true;
    }
}
