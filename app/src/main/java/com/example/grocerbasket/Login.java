package com.example.grocerbasket;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.grocerbasket.Session.SessionManager;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.hbb20.CountryCodePicker;

import java.util.HashMap;
import java.util.Objects;

public class Login extends AppCompatActivity {

    ImageView backbtn;
    TextInputLayout phoneno, password;
    TextView forgetpassword, signup, signup_as_text;
    CheckBox rememberme;
    RelativeLayout loginbtn, progressbar, signup_as;
    CountryCodePicker countryCodePicker;
    TextInputEditText phonenoEditText, passwordEditText;


    float v = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //hooks
        backbtn = findViewById(R.id.backbtn);
        countryCodePicker = findViewById(R.id.countryCodePicker);
        phoneno = findViewById(R.id.Phoneno);
        password = findViewById(R.id.password);
        forgetpassword = findViewById(R.id.forgetpassword);
        signup = findViewById(R.id.Signupbtn);
        rememberme = findViewById(R.id.RememberMe);
        loginbtn = findViewById(R.id.Loginbtn);
        progressbar = findViewById(R.id.progressBar);
        phonenoEditText = findViewById(R.id.PhonenoEditText);
        passwordEditText = findViewById(R.id.PasswordEditText);
        signup_as = findViewById(R.id.relativeLayout2);
        signup_as_text = findViewById(R.id.signup_as_text);

        progressbar.setVisibility(View.GONE);


