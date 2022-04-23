package com.example.grocerbasket;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.chaos.view.PinView;
import com.example.grocerbasket.Constructors.SellerHelperClass;
import com.example.grocerbasket.Constructors.UserHelperClass;
import com.example.grocerbasket.Session.SessionManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class Verification extends AppCompatActivity {

    PinView pinView;
    String firstname, lastname, shopname, shopadd, delfee, email, phoneno, password, WhatToDo, codeBySystem, forWhat,forWhom;
    RelativeLayout nextbtn;
    TextView NoText,text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //hooks
        pinView = findViewById(R.id.pinView);
        nextbtn = findViewById(R.id.Nextbtn);

        Intent signupintent = getIntent();
        forWhat = signupintent.getStringExtra("forWhat");
        forWhom = signupintent.getStringExtra("forWhom");
        firstname = signupintent.getStringExtra("firstname");
        lastname = signupintent.getStringExtra("lastname");
        shopname = signupintent.getStringExtra("shopname");
        shopadd = signupintent.getStringExtra("shopadd");
        delfee = signupintent.getStringExtra("delfee");
        email = signupintent.getStringExtra("email");
        phoneno = signupintent.getStringExtra("phoneno");
        password = signupintent.getStringExtra("password");
        WhatToDo = signupintent.getStringExtra("WhatToDo");
        text=findViewById(R.id.text);

        if("updatedata".equalsIgnoreCase(WhatToDo)){
            text.setText("Next");
        }
        else {
            text.setText("Signup");
        }


        NoText = findViewById(R.id.NumberText);


        NoText.setText(phoneno);

        //Listeners
        nextbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SignupScreen();
            }
        });


        //Functions
        sendVerificationCode(phoneno);
    }

    private void SignupScreen() {
        //Check internet
        //CheckInternetConnection
        if (!isConnectedToInternet(this)) {
            startActivity(new Intent(Verification.this, NoInternet.class));
            finish();
        }

        //Verification
        String code = pinView.getText().toString();
        if (!code.isEmpty()) {
            verifyCode(code);
        }
    }

    private void sendVerificationCode(String phoneno) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(phoneno)       // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(this)                 // Activity (for callback binding)
                        .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks =
            new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                @Override
                public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                    super.onCodeSent(s, forceResendingToken);
                    codeBySystem = s;
                }

                @Override
                public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                    String code = phoneAuthCredential.getSmsCode();
                    if (code != null) {
                        pinView.setText(code);
                        verifyCode(code);
                    }
                }

                @Override
                public void onVerificationFailed(@NonNull FirebaseException e) {
                    Toast.makeText(Verification.this, e.getMessage(), Toast.LENGTH_LONG).show();
                }
            };

    private void verifyCode(String code) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(codeBySystem, code);
        signInWithPhoneAuthCredential(credential);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            String Update = "updatedata";
                            if (Update.equalsIgnoreCase(WhatToDo)) {
                                updatePassword();
                            } else {
                                String forSeller = "forSeller";
                                //User & Seller
                                if (forSeller.equalsIgnoreCase(forWhat)) {
                                    storeNewSellerData();
                                } else {
                                    storeNewUserData();
                                }
                            }
                        } else {
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                Toast.makeText(Verification.this, "Verification not completed", Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                });
    }



    private void updatePassword() {
        if("user".equalsIgnoreCase(forWhom)) {
            Intent intent = new Intent(getApplicationContext(), NewPassword.class);
            intent.putExtra("phoneno", phoneno);
            intent.putExtra("firstname", firstname);
            intent.putExtra("forWhom", "user");
            startActivity(intent);
            finishAffinity();
        }else {
            Intent intent = new Intent(getApplicationContext(), NewPassword.class);
            intent.putExtra("phoneno", phoneno);
            intent.putExtra("firstname", firstname);
            intent.putExtra("forWhom", "seller");
            startActivity(intent);
            finishAffinity();
        }
    }

    private void storeNewSellerData() {
        FirebaseDatabase rootnode = FirebaseDatabase.getInstance();
        DatabaseReference reference = rootnode.getReference("Sellers");

        SellerHelperClass addNewSeller = new SellerHelperClass(firstname, lastname,shopname,shopadd,delfee, email, phoneno, password);

        SessionManager sessionManager = new SessionManager(Verification.this, SessionManager.SESSION_ADDRESS);
        sessionManager.creatingAddressSession(shopadd);

        reference.child(phoneno).setValue(addNewSeller);
        Intent intent = new Intent(Verification.this, Login.class);
        startActivity(intent);
        finishAffinity();
    }

    private void storeNewUserData() {
        FirebaseDatabase rootnode = FirebaseDatabase.getInstance();
        DatabaseReference reference = rootnode.getReference("Users");

        UserHelperClass addNewUser = new UserHelperClass(firstname, lastname, email, phoneno, password);

        reference.child(phoneno).setValue(addNewUser);

        SessionManager sessionManager2 = new SessionManager(Verification.this, SessionManager.SESSION_ADDRESS);
        HashMap<String, String> addDetails = sessionManager2.getAddressDetailFromSession();
        String addr=addDetails.get(SessionManager.KEY_ADD);

        reference.child(phoneno).child("useradd").setValue(addr);
        Intent intent = new Intent(Verification.this, Login.class);
        startActivity(intent);
        finishAffinity();

    }

    private boolean isConnectedToInternet(Verification verification) {
        ConnectivityManager connectivityManager = (ConnectivityManager) verification.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wificon = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mobilecon = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

        if ((wificon != null && wificon.isConnected()) || (mobilecon != null && mobilecon.isConnected())) {
            return true;
        } else {
            return false;
        }
    }


}