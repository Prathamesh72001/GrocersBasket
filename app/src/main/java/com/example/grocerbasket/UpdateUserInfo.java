package com.example.grocerbasket;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.grocerbasket.Session.SessionManager;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.hbb20.CountryCodePicker;

import java.util.HashMap;

public class UpdateUserInfo extends AppCompatActivity {

    TextInputLayout firstname,lastname,email,password,shopname,delfee;
    RelativeLayout UpdateBtn;
    String firstName,lastName,Phone,Password,Email,shopName,delFee,forWhom;
    float v = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_user_info);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //hooks
        firstname=findViewById(R.id.firstname);
        lastname=findViewById(R.id.lastname);
        email=findViewById(R.id.email);
        password=findViewById(R.id.password);
        UpdateBtn=findViewById(R.id.Signupbtn);
        shopname=findViewById(R.id.shopname);
        delfee=findViewById(R.id.delfee);

        //Visibility
        shopname.setVisibility(View.GONE);
        delfee.setVisibility(View.GONE);


        //Session
        SessionManager sessionManager = new SessionManager(this, SessionManager.SESSION_FORWHO);
        HashMap<String, String> forWhoDetails = sessionManager.getForWhomDetailFromSession();
        forWhom = forWhoDetails.get(SessionManager.KEY_FORWHO);


        if(forWhom.equalsIgnoreCase("forUser")) {
            //Session
            SessionManager sessionManager2 = new SessionManager(this, SessionManager.SESSION_USER);
            HashMap<String, String> userDetails = sessionManager2.getUserDetailFromSession();
            firstName = userDetails.get(SessionManager.KEY_FIRSTNAME);
            lastName= userDetails.get(SessionManager.KEY_LASTNAME);
            Phone=userDetails.get(SessionManager.KEY_PHONENO);
            Email= userDetails.get(SessionManager.KEY_EMAIL);
            Password= userDetails.get(SessionManager.KEY_PASSWORD);

            //SetText
            firstname.getEditText().setText(firstName);
            lastname.getEditText().setText(lastName);
            email.getEditText().setText(Email);
            password.getEditText().setText(Password);

            //Animation
            firstname.setTranslationX(800);
            lastname.setTranslationX(800);
            email.setTranslationX(800);
            password.setTranslationX(800);
            UpdateBtn.setTranslationX(800);

            firstname.setAlpha(v);
            lastname.setAlpha(v);
            email.setAlpha(v);
            password.setAlpha(v);
            UpdateBtn.setAlpha(v);

            firstname.animate().translationX(0).alpha(1).setDuration(800).setStartDelay(100).start();
            lastname.animate().translationX(0).alpha(1).setDuration(800).setStartDelay(300).start();
            email.animate().translationX(0).alpha(1).setDuration(800).setStartDelay(500).start();
            password.animate().translationX(0).alpha(1).setDuration(800).setStartDelay(700).start();
            UpdateBtn.animate().translationX(0).alpha(1).setDuration(800).setStartDelay(900).start();

            //Listeners
            UpdateBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                        EditUserData();

                }
            });
        }

        if(forWhom.equalsIgnoreCase("forSeller"))
        {
            //Session
            SessionManager sessionManager2 = new SessionManager(this, SessionManager.SESSION_SELLER);
            HashMap<String, String> userDetails = sessionManager2.getSellerDetailFromSession();
            firstName = userDetails.get(SessionManager.KEY_SELLERFIRSTNAME);
            lastName= userDetails.get(SessionManager.KEY_SELLERLASTNAME);
            Phone=userDetails.get(SessionManager.KEY_SELLERPHONENO);
            Email= userDetails.get(SessionManager.KEY_SELLEREMAIL);
            Password= userDetails.get(SessionManager.KEY_SELLERPASSWORD);
            shopName= userDetails.get(SessionManager.KEY_SELLERSHOPNAME);
            delFee= userDetails.get(SessionManager.KEY_SELLERDELFEE);

            //Visibility
            shopname.setVisibility(View.VISIBLE);
            delfee.setVisibility(View.VISIBLE);

            //SetText
            firstname.getEditText().setText(firstName);
            lastname.getEditText().setText(lastName);
            email.getEditText().setText(Email);
            password.getEditText().setText(Password);
            shopname.getEditText().setText(shopName);
            delfee.getEditText().setText(delFee);

            //Animation
            firstname.setTranslationX(800);
            lastname.setTranslationX(800);
            email.setTranslationX(800);
            password.setTranslationX(800);
            shopname.setTranslationX(800);
            delfee.setTranslationX(800);
            UpdateBtn.setTranslationX(800);

            firstname.setAlpha(v);
            lastname.setAlpha(v);
            email.setAlpha(v);
            password.setAlpha(v);
            shopname.setAlpha(v);
            delfee.setAlpha(v);
            UpdateBtn.setAlpha(v);

            firstname.animate().translationX(0).alpha(1).setDuration(800).setStartDelay(100).start();
            lastname.animate().translationX(0).alpha(1).setDuration(800).setStartDelay(300).start();
            shopname.animate().translationX(0).alpha(1).setDuration(800).setStartDelay(500).start();
            delfee.animate().translationX(0).alpha(1).setDuration(800).setStartDelay(700).start();
            email.animate().translationX(0).alpha(1).setDuration(800).setStartDelay(900).start();
            password.animate().translationX(0).alpha(1).setDuration(800).setStartDelay(1100).start();
            UpdateBtn.animate().translationX(0).alpha(1).setDuration(800).setStartDelay(1300).start();

            //Listeners
            UpdateBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    EditSellerData();

                }
            });
        }




    }

    private void EditSellerData() {
        //CheckInternetConnection
        if (!isConnectedToInternet(this)) {
            startActivity(new Intent(UpdateUserInfo.this,NoInternet.class));
            finishAffinity();
        }

        if (isFirstnameChanged() || isLastnameChanged() || isShopnameChanged() || isdelFeeChanged() || isEmailChanged()  || isPasswordChanged()) {
            Toast.makeText(this, "Data has been updated", Toast.LENGTH_LONG).show();
            startActivity(new Intent(UpdateUserInfo.this, Login.class));
            finishAffinity();
        } else {
            Toast.makeText(this, "Data is same and can not be updated", Toast.LENGTH_LONG).show();
        }
    }


    private void EditUserData() {
        //CheckInternetConnection
        if (!isConnectedToInternet(this)) {
            startActivity(new Intent(UpdateUserInfo.this,NoInternet.class));
            finishAffinity();
        }

        if (isFirstnameChanged() || isLastnameChanged() || isEmailChanged()  || isPasswordChanged()) {
            Toast.makeText(this, "Data has been updated", Toast.LENGTH_LONG).show();
            startActivity(new Intent(UpdateUserInfo.this, Login.class));
            finishAffinity();
        } else {
            Toast.makeText(this, "Data is same and can not be updated", Toast.LENGTH_LONG).show();
        }
    }

    private boolean isConnectedToInternet(UpdateUserInfo updateUserInfo) {
        ConnectivityManager connectivityManager = (ConnectivityManager) updateUserInfo.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wificon = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mobilecon = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

        if ((wificon != null && wificon.isConnected()) || (mobilecon != null && mobilecon.isConnected())) {
            return true;
        } else {
            return false;
        }
    }

    private boolean isFirstnameChanged() {
        if(forWhom.equalsIgnoreCase("forUsers")) {
            if (!firstName.equalsIgnoreCase(firstname.getEditText().getText().toString()) && !firstname.getEditText().getText().toString().isEmpty()) {
                String _newFirstname = firstname.getEditText().getText().toString();

                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
                databaseReference.child(Phone).child("firstname").setValue(_newFirstname);
                return true;
            } else {
                Toast.makeText(UpdateUserInfo.this,"Unable to update info",Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        else if(forWhom.equalsIgnoreCase("forSellers")){
            if (!firstName.equalsIgnoreCase(firstname.getEditText().getText().toString()) && !firstname.getEditText().getText().toString().isEmpty()) {
                String _newFirstname = firstname.getEditText().getText().toString();

                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Sellers");
                databaseReference.child(Phone).child("firstname").setValue(_newFirstname);
                return true;
            } else {
                Toast.makeText(UpdateUserInfo.this,"Unable to update info",Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        else {
            Toast.makeText(UpdateUserInfo.this,"Unable to update info",Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    private boolean isLastnameChanged() {
        if(forWhom.equalsIgnoreCase("forUsers")) {
            if (!lastName.equalsIgnoreCase(lastname.getEditText().getText().toString()) && !lastname.getEditText().getText().toString().isEmpty()) {
                String _newLastname = lastname.getEditText().getText().toString();

                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
                databaseReference.child(Phone).child("lastname").setValue(_newLastname);
                return true;
            } else {
                Toast.makeText(UpdateUserInfo.this,"Unable to update info",Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        else if(forWhom.equalsIgnoreCase("forSellers")){
            if (!lastName.equalsIgnoreCase(lastname.getEditText().getText().toString()) && !lastname.getEditText().getText().toString().isEmpty()) {
                String _newLastname = lastname.getEditText().getText().toString();

                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Sellers");
                databaseReference.child(Phone).child("lastname").setValue(_newLastname);
                return true;
            } else {
                Toast.makeText(UpdateUserInfo.this,"Unable to update info",Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        else {
            Toast.makeText(UpdateUserInfo.this,"Unable to update info",Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    private boolean isShopnameChanged() {
        if (!shopName.equalsIgnoreCase(shopname.getEditText().getText().toString()) && !shopname.getEditText().getText().toString().isEmpty()) {
            String _newShopname = shopname.getEditText().getText().toString();

            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Sellers");
            databaseReference.child(Phone).child("shopname").setValue(_newShopname);
            return true;
        } else {
            Toast.makeText(UpdateUserInfo.this,"Unable to update info",Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    private boolean isdelFeeChanged() {
        if (!delFee.equalsIgnoreCase(delfee.getEditText().getText().toString()) && !delfee.getEditText().getText().toString().isEmpty()) {
            String _newDelfee = delfee.getEditText().getText().toString();

            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Sellers");
            databaseReference.child(Phone).child("delfee").setValue(_newDelfee);
            return true;
        } else {
            Toast.makeText(UpdateUserInfo.this,"Unable to update info",Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    private boolean isEmailChanged() {
        if(forWhom.equalsIgnoreCase("forUsers")) {
            if (!Email.equalsIgnoreCase(email.getEditText().getText().toString()) && !email.getEditText().getText().toString().isEmpty()) {
                String _newEmail = email.getEditText().getText().toString();

                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
                databaseReference.child(Phone).child("email").setValue(_newEmail);
                return true;
            } else {
                Toast.makeText(UpdateUserInfo.this,"Unable to update info",Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        else if(forWhom.equalsIgnoreCase("forSellers")){
            if (!Email.equalsIgnoreCase(email.getEditText().getText().toString()) && !email.getEditText().getText().toString().isEmpty()) {
                String _newEmail = email.getEditText().getText().toString();

                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Sellers");
                databaseReference.child(Phone).child("email").setValue(_newEmail);
                return true;
            } else {
                Toast.makeText(UpdateUserInfo.this,"Unable to update info",Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        else {
            Toast.makeText(UpdateUserInfo.this,"Unable to update info",Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    private boolean isPasswordChanged() {
        if(forWhom.equalsIgnoreCase("forUsers")) {
            if (!Password.equalsIgnoreCase(password.getEditText().getText().toString()) && !password.getEditText().getText().toString().isEmpty()) {
                String _newPass = password.getEditText().getText().toString();

                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
                databaseReference.child(Phone).child("password").setValue(_newPass);
                return true;
            } else {
                Toast.makeText(UpdateUserInfo.this,"Unable to update info",Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        else if(forWhom.equalsIgnoreCase("forSellers")){
            if (!Password.equalsIgnoreCase(password.getEditText().getText().toString()) && !password.getEditText().getText().toString().isEmpty()) {
                String _newPass = password.getEditText().getText().toString();

                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Sellers");
                databaseReference.child(Phone).child("password").setValue(_newPass);
                return true;
            } else {
                Toast.makeText(UpdateUserInfo.this,"Unable to update info",Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        else {
            Toast.makeText(UpdateUserInfo.this,"Unable to update info",Toast.LENGTH_SHORT).show();
            return false;
        }
    }


}