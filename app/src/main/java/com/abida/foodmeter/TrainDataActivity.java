package com.abida.foodmeter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.mlkit.common.model.DownloadConditions;
import com.google.mlkit.common.model.RemoteModelManager;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.label.ImageLabel;
import com.google.mlkit.vision.label.ImageLabeler;
import com.google.mlkit.vision.label.ImageLabeling;
import com.google.mlkit.vision.label.automl.AutoMLImageLabelerLocalModel;
import com.google.mlkit.vision.label.automl.AutoMLImageLabelerOptions;
import com.google.mlkit.vision.label.automl.AutoMLImageLabelerRemoteModel;

import java.util.List;

public class TrainDataActivity extends AppCompatActivity {

    private ImageView imageView;
    private Button button;
    private TextView textView;
    private ProgressDialog progressDialog;
    private static final int ACCESS_FILE = 10;
    private static final int PERMISSION_FILE = 20;
    // Specify the name you assigned in the Firebase console.
    AutoMLImageLabelerRemoteModel remoteModel =
            new AutoMLImageLabelerRemoteModel.Builder("FoodModel_202081316945").build();
    DownloadConditions conditions = new DownloadConditions.Builder()
            .requireWifi()
            .build();
    AutoMLImageLabelerLocalModel localModel =
            new AutoMLImageLabelerLocalModel.Builder()
                    .setAssetFilePath("manifest.json")
                    .build();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_train_data);

        imageView = findViewById(R.id.image);
        button = findViewById(R.id.button);
        textView = findViewById(R.id.textView);

        progressDialog = new ProgressDialog(this);

//        button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if(ContextCompat.checkSelfPermission(TrainDataActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
//                    ActivityCompat.requestPermissions(TrainDataActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_FILE);
//                }
//                else {
//                    Intent intent =  new Intent();
//                    intent.setAction(Intent.ACTION_GET_CONTENT);
//                    intent.setType("image/*");
//                    startActivityForResult(Intent.createChooser(intent,"Pilih gambar"),ACCESS_FILE);
//                }
//            }
//        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if( requestCode == ACCESS_FILE && resultCode == Activity.RESULT_OK && data != null && data.getData() != null ) {
            Uri uri = data.getData();
            RemoteModelManager.getInstance().download(remoteModel, conditions);
            setLabelerFromLocalModel(uri);
            imageView.setImageURI(uri);
            textView.setText("");
        }
    }

    private void setLabelerFromLocalModel(Uri uri) {
        try {
            showProgressDialog();
            AutoMLImageLabelerOptions autoMLImageLabelerOptions =
                    new AutoMLImageLabelerOptions.Builder(localModel)
                            .setConfidenceThreshold(0.35f)  // Evaluate your model in the Firebase console
                            // to determine an appropriate value.
                            .build();
            ImageLabeler labeler = ImageLabeling.getClient(autoMLImageLabelerOptions);

            InputImage image = InputImage.fromFilePath(TrainDataActivity.this, uri);
            processImageLabeler(labeler,image);

        }
        catch (Exception ex)
        {
            textView.setText(ex.getMessage());
        }
    }


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
        catch (Exception ex)
        {
            textView.setText(ex.getMessage());
        }
    }


    private void processImageLabeler(ImageLabeler labeler, InputImage image) {
        labeler.process(image)
                .addOnSuccessListener(new OnSuccessListener<List<ImageLabel>>() {
                    @Override
                    public void onSuccess(List<ImageLabel> labels) {
                        progressDialog.dismiss();
                        for (ImageLabel label : labels) {
                            String text = label.getText();
                            float confidence = label.getConfidence();
                            textView.append(text+" : " + (""+confidence*100).subSequence(0,4)+"%"+"\n");
                            int index = label.getIndex();
                            break;
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(TrainDataActivity.this, e.getMessage(),Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void showProgressDialog(){
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
        progressDialog.show();
    }


}
