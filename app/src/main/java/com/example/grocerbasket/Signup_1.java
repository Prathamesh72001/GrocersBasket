package com.example.grocerbasket;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.textfield.TextInputLayout;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class Signup_1 extends AppCompatActivity {

    ImageView backbtn;
    TextInputLayout firstname, lastname, email,password,shopname, shopadd, delfee;
    RelativeLayout nextbtn,signupas, currLocBtn,loc;
    TextView login,signup_as_text;
    FusedLocationProviderClient fusedLocationProviderClient;
    float v = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup1);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //hooks
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        backbtn = findViewById(R.id.backBtn);
        firstname=findViewById(R.id.firstname);
        lastname=findViewById(R.id.lastname);
        shopname = findViewById(R.id.shopname);
        shopadd = findViewById(R.id.shopadd);
        delfee = findViewById(R.id.delfee);
        password=findViewById(R.id.password);
        email = findViewById(R.id.email);
        nextbtn = findViewById(R.id.Signupbtn);
        login = findViewById(R.id.login);
        signupas=findViewById(R.id.relativeLayout2);
        signup_as_text=findViewById(R.id.signup_as_text);
        currLocBtn = findViewById(R.id.currentLocBtn);
        loc=findViewById(R.id.loc);

        //Visibility
        shopname.setVisibility(View.GONE);
        delfee.setVisibility(View.GONE);
        loc.setVisibility(View.GONE);

        //Listeners
        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Signup_1.this, Welcome_To_Login_Signup.class);
                startActivity(intent);
                finish();
            }
        });

        nextbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(signup_as_text.getText().toString().equalsIgnoreCase("User")) {
                    callNextSignUpScreen_as_User();
                }
                else{
                    callNextSignUpScreen_as_Seller();
                }
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Signup_1.this, Login.class);
                startActivity(intent);
                finish();
            }
        });

        currLocBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getCurrentLocation();
            }
        });

        signupas.setOnClickListener(new View.OnClickListener() {
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
                                shopname.setVisibility(View.GONE);
                                delfee.setVisibility(View.GONE);
                                loc.setVisibility(View.GONE);
                                break;

                            case R.id.nav_seller:
                                signup_as_text.setText("Seller");
                                shopname.setVisibility(View.VISIBLE);
                                delfee.setVisibility(View.VISIBLE);
                                loc.setVisibility(View.VISIBLE);
                                break;
                        }
                        return false;
                    }
                });
                popupMenu.show();
            }
        });

        //Animation
        signupas.setTranslationX(800);
        firstname.setTranslationX(800);
        lastname.setTranslationX(800);
        email.setTranslationX(800);
        password.setTranslationX(800);
        login.setTranslationX(800);
        nextbtn.setTranslationX(800);

        signupas.setAlpha(v);
        firstname.setAlpha(v);
        lastname.setAlpha(v);
        email.setAlpha(v);
        password.setAlpha(v);
        login.setAlpha(v);
        nextbtn.setAlpha(v);

        signupas.animate().translationX(0).alpha(1).setDuration(800).setStartDelay(100).start();
        firstname.animate().translationX(0).alpha(1).setDuration(800).setStartDelay(300).start();
        lastname.animate().translationX(0).alpha(1).setDuration(800).setStartDelay(500).start();
        email.animate().translationX(0).alpha(1).setDuration(800).setStartDelay(700).start();
        password.animate().translationX(0).alpha(1).setDuration(800).setStartDelay(900).start();
        login.animate().translationX(0).alpha(1).setDuration(800).setStartDelay(1100).start();
        nextbtn.animate().translationX(0).alpha(1).setDuration(800).setStartDelay(1100).start();
    }

    @SuppressLint("MissingPermission")
    private void getCurrentLocation() {
        fusedLocationProviderClient.getLastLocation().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Location location = task.getResult();
                if (location != null) {

                    try {
                        Geocoder geocoder = new Geocoder(Signup_1.this, Locale.getDefault());
                        List<Address> addressList = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                        Objects.requireNonNull(shopadd.getEditText()).setText(addressList.get(0).getAddressLine(0));
                    } catch (IOException e) {
                        Toast.makeText(Signup_1.this,"Unable to detect current location",Toast.LENGTH_SHORT).show();

                    }
                }
                else
                {
                    Toast.makeText(Signup_1.this,"Unable to detect current location",Toast.LENGTH_SHORT).show();
                }
            }
            else
            {
                Toast.makeText(Signup_1.this,"Unable to detect current location",Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void callNextSignUpScreen_as_User() {
        if (!validateFirstname() | !validateLastname() | !validateEmail() | !validatePassword() ) {
            return;
        }
        String firstName = Objects.requireNonNull(firstname.getEditText()).getText().toString().trim();
        String lastName = Objects.requireNonNull(lastname.getEditText()).getText().toString().trim();
        String eMail = Objects.requireNonNull(email.getEditText()).getText().toString().trim();
        String pwd = Objects.requireNonNull(password.getEditText()).getText().toString().trim();


        Intent intent = new Intent(getApplicationContext(), Signup_2.class);
        intent.putExtra("firstname", firstName);
        intent.putExtra("lastname", lastName);
        intent.putExtra("email", eMail);
        intent.putExtra("password", pwd);
        startActivity(intent);
        finish();
    }

    private void callNextSignUpScreen_as_Seller() {
        if (!validateFirstname() | !validateLastname() | !validateShopname() | !validateShopadd() | !validateDelfee() | !validateEmail() | !validatePassword()) {
            return;
        }
        String firstName = Objects.requireNonNull(firstname.getEditText()).getText().toString().trim();
        String lastName = Objects.requireNonNull(lastname.getEditText()).getText().toString().trim();
        String shopName = Objects.requireNonNull(shopname.getEditText()).getText().toString().trim();
        String shopAdd = Objects.requireNonNull(shopadd.getEditText()).getText().toString().trim();
        String delFee = Objects.requireNonNull(delfee.getEditText()).getText().toString().trim();
        String eMail = Objects.requireNonNull(email.getEditText()).getText().toString().trim();
        String pwd = Objects.requireNonNull(password.getEditText()).getText().toString().trim();


        Intent intent = new Intent(getApplicationContext(), Signup_2.class);
        intent.putExtra("forWhat", "forSeller");
        intent.putExtra("firstname", firstName);
        intent.putExtra("lastname", lastName);
        intent.putExtra("shopname", shopName);
        intent.putExtra("shopadd", shopAdd);
        intent.putExtra("delfee", delFee);
        intent.putExtra("email", eMail);
        intent.putExtra("password", pwd);
        startActivity(intent);
        finish();
    }

    //Validation
    private boolean validateFirstname() {
        String val = Objects.requireNonNull(firstname.getEditText()).getText().toString().trim();
        if (val.isEmpty()) {
            firstname.setError("Empty");
            return false;
        } else {
            firstname.setError(null);
            firstname.requestFocus();
            return true;
        }
    }

    private boolean validateLastname() {
        String val = Objects.requireNonNull(lastname.getEditText()).getText().toString().trim();
        if (val.isEmpty()) {
            lastname.setError("Empty");
            return false;
        } else {
            lastname.setError(null);
            lastname.requestFocus();
            return true;
        }
    }

    private boolean validateShopname() {
        String val = Objects.requireNonNull(shopname.getEditText()).getText().toString().trim();
        if (val.isEmpty()) {
            shopname.setError("Empty");
            return false;
        } else {
            shopname.setError(null);
            shopname.requestFocus();
            return true;
        }
    }

    private boolean validateShopadd() {
        String val = Objects.requireNonNull(shopadd.getEditText()).getText().toString().trim();
        if (val.isEmpty()) {
            shopadd.setError("Empty");
            return false;
        } else {
            shopadd.setError(null);
            shopadd.requestFocus();
            return true;
        }
    }

    private boolean validateDelfee() {
        String val = Objects.requireNonNull(delfee.getEditText()).getText().toString().trim();
        if (val.isEmpty()) {
            delfee.setError("Empty");
            return false;
        } else {
            delfee.setError(null);
            delfee.requestFocus();
            return true;
        }
    }

    private boolean validateEmail() {
        String val = Objects.requireNonNull(email.getEditText()).getText().toString().trim();
        String checkEmail = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        if (val.isEmpty()) {
            email.setError("Empty");
            return false;
        } else if (!val.matches(checkEmail)) {
            email.setError("Invalid");
            return false;
        } else {
            email.setError(null);
            email.requestFocus();
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