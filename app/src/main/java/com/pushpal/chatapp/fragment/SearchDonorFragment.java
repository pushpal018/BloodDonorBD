package com.pushpal.chatapp.fragment;

import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

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
import com.pushpal.chatapp.Dashboard;
import com.pushpal.chatapp.R;
import com.pushpal.chatapp.adapter.SearchDonorAdapter;
import com.pushpal.chatapp.model.DonorData;

import java.util.ArrayList;
import java.util.List;

import static com.google.firebase.messaging.Constants.TAG;

public class SearchDonorFragment extends Fragment {

    private View view;

    FirebaseAuth mAuth;
    FirebaseUser fuser;
    FirebaseDatabase fdb;
    DatabaseReference db_ref;

    Spinner bloodgroup, division;
    Button btnsearch;
    private TextView TvNoDonor;
    ProgressDialog pd;
    List<DonorData> donorItem;
    private RecyclerView recyclerView;

    private SearchDonorAdapter sdadapter;

    InterstitialAd mInterstitialAd;
    //////////////ads

    public SearchDonorFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_search_donor, container, false);

        pd = new ProgressDialog(getActivity());
        pd.setMessage("Loading...");
        pd.setCancelable(true);
        pd.setCanceledOnTouchOutside(false);


        Dashboard.addPostBtn.setVisibility(View.GONE);


        mAuth = FirebaseAuth.getInstance();
        fuser = mAuth.getCurrentUser();
        fdb = FirebaseDatabase.getInstance();
        db_ref = fdb.getReference("donors");

        bloodgroup = view.findViewById(R.id.btngetBloodGroup);
        division = view.findViewById(R.id.btngetDivison);
        btnsearch = view.findViewById(R.id.btnSearch);

        TvNoDonor = view.findViewById(R.id.TvNoDobar);



        ///////////////////////ads

        // Initialize the Mobile Ads SDK.
        MobileAds.initialize(getActivity(), new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(@NonNull InitializationStatus initializationStatus) {
            }
        });


        ////////////ads
        AdView adView = view.findViewById(R.id.adView6);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);


        // Interstitial Ad
        AdRequest adRequest1 = new AdRequest.Builder().build();

        InterstitialAd.load(getActivity(),"ca-app-pub-1549251657688500/5271567117", adRequest1,
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




        getActivity().setTitle("Find Blood Donor");

        btnsearch.setEnabled(true);

        btnsearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mInterstitialAd != null) {
                        mInterstitialAd.show(getActivity());
                    } else {
                        Log.d("TAG", "The interstitial ad wasn't ready yet.");
                    }


                pd.show();
                btnsearch.setEnabled(false);
                donorItem = new ArrayList<>();
                donorItem.clear();

                sdadapter = new SearchDonorAdapter(donorItem);
                recyclerView = (RecyclerView) view.findViewById(R.id.showDonorList);
                recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                RecyclerView.LayoutManager searchdonor = new LinearLayoutManager(getContext());
                recyclerView.setLayoutManager(searchdonor);
                recyclerView.setItemAnimator(new DefaultItemAnimator());
                recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL));
                recyclerView.setAdapter(sdadapter);
                Query qpath  = db_ref.child(division.getSelectedItem().toString())
                        .child(bloodgroup.getSelectedItem().toString());
                qpath.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists())
                        {
                            TvNoDonor.setVisibility(View.GONE);
                            recyclerView.setVisibility(View.VISIBLE);
                            for(DataSnapshot singleitem : dataSnapshot.getChildren())
                            {

                                DonorData donorData = singleitem.getValue(DonorData.class);
                                donorItem.add(donorData);
                                sdadapter.notifyDataSetChanged();
                            }
                        }
                        else
                        {
                            recyclerView.setVisibility(View.GONE);
                            TvNoDonor.setVisibility(View.VISIBLE);
                        }
                        btnsearch.setEnabled(true);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.d("User", databaseError.getMessage());
                        btnsearch.setEnabled(true);
                    }
                });
                pd.dismiss();
            }
        });

        return view;
    }

}
