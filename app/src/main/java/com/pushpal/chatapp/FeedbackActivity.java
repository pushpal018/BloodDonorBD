package com.pushpal.chatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;

public class FeedbackActivity extends AppCompatActivity implements View.OnClickListener{


    private EditText email,feedback ;
    private Button send;
    private Button clear;

    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

    private ProgressDialog pd;

    ////////////ads
    private AdView adView;

    //////////////ads

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        //action bar set
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setTitle("Feedback");

        //backprase
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);


        pd = new ProgressDialog(this);
        pd.setMessage("Loading...");
        pd.setCancelable(true);
        pd.setCanceledOnTouchOutside(false);

        email = findViewById(R.id.name);
        feedback = findViewById(R.id.feedback_et);
        send  = findViewById(R.id.send_btn);
        clear = findViewById(R.id.clear_btn);


        //////////////////ads
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(@NonNull InitializationStatus initializationStatus) {
            }
        });


        adView = findViewById(R.id.adView2);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);


        //////////////////ads


        send.setOnClickListener(this);
        clear.setOnClickListener(this);

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
        feedback.addTextChangedListener(new TextWatcher() {
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


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            NotificationChannel notificationChannel = new NotificationChannel("mynotification", "myNotification", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager manager = getSystemService(NotificationManager.class);

            manager.createNotificationChannel(notificationChannel);
        }


        FirebaseMessaging.getInstance().subscribeToTopic("general")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        String msg = "";
                        if (!task.isSuccessful()) {
                            msg = "failed";
                        }



                    }
                });
    }

    @Override
    public void onClick(View v) {

        try{

            String name =email.getText().toString();
            String feedbacks =feedback.getText().toString();



            if(v.getId()==R.id.send_btn){

                pd.show();

                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("text/email");
                i.putExtra(Intent.EXTRA_EMAIL, new String[]{"pushpal.aust@gmail.com"});
                i.putExtra(Intent.EXTRA_SUBJECT, "Feedback from Blood Donor App");
                i.putExtra(Intent.EXTRA_TEXT, "Name : "+name+"\nMessage : "+feedbacks);
                try {
                    startActivity(Intent.createChooser(i, "Send feedback..."));

                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(getApplicationContext(), "There are no email clients installed.", Toast.LENGTH_SHORT).show();
                }



            }if(v.getId()==R.id.clear_btn){


                email.setText("");
                feedback.setText("");

            }
            pd.dismiss();
        }catch (Exception e){

            pd.dismiss();
            String error = e.getMessage();
            Toast.makeText(FeedbackActivity.this,error,Toast.LENGTH_SHORT).show();

        }
    }
    @Override
    public void onPause() {
        if (adView != null) {
            adView.pause();
        }
        super.onPause();
    }
    @Override
    public void onResume() {
        super.onResume();
        if (adView != null) {
            adView.resume();
        }

    }
 @Override
    public void onDestroy() {
        if (adView != null) {
            adView.destroy();
        }
        super.onDestroy();
    }

    private void checkInpute() {
        if(!TextUtils.isEmpty(email.getText())) {
            if(!TextUtils.isEmpty(feedback.getText())){
                send.setEnabled(true);
                clear.setEnabled(true);
                send.setTextColor(Color.rgb(255,255,255));
                clear.setTextColor(Color.rgb(255,255,255));
            }else {
                send.setEnabled(false);
                clear.setEnabled(false);
                send.setTextColor(Color.argb(50,255,255,255));
                clear.setTextColor(Color.argb(50,255,255,255));
            }
        }else {
            send.setEnabled(false);
            clear.setEnabled(false);
            send.setTextColor(Color.argb(50,255,255,255));
            clear.setTextColor(Color.argb(50,255,255,255));
        }

    }

    public boolean onSupportNavigateUp() {

        onBackPressed();//go to privious activity on back pressed from actionbar too

        return super.onSupportNavigateUp();
    }
}