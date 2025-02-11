package com.example.grocerbasket;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.style.TypefaceSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.grocerbasket.Adapters.OrderAdpter;
import com.example.grocerbasket.Constructors.OrderHelperClass;
import com.example.grocerbasket.Session.SessionManager;
import com.example.grocerbasket.Typefaces.CustomTypefaceSpan;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class AdminShowOrderedProducts extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    ImageView menu, profile;
    RelativeLayout contentView, login_or_signup_btn, location;
    static final float END_SCALE = 0.7f;
    ArrayList<OrderHelperClass> orderHelperClasses;
    OrderAdpter orderAdapter;
    RecyclerView recyclerView;
    SessionManager SellersessionManager;
    String firstname, phoneNo, forWhom;
    View header;
    TextView header_text, header_userLoctext;
    Menu menu1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_show_ordered_products);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //hooks
        contentView = findViewById(R.id.contentView);
        drawerLayout = findViewById(R.id.drawerlayout);
        navigationView = findViewById(R.id.navigation_view);
        menu = findViewById(R.id.menu);
        recyclerView = findViewById(R.id.recyclerView);
        profile = findViewById(R.id.profile);

        //mathods
        loadOrders();

        //navigation
        navigationView.bringToFront();
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.nav_home);

        //Header
        header = navigationView.getHeaderView(0);
        login_or_signup_btn = header.findViewById(R.id.login_or_signup_btn);
        header_text = header.findViewById(R.id.header_text);
        location = header.findViewById(R.id.Location);
        header_userLoctext = header.findViewById(R.id.UserLocation);

        //Menus
        menu1 = navigationView.getMenu();
        menu1.findItem(R.id.nav_basket).setVisible(false);
        menu1.findItem(R.id.nav_category).setVisible(false);
        menu1.findItem(R.id.nav_settings).setVisible(false);

        //Session
        SellersessionManager = new SessionManager(this, SessionManager.SESSION_SELLER);
        HashMap<String, String> userDetails = SellersessionManager.getSellerDetailFromSession();
        firstname = userDetails.get(SessionManager.KEY_SELLERFIRSTNAME);


        SessionManager sessionManager1 = new SessionManager(this, SessionManager.SESSION_ADDRESS);
        HashMap<String, String> addDetails = sessionManager1.getAddressDetailFromSession();
        String address = addDetails.get(SessionManager.KEY_ADD);
        header_userLoctext.setText(address);

        SessionManager sessionManager = new SessionManager(this, SessionManager.SESSION_FORWHO);
        HashMap<String, String> forWhoDetails = sessionManager.getForWhomDetailFromSession();
        forWhom = forWhoDetails.get(SessionManager.KEY_FORWHO);

        //Listeners
        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CloseDrawer();
            }
        });

        location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (forWhom.equalsIgnoreCase("forUser")) {
                    SessionManager sessionManager = new SessionManager(AdminShowOrderedProducts.this, SessionManager.SESSION_USER);
                    boolean isLoggedIn = sessionManager.checkLogin();
                    if (isLoggedIn) {
                        //Listener
                        location.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                startActivity(new Intent(AdminShowOrderedProducts.this, AddLocation.class));
                            }
                        });
                    }

                } else if (forWhom.equalsIgnoreCase("forSeller")) {
                    SessionManager sessionManager = new SessionManager(AdminShowOrderedProducts.this, SessionManager.SESSION_SELLER);
                    boolean isLoggedIn = sessionManager.checkLogin();
                    if (isLoggedIn) {
                        //Listener
                        location.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                startActivity(new Intent(AdminShowOrderedProducts.this, AddLocation.class));
                            }
                        });
                    }
                } else {
                    startActivity(new Intent(AdminShowOrderedProducts.this, Welcome_To_Login_Signup.class));
                }
            }
        });

        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AdminShowOrderedProducts.this, SellerProfile.class));
            }
        });

        header_text.setVisibility(View.VISIBLE);
        login_or_signup_btn.setVisibility(View.GONE);

        //setText
        header_text.setText("Hello, " + firstname);

        //Animations
        drawerAnim();
    }

    private void loadOrders() {
        //Session
        SellersessionManager = new SessionManager(this, SessionManager.SESSION_SELLER);
        HashMap<String, String> userDetails = SellersessionManager.getSellerDetailFromSession();
        phoneNo = userDetails.get(SessionManager.KEY_SELLERPHONENO);

        orderHelperClasses = new ArrayList<>();
        //get All Products
        FirebaseDatabase.getInstance().getReference("Orders").orderByChild("orderto").equalTo(phoneNo).addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    orderHelperClasses.clear();
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        OrderHelperClass orderHelperClass = ds.getValue(OrderHelperClass.class);
                        orderHelperClasses.add(orderHelperClass);
                    }
                    orderAdapter = new OrderAdpter(AdminShowOrderedProducts.this, orderHelperClasses);
                    recyclerView.setAdapter(orderAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void CloseDrawer() {
        if (drawerLayout.isDrawerVisible(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            drawerLayout.openDrawer(GravityCompat.START);
        }
    }

    private void drawerAnim() {
        //Add any color or remove it to use the default one!
        //To make it transparent use Color.Transparent in side setScrimColor();
        //drawerLayout.setScrimColor(Color.TRANSPARENT);
        drawerLayout.addDrawerListener(new DrawerLayout.SimpleDrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {

                // Scale the View based on current slide offset
                final float diffScaledOffset = slideOffset * (1 - END_SCALE);
                final float offsetScale = 1 - diffScaledOffset;
                contentView.setScaleX(offsetScale);
                contentView.setScaleY(offsetScale);

                // Translate the View, accounting for the scaled width
                final float xOffset = drawerView.getWidth() * slideOffset;
                final float xOffsetDiff = contentView.getWidth() * diffScaledOffset / 2;
                final float xTranslation = xOffset - xOffsetDiff;
                contentView.setTranslationX(xTranslation);
            }


        });
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerVisible(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }

    }

    private boolean isConnectedToInternet(AdminShowOrderedProducts dashboard) {
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
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_home) {
            CloseDrawer();
        } else if (id == R.id.nav_logout) {
            SessionManager sessionManager1 = new SessionManager(AdminShowOrderedProducts.this, SessionManager.SESSION_FORWHO);
            sessionManager1.creatingForWhomSession("forWho");

            SessionManager sessionManager = new SessionManager(this, SessionManager.SESSION_USER);
            sessionManager.logout();
            Intent logout_intent = new Intent(AdminShowOrderedProducts.this, Dashboard.class);
            startActivity(logout_intent);
            finishAffinity();
        } else if (id == R.id.nav_profile) {
            startActivity(new Intent(AdminShowOrderedProducts.this, SellerProfile.class));
        }
        return true;
    }
}