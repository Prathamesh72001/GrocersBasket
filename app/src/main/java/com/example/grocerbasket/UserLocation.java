package com.example.grocerbasket;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;

import com.example.grocerbasket.Constructors.SellerHelperClass;
import com.example.grocerbasket.Session.SessionManager;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class UserLocation extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    boolean isPermissionGranted;
    TextView locationText;
    FusedLocationProviderClient fusedLocationProviderClient;
    GoogleMap mgoogleMap;
    RelativeLayout useLocBtn, currLocBtn;
    SupportMapFragment supportMapFragment;
    SearchView searchView;
    private int GPS_REQUEST_CODE = 9001;
    String checkout;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_location);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //hooks
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        locationText = findViewById(R.id.Location);
        useLocBtn = findViewById(R.id.useLocbtn);
        searchView = findViewById(R.id.searchArea);
        currLocBtn = findViewById(R.id.currentLocBtn);
        checkout=getIntent().getStringExtra("checkout");

        //Session
        SessionManager sessionManager = new SessionManager(UserLocation.this, SessionManager.SESSION_ADDRESS);
        HashMap<String, String> addDetails = sessionManager.getAddressDetailFromSession();
        String address = addDetails.get(SessionManager.KEY_ADD);
        locationText.setText(address);

        SessionManager sessionManager1 = new SessionManager(UserLocation.this, SessionManager.SESSION_FORWHO);
        HashMap<String, String> forWhoDetails = sessionManager1.getForWhomDetailFromSession();
        String forWhom=forWhoDetails.get(SessionManager.KEY_FORWHO);

        //Methods
        checkMyPermission();
        initMaps();


        //Listeners
        useLocBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if("checkout".equals(checkout)) {
                    SessionManager sessionManager1 = new SessionManager(UserLocation.this, SessionManager.SESSION_USER);
                    HashMap<String, String> userDetails = sessionManager1.getUserDetailFromSession();
                    String phoneno = userDetails.get(SessionManager.KEY_PHONENO);

                    String address = locationText.getText().toString();
                    SessionManager sessionManager = new SessionManager(UserLocation.this, SessionManager.SESSION_CURRLOC);
                    sessionManager.creatingCurrLocSession(address);

                    FirebaseDatabase rootnode = FirebaseDatabase.getInstance();
                    DatabaseReference reference = rootnode.getReference("Users");
                    reference.child(phoneno).child("usercurrloc").setValue(address);

                    SessionManager sessionManager2 = new SessionManager(UserLocation.this, SessionManager.SESSION_ADDRESS);
                    HashMap<String, String> addDetails=sessionManager2.getAddressDetailFromSession();
                    String add = addDetails.get(SessionManager.KEY_ADD);
                    if(add.equals("Swagath Rd-Tilaknagar,Banglore-560041")){
                        Intent intent=new Intent(UserLocation.this,AddLocation.class);
                        intent.putExtra("checkout","checkout");
                        startActivity(intent);
                        finish();
                    }
                    else{
                        AdddetailsBottomSheet(add);
                    }
                }else{

                    if (forWhom.equalsIgnoreCase("forSeller")) {
                        SessionManager sessionManager1 = new SessionManager(UserLocation.this, SessionManager.SESSION_SELLER);
                        HashMap<String, String> sellerDetails = sessionManager1.getSellerDetailFromSession();
                        String phoneno = sellerDetails.get(SessionManager.KEY_SELLERPHONENO);

                        String address = locationText.getText().toString();
                        SessionManager sessionManager = new SessionManager(UserLocation.this, SessionManager.SESSION_ADDRESS);
                        sessionManager.creatingAddressSession(address);

                        FirebaseDatabase rootnode = FirebaseDatabase.getInstance();
                        DatabaseReference reference = rootnode.getReference("Sellers");
                        reference.child(phoneno).child("shopadd").setValue(address);

                        startActivity(new Intent(UserLocation.this, Dashboard.class));
                        finishAffinity();
                    } else if (forWhom.equalsIgnoreCase("forUser")) {
                        SessionManager sessionManager1 = new SessionManager(UserLocation.this, SessionManager.SESSION_USER);
                        HashMap<String, String> userDetails = sessionManager1.getUserDetailFromSession();
                        String phoneno = userDetails.get(SessionManager.KEY_PHONENO);

                        String address = locationText.getText().toString();
                        SessionManager sessionManager = new SessionManager(UserLocation.this, SessionManager.SESSION_CURRLOC);
                        sessionManager.creatingCurrLocSession(address);

                        FirebaseDatabase rootnode = FirebaseDatabase.getInstance();
                        DatabaseReference reference = rootnode.getReference("Users");
                        reference.child(phoneno).child("usercurrloc").setValue(address);

                        startActivity(new Intent(UserLocation.this, Dashboard.class));
                        finishAffinity();
                    }
                }

            }
        });

        searchView.setFocusable(false);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                String location = searchView.getQuery().toString();
                List<Address> addressList = null;

                if (location != null || !location.equals("")) {
                    Geocoder geocoder = new Geocoder(UserLocation.this);
                    try {
                        addressList = geocoder.getFromLocationName(location, 1);

                    } catch (IOException e) {
                        Toast.makeText(UserLocation.this,"Unable to detect current location",Toast.LENGTH_SHORT).show();
                    }

                    try {
                        Address address = addressList.get(0);
                        locationText.setText(address.getAddressLine(0));
                        LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
                        mgoogleMap.addMarker(new MarkerOptions().position(latLng).title(location));
                        mgoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18));
                    } catch (IndexOutOfBoundsException e) {
                        Toast.makeText(UserLocation.this, "Unable to detect location", Toast.LENGTH_SHORT).show();
                    }
                    catch (NullPointerException e){
                        Toast.makeText(UserLocation.this, "Unable to detect location", Toast.LENGTH_SHORT).show();
                    }
                }
                else
                {
                    Toast.makeText(UserLocation.this, "Unable to detect location", Toast.LENGTH_SHORT).show();
                }
                return false;
            }

            

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        currLocBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getCurrentLocation();
            }
        });
    }

    @SuppressLint("SetTextI18n")
    private void AdddetailsBottomSheet(String addDetails) {
        BottomSheetDialog bottomSheetDialog=new BottomSheetDialog(UserLocation.this);
        View view= LayoutInflater.from(UserLocation.this).inflate(R.layout.bs_add_details_user,null);
        bottomSheetDialog.setContentView(view);

        TextView userLoc=view.findViewById(R.id.UserLocation);
        RelativeLayout location=view.findViewById(R.id.Location);
        RelativeLayout enterlocation=view.findViewById(R.id.EnterLocation);
        ImageView backBtn=view.findViewById(R.id.backbtn);

        //set data
        userLoc.setText(addDetails);

        //showDialogue
        bottomSheetDialog.show();

        //listeners
        location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog.dismiss();
                Intent intent=new Intent(UserLocation.this,Select_Shop.class);
                startActivity(intent);
                finish();
            }
        });

        enterlocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog.dismiss();
                Intent intent=new Intent(UserLocation.this,AddLocation.class);
                intent.putExtra("checkout","checkout");
                startActivity(intent);
                finish();
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog.dismiss();
            }
        });
    }


    private void initMaps() {
        if (isPermissionGranted) {
            if (isGPSEnabled()) {
                supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.frag);
                supportMapFragment.getMapAsync(this::onMapReady);
                getCurrentLocation();
            }
            else
            {
                Toast.makeText(UserLocation.this,"Unable to detect current location",Toast.LENGTH_SHORT).show();
            }
        }
    }


    private boolean isGPSEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        boolean providerEnable = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (providerEnable) {
            return true;
        } else {
            AlertDialog alertDialog = new AlertDialog.Builder(this).setTitle("GPS Permission").setMessage("GPS is required for this work. Please enable GPS").setPositiveButton("Yes", ((dialog, which) -> {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivityForResult(intent, GPS_REQUEST_CODE);
            })).setCancelable(false)
                    .show();
        }
        return false;
    }

    @SuppressLint("MissingPermission")
    private void getCurrentLocation() {
        fusedLocationProviderClient.getLastLocation().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                Location location = task.getResult();
                try {
                    gotoLocation(location.getLatitude(), location.getLongitude());
                    try {
                        Geocoder geocoder = new Geocoder(UserLocation.this, Locale.getDefault());
                        List<Address> addressList = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                        locationText.setText(addressList.get(0).getAddressLine(0));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                catch (NullPointerException e){
                    Toast.makeText(UserLocation.this,"Unable to detect location",Toast.LENGTH_SHORT).show();
                }

            }
            else
            {
                Toast.makeText(UserLocation.this,"Unable to detect current location",Toast.LENGTH_SHORT).show();
            }

        });

    }

    private void gotoLocation(double latitude, double longitude) {
        LatLng latLng = new LatLng(latitude, longitude);
        mgoogleMap.addMarker(new MarkerOptions().position(latLng).title(""));
        mgoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18));
        mgoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
    }


    private void checkMyPermission() {
        Dexter.withContext(this).withPermission(Manifest.permission.ACCESS_FINE_LOCATION).withListener(new PermissionListener() {
            @Override
            public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                isPermissionGranted = true;
            }

            @Override
            public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", getPackageName(), "");
                intent.setData(uri);
                startActivity(intent);
            }

            @Override
            public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                permissionToken.cancelPermissionRequest();
            }
        }).check();
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mgoogleMap = googleMap;

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GPS_REQUEST_CODE) {
            LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
            boolean providerEnable = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            if (providerEnable) {
                Toast.makeText(UserLocation.this, "GPS is enable", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(UserLocation.this, "GPS is not enable", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}