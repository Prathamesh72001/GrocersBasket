package com.example.grocerbasket;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.grocerbasket.Session.SessionManager;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class SellerProfile extends AppCompatActivity {

    TextView shopnameDet,phoneDet,addressDet;
    RelativeLayout my_loc,log_out,userLoc_layout,progressBar,add_shop_img_text,sellerProf;
    CircleImageView userProfile;
    ImageView edit_ic,shop_img;
    StorageReference storageReference, profileRef,storageReference1,shopImgRef;
    String phone;
    SwitchCompat isShopOpenSwitch;
    boolean isShowOpen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_profile);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //hooks
        shopnameDet=findViewById(R.id.seller_shop_name);
        phoneDet=findViewById(R.id.seller_phone);
        addressDet=findViewById(R.id.UserLocation);
        userLoc_layout=findViewById(R.id.User_loc_Layout);
        my_loc=findViewById(R.id.my_loc);
        log_out=findViewById(R.id.logout);
        userProfile=findViewById(R.id.sellerProfile);
        storageReference = FirebaseStorage.getInstance().getReference();
        storageReference1 = FirebaseStorage.getInstance().getReference();
        progressBar=findViewById(R.id.progressBar);
        edit_ic=findViewById(R.id.edit_ic);
        add_shop_img_text=findViewById(R.id.Add_shop_img_text);
        shop_img=findViewById(R.id.ShopImage);
        isShopOpenSwitch=findViewById(R.id.isShopOpen);
        sellerProf=findViewById(R.id.sellerImg);

        //Session
        SessionManager sessionManager1 = new SessionManager(SellerProfile.this, SessionManager.SESSION_ADDRESS);
        HashMap<String, String> addDetails = sessionManager1.getAddressDetailFromSession();
        String address = addDetails.get(SessionManager.KEY_ADD);
        addressDet.setText(address);

        SessionManager sessionManager2 = new SessionManager(this, SessionManager.SESSION_SELLER);
        HashMap<String, String> userDetails = sessionManager2.getSellerDetailFromSession();
        String firstname = userDetails.get(SessionManager.KEY_SELLERSHOPNAME);
        isShowOpen=sessionManager2.checkIsOpen();
        if(isShowOpen)
        {
            isShopOpenSwitch.setChecked(true);
        }
        else{
            isShopOpenSwitch.setChecked(false);
        }

        phone = userDetails.get(SessionManager.KEY_SELLERPHONENO);
        shopnameDet.setText(firstname);
        phoneDet.setText(phone);

        //ProfileImageLoad
        profileRef = storageReference.child("Seller Profiles/").child(Objects.requireNonNull(phone + "/" + "profile.jpg"));
        profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).centerCrop().resize(userProfile.getMeasuredWidth(),userProfile.getMeasuredHeight()).into(userProfile);
            }
        });

        //ShopImageLoad
        shopImgRef = storageReference1.child("Seller Shop Images/").child(Objects.requireNonNull(phone + "/" + "profile.jpg"));
        shopImgRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                add_shop_img_text.setVisibility(View.GONE);
                Picasso.get().load(uri).centerCrop().resize(shop_img.getMeasuredWidth(),shop_img.getMeasuredHeight()).into(shop_img);
            }
        });

        //Listeners
        userLoc_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SellerProfile.this,AddLocation.class));
            }
        });

        my_loc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SellerProfile.this,AddLocation.class));
            }
        });

        log_out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SessionManager sessionManager1 = new SessionManager(SellerProfile.this, SessionManager.SESSION_FORWHO);
                sessionManager1.creatingForWhomSession("forWho");

                SessionManager sessionManager2 = new SessionManager(SellerProfile.this, SessionManager.SESSION_ADDRESS);
                sessionManager2.creatingAddressSession("Swagath Rd-Tilaknagar,Banglore-560041");

                SessionManager sessionManager = new SessionManager(SellerProfile.this, SessionManager.SESSION_USER);
                sessionManager.logout();
                Intent logout_intent = new Intent(SellerProfile.this, Dashboard.class);
                startActivity(logout_intent);
                finishAffinity();
            }
        });

        sellerProf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //CheckInternetConnection
                if (!isConnectedToInternet(SellerProfile.this)) {
                    startActivity(new Intent(SellerProfile.this,NoInternet.class));
                    finish();
                }

                Intent uploadintent=new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(uploadintent,1000);
            }
        });

        shop_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //CheckInternetConnection
                if (!isConnectedToInternet(SellerProfile.this)) {
                    startActivity(new Intent(SellerProfile.this,NoInternet.class));
                    finish();
                }

                Intent uploadintent=new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(uploadintent,1100);
            }
        });

        edit_ic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SellerProfile.this,UpdateUserInfo.class));
            }
        });

        //isOpen
        isShopOpenSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                FirebaseDatabase rootnode = FirebaseDatabase.getInstance();
                DatabaseReference reference = rootnode.getReference("Sellers");
                if(isChecked){
                    reference.child(phone).child("isOpen").setValue(true);

                    SessionManager sessionManager3 = new SessionManager(SellerProfile.this, SessionManager.SESSION_SELLER);
                    sessionManager3.OpenShop();
                }
                else
                {
                    reference.child(phone).child("isOpen").setValue(false);

                    SessionManager sessionManager3 = new SessionManager(SellerProfile.this, SessionManager.SESSION_SELLER);
                    sessionManager3.CloseShop();
                }
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(SellerProfile.this,Dashboard.class));
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

        if (requestCode == 1100) {
            if (resultCode == Activity.RESULT_OK) {
                Uri imageUri = data.getData();
                //ProgressBar
                progressBar.setVisibility(View.VISIBLE);

                uploadShopImageToFirebase(imageUri);

            }
        }

    }

    private void uploadShopImageToFirebase(Uri imageUri) {
        StorageReference fileRef = storageReference1.child("Seller Shop Images/").child(Objects.requireNonNull(phone) + "/" + "profile.jpg");
        fileRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Picasso.get().load(uri).centerCrop().resize(shop_img.getMeasuredWidth(),shop_img.getMeasuredHeight()).into(shop_img);
                        Toast.makeText(SellerProfile.this, "Profile Updated", Toast.LENGTH_SHORT).show();
                        //ProgressBar
                        progressBar.setVisibility(View.GONE);
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(SellerProfile.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                //ProgressBar
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    private void uploadImageToFirebase(Uri imageUri) {
        StorageReference fileRef = storageReference.child("Seller Profiles/").child(Objects.requireNonNull(phone) + "/" + "profile.jpg");
        fileRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Picasso.get().load(uri).centerCrop().resize(userProfile.getMeasuredWidth(),userProfile.getMeasuredHeight()).into(userProfile);
                        Toast.makeText(SellerProfile.this, "Profile Updated", Toast.LENGTH_SHORT).show();
                        //ProgressBar
                        progressBar.setVisibility(View.GONE);
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(SellerProfile.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                //ProgressBar
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    private boolean isConnectedToInternet(SellerProfile sellerProfile) {
        ConnectivityManager connectivityManager = (ConnectivityManager) sellerProfile.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wificon = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mobilecon = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

        if ((wificon != null && wificon.isConnected()) || (mobilecon != null && mobilecon.isConnected())) {
            return true;
        } else {
            return false;
        }
    }
}