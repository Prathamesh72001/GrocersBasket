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
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.grocerbasket.Adapters.OrderAdpter;
import com.example.grocerbasket.Adapters.ProductAdapter;
import com.example.grocerbasket.Constructors.OrderHelperClass;
import com.example.grocerbasket.Constructors.ProductHelperClass;
import com.example.grocerbasket.Constructors.SellerHelperClass;
import com.example.grocerbasket.Session.SessionManager;
import com.example.grocerbasket.Typefaces.CustomTypefaceSpan;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Admin_Show_Product extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    ImageView menu,removeme;
    RelativeLayout contentView,productsRl,ordersRl,searchRl;
    static final float END_SCALE = 0.7f;
    TextView tabProducts,tabOrders,catText;
    EditText searchEditText;
    RecyclerView recyclerView,recyclerView2;
    ArrayList<ProductHelperClass> productHelperClasses;
    ArrayList<OrderHelperClass> orderHelperClasses;
    ProductAdapter productAdapter;
    OrderAdpter orderAdapter;
    String category;


    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_show_product);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        //CheckInternetConnection
        if (!isConnectedToInternet(this)) {
            startActivity(new Intent(Admin_Show_Product.this, NoInternet.class));
            finish();
        }

        //hooks
        contentView=findViewById(R.id.contentView);
        drawerLayout = findViewById(R.id.drawerlayout);
        navigationView=findViewById(R.id.navigation_view);
        menu=findViewById(R.id.menu);
        removeme=findViewById(R.id.Removeme);
        tabOrders=findViewById(R.id.allOrders);
        tabProducts=findViewById(R.id.allProducts);
        productsRl=findViewById(R.id.Products_RelativeLayout);
        ordersRl=findViewById(R.id.Orders_RelativeLayout);
        searchEditText=findViewById(R.id.searchEditText);
        catText=findViewById(R.id.categoryText);
        recyclerView=findViewById(R.id.recyclerView);
        recyclerView2=findViewById(R.id.recyclerView2);
        searchRl=findViewById(R.id.searchView);

        //Typeface
        Typeface typeface= ResourcesCompat.getFont(this,R.font.baloo);
        TypefaceSpan typefaceSpan=new CustomTypefaceSpan(typeface);
        SpannableString spannableString=new SpannableString("Search...");
        spannableString.setSpan(typefaceSpan,0,spannableString.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        searchEditText.setHint(spannableString);


        //navigation
        navigationView.bringToFront();
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.nav_home);

        //Tabs
        category="All Products";
        catText.setText(category);
        catText.setVisibility(View.VISIBLE);
        loadProducts();
        ShowProductsRl();

        //Listeners
        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CloseDrawer();
            }
        });

        removeme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SessionManager sessionManager1 = new SessionManager(Admin_Show_Product.this, SessionManager.SESSION_FORWHO);
                sessionManager1.creatingForWhomSession("forWho");

                SessionManager sessionManager = new SessionManager(Admin_Show_Product.this, SessionManager.SESSION_ADMIN);
                sessionManager.logout();
                Intent logout_intent = new Intent(Admin_Show_Product.this, Dashboard.class);
                startActivity(logout_intent);
                finishAffinity();
            }
        });

        tabProducts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowProductsRl();
            }
        });

        tabOrders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowOrdersRl();
            }
        });


        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try{
                    productAdapter.getFilter().filter(s);

                }catch(Exception e){
                    Toast.makeText(Admin_Show_Product.this,"Unable to search",Toast.LENGTH_SHORT).show();
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

        //Animations
        drawerAnim();
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

    private void loadOrders() {
        orderHelperClasses=new ArrayList<>();
        //get All Products
        FirebaseDatabase.getInstance().getReference("Orders").addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                orderHelperClasses.clear();
                for(DataSnapshot ds: snapshot.getChildren()){
                    OrderHelperClass orderHelperClass=ds.getValue(OrderHelperClass.class);
                    orderHelperClasses.add(orderHelperClass);
                }
                orderAdapter=new OrderAdpter(Admin_Show_Product.this,orderHelperClasses);
                recyclerView2.setAdapter(orderAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void loadProducts() {
        productHelperClasses=new ArrayList<>();
        //get All Products
        FirebaseDatabase.getInstance().getReference("Products").child(category).addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                productHelperClasses.clear();
                for(DataSnapshot ds: snapshot.getChildren()){
                    ProductHelperClass productHelperClass=ds.getValue(ProductHelperClass.class);
                    productHelperClasses.add(productHelperClass);
                }
                productAdapter=new ProductAdapter(Admin_Show_Product.this,productHelperClasses);
                recyclerView.setAdapter(productAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void ShowOrdersRl() {
        productsRl.setVisibility(View.GONE);
        searchRl.setVisibility(View.GONE);
        ordersRl.setVisibility(View.VISIBLE);

        tabOrders.setBackgroundResource(R.drawable.selected_shape);
        tabProducts.setBackgroundColor(getResources().getColor(R.color.transparent));

        loadOrders();

    }


    private void ShowProductsRl() {
        productsRl.setVisibility(View.VISIBLE);
        searchRl.setVisibility(View.VISIBLE);
        ordersRl.setVisibility(View.GONE);

        tabProducts.setBackgroundResource(R.drawable.selected_shape);
        tabOrders.setBackgroundColor(getResources().getColor(R.color.transparent));

        loadProducts();

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

    private boolean isConnectedToInternet(Admin_Show_Product dashboard) {
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
        int id=item.getItemId();
        if(id==R.id.nav_add){
            startActivity(new Intent(Admin_Show_Product.this,Admin_Add_Product.class));
            finish();
        }else if(id==R.id.nav_home){
            CloseDrawer();
        }else if(id==R.id.nav_logout){
            SessionManager sessionManager1 = new SessionManager(Admin_Show_Product.this, SessionManager.SESSION_FORWHO);
            sessionManager1.creatingForWhomSession("forWho");

            SessionManager sessionManager = new SessionManager(this, SessionManager.SESSION_USER);
            sessionManager.logout();
            Intent logout_intent = new Intent(Admin_Show_Product.this, Dashboard.class);
            startActivity(logout_intent);
            finishAffinity();
        }
        return true;
    }

}