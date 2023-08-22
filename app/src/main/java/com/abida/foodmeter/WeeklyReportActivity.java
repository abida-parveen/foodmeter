package com.abida.foodmeter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.format.DateFormat;
import android.util.Log;
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
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class WeeklyReportActivity extends AppCompatActivity {

    CustomListAdapter mAdapter;
    List<DetailItem> mDataList;
    RecyclerView mRecyclerView;

    private FirebaseAuth auth;
    private String UID;
    private DatabaseReference databaseUsers;

    private double gainedCalories= 0.0;
    private double netCalories = 0.0;
    private double burnedCalories= 0.0;

    double[][] result = new double[7][3];

    Date date = new Date();
    String date1 = java.text.DateFormat.getDateInstance().format(date);
    String date2 = getDayString(-1);
    String date3 = getDayString(-2);
    String date4 = getDayString(-3);
    String date5 = getDayString(-4);
    String date6 = getDayString(-5);
    String date7 = getDayString(-6);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weekly_details_report);

        setTitle("Weekly Report");

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        databaseUsers = database.getReference();
        auth = FirebaseAuth.getInstance();
        UID = auth.getUid();


        mRecyclerView = findViewById(R.id.detail_recyclerView);


        mDataList = new ArrayList<>();
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new CustomListAdapter(this, mDataList);
        mRecyclerView.setAdapter(mAdapter);


//        mChildListener = new ChildEventListener() {
//            @Override
//            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//                Toast.makeText(WeeklyReportActivity.this, "User Added", Toast.LENGTH_SHORT).show();
//                DetailItem p1  = dataSnapshot.getValue(DetailItem.class);
//                if (p1==null)return;
//                p1.setTime(dataSnapshot.getKey());
//
//
//                Log.e("MyTag", p1.getTime());
                //Log.e("MyTag", "Ref: "+childRef);

//                mDataList.add(p1);
//                mAdapter.notifyDataSetChanged();

