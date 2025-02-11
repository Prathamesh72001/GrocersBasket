package com.example.grocerbasket;

import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.grocerbasket.Constructors.ProductHelperClass;
import com.example.grocerbasket.Session.SessionManager;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Objects;

public class Admin_Add_Product extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    ImageView prodImg,menu,removeme;
    RelativeLayout addProd, progressBar,contentView;
    TextInputLayout prodName, prodDesc,prodDescDetailed,prodQuantity, price, discount, discountPrice;
    TextInputEditText discountEditText,priceEditText;
    SwitchCompat isHaveDiscount,isInStock;
    CheckBox checkBox_trend, checkBox_top;
    RelativeLayout cat,sub_cat;
    TextView cat_text,subcat_text;
    String prodNm,prodDes,prodDesDetailed,prodQuan,prodPrice,discPrice,disc;

    Uri image_uri;
    static final float END_SCALE = 0.7f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_add_product);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //hooks
        prodImg = findViewById(R.id.ProductImage);
        addProd = findViewById(R.id.Addbtn);
        prodName = findViewById(R.id.productname);
        prodDesc = findViewById(R.id.proddesc);
        prodDescDetailed = findViewById(R.id.proddescdetailed);
        prodQuantity=findViewById(R.id.quantity);
        discount = findViewById(R.id.discount);
        price = findViewById(R.id.price);
        discountPrice = findViewById(R.id.discprice);
        isHaveDiscount = findViewById(R.id.isHaveDiscount);
        checkBox_trend = findViewById(R.id.cat_trnding);
        checkBox_top = findViewById(R.id.cat_top_offers);
        progressBar = findViewById(R.id.progressBar);
        contentView=findViewById(R.id.contentView);
        drawerLayout = findViewById(R.id.drawerlayout);
        navigationView=findViewById(R.id.navigation_view);
        menu=findViewById(R.id.menu);
        removeme=findViewById(R.id.Removeme);
        discountEditText=findViewById(R.id.editText);
        priceEditText=findViewById(R.id.editText2);
        isInStock=findViewById(R.id.isInStock);
        cat=findViewById(R.id.relativeLayout4);
        sub_cat=findViewById(R.id.relativeLayout5);
        cat_text=findViewById(R.id.cat_text);
        subcat_text=findViewById(R.id.subcat_text);

        //navigation
        navigationView.bringToFront();
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.nav_add);

        //Visibility
        discount.setVisibility(View.GONE);
        discountPrice.setVisibility(View.GONE);

        //Listeners
        cat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(v.getContext(), v);
                popupMenu.inflate(R.menu.category_popup_menu);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        cat_text.setText(item.getTitle().toString());
                        return false;
                    }
                });
                popupMenu.show();
            }
        });

        sub_cat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String catTxt = cat_text.getText().toString();
                PopupMenu popupMenu = new PopupMenu(v.getContext(), v);
                if (catTxt.equals("Fruits & Vegetables")) {
                    popupMenu.inflate(R.menu.fruits_veg_sub);
                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            subcat_text.setText(item.getTitle().toString());
                            return false;
                        }
                    });
                    popupMenu.show();
                } else if (catTxt.equals("Beverages")) {
                    popupMenu.inflate(R.menu.bev_sub);
                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            subcat_text.setText(item.getTitle().toString());
                            return false;
                        }
                    });
                    popupMenu.show();
                } else if (catTxt.equals("Foodgrains, Oil & Masala")) {
                    popupMenu.inflate(R.menu.foodgrain_oil_masala_sub);
                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            subcat_text.setText(item.getTitle().toString());
                            return false;
                        }
                    });
                    popupMenu.show();
                } else if (catTxt.equals("Bakery, Cakes & Diary")) {
                    popupMenu.inflate(R.menu.bakery_sub);
                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            subcat_text.setText(item.getTitle().toString());
                            return false;
                        }
                    });
                    popupMenu.show();
                }
                else if (catTxt.equals("Snacks & Branded Foods")) {
                    popupMenu.inflate(R.menu.snacks_sub);
                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            subcat_text.setText(item.getTitle().toString());
                            return false;
                        }
                    });
                    popupMenu.show();
                }
                else if (catTxt.equals("Beauty & Hygiene")) {
                    popupMenu.inflate(R.menu.beauty_hygiene_sub);
                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            subcat_text.setText(item.getTitle().toString());
                            return false;
                        }
                    });
                    popupMenu.show();
                }
                else if (catTxt.equals("Cleaning & Household")) {
                    popupMenu.inflate(R.menu.cleaning_sub);
                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            subcat_text.setText(item.getTitle().toString());
                            return false;
                        }
                    });
                    popupMenu.show();
                }

            }
        });

        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CloseDrawer();
            }
        });

        removeme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SessionManager sessionManager1 = new SessionManager(Admin_Add_Product.this, SessionManager.SESSION_FORWHO);
                sessionManager1.creatingForWhomSession("forWho");

                SessionManager sessionManager = new SessionManager(Admin_Add_Product.this, SessionManager.SESSION_ADMIN);
                sessionManager.logout();
                Intent logout_intent = new Intent(Admin_Add_Product.this, Dashboard.class);
                startActivity(logout_intent);
                finishAffinity();
            }
        });

        prodImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //CheckInternetConnection
                if (!isConnectedToInternet(Admin_Add_Product.this)) {
                    startActivity(new Intent(Admin_Add_Product.this, NoInternet.class));
                    finish();
                }
                pickImgFromGallery();

            }
        });

        addProd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UpdateData();
            }
        });

        isHaveDiscount.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    discount.setVisibility(View.VISIBLE);
                    discountPrice.setVisibility(View.VISIBLE);
                }else{
                    discount.setVisibility(View.GONE);
                    discountPrice.setVisibility(View.GONE);
                }
            }
        });

        discountEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length()!=0)
                {
                    int d=Integer.parseInt(String.valueOf(s));
                    int p=Integer.parseInt(String.valueOf(priceEditText.getText().toString()));
                    int dp= (int) (p-(p*((float)d/100)));
                    discountPrice.getEditText().setText(String.valueOf(dp));

                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        //Animations
        drawerAnim();

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


    private void UpdateData() {

        //Validation
        if (!validateProdName() | !validateProdDesc() | !validateProdQuantity() | !validateProdPrice() | !validateCat() | !validateSubCat()) {
            return;
        }

        //Progress
        progressBar.setVisibility(View.VISIBLE);

        //Strings
        prodNm=prodName.getEditText().getText().toString();
        prodDes=prodDesc.getEditText().getText().toString();
        prodDesDetailed=prodDescDetailed.getEditText().getText().toString();
        prodQuan=prodQuantity.getEditText().getText().toString();
        prodPrice=price.getEditText().getText().toString();
        disc=discount.getEditText().getText().toString();
        discPrice=discountPrice.getEditText().getText().toString();
        final String timeStamp=""+System.currentTimeMillis();
        boolean isdiscavail=isHaveDiscount.isChecked();
        boolean isinstock=isInStock.isChecked();
        float rating=0;

        String top=checkBox_top.getText().toString();
        String trend=checkBox_trend.getText().toString();


        //isDiscountApplicable
        if(isHaveDiscount.isChecked()){
            if(!validateProdDisc() | !validateProdDiscPrice()){
                return;
            }
        }
        else{
            disc="";
            discPrice="0";
        }

        if (image_uri == null) {
            FirebaseDatabase rootnode = FirebaseDatabase.getInstance();
            DatabaseReference reference = rootnode.getReference("Products");

            ProductHelperClass addNewProduct=new ProductHelperClass(timeStamp,prodNm,prodDes,prodQuan,prodPrice,disc,discPrice,"",Boolean.toString(isdiscavail),rating,timeStamp,prodDesDetailed,Boolean.toString(isinstock),subcat_text.getText().toString(),cat_text.getText().toString());

            reference.child("All Products").child(timeStamp).setValue(addNewProduct).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    //Progress
                    progressBar.setVisibility(View.GONE);
                    //

                    //Top Offers
                    if(checkBox_top.isChecked())
                    {
                        ProductHelperClass addNewProduct=new ProductHelperClass(timeStamp,prodNm,prodDes,prodQuan,prodPrice,disc,discPrice,"",Boolean.toString(isdiscavail),rating,timeStamp,prodDesDetailed,Boolean.toString(isinstock),subcat_text.getText().toString(),cat_text.getText().toString());
                        reference.child(top).child(timeStamp).setValue(addNewProduct);
                    }

                    //Trending
                    if(checkBox_trend.isChecked())
                    {
                        ProductHelperClass addNewProduct=new ProductHelperClass(timeStamp,prodNm,prodDes,prodQuan,prodPrice,disc,discPrice,"",Boolean.toString(isdiscavail),rating,timeStamp,prodDesDetailed,Boolean.toString(isinstock),subcat_text.getText().toString(),cat_text.getText().toString());
                        reference.child(trend).child(timeStamp).setValue(addNewProduct);
                    }


                    Toast.makeText(Admin_Add_Product.this,"Product Details Uploaded Successfully",Toast.LENGTH_SHORT).show();

                    clearData();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    //Progress
                    progressBar.setVisibility(View.GONE);
                    //

                    Toast.makeText(Admin_Add_Product.this,""+e.getMessage(),Toast.LENGTH_SHORT).show();
                }
            });


        } else {
            String filePathAndName="Product Images/"+""+timeStamp;
            StorageReference storageReference=FirebaseStorage.getInstance().getReference(filePathAndName);
            storageReference.putFile(image_uri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Task<Uri> uriTask=taskSnapshot.getStorage().getDownloadUrl();
                            while (!uriTask.isSuccessful());
                            Uri downloadUri=uriTask.getResult();
                            String imgURL=downloadUri.toString();

                            if(uriTask.isSuccessful()){
                                FirebaseDatabase rootnode = FirebaseDatabase.getInstance();
                                DatabaseReference reference = rootnode.getReference("Products");

                                ProductHelperClass addNewProduct=new ProductHelperClass(timeStamp,prodNm,prodDes,prodQuan,prodPrice,disc,discPrice,imgURL,Boolean.toString(isdiscavail),rating,timeStamp,prodDesDetailed,Boolean.toString(isinstock),subcat_text.getText().toString(),cat_text.getText().toString());

                                reference.child("All Products").child(timeStamp).setValue(addNewProduct).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        //Progress
                                        progressBar.setVisibility(View.GONE);
                                        //

                                        //Top Offers
                                        if(checkBox_top.isChecked())
                                        {
                                            ProductHelperClass addNewProduct=new ProductHelperClass(timeStamp,prodNm,prodDes,prodQuan,prodPrice,disc,discPrice,imgURL,Boolean.toString(isdiscavail),rating,timeStamp,prodDesDetailed,Boolean.toString(isinstock),subcat_text.getText().toString(),cat_text.getText().toString());
                                            reference.child(top).child(timeStamp).setValue(addNewProduct);
                                        }

                                        //Trending
                                        if(checkBox_trend.isChecked())
                                        {
                                            ProductHelperClass addNewProduct=new ProductHelperClass(timeStamp,prodNm,prodDes,prodQuan,prodPrice,disc,discPrice,imgURL,Boolean.toString(isdiscavail),rating,timeStamp,prodDesDetailed,Boolean.toString(isinstock),subcat_text.getText().toString(),cat_text.getText().toString());
                                            reference.child(trend).child(timeStamp).setValue(addNewProduct);
                                        }

                                        Toast.makeText(Admin_Add_Product.this,"Product Details Uploaded Successfully",Toast.LENGTH_SHORT).show();

                                        clearData();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        //Progress
                                        progressBar.setVisibility(View.GONE);
                                        //

                                        Toast.makeText(Admin_Add_Product.this,""+e.getMessage(),Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            //Progress
                            progressBar.setVisibility(View.GONE);
                            //

                            Toast.makeText(Admin_Add_Product.this,""+e.getMessage(),Toast.LENGTH_SHORT).show();

                        }
                    });
        }

    }

    private void clearData() {
        prodName.getEditText().setText("");
        prodDesc.getEditText().setText("");
        prodQuantity.getEditText().setText("");
        prodDescDetailed.getEditText().setText("");
        price.getEditText().setText("");
        discountPrice.getEditText().setText("");
        discount.getEditText().setText("");
        isHaveDiscount.setChecked(false);
        isInStock.setChecked(false);
        checkBox_top.setChecked(false);
        checkBox_trend.setChecked(false);
        cat_text.setText("Select Category");
        subcat_text.setText("Select Sub-Category");
        prodImg.setImageResource(R.drawable.add_image);

    }

    private boolean validateProdDisc() {
        String val = Objects.requireNonNull(discount.getEditText()).getText().toString();
        if (val.isEmpty()) {
            discount.setError("Empty");
            return false;
        } else {
            discount.setError(null);
            discount.requestFocus();
            return true;
        }
    }

    private boolean validateProdDiscPrice() {
        String val = Objects.requireNonNull(discountPrice.getEditText()).getText().toString();
        if (val.isEmpty()) {
            discountPrice.setError("Empty");
            return false;
        } else {
            discountPrice.setError(null);
            discountPrice.requestFocus();
            return true;
        }
    }

    private boolean validateCat() {
        String val = cat_text.getText().toString();
        if (val.equals("Select Category")) {
            Toast.makeText(Admin_Add_Product.this,"Select Category",Toast.LENGTH_LONG).show();
            return false;
        } else {
            return true;
        }
    }

    private boolean validateSubCat() {
        String val = subcat_text.getText().toString();
        if (val.equals("Select Sub-Category")) {
            Toast.makeText(Admin_Add_Product.this,"Select Sub-Category",Toast.LENGTH_LONG).show();
            return false;
        } else {
            return true;
        }
    }

    private boolean validateProdQuantity() {
        String val = Objects.requireNonNull(prodQuantity.getEditText()).getText().toString();
        if (val.isEmpty()) {
            prodQuantity.setError("Empty");
            return false;
        } else {
            prodQuantity.setError(null);
            prodQuantity.requestFocus();
            return true;
        }
    }

    private boolean validateProdPrice() {
        String val = Objects.requireNonNull(price.getEditText()).getText().toString();
        if (val.isEmpty()) {
            price.setError("Empty");
            return false;
        } else {
            price.setError(null);
            price.requestFocus();
            return true;
        }
    }

    private boolean validateProdDesc() {
        String val = Objects.requireNonNull(prodDesc.getEditText()).getText().toString();
        if (val.isEmpty()) {
            prodDesc.setError("Empty");
            return false;
        } else {
            prodDesc.setError(null);
            prodDesc.requestFocus();
            return true;
        }
    }

    private boolean validateProdName() {
        String val = Objects.requireNonNull(prodName.getEditText()).getText().toString();
        if (val.isEmpty()) {
            prodName.setError("Empty");
            return false;
        } else {
            prodName.setError(null);
            prodName.requestFocus();
            return true;
        }
    }



    void pickImgFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, 1000);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable  Intent data) {
        if(resultCode==RESULT_OK){
            if(requestCode==1000){
                image_uri=data.getData();
                prodImg.setImageURI(image_uri);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private boolean isConnectedToInternet(Admin_Add_Product userProfile) {
        ConnectivityManager connectivityManager = (ConnectivityManager) userProfile.getSystemService(Context.CONNECTIVITY_SERVICE);
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
            CloseDrawer();
        }
        else if(id==R.id.nav_home){
            startActivity(new Intent(Admin_Add_Product.this,Admin_Show_Product.class));
            finishAffinity();
        }else if(id==R.id.nav_logout){
            SessionManager sessionManager1 = new SessionManager(Admin_Add_Product.this, SessionManager.SESSION_FORWHO);
            sessionManager1.creatingForWhomSession("forWho");

            SessionManager sessionManager = new SessionManager(this, SessionManager.SESSION_USER);
            sessionManager.logout();
            Intent logout_intent = new Intent(Admin_Add_Product.this, Dashboard.class);
            FirebaseAuth.getInstance().signOut();
            startActivity(logout_intent);
            finishAffinity();
        }
        return true;
    }
}