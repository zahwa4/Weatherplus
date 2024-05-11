package com.zah.weatherplus;

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

public class CreateAccount extends AppCompatActivity {

    EditText emailTextBox, passwordTextBox, confirmPasswordTextBox;
    Button createNewAccountButton;
    ProgressBar progressbarNewAccount;
    TextView loginViewButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);
        emailTextBox = findViewById(R.id.email_text_box);
        passwordTextBox = findViewById(R.id.password_text_box);
        confirmPasswordTextBox = findViewById(R.id.confirm_password_text_box);
        createNewAccountButton = findViewById(R.id.create_new_account_button);
        progressbarNewAccount = findViewById(R.id.progressbar_new_account);
        loginViewButton = findViewById(R.id.login_view_button);

        createNewAccountButton.setOnClickListener(v -> CreateNewAccountForm());
        loginViewButton.setOnClickListener(v -> finish());

    }

    void CreateNewAccountForm() {
        String email = emailTextBox.getText().toString();
        String password = passwordTextBox.getText().toString();
        String confirmpassword = confirmPasswordTextBox.getText().toString();
        boolean isValidated = validateData(email, password, confirmpassword);
        if (!isValidated) {
            return;
        }

        createAccountInFirebase(email, password);

    }


    void createAccountInFirebase(String email, String password) {
        changeInProgress(true);

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(CreateAccount.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                changeInProgress(false);
                if (task.isSuccessful()) {
                    //create account is done
                    Tools.showToast(CreateAccount.this, "Successful create account Check email to verify");
                    firebaseAuth.getCurrentUser().sendEmailVerification();
                    firebaseAuth.signOut();
                    finish();

                } else {
                    //failure
                    Tools.showToast(CreateAccount.this, task.getException().getLocalizedMessage());
                }

            }
        });
    }

    void changeInProgress(boolean inProgress) {
        if (inProgress) {
            progressbarNewAccount.setVisibility(View.VISIBLE);
            createNewAccountButton.setVisibility(View.GONE);
        } else {
            progressbarNewAccount.setVisibility(View.GONE);
            createNewAccountButton.setVisibility(View.VISIBLE);
        }
    }

    boolean validateData(String email, String password, String confirmpassword) {
        //validate the data of user
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailTextBox.setError("Email is invalid");
            return false;
        }
        if (password.length() < 6) {
            passwordTextBox.setError("Password length is invalid");
            return false;
        }
        if (!password.equals(confirmpassword)) {
            confirmPasswordTextBox.setError("Password no matched");
            return false;
        }
        return true;
    }
}