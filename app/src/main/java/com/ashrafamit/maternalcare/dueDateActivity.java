package com.ashrafamit.maternalcare;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.ashrafamit.maternalcare.databinding.ActivityMainBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class dueDateActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {
    private Toolbar mToolbar;
    private RelativeLayout calculationLayout, resultLayout;
    int cycle=28,diff=0;
    String dDate;
    String date1;
    long lastDate=0,getMilliSec=24*3600*1000,periodlength=0;
    long nextPeriodm, ffdaym,lfdaym, dueDatem;


    private Spinner spinnerDays;
    private TextView cal,result,weeks;
    private Button btnCal,btnCalAgn,btnUp;
    DatePickerDialog.OnDateSetListener dateSetListener;
    SimpleDateFormat formater = new SimpleDateFormat("EEE, d MMM yyyy");

    ArrayList<String> w,wx;

    private DatabaseReference refUp;
    private FirebaseAuth dAuth;

    private AlarmManager alarmManager;
    private PendingIntent pendingIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_due_date);

        mToolbar=(Toolbar)findViewById(R.id.dueDate_activity_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Due Date Calculator");
        
        initialization();
        createNotificationChannel();
        resultLayout.setVisibility(View.INVISIBLE);
        calculationLayout.setVisibility(View.VISIBLE);
        spinnerFunc();

        cal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePickerDialog();
            }
        });

        btnCal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(lastDate==0){
                    Toast.makeText(dueDateActivity.this,"Please select a date first",Toast.LENGTH_LONG).show();
                }else {
                    btnCalFunc();
                    resultLayout.setVisibility(View.VISIBLE);
                    calculationLayout.setVisibility(View.INVISIBLE);
                }
            }
        });

        btnCalAgn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cycle=28;
                lastDate=0;
                date1="";
                resultLayout.setVisibility(View.INVISIBLE);
                calculationLayout.setVisibility(View.VISIBLE);
                cal.setText("Tap here to pick date");
            }
        });

        btnUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                upoadFunc();
            }
        });
    }

    private void initialization() {
        spinnerDays=(Spinner)findViewById(R.id.spDays);

        cal= (TextView)findViewById(R.id.tvCalender);
        result=(TextView)findViewById(R.id.tvShowResult);
        weeks=(TextView)findViewById(R.id.tvShowVisitDate);

        btnCal=(Button)findViewById(R.id.btnCalDueDate);
        btnCalAgn=(Button)findViewById(R.id.btnCalAgain);
        btnUp=(Button)findViewById(R.id.btnDueUpload);

        calculationLayout=(RelativeLayout)findViewById(R.id.dueCalculatorLayout);
        resultLayout=(RelativeLayout)findViewById(R.id.dueResultLayout);

        dAuth=FirebaseAuth.getInstance();
        refUp= FirebaseDatabase.getInstance().getReference();
    }

    private void spinnerFunc() {
        ArrayList<Integer> num= new ArrayList<>();

        for(int i= 20;i<=52;i++){
            num.add(i);
        }

        ArrayAdapter<Integer> adapter;
        adapter= new ArrayAdapter<Integer>(this,android.R.layout.simple_spinner_item,num);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDays.setAdapter(adapter);

        int spinnerPosition = adapter.getPosition(cycle);
        spinnerDays.setSelection(spinnerPosition);

        spinnerDays.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                cycle= Integer.parseInt(adapterView.getItemAtPosition(position).toString());
                periodlength=cycle*getMilliSec;

                Toast.makeText(dueDateActivity.this,""+cycle,Toast.LENGTH_LONG).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void showDatePickerDialog() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                this,
                Calendar.getInstance().get(Calendar.YEAR),
                Calendar.getInstance().get(Calendar.MONTH),
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Calendar c=Calendar.getInstance();

        c.set(Calendar.YEAR,year);
        c.set(Calendar.MONTH,month);
        c.set(Calendar.DAY_OF_MONTH,dayOfMonth);

        Date d= c.getTime();
        date1 = formater.format(d);
        Date inActiveDate = null;
        try {
            inActiveDate = formater.parse(date1);
            System.out.println("test date : "+ inActiveDate);
        } catch (ParseException e1) {
            // TODO Auto-generated catch block
            System.out.println("test date : error");
        }
//                dDate = DateFormat.getDateInstance(DateFormat.FULL).format(calendar2.getTime());
        lastDate=inActiveDate.getTime();
        cal.setText(date1+" "+lastDate);
    }


    private void btnCalFunc() {
        nextPeriodm= lastDate+cycle*getMilliSec;
        ffdaym=nextPeriodm-16*getMilliSec;
        lfdaym=nextPeriodm-12*getMilliSec;
        diff=cycle-28;
        dueDatem=lastDate+280*getMilliSec+diff*getMilliSec;

        Date np=new Date(nextPeriodm);
        Date ffd=new Date(ffdaym);
        Date lfd=new Date(lfdaym);
        Date dd=new Date(nextPeriodm);

        result.setText("Last period : "+ date1+ "\nNext period : "+formater.format(np)+"\nFirst firtile day : "+formater.format(ffd)+
                "\nLast firtile day : "+formater.format(lfd)+"\nYour estimated due date will be : "+formater.format(dd));

        w=new ArrayList<String>();
        wx=new ArrayList<String>();


        for (int i = 4; i < 29; i = i + 4) {
            Date convertDate=new Date(lastDate+i*7*getMilliSec);
            w.add(formater.format(convertDate));
            wx.add(Long.toString(convertDate.getTime()));

        }
        for (int i = 30; i < 37; i = i + 2){
            Date convertDate=new Date(lastDate+i*7*getMilliSec);
            w.add(formater.format(convertDate));
            wx.add(Long.toString(convertDate.getTime()));
        }
        for (int i = 37; i < 41; i++){
            Date convertDate=new Date(lastDate+i*7*getMilliSec);
            w.add(formater.format(convertDate));
            wx.add(Long.toString(convertDate.getTime()));
        }
        weeks.setText("1st visit date : "+w.get(0)+"\n2nd visit date : "+w.get(1)+"\n3rd visit date : "+w.get(2)+"\n4th visit date : "+w.get(3)
                +"\n5th visit date : "+w.get(4)+"\n6th visit date : "+w.get(5)+"\n7th visit date : "+w.get(6)+"\n8th visit date : "+w.get(7)
                +"\n9th visit date : "+w.get(8)+"\n10th visit date : "+w.get(9)+"\n11th visit date : "+w.get(10)+"\n12th visit date : "+w.get(11)
                +"\n13th visit date : "+w.get(12)+"\n14th visit date : "+w.get(13)+"\n15th visit date : "+w.get(14));
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            CharSequence name = "foxandroidReminderChannel";
            String description = "Channel For Alarm Manager";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel("foxandroid",name,importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void upoadFunc() {

        String currentUserId=dAuth.getCurrentUser().getUid();
        final HashMap<String, String> wMap = new HashMap<>();



        wMap.put("1657994400000","1");
        for(int i=0;i<=14;i++){
            wMap.put(wx.get(i),"1");

            refUp.child("dueReminder").child(currentUserId).setValue(wMap);
//
//            long date1 = 0;
//            long date2 = 0;
//
//            Date currentDate = new Date();
//            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
//            String stringdate1 = dateFormat.format(currentDate);
//
//            try {
//                Date cdate= dateFormat.parse(stringdate1);
//                date1 = cdate.getTime();
//                date2= date1 + 86400000;
//                    System.out.println("cdate abc " + date1);
//            } catch (ParseException e) {
//                System.out.println("error date "+ e);
//            }
//
//            if (alarmManager == null){
//                alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
//            }
//            alarmManager.cancel(pendingIntent);
        }
        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(dueDateActivity.this,AlarmReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(dueDateActivity.this,0,intent,PendingIntent.FLAG_UPDATE_CURRENT);
        long triggerAfter = 2 * 60 * 1000;
        long triggerEvery = 200 * 60 * 1000;
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, triggerAfter,
                triggerEvery, pendingIntent);

//        for(int i=0;i<=14;i++){
//            wMap.put("date",wx.get(i));
//            wMap.put("status","1");
//
//            refUp.child("dueReminder").child(currentUserId).child(wx.get(i)).setValue(wMap);
//        }


//        Toast.makeText(dueDateActivity.this,currentUserId,Toast.LENGTH_LONG).show();
    }

}