package com.example.notespro;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class CreateAccountActivity extends AppCompatActivity {
    EditText emailET,passwordET,confirmPasswordET;
    Button createAccountBtn;
    TextView loginTV;
    ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        emailET = (EditText) findViewById(R.id.emailET);
        passwordET = (EditText) findViewById(R.id.passwordET);
        confirmPasswordET = (EditText) findViewById(R.id.confirmPasswordET);
        loginTV = (TextView) findViewById(R.id.loginTV);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        createAccountBtn = (Button) findViewById(R.id.createAccountBtn);

        createAccountBtn.setOnClickListener(v->createAccount());

        loginTV.setOnClickListener(v-> finish());
    }

    void createAccount() {
        String email = emailET.getText().toString();
        String password = passwordET.getText().toString();
        String confirmPassword = confirmPasswordET.getText().toString();

        boolean isValidated = validateData(email,password,confirmPassword);
        if(isValidated) {
            createAccountInFirebase(email, password);
        }
    }

     void createAccountInFirebase(String email, String password) {
        changeInProgress(true);

         FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
         firebaseAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(CreateAccountActivity.this,
                 new OnCompleteListener<AuthResult>() {
                     @Override
                     public void onComplete(@NonNull Task<AuthResult> task) {
                         changeInProgress(false);
                         if(task.isSuccessful()){
                             Toast.makeText(CreateAccountActivity.this,"Account successfully created,Please check Email to verify.",Toast.LENGTH_SHORT).show();
                             firebaseAuth.getCurrentUser().sendEmailVerification();
                             firebaseAuth.signOut();
                             finish();
                         }else{
                             Toast.makeText(CreateAccountActivity.this,task.getException().getLocalizedMessage(),Toast.LENGTH_SHORT).show();
                         }
                     }
                 });
    }
    void changeInProgress(Boolean inProgress){
        if(inProgress){
            createAccountBtn.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
        }else{
            createAccountBtn.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
        }
    }

    boolean validateData(String email,String password,String confirmPassword){
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            emailET.setError("Email is invalid");
            return false;
        }
        if(password.length()<6){
            passwordET.setError("Password length is invalid");
            return false;
        }
        if(!confirmPassword.matches(password)){
            confirmPasswordET.setError("Password not matched");
        }
        return true;
    }
}