        //Listeners
        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Login.this, Welcome_To_Login_Signup.class);
                startActivity(intent);
                finish();

            }
        });

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Login.this, Signup_1.class);
                startActivity(intent);
                finish();
            }
        });


        loginbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Objects.requireNonNull(phoneno.getEditText()).getText().toString().equals("8108655480") && password.getEditText().getText().toString().equals("Pratham") ) {
                    //Create Session
                    //Create Session
                    SessionManager sessionManager1 = new SessionManager(Login.this, SessionManager.SESSION_FORWHO);
                    sessionManager1.creatingForWhomSession("forAdmin");

                    SessionManager sessionManager2 = new SessionManager(Login.this, SessionManager.SESSION_ADMIN);
                    sessionManager2.creatingAdminSession();

                    //Checkbox
                    if (rememberme.isChecked()) {
                        SessionManager sessionManager = new SessionManager(Login.this, SessionManager.SESSION_REMEMBERME);
                        sessionManager.creatingRememberMeSession("8108655480", "Pratham");

                    }

                    startActivity(new Intent(Login.this,Admin_Show_Product.class));
                    finishAffinity();
                }
                else if(signup_as_text.getText().toString().equalsIgnoreCase("User")) {
                    letUserLoggedIn();
                }
                else{
                    letSellerLoggedIn();
                }
            }
        });

        forgetpassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(signup_as_text.getText().toString().equalsIgnoreCase("User") ){
                    Intent intent = new Intent(Login.this, ForgetPassword.class);
                    intent.putExtra("forWhom","user");
                    startActivity(intent);
                }
                else{
                    Intent intent = new Intent(Login.this, ForgetPassword.class);
                    intent.putExtra("forWhom","seller");
                    startActivity(intent);
                }

            }
        });

        signup_as.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(v.getContext(), v);
                popupMenu.inflate(R.menu.signup_as_menu);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.nav_user:
                                signup_as_text.setText("User");
                                break;

                            case R.id.nav_seller:
                                signup_as_text.setText("Seller");
                                break;
                        }
                        return false;
                    }
                });
                popupMenu.show();

            }
        });


        //Animation
        signup_as.setTranslationX(800);
        countryCodePicker.setTranslationX(800);
        phoneno.setTranslationX(800);
        password.setTranslationX(800);
        forgetpassword.setTranslationX(800);
        rememberme.setTranslationX(800);
        loginbtn.setTranslationX(800);
        signup.setTranslationX(800);

        signup_as.setAlpha(v);
        countryCodePicker.setAlpha(v);
        phoneno.setAlpha(v);
        password.setAlpha(v);
        forgetpassword.setAlpha(v);
        rememberme.setAlpha(v);
        loginbtn.setAlpha(v);
        signup.setAlpha(v);

        signup_as.animate().translationX(0).alpha(1).setDuration(800).setStartDelay(100).start();
        countryCodePicker.animate().translationX(0).alpha(1).setDuration(800).setStartDelay(300).start();
        phoneno.animate().translationX(0).alpha(1).setDuration(800).setStartDelay(500).start();
        password.animate().translationX(0).alpha(1).setDuration(800).setStartDelay(700).start();
        forgetpassword.animate().translationX(0).alpha(1).setDuration(800).setStartDelay(900).start();
        rememberme.animate().translationX(0).alpha(1).setDuration(800).setStartDelay(1100).start();
        loginbtn.animate().translationX(0).alpha(1).setDuration(800).setStartDelay(1300).start();
        signup.animate().translationX(0).alpha(1).setDuration(800).setStartDelay(1300).start();

        //Check if RememberMe isChecked or not
        SessionManager sessionManager = new SessionManager(Login.this, SessionManager.SESSION_REMEMBERME);
        if (sessionManager.checkRememberMe()) {
            HashMap<String, String> rememberMeDetails = sessionManager.getRememberMeDetailFromSession();
            phonenoEditText.setText(rememberMeDetails.get(SessionManager.KEY_REMEMBERPHONENO));
            passwordEditText.setText(rememberMeDetails.get(SessionManager.KEY_REMEMBERPASSWORD));
            rememberme.setChecked(true);

        }
    }

    private void letSellerLoggedIn() {
        //CheckInternetConnection
        if (!isConnectedToInternet(this)) {
            startActivity(new Intent(Login.this, NoInternet.class));
            finish();
        }

        //Validation
        if (!validatePhone() | !validatePassword()) {
            return;
        }

        //Progress
        progressbar.setVisibility(View.VISIBLE);
        //


        //Set data
        String _phoneNo = phoneno.getEditText().getText().toString().trim();
        if (_phoneNo.charAt(0) == 0) {
            _phoneNo = _phoneNo.substring(1);
        }
        String _CompletePhoneno = "+" + countryCodePicker.getSelectedCountryCode() + _phoneNo;
        String _password = password.getEditText().getText().toString().trim();

        //Checkbox
        if (rememberme.isChecked()) {
            SessionManager sessionManager = new SessionManager(Login.this, SessionManager.SESSION_REMEMBERME);
            sessionManager.creatingRememberMeSession(_phoneNo, _password);

        }

        //VerifySeller
        Query checkseller = FirebaseDatabase.getInstance().getReference("Sellers").orderByChild("phoneno").equalTo(_CompletePhoneno);
        checkseller.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    phoneno.setError(null);
                    phoneno.setErrorEnabled(false);

                    String systemPassword = snapshot.child(_CompletePhoneno).child("password").getValue(String.class);
                    if (systemPassword.equals(_password)) {
                        password.setError(null);
                        password.setErrorEnabled(false);

                        String systemFirstname = snapshot.child(_CompletePhoneno).child("firstname").getValue(String.class);
                        String systemLastname = snapshot.child(_CompletePhoneno).child("lastname").getValue(String.class);
                        String systemShopname = snapshot.child(_CompletePhoneno).child("shopname").getValue(String.class);
                        String systemShopAdd = snapshot.child(_CompletePhoneno).child("shopadd").getValue(String.class);
                        String systemDelFee = snapshot.child(_CompletePhoneno).child("delfee").getValue(String.class);
                        String systemEmail = snapshot.child(_CompletePhoneno).child("email").getValue(String.class);
                        String systemPhoneno = snapshot.child(_CompletePhoneno).child("phoneno").getValue(String.class);

                        FirebaseDatabase rootnode = FirebaseDatabase.getInstance();
                        DatabaseReference reference = rootnode.getReference("Sellers");
                        reference.child(systemPhoneno).child("isOpen").setValue(true);

                        //Create Session
                        SessionManager sessionManager1 = new SessionManager(Login.this, SessionManager.SESSION_FORWHO);
                        sessionManager1.creatingForWhomSession("forSeller");

                        SessionManager sessionManager2 = new SessionManager(Login.this, SessionManager.SESSION_SELLER);
                        sessionManager2.creatingSellerSession(systemFirstname, systemLastname, systemShopname,systemShopAdd,systemDelFee, systemEmail, systemPassword, systemPhoneno);

                        SessionManager sessionManager3 = new SessionManager(Login.this, SessionManager.SESSION_ADDRESS);
                        sessionManager3.creatingAddressSession(systemShopAdd);

                        Intent intent = new Intent(getApplicationContext(), AdminShowOrderedProducts.class);
                        FirebaseAuth.getInstance().signInAnonymously();
                        startActivity(intent);
                        finishAffinity();
                    } else {
                        //Progress
                        progressbar.setVisibility(View.GONE);
                        //
                        Toast.makeText(Login.this, "Incorrect password", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    //Progress
                    progressbar.setVisibility(View.GONE);
                    //
                    Toast.makeText(Login.this, "No such seller exist", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                //Progress
                progressbar.setVisibility(View.GONE);
                //
                Toast.makeText(Login.this, "Login failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void letUserLoggedIn() {
        //CheckInternetConnection
        if (!isConnectedToInternet(this)) {
            startActivity(new Intent(Login.this, NoInternet.class));
            finish();
        }

        //Validation
        if (!validatePhone() | !validatePassword()) {
            return;
        }

        //Progress
        progressbar.setVisibility(View.VISIBLE);
        //


        //Set data
        String _phoneNo = phoneno.getEditText().getText().toString().trim();
        if (_phoneNo.charAt(0) == 0) {
            _phoneNo = _phoneNo.substring(1);
        }
        String _CompletePhoneno = "+" + countryCodePicker.getSelectedCountryCode() + _phoneNo;
        String _password = password.getEditText().getText().toString().trim();

        //Checkbox
        if (rememberme.isChecked()) {
            SessionManager sessionManager = new SessionManager(Login.this, SessionManager.SESSION_REMEMBERME);
            sessionManager.creatingRememberMeSession(_phoneNo, _password);

        }

        //VerifyUser
        Query checkuser = FirebaseDatabase.getInstance().getReference("Users").orderByChild("phoneno").equalTo(_CompletePhoneno);
        checkuser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    phoneno.setError(null);
                    phoneno.setErrorEnabled(false);

                    String systemPassword = snapshot.child(_CompletePhoneno).child("password").getValue(String.class);
                    if (systemPassword.equals(_password)) {
                        password.setError(null);
                        password.setErrorEnabled(false);

                        String systemFirstname = snapshot.child(_CompletePhoneno).child("firstname").getValue(String.class);
                        String systemLastname = snapshot.child(_CompletePhoneno).child("lastname").getValue(String.class);
                        String systemEmail = snapshot.child(_CompletePhoneno).child("email").getValue(String.class);
                        String systemPhoneno = snapshot.child(_CompletePhoneno).child("phoneno").getValue(String.class);
                        String systemAdd = snapshot.child(_CompletePhoneno).child("useradd").getValue(String.class);


                        //Create Session
                        SessionManager sessionManager1 = new SessionManager(Login.this, SessionManager.SESSION_FORWHO);
                        sessionManager1.creatingForWhomSession("forUser");

                        SessionManager sessionManager = new SessionManager(Login.this, SessionManager.SESSION_USER);
                        sessionManager.creatingUserSession(systemFirstname, systemLastname, systemEmail, systemPassword, systemPhoneno);

                        SessionManager sessionManager2 = new SessionManager(Login.this, SessionManager.SESSION_ADDRESS);
                        sessionManager2.creatingAddressSession(systemAdd);

                        Intent intent = new Intent(getApplicationContext(), Dashboard.class);
                        FirebaseAuth.getInstance().signInAnonymously();
                        startActivity(intent);
                        finishAffinity();
                    } else {
                        //Progress
                        progressbar.setVisibility(View.GONE);
                        //
                        Toast.makeText(Login.this, "Incorrect password", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    //Progress
                    progressbar.setVisibility(View.GONE);
                    //
                    Toast.makeText(Login.this, "No such user exist", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                //Progress
                progressbar.setVisibility(View.GONE);
                //
                Toast.makeText(Login.this, "Login failed", Toast.LENGTH_SHORT).show();
            }
        });


    }

    private boolean isConnectedToInternet(Login login) {
        ConnectivityManager connectivityManager = (ConnectivityManager) login.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wificon = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mobilecon = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

        if ((wificon != null && wificon.isConnected()) || (mobilecon != null && mobilecon.isConnected())) {
            return true;
        } else {
            return false;
        }
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

    private boolean validatePassword() {
        String val = Objects.requireNonNull(password.getEditText()).getText().toString().trim();
        if (val.isEmpty()) {
            password.setError("Empty");
            return false;
        } else {
            password.setError(null);
            password.requestFocus();
            return true;
        }
    }
}