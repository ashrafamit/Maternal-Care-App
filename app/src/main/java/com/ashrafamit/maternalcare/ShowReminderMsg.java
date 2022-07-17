package com.ashrafamit.maternalcare;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ShowReminderMsg extends AppCompatActivity {

    private TextView msg;
    private Toolbar mToolbar;
    String getMsg,currentUserId;
    long cDate=0;
    SimpleDateFormat formater = new SimpleDateFormat("EEE, d MMM yyyy");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_reminder_msg);

        mToolbar=(Toolbar)findViewById(R.id.showReminderMsg_activity_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Reminder");

        msg=  (TextView)findViewById(R.id.tvDueMsg);
        getMsg=getIntent().getExtras().getString("msg");
        cDate=Long.parseLong(getMsg);
        Date m=new Date(cDate);
        currentUserId=getIntent().getExtras().getString("id");
        msg.setText("Your Next Visit Date:\n"+formater.format(m));

        int cnt=0;
        DatabaseReference refUpdate= FirebaseDatabase.getInstance().getReference();
        refUpdate.child("dueReminder").child(currentUserId).child(getMsg).setValue("0");


    }
}