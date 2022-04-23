package com.example.grocerbasket;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.hbb20.CountryCodePicker;

import java.util.Objects;

public class ForgetPassword extends AppCompatActivity {
    TextInputLayout phoneno;
    RelativeLayout nextbtn, progressBar;
    CountryCodePicker countryCodePicker;
    float v = 0;
    String forWhom;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //hooks
        countryCodePicker = findViewById(R.id.countryCodePicker);
        phoneno = findViewById(R.id.Phoneno);
        nextbtn = findViewById(R.id.Nextbtn);

        progressBar = findViewById(R.id.progressBar);
        forWhom=getIntent().getStringExtra("forWhom");

        //Listeners





        nextbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if("user".equalsIgnoreCase(forWhom)) {
                    verifyUser();
                }else {
                    verifySeller();
                }
            }
        });


        //Animation
        phoneno.setTranslationX(800);
        countryCodePicker.setTranslationX(800);
        nextbtn.setTranslationX(800);

        phoneno.setAlpha(v);
        countryCodePicker.setAlpha(v);
        nextbtn.setAlpha(v);

        countryCodePicker.animate().translationX(0).alpha(1).setDuration(800).setStartDelay(100).start();
        phoneno.animate().translationX(0).alpha(1).setDuration(800).setStartDelay(300).start();
        nextbtn.animate().translationX(0).alpha(1).setDuration(800).setStartDelay(500).start();
    }


    //Validation
    private boolean validatePhone() {
        String val = Objects.requireNonNull(phoneno.getEditText()).getText().toString().trim();
        if (val.isEmpty()) {
            phoneno.setError("Empty");
            return false;
        } else {
            phoneno.setError(null);
            return true;
        }
    }

    private void verifySeller() {
        //Validation
        if (!validatePhone()) {
            return;
        }

        //Progress
        progressBar.setVisibility(View.VISIBLE);
        //


        //Set data
        String _phoneNo = phoneno.getEditText().getText().toString().trim();
        if (_phoneNo.charAt(0) == 0) {
            _phoneNo = _phoneNo.substring(1);
        }
        String _CompletePhoneno = "+" + countryCodePicker.getSelectedCountryCode() + _phoneNo;

        //VerifyUser
        Query checkuser = FirebaseDatabase.getInstance().getReference("Sellers").orderByChild("phoneno").equalTo(_CompletePhoneno);
        checkuser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    phoneno.setError(null);
                    phoneno.setErrorEnabled(false);

                    String systemFullname = snapshot.child(_CompletePhoneno).child("firstname").getValue(String.class);

                    String UpdateData = "updatedata";

                    Intent intent = new Intent(getApplicationContext(), Verification.class);
                    intent.putExtra("phoneno", _CompletePhoneno);
                    intent.putExtra("firstname", systemFullname);
                    intent.putExtra("forWhom", "seller");
                    intent.putExtra("WhatToDo", UpdateData);
                    startActivity(intent);
                    finish();

                    //Progress
                    progressBar.setVisibility(View.GONE);
                    //

                } else {
                    //Progress
                    progressBar.setVisibility(View.GONE);
                    //
                    Toast.makeText(ForgetPassword.this, "No such seller exist", Toast.LENGTH_SHORT).show();
                    phoneno.requestFocus();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                //Progress
                progressBar.setVisibility(View.GONE);
                //
                Toast.makeText(ForgetPassword.this, "Resetting password failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void verifyUser() {
        /*//Check internet
        CheckInternet checkInternet=new CheckInternet();
        if(!checkInternet.isConnected(this)){
            showCustomDialog();
            return;
        }*/


        //Validation
        if (!validatePhone()) {
            return;
        }

        //Progress
        progressBar.setVisibility(View.VISIBLE);
        //


        //Set data
        String _phoneNo = phoneno.getEditText().getText().toString().trim();
        if (_phoneNo.charAt(0) == 0) {
            _phoneNo = _phoneNo.substring(1);
        }
        String _CompletePhoneno = "+" + countryCodePicker.getSelectedCountryCode() + _phoneNo;

        //VerifyUser
        Query checkuser = FirebaseDatabase.getInstance().getReference("Users").orderByChild("phoneno").equalTo(_CompletePhoneno);
        checkuser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    phoneno.setError(null);
                    phoneno.setErrorEnabled(false);

                    String systemFullname = snapshot.child(_CompletePhoneno).child("firstname").getValue(String.class);

                    String UpdateData = "updatedata";

                    Intent intent = new Intent(getApplicationContext(), Verification.class);
                    intent.putExtra("phoneno", _CompletePhoneno);
                    intent.putExtra("firstname", systemFullname);
                    intent.putExtra("forWhom", "user");
                    intent.putExtra("WhatToDo", UpdateData);
                    startActivity(intent);
                    finish();

                    //Progress
                    progressBar.setVisibility(View.GONE);
                    //

                } else {
                    //Progress
                    progressBar.setVisibility(View.GONE);
                    //
                    Toast.makeText(ForgetPassword.this, "No such user exist", Toast.LENGTH_SHORT).show();
                    phoneno.requestFocus();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                //Progress
                progressBar.setVisibility(View.GONE);
                //
                Toast.makeText(ForgetPassword.this, "Resetting password failed", Toast.LENGTH_SHORT).show();
            }
        });
    }


}