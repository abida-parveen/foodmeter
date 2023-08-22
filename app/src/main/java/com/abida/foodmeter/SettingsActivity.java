package com.abida.foodmeter;


import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Objects;
import java.util.UUID;

public class SettingsActivity extends AppCompatActivity {

    DBManager dbm;



    ImageView ivUserImage;
    TextView tvId;
    EditText etName;

    Uri profileImageUri;
    Bitmap profileImageBitmap;
    StorageTask uploadTask;

    boolean isUploading = false;

    final int PICK_PHOTO_CODE = 743;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Objects.requireNonNull(getSupportActionBar()).hide();

        dbm = new DBManager(this);

        etName = findViewById(R.id.etName);
        ivUserImage = findViewById(R.id.ivUserImage);

        ivUserImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickImage();
            }
        });




//        etName.setText(dbm.getUserName());
        loadProfileImage();


    }
//
    public void copyId(View view) {
        copyId();
    }
//
    public void saveChanges(View view) throws FileNotFoundException {
        if (isUploading)
            return;

        String name = etName.getText().toString();
        Drawable image = ivUserImage.getDrawable();

        if (!name.equals("")){
            dbm.setUserName(name);
        }
        if (profileImageBitmap!=null){
            dbm.setProfileImage(profileImageBitmap);
//        }else {
//            updateProfile();
        }



    }

//    private void updateProfile(){
//        DatabaseReference userRef = dbm.getUsersRef().child(dbm.getUserId());
//        userRef.setValue(user);
//        // Read from the database
//        userRef.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                Toast.makeText(EditProfileActivity.this, "Changes Saved", Toast.LENGTH_SHORT).show();
//            }
//
//            @Override
//            public void onCancelled(DatabaseError error) {
//                Toast.makeText(EditProfileActivity.this, R.string.connection_fail_message+"\n"+error.toException(), Toast.LENGTH_SHORT).show();
//            }
//        });
//    }


    public void pickImage() {
        Intent intent = new Intent();
        intent.setType("image/*");//data type
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Choose File"), PICK_PHOTO_CODE);
    }
//
//
    private void copyId(){
        /*Create an ACTION_SEND Intent*/
        Intent intent = new Intent(android.content.Intent.ACTION_SEND);
        /*The type of the content is text, obviously.*/
        intent.setType("text/plain");
        /*Applying information Subject and Body.*/
        intent.putExtra(android.content.Intent.EXTRA_SUBJECT, "User ID");
        intent.putExtra(android.content.Intent.EXTRA_TEXT, dbm.getUID());
        /*Fire!*/
        startActivity(Intent.createChooser(intent, "Share Using"));
    }
//
//
//
//
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if(resultCode == RESULT_OK) {
            if (requestCode == PICK_PHOTO_CODE) {
                if (intent != null&&intent.getData()!=null) {
                    try {
                        Uri uri = intent.getData();
                        profileImageBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),uri);
                        ivUserImage.setImageBitmap(profileImageBitmap);
                    } catch (IOException e) { e.printStackTrace(); }
                } else Toast.makeText(this, "Data is null", Toast.LENGTH_SHORT).show();
            }else Toast.makeText(this, "Request code not match", Toast.LENGTH_SHORT).show();
        }
    }
//
//
    public void updateProfile(View view) {
        pickImage();
    }

//    private void saveProfileImageToStorage(){
//
//        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
//        profileImageBitmap.compress(Bitmap. CompressFormat.PNG,  80, outputStream);
//        byte[] data = outputStream.toByteArray();
//
//        final StorageReference profileImagesRef  = dbm.getProfileImagesRef("profile"+dbm.getUID()+".png");
//
//        StorageMetadata metadata = new StorageMetadata.Builder()
//                .setCustomMetadata("caption", "Image Caption will apear here...")
//                .build();
//
//        UploadTask uploadTask = profileImagesRef.putBytes(data, metadata);
//        // show progress bar
//        // set button disable
//
//        uploadTask.addOnCompleteListener(SettingsActivity.this, new OnCompleteListener<UploadTask.TaskSnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
//                Toast.makeText(SettingsActivity.this, "Profile Uploaded", Toast.LENGTH_SHORT).show();
//                // hide progress bar
//                // set button enable
//
//            }
//        });
//
//        Task<Uri> downloadUriTask = uploadTask.continueWithTask(
//                new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
//                    @Override
//                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
//                        if (!task.isSuccessful())
//                            throw  task.getException();
//                        return profileImagesRef.getDownloadUrl();
//                    }
//                }
//        );
//        downloadUriTask.addOnCompleteListener(SettingsActivity.this, new OnCompleteListener<Uri>() {
//            @Override
//            public void onComplete(@NonNull Task<Uri> task) {
//                if (task.isSuccessful()){
//                    Uri downloadUri = task.getResult();
////                    etAbout.setText("Download URI: "+downloadUri);
//                    //user.setProfileUri(downloadUri+"");
//                    //updateProfile();
//
//                    //                    Toast.makeText(EditProfileActivity.this, "Download URI\n"+downloadUri, Toast.LENGTH_SHORT).show();
//                }else
//                    Toast.makeText(SettingsActivity.this, "Failed Update Profile", Toast.LENGTH_SHORT).show();
//
//                // hide progress bar
//                // set button enable
//            }
//        });
//
//



//        Uri file = Uri.fromFile(new File("path/to/images/rivers.jpg"));
//        StorageReference riversRef = storageRef.child("images/rivers.jpg");
//
//        profileImagesRef.putFile(profileImageUri)
//                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//                    @Override
//                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                        // Get a URL to the uploaded content
////                        Uri downloadUrl = taskSnapshot.getDownloadUrl();
//                        Toast.makeText(EditProfileActivity.this, "Image Uploaded", Toast.LENGTH_SHORT).show();
//                    }
//                })
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception exception) {
//                        Toast.makeText(EditProfileActivity.this, "Failed To Uploaded Image", Toast.LENGTH_SHORT).show();
//                        // Handle unsuccessful uploads
//                        // ...
//                    }
//                });


   // }



    private void loadProfileImage(){
        dbm.getProfileImagesRef().getBytes(Long.MAX_VALUE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                ivUserImage.setImageBitmap(bitmap);
                etName.setText(dbm.getUserName());
                // Use the bytes to display the image
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                //
                Toast.makeText(SettingsActivity.this, "Something went wrong...", Toast.LENGTH_SHORT).show();
                // Handle any errors
            }
        });
    }




}