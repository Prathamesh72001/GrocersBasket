package com.example.grocerbasket;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
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
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Objects;

public class Admin_Edit_Product extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    String prodId;

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    ImageView prodImg, menu, removeme;
    RelativeLayout updateProd, progressBar, contentView;
    TextInputLayout prodName, prodDesc, prodDescDetailed, prodQuantity, price, discount, discountPrice;
    TextInputEditText discountEditText,priceEditText;
    SwitchCompat isHaveDiscount,isInStock;
    CheckBox checkBox_trend, checkBox_top;
    RelativeLayout cat,sub_cat;
    TextView cat_text,subcat_text;

    String prodNm, prodDes, prodDesDetailed, prodQuan, prodPrice, discPrice, disc;

    Uri image_uri;
    static final float END_SCALE = 0.7f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_edit_product);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //hooks
        prodId = getIntent().getStringExtra("prodid");

        prodImg = findViewById(R.id.ProductImage);
        updateProd = findViewById(R.id.Updatebtn);
        prodName = findViewById(R.id.productname);
        prodDesc = findViewById(R.id.proddesc);
        prodDescDetailed = findViewById(R.id.proddescdetailed);
        prodQuantity = findViewById(R.id.quantity);
        discount = findViewById(R.id.discount);
        price = findViewById(R.id.price);
        discountPrice = findViewById(R.id.discprice);
        isHaveDiscount = findViewById(R.id.isHaveDiscount);
        checkBox_trend = findViewById(R.id.cat_trnding);
        checkBox_top = findViewById(R.id.cat_top_offers);
        progressBar = findViewById(R.id.progressBar);
        contentView = findViewById(R.id.contentView);
        drawerLayout = findViewById(R.id.drawerlayout);
        navigationView = findViewById(R.id.navigation_view);
        menu = findViewById(R.id.menu);
        removeme = findViewById(R.id.Removeme);
        discountEditText=findViewById(R.id.editText);
        priceEditText=findViewById(R.id.editText2);
        isInStock=findViewById(R.id.isInStock);
        cat=findViewById(R.id.relativeLayout4);
        sub_cat=findViewById(R.id.relativeLayout5);
        cat_text=findViewById(R.id.cat_text);
        subcat_text=findViewById(R.id.subcat_text);
        
        //Methods
        loadProductDetails();

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
                SessionManager sessionManager1 = new SessionManager(Admin_Edit_Product.this, SessionManager.SESSION_FORWHO);
                sessionManager1.creatingForWhomSession("forWho");

                SessionManager sessionManager = new SessionManager(Admin_Edit_Product.this, SessionManager.SESSION_ADMIN);
                sessionManager.logout();
                Intent logout_intent = new Intent(Admin_Edit_Product.this, Dashboard.class);
                startActivity(logout_intent);
                finishAffinity();
            }
        });

        prodImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //CheckInternetConnection
                if (!isConnectedToInternet(Admin_Edit_Product.this)) {
                    startActivity(new Intent(Admin_Edit_Product.this, NoInternet.class));
                    finish();
                }
                pickImgFromGallery();

            }
        });

        updateProd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UpdateData();
            }
        });

        isHaveDiscount.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    discount.setVisibility(View.VISIBLE);
                    discountPrice.setVisibility(View.VISIBLE);
                } else {
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

    private void loadProductDetails() {
        FirebaseDatabase.getInstance().getReference("Products").child("All Products").child(prodId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String id = "" + snapshot.child("prodid").getValue();
                        String name = "" + snapshot.child("prodname").getValue();
                        String description = "" + snapshot.child("proddescription").getValue();
                        String quantity = "" + snapshot.child("prodquantity").getValue();
                        String prce = "" + snapshot.child("prodprice").getValue();
                        String discnt = "" + snapshot.child("discount").getValue();
                        String discountprice = "" + snapshot.child("discountprice").getValue();
                        String image = "" + snapshot.child("prodimg").getValue();
                        String isdiscountavail = "" + snapshot.child("isdiscountavail").getValue();
                        String timeStamp = "" + snapshot.child("timeStamp").getValue();
                        String isinStock = "" + snapshot.child("isInStock").getValue();
                        String descdetailed = "" + snapshot.child("proddescdetailed").getValue();
                        String subcatdb = "" + snapshot.child("subcat").getValue();
                        String catdb = "" + snapshot.child("cat").getValue();

                        //set data
                        if (isdiscountavail.equalsIgnoreCase("true")) {
                            isHaveDiscount.setChecked(true);
                            discount.setVisibility(View.VISIBLE);
                            discountPrice.setVisibility(View.VISIBLE);
                        } else {
                            isHaveDiscount.setChecked(false);
                            discount.setVisibility(View.GONE);
                            discountPrice.setVisibility(View.GONE);
                        }
                        
                        if(isinStock.equalsIgnoreCase("true")){
                            isInStock.setChecked(true);
                        }else{
                            isInStock.setChecked(false);
                        }

                        prodName.getEditText().setText(name);
                        prodDesc.getEditText().setText(description);
                        prodDescDetailed.getEditText().setText(descdetailed);
                        prodQuantity.getEditText().setText(quantity);
                        price.getEditText().setText(prce);
                        discount.getEditText().setText(discnt);
                        discountPrice.getEditText().setText(discountprice);

                        try {
                            Picasso.get().load(image).placeholder(R.drawable.default_image).into(prodImg);
                        } catch (Exception e) {
                            prodImg.setImageResource(R.drawable.default_image);
                        }

                        subcat_text.setText(subcatdb);
                        cat_text.setText(catdb);

                        loadCategories();

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(Admin_Edit_Product.this, "Unable To Load Information", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void loadCategories() {
        FirebaseDatabase rootnode = FirebaseDatabase.getInstance();

        //For Top Offers
        DatabaseReference reference2 = rootnode.getReference("Products").child("Top Offers");

        //VerifySeller
        Query checkproduct2 = reference2.orderByChild("timeStamp").equalTo(prodId);
        checkproduct2.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    checkBox_top.setChecked(true);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //For Trending
        DatabaseReference reference3 = rootnode.getReference("Products").child("Trending");

        //VerifySeller
        Query checkproduct3 = reference3.orderByChild("timeStamp").equalTo(prodId);
        checkproduct3.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    checkBox_trend.setChecked(true);
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

    private void UpdateData() {
        //Progress
        progressBar.setVisibility(View.VISIBLE);


        //Validation
        if (!validateProdName() | !validateProdDesc() | !validateProdQuantity() | !validateProdPrice() | !validateCat() | !validateSubCat()) {
            return;
        }

        //Strings
        prodNm = prodName.getEditText().getText().toString();
        prodDes = prodDesc.getEditText().getText().toString();
        prodDesDetailed = prodDescDetailed.getEditText().getText().toString();
        prodQuan = prodQuantity.getEditText().getText().toString();
        prodPrice = price.getEditText().getText().toString();
        disc = discount.getEditText().getText().toString();
        discPrice = discountPrice.getEditText().getText().toString();
        String catdb=cat_text.getText().toString();
        String subcatdb=subcat_text.getText().toString();
        boolean isdiscavail = isHaveDiscount.isChecked();
        boolean isinstock = isInStock.isChecked();

        //isDiscountApplicable
        if (isHaveDiscount.isChecked()) {
            if (!validateProdDisc() | !validateProdDiscPrice()) {
                return;
            }
        } else {
            disc = "";
            discPrice = "0";
        }

        if (image_uri == null) {

            //Hashmap
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("prodname", "" + prodNm);
            hashMap.put("proddescription", "" + prodDes);
            hashMap.put("prodquantity", "" + prodQuan);
            hashMap.put("prodprice", "" + prodPrice);
            hashMap.put("discount", "" + disc);
            hashMap.put("discountprice", "" + discPrice);
            hashMap.put("proddescdetailed", "" + prodDesDetailed);
            hashMap.put("isdiscountavail", "" + isdiscavail);
            hashMap.put("isInStock", "" + isinstock);
            hashMap.put("subcat", "" + subcatdb);
            hashMap.put("cat", "" + catdb);

            //For All Products
            FirebaseDatabase rootnode = FirebaseDatabase.getInstance();
            DatabaseReference reference1 = rootnode.getReference("Products").child("All Products");
            //VerifySeller
            Query checkproduct1 = reference1.orderByChild("timeStamp").equalTo(prodId);
            checkproduct1.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        reference1.child(prodId).updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(Admin_Edit_Product.this, "Product Detail Updated Successfully", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(Admin_Edit_Product.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

            //For Top Offers
            DatabaseReference reference2 = rootnode.getReference("Products").child("Top Offers");
            //VerifySeller
            Query checkproduct2 = reference2.orderByChild("timeStamp").equalTo(prodId);
            checkproduct2.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (checkBox_top.isChecked()) {

                        if (snapshot.exists()) {
                            reference2.child(prodId).updateChildren(hashMap);
                        } else {
                            ProductHelperClass addNewProduct = new ProductHelperClass(prodId, prodNm, prodDes, prodQuan, prodPrice, disc, discPrice, "", Boolean.toString(isdiscavail), 0, prodId, prodDesDetailed,Boolean.toString(isinstock),subcat_text.getText().toString(),cat_text.getText().toString());
                            reference2.child(prodId).setValue(addNewProduct);
                        }
                    } else {
                        if (snapshot.exists()) {
                            reference2.child(prodId).removeValue();
                        }
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });


            //For Trending
            DatabaseReference reference3 = rootnode.getReference("Products").child("Trending");
            //VerifySeller
            Query checkproduct3 = reference3.orderByChild("timeStamp").equalTo(prodId);
            checkproduct3.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (checkBox_trend.isChecked()) {
                        if (snapshot.exists()) {
                            reference3.child(prodId).updateChildren(hashMap);
                        } else {
                            ProductHelperClass addNewProduct = new ProductHelperClass(prodId, prodNm, prodDes, prodQuan, prodPrice, disc, discPrice, "", Boolean.toString(isdiscavail), 0, prodId, prodDesDetailed,Boolean.toString(isinstock),subcat_text.getText().toString(),cat_text.getText().toString());
                            reference3.child(prodId).setValue(addNewProduct);
                        }
                    } else {
                        if (snapshot.exists()) {
                            reference3.child(prodId).removeValue();
                        }
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });


            //Progress
            progressBar.setVisibility(View.GONE);

            startActivity(new Intent(Admin_Edit_Product.this, Admin_Show_Product.class));
            finishAffinity();


        } else {
            String filePathAndName = "Product Images/" + "" + prodId;
            StorageReference storageReference = FirebaseStorage.getInstance().getReference(filePathAndName);
            storageReference.putFile(image_uri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                            while (!uriTask.isSuccessful()) ;
                            Uri downloadUri = uriTask.getResult();
                            String imgURL = downloadUri.toString();

                            if (uriTask.isSuccessful()) {
                                //Hashmap
                                HashMap<String, Object> hashMap = new HashMap<>();
                                hashMap.put("prodname", "" + prodNm);
                                hashMap.put("proddescription", "" + prodDes);
                                hashMap.put("prodquantity", "" + prodQuan);
                                hashMap.put("prodprice", "" + prodPrice);
                                hashMap.put("discount", "" + disc);
                                hashMap.put("discountprice", "" + discPrice);
                                hashMap.put("proddescdetailed", "" + prodDesDetailed);
                                hashMap.put("isdiscountavail", "" + isdiscavail);
                                hashMap.put("prodimg", "" + imgURL);
                                hashMap.put("isInStock", "" + isinstock);
                                hashMap.put("subcat", "" + subcatdb);
                                hashMap.put("cat", "" + catdb);

                                //For All Products
                                FirebaseDatabase rootnode = FirebaseDatabase.getInstance();
                                DatabaseReference reference1 = rootnode.getReference("Products").child("All Products");
                                //VerifySeller
                                Query checkproduct1 = reference1.orderByChild("timeStamp").equalTo(prodId);
                                checkproduct1.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if (snapshot.exists()) {
                                            reference1.child(prodId).updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    Toast.makeText(Admin_Edit_Product.this, "Product Detail Updated Successfully", Toast.LENGTH_SHORT).show();
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(Admin_Edit_Product.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                        }

                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });


                                //For Top Offers
                                DatabaseReference reference2 = rootnode.getReference("Products").child("Top Offers");
                                //VerifySeller
                                Query checkproduct2 = reference2.orderByChild("timeStamp").equalTo(prodId);
                                checkproduct2.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if (checkBox_top.isChecked()) {
                                            if (snapshot.exists()) {
                                                reference2.child(prodId).updateChildren(hashMap);
                                            }
                                            else {
                                                ProductHelperClass addNewProduct = new ProductHelperClass(prodId, prodNm, prodDes, prodQuan, prodPrice, disc, discPrice, imgURL, Boolean.toString(isdiscavail), 0, prodId, prodDesDetailed,Boolean.toString(isinstock),subcat_text.getText().toString(),cat_text.getText().toString());
                                                reference2.child(prodId).setValue(addNewProduct);
                                            }
                                        } else {
                                            if (snapshot.exists()) {
                                                reference2.child(prodId).removeValue();
                                            }
                                        }


                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });

                                
                                //For Trending
                                DatabaseReference reference3 = rootnode.getReference("Products").child("Trending");
                                //VerifySeller
                                Query checkproduct3 = reference3.orderByChild("timeStamp").equalTo(prodId);
                                checkproduct3.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if (checkBox_trend.isChecked()) {
                                            if (snapshot.exists()) {
                                                reference3.child(prodId).updateChildren(hashMap);
                                            }
                                            else {
                                                ProductHelperClass addNewProduct = new ProductHelperClass(prodId, prodNm, prodDes, prodQuan, prodPrice, disc, discPrice, imgURL, Boolean.toString(isdiscavail), 0, prodId, prodDesDetailed,Boolean.toString(isinstock),subcat_text.getText().toString(),cat_text.getText().toString());
                                                reference3.child(prodId).setValue(addNewProduct);
                                            }
                                        } else {
                                            if (snapshot.exists()) {
                                                reference3.child(prodId).removeValue();
                                            }
                                        }

                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });


                                //Progress
                                progressBar.setVisibility(View.GONE);

                                startActivity(new Intent(Admin_Edit_Product.this, Admin_Show_Product.class));
                                finishAffinity();
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            //Progress
                            progressBar.setVisibility(View.GONE);
                            //

                            Toast.makeText(Admin_Edit_Product.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();

                        }
                    });

        }

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

    private boolean validateCat() {
        String val = cat_text.getText().toString();
        if (val.equals("Select Category")) {
            Toast.makeText(Admin_Edit_Product.this,"Select Category",Toast.LENGTH_LONG).show();
            return false;
        } else {
            return true;
        }
    }

    private boolean validateSubCat() {
        String val = subcat_text.getText().toString();
        if (val.equals("Select Sub-Category")) {
            Toast.makeText(Admin_Edit_Product.this,"Select Sub-Category",Toast.LENGTH_LONG).show();
            return false;
        } else {
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

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerVisible(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }

    }

    void pickImgFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, 1000);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == 1000) {
                image_uri = data.getData();
                prodImg.setImageURI(image_uri);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private boolean isConnectedToInternet(Admin_Edit_Product userProfile) {
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
            startActivity(new Intent(Admin_Edit_Product.this, Admin_Add_Product.class));
            finish();
        }else if(id==R.id.nav_home){
            startActivity(new Intent(Admin_Edit_Product.this, Admin_Show_Product.class));
            finishAffinity();
        }else if(id==R.id.nav_logout){
            SessionManager sessionManager1 = new SessionManager(Admin_Edit_Product.this, SessionManager.SESSION_FORWHO);
            sessionManager1.creatingForWhomSession("forWho");

            SessionManager sessionManager = new SessionManager(this, SessionManager.SESSION_USER);
            sessionManager.logout();
            Intent logout_intent = new Intent(Admin_Edit_Product.this, Dashboard.class);
            FirebaseAuth.getInstance().signOut();
            startActivity(logout_intent);
            finishAffinity();
        }
        return true;
    }
}