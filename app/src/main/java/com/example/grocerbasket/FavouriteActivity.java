package com.example.grocerbasket;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.core.widget.NestedScrollView;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.grocerbasket.Adapters.OrderProductAdapter;
import com.example.grocerbasket.Adapters.ProductAdapter_Dashboard;
import com.example.grocerbasket.Constructors.CartProdHelperClass;
import com.example.grocerbasket.Constructors.OrderHelperClass;
import com.example.grocerbasket.Constructors.ProductHelperClass;
import com.example.grocerbasket.Session.SessionManager;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class FavouriteActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    RecyclerView FavouriteRecycler,TopOffersRecycler, TrendingRecycler;
    RecyclerView.Adapter adapter;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    BottomNavigationView bottomNavigationView;
    String firstname, phoneNo, forWhom;
    RelativeLayout login_or_signup_btn,btnRl,progBtn, location, contentView, CartEmptyInfo, CartFullInfo;
    NestedScrollView nestedScrollView;
    LinearLayout bill,area,prog;
    View header,cartInfoView;
    Menu menu1;
    String orderid,orderTo;
    static final float END_SCALE = 0.7f;
    ImageView menu, search,img;
    TextView header_text, header_userLoctext,orderlocation,cost,delcost,topay,payOpn,txt,txt2;
    SessionManager UsersessionManager, SellersessionManager;
    ArrayList<ProductHelperClass> productHelperClass2,productHelperClass3;
    ArrayList<CartProdHelperClass> productHelperClass1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourite);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //hooks
        menu = findViewById(R.id.menu);
        search = findViewById(R.id.Search);
        contentView = findViewById(R.id.contentView);
        drawerLayout = findViewById(R.id.drawerlayout);
        navigationView = findViewById(R.id.navigation_view);
        nestedScrollView = findViewById(R.id.nested_scroll);
        bill=findViewById(R.id.bill);
        bottomNavigationView = findViewById(R.id.bottom_nav);
        area=findViewById(R.id.area);
        CartEmptyInfo = findViewById(R.id.orderEmptyInfo);
        CartFullInfo = findViewById(R.id.orderFullInfo);
        FavouriteRecycler=findViewById(R.id.OrderRecyclerView);
        TopOffersRecycler = findViewById(R.id.TopOffersRV);
        TrendingRecycler = findViewById(R.id.trendingRV);
        orderlocation=findViewById(R.id.UserLocation);
        cost=findViewById(R.id.totalcost);
        topay=findViewById(R.id.ToPay);
        delcost=findViewById(R.id.delcost);
        payOpn=findViewById(R.id.PayOption);
        txt=findViewById(R.id.txt);
        txt2=findViewById(R.id.txt2);
        img=findViewById(R.id.img);
        progBtn=findViewById(R.id.ProgressBtn);
        prog=findViewById(R.id.progress);
        btnRl=findViewById(R.id.btn_barRl);
        cartInfoView=findViewById(R.id.cartInfoView);

        //Recyclers
        TopOffersRecycler();
        TrendingRecycler();

        //CheckInternetConnection
        if (!isConnectedToInternet(this)) {
            startActivity(new Intent(FavouriteActivity.this, NoInternet.class));
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

                //methods
                FavouriteRecycler();

            }
        }

        SessionManager sessionManager1 = new SessionManager(this, SessionManager.SESSION_ADDRESS);
        HashMap<String, String> addDetails = sessionManager1.getAddressDetailFromSession();
        String address = addDetails.get(SessionManager.KEY_ADD);
        header_userLoctext.setText(address);

        //NavigationView & bottomNavigationView
        navigationView.bringToFront();
        navigationView.setNavigationItemSelectedListener(this);

        bottomNavigationView.setSelectedItemId(R.id.nav_bot_list);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id=item.getItemId();
                if(id==R.id.nav_bot_home){
                    startActivity(new Intent(FavouriteActivity.this, Dashboard.class));
                    finishAffinity();
                }else if(id==R.id.nav_bot_cat){
                    startActivity(new Intent(FavouriteActivity.this,Categories.class));
                    finish();
                }else if(id==R.id.nav_bot_search){
                    startActivity(new Intent(FavouriteActivity.this, SearchProduct.class));
                    finish();
                }else if(id==R.id.nav_bot_basket){
                    startActivity(new Intent(FavouriteActivity.this, CartActivity.class));
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
                startActivity(new Intent(FavouriteActivity.this, Welcome_To_Login_Signup.class));
            }
        });

        btnRl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if("Cancel Order".equals(txt2.getText().toString())) {
                    AlertDialog.Builder builder=new androidx.appcompat.app.AlertDialog.Builder(FavouriteActivity.this, androidx.appcompat.R.style.Theme_AppCompat_Dialog);
                    builder.setTitle("Cancel Order").setMessage("Are you sure you want to cancel this order ?")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    cancelOrder();

                                }
                            })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            }).show();
                }
            }
        });

        location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (forWhom.equalsIgnoreCase("forUser")) {
                    SessionManager sessionManager = new SessionManager(FavouriteActivity.this, SessionManager.SESSION_USER);
                    boolean isLoggedIn = sessionManager.checkLogin();
                    if (isLoggedIn) {
                        //Listener
                        location.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                startActivity(new Intent(FavouriteActivity.this, AddLocation.class));
                            }
                        });
                    }

                } else if (forWhom.equalsIgnoreCase("forSeller")) {
                    SessionManager sessionManager = new SessionManager(FavouriteActivity.this, SessionManager.SESSION_SELLER);
                    boolean isLoggedIn = sessionManager.checkLogin();
                    if (isLoggedIn) {
                        //Listener
                        location.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                startActivity(new Intent(FavouriteActivity.this, AddLocation.class));
                            }
                        });
                    }
                } else {
                    startActivity(new Intent(FavouriteActivity.this, Welcome_To_Login_Signup.class));
                }
            }
        });

    }

    private void cancelOrder() {
        FirebaseDatabase.getInstance().getReference("Orders").orderByChild("orderby").equalTo(phoneNo).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {

                    FirebaseDatabase.getInstance().getReference("Orders").child(phoneNo).removeValue();
                    prepareNotification(orderid,phoneNo,orderTo);
                    if("COD".equals(payOpn.getText().toString())) {
                        Toast.makeText(FavouriteActivity.this, "Order Canceled Successfully", Toast.LENGTH_LONG).show();
                    }
                    else if("Paid".equals(payOpn.getText().toString())){
                        Toast.makeText(FavouriteActivity.this, "Order Canceled Successfully, Debited Amount will be Refunded in 4-5 Business Days", Toast.LENGTH_LONG).show();

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void FavouriteRecycler() {
        productHelperClass1= new ArrayList<>();
        UsersessionManager = new SessionManager(this, SessionManager.SESSION_USER);
        HashMap<String, String> userDetails = UsersessionManager.getUserDetailFromSession();
        phoneNo = userDetails.get(SessionManager.KEY_PHONENO);

        FirebaseDatabase.getInstance().getReference("Orders").orderByChild("orderby").equalTo(phoneNo).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    CartFullInfo.setVisibility(View.VISIBLE);
                    cartInfoView.setVisibility(View.VISIBLE);
                    area.setVisibility(View.VISIBLE);
                    bill.setVisibility(View.VISIBLE);
                    prog.setVisibility(View.VISIBLE);
                    btnRl.setVisibility(View.VISIBLE);
                    CartEmptyInfo.setVisibility(View.GONE);
                    productHelperClass1.clear();

                    for (DataSnapshot ds : snapshot.getChildren()) {
                        OrderHelperClass orderHelperClass = ds.getValue(OrderHelperClass.class);
                        orderid=orderHelperClass.getOrderid();
                        String delOp=orderHelperClass.getDeliveryOption();
                        String payOp=orderHelperClass.getPayOption();
                        String totalCost=orderHelperClass.getOrdercost();
                        orderTo=orderHelperClass.getOrderto();
                        int progress=orderHelperClass.getProgress();

                        topay.setText("\u20b9 "+totalCost);

                        if(payOp.equals("Cash On Delivery"))
                        {
                            payOpn.setText("COD");
                            payOpn.setTextColor(Color.parseColor("#ec1c24"));
                        }
                        else{
                            payOpn.setText("Paid");
                            payOpn.setTextColor(Color.parseColor("#04942d"));
                        }

                        if(progress==0){
                            txt.setText("Order Placed");
                            txt.setTextColor(Color.parseColor("#FF000000"));
                            progBtn.setBackgroundTintList(null);
                            txt2.setText("Cancel Order");
                            img.setImageResource(R.drawable.cancel_order);
                        }
                        else if(progress==50){
                            txt.setText("Order In Progress");
                            txt.setTextColor(Color.parseColor("#FFFFFFFF"));
                            progBtn.setBackgroundTintList(ContextCompat.getColorStateList(FavouriteActivity.this, R.color.orange));
                            txt2.setText("Track Order");
                            img.setImageResource(R.drawable.ic_baseline_my_location_24);
                        }
                        else if(progress==100){
                            txt.setText("Order Is Ready");
                            txt.setTextColor(Color.parseColor("#FFFFFFFF"));
                            progBtn.setBackgroundTintList(ContextCompat.getColorStateList(FavouriteActivity.this, R.color.dark_green));
                            txt2.setText("Track Order");
                            img.setImageResource(R.drawable.ic_baseline_my_location_24);
                        }

                        if(delOp.equalsIgnoreCase("Delivery")){
                            FirebaseDatabase.getInstance().getReference("Sellers").child(orderTo).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if(snapshot.exists()){
                                        String delFee=snapshot.child("delfee").getValue(String.class);
                                        delcost.setText("\u20b9 "+delFee);
                                        int dF=Integer.parseInt(delFee);
                                        int tC=Integer.parseInt(totalCost);
                                        String orgCost=String.valueOf(tC-dF);
                                        cost.setText("\u20b9 "+orgCost);

                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        }
                        else{
                            delcost.setText("\u20b9 0");
                            cost.setText("\u20b9 "+totalCost);
                        }
                    }

                    FirebaseDatabase.getInstance().getReference("Orders").child(phoneNo).child("Items").orderByChild("prodid").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot ds : snapshot.getChildren()) {
                                CartProdHelperClass productHelperClass = ds.getValue(CartProdHelperClass.class);
                                productHelperClass1.add(productHelperClass);
                            }
                            adapter = new OrderProductAdapter(FavouriteActivity.this, productHelperClass1);
                            FavouriteRecycler.setAdapter(adapter);

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                    SessionManager sessionManager1 = new SessionManager(FavouriteActivity.this, SessionManager.SESSION_ADDRESS);
                    HashMap<String, String> addDetails = sessionManager1.getAddressDetailFromSession();
                    String address = addDetails.get(SessionManager.KEY_ADD);
                    orderlocation.setText(address);
                }
                else{
                    cartInfoView.setVisibility(View.GONE);
                    CartFullInfo.setVisibility(View.GONE);
                    CartEmptyInfo.setVisibility(View.VISIBLE);
                    area.setVisibility(View.GONE);
                    bill.setVisibility(View.GONE);
                    prog.setVisibility(View.GONE);
                    btnRl.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void TrendingRecycler() {
        productHelperClass2 = new ArrayList<>();
        //get All Products
        FirebaseDatabase.getInstance().getReference("Products").child("Trending").addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                productHelperClass2.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    ProductHelperClass productHelperClass = ds.getValue(ProductHelperClass.class);
                    productHelperClass2.add(productHelperClass);
                }
                adapter = new ProductAdapter_Dashboard(FavouriteActivity.this, productHelperClass2);
                TrendingRecycler.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void TopOffersRecycler() {
        productHelperClass3 = new ArrayList<>();
        //get All Products
        FirebaseDatabase.getInstance().getReference("Products").child("Top Offers").addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                productHelperClass3.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    ProductHelperClass productHelperClass = ds.getValue(ProductHelperClass.class);
                    productHelperClass3.add(productHelperClass);
                }
                adapter = new ProductAdapter_Dashboard(FavouriteActivity.this, productHelperClass3);
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

    private boolean isConnectedToInternet(FavouriteActivity dashboard) {
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
            startActivity(new Intent(FavouriteActivity.this, Dashboard.class));
            finishAffinity();
        }else if(id==R.id.nav_profile){
            Intent profile_intent;
            if (forWhom.equalsIgnoreCase("forUser")) {
                profile_intent = new Intent(FavouriteActivity.this, UserProfile.class);
            } else {
                profile_intent = new Intent(FavouriteActivity.this, SellerProfile.class);
            }
            startActivity(profile_intent);
        }else if(id==R.id.nav_settings){
            startActivity(new Intent(FavouriteActivity.this,Settings.class));
        }else if(id==R.id.nav_logout){
            SessionManager sessionManager1 = new SessionManager(this, SessionManager.SESSION_FORWHO);
            sessionManager1.creatingForWhomSession("forWho");

            SessionManager sessionManager2 = new SessionManager(this, SessionManager.SESSION_ADDRESS);
            sessionManager2.creatingAddressSession("Swagath Rd-Tilaknagar,Banglore-560041");

            SessionManager sessionManager = new SessionManager(this, SessionManager.SESSION_USER);
            sessionManager.logout();
            Intent logout_intent = new Intent(FavouriteActivity.this, Dashboard.class);
            startActivity(logout_intent);
            finishAffinity();
        }else if(id==R.id.nav_category){
            startActivity(new Intent(FavouriteActivity.this,Categories.class));
            finish();
        }else if(id==R.id.nav_basket){
            startActivity(new Intent(FavouriteActivity.this, CartActivity.class));
            finish();
        }
        return true;
    }

    void prepareNotification(String orderId,String orderby,String orderto){
        String TOPIC="/topics/Push_Notification";
        String TITLE="Order "+orderId;
        String MESSAGE="Your Order is Canceled";
        String TYPE="OrderStatus";

        JSONObject jsonObject=new JSONObject();
        JSONObject jsonObjectBody=new JSONObject();
        try{
            jsonObjectBody.put("type",TYPE);
            jsonObjectBody.put("order", orderId);
            jsonObjectBody.put("orderby", orderby);
            jsonObjectBody.put("orderto", orderto);
            jsonObjectBody.put("title", TITLE);
            jsonObjectBody.put("message", MESSAGE);

            jsonObject.put("to",TOPIC);
            jsonObject.put("data",jsonObjectBody);

        }catch (Exception e){
            Toast.makeText(FavouriteActivity.this,""+e.getMessage(),Toast.LENGTH_LONG).show();
        }

        sendNotification(jsonObject);
    }

    private void sendNotification(JSONObject jsonObject) {
        JsonObjectRequest jsonObjectRequest=new JsonObjectRequest("https://fcm.googleapis.com/fcm/send", jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            public Map<String,String> getHeaders() throws AuthFailureError {
                Map<String,String> headers=new HashMap<>();
                headers.put("Content-Type","application/json");
                headers.put("Authorization","key=AAAA8uRayzA:APA91bEykbsck74MlrrSTo07wd84IXg2kHSFua6IAYrZbCK9LKgjDbaRse9O36zrReUVy6qAGv9W-RxmS8N9-xl9lY75c9UYI1sh8zih6Clq5Jdj9rjBGNFpcUYzOKN3IKEVq2nKVHbP");

                return headers;
            }
        };

        Volley.newRequestQueue(FavouriteActivity.this).add(jsonObjectRequest);
    }

}