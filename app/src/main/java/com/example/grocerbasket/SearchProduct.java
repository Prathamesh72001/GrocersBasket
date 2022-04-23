package com.example.grocerbasket;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.speech.RecognizerIntent;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.grocerbasket.Adapters.FavouriteAdapter;
import com.example.grocerbasket.Adapters.ProductAdapter;
import com.example.grocerbasket.Adapters.SearchAdapter;
import com.example.grocerbasket.Constructors.CartHelperClass;
import com.example.grocerbasket.Constructors.OrderHelperClass;
import com.example.grocerbasket.Constructors.ProductHelperClass;
import com.example.grocerbasket.Session.SessionManager;
import com.example.grocerbasket.Typefaces.CustomTypefaceSpan;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class SearchProduct extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    RecyclerView recyclerView;
    SearchAdapter productAdapter;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    String firstname, phoneNo, forWhom;
    RelativeLayout login_or_signup_btn, location, contentView;
    View header;
    Menu menu1;
    static final float END_SCALE = 0.7f;
    EditText searchEditText;
    ImageView menu, add, profile;
    TextView header_text, header_userLoctext;
    SessionManager UsersessionManager, SellersessionManager;
    ArrayList<ProductHelperClass> productHelperClasses;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_product);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //hooks
        menu = findViewById(R.id.menu);
        add = findViewById(R.id.Addme);
        profile = findViewById(R.id.profile);
        contentView = findViewById(R.id.contentView);
        drawerLayout = findViewById(R.id.drawerlayout);
        navigationView = findViewById(R.id.navigation_view);
        recyclerView=findViewById(R.id.recyclerView);

        //CheckInternetConnection
        if (!isConnectedToInternet(this)) {
            startActivity(new Intent(SearchProduct.this, NoInternet.class));
            finish();
        }

        searchEditText=findViewById(R.id.searchEditText);
        recyclerView=findViewById(R.id.recyclerView);

        if(searchEditText.getText().equals(""))
        {
            recyclerView.setVisibility(View.GONE);
        }

        //Typeface
        Typeface typeface= ResourcesCompat.getFont(this,R.font.baloo);
        TypefaceSpan typefaceSpan=new CustomTypefaceSpan(typeface);
        SpannableString spannableString=new SpannableString("Search...");
        spannableString.setSpan(typefaceSpan,0,spannableString.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        searchEditText.setHint(spannableString);

        loadProducts();

        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length()==0){
                    recyclerView.setVisibility(View.GONE);
                }
                else {
                    recyclerView.setVisibility(View.VISIBLE);
                    try {
                        productAdapter.getFilter().filter(s);

                    } catch (Exception e) {
                        Toast.makeText(SearchProduct.this, "Unable to search", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        searchEditText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final  int DRAWABLE_RIGHT=2;
                if (event.getAction()==MotionEvent.ACTION_UP){
                    if(event.getRawX()>=(searchEditText.getRight()-searchEditText.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())){
                        openVoiceDialog();
                        return true;
                    }
                }
                return false;
            }
        });

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
            //Listener
            profile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(SearchProduct.this, UserProfile.class));
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

            }
        }

        SessionManager sessionManager1 = new SessionManager(this, SessionManager.SESSION_ADDRESS);
        HashMap<String, String> addDetails = sessionManager1.getAddressDetailFromSession();
        String address = addDetails.get(SessionManager.KEY_ADD);
        header_userLoctext.setText(address);

        //NavigationView & bottomNavigationView
        navigationView.bringToFront();
        navigationView.setNavigationItemSelectedListener(this);

        //Animations
        drawerAnim();

        //Listeners
        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CloseDrawer();
            }
        });

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SearchProduct.this, Welcome_To_Login_Signup.class));
            }
        });

        login_or_signup_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SearchProduct.this, Welcome_To_Login_Signup.class));
            }
        });

        location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (forWhom.equalsIgnoreCase("forUser")) {
                    SessionManager sessionManager = new SessionManager(SearchProduct.this, SessionManager.SESSION_USER);
                    boolean isLoggedIn = sessionManager.checkLogin();
                    if (isLoggedIn) {
                        //Listener
                        location.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                startActivity(new Intent(SearchProduct.this, AddLocation.class));
                            }
                        });
                    }

                } else if (forWhom.equalsIgnoreCase("forSeller")) {
                    SessionManager sessionManager = new SessionManager(SearchProduct.this, SessionManager.SESSION_SELLER);
                    boolean isLoggedIn = sessionManager.checkLogin();
                    if (isLoggedIn) {
                        //Listener
                        location.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                startActivity(new Intent(SearchProduct.this, AddLocation.class));
                            }
                        });
                    }
                } else {
                    startActivity(new Intent(SearchProduct.this, Welcome_To_Login_Signup.class));
                }
            }
        });
    }

    private void openVoiceDialog() {
        Intent intent=new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        startActivityForResult(intent,200);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==200 && resultCode==RESULT_OK){
            ArrayList<String> arrayList=data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            String voice=arrayList.get(0);
            searchEditText.setText(voice);
            searchEditText.clearFocus();
        }else{
            Toast.makeText(this,"Something went wrong",Toast.LENGTH_LONG).show();
        }
    }



    private void loadProducts() {
        productHelperClasses=new ArrayList<>();
        //get All Products
        FirebaseDatabase.getInstance().getReference("Products").child("All Products").addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                productHelperClasses.clear();
                for(DataSnapshot ds: snapshot.getChildren()){
                    ProductHelperClass productHelperClass=ds.getValue(ProductHelperClass.class);
                    productHelperClasses.add(productHelperClass);
                }
                productAdapter=new SearchAdapter(SearchProduct.this,productHelperClasses);
                recyclerView.setAdapter(productAdapter);
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

    private boolean isConnectedToInternet(SearchProduct dashboard) {
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
                startActivity(new Intent(SearchProduct.this, Dashboard.class));
                finishAffinity();
                break;

            case R.id.nav_profile:
                Intent profile_intent;
                if (forWhom.equalsIgnoreCase("forUser")) {
                    profile_intent = new Intent(SearchProduct.this, UserProfile.class);
                } else {
                    profile_intent = new Intent(SearchProduct.this, SellerProfile.class);
                }
                startActivity(profile_intent);
                break;

            case R.id.nav_settings:
                startActivity(new Intent(SearchProduct.this,Settings.class));
                break;

            case R.id.nav_logout:
                SessionManager sessionManager1 = new SessionManager(this, SessionManager.SESSION_FORWHO);
                sessionManager1.creatingForWhomSession("forWho");

                SessionManager sessionManager2 = new SessionManager(this, SessionManager.SESSION_ADDRESS);
                sessionManager2.creatingAddressSession("Swagath Rd-Tilaknagar,Banglore-560041");

                SessionManager sessionManager = new SessionManager(this, SessionManager.SESSION_USER);
                sessionManager.logout();
                Intent logout_intent = new Intent(SearchProduct.this, Dashboard.class);
                startActivity(logout_intent);
                finishAffinity();
                break;

            case R.id.nav_category:
                startActivity(new Intent(SearchProduct.this,Categories.class));
                break;

            case R.id.nav_aboutus:
                break;

            case R.id.nav_basket:
                startActivity(new Intent(SearchProduct.this,CartActivity.class));
                break;
        }
        return true;
    }
}