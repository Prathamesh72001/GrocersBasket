package com.example.grocerbasket;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

import com.example.grocerbasket.Adapters.CartAdapter;
import com.example.grocerbasket.Adapters.ProductAdapter_Dashboard;
import com.example.grocerbasket.Constructors.CartHelperClass;
import com.example.grocerbasket.Constructors.CartProdHelperClass;
import com.example.grocerbasket.Constructors.MyButtonClickListener;
import com.example.grocerbasket.Constructors.ProductHelperClass;
import com.example.grocerbasket.Constructors.SwipeHelper;
import com.example.grocerbasket.Session.SessionManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class CartActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    RecyclerView TopOffersRecycler, TrendingRecycler, CartRecycler, FavouriteRecycler;
    RecyclerView.Adapter adapter;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    BottomNavigationView bottomNavigationView;
    String firstname,phoneNo,forWhom,cart_q,cart_f,cart_o;
    int finalcost;
    int orgcost, quan;
    RelativeLayout login_or_signup_btn, location, contentView, cartInfoRl, CartEmptyInfo, CartFullInfo, favEmptyInfo, favFullInfo, startShopping;
    NestedScrollView nestedScrollView;
    View header,cartInfoView;
    LinearLayout NoteLL;
    Menu menu1;
    static final float END_SCALE = 0.7f;
    ImageView menu, search;
    TextView header_text, header_userLoctext, cartText, priceText, orgpriceText, top_viewMore, trend_viewMore, checkoutText;
    SessionManager UsersessionManager, SellersessionManager;
    ArrayList<ProductHelperClass> productHelperClass1, productHelperClass2, productHelperClass3;
    ArrayList<CartProdHelperClass> cartProdHelperClass;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //hooks
        menu = findViewById(R.id.menu);
        search = findViewById(R.id.Search);
        contentView = findViewById(R.id.contentView);
        drawerLayout = findViewById(R.id.drawerlayout);
        navigationView = findViewById(R.id.navigation_view);
        TopOffersRecycler = findViewById(R.id.TopOffersRV);
        TrendingRecycler = findViewById(R.id.trendingRV);
        CartRecycler = findViewById(R.id.CartRecyclerView);
        nestedScrollView = findViewById(R.id.nested_scroll);
        cartInfoRl = findViewById(R.id.cart_barRl);
        cartText = findViewById(R.id.quantity_txt);
        priceText = findViewById(R.id.price_txt);
        bottomNavigationView = findViewById(R.id.bottom_nav);
        orgpriceText = findViewById(R.id.orgprice_txt);
        CartEmptyInfo = findViewById(R.id.cartEmptyInfo);
        CartFullInfo = findViewById(R.id.CartFullInfo);
        checkoutText = findViewById(R.id.checkoutText);
        startShopping = findViewById(R.id.startShopbtn);
        FavouriteRecycler = findViewById(R.id.FavouriteRecyclerView);
        favEmptyInfo = findViewById(R.id.favEmptyInfo);
        favFullInfo = findViewById(R.id.favFullInfo);
        NoteLL=findViewById(R.id.NoteLL);
        top_viewMore = findViewById(R.id.top_view_more);
        trend_viewMore = findViewById(R.id.trending_view_more);
        cartInfoView=findViewById(R.id.cartInfoView);

        //Recyclers
        TopOffersRecycler();
        TrendingRecycler();

        //CheckInternetConnection
        if (!isConnectedToInternet(this)) {
            startActivity(new Intent(CartActivity.this, NoInternet.class));
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

                //methods
                CartViewBar();
                CartRecycler();
                FavouriteRecycler();

                SwipeHelper swipeHelper = new SwipeHelper(this, CartRecycler, 200) {
                    CartProdHelperClass item = new CartProdHelperClass();

                    @Override
                    public void instantiateMyButton(RecyclerView.ViewHolder viewHolder, List<SwipeHelper.MyButton> buffer) {
                        buffer.add(new MyButton(CartActivity.this, "Remove", 30, 0, Color.parseColor("#ff5c62"),
                                new MyButtonClickListener() {
                                    @Override
                                    public void onClick(int pos) {
                                        item = cartProdHelperClass.get(pos);

                                        FirebaseDatabase rootnode = FirebaseDatabase.getInstance();
                                        DatabaseReference reference = rootnode.getReference("Products").child("Cart Products");
                                        Query checkuser = reference.orderByChild("userphoneno").equalTo(phoneNo);
                                        checkuser.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                if (snapshot.exists()) {
                                                    Query checkproduct = reference.child(phoneNo).child("cartProdHelperClass").orderByChild("prodid").equalTo(item.getProdid());
                                                    checkproduct.addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot snapshot1) {
                                                            if (snapshot1.exists()) {

                                                                quan = Integer.parseInt(item.getProdquan());
                                                                finalcost = Integer.parseInt(item.getProdfinalcost());
                                                                orgcost = Integer.parseInt(item.getProdorgcost());

                                                                removeFromCart(item.getProdid(), orgcost, finalcost, quan, phoneNo);

                                                            }
                                                        }

                                                        @Override
                                                        public void onCancelled(@NonNull DatabaseError error) {

                                                        }
                                                    });
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });

                                        Snackbar.make(CartRecycler, item.getProdname(), Snackbar.LENGTH_LONG)
                                                .setAction("Undo", new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {
                                                        quan = Integer.parseInt(item.getProdquan());
                                                        finalcost = Integer.parseInt(item.getProdfinalcost());
                                                        orgcost = Integer.parseInt(item.getProdorgcost());

                                                        addToCart(item, orgcost, finalcost, quan, phoneNo);
                                                    }
                                                }).setActionTextColor(Color.parseColor("#ffffff"))
                                                .setBackgroundTintList(ContextCompat.getColorStateList(CartActivity.this, R.color.orange))
                                                .show();
                                    }
                                }));
                    }
                };

            }
        }

        SessionManager sessionManager1 = new SessionManager(this, SessionManager.SESSION_ADDRESS);
        HashMap<String, String> addDetails = sessionManager1.getAddressDetailFromSession();
        String address = addDetails.get(SessionManager.KEY_ADD);
        header_userLoctext.setText(address);

        //NavigationView & bottomNavigationView
        navigationView.bringToFront();
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.nav_basket);


        bottomNavigationView.setSelectedItemId(R.id.nav_bot_basket);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id=item.getItemId();
                if(id==R.id.nav_bot_home){
                    startActivity(new Intent(CartActivity.this, Dashboard.class));
                    finishAffinity();
                }else if(id==R.id.nav_bot_cat){
                    startActivity(new Intent(CartActivity.this, Categories.class));
                    finish();
                }else if(id==R.id.nav_bot_search){
                    startActivity(new Intent(CartActivity.this, SearchProduct.class));
                    finish();
                }else if(id==R.id.nav_bot_list){
                    startActivity(new Intent(CartActivity.this, FavouriteActivity.class));
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
                startActivity(new Intent(CartActivity.this, Welcome_To_Login_Signup.class));
            }
        });

        location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (forWhom.equalsIgnoreCase("forUser")) {
                    SessionManager sessionManager = new SessionManager(CartActivity.this, SessionManager.SESSION_USER);
                    boolean isLoggedIn = sessionManager.checkLogin();
                    if (isLoggedIn) {
                        //Listener
                        location.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                startActivity(new Intent(CartActivity.this, AddLocation.class));
                            }
                        });
                    }

                } else if (forWhom.equalsIgnoreCase("forSeller")) {
                    SessionManager sessionManager = new SessionManager(CartActivity.this, SessionManager.SESSION_SELLER);
                    boolean isLoggedIn = sessionManager.checkLogin();
                    if (isLoggedIn) {
                        //Listener
                        location.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                startActivity(new Intent(CartActivity.this, AddLocation.class));
                            }
                        });
                    }
                } else {
                    startActivity(new Intent(CartActivity.this, Welcome_To_Login_Signup.class));
                }
            }
        });

        checkoutText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (forWhom.equalsIgnoreCase("forUser")) {
                    SessionManager sessionManager = new SessionManager(CartActivity.this, SessionManager.SESSION_USER);
                    boolean isLoggedIn = sessionManager.checkLogin();
                    if (isLoggedIn) {
                        FirebaseDatabase.getInstance().getReference("Orders").orderByChild("orderby").equalTo(phoneNo).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()) {
                                    Toast.makeText(CartActivity.this,"An order is already in process",Toast.LENGTH_LONG).show();
                                    startActivity(new Intent(CartActivity.this,FavouriteActivity.class));
                                }
                                else{
                                    Intent checkout = new Intent(CartActivity.this, UserLocation.class);
                                    checkout.putExtra("checkout", "checkout");
                                    startActivity(checkout);
                                }
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    } else {
                        startActivity(new Intent(CartActivity.this, Welcome_To_Login_Signup.class));
                    }
                } else {
                    startActivity(new Intent(CartActivity.this, Welcome_To_Login_Signup.class));
                }
            }
        });

        startShopping.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CartActivity.this, Dashboard.class));
                finishAffinity();
            }
        });

        //View More
        top_viewMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CartActivity.this, Show_Products.class);
                intent.putExtra("cat", "Top Offers");
                intent.putExtra("subcat", "");
                startActivity(intent);
            }
        });

        trend_viewMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CartActivity.this, Show_Products.class);
                intent.putExtra("cat", "Trending");
                intent.putExtra("subcat", "");
                startActivity(intent);
            }
        });


    }

    private void addToCart(CartProdHelperClass item, int orgcost, int finalcost, int quan, String number) {
        FirebaseDatabase rootnode = FirebaseDatabase.getInstance();
        DatabaseReference reference = rootnode.getReference("Products").child("Cart Products");
        Query checkuser = reference.orderByChild("userphoneno").equalTo(number);
        checkuser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String cartquan = snapshot.child(number).child("cartquan").getValue(String.class);
                    String cartfinalcost = snapshot.child(number).child("cartfinalcost").getValue(String.class);
                    String cartorgcost = snapshot.child(number).child("cartorgcost").getValue(String.class);

                    int c_final = Integer.parseInt(cartfinalcost) + finalcost;
                    int c_org = Integer.parseInt(cartorgcost) + orgcost;
                    int c_quan = Integer.parseInt(cartquan) + quan;
                    Query checkproduct = reference.child(number).child("cartProdHelperClass").orderByChild("prodid").equalTo(item.getProdid());
                    checkproduct.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot1) {
                            if (snapshot1.exists()) {

                            } else {
                                reference.child(number).child("cartProdHelperClass").child(item.getProdid()).setValue(item);
                            }
                            reference.child(number).child("cartquan").setValue(String.valueOf(c_quan));
                            reference.child(number).child("cartfinalcost").setValue(String.valueOf(c_final));
                            reference.child(number).child("cartorgcost").setValue(String.valueOf(c_org));
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                } else {

                    Map<String, CartProdHelperClass> cartitem = new HashMap<String, CartProdHelperClass>();
                    cartitem.put(item.getProdid(), item);
                    CartHelperClass cartHelperClass = new CartHelperClass(cartitem, String.valueOf(orgcost), String.valueOf(finalcost), String.valueOf(quan), number);
                    reference.child(number).setValue(cartHelperClass);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void removeFromCart(String id, int orgcost, int finalcost, int quan, String number) {
        FirebaseDatabase rootnode = FirebaseDatabase.getInstance();
        DatabaseReference reference = rootnode.getReference("Products").child("Cart Products");
        Query checkuser = reference.orderByChild("userphoneno").equalTo(number);
        checkuser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String cartquan = snapshot.child(number).child("cartquan").getValue(String.class);
                    String cartfinalcost = snapshot.child(number).child("cartfinalcost").getValue(String.class);
                    String cartorgcost = snapshot.child(number).child("cartorgcost").getValue(String.class);

                    int c_final = Integer.parseInt(cartfinalcost) - finalcost;
                    int c_org = Integer.parseInt(cartorgcost) - orgcost;
                    int c_quan = Integer.parseInt(cartquan) - quan;
                    if (c_quan < 1) {
                        reference.child(number).removeValue();
                    } else {
                        Query checkproduct = reference.child(number).child("cartProdHelperClass").orderByChild("prodid").equalTo(id);
                        checkproduct.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot1) {
                                if (snapshot1.exists()) {

                                    reference.child(number).child("cartProdHelperClass").child(id).removeValue();
                                    reference.child(number).child("cartquan").setValue(String.valueOf(c_quan));
                                    reference.child(number).child("cartfinalcost").setValue(String.valueOf(c_final));
                                    reference.child(number).child("cartorgcost").setValue(String.valueOf(c_org));
                                }

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private void FavouriteRecycler() {
        productHelperClass3 = new ArrayList<>();
        UsersessionManager = new SessionManager(this, SessionManager.SESSION_USER);
        HashMap<String, String> userDetails = UsersessionManager.getUserDetailFromSession();
        phoneNo = userDetails.get(SessionManager.KEY_PHONENO);

        FirebaseDatabase.getInstance().getReference("Products").child("Favourite Products").child(phoneNo).orderByChild("prodid").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    favFullInfo.setVisibility(View.VISIBLE);
                    favEmptyInfo.setVisibility(View.GONE);
                    productHelperClass3.clear();
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        ProductHelperClass productHelperClass = ds.getValue(ProductHelperClass.class);
                        productHelperClass3.add(productHelperClass);
                    }
                    adapter = new ProductAdapter_Dashboard(CartActivity.this, productHelperClass3);
                    FavouriteRecycler.setAdapter(adapter);
                } else {
                    favFullInfo.setVisibility(View.GONE);
                    favEmptyInfo.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void CartRecycler() {
        cartProdHelperClass = new ArrayList<>();
        UsersessionManager = new SessionManager(this, SessionManager.SESSION_USER);
        HashMap<String, String> userDetails = UsersessionManager.getUserDetailFromSession();
        phoneNo = userDetails.get(SessionManager.KEY_PHONENO);

        FirebaseDatabase.getInstance().getReference("Products").child("Cart Products").child(phoneNo).child("cartProdHelperClass").orderByChild("prodid").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    CartFullInfo.setVisibility(View.VISIBLE);
                    NoteLL.setVisibility(View.VISIBLE);
                    CartEmptyInfo.setVisibility(View.GONE);
                    cartProdHelperClass.clear();
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        CartProdHelperClass cartproductHelperClass = ds.getValue(CartProdHelperClass.class);
                        cartProdHelperClass.add(cartproductHelperClass);
                    }
                    adapter = new CartAdapter(CartActivity.this, cartProdHelperClass);
                    CartRecycler.setAdapter(adapter);
                } else {
                    CartFullInfo.setVisibility(View.GONE);
                    NoteLL.setVisibility(View.GONE);
                    CartEmptyInfo.setVisibility(View.VISIBLE);
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

                    cartInfoRl.setVisibility(View.GONE);
                    cartInfoView.setVisibility(View.GONE);
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
                adapter = new ProductAdapter_Dashboard(CartActivity.this, productHelperClass1);
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
                adapter = new ProductAdapter_Dashboard(CartActivity.this, productHelperClass2);
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

    private boolean isConnectedToInternet(CartActivity dashboard) {
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
            startActivity(new Intent(CartActivity.this, Dashboard.class));
            finishAffinity();
        }else if(id==R.id.nav_profile){
            Intent profile_intent;
            if (forWhom.equalsIgnoreCase("forUser")) {
                profile_intent = new Intent(CartActivity.this, UserProfile.class);
            } else {
                profile_intent = new Intent(CartActivity.this, SellerProfile.class);
            }
            startActivity(profile_intent);
        }else if(id==R.id.nav_settings){
            startActivity(new Intent(CartActivity.this,Settings.class));
        }else if(id==R.id.nav_logout){
            SessionManager sessionManager1 = new SessionManager(this, SessionManager.SESSION_FORWHO);
            sessionManager1.creatingForWhomSession("forWho");

            SessionManager sessionManager2 = new SessionManager(this, SessionManager.SESSION_ADDRESS);
            sessionManager2.creatingAddressSession("Swagath Rd-Tilaknagar,Banglore-560041");

            SessionManager sessionManager = new SessionManager(this, SessionManager.SESSION_USER);
            sessionManager.logout();
            Intent logout_intent = new Intent(CartActivity.this, Dashboard.class);
            startActivity(logout_intent);
            finishAffinity();
        }else if(id==R.id.nav_category){
            startActivity(new Intent(CartActivity.this, Categories.class));
            finish();
        }else if(id==R.id.nav_basket){
            CloseDrawer();
        }
        return true;
    }

}