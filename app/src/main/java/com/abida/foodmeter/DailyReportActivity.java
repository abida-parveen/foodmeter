package com.abida.foodmeter;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

public class DailyReportActivity extends AppCompatActivity {

    private static final int CAMERA_REQUEST = 1888;
    private static final int MY_CAMERA_PERMISSION_CODE = 100;
    private String meal;
    private String burnedCaloriesDetails = "Burned Calories Details";
    private String burnedRecord = "Record Not Added";
    private String cal;
    private DBManager mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_report);
        Objects.requireNonNull(getSupportActionBar()).hide();

        mDatabase = new DBManager(getApplicationContext());

    }

    /////////////////////////////////////////// click Listener /////////////////////////////////////

    public void addBreakfast(View view) {
        showAddOptionsDialog("Breakfast");
    }

    public void addLunch(View view) {
        showAddOptionsDialog("Lunch");
    }

    public void addDinner(View view) {
        showAddOptionsDialog("Dinner");
    }

    public void addBurned(View view) {
        showBurnedCaloriesDialog();
    }

    ////////////////////////////////////// click listener end here ///////////////////////////////



    ////////////////////////////////////// show Info Button /////////////////////////////////////

    public void showBreakfastInfo(View view) {
        boolean test;
        if(mDatabase.getBreakfast()==0.0){test = true;}else{test = false;}
        showDetailsDialog("Breakfast Details", test?"Breakfast: Not Done":"Breakfast: Done", "Calories Taken: "+mDatabase.getBreakfast());
    }

    public void showLunchInfo(View view) {
        boolean test;
        if(mDatabase.getLunch()==0.0){test = true;}else{test = false;}
        showDetailsDialog("Lunch Details", test?"Lunch: Not Done":"Lunch: Done", "Calories Taken: "+mDatabase.getLunch());
    }

    public void showDinnerInfo(View view) {
        boolean test;
        if(mDatabase.getDinner()==0.0){test = true;}else{test = false;}
        showDetailsDialog("Dinner Details", test?"Dinner: Not Done":"Dinner: Done", "Calories Taken: "+mDatabase.getDinner());
    }

    public void showBurnedInfo(View view) {
        boolean test;
        if(mDatabase.getBurn()==0.0){test = true;}else{test = false;}
        showDetailsDialog(burnedCaloriesDetails, test?burnedRecord:"Calories Updated", "Calories Burned: "+mDatabase.getBurn());
    }

    ////////////////////////////////////////// show info buttons end here /////////////////////////////////


    /////////////////////// show choose options Box for breakfast lunch dinner ////////////////////////////////

    public void showAddOptionsDialog(final String title) {
        LayoutInflater inflater = getLayoutInflater();
        final View layout = inflater.inflate(R.layout.custom_add_food_dialog, (CardView) findViewById(R.id.add_food_dialog));

        meal = title;

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setView(layout);

        final AlertDialog alertDialog = builder.create();

        alertDialog.setCanceledOnTouchOutside(false);

        ((TextView)layout.findViewById(R.id.tvTitle)).setText((title+"!"));

        layout.findViewById(R.id.tvCamera).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                captureImage();
                alertDialog.dismiss();
            }
        });
        layout.findViewById(R.id.tvGallery).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DailyReportActivity.this, CameraResultActivity.class)
                        .putExtra("pic_image", true).putExtra("title",meal)
                );
                alertDialog.dismiss();
            }
        });
        layout.findViewById(R.id.tvAddManually).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddCaloriesDialog(title);
                alertDialog.dismiss();
            }
        });

        layout.findViewById(R.id.tvCancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

        if (alertDialog.getWindow() != null){
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }
        alertDialog.show();
    }

    ////////////////////// show choose options Box for breakfast lunch dinner ends here ////////////////

    //============================== Burned Calories Code ==================================//

    public void showBurnedCaloriesDialog() {
        LayoutInflater inflater = getLayoutInflater();
        final View layout = inflater.inflate(R.layout.custom_dialog_add_food_manually, (CardView) findViewById(R.id.add_calories_dialog));

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setView(layout);

        final AlertDialog alertDialog = builder.create();

        alertDialog.setCanceledOnTouchOutside(false);

        ((TextView)layout.findViewById(R.id.tvTitle)).setText(("Add Burned Calories"));

        layout.findViewById(R.id.btnAdd).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cal = ((TextView) layout.findViewById(R.id.etAddCalories)).getText().toString();
                //showToastMessage(cal+" Calories Added to Burned");
                //Log.e("Calories", calories+" : "+"dinner");
//                databaseUsers.child(UID).child(currentDate).child("Burned").child("burnedCalories").setValue(cal);
//                databaseUsers.child(UID).child(currentDate).child("Burned").child("time").setValue(timeStamp);
                mDatabase.createBurnCal(Double.parseDouble(cal));
                alertDialog.dismiss();

            }
        });

        layout.findViewById(R.id.btnCancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

        if (alertDialog.getWindow() != null){
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }
        alertDialog.show();
    }

    //==================================== burning calories end here =============================//


    /////////////////////////////// for manually add calories /////////////////////////////////////

    public void showAddCaloriesDialog(final String title) {
        LayoutInflater inflater = getLayoutInflater();
        final View layout = inflater.inflate(R.layout.custom_dialog_add_food_manually, (CardView) findViewById(R.id.add_calories_dialog));

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setView(layout);

        final AlertDialog alertDialog = builder.create();

        alertDialog.setCanceledOnTouchOutside(false);

        ((TextView)layout.findViewById(R.id.tvTitle)).setText(title);

        layout.findViewById(R.id.btnAdd).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String calories = ((TextView) layout.findViewById(R.id.etAddCalories)).getText().toString();
//                databaseUsers.child(UID).child(currentDate).child(title).child("calories").setValue(calories);
//                databaseUsers.child(UID).child(currentDate).child(title).child("dishName").setValue("Manually Added Record");
//                databaseUsers.child(UID).child(currentDate).child(title).child("time").setValue(timeStamp);
                mDatabase.createMealManually(Double.parseDouble(calories),title);
                Log.e("AddRecord", ""+" : "+title);
                //showToastMessage(calories+" Calories Added Manually "+title);
                alertDialog.dismiss();
            }
        });

        layout.findViewById(R.id.btnCancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

        if (alertDialog.getWindow() != null){
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }
        alertDialog.show();
    }

    ////////////////////////////// manually add calories end here //////////////////////////////////

    //////////////////////////////// detail info dialogue code //////////////////////////////////////

    public void showDetailsDialog(String title, String breakfast, String calories) {
        LayoutInflater inflater = getLayoutInflater();
        final View layout = inflater.inflate(R.layout.custom_details_dialog, (CardView) findViewById(R.id.detail_dialog));

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setView(layout);

        final AlertDialog alertDialog = builder.create();

        alertDialog.setCanceledOnTouchOutside(false);

        ((TextView)layout.findViewById(R.id.tvTitle)).setText(title);
        ((TextView)layout.findViewById(R.id.tvDoneBreakfast)).setText(breakfast);
        ((TextView)layout.findViewById(R.id.tvCaloriesTaken)).setText(calories);

        layout.findViewById(R.id.tvClose).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

        if (alertDialog.getWindow() != null){
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }
        alertDialog.show();
    }

    /////////////////////////////// detail info dialogue ends here ////////////////////////////////



    /////////////////////////////////// if we choose take picture from camera /////////////////////////////

    private void captureImage(){

        if (!hasCamera()) {
            Toast.makeText(DailyReportActivity.this, "Camera Hardware Issue", Toast.LENGTH_SHORT).show();
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.CAMERA}, MY_CAMERA_PERMISSION_CODE);
            } else {
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
            }
        }

    }

    ////////////////////////////// taking picture from camera ends here /////////////////////////////



    ////////////////////////////////// requesting q ////////////// //////////////////////////

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_CAMERA_PERMISSION_CODE)
        {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "camera permission granted", Toast.LENGTH_LONG).show();
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
            }
            else {
                Toast.makeText(this, "camera permission denied", Toast.LENGTH_LONG).show();
            }
        }
    }

    //////////////////////// permission request ends here /////////////////////////////////////////


    ////////////////////////////////// taking picture from camera result Activity /////////////////////////

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if ( resultCode == Activity.RESULT_OK) {
            if (requestCode == CAMERA_REQUEST) {
                Bitmap photo = (Bitmap) data.getExtras().get("data");
                startActivity(new Intent(DailyReportActivity.this, CameraResultActivity.class)
                        .putExtra("photo", photo).putExtra("title",meal)
                );
            }  else Toast.makeText(this, "Request code not match", Toast.LENGTH_SHORT).show();
        }
    }

    ///////////////////// taking picture from camera result Activity ends here /////////////////////////


    ///////////////////// Check if this device has a camera ///////////////////////////////////

    private boolean hasCamera() {
        return getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA);
    }

    ///////////////////// Check if this device has a camera ends here /////////////////////////


    ///////////////////////////////// toast message code ///////////////////////////////////

    private void showToastMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    ///////////////////////////////// toast message code ends here ///////////////////////////////////

}