package com.zah.weatherplus;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Login extends AppCompatActivity {

    EditText emailTextBox, passwordTextBox;
    Button loginButton;
    ProgressBar progressbarLoginAccount;
    TextView createNewAccountViewButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailTextBox = findViewById(R.id.email_text_box);
        passwordTextBox = findViewById(R.id.password_text_box);
        loginButton = findViewById(R.id.login_button);
        progressbarLoginAccount = findViewById(R.id.progressbar_login_account);
        createNewAccountViewButton = findViewById(R.id.create_new_account_view_button);

        loginButton.setOnClickListener((v) -> loginUser());
        createNewAccountViewButton.setOnClickListener((v) -> startActivity(new Intent(Login.this, CreateAccount.class)));
    }


    void loginUser() {
        String email = emailTextBox.getText().toString();
        String password = passwordTextBox.getText().toString();
        boolean isValidated = validateData(email, password);
        if (!isValidated) {
            return;
        }

        loginAccountInFirebase(email, password);
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        changeInProgress(true);
        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                changeInProgress(false);
                if (task.isSuccessful()) {
                    //login is success
                    if (firebaseAuth.getCurrentUser().isEmailVerified()) {
                        //go to main activity
                        startActivity(new Intent(Login.this, MainActivity.class));
                        finish();
                    } else {
                        Tools.showToast(Login.this, "Email not verified, Please verify it");
                    }

                } else {
                    //login failed
                    Tools.showToast(Login.this, task.getException().getLocalizedMessage());
                }
            }
        });

    }

    void loginAccountInFirebase(String email, String password) {

    }

    void changeInProgress(boolean inProgress) {
        if (inProgress) {
            progressbarLoginAccount.setVisibility(View.VISIBLE);
            createNewAccountViewButton.setVisibility(View.GONE);
        } else {
            progressbarLoginAccount.setVisibility(View.GONE);
            createNewAccountViewButton.setVisibility(View.VISIBLE);
        }
    }

    boolean validateData(String email, String password) {
        //validate the data of user
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailTextBox.setError("Email is invalid");
            return false;
        }
        if (password.length() < 6) {
            passwordTextBox.setError("Password length is invalid");
            return false;
        }
        return true;
    }
}