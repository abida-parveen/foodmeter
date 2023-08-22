package com.abida.foodmeter;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.Date;

public class DBManager {

    private Context context;
    public FirebaseDatabase mDatabase;
    public DatabaseReference meal,user;
    public static final String TAG = "MyTag";
    LocalDateTime currentTime = LocalDateTime.now();
    Month month = currentTime.getMonth();
    int year = currentTime.getYear();
    private FirebaseAuth auth;
    private String UID;
    private String currentDate;
    Date date = new Date();
    SimpleDateFormat sdf4 = new SimpleDateFormat("h:mm:ss a");
    DateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy");
    String timeStamp = sdf4.format(date);
    private double burnedCalories;
    private double totalCalCount;
    private double breakfastCalories;
    private double lunchCalories;
    private double dinnerCalories;
    private String userName;
    private String email;
    // [START storage_field_initialization]
    FirebaseStorage storage;
    // [END storage_field_initialization]

    // Create a storage reference from our app
    StorageReference storageRef;
    // [END create_storage_reference]

    // Create a storage reference from our app
    StorageReference userProfileRef;
    // [END create_storage_reference]

    private String [] spec = {"","","",""};

    public DBManager(Context context) {
        this.context = context;

        mDatabase = FirebaseDatabase.getInstance();
        meal = mDatabase.getReference();
        user = mDatabase.getReference();
        auth = FirebaseAuth.getInstance();
        UID = auth.getUid();
        currentDate = dateFormat.format(date);
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
        userProfileRef = storageRef.child(UID+"/profile.jpg");
        initializeDetails();
        initializeUser();

    }

    String getUID(){
        return UID;
    }

    void createUserSnapshot(String username,String email,String uid)
    {
        user.child("users").child(uid).child("name").setValue(username);
        user.child("users").child(uid).child("email").setValue(email);
        user.child("users").child(uid).child("timeStamp").setValue(timeStamp);
    }

    String getCurrentDate(){
        return currentDate;
    }

