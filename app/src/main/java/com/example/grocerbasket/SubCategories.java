package com.example.grocerbasket;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
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

import com.example.grocerbasket.Session.SessionManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

import java.util.HashMap;
import java.util.Objects;

public class SubCategories extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    BottomNavigationView bottomNavigationView;
    String firstname, phoneNo, forWhom;
    RelativeLayout login_or_signup_btn, location, contentView;
    RelativeLayout freshfruits,veg,dal,oil,flour,masala,salt,detergent,cleaner,skin,hair,deos
            ,bath,feminine,oral,men,repel,choc,noodle,biscuit,namkeen,spread,dairy,bread,rusk,
    cakes,tea,coffee,health,juices;
    View header;
    Menu menu1;
    static final float END_SCALE = 0.7f;
    ImageView menu, search;
    TextView header_text, header_userLoctext;
    String cat;
    SessionManager UsersessionManager, SellersessionManager;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub_categories);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //hooks
        menu = findViewById(R.id.menu);
        search = findViewById(R.id.Search);
        contentView = findViewById(R.id.contentView);
        drawerLayout = findViewById(R.id.drawerlayout);
        navigationView = findViewById(R.id.navigation_view);
        bottomNavigationView = findViewById(R.id.bottom_nav);
        cat=getIntent().getStringExtra("catName");

        freshfruits=findViewById(R.id.fresh_fruits);
        veg=findViewById(R.id.veg);
        dal=findViewById(R.id.dals_pulses);
        oil=findViewById(R.id.oil);
        flour=findViewById(R.id.flour);
        masala=findViewById(R.id.masala);
        salt=findViewById(R.id.salt_sugr_jaggery);
        detergent=findViewById(R.id.detergent_dishwash);
        cleaner=findViewById(R.id.cleaners);
        skin=findViewById(R.id.skin_care);
        hair=findViewById(R.id.hair_care);
        deos=findViewById(R.id.deos);
        bath=findViewById(R.id.bath);
        feminine=findViewById(R.id.feminine_hygiene);
        oral=findViewById(R.id.oral);
        men=findViewById(R.id.men_groom);
        repel=findViewById(R.id.repellent_freshener);
        choc=findViewById(R.id.choc);
        noodle=findViewById(R.id.noodle);
        biscuit=findViewById(R.id.biscuits);
        namkeen=findViewById(R.id.namkeen);
        spread=findViewById(R.id.spreads);
        dairy=findViewById(R.id.dairy);
        bread=findViewById(R.id.breads);
        rusk=findViewById(R.id.rusk);
        cakes=findViewById(R.id.cakes);
        tea=findViewById(R.id.tea);
        coffee=findViewById(R.id.coffee);
        health=findViewById(R.id.health);
        juices=findViewById(R.id.juices_drinks);

        //Sub Categories
        if(cat.equals("Fruits")){
            freshfruits.setVisibility(View.VISIBLE);
            veg.setVisibility(View.VISIBLE);

            freshfruits.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(SubCategories.this,Show_Products.class);
                    intent.putExtra("cat","Fruits & Vegetables");
                    intent.putExtra("subcat","Fresh Fruits");
                    startActivity(intent);
                }
            });

            veg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(SubCategories.this,Show_Products.class);
                    intent.putExtra("cat","Fruits & Vegetables");
                    intent.putExtra("subcat","Vegetables");
                    startActivity(intent);
                }
            });
        }
        else if (cat.equals("Bakery")){
            dairy.setVisibility(View.VISIBLE);
            bread.setVisibility(View.VISIBLE);
            rusk.setVisibility(View.VISIBLE);
            cakes.setVisibility(View.VISIBLE);

            dairy.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(SubCategories.this,Show_Products.class);
                    intent.putExtra("cat","Bakery, Cakes & Dairy");
                    intent.putExtra("subcat","Dairy");
                    startActivity(intent);
                }
            });

            bread.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(SubCategories.this,Show_Products.class);
                    intent.putExtra("cat","Bakery, Cakes & Dairy");
                    intent.putExtra("subcat","Breads & Buns");
                    startActivity(intent);
                }
            });

            rusk.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(SubCategories.this,Show_Products.class);
                    intent.putExtra("cat","Bakery, Cakes & Dairy");
                    intent.putExtra("subcat","Cookies,Rusk & Khari");
                    startActivity(intent);
                }
            });

            cakes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(SubCategories.this,Show_Products.class);
                    intent.putExtra("cat","Bakery, Cakes & Dairy");
                    intent.putExtra("subcat","Cakes & Pastries");
                    startActivity(intent);
                }
            });
        }
        else if(cat.equals("Foodgrain")){
            dal.setVisibility(View.VISIBLE);
            oil.setVisibility(View.VISIBLE);
            flour.setVisibility(View.VISIBLE);
            masala.setVisibility(View.VISIBLE);
            salt.setVisibility(View.VISIBLE);

            dal.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(SubCategories.this,Show_Products.class);
                    intent.putExtra("cat","Foodgrains, Oil & Masala");
                    intent.putExtra("subcat","Dals & Pulses");
                    startActivity(intent);
                }
            });

            oil.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(SubCategories.this,Show_Products.class);
                    intent.putExtra("cat","Foodgrains, Oil & Masala");
                    intent.putExtra("subcat","Cooking Oil");
                    startActivity(intent);
                }
            });

            flour.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(SubCategories.this,Show_Products.class);
                    intent.putExtra("cat","Foodgrains, Oil & Masala");
                    intent.putExtra("subcat","Flours & Grains");
                    startActivity(intent);
                }
            });

            masala.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(SubCategories.this,Show_Products.class);
                    intent.putExtra("cat","Foodgrains, Oil & Masala");
                    intent.putExtra("subcat","Masala & Spices");
                    startActivity(intent);
                }
            });

            salt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(SubCategories.this,Show_Products.class);
                    intent.putExtra("cat","Foodgrains, Oil & Masala");
                    intent.putExtra("subcat","Salt, Sugar & Jaggery");
                    startActivity(intent);
                }
            });
        }
        else if(cat.equals("Beverages"))
        {
            tea.setVisibility(View.VISIBLE);
            coffee.setVisibility(View.VISIBLE);
            health.setVisibility(View.VISIBLE);
            juices.setVisibility(View.VISIBLE);

            tea.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(SubCategories.this,Show_Products.class);
                    intent.putExtra("cat","Beverages");
                    intent.putExtra("subcat","Tea");
                    startActivity(intent);
                }
            });

            coffee.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(SubCategories.this,Show_Products.class);
                    intent.putExtra("cat","Beverages");
                    intent.putExtra("subcat","Coffee");
                    startActivity(intent);
                }
            });

            health.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(SubCategories.this,Show_Products.class);
                    intent.putExtra("cat","Beverages");
                    intent.putExtra("subcat","Health Drink");
                    startActivity(intent);
                }
            });

            juices.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(SubCategories.this,Show_Products.class);
                    intent.putExtra("cat","Beverages");
                    intent.putExtra("subcat","Fresh Juices & Drinks");
                    startActivity(intent);
                }
            });
        }
        else if(cat.equals("Beauty")){
            skin.setVisibility(View.VISIBLE);
            hair.setVisibility(View.VISIBLE);
            deos.setVisibility(View.VISIBLE);
            bath.setVisibility(View.VISIBLE);
            feminine.setVisibility(View.VISIBLE);
            oral.setVisibility(View.VISIBLE);
            men.setVisibility(View.VISIBLE);

            skin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(SubCategories.this,Show_Products.class);
                    intent.putExtra("cat","Beauty & Hygiene");
                    intent.putExtra("subcat","Skin Care");
                    startActivity(intent);
                }
            });

            hair.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(SubCategories.this,Show_Products.class);
                    intent.putExtra("cat","Beauty & Hygiene");
                    intent.putExtra("subcat","Hair Care");
                    startActivity(intent);
                }
            });

            deos.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(SubCategories.this,Show_Products.class);
                    intent.putExtra("cat","Beauty & Hygiene");
                    intent.putExtra("subcat","Fragrances & Deos");
                    startActivity(intent);
                }
            });

            bath.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(SubCategories.this,Show_Products.class);
                    intent.putExtra("cat","Beauty & Hygiene");
                    intent.putExtra("subcat","Bath & Hand Wash");
                    startActivity(intent);
                }
            });

            feminine.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(SubCategories.this,Show_Products.class);
                    intent.putExtra("cat","Beauty & Hygiene");
                    intent.putExtra("subcat","Feminine Hygiene");
                    startActivity(intent);
                }
            });

            oral.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(SubCategories.this,Show_Products.class);
                    intent.putExtra("cat","Beauty & Hygiene");
                    intent.putExtra("subcat","Oral Care");
                    startActivity(intent);
                }
            });

            men.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(SubCategories.this,Show_Products.class);
                    intent.putExtra("cat","Beauty & Hygiene");
                    intent.putExtra("subcat","Men's Grooming");
                    startActivity(intent);
                }
            });
        }
        else if(cat.equals("Cleaning")){
            detergent.setVisibility(View.VISIBLE);
            cleaner.setVisibility(View.VISIBLE);
            repel.setVisibility(View.VISIBLE);

            detergent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(SubCategories.this,Show_Products.class);
                    intent.putExtra("cat","Cleaning & Household");
                    intent.putExtra("subcat","Detergent & Dishwash");
                    startActivity(intent);
                }
            });

            cleaner.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(SubCategories.this,Show_Products.class);
                    intent.putExtra("cat","Cleaning & Household");
                    intent.putExtra("subcat","All Purpose Cleaners");
                    startActivity(intent);
                }
            });

            repel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(SubCategories.this,Show_Products.class);
                    intent.putExtra("cat","Cleaning & Household");
                    intent.putExtra("subcat","Repellents & Fresheners");
                    startActivity(intent);
                }
            });
        }
        else if(cat.equals("Snacks")){
            choc.setVisibility(View.VISIBLE);
            noodle.setVisibility(View.VISIBLE);
            biscuit.setVisibility(View.VISIBLE);
            namkeen.setVisibility(View.VISIBLE);
            spread.setVisibility(View.VISIBLE);

            choc.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(SubCategories.this,Show_Products.class);
                    intent.putExtra("cat","Snacks & Branded Foods");
                    intent.putExtra("subcat","Chocolates & Candies");
                    startActivity(intent);
                }
            });

            noodle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(SubCategories.this,Show_Products.class);
                    intent.putExtra("cat","Snacks & Branded Foods");
                    intent.putExtra("subcat","Noodle, Pasta & Vermicelli");
                    startActivity(intent);
                }
            });

            biscuit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(SubCategories.this,Show_Products.class);
                    intent.putExtra("cat","Snacks & Branded Foods");
                    intent.putExtra("subcat","Biscuits & Cookies");
                    startActivity(intent);
                }
            });

            namkeen.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(SubCategories.this,Show_Products.class);
                    intent.putExtra("cat","Snacks & Branded Foods");
                    intent.putExtra("subcat","Snacks & Namkeen");
                    startActivity(intent);
                }
            });

            spread.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(SubCategories.this,Show_Products.class);
                    intent.putExtra("cat","Snacks & Branded Foods");
                    intent.putExtra("subcat","Spreads, Sauces & Ketchup");
                    startActivity(intent);
                }
            });
        }



        //CheckInternetConnection
        if (!isConnectedToInternet(this)) {
            startActivity(new Intent(SubCategories.this, NoInternet.class));
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
        menu1.findItem(R.id.nav_settings).setVisible(false);
        menu1.findItem(R.id.nav_logout).setVisible(false);

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

        
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id=item.getItemId();
                if(id==R.id.nav_bot_home){
                    startActivity(new Intent(SubCategories.this, Dashboard.class));
                    finishAffinity();
                }else if(id==R.id.nav_bot_cat){
                    startActivity(new Intent(SubCategories.this,Categories.class));

                }else if(id==R.id.nav_bot_search){
                    startActivity(new Intent(SubCategories.this,SearchProduct.class));

                }else if(id==R.id.nav_bot_list){
                    startActivity(new Intent(SubCategories.this,FavouriteActivity.class));

                }else if(id==R.id.nav_bot_list){
                    startActivity(new Intent(SubCategories.this,CartActivity.class));

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
                startActivity(new Intent(SubCategories.this, Welcome_To_Login_Signup.class));
            }
        });

        location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (forWhom.equalsIgnoreCase("forUser")) {
                    SessionManager sessionManager = new SessionManager(SubCategories.this, SessionManager.SESSION_USER);
                    boolean isLoggedIn = sessionManager.checkLogin();
                    if (isLoggedIn) {
                        //Listener
                        location.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                startActivity(new Intent(SubCategories.this, AddLocation.class));
                            }
                        });
                    }

                } else if (forWhom.equalsIgnoreCase("forSeller")) {
                    SessionManager sessionManager = new SessionManager(SubCategories.this, SessionManager.SESSION_SELLER);
                    boolean isLoggedIn = sessionManager.checkLogin();
                    if (isLoggedIn) {
                        //Listener
                        location.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                startActivity(new Intent(SubCategories.this, AddLocation.class));
                            }
                        });
                    }
                } else {
                    startActivity(new Intent(SubCategories.this, Welcome_To_Login_Signup.class));
                }
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

    private boolean isConnectedToInternet(SubCategories dashboard) {
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
            startActivity(new Intent(SubCategories.this, Dashboard.class));
            finishAffinity();
        }else if(id==R.id.nav_profile){
            Intent profile_intent;
            if (forWhom.equalsIgnoreCase("forUser")) {
                profile_intent = new Intent(SubCategories.this, UserProfile.class);
            } else {
                profile_intent = new Intent(SubCategories.this, SellerProfile.class);
            }
            startActivity(profile_intent);
        }else if(id==R.id.nav_settings){
            startActivity(new Intent(SubCategories.this,Settings.class));
        }else if(id==R.id.nav_logout){
            SessionManager sessionManager1 = new SessionManager(this, SessionManager.SESSION_FORWHO);
            sessionManager1.creatingForWhomSession("forWho");

            SessionManager sessionManager2 = new SessionManager(this, SessionManager.SESSION_ADDRESS);
            sessionManager2.creatingAddressSession("Swagath Rd-Tilaknagar,Banglore-560041");

            SessionManager sessionManager = new SessionManager(this, SessionManager.SESSION_USER);
            sessionManager.logout();
            Intent logout_intent = new Intent(SubCategories.this, Dashboard.class);
            startActivity(logout_intent);
            finishAffinity();
        }else if(id==R.id.nav_category){
            startActivity(new Intent(SubCategories.this,Categories.class));

        }else if(id==R.id.nav_basket){
            startActivity(new Intent(SubCategories.this,CartActivity.class));

        }
        return true;
    }
}