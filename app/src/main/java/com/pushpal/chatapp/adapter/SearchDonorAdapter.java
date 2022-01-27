package com.pushpal.chatapp.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.pushpal.chatapp.R;
import com.pushpal.chatapp.model.DonorData;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class SearchDonorAdapter extends RecyclerView.Adapter<SearchDonorAdapter.PostHolder> {


    private final List<DonorData> postLists;
    Context context;

    public class PostHolder extends RecyclerView.ViewHolder
    {

        TextView Name, Address, contact, posted, totaldonate;
        CircleImageView profileImage;
        public PostHolder(@NonNull View itemView) {
            super(itemView);

            context = itemView.getContext();
            profileImage = itemView.findViewById(R.id.profile_image);
            Name = itemView.findViewById(R.id.donorName);
            contact = itemView.findViewById(R.id.donorContact);
            totaldonate = itemView.findViewById(R.id.totaldonate);
            Address = itemView.findViewById(R.id.donorAddress);
            posted = itemView.findViewById(R.id.lastdonate);

        }
    }

    public SearchDonorAdapter(List<DonorData> postLists)
    {
        this.postLists = postLists;
    }

    @NonNull
    @Override
    public PostHolder onCreateViewHolder(ViewGroup viewGroup, int i) {

        View listitem = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.search_donor_item, viewGroup, false);

        return new PostHolder(listitem);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull PostHolder postHolder, int i) {

        if(i%2==0)
        {
            postHolder.itemView.setBackgroundColor(Color.parseColor("#C13F31"));
        }
        else
        {
            postHolder.itemView.setBackgroundColor(Color.parseColor("#E74C3C"));
        }
        DonorData donorData = postLists.get(i);



        //////show profile image
        if (donorData.getImageURL().equals("default")){
            postHolder.profileImage.setImageResource(R.drawable.user);
        } else {
            Glide.with(context).load(donorData.getImageURL()).into(postHolder.profileImage);
        }
        //////show profile image



        postHolder.Name.setText("Name: "+donorData.getName());
        postHolder.contact.setText("+88"+donorData.getContact());
        postHolder.Address.setText("Address: "+donorData.getAddress());
        postHolder.totaldonate.setText("Total Donation: "+donorData.getTotalDonate()+" times");
        postHolder.posted.setText("Last Donation: "+donorData.getLastDonate());


    }

    @Override
    public int getItemCount() {
        return postLists.size();
    }
}
