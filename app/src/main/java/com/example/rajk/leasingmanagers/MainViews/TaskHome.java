package com.example.rajk.leasingmanagers.MainViews;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.DefaultItemAnimator;
import com.example.rajk.leasingmanagers.helper.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.rajk.leasingmanagers.ForwardTask.forwardTask;
import com.example.rajk.leasingmanagers.R;
import com.example.rajk.leasingmanagers.adapter.taskAdapter;
import com.example.rajk.leasingmanagers.customer.UploadQuotationActivity;
import com.example.rajk.leasingmanagers.helper.FilePath;
import com.example.rajk.leasingmanagers.helper.MarshmallowPermissions;
import com.example.rajk.leasingmanagers.model.Task;
import com.example.rajk.leasingmanagers.services.UploadQuotationService;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static com.example.rajk.leasingmanagers.LeasingManagers.DBREF;

public class TaskHome extends Fragment implements taskAdapter.TaskAdapterListener {
    RecyclerView task_list;
    DatabaseReference dbTask;
    LinearLayoutManager linearLayoutManager;
    private ArrayList<Task> TaskList = new ArrayList<>();
    private taskAdapter mAdapter;
    private ActionModeCallback actionModeCallback;
    private ActionMode actionMode;
    Activity context;
    MarshmallowPermissions marshMallowPermission;
    private static final int PICK_FILE_REQUEST = 1;
    private StorageReference mStorageRef = FirebaseStorage.getInstance().getReference();
    ProgressDialog progressDialog;
    private String custId = "nocust";
    private String customerName;
    private String custName = "nocust";

    public TaskHome() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.activity_task_home, container, false);

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        context = getActivity();
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            custId = bundle.getString("custId");
            customerName = bundle.getString("customerName");
            custName = bundle.getString("custName");
        }
        marshMallowPermission = new MarshmallowPermissions(getActivity());
        progressDialog = new ProgressDialog(getActivity());
        dbTask = DBREF.child("Task").getRef();
        task_list = (RecyclerView) getView().findViewById(R.id.task_list);
        LoadData();
        mAdapter = new taskAdapter(TaskList, getContext(), this);
        linearLayoutManager = new LinearLayoutManager(getContext());
        task_list.setLayoutManager(linearLayoutManager);
        task_list.setItemAnimator(new DefaultItemAnimator());
        task_list.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));
        task_list.setAdapter(mAdapter);

        actionModeCallback = new ActionModeCallback();

    }

    @Override
    public void onRowLongClicked(int position) {
        // long press is performed, enable action mode
        enableActionMode(position);
    }

    private void enableActionMode(int position) {
        if (actionMode == null) {
            actionMode = ((AppCompatActivity) getActivity()).startSupportActionMode(actionModeCallback);
        }
        toggleSelection(position);
    }

    private void toggleSelection(int position) {
        mAdapter.toggleSelection(position);
        int count = mAdapter.getSelectedItemCount();

        if (count == 0) {
            //actionMode = null;
            actionMode.finish();
        } else {
            actionMode.setTitle(String.valueOf(count));
            actionMode.invalidate();
        }
    }

    @Override
    public void onIconClicked(int position) {
        if (actionMode == null) {
            actionMode = ((AppCompatActivity) getActivity()).startSupportActionMode(actionModeCallback);
        }
        toggleSelection(position);
    }

    @Override
    public void onMessageRowClicked(int position) {

        if (mAdapter.getSelectedItemCount() > 0) {
            enableActionMode(position);
        } else {
            Intent intent = new Intent(getContext(), TaskDetail.class);
            Task task = TaskList.get(position);
            intent.putExtra("task_id", task.getTaskId());
            startActivity(intent);
        }
    }

    private class ActionModeCallback implements ActionMode.Callback {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mode.getMenuInflater().inflate(R.menu.menu_action_mode, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.uploadquotation:
                    if (!marshMallowPermission.checkPermissionForExternalStorage())
                        marshMallowPermission.requestPermissionForExternalStorage();
                    else {
                        UploadQuotation();
                    }
                    mode = null;
                    return true;

/*                case R.id.Forwardtoquotationwala:
                    Intent intent = new Intent(getActivity(), forwardTask.class);
                    intent.putExtra("forQuotation", true);
                    List<Integer> selectedItems = mAdapter.getSelectedItems();
                    ArrayList<String> taskIds = new ArrayList<>();
                    for (Integer x : selectedItems) {
                        Task task = TaskList.get(x);
                        taskIds.add(task.getTaskId());
                    }
                    intent.putExtra("custId", custId);
                    intent.putExtra("custName", custName);
                    intent.putStringArrayListExtra("taskIds", taskIds);
                    startActivity(intent);
                    return true;
                */
                default:
                    return false;
            }
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            mAdapter.clearSelections();
            actionMode = null;
            task_list.post(new Runnable() {
                @Override
                public void run() {
                    mAdapter.resetAnimationIndex();
                    // mAdapter.notifyDataSetChanged();
                }
            });
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == PICK_FILE_REQUEST) {
                if (data == null) {
                    //no data present
                    return;
                }

                String selectedFilePath = "";
                Uri selectedFileUri = data.getData();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    selectedFilePath = FilePath.getPath(getActivity(), selectedFileUri);
                }

                if (selectedFilePath != null && !selectedFilePath.equals("")) {
                    mAdapter.resetAnimationIndex();
                    List<Integer> selectedItemPositions = mAdapter.getSelectedItems();
                    ArrayList<String> taskid_list = new ArrayList<>();

                    for (int i = selectedItemPositions.size() - 1; i >= 0; i--) {
                        selectedItemPositions.get(i);
                        final Task task = TaskList.get(selectedItemPositions.get(i));
                        taskid_list.add(task.getTaskId());
                    }

                    Intent serviceIntent = new Intent(getActivity(), UploadQuotationService.class);
                    serviceIntent.putExtra("TaskIdList", taskid_list);
                    serviceIntent.putExtra("selectedFileUri", selectedFileUri.toString());
                    serviceIntent.putExtra("customerId",custId);
                    serviceIntent.putExtra("customerName",customerName);

                    getActivity().startService(serviceIntent);
                } else {
                    Toast.makeText(getActivity(), "Cannot upload file to server", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    void LoadData() {

        dbTask.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Task task = dataSnapshot.getValue(Task.class);
                if (context instanceof UploadQuotationActivity) {
                    if (task.getCustomerId().equals(custId))
                        TaskList.add(task);

                } else
                    TaskList.add(task);

                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Task task = dataSnapshot.getValue(Task.class);
                if (context instanceof UploadQuotationActivity) {
                    if (task.getCustomerId().equals(custId))
                        TaskList.remove(task);

                } else
                    TaskList.remove(task);
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 2: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    Intent intent = new Intent();
                    intent.setType("*/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(intent, "Choose File to Upload.."), PICK_FILE_REQUEST);
                } else {

                    final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setMessage("These permissions are necessary else you cant upload images")
                            .setCancelable(false)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    ActivityCompat.requestPermissions(getActivity(),
                                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA},
                                            2);
                                    dialog.dismiss();
                                }
                            });
                    AlertDialog alert = builder.create();
                    alert.show();
                }
                return;
            }

        }
    }

    private void UploadQuotation() {
        Intent intent = new Intent();
        //sets the select file to all types of files
        intent.setType("*/*");
        //allows to select data and return it
        intent.setAction(Intent.ACTION_GET_CONTENT);
        //starts new activity to select file and return data
        startActivityForResult(Intent.createChooser(intent, "Choose File to Upload.."), PICK_FILE_REQUEST);
    }
}