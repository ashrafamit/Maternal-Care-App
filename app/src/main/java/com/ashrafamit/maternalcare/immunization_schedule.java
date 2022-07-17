package com.ashrafamit.maternalcare;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import de.codecrafters.tableview.TableView;
import de.codecrafters.tableview.toolkit.SimpleTableDataAdapter;
import de.codecrafters.tableview.toolkit.SimpleTableHeaderAdapter;

public class immunization_schedule extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    private Toolbar mToolbar;

    private LinearLayout front,back;
    private Button btnCalEpi, btnCalAgn;
    private TextView cal,penta,pcv,opv,mr,measles;
    SimpleDateFormat formater = new SimpleDateFormat("EEE, d MMM yyyy");

    String sDate;
    long cDate=0,getMilliSec=24*3600*1000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_immunization_schedule);

        mToolbar=(Toolbar)findViewById(R.id.is_activity_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Due Date Calculator");

        initializations();

        cal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePickerDialog();
            }
        });
        btnCalEpi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(cDate==0){
                    Toast.makeText(immunization_schedule.this,"Please select a date first",Toast.LENGTH_LONG).show();
                }else {
                    epical();
                    front.setVisibility(View.INVISIBLE);
                    back.setVisibility(View.VISIBLE);
                }
            }
        });
        btnCalAgn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                back.setVisibility(View.INVISIBLE);
                front.setVisibility(View.VISIBLE);
                cal.setText("Tap here to pick date");
                sDate="";
                cDate=0;
            }
        });

    }

    private void initializations() {
        front= (LinearLayout)findViewById(R.id.frontView);
        back=(LinearLayout)findViewById(R.id.backView);
        btnCalEpi=(Button)findViewById(R.id.btnEPICalculate);
        btnCalAgn=(Button)findViewById(R.id.btnEPICalculateAgaain);
        cal=(TextView)findViewById(R.id.tvCalenderEPI);
        penta=(TextView)findViewById(R.id.tvPenta);
        pcv=(TextView)findViewById(R.id.tvPCV);
        opv=(TextView)findViewById(R.id.tvOPV);
        mr=(TextView)findViewById(R.id.tvMR);
        measles=(TextView)findViewById(R.id.tvMeasles);
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
        sDate = formater.format(d);
        Date inActiveDate = null;
        try {
            inActiveDate = formater.parse(sDate);
            System.out.println("test date : "+ inActiveDate);
        } catch (ParseException e1) {
            // TODO Auto-generated catch block
            System.out.println("test date : error");
        }
//                dDate = DateFormat.getDateInstance(DateFormat.FULL).format(calendar2.getTime());
        cDate=inActiveDate.getTime();
        cal.setText(sDate+" "+cDate);
    }


    private void epical() {
        Date p1=new Date(cDate+6*7*getMilliSec);
        Date p2=new Date(cDate+10*7*getMilliSec);
        Date p3=new Date(cDate+14*7*getMilliSec);
        Date m=new Date(cDate+9*30*getMilliSec);
        Date me=new Date(cDate+15*30*getMilliSec);
        penta.setText("Penta1: "+formater.format(p1)+"\nPenta2: "+formater.format(p2)+"\nPenta3: "+formater.format(p3));
        pcv.setText("PCV1: "+formater.format(p1)+"\nPCV2: "+formater.format(p2)+"\nPCV3: "+formater.format(p3));
        opv.setText("OPV0: Soon After Birth\nOPV1: "+formater.format(p1)+"\nOPV2: "+formater.format(p2)+"\nOPV3: "+formater.format(p3));
        mr.setText("After :\n"+formater.format(m));
        measles.setText("After :\n"+formater.format(me));
    }

}