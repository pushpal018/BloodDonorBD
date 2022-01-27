package com.pushpal.chatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;


import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {


    private String appPackageName ="";
    private  String strAppLink = "";

    private EditText email,password;
    private Button signInBtn;
    private ProgressBar progressBar;
    private FirebaseAuth firebaseAuth;


    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        TextView dontHaveAnAccount = findViewById(R.id.tv_donot_have_account);


        TextView forgotPassword = findViewById(R.id.sign_in_forgot_password);
        email = findViewById(R.id.name);
        password = findViewById(R.id.feedback_et);
        signInBtn = findViewById(R.id.sign_in_btn);
        progressBar = findViewById(R.id.sign_In_progressBar);
        firebaseAuth = FirebaseAuth.getInstance();


        dontHaveAnAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this,ProfileActivity.class);
                onEnterAnimationComplete();
                startActivity(intent);
            }
        });
        email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkInpute();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkInpute();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        signInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkEmailAndPassword();
            }
        });
        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ResetPasswordActivity.class);
                startActivity(intent);
            }
        });


    }


    private void checkInpute() {
        if(!TextUtils.isEmpty(email.getText())) {
            if(!TextUtils.isEmpty(password.getText())){
                signInBtn.setEnabled(true);
                signInBtn.setTextColor(Color.rgb(255,255,255));
            }else {
                signInBtn.setEnabled(false);
                signInBtn.setTextColor(Color.argb(50,255,255,255));
            }
        }else {
            signInBtn.setEnabled(false);
            signInBtn.setTextColor(Color.argb(50,255,255,255));
        }

    }
    private void checkEmailAndPassword(){
        if(email.getText().toString().matches(emailPattern)){
            if(password.length()>=5){
                progressBar.setVisibility(View.VISIBLE);
                signInBtn.setEnabled(false);
                signInBtn.setTextColor(Color.argb(50,255,255,255));

                firebaseAuth.signInWithEmailAndPassword(email.getText().toString(),password.getText().toString())
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful()){
                                    Intent intent = new Intent(LoginActivity.this, Dashboard.class);
                                    startActivity(intent);
                                    finish();
                                }else {
                                    progressBar.setVisibility(View.INVISIBLE);
                                    signInBtn.setEnabled(true);
                                    signInBtn.setTextColor(Color.rgb(255,255,255));
                                    String error = Objects.requireNonNull(task.getException()).getMessage();
                                    Toast.makeText(LoginActivity.this, error, Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }else {
                Toast.makeText(LoginActivity.this,"Incorrect Email Or Password !", Toast.LENGTH_SHORT).show();
            }
        }else {
            Toast.makeText(LoginActivity.this,"Incorrect Email Or Password !", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("Confirm exit!!");
        alertDialogBuilder.setIcon(R.drawable.exit);
        alertDialogBuilder.setMessage("Do you want to exit?");
        alertDialogBuilder.setCancelable(false);
        alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });

        alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(LoginActivity.this, "You are clicked on cancel", Toast.LENGTH_SHORT).show();
            }
        });

        alertDialogBuilder.setNeutralButton("Rate this app", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                appPackageName = getApplicationContext().getPackageName();

                try
                {
                    strAppLink = "https://play.google.com/store/apps/details?id=" + appPackageName;
                }
                catch (android.content.ActivityNotFoundException anfe)
                {
                    strAppLink = "https://play.google.com/store/apps/details?id=" + appPackageName;
                }

                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(strAppLink));
                startActivity(intent);
            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();

    }


}