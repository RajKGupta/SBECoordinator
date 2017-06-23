package com.example.rajk.leasingmanagers.employee;

import android.app.Dialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.rajk.leasingmanagers.R;
import com.example.rajk.leasingmanagers.adapter.CustomerTasks_Adapter;
import com.example.rajk.leasingmanagers.adapter.EmployeeTask_Adapter;
import com.example.rajk.leasingmanagers.model.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Emp_details extends AppCompatActivity {

    Dialog dialog;
    String id,name,num,add,desig,temp_name,temp_add,temp_num,temp_designation;
    EditText Name,Num,Add,Desig;
    DatabaseReference db;
    RecyclerView rec_employeetask;
    LinearLayoutManager linearLayoutManager;
    private ArrayList<Task> TaskList = new ArrayList<>();
    private EmployeeTask_Adapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emp_details);

        id = getIntent().getStringExtra("id");

        Name = (EditText) findViewById(R.id.name);
        Num = (EditText) findViewById(R.id.num);
        Add = (EditText) findViewById(R.id.add);
        Desig = (EditText) findViewById(R.id.desig);

        rec_employeetask = (RecyclerView)findViewById(R.id.rec_employeetask);
        linearLayoutManager=new LinearLayoutManager(getApplicationContext());
        rec_employeetask.setLayoutManager(linearLayoutManager);
        rec_employeetask.setItemAnimator(new DefaultItemAnimator());
        rec_employeetask.addItemDecoration(new DividerItemDecoration(getApplicationContext(), LinearLayoutManager.VERTICAL));

        db = FirebaseDatabase.getInstance().getReference().child("MeChat").child("Employee").child(id);
        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Map<String, String> map_new = (Map<String, String>) dataSnapshot.getValue();

                name = (map_new.get("name"));
                add = (map_new.get("address"));
                num = (map_new.get("phone_num"));
                desig = (map_new.get("designation"));

                Name.setText(name);
                Num.setText(num);
                Add.setText(add);
                Desig.setText(desig);
                getSupportActionBar().setTitle(name);
                getSupportActionBar().setSubtitle(desig);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        final List<String> listoftasks = new ArrayList<>();
        db = db.child("AssignedTask").getRef();
        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot childSnapshot: dataSnapshot.getChildren()) {
                    listoftasks.add(childSnapshot.getKey());
                }
                mAdapter = new EmployeeTask_Adapter(listoftasks,getApplication(),id);
                rec_employeetask.setAdapter(mAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.emp_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.item1:

                final EditText name_new,num_new,add_new,desig_new;
                Button sub;

                dialog = new Dialog(Emp_details.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.edit_emp);

                name_new = (EditText) dialog.findViewById(R.id.name);
                num_new = (EditText) dialog.findViewById(R.id.num);
                add_new = (EditText) dialog.findViewById(R.id.add);
                desig_new = (EditText) dialog.findViewById(R.id.desig);
                sub = (Button) dialog.findViewById(R.id.submit);

                name_new.setText(name);
                num_new.setText(num);
                add_new.setText(add);
                desig_new.setText(desig);

                sub.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // update database accordingly

                        temp_add = add_new.getText().toString().trim();
                        temp_name = name_new.getText().toString().trim();
                        temp_num = num_new.getText().toString().trim();
                        temp_designation = desig_new.getText().toString().trim();

                        if(TextUtils.isEmpty(temp_add) || TextUtils.isEmpty(temp_name) || TextUtils.isEmpty(temp_num) || TextUtils.isEmpty(temp_designation))
                            Toast.makeText(Emp_details.this,"Enter details...",Toast.LENGTH_SHORT).show();

                        else{

                            db = FirebaseDatabase.getInstance().getReference().child("MeChat").child("Employee").child(id);
                            db.child("name").setValue(temp_name);
                            db.child("address").setValue(temp_add);
                            db.child("phone_num").setValue(temp_num);
                            db.child("designation").setValue(temp_designation);
                            dialog.dismiss();
                        }

                    }
                });

                dialog.show();
                break;

        }
        return true;
    }
}