//                Map<String, Object> data = (Map<String, Object>) dataSnapshot.getValue();
//                if (data != null) {
//                    Person p1  = new Person(data.get("name")+"", Integer.parseInt(""+data.get("age")), ""+data.get("profession"), ""+data.get("country"));
//                    p1.setUid(dataSnapshot.getKey());
//                    mDataList.add(p1);
//                    mUserAdapter.notifyDataSetChanged();
//
//                }
            //}



        final String day1 = getDay(date1);
        final String day2 = getDay(date2);
        final String day3 = getDay(date3);
        final String day4 = getDay(date4);
        final String day5 = getDay(date5);
        final String day6 = getDay(date6);
        final String day7 = getDay(date7);

        final double d1[][] = initializeDetails();

        final Context context = getApplicationContext();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

            FoodData fd = new FoodData("23",1700,967,"","");

            mDataList.add(new DetailItem(d1[0][2],d1[0][1], d1[0][0],date1));
            mDataList.add(new DetailItem(d1[1][2],d1[1][1], d1[1][0],date2));
            mDataList.add(new DetailItem(d1[2][2],d1[2][1], d1[2][0],date3));
            mDataList.add(new DetailItem(d1[3][2],d1[3][1], d1[3][0],date4));
            mDataList.add(new DetailItem(d1[4][2],d1[4][1], d1[4][0],date5));
            mDataList.add(new DetailItem(d1[5][2],d1[5][1], d1[5][0],date6));
            mDataList.add(new DetailItem(d1[6][2],d1[6][1], d1[6][0],date7));

            mRecyclerView.setLayoutManager(new LinearLayoutManager(context));
            mAdapter = new CustomListAdapter(context, mDataList);
            mRecyclerView.setAdapter(mAdapter);

            }
        }, 2000);

        //Date date =  new Date();

    }

    private Date yesterday(int rec) {
        final Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, rec);
        return cal.getTime();
    }

    private String getDayString(int rec) {
        java.text.DateFormat dateFormat = new SimpleDateFormat("d MMM YYYY");
        String date = dateFormat.format(yesterday(rec));
        return date;
    }

    private String getDay(String date){
        String dayOfTheWeek = (String) DateFormat.format("EEE", Date.parse(date));
        //Toast.makeText(this, dayOfTheWeek, Toast.LENGTH_SHORT).show();
        return dayOfTheWeek;
    }

    private double[][] initializeDetails() {

        ///////////////////////////////// summary 1 ///////////////////////////////////////////
        Log.e("HelloTest", date1);
        Log.e("HelloTest", date2);
        Log.e("HelloTest", date3);
        Log.e("HelloTest", date4);
        Log.e("HelloTest", date5);
        Log.e("HelloTest", date6);
        Log.e("HelloTest", date7);

        databaseUsers.child(UID+"/"+date1+"/Summary").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Log.e("HelloTest", snapshot + " Summary saved "+date1);
                if(snapshot.getKey().equalsIgnoreCase("totalGained")) {
                    gainedCalories = Double.parseDouble(snapshot.getValue() + "");
                    Log.e("HelloTest", gainedCalories + " Summary saved "+date1);
                    result[0][0] = netCalories;
                    result[0][1] = burnedCalories;
                    result[0][2] = gainedCalories;
                }
                if(snapshot.getKey().equalsIgnoreCase("totalBurned")) {
                    burnedCalories = Double.parseDouble(snapshot.getValue() + "");
                    Log.e("HelloTest",burnedCalories+" Summary ");
                }
                if(snapshot.getKey().equalsIgnoreCase("netCal")) {
                    netCalories = Double.parseDouble(snapshot.getValue() + "");
                    Log.e("HelloTest",netCalories+" Summary ");
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if(snapshot.getKey().equalsIgnoreCase("calories")) {
                    Log.e("HelloTest",""+snapshot + " onChildChanged");
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                Log.e("HelloTest",""+snapshot + " onChildRemoved");
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Log.e("HelloTest",""+snapshot + " onChildMoved");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("HelloTest",""+error + " onCancel");
            }
        });
        ////////////////////////// summary //////////////////////////////////////////////
        ///////////////////////////////// summary 2 ///////////////////////////////////////////
        databaseUsers.child(UID+"/"+date2+"/Summary").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Log.e("HelloTest", snapshot + " Summary saved "+date2);
                if(snapshot.getKey().equalsIgnoreCase("totalGained")) {
                    gainedCalories = Double.parseDouble(snapshot.getValue() + "");
                    Log.e("HelloTest", gainedCalories + " Summary saved "+date2);
                    result[1][0] = netCalories;
                    result[1][1] = burnedCalories;
                    result[1][2] = gainedCalories;
                }
                if(snapshot.getKey().equalsIgnoreCase("totalBurned")) {
                    burnedCalories = Double.parseDouble(snapshot.getValue() + "");
                    Log.e("HelloTest",burnedCalories+" Summary ");
                }
                if(snapshot.getKey().equalsIgnoreCase("netCal")) {
                    netCalories = Double.parseDouble(snapshot.getValue() + "");
                    Log.e("HelloTest",netCalories+" Summary ");
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if(snapshot.getKey().equalsIgnoreCase("calories")) {
                    Log.e("HelloTest",""+snapshot + " onChildChanged");
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                Log.e("HelloTest",""+snapshot + " onChildRemoved");
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Log.e("HelloTest",""+snapshot + " onChildMoved");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("HelloTest",""+error + " onCancel");
            }
        });
        ////////////////////////// summary //////////////////////////////////////////////
        ///////////////////////////////// summary 3 ///////////////////////////////////////////
        databaseUsers.child(UID+"/"+date3+"/Summary").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Log.e("HelloTest", snapshot + " Summary saved "+date3);
                if(snapshot.getKey().equalsIgnoreCase("totalGained")) {
                    gainedCalories = Double.parseDouble(snapshot.getValue() + "");
                    Log.e("HelloTest", gainedCalories + " Summary saved "+date3);
                    result[2][0] = netCalories;
                    result[2][1] = burnedCalories;
                    result[2][2] = gainedCalories;
                }
                if(snapshot.getKey().equalsIgnoreCase("totalBurned")) {
                    burnedCalories = Double.parseDouble(snapshot.getValue() + "");
                    Log.e("HelloTest",burnedCalories+" Summary ");
                }
                if(snapshot.getKey().equalsIgnoreCase("netCal")) {
                    netCalories = Double.parseDouble(snapshot.getValue() + "");
                    Log.e("HelloTest",netCalories+" Summary ");
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if(snapshot.getKey().equalsIgnoreCase("calories")) {
                    Log.e("HelloTest",""+snapshot + " onChildChanged");
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                Log.e("HelloTest",""+snapshot + " onChildRemoved");
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Log.e("HelloTest",""+snapshot + " onChildMoved");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("HelloTest",""+error + " onCancel");
            }
        });
        ////////////////////////// summary //////////////////////////////////////////////
        ///////////////////////////////// summary 4 ///////////////////////////////////////////
        databaseUsers.child(UID+"/"+date4+"/Summary").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Log.e("HelloTest", snapshot + " Summary saved "+date4);
                if(snapshot.getKey().equalsIgnoreCase("totalGained")) {
                    gainedCalories = Double.parseDouble(snapshot.getValue() + "");
                    Log.e("HelloTest", gainedCalories + " Summary saved "+date4);
                    result[3][0] = netCalories;
                    result[3][1] = burnedCalories;
                    result[3][2] = gainedCalories;
                }
                if(snapshot.getKey().equalsIgnoreCase("totalBurned")) {
                    burnedCalories = Double.parseDouble(snapshot.getValue() + "");
                    Log.e("HelloTest",burnedCalories+" Summary ");
                }
                if(snapshot.getKey().equalsIgnoreCase("netCal")) {
                    netCalories = Double.parseDouble(snapshot.getValue() + "");
                    Log.e("HelloTest",netCalories+" Summary ");
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if(snapshot.getKey().equalsIgnoreCase("calories")) {
                    Log.e("HelloTest",""+snapshot + " onChildChanged");
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                Log.e("HelloTest",""+snapshot + " onChildRemoved");
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Log.e("HelloTest",""+snapshot + " onChildMoved");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("HelloTest",""+error + " onCancel");
            }
        });
        ////////////////////////// summary //////////////////////////////////////////////
        ///////////////////////////////// summary 5 ///////////////////////////////////////////
        databaseUsers.child(UID+"/"+date5+"/Summary").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Log.e("HelloTest", snapshot + " Summary saved "+date5);
                if(snapshot.getKey().equalsIgnoreCase("totalGained")) {
                    gainedCalories = Double.parseDouble(snapshot.getValue() + "");
                    Log.e("HelloTest", gainedCalories + " Summary saved "+date5);
                    result[4][0] = netCalories;
                    result[4][1] = burnedCalories;
                    result[4][2] = gainedCalories;
                }
                if(snapshot.getKey().equalsIgnoreCase("totalBurned")) {
                    burnedCalories = Double.parseDouble(snapshot.getValue() + "");
                    Log.e("HelloTest",burnedCalories+" Summary ");
                }
                if(snapshot.getKey().equalsIgnoreCase("netCal")) {
                    netCalories = Double.parseDouble(snapshot.getValue() + "");
                    Log.e("HelloTest",netCalories+" Summary ");
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if(snapshot.getKey().equalsIgnoreCase("calories")) {
                    Log.e("HelloTest",""+snapshot + " onChildChanged");
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                Log.e("HelloTest",""+snapshot + " onChildRemoved");
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Log.e("HelloTest",""+snapshot + " onChildMoved");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("HelloTest",""+error + " onCancel");
            }
        });
        ////////////////////////// summary //////////////////////////////////////////////
        ///////////////////////////////// summary 6 ///////////////////////////////////////////
        databaseUsers.child(UID+"/"+date6+"/Summary").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Log.e("HelloTest", snapshot + " Summary saved "+date6);
                if(snapshot.getKey().equalsIgnoreCase("totalGained")) {
                    gainedCalories = Double.parseDouble(snapshot.getValue() + "");
                    Log.e("HelloTest", gainedCalories + " Summary saved "+date6);
                    result[5][0] = netCalories;
                    result[5][1] = burnedCalories;
                    result[5][2] = gainedCalories;
                }
                if(snapshot.getKey().equalsIgnoreCase("totalBurned")) {
                    burnedCalories = Double.parseDouble(snapshot.getValue() + "");
                    Log.e("HelloTest",burnedCalories+" Summary ");
                }
                if(snapshot.getKey().equalsIgnoreCase("netCal")) {
                    netCalories = Double.parseDouble(snapshot.getValue() + "");
                    Log.e("HelloTest",netCalories+" Summary ");
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if(snapshot.getKey().equalsIgnoreCase("calories")) {
                    Log.e("HelloTest",""+snapshot + " onChildChanged");
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                Log.e("HelloTest",""+snapshot + " onChildRemoved");
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Log.e("HelloTest",""+snapshot + " onChildMoved");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("HelloTest",""+error + " onCancel");
            }
        });
        ////////////////////////// summary //////////////////////////////////////////////
        ///////////////////////////////// summary 7 ///////////////////////////////////////////
        databaseUsers.child(UID+"/"+date7+"/Summary").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Log.e("HelloTest", snapshot + " Summary saved "+date7);
                if(snapshot.getKey().equalsIgnoreCase("totalGained")) {
                    gainedCalories = Double.parseDouble(snapshot.getValue() + "");
                    Log.e("HelloTest", gainedCalories + " Summary saved "+date7);
                    result[6][0] = netCalories;
                    result[6][1] = burnedCalories;
                    result[6][2] = gainedCalories;
                }
                if(snapshot.getKey().equalsIgnoreCase("totalBurned")) {
                    burnedCalories = Double.parseDouble(snapshot.getValue() + "");
                    Log.e("HelloTest",burnedCalories+" Summary ");
                }
                if(snapshot.getKey().equalsIgnoreCase("netCal")) {
                    netCalories = Double.parseDouble(snapshot.getValue() + "");
                    Log.e("HelloTest",netCalories+" Summary ");
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if(snapshot.getKey().equalsIgnoreCase("calories")) {
                    Log.e("HelloTest",""+snapshot + " onChildChanged");
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                Log.e("HelloTest",""+snapshot + " onChildRemoved");
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Log.e("HelloTest",""+snapshot + " onChildMoved");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("HelloTest",""+error + " onCancel");
            }
        });
        ////////////////////////// summary //////////////////////////////////////////////
        return result;
    }

}