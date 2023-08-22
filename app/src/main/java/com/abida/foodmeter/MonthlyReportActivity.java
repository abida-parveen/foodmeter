package com.abida.foodmeter;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.hadiidbouk.charts.BarData;
import com.hadiidbouk.charts.ChartProgressBar;
import com.whiteelephant.monthpicker.MonthPickerDialog;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MonthlyReportActivity extends AppCompatActivity {

    CustomAdapter mAdapter;
    List<DetailItem> mDataList;
    RecyclerView mRecyclerView;

    private FirebaseAuth auth;
    private String UID;
    private DatabaseReference databaseUsers;

    private String gainedCalories= "";
    private String netCalories = "";
    private String burnedCalories= "";

    private TextView change;

    String [] months= {"JANUARY","FEBRUARY","MARCH","APRIL","MAY","JUNE","JULY","AUGUST","SEPTEMBER","OCTOBER","NOVEMBER","DECEMBER"};
    String [] monthHalf = {"Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec"};

    LocalDateTime currentTime = LocalDateTime.now();
    Month month = currentTime.getMonth();
    int m = currentTime.getMonthValue();
    int year = currentTime.getYear();
    int selectedMonth,selectedYear;

    Date date = new Date();
    String [] key = new String [31];
    String currentDate = java.text.DateFormat.getDateInstance().format(date);


    int index = 0;

    Calendar cal = Calendar.getInstance();
    String month2 = new SimpleDateFormat("MMM").format(cal.getTime());

    String [][] result = new String[31][4];
    ChartProgressBar mChart;
    ArrayList<BarData> dataList = new ArrayList<>();
    BarData data;
    Context context;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monthly_details_report);

        setTitle("Monthly Report");

        mRecyclerView = findViewById(R.id.detail_recyclerView);

        change = findViewById(R.id.choose_month);
        change.setText(month2+ " "+year);

        context = getApplicationContext();


        FirebaseDatabase database = FirebaseDatabase.getInstance();
        databaseUsers = database.getReference();
        auth = FirebaseAuth.getInstance();
        UID = auth.getUid();

        key[0] = java.text.DateFormat.getDateInstance().format(date);
        getDayString(-1);

        mDataList = new ArrayList<>();

        for (int i=0;i<key.length;i++) {
            if(key[i]!=null){
                initializeDetails(i);

            }
        }


        change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final MonthPickerDialog.Builder builder = new MonthPickerDialog.Builder(MonthlyReportActivity.this,
                        new MonthPickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(int selectedMonth, int selectedYear) {
                                String date = "01 "+monthHalf[selectedMonth]+" "+selectedYear;
                                for(int i = 0; i<key.length; i++)
                                {
                                    key[i]=null;
                                }
                                try {
                                    String d = getLastDayOfMonth(selectedYear,selectedMonth+1);
                                    change.setText(monthHalf[selectedMonth]+ " "+ year);
                                    key[0] = d;
                                    mChart.removeAllViews();
                                    getDayStringSpec(-1,d,monthHalf[selectedMonth], selectedYear);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                mDataList = new ArrayList<>();

                                for (int i=0;i<key.length;i++) {
                                    if(key[i]!=null){
                                        getMonthRec(i,months[selectedMonth],selectedYear);
                                    }
                                }
                                setData();
//                                Toast.makeText(MonthlyReportActivity.this, selectedMonth+" "+selectedYear, Toast.LENGTH_SHORT).show();
                            }
                        }, year, m);

                builder
                        .setActivatedMonth(m-1)
//                        .setMinYear(2019)
                        .setActivatedYear(year)
//                        .setMaxYear(year)
                        .setMinMonth(Calendar.JANUARY)
                        .setTitle("Select trading month")
                        //.setMaxMonth(m)
                         .setYearRange(2019, year)
                        // .setMonthAndYearRange(Calendar.FEBRUARY, Calendar.OCTOBER, 1890, 1890)
                        //.showMonthOnly()
                        // .showYearOnly()
                        .setOnMonthChangedListener(new MonthPickerDialog.OnMonthChangedListener() {
                            @Override
                            public void onMonthChanged(int Month) {
                                selectedMonth = Month;
                                //Toast.makeText(context, "Month "+selectedMonth, Toast.LENGTH_SHORT).show();
                            } })
                        .setOnYearChangedListener(new MonthPickerDialog.OnYearChangedListener() {
                            @Override
                            public void onYearChanged(int Year) {
                                selectedYear = Year;
                                if (Year == year){
                                    builder.setMonthRange(Calendar.JANUARY, m-1);
                                }

                                //Toast.makeText(context, "Year "+selectedYear+ " ", Toast.LENGTH_SHORT).show();
                            } })

                        .build()
                        .show();
            }

        });


        setData();



//        FoodData fd = new FoodData("23",1700,967,"","");
//        int u=1335*7, b=255*7;
//        mDataList.add(new DetailItem(u,b, (u-b),"Wk1"));



