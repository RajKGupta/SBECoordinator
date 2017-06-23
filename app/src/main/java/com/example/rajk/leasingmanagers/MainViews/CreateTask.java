package com.example.rajk.leasingmanagers.MainViews;

import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.net.Uri;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.codetroopers.betterpickers.calendardatepicker.CalendarDatePickerDialogFragment;
import com.codetroopers.betterpickers.calendardatepicker.MonthAdapter;
import com.example.rajk.leasingmanagers.ForwardTask.forwardTaskScreen2;
import com.example.rajk.leasingmanagers.R;
import com.example.rajk.leasingmanagers.customer.Cust_details;
import com.example.rajk.leasingmanagers.helper.MarshmallowPermissions;
import com.example.rajk.leasingmanagers.model.Task;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.zfdang.multiple_images_selector.ImagesSelectorActivity;
import com.zfdang.multiple_images_selector.SelectorSettings;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class CreateTask extends AppCompatActivity implements CalendarDatePickerDialogFragment.OnDateSetListener {
    DatabaseReference dbRef;
    EditText taskName,startDate,endDate,quantity,description,custId;
    String customerId,customerName,curdate;
    Button submit_task;
    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
    MarshmallowPermissions marshMallowPermission;
    private ArrayList<String> mResults;
    private AlertDialog descriptionDialog ;
    private ArrayList<Uri> picUriList ;
    private int REQUEST_CODE =1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_task);
        Fresco.initialize(getApplicationContext());
        marshMallowPermission = new MarshmallowPermissions(this);
        getSupportActionBar().setTitle("Create New Task");
        dbRef= FirebaseDatabase.getInstance().getReference().child("MeChat");
        Intent intent = getIntent();
        customerName = intent.getStringExtra("customerName");
        customerId = intent.getStringExtra("customerId");
        taskName = (EditText) findViewById(R.id.taskName);
        startDate = (EditText) findViewById(R.id.startDate);
        endDate = (EditText) findViewById(R.id.endDate);
        endDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Date now = new Date();
                Calendar calendar = new GregorianCalendar();
                calendar.setTime(now);
                MonthAdapter.CalendarDay minDate = new MonthAdapter.CalendarDay(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

                CalendarDatePickerDialogFragment cdp = new CalendarDatePickerDialogFragment()
                        .setOnDateSetListener(CreateTask.this)
                        .setFirstDayOfWeek(Calendar.SUNDAY)
                        .setDateRange( minDate,null)
                        .setDoneText("Ok")
                        .setCancelText("Cancel").setThemeLight();
                cdp.show(getSupportFragmentManager(), "Select Day, Month and Year.");

            }
        });
        quantity=(EditText) findViewById(R.id.quantity);
        description = (EditText) findViewById(R.id.description);
        description.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                descriptionDialog = new AlertDialog.Builder(CreateTask.this).setTitle("Enter Description").create();
                descriptionDialog.setContentView(R.layout.description_dialog);
                descriptionDialog.show();
                if (!marshMallowPermission.checkPermissionForCamera()) {
                    marshMallowPermission.requestPermissionForCamera();
                } else {
                    if (!marshMallowPermission.checkPermissionForExternalStorage()) {
                        marshMallowPermission.requestPermissionForExternalStorage();
                    } else {
// start multiple photos selector
                        Intent intent = new Intent(CreateTask.this, ImagesSelectorActivity.class);
// max number of images to be selected
                        intent.putExtra(SelectorSettings.SELECTOR_MAX_IMAGE_NUMBER, 5);
// min size of image which will be shown; to filter tiny images (mainly icons)
// intent.putExtra(SelectorSettings.SELECTOR_MIN_IMAGE_SIZE, 100000);
// show camera or not
                        intent.putExtra(SelectorSettings.SELECTOR_SHOW_CAMERA, true);
// pass current selected images as the initial value
// intent.putStringArrayListExtra(SelectorSettings.SELECTOR_INITIAL_SELECTED_LIST, mResults);
// start the selector
                        startActivityForResult(intent, REQUEST_CODE);
                    }
                }
            }
        });
        custId = (EditText) findViewById(R.id.custId);
        custId.setText(customerId+": "+customerName);
        submit_task = (Button)findViewById(R.id.submit_task);
        Calendar c = Calendar.getInstance();
        curdate = dateFormat.format(c.getTime());
        startDate.setText(curdate);


        submit_task.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createTask();
            }
        });
    }
    void createTask()
    {
        String taskname = taskName.getText().toString().trim();

        String qty = quantity.getText().toString().trim();

        String desc = description.getText().toString().trim();

        String enddate = endDate.getText().toString().trim();

        String startdate= startDate.getText().toString().trim();

        long curTime = Calendar.getInstance().getTimeInMillis();
        curTime=9999999999999L-curTime;

        if(TextUtils.isEmpty(taskname)||TextUtils.isEmpty(qty)||TextUtils.isEmpty(desc)||TextUtils.isEmpty(enddate)||TextUtils.isEmpty(startdate)) {
            Toast.makeText(CreateTask.this,"Fill all the details",Toast.LENGTH_SHORT).show();
        }
        else
            {
            Task newTask = new Task("task" + curTime, taskname, startdate, enddate, qty, desc, customerId, getRandomMaterialColor("400"));
            dbRef.child("Task").child("task" + curTime).setValue(newTask);
            dbRef.child("Customer").child(customerId).child("Task").child("task" + curTime).setValue("pending");
                Toast.makeText(CreateTask.this,"Task Created",Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(CreateTask.this, Cust_details.class);
                intent.putExtra("id",customerId);
                startActivity(intent);
                finish();
            }
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
        Intent intent = new Intent(CreateTask.this, Cust_details.class);
        intent.putExtra("id",customerId);
        startActivity(intent);
        finish();
    }

    @Override
    public void onDateSet(CalendarDatePickerDialogFragment dialog, int year, int monthOfYear, int dayOfMonth) {
        endDate.setText(dayOfMonth+"/"+(monthOfYear+1)+"/"+year);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE) {
            if (data != null) {
                mResults = data.getStringArrayListExtra(SelectorSettings.SELECTOR_RESULTS);
                assert mResults != null;

                picUriList = new ArrayList<>();
// show results in textview
                StringBuilder sb = new StringBuilder();
                System.out.println(String.format("Totally %d images selected:", mResults.size()));
                for (String result : mResults) {
                    picUriList.add(Uri.parse(result));
                }

            }
        }
    }

}
