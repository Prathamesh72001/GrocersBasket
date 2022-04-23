package com.example.grocerbasket;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;
import com.hbb20.CountryCodePicker;

import java.util.Objects;

public class Signup_2 extends AppCompatActivity {

    ImageView backbtn;
    CountryCodePicker countryCodePicker;
    TextInputLayout phoneno;
    RelativeLayout nextbtn;
    TextView login, tag;
    float v = 0;
    String firstname, lastname, shopname, shopadd, delfee, email, password, forWhat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup2);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //hooks
        backbtn = findViewById(R.id.backBtn);
        countryCodePicker = findViewById(R.id.countryCodePicker);
        phoneno = findViewById(R.id.Phoneno);
        nextbtn = findViewById(R.id.Signupbtn);
        login = findViewById(R.id.login);
        tag = findViewById(R.id.tag);

        Intent signupintent = getIntent();
        forWhat = signupintent.getStringExtra("forWhat");
        firstname = signupintent.getStringExtra("firstname");
        lastname = signupintent.getStringExtra("lastname");
        shopname = signupintent.getStringExtra("shopname");
        shopadd = signupintent.getStringExtra("shopadd");
        delfee = signupintent.getStringExtra("delfee");
        email = signupintent.getStringExtra("email");
        password = signupintent.getStringExtra("password");




        nextbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String forSeller = "forSeller";
                //User & Seller
                if(forSeller.equalsIgnoreCase(forWhat))
                {
                    callVerificationScreenforSeller();
                }
                else
                {
                    callVerificationScreen();
                }
            }
        });


        //Listeners
        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Signup_2.this, Signup_1.class);
                startActivity(intent);
                finish();
            }
        });


        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Signup_2.this, Login.class);
                startActivity(intent);
                finish();
            }
        });

        //Animation
        countryCodePicker.setTranslationX(800);
        phoneno.setTranslationX(800);
        login.setTranslationX(800);
        nextbtn.setTranslationX(800);


        countryCodePicker.setAlpha(v);
        phoneno.setAlpha(v);
        login.setAlpha(v);
        nextbtn.setAlpha(v);

        countryCodePicker.animate().translationX(0).alpha(1).setDuration(800).setStartDelay(100).start();
        phoneno.animate().translationX(0).alpha(1).setDuration(800).setStartDelay(300).start();
        login.animate().translationX(0).alpha(1).setDuration(800).setStartDelay(500).start();
        nextbtn.animate().translationX(0).alpha(1).setDuration(800).setStartDelay(500).start();
    }

    private void callVerificationScreenforSeller() {
        if (!validatePhone()) {
            return;
        }

        String phoneNo = Objects.requireNonNull(phoneno.getEditText()).getText().toString().trim();
        String phone = "+" + countryCodePicker.getSelectedCountryCode() + phoneNo;

        Intent intent = new Intent(getApplicationContext(), Verification.class);
        intent.putExtra("forWhat", "forSeller");
        intent.putExtra("firstname", firstname);
        intent.putExtra("lastname", lastname);
        intent.putExtra("shopname", shopname);
        intent.putExtra("shopadd", shopadd);
        intent.putExtra("delfee", delfee);
        intent.putExtra("email", email);
        intent.putExtra("password", password);
        intent.putExtra("phoneno", phone);
        startActivity(intent);
        finish();
    }

    private void callVerificationScreen() {
        if (!validatePhone()) {
            return;
        }

        String phoneNo = Objects.requireNonNull(phoneno.getEditText()).getText().toString().trim();
        String phone = "+" + countryCodePicker.getSelectedCountryCode() + phoneNo;

        Intent intent = new Intent(getApplicationContext(), Verification.class);
        intent.putExtra("firstname", firstname);
        intent.putExtra("lastname", lastname);
        intent.putExtra("email", email);
        intent.putExtra("password", password);
        intent.putExtra("phoneno", phone);
        startActivity(intent);
        finish();
    }

    //Validation
    private boolean validatePhone() {
        String val = Objects.requireNonNull(phoneno.getEditText()).getText().toString().trim();
        if (val.isEmpty()) {
            phoneno.setError("Empty");
            return false;
        } else {
            phoneno.setError(null);
            phoneno.requestFocus();
            return true;
        }
    }
}