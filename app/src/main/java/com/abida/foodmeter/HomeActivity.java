package com.abida.foodmeter;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.timqi.sectorprogressview.ColorfulRingProgressView;
import com.tsongkha.spinnerdatepicker.DatePicker;
import com.tsongkha.spinnerdatepicker.DatePickerDialog;
import com.tsongkha.spinnerdatepicker.SpinnerDatePickerDialogBuilder;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {


    private FirebaseAuth mAuth;
    private DBManager mDatabase;
    private double netCalories;
    private double breakfast;
    private double lunch;
    private double dinner;
    private double burned;
    ColorfulRingProgressView crpv;
    TextView tv,b,l,d,burn,dateChange;

    String [] months= {"JANUARY","FEBRUARY","MARCH","APRIL","MAY","JUNE","JULY","AUGUST","SEPTEMBER","OCTOBER","NOVEMBER","DECEMBER"};
    String [] month = {"Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec"};

    String spec [] = {"","","",""};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.app_name));

        setSupportActionBar(toolbar);

        crpv = (ColorfulRingProgressView) findViewById(R.id.crpv);
        tv = findViewById(R.id.data);
        b = findViewById(R.id.textView3);
        l = findViewById(R.id.textView4);
        d = findViewById(R.id.textView5);
        burn = findViewById(R.id.burn);
        dateChange = findViewById(R.id.dateC);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = new DBManager(getApplicationContext());

        netCalories = mDatabase.getNetCalories();
        breakfast = mDatabase.getBreakfast();
        lunch = mDatabase.getLunch();
        dinner = mDatabase.getDinner();
        burned = mDatabase.getBurn();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                netCalories = mDatabase.getNetCalories();
                breakfast = mDatabase.getBreakfast();
                lunch = mDatabase.getLunch();
                dinner = mDatabase.getDinner();
                burned = mDatabase.getBurn();
                dateChange.setText(mDatabase.getCurrentDate());
                crpv.setPercent((int) (netCalories/2000*100));
                tv.setText(netCalories+"");
                b.setText("breakfast "+breakfast);
                l.setText("lunch "+lunch+"");
                d.setText("dinner "+dinner+"");
                burn.setText("burned "+burned+"");
                Log.e("marHane",netCalories+" "+breakfast+" "+lunch+" "+dinner+" "+burned);
            }
        }, 4000);

        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                netCalories = mDatabase.getNetCalories();
                breakfast = mDatabase.getBreakfast();
                lunch = mDatabase.getLunch();
                dinner = mDatabase.getDinner();
                burned = mDatabase.getBurn();
                crpv.setPercent((int) (netCalories/2000*100));
                tv.setText(netCalories+"");
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);



    }

    public void showDate(View view){
        showDatePickerDialog();
    }

private void showDatePickerDialog(){
    new SpinnerDatePickerDialogBuilder()
            .context(HomeActivity.this)
            .callback(new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                    mDatabase.getDateRec(months[monthOfYear],year,month[monthOfYear],((dayOfMonth<10)?"0":"")+dayOfMonth);
                    spec = mDatabase.getSpec();
                    Double bf = (spec[0]=="")? 0.0 : Double.parseDouble(spec[0]);
                    Double ln = (spec[1]=="")? 0.0 : Double.parseDouble(spec[1]);
                    Double dn = (spec[2]=="")? 0.0 : Double.parseDouble(spec[2]);
                    Double br = (spec[3]=="")? 0.0 : Double.parseDouble(spec[3]);
                    netCalories = bf+ln+dn-br;
                    breakfast = bf;
                    lunch = ln;
                    dinner = dn;
                    burned = br;
                    dateChange.setText(((dayOfMonth<10)?"0":"")+dayOfMonth+" "+month[monthOfYear]+" "+year);
                    crpv.setPercent((int) (netCalories/2000*100));
                    tv.setText(netCalories+"");
                    b.setText("breakfast "+breakfast);
                    l.setText("lunch "+lunch+"");
                    d.setText("dinner "+dinner+"");
                    burn.setText("burned "+burned+"");
                    Toast.makeText(HomeActivity.this, ((dayOfMonth<10)?"0":"")+dayOfMonth+" "+monthOfYear+" "+year, Toast.LENGTH_SHORT).show();
                }
            })
            .spinnerTheme(R.style.NumberPickerStyle)
            .showTitle(true)

            //.customTitle("My custom title")
            .showDaySpinner(true)
            .defaultDate(2020, 9, 20)
            .maxDate(2020, 11, 31)
            .minDate(2019, 0, 1)
            .build()
            .show();
}

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == R.id.nav_daily){
//            showToastMessage("Show Daily Result");
            startActivity(new Intent(HomeActivity.this, DailyReportActivity.class));
//            finish();
        //}else if (item.getItemId() == R.id.nav_weekly){
            //startActivity(new Intent(HomeActivity.this, WeeklyReportActivity.class));
//            showToastMessage("Show Weekly Result");
        }else if (item.getItemId() == R.id.nav_monthly){
//            showToastMessage("Show Monthly Result");
            startActivity(new Intent(HomeActivity.this, MonthlyReportActivity.class));
        }else if (item.getItemId() == R.id.nav_settings){
            //showToastMessage("Settings");
            startActivity(new Intent(HomeActivity.this, SettingsActivity.class));
        }else if (item.getItemId() == R.id.nav_logout){
            showToastMessage("Logged Out");

            mAuth.signOut();

        }else
            showToastMessage("Item Selected");

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START))
            drawer.closeDrawer(GravityCompat.START);
        else
        super.onBackPressed();
    }

    private void showToastMessage(String message){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}