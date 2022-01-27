package com.pushpal.chatapp.fragment;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;

import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.pushpal.chatapp.Dashboard;
import com.pushpal.chatapp.R;
import com.pushpal.chatapp.adapter.BloodRequestAdapter;
import com.pushpal.chatapp.model.CustomUserData;

import java.util.ArrayList;
import java.util.List;

public class HomeView extends Fragment {

    private RecyclerView recentPosts;

    public  SwipeRefreshLayout swipeRefreshLayout;
    private ImageView noInternetConnection;
    private Button retryBtn;

    private ConnectivityManager connectivityManager;
    private NetworkInfo networkInfo;

    private DatabaseReference donor_ref;
    FirebaseAuth mAuth;
    private BloodRequestAdapter restAdapter;
    private List<CustomUserData> postLists;
    private ProgressDialog pd;

    private ConstraintLayout homeView;


    public HomeView() {

    }

    @SuppressLint("WrongConstant")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home_view, container, false);
        recentPosts = (RecyclerView) view.findViewById(R.id.recyleposts);
        recentPosts.setLayoutManager(new LinearLayoutManager(getContext()));

        swipeRefreshLayout = view.findViewById(R.id.refresh_layout);
        noInternetConnection = view.findViewById(R.id.no_internet_connection);
        retryBtn = view.findViewById(R.id.retry_btn);
        homeView = view.findViewById(R.id.home_view);
        donor_ref = FirebaseDatabase.getInstance().getReference();
        postLists = new ArrayList<>();

        pd = new ProgressDialog(getActivity());
        pd.setMessage("Loading...");
        pd.setCancelable(true);
        pd.setCanceledOnTouchOutside(false);

        mAuth = FirebaseAuth.getInstance();
        getActivity().setTitle("Blood Point");

        swipeRefreshLayout.setColorSchemeColors(getContext().getResources().getColor(R.color.colorPrimary),getContext().getResources().getColor(R.color.colorPrimary),getContext().getResources().getColor(R.color.colorPrimary));

        //recentPosts.setAdapter(restAdapter);
        connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        networkInfo = connectivityManager.getActiveNetworkInfo();
        if(networkInfo != null && networkInfo.isConnected() == true) {
            Dashboard.drawer.setDrawerLockMode(0);
            noInternetConnection.setVisibility(View.GONE);
            retryBtn.setVisibility(View.GONE);
            recentPosts.setVisibility(View.VISIBLE);
            Dashboard.addPostBtn.setVisibility(View.VISIBLE);
            homeView.setBackgroundColor(Color.parseColor("#E74C3C"));
            restAdapter = new BloodRequestAdapter(postLists);
            RecyclerView.LayoutManager pmLayout = new LinearLayoutManager(getContext());
            recentPosts.setLayoutManager(pmLayout);
            recentPosts.setItemAnimator(new DefaultItemAnimator());
            recentPosts.addItemDecoration(new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL));
            recentPosts.setAdapter(restAdapter);

            AddPosts();
        }else {
            Dashboard.drawer.setDrawerLockMode(1);
            Glide.with(this).load(R.drawable.no_internet_connection).into(noInternetConnection);
            noInternetConnection.setVisibility(View.VISIBLE);
            retryBtn.setVisibility(View.VISIBLE);
            recentPosts.setVisibility(View.GONE);
            homeView.setBackgroundColor(Color.parseColor("#FFFFFF"));
            Dashboard.addPostBtn.setVisibility(View.GONE);
        }
        pd.dismiss();

        ///////////////////////

        ///////////refresh layout
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
                reloadPage();
            }
        });
        ///////////refresh layout

        retryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reloadPage();
            }
        });
        return view;
    }
    private void AddPosts()
    {
        Query allposts = donor_ref.child("posts");
        pd.show();
        allposts.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists()) {

                    for (DataSnapshot singlepost : dataSnapshot.getChildren()) {
                        CustomUserData customUserData = singlepost.getValue(CustomUserData.class);
                        postLists.add(customUserData);
                        restAdapter.notifyDataSetChanged();
                    }
                    pd.dismiss();
                }
                else
                {
                    Toast.makeText(getActivity(), "Database is empty now!",
                            Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                Log.d("User", databaseError.getMessage());

            }
        });

    }

    @SuppressLint("WrongConstant")
    private void reloadPage(){
        networkInfo = connectivityManager.getActiveNetworkInfo();
        postLists.clear();
        if(networkInfo != null && networkInfo.isConnected() == true) {
            Dashboard.drawer.setDrawerLockMode(0);
            noInternetConnection.setVisibility(View.GONE);
            retryBtn.setVisibility(View.GONE);
            recentPosts.setVisibility(View.VISIBLE);

            homeView.setBackgroundColor(Color.parseColor("#E74C3C"));

            Dashboard.addPostBtn.setVisibility(View.VISIBLE);

            restAdapter = new BloodRequestAdapter(postLists);
            RecyclerView.LayoutManager pmLayout = new LinearLayoutManager(getContext());
            recentPosts.setLayoutManager(pmLayout);
            recentPosts.setItemAnimator(new DefaultItemAnimator());
            recentPosts.addItemDecoration(new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL));
            recentPosts.setAdapter(restAdapter);

            AddPosts();


            //loadCategorues(catagoryRecyclerview,getContext());

            //loadedCategoriesName.add("HOME");
            //lists.add(new ArrayList<HomeViewModel>());
            //loadFragmentData(homePageRecyclerView,getContext(),0,"Home");

        }else {
            Dashboard.drawer.setDrawerLockMode(1);
            Toast.makeText(getContext(), "No Internet Connection!", Toast.LENGTH_SHORT).show();
            recentPosts.setVisibility(View.GONE);

            homeView.setBackgroundColor(Color.parseColor("#FFFFFF"));

            Glide.with(getContext()).load(R.drawable.no_internet_connection).into(noInternetConnection);
            noInternetConnection.setVisibility(View.VISIBLE);
            retryBtn.setVisibility(View.VISIBLE);

            Dashboard.addPostBtn.setVisibility(View.GONE);
        }
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
