package com.example.grocerbasket;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatSpinner;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import com.airbnb.lottie.L;
import com.example.grocerbasket.Constructors.CartHelperClass;
import com.example.grocerbasket.Session.SessionManager;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class AddLocation extends AppCompatActivity {

    RelativeLayout chooseCurrentLocBtn,addAddrBtn;
    TextInputEditText houseNo,apartNm,StDetails,Landmark,ArDetails,city,pinCd;
    String address,checkout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_location);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //hooks
        chooseCurrentLocBtn=findViewById(R.id.chooseCurrentLocBtn);
        houseNo=findViewById(R.id.HousenoEditText);
        apartNm=findViewById(R.id.AppartNameEditText);
        StDetails=findViewById(R.id.StreetDetailsEditText);
        Landmark =findViewById(R.id.LandmarkEditText);
        ArDetails=findViewById(R.id.AreaDetailsEditText);
        city=findViewById(R.id.CityEditText);
        pinCd=findViewById(R.id.PincodeEditText);
        addAddrBtn=findViewById(R.id.AddAddressbtn);
        checkout=getIntent().getStringExtra("checkout");

        //Sessions
        SessionManager sessionManager1 = new SessionManager(AddLocation.this, SessionManager.SESSION_FORWHO);
        HashMap<String, String> forWhoDetails = sessionManager1.getForWhomDetailFromSession();
        String forWhom=forWhoDetails.get(SessionManager.KEY_FORWHO);

        if("checkout".equals(checkout)){
            chooseCurrentLocBtn.setVisibility(View.GONE);
        }

        //Listeners
        chooseCurrentLocBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AddLocation.this,UserLocation.class));
            }
        });

        addAddrBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Validation
                if (!validateHouseNo() || !validateApartNm() || !validateStreetDet() || !validateAreaDet() || !validateCity() || !validatePincode()) {
                    return;
                }

                String house=houseNo.getText().toString();
                String apartment=apartNm.getText().toString();
                String street=StDetails.getText().toString();
                String landmark=Landmark.getText().toString();
                String area=ArDetails.getText().toString();
                String cit=city.getText().toString();
                String pincode=pinCd.getText().toString();
                if(!landmark.equals("")) {
                    address = house + ", " + apartment + ", " + street + ", " +landmark+", "+area+", "+cit+", "+pincode;
                }
                else
                {
                    address = house + ", " + apartment + ", " + street + ", "+area+", "+cit+", "+pincode;

                }

                if("checkout".equals(checkout)){
                    SessionManager sessionManager1 = new SessionManager(AddLocation.this, SessionManager.SESSION_USER);
                    HashMap<String, String> userDetails = sessionManager1.getUserDetailFromSession();
                    String phoneno = userDetails.get(SessionManager.KEY_PHONENO);

                    SessionManager sessionManager = new SessionManager(AddLocation.this, SessionManager.SESSION_ADDRESS);
                    sessionManager.creatingAddressSession(address);

                    FirebaseDatabase rootnode = FirebaseDatabase.getInstance();
                    DatabaseReference reference = rootnode.getReference("Users");
                    reference.child(phoneno).child("useradd").setValue(address);

                    startActivity(new Intent(AddLocation.this,Select_Shop.class));
                    finish();
                }else {

                    if (forWhom.equalsIgnoreCase("forSeller")) {
                        SessionManager sessionManager1 = new SessionManager(AddLocation.this, SessionManager.SESSION_SELLER);
                        HashMap<String, String> sellerDetails = sessionManager1.getSellerDetailFromSession();
                        String phoneno = sellerDetails.get(SessionManager.KEY_SELLERPHONENO);

                        SessionManager sessionManager = new SessionManager(AddLocation.this, SessionManager.SESSION_ADDRESS);
                        sessionManager.creatingAddressSession(address);

                        FirebaseDatabase rootnode = FirebaseDatabase.getInstance();
                        DatabaseReference reference = rootnode.getReference("Sellers");
                        reference.child(phoneno).child("shopadd").setValue(address);

                        startActivity(new Intent(AddLocation.this, Dashboard.class));
                        finishAffinity();
                    } else if (forWhom.equalsIgnoreCase("forUser")) {
                        SessionManager sessionManager1 = new SessionManager(AddLocation.this, SessionManager.SESSION_USER);
                        HashMap<String, String> userDetails = sessionManager1.getUserDetailFromSession();
                        String phoneno = userDetails.get(SessionManager.KEY_PHONENO);

                        SessionManager sessionManager = new SessionManager(AddLocation.this, SessionManager.SESSION_ADDRESS);
                        sessionManager.creatingAddressSession(address);

                        FirebaseDatabase rootnode = FirebaseDatabase.getInstance();
                        DatabaseReference reference = rootnode.getReference("Users");
                        reference.child(phoneno).child("useradd").setValue(address);

                        startActivity(new Intent(AddLocation.this, Dashboard.class));
                        finishAffinity();
                    }
                }
            }
        });


    }


    //validation
    private boolean validateHouseNo() {
        String val = houseNo.getText().toString().trim();
        if (val.isEmpty()) {
            houseNo.setError("Empty");
            return false;
        } else {
            houseNo.setError(null);
            houseNo.requestFocus();
            return true;
        }
    }

    private boolean validateApartNm() {
        String val = apartNm.getText().toString().trim();
        if (val.isEmpty()) {
            apartNm.setError("Empty");
            return false;
        } else {
            apartNm.setError(null);
            apartNm.requestFocus();
            return true;
        }
    }

    private boolean validateStreetDet() {
        String val = StDetails.getText().toString().trim();
        if (val.isEmpty()) {
            StDetails.setError("Empty");
            return false;
        } else {
            StDetails.setError(null);
            StDetails.requestFocus();
            return true;
        }
    }

    private boolean validateAreaDet() {
        String val = ArDetails.getText().toString().trim();
        if (val.isEmpty()) {
            ArDetails.setError("Empty");
            return false;
        } else {
            ArDetails.setError(null);
            ArDetails.requestFocus();
            return true;
        }
    }

    private boolean validateCity() {
        String val = city.getText().toString().trim();
        if (val.isEmpty()) {
            city.setError("Empty");
            return false;
        } else {
            city.setError(null);
            city.requestFocus();
            return true;
        }
    }

    private boolean validatePincode() {
        String val = pinCd.getText().toString().trim();
        if (val.isEmpty()) {
            pinCd.setError("Empty");
            return false;
        } else {
            pinCd.setError(null);
            pinCd.requestFocus();
            return true;
        }
    }
}