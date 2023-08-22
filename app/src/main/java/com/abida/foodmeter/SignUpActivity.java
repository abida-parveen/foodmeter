package com.abida.foodmeter;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

public class SignUpActivity extends AppCompatActivity {

    EditText editTextPassword, editTextConfirmPassword, editTextName, editTextEmail;
    Button btnRegister;

    String email="";
    String pass="";
    String name="";
    private DBManager mDatabase;

///////////////////////////////////

    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        Objects.requireNonNull(getSupportActionBar()).hide();

        mAuth = FirebaseAuth.getInstance();

        editTextName = findViewById(R.id.editText_username);
        editTextEmail = findViewById(R.id.editText_email);
        editTextPassword = findViewById(R.id.editText_password);
        editTextConfirmPassword = findViewById(R.id.editText_confirmPassword);
        mDatabase = new DBManager(getApplicationContext());

        btnRegister = findViewById(R.id.button_register);

//        if (sp.isUserLoggedIn()){
//                startActivity(new Intent(SignUpActivity.this, HomeActivity.class));
//                finish();
//        }else

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name = editTextName.getText().toString();
                email = editTextEmail.getText().toString();
                pass = editTextPassword.getText().toString();
                String confirmPass = editTextConfirmPassword.getText().toString();

                if (name.equals("")) {
                    editTextName.setError("Please enter Your Name");
                    editTextName.requestFocus();
                } else if (email.equals("")) {
                    editTextEmail.setError("Please enter Email");
                    editTextEmail.requestFocus();
                } else if (pass.equals("")) {
                    editTextPassword.setError("Please enter Password");
                    editTextPassword.requestFocus();
                } else if (confirmPass.equals("")) {
                    editTextConfirmPassword.setError("Please Confirm Password");
                    editTextConfirmPassword.requestFocus();
                } else if (!confirmPass.equals(pass)) {
                    Snackbar.make(v,"Password does't match!", Snackbar.LENGTH_LONG).show();
                } else {
                    signUp(name, email, pass);
                }
            }
        });

    }


    private void signUp(String name, String email, String pass){
        showToast("Register...");
        createUserWithEmailAndPassword(email, pass);
    }

    public void loginClicked(View view) {
        startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
        finish();
    }


    private void showToast(String message){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }


    private void createUserWithEmailAndPassword(final String email, final String password){
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Toast.makeText(getApplicationContext(),"Created Successfully", Toast.LENGTH_LONG).show();
                    FirebaseUser user = mAuth.getCurrentUser();
                    updateUI(user);
                }
                else if (!task.isSuccessful())
                {
                    try {
                        throw task.getException();
                    }
                    // if user enters wrong email.
                    catch (FirebaseAuthWeakPasswordException weakPassword)
                    {
                        Toast.makeText(getApplicationContext(), "weak_password",
                                Toast.LENGTH_SHORT).show();
                    }
                    // if user enters wrong password.
                    catch (FirebaseAuthInvalidCredentialsException malformedEmail)
                    {
                        Toast.makeText(getApplicationContext(), "malformed_email",
                                Toast.LENGTH_SHORT).show();
                    }
                    catch (FirebaseAuthUserCollisionException existEmail)
                    {
                        Toast.makeText(getApplicationContext(), "exist_email",
                                Toast.LENGTH_SHORT).show();
                    }
                    catch (Exception e)
                    {
                        Toast.makeText(getApplicationContext(), ""+e.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }


    private void updateUI(FirebaseUser user) {

        if ( user != null ){
            String personName = name;
            String personEmail = user.getEmail();
            String uid = user.getUid();
            Toast.makeText(getApplicationContext(),personName+"\n"+personEmail
                    + uid,Toast.LENGTH_LONG).show();
            mDatabase.createUserSnapshot(personName,personEmail,uid);
        }
        else {
            Toast.makeText(getApplicationContext(),"Failure",Toast.LENGTH_LONG).show();
        }
    }

}