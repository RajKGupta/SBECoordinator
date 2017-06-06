package com.example.rajk.leasingmanagers.adapter;

/**
 * Created by RajK on 04-06-2017.
 */
        import java.util.HashMap;
        import java.util.List;
        import android.content.Context;
        import android.graphics.Typeface;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.BaseExpandableListAdapter;
        import android.widget.ImageButton;
        import android.widget.RelativeLayout;
        import android.widget.TextView;
        import com.example.rajk.leasingmanagers.R;
        import com.example.rajk.leasingmanagers.model.CompletedBy;
        import com.google.firebase.database.DataSnapshot;
        import com.google.firebase.database.DatabaseError;
        import com.google.firebase.database.DatabaseReference;
        import com.google.firebase.database.FirebaseDatabase;
        import com.google.firebase.database.ValueEventListener;

public class completedByAdapter extends BaseExpandableListAdapter {

    private Context _context;
    private String taskId;
    private List<String> _listDataHeader; // header titles
    // child data in format of header title, child title
    private HashMap<String, List<CompletedBy>> _listDataChild;

    public completedByAdapter(Context context, List<String> listDataHeader,
                                 HashMap<String, List<CompletedBy>> listChildData,String taskID) {
        this._context = context;
        this._listDataHeader = listDataHeader;
        this._listDataChild = listChildData;
        this.taskId = taskID;

    }

    @Override
    public Object getChild(int groupPosition, int childPosititon) {
        return this._listDataChild.get(this._listDataHeader.get(groupPosition))
                .get(childPosititon);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.assignedto_list_row, null);
        }
        final TextView dateCompleted = (TextView) convertView
                .findViewById(R.id.dateCompleted);

        final TextView employeename = (TextView) convertView
                .findViewById(R.id.employeeName);


        final TextView employeeDesig = (TextView) convertView
                .findViewById(R.id.employeeDesig);

        final TextView dateassigned = (TextView) convertView
                .findViewById(R.id.dateAssign);


        final TextView tv_dateCompleted = (TextView) convertView
                .findViewById(R.id.tv_datecompleted);

        final TextView noteAuthor = (TextView)convertView.findViewById(R.id.noteAuthor);
        final TextView noteString = (TextView) convertView.findViewById(R.id.noteString);

        final RelativeLayout button_rl = (RelativeLayout)convertView.findViewById(R.id.button_rl);

        final CompletedBy emp = (CompletedBy) getChild(groupPosition, childPosition);

        final ImageButton removeButton = (ImageButton)convertView.findViewById(R.id.remove);
        final ImageButton remindButton = (ImageButton) convertView.findViewById(R.id.remind);

        removeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference dbCancelJob = FirebaseDatabase.getInstance().getReference().child("MeChat").child("Task").child(taskId).child("AssignedTo").child(emp.getId()).getRef();
                dbCancelJob.removeValue();
            }
        });

        remindButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });


        if(groupPosition==0)
        {
            button_rl.setVisibility(View.GONE);
            noteAuthor.setText("Employees Note:");
            tv_dateCompleted.setText("Date Completed :");
        }

        else
        {
            button_rl.setVisibility(View.VISIBLE);
            noteAuthor.setText("Coordinator's Note:");
            tv_dateCompleted.setText("Expected Deadline :");
        }

        dateassigned.setText(emp.getDateassigned());
        dateCompleted.setText(emp.getDatecompleted());
        noteString.setText(emp.getNote());

        DatabaseReference dbEmp = FirebaseDatabase.getInstance().getReference().child("MeChat").child("Employee").child(emp.getEmpId()).getRef();
        dbEmp.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String empname = dataSnapshot.child("name").getValue(String.class);
                employeename.setText(empname);

                String empdesig = dataSnapshot.child("designation").getValue(String.class);
                employeeDesig.setText(empdesig);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return this._listDataChild.get(this._listDataHeader.get(groupPosition))
                .size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this._listDataHeader.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return this._listDataHeader.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        String headerTitle = (String) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.assignedto_header, null);
        }

        TextView lblListHeader = (TextView) convertView
                .findViewById(R.id.lblListHeader);
        lblListHeader.setTypeface(null, Typeface.BOLD);
        lblListHeader.setText(headerTitle);

        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    }
