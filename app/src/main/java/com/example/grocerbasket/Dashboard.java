package com.example.grocerbasket;

import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.Image;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.core.widget.NestedScrollView;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.grocerbasket.Adapters.ProductAdapter_Dashboard;
import com.example.grocerbasket.Constructors.CartHelperClass;
import com.example.grocerbasket.Constructors.OrderHelperClass;
import com.example.grocerbasket.Constructors.ProductHelperClass;
import com.example.grocerbasket.Constructors.SellerHelperClass;
import com.example.grocerbasket.Session.SessionManager;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class Dashboard extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    RecyclerView TopOffersRecycler, TrendingRecycler, FoodgrainRV;
    RecyclerView.Adapter adapter;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    BottomNavigationView bottomNavigationView;
    String firstname, phoneNo, forWhom, cart_q, cart_f, cart_o;
    RelativeLayout login_or_signup_btn,Searchbar, location, contentView, cartInfoRl,exploreBtn;
    NestedScrollView nestedScrollView;
    View header,cartInfoView;
    Menu menu1;
    LinearLayout fruits,foodgain,beverage,cleaning,beauty,snacks,bakery,top,trend;
    static final float END_SCALE = 0.7f;
    ImageView menu, add, profile;
    TextView header_text, header_userLoctext, cartText, priceText, orgpriceText, viewCart,top_viewMore,trend_viewMore
            ,foodgrain_viewmore;
    SessionManager UsersessionManager;
    ArrayList<ProductHelperClass> productHelperClass1, productHelperClass2, productHelperClass3;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //hooks
        menu = findViewById(R.id.menu);
        add = findViewById(R.id.Addme);
        profile = findViewById(R.id.profile);
        contentView = findViewById(R.id.contentView);
        drawerLayout = findViewById(R.id.drawerlayout);
        navigationView = findViewById(R.id.navigation_view);
        bottomNavigationView = findViewById(R.id.bottom_nav);
        TopOffersRecycler = findViewById(R.id.TopOffersRV);
        TrendingRecycler = findViewById(R.id.trendingRV);
        FoodgrainRV = findViewById(R.id.Foodgrain_oil_masalaRV);
        nestedScrollView = findViewById(R.id.nested_scroll);
        cartInfoRl = findViewById(R.id.cart_barRl);
        cartText = findViewById(R.id.quantity_txt);
        priceText = findViewById(R.id.price_txt);
        orgpriceText = findViewById(R.id.orgprice_txt);
        viewCart = findViewById(R.id.viewCart);
        exploreBtn=findViewById(R.id.Explorebtn);

        fruits=findViewById(R.id.fruit_and_vegetables);
        foodgain=findViewById(R.id.foodgrains_and_masala);
        beverage=findViewById(R.id.beverages);
        beauty=findViewById(R.id.beauty_and_hygiene);
        bakery=findViewById(R.id.bakery);
        snacks=findViewById(R.id.snacks);
        cleaning=findViewById(R.id.cleaning_and_household);
        top=findViewById(R.id.top_offers);
        trend=findViewById(R.id.Trending);

        top_viewMore=findViewById(R.id.top_offers_viewmore);
        trend_viewMore=findViewById(R.id.trending_view_more);
        foodgrain_viewmore=findViewById(R.id.foodgrain_viewmore);

        Searchbar=findViewById(R.id.SearchBar);
        cartInfoView=findViewById(R.id.cartInfoView);

        //Recyclers
        TopOffersRecycler();
        TrendingRecycler();
        FoodgrainRecycler();

        //CheckInternetConnection
        if (!isConnectedToInternet(this)) {
            startActivity(new Intent(Dashboard.this, NoInternet.class));
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


        if (Objects.requireNonNull(forWhom).equalsIgnoreCase("forAdmin")) {
            startActivity(new Intent(Dashboard.this, Admin_Show_Product.class));
            finishAffinity();
        }

        if (Objects.requireNonNull(forWhom).equalsIgnoreCase("forUser")) {
            UsersessionManager = new SessionManager(this, SessionManager.SESSION_USER);
            HashMap<String, String> userDetails = UsersessionManager.getUserDetailFromSession();
            firstname = userDetails.get(SessionManager.KEY_FIRSTNAME);

            //Listener
            profile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(Dashboard.this, UserProfile.class));
                }
            });

            boolean isLoggedIn = UsersessionManager.checkLogin();
            if (isLoggedIn) {
                //Visibilities
                header_text.setVisibility(View.VISIBLE);
                login_or_signup_btn.setVisibility(View.GONE);
                add.setVisibility(View.GONE);
                profile.setVisibility(View.VISIBLE);
                menu1.findItem(R.id.nav_profile).setVisible(true);
                menu1.findItem(R.id.nav_logout).setVisible(true);
                menu1.findItem(R.id.nav_settings).setVisible(true);

                //setText
                header_text.setText("Hello, " + firstname);

                //methods
                CartViewBar();
                detailsBottomSheet(phoneNo);
            }
        }


        if (Objects.requireNonNull(forWhom).equalsIgnoreCase("forSeller")) {
            startActivity(new Intent(Dashboard.this, AdminShowOrderedProducts.class));
            finishAffinity();
        }

        SessionManager sessionManager1 = new SessionManager(this, SessionManager.SESSION_ADDRESS);
        HashMap<String, String> addDetails = sessionManager1.getAddressDetailFromSession();
        String address = addDetails.get(SessionManager.KEY_ADD);
        header_userLoctext.setText(address);

        //NavigationView & bottomNavigationView
        navigationView.bringToFront();
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.nav_home);

        bottomNavigationView.setSelectedItemId(R.id.nav_bot_home);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nav_bot_home:
                        break;

                    case R.id.nav_bot_cat:
                        startActivity(new Intent(Dashboard.this,Categories.class));
                        break;

                    case R.id.nav_bot_search:
                        startActivity(new Intent(Dashboard.this, SearchProduct.class));
                        break;

                    case R.id.nav_bot_list:
                        startActivity(new Intent(Dashboard.this, FavouriteActivity.class));
                        break;

                    case R.id.nav_bot_basket:
                        startActivity(new Intent(Dashboard.this, CartActivity.class));
                        break;
                }
                return false;
            }
        });

        //Animations
        drawerAnim();

        //Listeners
        Searchbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Dashboard.this, SearchProduct.class);

                //Animation
                Pair[] pairs = new Pair[1];
                pairs[0] = new Pair<View, String>(Searchbar, "search_bar");

                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(Dashboard.this, pairs);
                startActivity(intent, options.toBundle());
            }
        });

        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CloseDrawer();
            }
        });

        login_or_signup_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Dashboard.this, Welcome_To_Login_Signup.class));
            }
        });

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Dashboard.this, Welcome_To_Login_Signup.class));
            }
        });

        viewCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Dashboard.this, CartActivity.class));
            }
        });

        location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (forWhom.equalsIgnoreCase("forUser")) {
                    SessionManager sessionManager = new SessionManager(Dashboard.this, SessionManager.SESSION_USER);
                    boolean isLoggedIn = sessionManager.checkLogin();
                    if (isLoggedIn) {
                        //Listener
                        location.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                startActivity(new Intent(Dashboard.this, AddLocation.class));
                            }
                        });
                    }

                } else if (forWhom.equalsIgnoreCase("forSeller")) {
                    SessionManager sessionManager = new SessionManager(Dashboard.this, SessionManager.SESSION_SELLER);
                    boolean isLoggedIn = sessionManager.checkLogin();
                    if (isLoggedIn) {
                        //Listener
                        location.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                startActivity(new Intent(Dashboard.this, AddLocation.class));
                            }
                        });
                    }
                } else {
                    startActivity(new Intent(Dashboard.this, Welcome_To_Login_Signup.class));
                }
            }
        });

        exploreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Dashboard.this,Categories.class));
            }
        });

        //Categories
        fruits.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Dashboard.this,SubCategories.class);
                intent.putExtra("catName","Fruits");
                startActivity(intent);
            }
        });

        bakery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Dashboard.this,SubCategories.class);
                intent.putExtra("catName","Bakery");
                startActivity(intent);
            }
        });

        foodgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Dashboard.this,SubCategories.class);
                intent.putExtra("catName","Foodgrain");
                startActivity(intent);
            }
        });

        beverage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Dashboard.this,SubCategories.class);
                intent.putExtra("catName","Beverages");
                startActivity(intent);
            }
        });

        beauty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Dashboard.this,SubCategories.class);
                intent.putExtra("catName","Beauty");
                startActivity(intent);
            }
        });

        cleaning.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Dashboard.this,SubCategories.class);
                intent.putExtra("catName","Cleaning");
                startActivity(intent);
            }
        });

        snacks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Dashboard.this,SubCategories.class);
                intent.putExtra("catName","Snacks");
                startActivity(intent);
            }
        });

        top.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Dashboard.this,Show_Products.class);
                intent.putExtra("cat","Top Offers");
                intent.putExtra("subcat","");
                startActivity(intent);
            }
        });

        trend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Dashboard.this,Show_Products.class);
                intent.putExtra("cat","Trending");
                intent.putExtra("subcat","");
                startActivity(intent);
            }
        });

        //View More
        top_viewMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Dashboard.this,Show_Products.class);
                intent.putExtra("cat","Top Offers");
                intent.putExtra("subcat","");
                startActivity(intent);
            }
        });

        trend_viewMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Dashboard.this,Show_Products.class);
                intent.putExtra("cat","Trending");
                intent.putExtra("subcat","");
                startActivity(intent);
            }
        });
    }

    private void FoodgrainRecycler() {
        productHelperClass3 = new ArrayList<>();
        //get All Products
        FirebaseDatabase.getInstance().getReference("Products").child("All Products").orderByChild("cat").equalTo("Foodgrains, Oil & Masala").addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                productHelperClass3.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    ProductHelperClass productHelperClass = ds.getValue(ProductHelperClass.class);
                    productHelperClass3.add(productHelperClass);
                }
                adapter = new ProductAdapter_Dashboard(Dashboard.this, productHelperClass3);
                FoodgrainRV.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void detailsBottomSheet(String phoneNo) {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(Dashboard.this);
        View view = LayoutInflater.from(Dashboard.this).inflate(R.layout.bs_order_user, null);
        bottomSheetDialog.setContentView(view);

        //hooks
        TextView orderId=view.findViewById(R.id.OrderId);
        TextView orderTo=view.findViewById(R.id.OrderTo);
        TextView orderPrice=view.findViewById(R.id.productPrice);
        TextView orderTime=view.findViewById(R.id.OrderTime);
        CircleImageView sellerImage=view.findViewById(R.id.sellerImage);
        RelativeLayout showOrder=view.findViewById(R.id.Nextbtn);
        ImageView backBtn=view.findViewById(R.id.backbtn);


        FirebaseDatabase.getInstance().getReference("Orders").orderByChild("orderby").equalTo(phoneNo).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    //showDialogue
                    bottomSheetDialog.show();
                    for(DataSnapshot ds: snapshot.getChildren()) {
                        OrderHelperClass orderHelperClass = ds.getValue(OrderHelperClass.class);

                        //get
                        String orderid=orderHelperClass.getOrderid();
                        String orderprice=orderHelperClass.getOrdercost();
                        String ordertime=orderHelperClass.getOrdertime();
                        String orderto=orderHelperClass.getOrderto();

                        //set
                        orderId.setText(orderid);
                        orderPrice.setText(orderprice);

                        Calendar calendar=Calendar.getInstance();
                        calendar.setTimeInMillis(Long.parseLong(ordertime));
                        String dateForm= DateFormat.format("dd/MM/yyyy",calendar).toString();
                        orderTime.setText(dateForm);

                        FirebaseStorage.getInstance().getReference().child("Seller Shop Images/").child(Objects.requireNonNull(orderto + "/" + "profile.jpg")).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                try {
                                    Picasso.get().load(uri).centerCrop().resize(sellerImage.getMeasuredWidth(),sellerImage.getMeasuredHeight()).placeholder(R.drawable.default_image).into(sellerImage);;
                                }catch(Exception e){
                                    sellerImage.setImageResource(R.drawable.default_image);
                                }
                            }
                        });

                        FirebaseDatabase.getInstance().getReference("Sellers").orderByChild("phoneno").equalTo(orderto).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if(snapshot.exists()){
                                    for (DataSnapshot ds : snapshot.getChildren()){
                                        SellerHelperClass sellerHelperClass=ds.getValue(SellerHelperClass.class);
                                        String shopname=sellerHelperClass.getShopname();
                                        orderTo.setText("From "+shopname);
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                        backBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                //showDialogue
                                bottomSheetDialog.dismiss();
                            }
                        });

                        showOrder.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                startActivity(new Intent(Dashboard.this,FavouriteActivity.class));
                            }
                        });
                    }
                }
                else{
                    //showDialogue
                    bottomSheetDialog.hide();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


    private void CartViewBar() {
        UsersessionManager = new SessionManager(this, SessionManager.SESSION_USER);
        HashMap<String, String> userDetails = UsersessionManager.getUserDetailFromSession();
        phoneNo = userDetails.get(SessionManager.KEY_PHONENO);

        FirebaseDatabase.getInstance().getReference("Products").child("Cart Products").orderByChild("userphoneno").equalTo(phoneNo).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        CartHelperClass cartHelperClasses = ds.getValue(CartHelperClass.class);
                        cart_q = cartHelperClasses.getCartquan();
                        cart_f = cartHelperClasses.getCartfinalcost();
                        cart_o = cartHelperClasses.getCartorgcost();
                    }
                    cartInfoView.setVisibility(View.VISIBLE);
                    cartInfoRl.setVisibility(View.VISIBLE);
                    if (Integer.parseInt(cart_q) == 1) {
                        cartText.setText(cart_q + " item");
                    } else {
                        cartText.setText(cart_q + " items");
                    }

                    if (Integer.parseInt(cart_o) == Integer.parseInt(cart_f)) {
                        orgpriceText.setVisibility(View.GONE);
                    } else {
                        orgpriceText.setVisibility(View.VISIBLE);
                        orgpriceText.setText("\u20b9" + cart_o);
                        orgpriceText.setPaintFlags(orgpriceText.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    }
                    priceText.setText("\u20b9" + cart_f);

                } else {
                    cartInfoView.setVisibility(View.GONE);
                    cartInfoRl.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private void TrendingRecycler() {
        productHelperClass1 = new ArrayList<>();
        //get All Products
        FirebaseDatabase.getInstance().getReference("Products").child("Trending").addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                productHelperClass1.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    ProductHelperClass productHelperClass = ds.getValue(ProductHelperClass.class);
                    productHelperClass1.add(productHelperClass);
                }
                adapter = new ProductAdapter_Dashboard(Dashboard.this, productHelperClass1);
                TrendingRecycler.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void TopOffersRecycler() {
        productHelperClass2 = new ArrayList<>();
        //get All Products
        FirebaseDatabase.getInstance().getReference("Products").child("Top Offers").addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                productHelperClass2.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    ProductHelperClass productHelperClass = ds.getValue(ProductHelperClass.class);
                    productHelperClass2.add(productHelperClass);
                }
                adapter = new ProductAdapter_Dashboard(Dashboard.this, productHelperClass2);
                TopOffersRecycler.setAdapter(adapter);
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

    private boolean isConnectedToInternet(Dashboard dashboard) {
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
        switch (item.getItemId()) {
            case R.id.nav_home:
                CloseDrawer();
                break;

            case R.id.nav_profile:
                if (forWhom.equalsIgnoreCase("forUser")) {
                    Intent profile_intent = new Intent(Dashboard.this, UserProfile.class);
                    startActivity(profile_intent);
                } else {
                    Intent profile_intent = new Intent(Dashboard.this, SellerProfile.class);
                    startActivity(profile_intent);
                }
                break;

            case R.id.nav_settings:
                startActivity(new Intent(Dashboard.this,Settings.class));
                break;

            case R.id.nav_logout:
                SessionManager sessionManager1 = new SessionManager(this, SessionManager.SESSION_FORWHO);
                sessionManager1.creatingForWhomSession("forWho");

                SessionManager sessionManager2 = new SessionManager(this, SessionManager.SESSION_ADDRESS);
                sessionManager2.creatingAddressSession("Swagath Rd-Tilaknagar,Banglore-560041");

                SessionManager sessionManager = new SessionManager(this, SessionManager.SESSION_USER);
                sessionManager.logout();
                Intent logout_intent = new Intent(Dashboard.this, Dashboard.class);
                startActivity(logout_intent);
                finishAffinity();
                break;

            case R.id.nav_category:
                startActivity(new Intent(Dashboard.this,Categories.class));
                break;

            case R.id.nav_aboutus:
                break;

            case R.id.nav_basket:
                startActivity(new Intent(Dashboard.this, CartActivity.class));
                break;
        }
        return true;
    }
}