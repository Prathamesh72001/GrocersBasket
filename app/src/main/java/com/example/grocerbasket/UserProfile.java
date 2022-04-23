package com.example.grocerbasket;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.grocerbasket.Session.SessionManager;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserProfile extends AppCompatActivity {

    TextView firstnameDet,phoneDet,addressDet;
    RelativeLayout my_loc,log_out,my_order,my_ratings,userLoc_layout,progressBar,userProf;
    CircleImageView userProfile;
    ImageView edit_ic;
    StorageReference storageReference, profileRef;
    String phone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //hooks
        firstnameDet=findViewById(R.id.user_firstname);
        phoneDet=findViewById(R.id.user_phone);
        addressDet=findViewById(R.id.UserLocation);
        userLoc_layout=findViewById(R.id.User_loc_Layout);
        my_loc=findViewById(R.id.my_loc);
        log_out=findViewById(R.id.logout);
        my_order=findViewById(R.id.my_orders);
        my_ratings=findViewById(R.id.my_ratings);
        userProfile=findViewById(R.id.user_profile);
        storageReference = FirebaseStorage.getInstance().getReference();
        progressBar=findViewById(R.id.progressBar);
        edit_ic=findViewById(R.id.edit_ic);
        userProf=findViewById(R.id.userProf);
        //Session
        SessionManager sessionManager1 = new SessionManager(UserProfile.this, SessionManager.SESSION_ADDRESS);
        HashMap<String, String> addDetails = sessionManager1.getAddressDetailFromSession();
        String address = addDetails.get(SessionManager.KEY_ADD);
        addressDet.setText(address);

        SessionManager sessionManager2 = new SessionManager(this, SessionManager.SESSION_USER);
        HashMap<String, String> userDetails = sessionManager2.getUserDetailFromSession();
        String firstname = userDetails.get(SessionManager.KEY_FIRSTNAME);
        phone = userDetails.get(SessionManager.KEY_PHONENO);
        firstnameDet.setText(firstname);
        phoneDet.setText(phone);

        //ProfileImageLoad
        profileRef = storageReference.child("User Profiles/").child(Objects.requireNonNull(phone + "/" + "profile.jpg"));
        profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).centerCrop().resize(userProfile.getMeasuredWidth(),userProfile.getMeasuredHeight()).into(userProfile);
            }
        });

        //Listeners
        userLoc_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(UserProfile.this,AddLocation.class));
            }
        });

        my_loc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(UserProfile.this,AddLocation.class));
            }
        });

        my_order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(UserProfile.this,FavouriteActivity.class));
            }
        });

        my_ratings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(UserProfile.this,UserReviews.class));
            }
        });

        log_out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SessionManager sessionManager1 = new SessionManager(UserProfile.this, SessionManager.SESSION_FORWHO);
                sessionManager1.creatingForWhomSession("forWho");

                SessionManager sessionManager2 = new SessionManager(UserProfile.this, SessionManager.SESSION_ADDRESS);
                sessionManager2.creatingAddressSession("Swagath Rd-Tilaknagar,Banglore-560041");

                SessionManager sessionManager = new SessionManager(UserProfile.this, SessionManager.SESSION_USER);
                sessionManager.logout();
                Intent logout_intent = new Intent(UserProfile.this, Dashboard.class);
                startActivity(logout_intent);
                finishAffinity();
            }
        });

        userProf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //CheckInternetConnection
                if (!isConnectedToInternet(UserProfile.this)) {
                    startActivity(new Intent(UserProfile.this,NoInternet.class));
                    finish();
                }

                Intent uploadintent=new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(uploadintent,1000);
            }
        });

        edit_ic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(UserProfile.this,UpdateUserInfo.class));
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(UserProfile.this,Dashboard.class));
        finishAffinity();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1000) {
            if (resultCode == Activity.RESULT_OK) {
                Uri imageUri = data.getData();
                //ProgressBar
                progressBar.setVisibility(View.VISIBLE);

                uploadImageToFirebase(imageUri);

            }


        }

    }

    private void uploadImageToFirebase(Uri imageUri) {
        StorageReference fileRef = storageReference.child("User Profiles/").child(Objects.requireNonNull(phone) + "/" + "profile.jpg");
        fileRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Picasso.get().load(uri).centerCrop().resize(userProfile.getMeasuredWidth(),userProfile.getMeasuredHeight()).into(userProfile);
                        Toast.makeText(UserProfile.this, "Profile Updated", Toast.LENGTH_SHORT).show();
                        //ProgressBar
                        progressBar.setVisibility(View.GONE);
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(UserProfile.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                //ProgressBar
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    private boolean isConnectedToInternet(UserProfile userProfile) {
        ConnectivityManager connectivityManager = (ConnectivityManager) userProfile.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wificon = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mobilecon = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

        if ((wificon != null && wificon.isConnected()) || (mobilecon != null && mobilecon.isConnected())) {
            return true;
        } else {
            return false;
        }
    }
}