//        for (int i=0;i<key.length;i++)
//        {
//            if(key[i]!=null)
//            {
//                double net = result[i][1]!=null?Double.parseDouble(result[i][3]):0.0;
//                String d = key[i].replace(month2+" "+year,"");
//                data = new BarData(d, (float) net, ""+net);
//                dataList.add(data);
//            }
//        }
//        mChart = (ChartProgressBar) findViewById(R.id.ChartProgressBar);
//
//        mChart.setDataList(dataList);
//        mChart.build();



    }

    void setData(){
        dataList.clear();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                for (int i=0;i<key.length;i++) {
                    if(key[i]!=null){
                        double gained = result[i][1]!=null?Double.parseDouble(result[i][1]):0.0;
                        double burned = result[i][1]!=null?Double.parseDouble(result[i][2]):0.0;
                        double net = result[i][1]!=null?Double.parseDouble(result[i][3]):0.0;
                        String d = key[i].replace(change.getText().toString(),"");
                        data = new BarData(d+" ", (float) net, ""+net);
                        dataList.add(data);
                        mChart = (ChartProgressBar) findViewById(R.id.ChartProgressBar);
                        mChart.setDataList(dataList);
                        mChart.build();
                        mDataList.add(new DetailItem(gained,burned, net,key[i]));
                    }
                }
                mRecyclerView.setLayoutManager(new LinearLayoutManager(context));
                mAdapter = new CustomAdapter(context, mDataList);
                mRecyclerView.setAdapter(mAdapter);
            }
        }, 5000);
    }

    private Date yesterday(int rec) {
        final Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, rec);
        return cal.getTime();
    }

    private void getDayString(int rec) {
        java.text.DateFormat dateFormat = new SimpleDateFormat("dd MMM YYYY");
        String date = dateFormat.format(yesterday(rec));
        if(date.contains(month2))
        {
            int ind = rec*-1;
            key[ind] = date;
            getDayString(rec-1);
        }
    }

    private Date yesterdaySpec(int rec, Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DATE, rec);
        return cal.getTime();
    }

    private void getDayStringSpec(int rec, String de, String mon, int ye) throws Exception{
        DateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy");
        Date d = dateFormat.parse(de);
        String date = dateFormat.format(yesterdaySpec(rec, d));
        if(date.contains(mon))
        {
            int ind = rec*-1;
            key[ind] = date;
            getDayStringSpec(rec-1,de,mon, ye);
        }
    }


    private void initializeDetails(final int ind) {

        ///////////////////////////////// summary 1 ///////////////////////////////////////////
        databaseUsers.child(UID + "/" + year + "/" + month + "/" + key[ind] + "/Summary").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if(snapshot.getKey().equalsIgnoreCase("totalGained")) {
                    Log.e("Monthly", snapshot.getKey() + " and value " +snapshot.getValue());
                    gainedCalories = snapshot.getValue().toString();
                    result[ind][0] = key[index];
                    result[ind][1] = gainedCalories;
                    result[ind][2] = burnedCalories;
                    result[ind][3] = netCalories;
                }
                if (snapshot.getKey().equalsIgnoreCase("totalBurned")) {
                    Log.e("Monthly", snapshot.getKey() + " and value " +snapshot.getValue());
                    burnedCalories = snapshot.getValue().toString();
                }
                if (snapshot.getKey().equalsIgnoreCase("netCal")) {
                    Log.e("Monthly", snapshot.getKey() + " and value " +snapshot.getValue());
                    netCalories = snapshot.getValue().toString();
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if (snapshot.getKey().equalsIgnoreCase("calories")) {
                    Log.e("HelloTest", "" + snapshot + " onChildChanged");
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                Log.e("HelloTest", "" + snapshot + " onChildRemoved");
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Log.e("HelloTest", "" + snapshot + " onChildMoved");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("HelloTest", "" + error + " onCancel");
            }
        });

    }

    private void getMonthRec(final int ind,String mon, int year1) {

        ///////////////////////////////// summary 1 ///////////////////////////////////////////
        databaseUsers.child(UID + "/" + year1 + "/" + mon + "/" + key[ind] + "/Summary").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if(snapshot.getKey().equalsIgnoreCase("totalGained")) {
                    Log.e("Monthly", snapshot.getKey() + " and value " +snapshot.getValue());
                    gainedCalories = snapshot.getValue().toString();
                    result[ind][0] = key[index];
                    result[ind][1] = gainedCalories;
                    result[ind][2] = burnedCalories;
                    result[ind][3] = netCalories;
                }
                if (snapshot.getKey().equalsIgnoreCase("totalBurned")) {
                    Log.e("Monthly", snapshot.getKey() + " and value " +snapshot.getValue());
                    burnedCalories = snapshot.getValue().toString();
                }
                if (snapshot.getKey().equalsIgnoreCase("netCal")) {
                    Log.e("Monthly", snapshot.getKey() + " and value " +snapshot.getValue());
                    netCalories = snapshot.getValue().toString();
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if (snapshot.getKey().equalsIgnoreCase("calories")) {
                    Log.e("HelloTest", "" + snapshot + " onChildChanged");
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                Log.e("HelloTest", "" + snapshot + " onChildRemoved");
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Log.e("HelloTest", "" + snapshot + " onChildMoved");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("HelloTest", "" + error + " onCancel");
            }
        });

    }

    public String getLastDayOfMonth(int year, int month) throws Exception{
        DateFormat sdf = new SimpleDateFormat("dd MMM yyyy");

        Date date = sdf.parse("01 "+monthHalf[month-1]+" "+year);

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        calendar.add(Calendar.MONTH, 1);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.add(Calendar.DATE, -1);

        Date lastDayOfMonth = calendar.getTime();

        return sdf.format(lastDayOfMonth);
    }

}