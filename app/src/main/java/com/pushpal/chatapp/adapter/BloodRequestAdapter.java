package com.pushpal.chatapp.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.pushpal.chatapp.R;
import com.pushpal.chatapp.model.CustomUserData;
import java.util.List;
import de.hdodenhof.circleimageview.CircleImageView;

public class BloodRequestAdapter extends RecyclerView.Adapter<BloodRequestAdapter.PostHolder> {

    FirebaseDatabase fdb;
    DatabaseReference db_ref;
    FirebaseUser firebaseUser;

    private final List<CustomUserData> postLists;

    String uid;

    public class PostHolder extends RecyclerView.ViewHolder
    {
        Context context;
        TextView Name, bloodgroup, Address, contact, posted;
        CircleImageView profileImage;

        public PostHolder(@NonNull View itemView) {
            super(itemView);
            context = itemView.getContext();

            firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

            profileImage = itemView.findViewById(R.id.profile_image);
            Name = itemView.findViewById(R.id.reqstUser);
            contact = itemView.findViewById(R.id.targetCN);
            bloodgroup = itemView.findViewById(R.id.targetBG);
            Address = itemView.findViewById(R.id.reqstLocation);
            posted = itemView.findViewById(R.id.posted);
            //delectBtn = itemView.findViewById(R.id.delete_btn);
            fdb = FirebaseDatabase.getInstance();
            db_ref = fdb.getReference("posts");

            if(firebaseUser == null)
            {
                Toast.makeText(context, "you don't delect this !", Toast.LENGTH_SHORT).show();
            } else {
                uid = firebaseUser.getUid();
            }
        }
    }

    public BloodRequestAdapter(List<CustomUserData> postLists)
    {
        this.postLists = postLists;
    }

    @NonNull
    @Override
    public PostHolder onCreateViewHolder(ViewGroup viewGroup, int i) {

        View listitem = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.request_list_item, viewGroup, false);

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
        CustomUserData customUserData = postLists.get(i);

        //////show profile image

        if (customUserData.getImageURL().equals("default")){
            postHolder.profileImage.setImageResource(R.drawable.user);
        } else {
            Glide.with(postHolder.context).load(customUserData.getImageURL()).into(postHolder.profileImage);
        }

        //////show profile image

        postHolder.Name.setText("Posted by: "+customUserData.getName());
        postHolder.Address.setText("From: "+customUserData.getAddress()+", "+customUserData.getDivision());
        postHolder.bloodgroup.setText("Needs "+customUserData.getBloodGroup());
        postHolder.posted.setText("Posted on:"+customUserData.getTime()+", "+customUserData.getDate());
        postHolder.contact.setText("+88"+customUserData.getContact());


    }

    @Override
    public int getItemCount() {
        return postLists.size();
    }
}
