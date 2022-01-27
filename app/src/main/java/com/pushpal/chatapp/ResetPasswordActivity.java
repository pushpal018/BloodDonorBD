package com.pushpal.chatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.transition.TransitionManager;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ResetPasswordActivity extends AppCompatActivity {

    private EditText forgotEmail;
    private Button resetBtn;

    private FirebaseAuth firebaseAuth;

    private ViewGroup emailIconContainer;
    private ImageView emailIcon;
    private TextView emailIconText;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);


        forgotEmail = findViewById(R.id.forget_password_Email);
        resetBtn = findViewById(R.id.reset_password_btn);
        TextView forgot_password_goback = findViewById(R.id.forgrt_password_goback);

        firebaseAuth = FirebaseAuth.getInstance();

        emailIconContainer = findViewById(R.id.forgot_email_icon_contant);
        emailIcon = findViewById(R.id.forgot_email_icon);
        emailIconText = findViewById(R.id.forgot_email_icon_text);
        progressBar = findViewById(R.id.forgot_email_icon_progressBar);


//        mAuth = FirebaseAuth.getInstance();
//        pd = new ProgressDialog(this);
//        pd.setMessage("Loading...");
//        pd.setCancelable(true);
//        pd.setCanceledOnTouchOutside(false);
//
//        useremail = findViewById(R.id.resetUsingEmail);
//
//        findViewById(R.id.resetPassbtn).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                FirebaseUser user = mAuth.getCurrentUser();
//
//                final String email = useremail.getText().toString();
//
//                if(TextUtils.isEmpty(email))
//                {
//                    useremail.setError("Email required!");
//                }
//                else
//                {
//                    pd.show();
//                    mAuth.sendPasswordResetEmail(email)
//                            .addOnCompleteListener(new OnCompleteListener<Void>() {
//                                @Override
//                                public void onComplete(@NonNull Task<Void> task) {
//                                    if(task.isSuccessful())
//                                    {
//                                        Toast.makeText(getApplicationContext(), "We have sent an email to "+" '"+ email +"'. Please check your email.", Toast.LENGTH_LONG)
//                                                .show();
//                                        startActivity(new Intent(getApplicationContext(), LoginActivity.class));
//                                        //useremail.setText(null);
//                                    }
//                                    else
//                                    {
//                                        Toast.makeText(getApplicationContext(), "Sorry, There is something went wrong. please try again some time later.", Toast.LENGTH_LONG)
//                                                .show();
//                                        useremail.setText(null);
//                                    }
//                                    pd.dismiss();
//                                }
//                            });
//                }
//            }
//        });
        forgotEmail.addTextChangedListener(new TextWatcher() {
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
        resetBtn.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View v) {
                TransitionManager.beginDelayedTransition(emailIconContainer);
                emailIconText.setVisibility(View.GONE);

                TransitionManager.beginDelayedTransition(emailIconContainer);
                emailIcon.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.VISIBLE);
                resetBtn.setEnabled(false);
                resetBtn.setTextColor(Color.argb(50,255,255,255));

                firebaseAuth.sendPasswordResetEmail(forgotEmail.getText().toString().trim())
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    ScaleAnimation scaleAnimation = new ScaleAnimation(1,0,1,0,emailIcon.getWidth()/2,emailIcon.getHeight()/2);
                                    scaleAnimation.setDuration(100);
                                    scaleAnimation.setInterpolator(new AccelerateInterpolator());
                                    scaleAnimation.setRepeatMode(Animation.REVERSE);
                                    scaleAnimation.setRepeatCount(1);

                                    scaleAnimation.setAnimationListener(new Animation.AnimationListener() {
                                        @Override
                                        public void onAnimationStart(Animation animation) {

                                        }

                                        @SuppressLint("SetTextI18n")
                                        @Override
                                        public void onAnimationEnd(Animation animation) {
                                            emailIconText.setText("Recovery email sent successfully ! check your inbox");
                                            emailIconText.setTextColor(getResources().getColor(R.color.greenSuccess));

                                            TransitionManager.beginDelayedTransition(emailIconContainer);
                                            emailIconText.setVisibility(View.VISIBLE);
                                        }

                                        @Override
                                        public void onAnimationRepeat(Animation animation) {
                                            emailIcon.setImageResource(R.drawable.green_email);
                                        }
                                    });

                                    emailIcon.startAnimation(scaleAnimation);
                                }else {
                                    String error=task.getException().getMessage();
                                    resetBtn.setEnabled(true);
                                    resetBtn.setTextColor(Color.rgb(255,255,255));
                                    TransitionManager.beginDelayedTransition(emailIconContainer);
                                    emailIconText.setText(error);
                                    emailIconText.setTextColor(getResources().getColor(R.color.colorPrimary));
                                    emailIconText.setVisibility(View.VISIBLE);

                                }
                                progressBar.setVisibility(View.GONE);

                            }
                        });
            }
        });
        forgot_password_goback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               Intent intent = new Intent(ResetPasswordActivity.this,LoginActivity.class);
               startActivity(intent);
            }
        });
    }
    private void checkInpute(){
        if(TextUtils.isEmpty(forgotEmail.getText())){
            resetBtn.setEnabled(false);
            resetBtn.setTextColor(Color.argb(50,255,255,255));
        }else {
            resetBtn.setEnabled(true);
            resetBtn.setTextColor(Color.rgb(255,255,255));
        }
    }
}
