package com.pushpal.chatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.pushpal.chatapp.model.UserData;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {

    private EditText inputemail, inputpassword, retypePassword, fullName, address, contact;
    private FirebaseAuth mAuth;
    private Spinner gender, bloodgroup, division;

    private boolean isUpdate = false;

    private DatabaseReference db_ref, donor_ref;
    private CheckBox isDonor;

    private ProgressDialog pd;


    CircleImageView image_profile;
    StorageReference storageReference;
    private static final int IMAGE_REQUEST = 1;
    private Uri imageUri;
    private StorageTask<UploadTask.TaskSnapshot> uploadTask;



    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        pd = new ProgressDialog(this);
        pd.setMessage("Loading...");
        pd.setCancelable(true);
        pd.setCanceledOnTouchOutside(false);
        pd.show();

        FirebaseDatabase db_User = FirebaseDatabase.getInstance();
        db_ref = db_User.getReference("users");
        donor_ref = db_User.getReference("donors");
        mAuth = FirebaseAuth.getInstance();

        inputemail = findViewById(R.id.input_userEmail);
        inputpassword = findViewById(R.id.input_password);
        retypePassword = findViewById(R.id.input_password_confirm);
        fullName = findViewById(R.id.input_fullName);
        gender = findViewById(R.id.gender);
        address = findViewById(R.id.inputAddress);
        division = findViewById(R.id.inputDivision);
        bloodgroup = findViewById(R.id.inputBloodGroup);
        contact = findViewById(R.id.inputMobile);
        isDonor = findViewById(R.id.checkbox);

        Button btnSignup = findViewById(R.id.button_register);
        //pd = findViewById(R.id.sign_Up_progressBar);
        TextView alreadyHaveAnAccount = findViewById(R.id.tv_have_account);
        LinearLayout orTv = findViewById(R.id.orTv);

        image_profile = findViewById(R.id.profile_image);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        storageReference = FirebaseStorage.getInstance().getReference("uploads");





        image_profile.setVisibility(View.GONE);

        if (mAuth.getCurrentUser() != null) {

            inputemail.setVisibility(View.GONE);
            inputpassword.setVisibility(View.GONE);
            retypePassword.setVisibility(View.GONE);
            btnSignup.setText("Update Profile");
            alreadyHaveAnAccount.setVisibility(View.GONE);
            orTv.setVisibility(View.GONE);
            image_profile.setVisibility(View.VISIBLE);

            pd.dismiss();
            /// getActionBar().setTitle("Profile");
            getSupportActionBar().setTitle("Profile");
            isUpdate = true;

            Query Profile = db_ref.child(mAuth.getCurrentUser().getUid());
            Profile.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    try {
                        ///ai khn a exception dekha jai jkn internet cole jai

                        UserData userData = dataSnapshot.getValue(UserData.class);

                        if (userData != null) {
                            pd.show();

                            //////show profile image
                            if (userData.getImageURL().equals("default")){
                                image_profile.setImageResource(R.drawable.user);
                            } else {
                                Glide.with(ProfileActivity.this).load(userData.getImageURL()).into(image_profile);
                            }
                            //////show profile image



                            fullName.setText(userData.getName());
                            gender.setSelection(userData.getGender());
                            address.setText(userData.getAddress());
                            contact.setText(userData.getContact());
                            bloodgroup.setSelection(userData.getBloodGroup());
                            division.setSelection(userData.getDivision());
                            Query donor = donor_ref.child(division.getSelectedItem().toString())
                                    .child(bloodgroup.getSelectedItem().toString())
                                    .child(mAuth.getCurrentUser().getUid());

                            donor.addListenerForSingleValueEvent(new ValueEventListener() {

                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                    if(dataSnapshot.exists())
                                    {
                                        isDonor.setChecked(true);
                                        isDonor.setText("Unmark this to leave from donors");
                                    }
                                    else
                                    {
                                        Toast.makeText(ProfileActivity.this, "Your are not a donor! Be a donor and save life by donating blood.",
                                                Toast.LENGTH_LONG).show();
                                    }
                                    pd.dismiss();

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                    Log.d("User", databaseError.getMessage());
                                }

                            });
                        }


                    }catch (Exception e){
                        //String error = e.getMessage();
                        //Toast.makeText(ProfileActivity.this, error, Toast.LENGTH_SHORT).show();
                    }



                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.d("User", databaseError.getMessage());
                }
            });



        } else pd.dismiss();


        image_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openImage();
            }
        });
        alreadyHaveAnAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this,LoginActivity.class);
                startActivity(intent);
            }
        });

        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {





                final String email = inputemail.getText().toString();
                final String password = inputpassword.getText().toString();
                final String ConfirmPassword = retypePassword.getText().toString();
                final String Name = fullName.getText().toString();
                final int Gender = gender.getSelectedItemPosition();
                final String Contact = contact.getText().toString();
                final int BloodGroup = bloodgroup.getSelectedItemPosition();
                final String Address = address.getText().toString();
                final int Division = division.getSelectedItemPosition();
                final String blood = bloodgroup.getSelectedItem().toString();
                final String div   = division.getSelectedItem().toString();

                try {

                    if (Name.length() <= 2) {
                        ShowError("Name");
                        fullName.requestFocusFromTouch();
                    } else if (Contact.length() != 11) {
                        ShowError("Contact Number");
                        contact.requestFocusFromTouch();
                    } else if (Address.length() <= 2) {
                        ShowError("Address");
                        address.requestFocusFromTouch();
                    } else {
                        if (!isUpdate) {
                            if (email.length() == 0 && email.matches(emailPattern)) {
                                ShowError("Email ID");
                                inputemail.requestFocusFromTouch();
                            } else if (password.length() < 5) {
                                ShowError("Password");
                                inputpassword.requestFocusFromTouch();
                            } else if (password.compareTo(ConfirmPassword) != 0) {
                                Toast.makeText(ProfileActivity.this, "Password did not match!", Toast.LENGTH_LONG)
                                        .show();
                                retypePassword.requestFocusFromTouch();
                            } else {
                                pd.show();
                                mAuth.createUserWithEmailAndPassword(email, password)
                                        .addOnCompleteListener(ProfileActivity.this, new OnCompleteListener<AuthResult>() {
                                            @Override
                                            public void onComplete(@NonNull Task<AuthResult> task) {

                                                if (!task.isSuccessful()) {
                                                    Toast.makeText(ProfileActivity.this, "Registration failed! try agian.", Toast.LENGTH_LONG)
                                                            .show();
                                                    Log.v("error", task.getException().getMessage());
                                                } else {
                                                    String id = mAuth.getCurrentUser().getUid();

                                                    db_ref.child(id).child("imageURL").setValue("default");


                                                    db_ref.child(id).child("Name").setValue(Name);
                                                    db_ref.child(id).child("Gender").setValue(Gender);
                                                    db_ref.child(id).child("Contact").setValue(Contact);
                                                    db_ref.child(id).child("BloodGroup").setValue(BloodGroup);
                                                    db_ref.child(id).child("Address").setValue(Address);
                                                    db_ref.child(id).child("Division").setValue(Division);

                                                    if(isDonor.isChecked())
                                                    {
                                                        donor_ref.child(div).child(blood).child(id).child("imageURL").setValue("default");

                                                        donor_ref.child(div).child(blood).child(id).child("UID").setValue(id).toString();
                                                        donor_ref.child(div).child(blood).child(id).child("LastDonate").setValue("Don't donate yet!");
                                                        donor_ref.child(div).child(blood).child(id).child("TotalDonate").setValue(0);
                                                        donor_ref.child(div).child(blood).child(id).child("Name").setValue(Name);
                                                        donor_ref.child(div).child(blood).child(id).child("Contact").setValue(Contact);
                                                        donor_ref.child(div).child(blood).child(id).child("Address").setValue(Address);

                                                    }

                                                    Toast.makeText(getApplicationContext(), "Welcome, your account has been created!", Toast.LENGTH_LONG)
                                                            .show();
                                                    Intent intent = new Intent(ProfileActivity.this, Dashboard.class);
                                                    startActivity(intent);
                                                    finish();
                                                }
                                                pd.dismiss();

                                            }

                                        });
                            }

                        } else {

                            String id = mAuth.getCurrentUser().getUid();

                            db_ref.child(id).child("Name").setValue(Name);
                            db_ref.child(id).child("Gender").setValue(Gender);
                            db_ref.child(id).child("Contact").setValue(Contact);
                            db_ref.child(id).child("BloodGroup").setValue(BloodGroup);
                            db_ref.child(id).child("Address").setValue(Address);
                            db_ref.child(id).child("Division").setValue(Division);

                            if(isDonor.isChecked())
                            {



                                Query Profile = db_ref.child(mAuth.getCurrentUser().getUid());
                                Profile.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        UserData userData = snapshot.getValue(UserData.class);

                                        if (userData != null) {


                                            try {

                                                String mUri = userData.getImageURL();
                                                donor_ref.child(div).child(blood).child(id).child("imageURL").setValue(mUri).toString();

                                            }catch (Exception e){
                                                String erreo = e.getMessage();
                                                Toast.makeText(ProfileActivity.this, erreo, Toast.LENGTH_SHORT).show();
                                            }


                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        Log.d("User", error.getMessage());
                                    }
                                });





                                donor_ref.child(div).child(blood).child(id).child("UID").setValue(id).toString();
                                donor_ref.child(div).child(blood).child(id).child("LastDonate").setValue("Don't donate yet!");
                                donor_ref.child(div).child(blood).child(id).child("TotalDonate").setValue(0);
                                donor_ref.child(div).child(blood).child(id).child("Name").setValue(Name);
                                donor_ref.child(div).child(blood).child(id).child("Contact").setValue(Contact);
                                donor_ref.child(div).child(blood).child(id).child("Address").setValue(Address);

                            }
                            else
                            {
                                donor_ref.child(div).child(blood).child(id).removeValue();

                            }
                            Toast.makeText(getApplicationContext(), "Your account has been updated!", Toast.LENGTH_LONG)
                                    .show();
                            Intent intent = new Intent(ProfileActivity.this, Dashboard.class);
                            startActivity(intent);
                            finish();
                        }
                        pd.dismiss();

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });
    }

    private void ShowError(String error) {

        Toast.makeText(ProfileActivity.this, "Please, Enter a valid "+error,
                Toast.LENGTH_LONG).show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void openImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, IMAGE_REQUEST);
    }

    private String getFileExtension(Uri uri){
        ContentResolver contentResolver = this.getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void uploadImage(){
        pd.setMessage("Uploading");
        pd.show();

        if (imageUri != null){
            final  StorageReference fileReference = storageReference.child(System.currentTimeMillis()
                    +"."+getFileExtension(imageUri));

            uploadTask = fileReference.putFile(imageUri);
            uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()){
                        throw  task.getException();
                    }

                    return  fileReference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()){
                        Uri downloadUri = task.getResult();
                        String mUri = downloadUri.toString();

                        HashMap<String, Object> map = new HashMap<>();
                        map.put("imageURL", ""+mUri);

//                        db_ref = FirebaseDatabase.getInstance().getReference("users").child(mAuth.getCurrentUser().getUid());

//                        db_ref.updateChildren(map);

                        final String blood = bloodgroup.getSelectedItem().toString();
                        final String div   = division.getSelectedItem().toString();


                        String id = mAuth.getCurrentUser().getUid();

                        if (isDonor.isChecked()) {
                                donor_ref.child(div).child(blood).child(id).updateChildren(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {

                                        Glide.with(ProfileActivity.this).load(mUri).into(image_profile);
                                        Toast.makeText(ProfileActivity.this, "Profile Image uploaded!", Toast.LENGTH_SHORT).show();
                                    }
                                });

                        }

                        db_ref.child(id).updateChildren(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {

                                    Glide.with(ProfileActivity.this).load(mUri).into(image_profile);
                                    Toast.makeText(ProfileActivity.this, "Profile Image uploaded!", Toast.LENGTH_SHORT).show();
                                }
                            });


                        pd.dismiss();
                    } else {
                        Toast.makeText(ProfileActivity.this, "Failed!", Toast.LENGTH_SHORT).show();
                        pd.dismiss();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(ProfileActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    pd.dismiss();
                }
            });
        } else {
            Toast.makeText(ProfileActivity.this, "No image selected", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null){
            imageUri = data.getData();

            if (uploadTask != null && uploadTask.isInProgress()){
                Toast.makeText(this, "Upload in preogress", Toast.LENGTH_SHORT).show();
            } else {
                uploadImage();
            }
        }
    }
}

