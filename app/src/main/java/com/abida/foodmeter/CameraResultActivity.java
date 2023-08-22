package com.abida.foodmeter;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.mlkit.common.model.DownloadConditions;
import com.google.mlkit.common.model.RemoteModelManager;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.label.ImageLabel;
import com.google.mlkit.vision.label.ImageLabeler;
import com.google.mlkit.vision.label.ImageLabeling;
import com.google.mlkit.vision.label.automl.AutoMLImageLabelerLocalModel;
import com.google.mlkit.vision.label.automl.AutoMLImageLabelerOptions;
import com.google.mlkit.vision.label.automl.AutoMLImageLabelerRemoteModel;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class CameraResultActivity extends AppCompatActivity {
    private static final int PICK_PHOTO_CODE = 743;
    private static final int MY_PERMISSION_CODE = 110;
    private ImageView imageView;
    private String DishName;
    private String meal;
    private TextView tvCalories, tvDishName;
    private String [] calorieData = {"ChickenBiryani","Chicken Briyani","3703.85","Chickenbroast","Chicken Broast","1252.5","ChickenBurger","Chicken Burger",
            "2146.25","ChickenHaleem","Chicken Haleem","2804.5","ChickenKorma","Chicken Korma","4179.95","ChickenNihari","Chicken Nihari","2070.2",
            "ChickenPasta","Chicken Pasta","3158.5","DaalPalak","Daal Palak","901","FrenchFries","French Fries","1163.5","MixVegetable","Mix Vegetable","1469"};

    private double calories = 0.0;
    private ProgressDialog progressDialog;
    DBManager mDatabase;


    ////////////////////////////// initializing model //////////////////////////////////////////

    AutoMLImageLabelerRemoteModel remoteModel =
            new AutoMLImageLabelerRemoteModel.Builder("FoodModel_202081316945").build();
    DownloadConditions conditions = new DownloadConditions.Builder()
            .requireWifi()
            .build();
    AutoMLImageLabelerLocalModel localModel =
            new AutoMLImageLabelerLocalModel.Builder()
                    .setAssetFilePath("manifest.json")
                    .build();

    ////////////////////////////// initializing model end //////////////////////////////////////////

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_details);


        imageView = (ImageView)this.findViewById(R.id.imageView1);

        tvCalories = findViewById(R.id.tvCaloriesInDish);
        tvDishName = findViewById(R.id.tvDishName);

        mDatabase = new DBManager(getApplicationContext());

        progressDialog = new ProgressDialog(this);

        boolean picImage = getIntent().getBooleanExtra("pic_image", false);
        meal = getIntent().getStringExtra("title");
        if (picImage) {
            pickImage();
        } else {
            Bitmap bitmap = (Bitmap) getIntent().getParcelableExtra("photo");

            imageView.setImageBitmap(bitmap);
            RemoteModelManager.getInstance().download(remoteModel, conditions);
            setLabelerFromLocalModel(bitmap);
        }

    }

    /////////////////////////////// request permissions ////////////////////////////////////////////

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_PERMISSION_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "camera permission granted", Toast.LENGTH_LONG).show();
                pickImage();
            } else {
                Toast.makeText(this, "camera permission denied", Toast.LENGTH_LONG).show();
            }
        }
    }

    /////////////////////////////// request permissions ////////////////////////////////////////////


    /////////////////////////////// result activity pic from gallery ////////////////////////////////////////////

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == PICK_PHOTO_CODE) {
                if (data != null && data.getData() != null) {
                    try {
                        Uri uri = data.getData();

                        final Bitmap photo = (Bitmap) MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                        imageView.setImageBitmap(photo);

                        ////===========================================
                        RemoteModelManager.getInstance().download(remoteModel, conditions);
                        setLabelerFromLocalModel(uri);


                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    /////////////////////////////// result activity pic from gallery end ////////////////////////////////////////



    /////////////////////////////// choose pic from gallery ////////////////////////////////////////////

    public void pickImage() {
        Intent intent = new Intent();
        intent.setType("image/*");//data type
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Choose Image"), PICK_PHOTO_CODE);
    }

    /////////////////////////////// choose pic from gallery ////////////////////////////////////////////


    /////////////////////////////// Sending Pic to train ////////////////////////////////////////////

    private void setLabelerFromLocalModel(Uri uri) {
        try {
            showProgressDialog();
            AutoMLImageLabelerOptions autoMLImageLabelerOptions =
                    new AutoMLImageLabelerOptions.Builder(localModel)
                            .setConfidenceThreshold(0.35f)  // Evaluate your model in the Firebase console
                            // to determine an appropriate value.
                            .build();
            ImageLabeler labeler = ImageLabeling.getClient(autoMLImageLabelerOptions);

            InputImage image = InputImage.fromFilePath(CameraResultActivity.this, uri);
            processImageLabeler(labeler,image);

        }
        catch (Exception ex) {
            Toast.makeText(this, ""+ex.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    ///////////////////////////////  ////////////////////////////////////////////

    ///////////////////////////////  ////////////////////////////////////////////

    private void setLabelerFromLocalModel(Bitmap bitmap) {
        try {
            showProgressDialog();
            AutoMLImageLabelerOptions autoMLImageLabelerOptions =
                    new AutoMLImageLabelerOptions.Builder(localModel)
                            .setConfidenceThreshold(0.35f)  // Evaluate your model in the Firebase console
                            // to determine an appropriate value.
                            .build();
            ImageLabeler labeler = ImageLabeling.getClient(autoMLImageLabelerOptions);

            InputImage image = InputImage.fromBitmap(bitmap, 0);
//            InputImage image = InputImage.fromFilePath(TrainDataActivity.this, uri);
            processImageLabeler(labeler,image);

        }
        catch (Exception ex) {
            Toast.makeText(this, ""+ex.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    ///////////////////////////////  ////////////////////////////////////////////

    ///////////////////////////////  ////////////////////////////////////////////

    private void processImageLabeler(ImageLabeler labeler, InputImage image) {
        labeler.process(image)
                .addOnSuccessListener(new OnSuccessListener<List<ImageLabel>>() {
                    @Override
                    public void onSuccess(List<ImageLabel> labels) {
                        progressDialog.dismiss();
                        String msg="";
                        for (ImageLabel label : labels) {
                            String text = label.getText();
                            float confidence = label.getConfidence();
                            tvDishName.setText(text+" : " + (""+confidence*100).subSequence(0,4)+"%"+"\n");
                            for(int i=0;i<calorieData.length;i+=3)
                            {
                                if(calorieData[i].equalsIgnoreCase(text)){
                                    DishName = calorieData[i+1];
                                    calories = Double.parseDouble(calorieData[i+2]);
                                }
                            }
                            tvCalories.setText(calories/4+"");
                            break;
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(CameraResultActivity.this, e.getMessage(),Toast.LENGTH_LONG).show();
                    }
                });
    }

    /////////////////////////////// getting trained data ////////////////////////////////////////////

    ////////////////////////////// show progress dialog ///////////////////////////////////////

    private void showProgressDialog(){
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    ///////////////////////////////////// end progress dialog ///////////////////////////////////

    public void cancelClicked(View view) {
        finish();
    }

    public void saveDataToFirebase(View view) {

        mDatabase.createMeal(DishName,calories,meal);

        finish();
    }

}