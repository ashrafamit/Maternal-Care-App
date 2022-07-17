package com.ashrafamit.maternalcare;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    DrawerLayout drawerLayout;
    NavigationView navigationView;

    private Toolbar mToolbar;
    ActionBarDrawerToggle toggle;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private DatabaseReference ref,refAdminAccess;
    public String UserStatus,stUserName,category,currentUserId,currentUserGmail,stcounter;

    private TextView tvUserName,tvUserEmail,counter;
    private LinearLayout dueDate,routineVisit,childVaccination,customReminder,bmiCal,history;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initialization();

        mAuth=FirebaseAuth.getInstance();
        ref= FirebaseDatabase.getInstance().getReference();
        currentUser=mAuth.getCurrentUser();
        
        mToolbar=(Toolbar)findViewById(R.id.main_activity_toolbar);
        drawerLayout= findViewById(R.id.drawer);
        navigationView=findViewById(R.id.navigationView);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Maternal Care");
        toggle=new ActionBarDrawerToggle(this,drawerLayout,mToolbar,R.string.drawerOpen,R.string.drawerClose);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        
        
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (currentUser != null && currentUser.isEmailVerified()) {
            VerifyUserExistance();
        } else {
            LoginActivity();
        }
    }

    private void initialization() {
        dueDate=(LinearLayout)findViewById(R.id.btnDueDate);
        routineVisit=(LinearLayout)findViewById(R.id.btnRoutineVisit);
        childVaccination=(LinearLayout)findViewById(R.id.btnChildVaccination);
        customReminder=(LinearLayout)findViewById(R.id.btnCustomReminder);
        bmiCal=(LinearLayout)findViewById(R.id.btnBmi);
        history=(LinearLayout)findViewById(R.id.btnHistory);
        tvUserEmail=(TextView)findViewById(R.id.tvUserEmail);
        tvUserName=(TextView)findViewById(R.id.tvUserName);
        counter=(TextView)findViewById(R.id.tvcounter);
    }

    private void VerifyUserExistance() {
        currentUserId=mAuth.getCurrentUser().getUid();
        currentUserGmail=mAuth.getCurrentUser().getEmail();

        ref.child("User").child(currentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.child("name").exists()){
                        stUserName = dataSnapshot.child("name").getValue().toString();
                        stcounter = dataSnapshot.child("notification").getValue().toString();
                        if (stcounter.equals("0")){
                            counter.setVisibility(View.INVISIBLE);
                        }else{
                            counter.setText(stcounter);
                        }
                        tvUserName.setText(stUserName);
                        tvUserEmail.setText(currentUserGmail);

                        dueDate.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent loginIntent= new Intent(MainActivity.this,dueDateActivity.class);
                                startActivity(loginIntent);
                            }
                        });
                        childVaccination.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent immunization= new Intent(MainActivity.this,immunization_schedule.class);
                                startActivity(immunization);
                            }
                        });
                        routineVisit.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent alarmNotificationIntent= new Intent(MainActivity.this,alarmNotification.class);
                                startActivity(alarmNotificationIntent);
                            }
                        });

                        bmiCal.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent queryIntent= new Intent(MainActivity.this,queryActivity.class);
                                startActivity(queryIntent);
                            }
                        });

                }else{
                    SendUserToSettingsActivity();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void SendUserToSettingsActivity() {
        Intent settingsIntent= new Intent(MainActivity.this,settingsActivity.class);
        settingsIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(settingsIntent);
        finish();
    }

    private void LoginActivity() {
        Intent loginIntent= new Intent(MainActivity.this,loginActivity.class);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginIntent);
        finish();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        if(menuItem.getItemId() == R.id.menuCustomReminder){
        }
        if(menuItem.getItemId() == R.id.menuRoutineVisit){
        }
        if(menuItem.getItemId() == R.id.menuSttings){
        }
        if(menuItem.getItemId() == R.id.menuLogOut){
            mAuth.signOut();
            LoginActivity();
        }
        return false;
    }
}