    void userSnapShotExist(final String username, final String email, final String uid)
    {
        Query phoneQuery = user.child("users/").orderByChild("users/");
        phoneQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean check = true;
                for(DataSnapshot singleSnapshot : dataSnapshot.getChildren()){
                    Log.e("TestHello",singleSnapshot.getKey());
                    if(singleSnapshot.getKey().equalsIgnoreCase(uid))
                    {
                        Toast.makeText(context,"User EXIST",Toast.LENGTH_LONG).show();
                        check = false;
                    }
                }
                if(check)
                {
                    createUserSnapshot(username,email,uid);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "onCancelled", databaseError.toException());
            }
        });
    }

    String getUserName () {
        return userName;
    }

    String getEmail() {
        return email;
    }

    void setUserName (String name){
        user.child("users").child(UID).child("name").setValue(name);
    }


    void initializeUser()
    {
        user.child("users/"+UID).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if(snapshot.getKey().equalsIgnoreCase("name")){
                    userName = snapshot.getValue().toString();
                    Log.e("UserName",snapshot.getValue().toString());
                }
                if(snapshot.getKey().equalsIgnoreCase("email")){
                    email = snapshot.getValue().toString();
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    void createMeal(String dishName, double cal, String mealTime)
    {

        Toast.makeText(context,cal+"",Toast.LENGTH_LONG).show();

        meal.child(UID).child(year+"").child(month+"").child(currentDate).
                child(mealTime).child("dishName").setValue(dishName);

        meal.child(UID).child(String.valueOf(year)).child(String.valueOf(month)).child(currentDate).
                child(mealTime).child("time").setValue(timeStamp);

        meal.child(UID).child(String.valueOf(year)).child(String.valueOf(month)).child(currentDate).
                child(mealTime).child("calories").setValue(""+cal/4);
        initializeDetails();
    }

    void createMealManually(double cal, String mealTime)
    {

        Toast.makeText(context,cal+"",Toast.LENGTH_LONG).show();

        meal.child(UID).child(String.valueOf(year)).child(String.valueOf(month)).child(currentDate).
                child(mealTime).child("dishName").setValue("Manually Added Record");

        meal.child(UID).child(String.valueOf(year)).child(String.valueOf(month)).child(currentDate).
                child(mealTime).child("time").setValue(timeStamp);

        meal.child(UID).child(String.valueOf(year)).child(String.valueOf(month)).child(currentDate).
                child(mealTime).child("calories").setValue(""+cal);
        initializeDetails();
    }


    void createBurnCal(Double cal)
    {

        Toast.makeText(context,cal+"",Toast.LENGTH_LONG).show();

        meal.child(UID).child(String.valueOf(year)).child(String.valueOf(month)).child(currentDate)
                .child("Burned").child("burnedCalories").setValue(cal);

        meal.child(UID).child(String.valueOf(year)).child(String.valueOf(month)).child(currentDate)
                .child("Burned").child("time").setValue(timeStamp);

        initializeDetails();
    }

    Double getDinner()
    {
        return dinnerCalories;
    }
    Double getLunch()
    {
        return lunchCalories;
    }
    Double getBreakfast()
    {
        return breakfastCalories;
    }
    Double getBurn()
    {
        return burnedCalories;
    }
    Double getNetCalories(){return (dinnerCalories+lunchCalories+breakfastCalories-burnedCalories);}

    //============================= update Summary ===========================================//

    private void addSummary() {

        double getTotal = breakfastCalories+lunchCalories+dinnerCalories;
        double getNet = getTotal-burnedCalories;

        meal.child(UID).child(String.valueOf(year)).child(String.valueOf(month)).child(currentDate).
                child("Summary").child("totalBurned").setValue(burnedCalories);

        meal.child(UID).child(String.valueOf(year)).child(String.valueOf(month)).child(currentDate).
                child("Summary").child("netCal").setValue(getNet);

        meal.child(UID).child(String.valueOf(year)).child(String.valueOf(month)).child(currentDate).
                child("Summary").child("totalGained").setValue(getTotal);

        meal.child(UID).child(String.valueOf(year)).child(String.valueOf(month)).child(currentDate).
                child("Summary").child("time").setValue(timeStamp);

        Log.e("AddSummary", totalCalCount+" sum");
    }

    //============================= end of Summary ===========================================//

    ///////////////////////////////// set Variables ///////////////////////////////////////////

    private void initializeDetails() {
        burnedCalories = 0.0;
        breakfastCalories = 0.0;
        lunchCalories = 0.0;
        dinnerCalories = 0.0;
        totalCalCount = 0.0;

        ///////////////////////////////// breakfast///////////////////////////////////////////

        meal.child(UID+"/"+year+"/"+month+"/"+currentDate+"/Breakfast").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if(snapshot.getKey().equalsIgnoreCase("calories")) {
                    breakfastCalories = Double.parseDouble(snapshot.getValue() + "");
                    Log.e("HelloTest",breakfastCalories+" breakfast");
                    addSummary();
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if(snapshot.getKey().equalsIgnoreCase("calories")) {
                    breakfastCalories = Double.parseDouble(snapshot.getValue() + "");
                    Log.e("HelloTest",""+snapshot + " onChildChanged");
                    addSummary();
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

        ////////////////////////// dinner //////////////////////////////////////////////

        meal.child(UID+"/"+year+"/"+month+"/"+currentDate+"/Dinner").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if(snapshot.getKey().equalsIgnoreCase("calories")) {
                    dinnerCalories = Double.parseDouble(snapshot.getValue() + "");
                    Log.e("HelloTest",dinnerCalories+" dinner ");
                    addSummary();
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if(snapshot.getKey().equalsIgnoreCase("calories")) {
                    dinnerCalories = Double.parseDouble(snapshot.getValue() + "");
                    Log.e("HelloTest",""+snapshot + " onChildChanged");
                    addSummary();
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

        /////////////////////////////////////////// lunch ////////////////////////////////////

        meal.child(UID+"/"+year+"/"+month+"/"+currentDate+"/Lunch").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if(snapshot.getKey().equalsIgnoreCase("calories")) {
                    lunchCalories = Double.parseDouble(snapshot.getValue() + "");
                    Log.e("HelloTest",lunchCalories+ " lunch");
                    addSummary();
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if(snapshot.getKey().equalsIgnoreCase("calories")) {
                    lunchCalories = Double.parseDouble(snapshot.getValue() + "");
                    Log.e("HelloTest",""+snapshot + " onChildChanged");
                    addSummary();
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

        /////////////////////////////////////////// burned ////////////////////////////////////

        meal.child(UID+"/"+year+"/"+month+"/"+currentDate+"/Burned").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if(snapshot.getKey().equalsIgnoreCase("burnedCalories")) {
                    burnedCalories = Double.parseDouble(snapshot.getValue() + "");
                    Log.e("HelloTest",burnedCalories+" Burned");
                    addSummary();
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if(snapshot.getKey().equalsIgnoreCase("burnedCalories")) {
                    burnedCalories = Double.parseDouble(snapshot.getValue() + "");
                    Log.e("HelloTest",""+snapshot + " onChildChanged");
                    addSummary();
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
    }

    //////////////////////////////////// set Variables end here /////////////////////////////////////


//    Bitmap getProfileImagesRef () {
//
//        StorageReference islandRef = storageRef.child("images/island.jpg");
//
//        final long ONE_MEGABYTE = 1024 * 1024;
//        islandRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
//            @Override
//            public void onSuccess(byte[] bytes) {
//                // Data for "images/island.jpg" is returns, use this as needed
//            }
//        }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception exception) {
//                // Handle any errors
//            }
//        });
//        return null;
//    }


    void setProfileImage (Bitmap image) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = userProfileRef.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
                Toast.makeText(context, "Image Uploaded", Toast.LENGTH_SHORT).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                Toast.makeText(context, "Image Uploaded", Toast.LENGTH_SHORT).show();
                // ...
            }
        });


    }

    public String[] getSpec() {
        return spec;
    }

    public StorageReference getProfileImagesRef(){
        return FirebaseStorage.getInstance().getReference(UID+"/profile.jpg");
    }

    void getDateRec(String monthComplete, final int year1, final String month, final String date)
    {
        Log.e("NEW_NEW",monthComplete+" "+year1+" "+(date+" "+month+" "+year1));
        meal.child(UID+"/"+year1+"/"+monthComplete+"/"+(date+" "+month+" "+year1)+"/Breakfast").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if(snapshot.getKey().equalsIgnoreCase("calories")) {
                    spec[0] = (snapshot.getValue() + "");
                    Log.e("NEW_NEW",snapshot.getValue()+" breakfast");
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

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

        ////////////////////////// dinner //////////////////////////////////////////////

        meal.child(UID+"/"+year1+"/"+monthComplete+"/"+(date+" "+month+" "+year1)+"/Dinner").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if(snapshot.getKey().equalsIgnoreCase("calories")) {
                    spec[2] = (snapshot.getValue() + "");
                    Log.e("NEW_NEW",snapshot.getValue()+" dinner "+ (date+" "+month+" "+year1) );
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

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

        /////////////////////////////////////////// lunch ////////////////////////////////////

        meal.child(UID+"/"+year1+"/"+monthComplete+"/"+(date+" "+month+" "+year1)+"/Lunch").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if(snapshot.getKey().equalsIgnoreCase("calories")) {
                    spec[1] = (snapshot.getValue() + "");
                    Log.e("NEW_NEW",snapshot.getValue()+ " lunch");
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

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

        /////////////////////////////////////////// burned ////////////////////////////////////

        meal.child(UID+"/"+year1+"/"+monthComplete+"/"+(date+" "+month+" "+year1)+"Burned").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if(snapshot.getKey().equalsIgnoreCase("burnedCalories")) {
                    spec[3] = snapshot.getValue() + "";
                    Log.e("NEW_NEW",snapshot.getValue()+" Burned");
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Log.e("HelloTest",""+snapshot + " onChildChanged");
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
    }

}
