package com.example.notespro;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {
    EditText emailET,passwordET;
    Button loginBtn;
    TextView createAccountTV;
    ProgressBar progressBar;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailET = (EditText) findViewById(R.id.emailET);
        passwordET = (EditText) findViewById(R.id.passwordET);
        createAccountTV = (TextView) findViewById(R.id.createAccountTV);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        loginBtn = (Button) findViewById(R.id.loginBtn);

        loginBtn.setOnClickListener(view -> loginAccount());

        createAccountTV.setOnClickListener(view -> startActivity(new Intent(LoginActivity.this,CreateAccountActivity.class)));
    }

    void  loginAccount() {
        String email = emailET.getText().toString();
        String password = passwordET.getText().toString();

        boolean isValidated = validateData(email,password);
        if(isValidated) {
            loginAccountInFirebase(email, password);
        }
    }

    void loginAccountInFirebase(String email, String password) {
        changeInProgress(true);
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                changeInProgress(false);
                if(task.isSuccessful()){
                    if(firebaseAuth.getCurrentUser().isEmailVerified()){
                        startActivity(new Intent(LoginActivity.this,MainActivity.class));
                        finish();
                    }else{
                        Utility.showToast(LoginActivity.this,"Email not verified,Please verify your email");
                    }
                }else{
                    Utility.showToast(LoginActivity.this,task.getException().getLocalizedMessage());
                }
            }
        });
    }

    void changeInProgress(Boolean inProgress){
        if(inProgress){
            loginBtn.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
        }else{
            loginBtn.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
        }
    }

    boolean validateData(String email,String password){
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            emailET.setError("Email is invalid");
            return false;
        }
        if(password.length()<6){
            passwordET.setError("Password length is invalid");
            return false;
        }

        return true;
    }
}