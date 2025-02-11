package com.example.grocerbasket;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.core.widget.NestedScrollView;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.grocerbasket.Adapters.CartAdapter;
import com.example.grocerbasket.Adapters.ProductAdapter_Dashboard;
import com.example.grocerbasket.Constructors.CartHelperClass;
import com.example.grocerbasket.Constructors.CartProdHelperClass;
import com.example.grocerbasket.Constructors.ProductHelperClass;
import com.example.grocerbasket.Session.SessionManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class Categories extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    BottomNavigationView bottomNavigationView;
    String firstname, phoneNo, forWhom;
    RelativeLayout login_or_signup_btn, location, contentView,fruits,foodgrain,cleaning,beauty,snacks,bakery,bev;
    View header;
    Menu menu1;
    static final float END_SCALE = 0.7f;
    ImageView menu, search;
    TextView header_text, header_userLoctext;
    SessionManager UsersessionManager;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categories);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //hooks
        menu = findViewById(R.id.menu);
        search = findViewById(R.id.Search);
        contentView = findViewById(R.id.contentView);
        drawerLayout = findViewById(R.id.drawerlayout);
        navigationView = findViewById(R.id.navigation_view);
        bottomNavigationView = findViewById(R.id.bottom_nav);

        fruits=findViewById(R.id.friuts);
        foodgrain=findViewById(R.id.foodgrains);
        cleaning=findViewById(R.id.cleaning);
        beauty=findViewById(R.id.beauty);
        snacks=findViewById(R.id.snacks);
        bakery=findViewById(R.id.bakery);
        bev=findViewById(R.id.bev);

        //CheckInternetConnection
        if (!isConnectedToInternet(this)) {
            startActivity(new Intent(Categories.this, NoInternet.class));
            finish();
        }

        //Header
        header = navigationView.getHeaderView(0);
        login_or_signup_btn = header.findViewById(R.id.login_or_signup_btn);
        header_text = header.findViewById(R.id.header_text);
        location = header.findViewById(R.id.Location);
        header_userLoctext = header.findViewById(R.id.UserLocation);

        //Menus
        menu1 = navigationView.getMenu();
        menu1.findItem(R.id.nav_profile).setVisible(false);
        menu1.findItem(R.id.nav_logout).setVisible(false);
        menu1.findItem(R.id.nav_settings).setVisible(false);

        //Session
        SessionManager sessionManager = new SessionManager(this, SessionManager.SESSION_FORWHO);
        HashMap<String, String> forWhoDetails = sessionManager.getForWhomDetailFromSession();
        forWhom = forWhoDetails.get(SessionManager.KEY_FORWHO);

        if (Objects.requireNonNull(forWhom).equalsIgnoreCase("forUser")) {
            UsersessionManager = new SessionManager(this, SessionManager.SESSION_USER);
            HashMap<String, String> userDetails = UsersessionManager.getUserDetailFromSession();
            firstname = userDetails.get(SessionManager.KEY_FIRSTNAME);
            phoneNo = userDetails.get(SessionManager.KEY_PHONENO);

            //Listener


            boolean isLoggedIn = UsersessionManager.checkLogin();
            if (isLoggedIn) {
                //Visibilities
                header_text.setVisibility(View.VISIBLE);
                login_or_signup_btn.setVisibility(View.GONE);
                menu1.findItem(R.id.nav_profile).setVisible(true);
                menu1.findItem(R.id.nav_logout).setVisible(true);
                menu1.findItem(R.id.nav_settings).setVisible(true);

                //setText
                header_text.setText("Hello, " + firstname);

            }
        }

        SessionManager sessionManager1 = new SessionManager(this, SessionManager.SESSION_ADDRESS);
        HashMap<String, String> addDetails = sessionManager1.getAddressDetailFromSession();
        String address = addDetails.get(SessionManager.KEY_ADD);
        header_userLoctext.setText(address);

        //NavigationView & bottomNavigationView
        navigationView.bringToFront();
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.nav_category);


        bottomNavigationView.setSelectedItemId(R.id.nav_bot_cat);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id=item.getItemId();
                if(id==R.id.nav_bot_home){
                    startActivity(new Intent(Categories.this, Dashboard.class));
                    finishAffinity();
                }else if(id==R.id.nav_bot_search){
                    startActivity(new Intent(Categories.this, SearchProduct.class));
                    finish();
                }else if(id==R.id.nav_bot_list){
                    startActivity(new Intent(Categories.this,FavouriteActivity.class));
                    finish();
                }else if(id==R.id.nav_bot_basket){
                    startActivity(new Intent(Categories.this,CartActivity.class));
                    finish();
                }
                return false;
            }
        });


        //Animations
        drawerAnim();

        //Listeners
        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CloseDrawer();
            }
        });

        login_or_signup_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Categories.this, Welcome_To_Login_Signup.class));
            }
        });

        location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (forWhom.equalsIgnoreCase("forUser")) {
                    SessionManager sessionManager = new SessionManager(Categories.this, SessionManager.SESSION_USER);
                    boolean isLoggedIn = sessionManager.checkLogin();
                    if (isLoggedIn) {
                        //Listener
                        location.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                startActivity(new Intent(Categories.this, AddLocation.class));
                            }
                        });
                    }

                } else if (forWhom.equalsIgnoreCase("forSeller")) {
                    SessionManager sessionManager = new SessionManager(Categories.this, SessionManager.SESSION_SELLER);
                    boolean isLoggedIn = sessionManager.checkLogin();
                    if (isLoggedIn) {
                        //Listener
                        location.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                startActivity(new Intent(Categories.this, AddLocation.class));
                            }
                        });
                    }
                } else {
                    startActivity(new Intent(Categories.this, Welcome_To_Login_Signup.class));
                }
            }
        });

        //Categories
        fruits.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Categories.this,SubCategories.class);
                intent.putExtra("catName","Fruits");
                startActivity(intent);
            }
        });

        bakery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Categories.this,SubCategories.class);
                intent.putExtra("catName","Bakery");
                startActivity(intent);
            }
        });

        foodgrain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Categories.this,SubCategories.class);
                intent.putExtra("catName","Foodgrain");
                startActivity(intent);
            }
        });

        bev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Categories.this,SubCategories.class);
                intent.putExtra("catName","Beverages");
                startActivity(intent);
            }
        });

        beauty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Categories.this,SubCategories.class);
                intent.putExtra("catName","Beauty");
                startActivity(intent);
            }
        });

        cleaning.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Categories.this,SubCategories.class);
                intent.putExtra("catName","Cleaning");
                startActivity(intent);
            }
        });

        snacks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Categories.this,SubCategories.class);
                intent.putExtra("catName","Snacks");
                startActivity(intent);
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

    private boolean isConnectedToInternet(Categories dashboard) {
        ConnectivityManager connectivityManager = (ConnectivityManager) dashboard.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wificon = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mobilecon = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

        if ((wificon != null && wificon.isConnected()) || (mobilecon != null && mobilecon.isConnected())) {
            return true;
        } else {
            return false;
        }
    }

    //Navigation Func
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id=item.getItemId();
        if(id==R.id.nav_home){
            startActivity(new Intent(Categories.this, Dashboard.class));
            finishAffinity();
        }else if(id==R.id.nav_profile){
            Intent profile_intent;
            if (forWhom.equalsIgnoreCase("forUser")) {
                profile_intent = new Intent(Categories.this, UserProfile.class);
            } else {
                profile_intent = new Intent(Categories.this, SellerProfile.class);
            }
            startActivity(profile_intent);
        }else if(id==R.id.nav_settings){
            startActivity(new Intent(Categories.this,Settings.class));

        }else if(id==R.id.nav_logout){
            SessionManager sessionManager1 = new SessionManager(this, SessionManager.SESSION_FORWHO);
            sessionManager1.creatingForWhomSession("forWho");

            SessionManager sessionManager2 = new SessionManager(this, SessionManager.SESSION_ADDRESS);
            sessionManager2.creatingAddressSession("Swagath Rd-Tilaknagar,Banglore-560041");

            SessionManager sessionManager = new SessionManager(this, SessionManager.SESSION_USER);
            sessionManager.logout();
            Intent logout_intent = new Intent(Categories.this, Dashboard.class);
            startActivity(logout_intent);
            finishAffinity();
        }else if(id==R.id.nav_category){
            CloseDrawer();

        }else if(id==R.id.nav_basket){
            startActivity(new Intent(Categories.this,CartActivity.class));
            finish();
        }
        return true;
    }
}