package com.example.grocerbasket;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.Toast;

import com.example.grocerbasket.Adapters.FavouriteAdapter;
import com.example.grocerbasket.Adapters.ShopsAdapter;
import com.example.grocerbasket.Constructors.ProductHelperClass;
import com.example.grocerbasket.Constructors.SellerHelperClass;
import com.example.grocerbasket.Session.SessionManager;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.razorpay.PaymentResultListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class Select_Shop extends AppCompatActivity implements PaymentResultListener {

    RecyclerView recyclerView;
    ArrayList<SellerHelperClass> sellerHelperClasses;
    RecyclerView.Adapter adapter;
    double lat1,lon1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_shop);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        recyclerView=findViewById(R.id.recyclerView);

        //CheckInternetConnection
        if (!isConnectedToInternet(this)) {
            startActivity(new Intent(Select_Shop.this, NoInternet.class));
            finish();
        }


        //Methods
        CalculateDistance();
        LoadShops();
    }

    private void CalculateDistance() {
        SessionManager sessionManager1 = new SessionManager(this, SessionManager.SESSION_ADDRESS);
        HashMap<String, String> addDetails = sessionManager1.getAddressDetailFromSession();
        String address = addDetails.get(SessionManager.KEY_ADD);
        Geocoder geocoder=new Geocoder(Select_Shop.this, Locale.getDefault());

        try {
            List addList=geocoder.getFromLocationName(address,1);
            if (addList!=null && addList.size()>0){
                Address Maddress=(Address)addList.get(0);
                lat1= Maddress.getLatitude();
                lon1= Maddress.getLongitude();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void LoadShops() {
        sellerHelperClasses= new ArrayList<>();
        FirebaseDatabase.getInstance().getReference("Sellers").orderByChild("phoneno").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    sellerHelperClasses.clear();
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        SellerHelperClass sellerHelperClass = ds.getValue(SellerHelperClass.class);
                        sellerHelperClasses.add(sellerHelperClass);
                    }
                    adapter = new ShopsAdapter(Select_Shop.this, sellerHelperClasses);
                    recyclerView.setAdapter(adapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private boolean isConnectedToInternet(Select_Shop dashboard) {
        ConnectivityManager connectivityManager = (ConnectivityManager) dashboard.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wificon = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mobilecon = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

        if ((wificon != null && wificon.isConnected()) || (mobilecon != null && mobilecon.isConnected())) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void onPaymentSuccess(String s) {
        Toast.makeText(Select_Shop.this,"Bill Paid Successfully",Toast.LENGTH_LONG).show();
    }

    @Override
    public void onPaymentError(int i, String s) {
        Toast.makeText(Select_Shop.this,"Something Went Wrong :"+s,Toast.LENGTH_LONG).show();
    }
}