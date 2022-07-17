package com.ashrafamit.maternalcare;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class alarmNotification extends AppCompatActivity {
    private Toolbar mToolbar;
    private FirebaseAuth mAuth;
    private DatabaseReference rootRef,ref;
    private ListView listView;
    String currentUserId,msg,fullMsg;

    private ArrayAdapter<String> arrayAdapter;
    private ArrayList<String> list_of_groups=new ArrayList<>();

    SimpleDateFormat dateFormat1 = new SimpleDateFormat("EEE, d MMM yyyy");
    SimpleDateFormat dateFormat2 = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss");


    long date1 = 0;
    long date2 = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_notification);


        mToolbar=(Toolbar)findViewById(R.id.alarmMsg_activity_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Maternal Care");

        mAuth=FirebaseAuth.getInstance();
        currentUserId=mAuth.getCurrentUser().getUid();


        rootRef= FirebaseDatabase.getInstance().getReference();
        rootRef.child("User").child(currentUserId).child("notification").setValue("0");

        InitializeFields();

        retriveDateView();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                msg=parent.getItemAtPosition(position).toString();
                Toast.makeText(alarmNotification.this,msg,Toast.LENGTH_LONG).show();
            }
        });
    }

    private void retriveDateView() {
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                Set<String> set=new HashSet<>();

                for (DataSnapshot ds:dataSnapshot.getChildren()) {
                    String s=ds.child("rtime").getValue().toString();
                    date1=Long.parseLong(s);
                    Date rDateGet = new Date(date1);
                    if (ds.child("key").getValue().toString().equals("1")) {
                        set.add(""+ds.child("ctime").getValue().toString() + "\nYour next routine visit date : " + dateFormat1.format(rDateGet));
                    }else{
                        set.add(""+ds.child("ctime").getValue().toString() + "\nYour Set Reminder at : " + dateFormat2.format(rDateGet));
                    }
                }

                list_of_groups.clear();
                list_of_groups.addAll(set);
                arrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void InitializeFields() {
        listView=(ListView)findViewById(R.id.lvNotification);
        arrayAdapter=new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,list_of_groups);
        listView.setAdapter(arrayAdapter);

        ref= FirebaseDatabase.getInstance().getReference().child("notification").child(currentUserId);
    }
}