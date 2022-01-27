package com.pushpal.chatapp;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.pushpal.chatapp.fragment.AboutUs;
import com.pushpal.chatapp.fragment.AchievmentsView;
import com.pushpal.chatapp.fragment.BloodInfo;
import com.pushpal.chatapp.fragment.HomeView;
import com.pushpal.chatapp.fragment.NearByHospitalActivity;
import com.pushpal.chatapp.fragment.SearchDonorFragment;
import com.pushpal.chatapp.model.UserData;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.pushpal.chatapp.PostActivity.mInterstitialAd;

public class Dashboard extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private String appPackageName ="";
    private  String strAppLink = "";

    private FirebaseAuth mAuth;
    private TextView getUserName;
    private TextView getUserEmail;
    private CircleImageView profile_image;
    private FirebaseDatabase user_db;
    private FirebaseUser cur_user;
    private DatabaseReference userdb_ref;

    public static FloatingActionButton addPostBtn;

    public static DrawerLayout drawer;

    private ProgressDialog pd;

    @RequiresApi(api = Build.VERSION_CODES.CUPCAKE)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        pd = new ProgressDialog(this);
        pd.setMessage("Loading...");
        pd.setCancelable(true);
        pd.setCanceledOnTouchOutside(false);

        mAuth = FirebaseAuth.getInstance();
        user_db = FirebaseDatabase.getInstance();
        cur_user = mAuth.getCurrentUser();
        userdb_ref = user_db.getReference("users");

        getUserEmail = findViewById(R.id.UserEmailView);
        getUserName = findViewById(R.id.UserNameView);
        profile_image = findViewById(R.id.profile_image);
        addPostBtn = findViewById(R.id.fab);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View header = navigationView.getHeaderView(0);

        getUserEmail = (TextView) header.findViewById(R.id.UserEmailView);
        getUserName = (TextView) header.findViewById(R.id.UserNameView);
        profile_image = header.findViewById(R.id.profile_image);

        Query singleuser = userdb_ref.child(cur_user.getUid());
        pd.show();
        singleuser.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //pd.show();
                UserData userData = dataSnapshot.getValue(UserData.class);

                assert userData != null;
                String name = userData.getName();

                getUserName.setText(name);
                getUserEmail.setText(cur_user.getEmail());

                //////show profile image
                if (userData.getImageURL().equals("default")){
                    profile_image.setImageResource(R.drawable.user);
                } else {
                    Glide.with(Dashboard.this).load(userData.getImageURL()).into(profile_image);
                }
                //////show profile image

                pd.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("User", databaseError.getMessage());

            }
        });


        if(savedInstanceState == null)
        {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragmentcontainer, new HomeView()).commit();
            navigationView.getMenu().getItem(0).setChecked(true);

        }

        addPostBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent postIntent = new Intent(Dashboard.this,PostActivity.class);
                startActivity(postIntent);
            }
        });


        if (mInterstitialAd != null) {
            mInterstitialAd.show(Dashboard.this);
        } else {
            Log.d("TAG", "The interstitial ad wasn't ready yet.");
        }
    }




    @Override
    public void onBackPressed() {

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {

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
                    Toast.makeText(Dashboard.this, "You are clicked on cancel", Toast.LENGTH_SHORT).show();
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.dashboard, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.donateinfo) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragmentcontainer, new BloodInfo()).commit();
        }
        if (id == R.id.devinfo) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragmentcontainer, new AboutUs()).commit();
        }
        if (id == R.id.feedback){
            Intent intent = new Intent(this,FeedbackActivity.class);
            startActivity(intent);
        }
        if(item.getItemId()==R.id.rate){

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
        if(item.getItemId()==R.id.share){
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");

            try
            {
                strAppLink = "https://play.google.com/store/apps/details?id=" + appPackageName;
            }
            catch (android.content.ActivityNotFoundException anfe)
            {
                strAppLink = "https://play.google.com/store/apps/details?id=" + appPackageName;
            }

            String subject = "Blood Donor BD";
            String body = "Hey!Download the Blood Donor App Today.Schedule blood donations quickly and easily right from the palm of your hand." +
                    "\n"+""+strAppLink;

            intent.putExtra(Intent.EXTRA_SUBJECT,subject);
            intent.putExtra(Intent.EXTRA_TEXT,body);

            startActivity(Intent.createChooser(intent,"share with"));
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.home) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragmentcontainer, new HomeView()).commit();

        } else if (id == R.id.userprofile) {
            Intent profileIntent = new Intent(Dashboard.this,ProfileActivity.class);

            onEnterAnimationComplete();

            startActivity(profileIntent);

        }
        else if (id == R.id.user_achiev) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragmentcontainer, new AchievmentsView()).commit();

        }
        else if (id == R.id.logout) {
            mAuth.signOut();
            Intent intent = new Intent(Dashboard.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
        else if (id == R.id.blood_storage){

            getSupportFragmentManager().beginTransaction().replace(R.id.fragmentcontainer, new SearchDonorFragment()).commit();

        } else if (id == R.id.nearby_hospital) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragmentcontainer, new NearByHospitalActivity()).commit();

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser == null)
        {
            Intent intent = new Intent(Dashboard.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser == null)
        {
            Intent intent = new Intent(Dashboard.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
    }

}
