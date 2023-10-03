package com.example.grocerbasket;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.grocerbasket.Adapters.FavouriteAdapter;
import com.example.grocerbasket.Adapters.ProductAdapter_Dashboard;
import com.example.grocerbasket.Adapters.ReviewAdapter;
import com.example.grocerbasket.Constructors.CartHelperClass;
import com.example.grocerbasket.Constructors.CartProdHelperClass;
import com.example.grocerbasket.Constructors.ProductHelperClass;
import com.example.grocerbasket.Constructors.ReviewHelperClass;
import com.example.grocerbasket.Session.SessionManager;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Product_Details extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    RecyclerView recyclerView,reviewRecycler;
    RecyclerView.Adapter adapter;
    ReviewAdapter reviewAdapter;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    BottomNavigationView bottomNavigationView;
    String firstname, phoneNo, forWhom, cart_q, cart_f, cart_o,pid,psubcat;
    RelativeLayout login_or_signup_btn, location, contentView, cartInfoRl;
    View header,cartInfoView;
    Menu menu1;
    int count=0;
    static final float END_SCALE = 0.7f;
    ImageView menu, search,icon;
    TextView header_text, header_userLoctext, cartText, priceText, orgpriceText, viewCart,text,no_of_reviews;
    SessionManager UsersessionManager, SellersessionManager;
    ArrayList<ReviewHelperClass> reviewHelperClasses;
    RatingBar revRating,prodRating;
    TextInputLayout prodrev;
    TextView prodname,applicable,non_applicable,proddetail,discountprice,productprice,discount,proddetaildetailed,prodquan,productrating,rupeesign2, quan;
    ImageView prodimage,check_fvrt,uncheck_fvrt, minus, plus,share_btn;
    RelativeLayout discountPriceRl, discountRl, add_btn, quantityLayout, isInStockRl
            ,Be_The_FirstRl,WriteReviewRl,addReviewBtn;
    LinearLayout addReviewRl,ReviewRl;
    ProductHelperClass productHelperClass;
    private String id,name,description,descriptiondetailed,price,discnt,discntprice,quantity,isdiscountavailable,Image,isinstock,timestamp,subcat;
    float rating;
    int finalcost, orgcost;
    private String cat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //hooks
        menu = findViewById(R.id.menu);
        search = findViewById(R.id.Search);
        contentView = findViewById(R.id.contentView);
        drawerLayout = findViewById(R.id.drawerlayout);
        navigationView = findViewById(R.id.navigation_view);
        bottomNavigationView = findViewById(R.id.bottom_nav);
        recyclerView=findViewById(R.id.trendingRV);
        cartInfoRl = findViewById(R.id.cart_barRl);
        cartText = findViewById(R.id.quantity_txt);
        priceText = findViewById(R.id.price_txt);
        orgpriceText = findViewById(R.id.orgprice_txt);
        viewCart = findViewById(R.id.viewCart);
        pid=getIntent().getStringExtra("Product ID");
        psubcat=getIntent().getStringExtra("Product Subcat");
        share_btn=findViewById(R.id.share_btn);

        prodname=findViewById(R.id.prodName);
        proddetail=findViewById(R.id.prodDetails);
        discount=findViewById(R.id.discount);
        discountprice=findViewById(R.id.discountPrice);
        rupeesign2=findViewById(R.id.rupeesign2);
        productprice=findViewById(R.id.productPrice);
        check_fvrt=findViewById(R.id.check_fvrt);
        uncheck_fvrt=findViewById(R.id.uncheck_fvrt);
        prodimage=findViewById(R.id.prodImage);
        isInStockRl=findViewById(R.id.outOfStockRl);
        plus=findViewById(R.id.plus);
        minus=findViewById(R.id.minu);
        quan=findViewById(R.id.quan);
        add_btn=findViewById(R.id.AddBtn);
        quantityLayout=findViewById(R.id.quantitylayout);
        proddetaildetailed=findViewById(R.id.prodDetailsDetailed);
        prodquan=findViewById(R.id.prodQuanity);
        productrating=findViewById(R.id.prodRatingVal);
        discountPriceRl=findViewById(R.id.DiscountPrice);
        discountRl=findViewById(R.id.discountRl);
        prodRating=findViewById(R.id.prodRating);

        icon=findViewById(R.id.icon);
        text=findViewById(R.id.text);

        Be_The_FirstRl=findViewById(R.id.Be_The_First_To_ReviewBtn);
        WriteReviewRl=findViewById(R.id.Write_ReviewBtn);
        addReviewRl=findViewById(R.id.add_reviewRL);
        applicable=findViewById(R.id.applicable);
        non_applicable=findViewById(R.id.not_applicable);
        revRating=findViewById(R.id.revRating);
        prodrev=findViewById(R.id.prodrev);
        no_of_reviews=findViewById(R.id.NoOfReviews);
        addReviewBtn=findViewById(R.id.AddReview);

        ReviewRl=findViewById(R.id.ReviewRL);
        reviewRecycler=findViewById(R.id.reviewRV);
        cartInfoView=findViewById(R.id.cartInfoView);

        //Share Image
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this,new String[]{
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            },1);
        }

        //get Product
        FirebaseDatabase.getInstance().getReference("Products").child("All Products").orderByChild("prodid").equalTo(pid).addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    productHelperClass = ds.getValue(ProductHelperClass.class);
                }
                assert productHelperClass != null;
                //Get Data
                id = productHelperClass.getProdid();
                name = productHelperClass.getProdname();
                description = productHelperClass.getProddescription();
                descriptiondetailed = productHelperClass.getProddescdetailed();
                quantity = productHelperClass.getProdquantity();
                discnt = productHelperClass.getDiscount();
                discntprice = productHelperClass.getDiscountprice();
                price = productHelperClass.getProdprice();
                isdiscountavailable = productHelperClass.getIsdiscountavail();
                Image = productHelperClass.getProdimg();
                isinstock = productHelperClass.getIsInStock();
                timestamp = productHelperClass.getTimeStamp();
                rating = productHelperClass.getRating();
                subcat=productHelperClass.getSubcat();
                cat=productHelperClass.getCat();

                prodname.setText(name);
                proddetail.setText(description);
                productprice.setText(price);
                discountprice.setText(discntprice);
                discount.setText(discnt+"%");
                prodquan.setText(quantity);
                productrating.setText(String.valueOf(rating));
                prodRating.setRating(rating);

                if(descriptiondetailed.equals("")){
                    proddetaildetailed.setText("Product details not found.");
                }
                else{
                    proddetaildetailed.setText(descriptiondetailed);
                }

                if(isdiscountavailable.equalsIgnoreCase("true")){
                    discountPriceRl.setVisibility(View.VISIBLE);
                    discountRl.setVisibility(View.VISIBLE);
                    productprice.setTextSize(13);
                    productprice.setPaintFlags(productprice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    productprice.setTextColor(Color.parseColor("#484848"));

                    rupeesign2.setTextSize(13);
                    rupeesign2.setPaintFlags(rupeesign2.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    rupeesign2.setTextColor(Color.parseColor("#484848"));
                }
                else{
                    discountPriceRl.setVisibility(View.GONE);
                    discountRl.setVisibility(View.GONE);
                }

                if (isinstock.equalsIgnoreCase("true")) {
                    prodimage.setImageAlpha(255);
                    isInStockRl.setVisibility(View.GONE);
                    add_btn.setEnabled(true);
                    add_btn.setBackgroundTintList(ContextCompat.getColorStateList(Product_Details.this, R.color.orange));
                    icon.setImageTintList(ContextCompat.getColorStateList(Product_Details.this, R.color.white));
                    text.setTextColor(Color.parseColor("#FFFFFFFF"));
                }
                else {
                    SessionManager sessionManager = new SessionManager(Product_Details.this, SessionManager.SESSION_FORWHO);
                    HashMap<String, String> forWhoDetails = sessionManager.getForWhomDetailFromSession();
                    String forWhom = forWhoDetails.get(SessionManager.KEY_FORWHO);
                    if (Objects.requireNonNull(forWhom).equalsIgnoreCase("forUser")) {
                        SessionManager UsersessionManager = new SessionManager(Product_Details.this, SessionManager.SESSION_USER);
                        HashMap<String, String> userDetails = UsersessionManager.getUserDetailFromSession();
                        String number = userDetails.get(SessionManager.KEY_PHONENO);

                        boolean isLoggedIn = UsersessionManager.checkLogin();
                        if (isLoggedIn) {
                            FirebaseDatabase rootnode = FirebaseDatabase.getInstance();
                            DatabaseReference reference = rootnode.getReference("Products").child("Cart Products");
                            Query checkuser = reference.orderByChild("userphoneno").equalTo(number);
                            checkuser.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.exists()) {
                                        Query checkproduct = reference.child(number).child("cartProdHelperClass").orderByChild("prodid").equalTo(id);
                                        checkproduct.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot1) {
                                                if (snapshot1.exists()) {
                                                    String prodquan = snapshot.child(number).child("cartProdHelperClass").child(id).child("prodquan").getValue(String.class);
                                                    String prodorgcost = snapshot.child(number).child("cartProdHelperClass").child(id).child("prodorgcost").getValue(String.class);
                                                    String prodfinalcost = snapshot.child(number).child("cartProdHelperClass").child(id).child("prodfinalcost").getValue(String.class);

                                                    removeProductFromCart(id,prodorgcost,prodfinalcost,prodquan,number);
                                                    add_btn.setVisibility(View.VISIBLE);
                                                    quantityLayout.setVisibility(View.GONE);

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

                            FirebaseDatabase.getInstance().getReference("Products").child("Cart Products").child(number).child("cartProdHelperClass").orderByChild("prodid").equalTo(id).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.exists()) {

                                    } else {
                                        add_btn.setVisibility(View.VISIBLE);
                                        quantityLayout.setVisibility(View.GONE);
                                        add_btn.setEnabled(false);
                                        add_btn.setBackgroundTintList(ContextCompat.getColorStateList(Product_Details.this, R.color.light_grey));
                                        icon.setImageTintList(ContextCompat.getColorStateList(Product_Details.this, R.color.black));
                                        text.setTextColor(Color.parseColor("#FF000000"));
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });

                        } else {
                            startActivity(new Intent(Product_Details.this, Welcome_To_Login_Signup.class));

                        }
                    }
                    prodimage.setImageAlpha(50);
                    isInStockRl.setVisibility(View.VISIBLE);

                }

                try {
                    Picasso.get().load(Image).placeholder(R.drawable.default_image).into(prodimage);
                } catch (Exception e) {
                    prodimage.setImageResource(R.drawable.default_image);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        share_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BitmapDrawable drawable=(BitmapDrawable)prodimage.getDrawable();
                Bitmap bitmap=drawable.getBitmap();
                String bitmapPath= MediaStore.Images.Media.insertImage(getContentResolver(),bitmap,"title",null);
                Uri uri=Uri.parse(bitmapPath);
                Intent intent=new Intent(Intent.ACTION_SEND);
                intent.setType("image/png");
                intent.putExtra(Intent.EXTRA_STREAM,uri);
                startActivity(Intent.createChooser(intent,"Share VIA"));
            }
        });

        //Methods
        SimilarRecycler();
        ReviewBar();

        //CheckInternetConnection
        if (!isConnectedToInternet(this)) {
            startActivity(new Intent(Product_Details.this, NoInternet.class));
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

        add_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Product_Details.this, Welcome_To_Login_Signup.class));
            }
        });

        plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Product_Details.this, Welcome_To_Login_Signup.class));
            }
        });

        uncheck_fvrt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Product_Details.this, Welcome_To_Login_Signup.class));
            }
        });

        Be_The_FirstRl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Product_Details.this, Welcome_To_Login_Signup.class));
            }
        });

        WriteReviewRl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Product_Details.this, Welcome_To_Login_Signup.class));
            }
        });

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

                //Listeners
                uncheck_fvrt.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        uncheck_fvrt.setVisibility(View.GONE);
                        check_fvrt.setVisibility(View.VISIBLE);
                        addToFavourite(id, name, description, quantity, price, discnt, discntprice, Image, isdiscountavailable, rating, timestamp, descriptiondetailed, isinstock,phoneNo,subcat,cat);

                        FirebaseDatabase.getInstance().getReference("Products").child("Favourite Products").child(phoneNo).orderByChild("prodid").equalTo(id).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()) {
                                    uncheck_fvrt.setVisibility(View.GONE);
                                    check_fvrt.setVisibility(View.VISIBLE);
                                } else {
                                    check_fvrt.setVisibility(View.GONE);
                                    uncheck_fvrt.setVisibility(View.VISIBLE);
                                }

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                    }
                });

                check_fvrt.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        check_fvrt.setVisibility(View.GONE);
                        uncheck_fvrt.setVisibility(View.VISIBLE);
                        removeFromFavourite(id, phoneNo);

                        FirebaseDatabase.getInstance().getReference("Products").child("Favourite Products").child(phoneNo).orderByChild("prodid").equalTo(id).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()) {
                                    uncheck_fvrt.setVisibility(View.GONE);
                                    check_fvrt.setVisibility(View.VISIBLE);
                                } else {
                                    check_fvrt.setVisibility(View.GONE);
                                    uncheck_fvrt.setVisibility(View.VISIBLE);
                                }

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                    }
                });

                add_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (isdiscountavailable.equals("true")) {
                            finalcost = Integer.parseInt(discntprice);
                        } else {
                            finalcost = Integer.parseInt(price);
                        }
                        orgcost = Integer.parseInt(price);
                        quan.setText(String.valueOf(1));
                        add_btn.setVisibility(View.GONE);
                        quantityLayout.setVisibility(View.VISIBLE);
                        addToCart(id, name, description, quantity, price, discnt, discntprice, Image, isdiscountavailable, rating, timestamp, descriptiondetailed, isinstock,subcat,cat, orgcost, finalcost, 1, phoneNo);

                    }
                });

                plus.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        FirebaseDatabase rootnode = FirebaseDatabase.getInstance();
                        DatabaseReference reference = rootnode.getReference("Products").child("Cart Products");
                        Query checkuser = reference.orderByChild("userphoneno").equalTo(phoneNo);
                        checkuser.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()) {
                                    Query checkproduct = reference.child(phoneNo).child("cartProdHelperClass").orderByChild("prodid").equalTo(id);
                                    checkproduct.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot1) {
                                            if (snapshot1.exists()) {
                                                String prodquan = snapshot.child(phoneNo).child("cartProdHelperClass").child(id).child("prodquan").getValue(String.class);

                                                int p_quan = Integer.parseInt(prodquan);
                                                p_quan++;
                                                quan.setText(String.valueOf(p_quan));
                                                if (isdiscountavailable.equals("true")) {
                                                    finalcost = Integer.parseInt(discntprice);
                                                } else {
                                                    finalcost = Integer.parseInt(price);
                                                }
                                                orgcost = Integer.parseInt(price);
                                                addToCart(id, name, description, quantity, price, discnt, discntprice, Image, isdiscountavailable, rating, timestamp, descriptiondetailed, isinstock,subcat,cat, orgcost, finalcost, 1, phoneNo);
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

                        FirebaseDatabase.getInstance().getReference("Products").child("Cart Products").child(phoneNo).child("cartProdHelperClass").orderByChild("prodid").equalTo(id).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()) {
                                    for (DataSnapshot ds : snapshot.getChildren()) {
                                        CartProdHelperClass cartProdHelperClass = ds.getValue(CartProdHelperClass.class);
                                        String prodquan = cartProdHelperClass.getProdquan();

                                        add_btn.setVisibility(View.GONE);
                                        quantityLayout.setVisibility(View.VISIBLE);

                                        quan.setText(prodquan);

                                    }
                                } else {
                                    add_btn.setVisibility(View.VISIBLE);
                                    quantityLayout.setVisibility(View.GONE);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                });

                minus.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //Session
                        SessionManager sessionManager = new SessionManager(Product_Details.this, SessionManager.SESSION_FORWHO);
                        HashMap<String, String> forWhoDetails = sessionManager.getForWhomDetailFromSession();
                        String forWhom = forWhoDetails.get(SessionManager.KEY_FORWHO);
                        if (Objects.requireNonNull(forWhom).equalsIgnoreCase("forUser")) {
                            SessionManager UsersessionManager = new SessionManager(Product_Details.this, SessionManager.SESSION_USER);
                            HashMap<String, String> userDetails = UsersessionManager.getUserDetailFromSession();
                            String number = userDetails.get(SessionManager.KEY_PHONENO);

                            boolean isLoggedIn = UsersessionManager.checkLogin();
                            if (isLoggedIn) {
                                FirebaseDatabase rootnode = FirebaseDatabase.getInstance();
                                DatabaseReference reference = rootnode.getReference("Products").child("Cart Products");
                                Query checkuser = reference.orderByChild("userphoneno").equalTo(number);
                                checkuser.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if (snapshot.exists()) {
                                            Query checkproduct = reference.child(number).child("cartProdHelperClass").orderByChild("prodid").equalTo(id);
                                            checkproduct.addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot1) {
                                                    if (snapshot1.exists()) {
                                                        String prodquan = snapshot.child(number).child("cartProdHelperClass").child(id).child("prodquan").getValue(String.class);

                                                        int p_quan = Integer.parseInt(prodquan);
                                                        p_quan--;
                                                        if (p_quan < 1) {
                                                            if (isdiscountavailable.equals("true")) {
                                                                finalcost = Integer.parseInt(discntprice);
                                                            } else {
                                                                finalcost = Integer.parseInt(price);
                                                            }
                                                            orgcost = Integer.parseInt(price);
                                                            add_btn.setVisibility(View.VISIBLE);
                                                            quantityLayout.setVisibility(View.GONE);

                                                        } else {
                                                            if (isdiscountavailable.equals("true")) {
                                                                finalcost = Integer.parseInt(discntprice);
                                                            } else {
                                                                finalcost = Integer.parseInt(price);
                                                            }
                                                            orgcost = Integer.parseInt(price);
                                                        }
                                                        quan.setText(String.valueOf(p_quan));

                                                        removeFromCart(id, orgcost, finalcost, 1, number);

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

                                FirebaseDatabase.getInstance().getReference("Products").child("Cart Products").child(number).child("cartProdHelperClass").orderByChild("prodid").equalTo(id).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if (snapshot.exists()) {
                                            for (DataSnapshot ds : snapshot.getChildren()) {
                                                CartProdHelperClass cartProdHelperClass = ds.getValue(CartProdHelperClass.class);
                                                String prodquan = cartProdHelperClass.getProdquan();

                                                add_btn.setVisibility(View.GONE);
                                                quantityLayout.setVisibility(View.VISIBLE);

                                                quan.setText(prodquan);

                                            }
                                        } else {
                                            add_btn.setVisibility(View.VISIBLE);
                                            quantityLayout.setVisibility(View.GONE);
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });

                            } else {
                                startActivity(new Intent(Product_Details.this, Welcome_To_Login_Signup.class));
                            }
                        }
                    }
                });

                Be_The_FirstRl.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        applicable.setVisibility(View.VISIBLE);
                        revRating.setVisibility(View.VISIBLE);
                        prodrev.setVisibility(View.VISIBLE);
                        addReviewBtn.setVisibility(View.VISIBLE);
                        Be_The_FirstRl.setVisibility(View.GONE);
                    }
                });

                WriteReviewRl.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        applicable.setVisibility(View.VISIBLE);
                        revRating.setVisibility(View.VISIBLE);
                        prodrev.setVisibility(View.VISIBLE);
                        addReviewBtn.setVisibility(View.VISIBLE);
                        WriteReviewRl.setVisibility(View.GONE);
                    }
                });

                addReviewBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String r=prodrev.getEditText().getText().toString();
                        if(r.isEmpty() || revRating.getRating()==0){
                            Toast.makeText(Product_Details.this,"Please give your review",Toast.LENGTH_LONG).show();
                        }
                        else{
                            inputData();
                        }
                    }
                });

                FirebaseDatabase.getInstance().getReference("Products").child("Cart Products").child(phoneNo).child("cartProdHelperClass").orderByChild("prodid").equalTo(pid).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            for (DataSnapshot ds : snapshot.getChildren()) {
                                CartProdHelperClass cartProdHelperClass = ds.getValue(CartProdHelperClass.class);
                                String prodquan = cartProdHelperClass.getProdquan();

                                add_btn.setVisibility(View.GONE);
                                quantityLayout.setVisibility(View.VISIBLE);

                                quan.setText(prodquan);

                            }
                        } else {
                            add_btn.setVisibility(View.VISIBLE);
                            quantityLayout.setVisibility(View.GONE);
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                FirebaseDatabase.getInstance().getReference("Products").child("Favourite Products").child(phoneNo).orderByChild("prodid").equalTo(pid).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            uncheck_fvrt.setVisibility(View.GONE);
                            check_fvrt.setVisibility(View.VISIBLE);
                        } else {
                            check_fvrt.setVisibility(View.GONE);
                            uncheck_fvrt.setVisibility(View.VISIBLE);
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });



            }
        }

        SessionManager sessionManager1 = new SessionManager(this, SessionManager.SESSION_ADDRESS);
        HashMap<String, String> addDetails = sessionManager1.getAddressDetailFromSession();
        String address = addDetails.get(SessionManager.KEY_ADD);
        header_userLoctext.setText(address);

        //NavigationView & bottomNavigationView
        navigationView.bringToFront();
        navigationView.setNavigationItemSelectedListener(this);

        bottomNavigationView.setItemIconTintList(null);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nav_bot_home:
                        startActivity(new Intent(Product_Details.this, Dashboard.class));
                        finishAffinity();
                        break;

                    case R.id.nav_bot_cat:
                        startActivity(new Intent(Product_Details.this,Categories.class));
                        break;

                    case R.id.nav_bot_search:
                        startActivity(new Intent(Product_Details.this,SearchProduct.class));
                        break;

                    case R.id.nav_bot_list:
                        startActivity(new Intent(Product_Details.this,FavouriteActivity.class));
                        break;

                    case R.id.nav_bot_basket:
                        startActivity(new Intent(Product_Details.this,CartActivity.class));
                        break;
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
                startActivity(new Intent(Product_Details.this, Welcome_To_Login_Signup.class));
            }
        });

        viewCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Product_Details.this, CartActivity.class));
            }
        });

        location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (forWhom.equalsIgnoreCase("forUser")) {
                    SessionManager sessionManager = new SessionManager(Product_Details.this, SessionManager.SESSION_USER);
                    boolean isLoggedIn = sessionManager.checkLogin();
                    if (isLoggedIn) {
                        //Listener
                        location.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                startActivity(new Intent(Product_Details.this, AddLocation.class));
                            }
                        });
                    }

                } else if (forWhom.equalsIgnoreCase("forSeller")) {
                    SessionManager sessionManager = new SessionManager(Product_Details.this, SessionManager.SESSION_SELLER);
                    boolean isLoggedIn = sessionManager.checkLogin();
                    if (isLoggedIn) {
                        //Listener
                        location.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                startActivity(new Intent(Product_Details.this, AddLocation.class));
                            }
                        });
                    }
                } else {
                    startActivity(new Intent(Product_Details.this, Welcome_To_Login_Signup.class));
                }
            }
        });


    }

    private void ReviewBar() {
        reviewHelperClasses=new ArrayList<>();
        FirebaseDatabase.getInstance().getReference("Product Reviews").child(pid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    Be_The_FirstRl.setVisibility(View.GONE);
                    WriteReviewRl.setVisibility(View.VISIBLE);
                    ReviewRl.setVisibility(View.VISIBLE);
                    reviewHelperClasses.clear();
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        ReviewHelperClass reviewHelperClass=ds.getValue(ReviewHelperClass.class);
                        reviewHelperClasses.add(reviewHelperClass);
                        count=count+1;
                    }
                    no_of_reviews.setText(String.valueOf(snapshot.getChildrenCount()));
                    reviewAdapter = new ReviewAdapter(Product_Details.this,reverseArrayList(reviewHelperClasses));
                    reviewRecycler.setAdapter(reviewAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public ArrayList<ReviewHelperClass> reverseArrayList(ArrayList<ReviewHelperClass> alist)
    {
        // Arraylist for storing reversed elements
        ArrayList<ReviewHelperClass> revArrayList = new ArrayList<ReviewHelperClass>();
        for (int i = alist.size() - 1; i >= 0; i--) {

            // Append the elements in reverse order
            revArrayList.add(alist.get(i));
        }

        // Return the reversed arraylist
        return revArrayList;
    }

    private void inputData() {
        float Prating= Float.parseFloat(""+revRating.getRating());
        String review=""+prodrev.getEditText().getText().toString();
        String timeStamp=""+System.currentTimeMillis();

        ReviewHelperClass Review=new ReviewHelperClass(phoneNo,Prating,review,timeStamp,pid);

        FirebaseDatabase.getInstance().getReference("Product Reviews").child(pid).child(timeStamp).setValue(Review).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                if(rating==0.0){
                    DecimalFormat df = new DecimalFormat("#.#");
                    String d = df.format(Prating);
                    FirebaseDatabase.getInstance().getReference("Products").child("All Products").child(pid).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(snapshot.exists()){
                                FirebaseDatabase.getInstance().getReference("Products").child("All Products").child(pid).child("rating").setValue(Float.parseFloat(d));
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                    FirebaseDatabase.getInstance().getReference("Products").child("Top Offers").child(pid).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(snapshot.exists()){
                                FirebaseDatabase.getInstance().getReference("Products").child("Top Offers").child(pid).child("rating").setValue(Float.parseFloat(d));
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                    FirebaseDatabase.getInstance().getReference("Products").child("Trending").child(pid).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(snapshot.exists()){
                                FirebaseDatabase.getInstance().getReference("Products").child("Trending").child(pid).child("rating").setValue(Float.parseFloat(d));
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                    FirebaseDatabase.getInstance().getReference("Products").child("Cart Products").child(phoneNo).child("cartProdHelperClass").child(pid).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(snapshot.exists()){
                                FirebaseDatabase.getInstance().getReference("Products").child("Cart Products").child(phoneNo).child("cartProdHelperClass").child(pid).child("rating").setValue(Float.parseFloat(d));
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                    FirebaseDatabase.getInstance().getReference("Products").child("Favourite Products").child(phoneNo).child(pid).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(snapshot.exists()){
                                FirebaseDatabase.getInstance().getReference("Products").child("Favourite Products").child(phoneNo).child(pid).child("rating").setValue(Float.parseFloat(d));
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                    FirebaseDatabase.getInstance().getReference("Orders").child(phoneNo).child("Items").child(pid).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(snapshot.exists()){
                                FirebaseDatabase.getInstance().getReference("Orders").child(phoneNo).child("Items").child(pid).child("rating").setValue(Float.parseFloat(d));
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                }
                else{
                    float new_rating=(rating+Prating)/2;
                    DecimalFormat df = new DecimalFormat("#.#");
                    String d = df.format(new_rating);
                    FirebaseDatabase.getInstance().getReference("Products").child("All Products").child(pid).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(snapshot.exists()){
                                FirebaseDatabase.getInstance().getReference("Products").child("All Products").child(pid).child("rating").setValue(Float.parseFloat(d));
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                    FirebaseDatabase.getInstance().getReference("Products").child("Top Offers").child(pid).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(snapshot.exists()){
                                FirebaseDatabase.getInstance().getReference("Products").child("Top Offers").child(pid).child("rating").setValue(Float.parseFloat(d));
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                    FirebaseDatabase.getInstance().getReference("Products").child("Trending").child(pid).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(snapshot.exists()){
                                FirebaseDatabase.getInstance().getReference("Products").child("Trending").child(pid).child("rating").setValue(Float.parseFloat(d));
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                    FirebaseDatabase.getInstance().getReference("Products").child("Cart Products").child(phoneNo).child("cartProdHelperClass").child(pid).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(snapshot.exists()){
                                FirebaseDatabase.getInstance().getReference("Products").child("Cart Products").child(phoneNo).child("cartProdHelperClass").child(pid).child("rating").setValue(Float.parseFloat(d));
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                    FirebaseDatabase.getInstance().getReference("Products").child("Favourite Products").child(phoneNo).child(pid).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(snapshot.exists()){
                                FirebaseDatabase.getInstance().getReference("Products").child("Favourite Products").child(phoneNo).child(pid).child("rating").setValue(Float.parseFloat(d));
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                    FirebaseDatabase.getInstance().getReference("Orders").child(phoneNo).child("Items").child(pid).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(snapshot.exists()){
                                FirebaseDatabase.getInstance().getReference("Orders").child(phoneNo).child("Items").child(pid).child("rating").setValue(Float.parseFloat(d));
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                }
                FirebaseDatabase.getInstance().getReference("User Reviews").child(phoneNo).child(timeStamp).setValue(Review);
                Toast.makeText(Product_Details.this,"Review added successfully",Toast.LENGTH_LONG).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
               Toast.makeText(Product_Details.this,"Unable to upload review",Toast.LENGTH_LONG).show();
            }
        });

        applicable.setVisibility(View.GONE);
        revRating.setRating(0);
        prodrev.getEditText().setText("");
        revRating.setVisibility(View.GONE);
        prodrev.setVisibility(View.GONE);
        addReviewBtn.setVisibility(View.GONE);
    }


    private void SimilarRecycler() {
        ArrayList<ProductHelperClass> productClass = new ArrayList<>();
        //get All Products
        FirebaseDatabase.getInstance().getReference("Products").child("All Products").orderByChild("subcat").equalTo(psubcat).addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                productClass.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    ProductHelperClass productHelperClasses = ds.getValue(ProductHelperClass.class);
                    productClass.add(productHelperClasses);
                }
                adapter = new ProductAdapter_Dashboard(Product_Details.this, productClass);
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void removeProductFromCart(String id,String prodorgcost,String prodfinalcost,String prodquan, String number) {
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

                    int c_final = Integer.parseInt(cartfinalcost) - Integer.parseInt(prodfinalcost);
                    int c_org = Integer.parseInt(cartorgcost) - Integer.parseInt(prodorgcost);
                    int c_quan = Integer.parseInt(cartquan) - Integer.parseInt(prodquan);
                    if (c_quan < 1) {
                        reference.child(number).removeValue();
                    } else {
                        reference.child(number).child("cartProdHelperClass").child(id).removeValue();
                        reference.child(number).child("cartquan").setValue(String.valueOf(c_quan));
                        reference.child(number).child("cartfinalcost").setValue(String.valueOf(c_final));
                        reference.child(number).child("cartorgcost").setValue(String.valueOf(c_org));
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private void removeFromFavourite(String id, String number) {
        FirebaseDatabase rootnode = FirebaseDatabase.getInstance();
        DatabaseReference reference = rootnode.getReference("Products").child("Favourite Products");
        reference.child(number).child(id).removeValue();
    }

    private void addToFavourite(String id, String name, String description, String quantity, String price, String discount, String discountprice, String image, String isdiscountavailable, float rating, String timestamp, String descriptiondetailed, String isinstock, String number,String subcat,String cat) {
        FirebaseDatabase rootnode = FirebaseDatabase.getInstance();
        DatabaseReference reference = rootnode.getReference("Products").child("Favourite Products");
        ProductHelperClass productHelperClass = new ProductHelperClass(id,name,description,quantity,price,discount,discountprice,image,isdiscountavailable,rating,timestamp,descriptiondetailed,isinstock,subcat,cat);
        reference.child(number).child(id).setValue(productHelperClass);
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
                                    String prodquan = snapshot.child(number).child("cartProdHelperClass").child(id).child("prodquan").getValue(String.class);
                                    String prodfinalcost = snapshot.child(number).child("cartProdHelperClass").child(id).child("prodfinalcost").getValue(String.class);
                                    String prodorgcost = snapshot.child(number).child("cartProdHelperClass").child(id).child("prodorgcost").getValue(String.class);
                                    int p_quan = Integer.parseInt(prodquan) - quan;
                                    int p_final = Integer.parseInt(prodfinalcost) - finalcost;
                                    int p_org = Integer.parseInt(prodorgcost) - orgcost;

                                    if (p_quan < 1) {
                                        reference.child(number).child("cartProdHelperClass").child(id).removeValue();
                                    } else {
                                        reference.child(number).child("cartProdHelperClass").child(id).child("prodquan").setValue(String.valueOf(p_quan));
                                        reference.child(number).child("cartProdHelperClass").child(id).child("prodfinalcost").setValue(String.valueOf(p_final));
                                        reference.child(number).child("cartProdHelperClass").child(id).child("prodorgcost").setValue(String.valueOf(p_org));
                                    }
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


    private void addToCart(String prodid, String prodname, String proddescription, String prodquantity, String prodprice,
                           String discount, String discountprice, String prodimg, String isdiscountavail, float rating,
                           String timeStamp, String proddescdetailed, String isInStock,String subcat,String cat, int orgcost, int finalcost, int quan, String number) {
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
                    Query checkproduct = reference.child(number).child("cartProdHelperClass").orderByChild("prodid").equalTo(prodid);
                    checkproduct.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot1) {
                            if (snapshot1.exists()) {
                                String prodquan = snapshot.child(number).child("cartProdHelperClass").child(prodid).child("prodquan").getValue(String.class);
                                String prodfinalcost = snapshot.child(number).child("cartProdHelperClass").child(prodid).child("prodfinalcost").getValue(String.class);
                                String prodorgcost = snapshot.child(number).child("cartProdHelperClass").child(prodid).child("prodorgcost").getValue(String.class);
                                int p_quan = Integer.parseInt(prodquan) + quan;
                                int p_final = Integer.parseInt(prodfinalcost) + finalcost;
                                int p_org = Integer.parseInt(prodorgcost) + orgcost;

                                reference.child(number).child("cartProdHelperClass").child(prodid).child("prodquan").setValue(String.valueOf(p_quan));
                                reference.child(number).child("cartProdHelperClass").child(prodid).child("prodfinalcost").setValue(String.valueOf(p_final));
                                reference.child(number).child("cartProdHelperClass").child(prodid).child("prodorgcost").setValue(String.valueOf(p_org));
                            } else {
                                CartProdHelperClass cartProdHelperClass = new CartProdHelperClass(prodid, prodname, proddescription, prodquantity, prodprice
                                        , discount, discountprice, prodimg, isdiscountavail, rating, timeStamp, proddescdetailed, isInStock,subcat,cat,
                                        String.valueOf(quan), String.valueOf(orgcost), String.valueOf(finalcost));
                                reference.child(number).child("cartProdHelperClass").child(prodid).setValue(cartProdHelperClass);
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
                    CartProdHelperClass cartProdHelperClass = new CartProdHelperClass(prodid, prodname, proddescription, prodquantity, prodprice
                            , discount, discountprice, prodimg, isdiscountavail, rating, timeStamp, proddescdetailed, isInStock,subcat,cat,
                            String.valueOf(quan), String.valueOf(orgcost), String.valueOf(finalcost));
                    Map<String, CartProdHelperClass> cartitem = new HashMap<String, CartProdHelperClass>();
                    cartitem.put(prodid, cartProdHelperClass);
                    CartHelperClass cartHelperClass = new CartHelperClass(cartitem, String.valueOf(orgcost), String.valueOf(finalcost), String.valueOf(quan), number);
                    reference.child(number).setValue(cartHelperClass);
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

    private boolean isConnectedToInternet(Product_Details dashboard) {
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
                startActivity(new Intent(Product_Details.this, Dashboard.class));
                finishAffinity();
                break;

            case R.id.nav_profile:
                Intent profile_intent;
                if (forWhom.equalsIgnoreCase("forUser")) {
                    profile_intent = new Intent(Product_Details.this, UserProfile.class);
                } else {
                    profile_intent = new Intent(Product_Details.this, SellerProfile.class);
                }
                startActivity(profile_intent);
                break;

            case R.id.nav_settings:
                startActivity(new Intent(Product_Details.this,Settings.class));
                break;

            case R.id.nav_logout:
                SessionManager sessionManager1 = new SessionManager(this, SessionManager.SESSION_FORWHO);
                sessionManager1.creatingForWhomSession("forWho");

                SessionManager sessionManager2 = new SessionManager(this, SessionManager.SESSION_ADDRESS);
                sessionManager2.creatingAddressSession("Swagath Rd-Tilaknagar,Banglore-560041");

                SessionManager sessionManager = new SessionManager(this, SessionManager.SESSION_USER);
                sessionManager.logout();
                Intent logout_intent = new Intent(Product_Details.this, Dashboard.class);
                startActivity(logout_intent);
                finishAffinity();
                break;

            case R.id.nav_category:
                startActivity(new Intent(Product_Details.this,Categories.class));
                break;

            case R.id.nav_aboutus:
                break;

            case R.id.nav_basket:
                startActivity(new Intent(Product_Details.this,CartActivity.class));
                break;
        }
        return true;
    }
}