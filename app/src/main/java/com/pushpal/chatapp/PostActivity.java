package com.pushpal.chatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.pushpal.chatapp.model.UserData;

import java.util.Calendar;

import static com.google.firebase.messaging.Constants.TAG;

public class PostActivity extends AppCompatActivity {

    ProgressDialog pd;

    EditText text1, text2;
    Spinner spinner1, spinner2;
    Button btnpost;

    FirebaseDatabase fdb;
    DatabaseReference db_ref;
    FirebaseAuth mAuth;

    Calendar cal;
    String uid;
    String Time, Date;


    ////////////ads
    private AdView adView;

    public static InterstitialAd mInterstitialAd;
    //////////////ads

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        pd = new ProgressDialog(this);
        pd.setMessage("Loading...");
        pd.setCancelable(true);
        pd.setCanceledOnTouchOutside(false);

        ///////////////////////ads

        // Initialize the Mobile Ads SDK.
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(@NonNull InitializationStatus initializationStatus) {
            }
        });


        adView = findViewById(R.id.adView3);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);


        // Interstitial Ad
        AdRequest adRequest1 = new AdRequest.Builder().build();

        InterstitialAd.load(this,"ca-app-pub-1549251657688500/5271567117", adRequest1,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        // The mInterstitialAd reference will be null until
                        // an ad is loaded.
                        mInterstitialAd = interstitialAd;
                        Log.i(TAG, "onAdLoaded");
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Handle the error
                        Log.i(TAG, loadAdError.getMessage());
                        mInterstitialAd = null;
                    }
                });


        /////////////ads

        getSupportActionBar().setTitle("Post Blood Request");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        text1 = findViewById(R.id.getMobile);
        text2 = findViewById(R.id.getLocation);

        spinner1 = findViewById(R.id.SpinnerBlood);
        spinner2 = findViewById(R.id.SpinnerDivision);

        btnpost = findViewById(R.id.postbtn);


        text1.addTextChangedListener(new TextWatcher() {
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
        text2.addTextChangedListener(new TextWatcher() {
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


        cal = Calendar.getInstance();

        int day = cal.get(Calendar.DAY_OF_MONTH);
        int month = cal.get(Calendar.MONTH);
        int year = cal.get(Calendar.YEAR);
        int hour = cal.get(Calendar.HOUR);
        int min = cal.get(Calendar.MINUTE);
        month += 1;
        Time = "";
        Date = "";
        String ampm = "AM";

        if (cal.get(Calendar.AM_PM) == Calendar.PM) {
            ampm = "PM";
        }

        if (hour < 10) {
            Time += "0";
        }
        Time += hour;
        Time += ":";

        if (min < 10) {
            Time += "0";
        }

        Time += min;
        Time += (" " + ampm);

        Date = day + "/" + month + "/" + year;

        FirebaseUser cur_user = FirebaseAuth.getInstance().getCurrentUser();

        if (cur_user == null) {
            startActivity(new Intent(PostActivity.this, LoginActivity.class));
        } else {
            uid = cur_user.getUid();
        }

        mAuth = FirebaseAuth.getInstance();
        fdb = FirebaseDatabase.getInstance();
        db_ref = fdb.getReference("posts");

        try {
            btnpost.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


//            mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback(){
//            @Override
//            public void onAdDismissedFullScreenContent() {
//                // Called when fullscreen content is dismissed.
//                Log.d("TAG", "The ad was dismissed.");
//            }
//
//            @Override
//            public void onAdFailedToShowFullScreenContent(AdError adError) {
//                // Called when fullscreen content failed to show.
//                Log.d("TAG", "The ad failed to show.");
//            }
//
//            @Override
//            public void onAdShowedFullScreenContent() {
//                // Called when fullscreen content is shown.
//                // Make sure to set your reference to null so you don't
//                // show it a second time.
//                mInterstitialAd = null;
//                Log.d("TAG", "The ad was shown.");
//            }
//        });



                    //////////////////////ads

//                    if (mInterstitialAd != null) {
//                        mInterstitialAd.show(PostActivity.this);
//                    } else {
//                        Log.d("TAG", "The interstitial ad wasn't ready yet.");
//                    }

                    btnpost.setEnabled(false);
                    btnpost.setTextColor(Color.argb(50, 255, 255, 255));
                    pd.show();
                    final Query findname = fdb.getReference("users").child(uid);

                    findname.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            if (dataSnapshot.exists()) {

                                db_ref.child(uid).child("imageURL").setValue(dataSnapshot.getValue(UserData.class).getImageURL());

                                db_ref.child(uid).child("Name").setValue(dataSnapshot.getValue(UserData.class).getName());
                                db_ref.child(uid).child("Contact").setValue(text1.getText().toString());
                                db_ref.child(uid).child("Address").setValue(text2.getText().toString());
                                db_ref.child(uid).child("Division").setValue(spinner2.getSelectedItem().toString());
                                db_ref.child(uid).child("BloodGroup").setValue(spinner1.getSelectedItem().toString());
                                db_ref.child(uid).child("Time").setValue(Time);
                                db_ref.child(uid).child("Date").setValue(Date);
                                Toast.makeText(PostActivity.this, "Your post has been created successfully",
                                        Toast.LENGTH_LONG).show();



                                startActivity(new Intent(PostActivity.this, Dashboard.class));


                            } else {
                                Toast.makeText(getApplicationContext(), "Database error occured.",
                                        Toast.LENGTH_LONG).show();
                            }
                            btnpost.setEnabled(true);
                            btnpost.setTextColor(Color.rgb(255, 255, 255));

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                            btnpost.setEnabled(true);
                            btnpost.setTextColor(Color.rgb(255, 255, 255));

                            Log.d("User", databaseError.getMessage());

                        }
                    });

                    pd.dismiss();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        pd.dismiss();

    }

    /**
     * Called when leaving the activity
     */
    @Override
    public void onPause() {
        if (adView != null) {
            adView.pause();
        }
        super.onPause();
    }

    /**
     * Called when returning to the activity
     */
    @Override
    public void onResume() {
        super.onResume();
        if (adView != null) {
            adView.resume();
        }

    }

    /**
     * Called before the activity is destroyed
     */
    @Override
    public void onDestroy() {
        if (adView != null) {
            adView.destroy();
        }
        super.onDestroy();
    }

    @Override
    public boolean onOptionsItemSelected (MenuItem item){
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }

            return super.onOptionsItemSelected(item);
        }

    private void checkInpute () {
            if (!TextUtils.isEmpty(text1.getText()) && text1.length() == 11) {
                if (!TextUtils.isEmpty(text2.getText())) {
                    btnpost.setEnabled(true);
                    btnpost.setTextColor(Color.rgb(255, 255, 255));
                } else {
                    btnpost.setEnabled(false);
                    btnpost.setTextColor(Color.argb(50, 255, 255, 255));
                }
            } else {
                btnpost.setEnabled(false);
                btnpost.setTextColor(Color.argb(50, 255, 255, 255));
            }

        }
}