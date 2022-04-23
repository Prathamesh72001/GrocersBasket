package com.example.grocerbasket.Adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.example.grocerbasket.Constructors.CartHelperClass;
import com.example.grocerbasket.Constructors.CartProdHelperClass;
import com.example.grocerbasket.Constructors.ProductHelperClass;
import com.example.grocerbasket.Product_Details;
import com.example.grocerbasket.R;
import com.example.grocerbasket.Session.SessionManager;
import com.example.grocerbasket.Welcome_To_Login_Signup;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartAdapterViewHolder> {

    Context context;
    public ArrayList<CartProdHelperClass> cartProdHelperClasses;
    int finalcost, orgcost;


    public CartAdapter(Context context, ArrayList<CartProdHelperClass> cartProdHelperClasses) {
        this.context = context;
        this.cartProdHelperClasses = cartProdHelperClasses;

    }

    @NonNull
    @Override
    public CartAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_layout, parent, false);
        CartAdapter.CartAdapterViewHolder productAdapterViewHolder = new CartAdapter.CartAdapterViewHolder(view);

        return productAdapterViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull CartAdapterViewHolder holder, int position) {
        //Get Data
        CartProdHelperClass cartProdHelperClass = cartProdHelperClasses.get(position);
        String id = cartProdHelperClass.getProdid();
        String name = cartProdHelperClass.getProdname();
        String description = cartProdHelperClass.getProddescription();
        String descriptiondetailed = cartProdHelperClass.getProddescdetailed();
        String timestamp = cartProdHelperClass.getTimeStamp();
        String quantity = cartProdHelperClass.getProdquantity();
        String discount = cartProdHelperClass.getDiscount();
        String discountprice = cartProdHelperClass.getDiscountprice();
        String price = cartProdHelperClass.getProdprice();
        String isdiscountavailable = cartProdHelperClass.getIsdiscountavail();
        String isinstock = cartProdHelperClass.getIsInStock();
        float rating = cartProdHelperClass.getRating();
        String Image = cartProdHelperClass.getProdimg();
        String subcat=cartProdHelperClass.getSubcat();
        String cat=cartProdHelperClass.getCat();

        //Set Data
        holder.prodName.setText(name);
        holder.prodDescription.setText(description);
        holder.prodRating.setRating(rating);
        holder.productPrice.setText(price);
        holder.prodQuantity.setText(quantity);
        holder.discount.setText(discount + "%");
        holder.discountPrice.setText(discountprice);
        if (isdiscountavailable.equalsIgnoreCase("true")) {
            holder.discountPriceRl.setVisibility(View.VISIBLE);
            holder.discountRl.setVisibility(View.VISIBLE);
            holder.productPrice.setPaintFlags(holder.productPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            holder.productPrice.setTextSize(15);
            holder.productPrice.setTextColor(Color.parseColor("#484848"));

            holder.rupeesign2.setPaintFlags(holder.rupeesign2.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            holder.rupeesign2.setTextSize(15);
            holder.rupeesign2.setTextColor(Color.parseColor("#484848"));
        } else {
            holder.discountPriceRl.setVisibility(View.GONE);
            holder.discountRl.setVisibility(View.GONE);
        }

        if (isinstock.equalsIgnoreCase("true")) {
            holder.prodImage.setImageAlpha(255);
            holder.outOfStockRl.setVisibility(View.GONE);
            holder.add_btn.setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.orange));
            holder.plusIcon.setImageTintList(ContextCompat.getColorStateList(context, R.color.white));
            holder.add_btn.setEnabled(true);
        }
        else {
            SessionManager sessionManager = new SessionManager(context, SessionManager.SESSION_FORWHO);
            HashMap<String, String> forWhoDetails = sessionManager.getForWhomDetailFromSession();
            String forWhom = forWhoDetails.get(SessionManager.KEY_FORWHO);
            if (Objects.requireNonNull(forWhom).equalsIgnoreCase("forUser")) {
                SessionManager UsersessionManager = new SessionManager(context, SessionManager.SESSION_USER);
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
                                            holder.add_btn.setVisibility(View.VISIBLE);
                                            holder.quantityLayout.setVisibility(View.GONE);
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
                                holder.add_btn.setVisibility(View.VISIBLE);
                                holder.quantityLayout.setVisibility(View.GONE);
                                holder.add_btn.setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.light_grey));
                                holder.plusIcon.setImageTintList(ContextCompat.getColorStateList(context, R.color.black));
                                holder.add_btn.setEnabled(false);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                } else {
                    context.startActivity(new Intent(context, Welcome_To_Login_Signup.class));

                }
            }
            holder.prodImage.setImageAlpha(50);
            holder.outOfStockRl.setVisibility(View.VISIBLE);

        }

        try {
            Picasso.get().load(Image).placeholder(R.drawable.default_image).into(holder.prodImage);
        } catch (Exception e) {
            holder.prodImage.setImageResource(R.drawable.default_image);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context, Product_Details.class);
                intent.putExtra("Product ID",id);
                intent.putExtra("Product Subcat",subcat);
                context.startActivity(intent);
            }
        });

        holder.add_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(context, Welcome_To_Login_Signup.class));
            }
        });

        holder.plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(context, Welcome_To_Login_Signup.class));
            }
        });

        holder.uncheck_fvrt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(context, Welcome_To_Login_Signup.class));
            }
        });

        //Session
        SessionManager sessionManager = new SessionManager(context, SessionManager.SESSION_FORWHO);
        HashMap<String, String> forWhoDetails = sessionManager.getForWhomDetailFromSession();
        String forWhom = forWhoDetails.get(SessionManager.KEY_FORWHO);
        if (Objects.requireNonNull(forWhom).equalsIgnoreCase("forUser")) {
            SessionManager UsersessionManager = new SessionManager(context, SessionManager.SESSION_USER);
            HashMap<String, String> userDetails = UsersessionManager.getUserDetailFromSession();
            String number = userDetails.get(SessionManager.KEY_PHONENO);

            boolean isLoggedIn = UsersessionManager.checkLogin();
            if (isLoggedIn) {

                holder.uncheck_fvrt.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        holder.uncheck_fvrt.setVisibility(View.GONE);
                        holder.check_fvrt.setVisibility(View.VISIBLE);
                        addToFavourite(id, name, description, quantity, price, discount, discountprice, Image, isdiscountavailable, rating, timestamp, descriptiondetailed, isinstock,number,subcat,cat);

                        FirebaseDatabase.getInstance().getReference("Products").child("Favourite Products").child(number).orderByChild("prodid").equalTo(id).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()) {
                                    holder.uncheck_fvrt.setVisibility(View.GONE);
                                    holder.check_fvrt.setVisibility(View.VISIBLE);
                                } else {
                                    holder.check_fvrt.setVisibility(View.GONE);
                                    holder.uncheck_fvrt.setVisibility(View.VISIBLE);
                                }

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                    }
                });

                holder.check_fvrt.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        holder.check_fvrt.setVisibility(View.GONE);
                        holder.uncheck_fvrt.setVisibility(View.VISIBLE);
                        removeFromFavourite(id, number);

                        FirebaseDatabase.getInstance().getReference("Products").child("Favourite Products").child(number).orderByChild("prodid").equalTo(id).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()) {
                                    holder.uncheck_fvrt.setVisibility(View.GONE);
                                    holder.check_fvrt.setVisibility(View.VISIBLE);
                                } else {
                                    holder.check_fvrt.setVisibility(View.GONE);
                                    holder.uncheck_fvrt.setVisibility(View.VISIBLE);
                                }

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                    }
                });

                holder.add_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (isdiscountavailable.equals("true")) {
                            finalcost = Integer.parseInt(discountprice);
                        } else {
                            finalcost = Integer.parseInt(price);
                        }
                        orgcost = Integer.parseInt(price);
                        holder.quan.setText(String.valueOf(1));
                        holder.add_btn.setVisibility(View.GONE);
                        holder.quantityLayout.setVisibility(View.VISIBLE);
                        addToCart(id, name, description, quantity, price, discount, discountprice, Image, isdiscountavailable, rating, timestamp, descriptiondetailed, isinstock,subcat,cat, orgcost, finalcost, 1, number);

                    }
                });

                holder.plus.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
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
                                                p_quan++;
                                                holder.quan.setText(String.valueOf(p_quan));
                                                if (isdiscountavailable.equals("true")) {
                                                    finalcost = Integer.parseInt(discountprice);
                                                } else {
                                                    finalcost = Integer.parseInt(price);
                                                }
                                                orgcost = Integer.parseInt(price);
                                                addToCart(id, name, description, quantity, price, discount, discountprice, Image, isdiscountavailable, rating, timestamp, descriptiondetailed, isinstock,subcat,cat, orgcost, finalcost, 1, number);
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

                                        holder.add_btn.setVisibility(View.GONE);
                                        holder.quantityLayout.setVisibility(View.VISIBLE);

                                        holder.quan.setText(prodquan);

                                    }
                                } else {
                                    holder.add_btn.setVisibility(View.VISIBLE);
                                    holder.quantityLayout.setVisibility(View.GONE);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                });

                holder.minus.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //Session
                        SessionManager sessionManager = new SessionManager(context, SessionManager.SESSION_FORWHO);
                        HashMap<String, String> forWhoDetails = sessionManager.getForWhomDetailFromSession();
                        String forWhom = forWhoDetails.get(SessionManager.KEY_FORWHO);
                        if (Objects.requireNonNull(forWhom).equalsIgnoreCase("forUser")) {
                            SessionManager UsersessionManager = new SessionManager(context, SessionManager.SESSION_USER);
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
                                                                finalcost = Integer.parseInt(discountprice);
                                                            } else {
                                                                finalcost = Integer.parseInt(price);
                                                            }
                                                            orgcost = Integer.parseInt(price);
                                                            holder.add_btn.setVisibility(View.VISIBLE);
                                                            holder.quantityLayout.setVisibility(View.GONE);

                                                        } else {
                                                            if (isdiscountavailable.equals("true")) {
                                                                finalcost = Integer.parseInt(discountprice);
                                                            } else {
                                                                finalcost = Integer.parseInt(price);
                                                            }
                                                            orgcost = Integer.parseInt(price);
                                                        }
                                                        holder.quan.setText(String.valueOf(p_quan));

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

                                                holder.add_btn.setVisibility(View.GONE);
                                                holder.quantityLayout.setVisibility(View.VISIBLE);

                                                holder.quan.setText(prodquan);

                                            }
                                        } else {
                                            holder.add_btn.setVisibility(View.VISIBLE);
                                            holder.quantityLayout.setVisibility(View.GONE);
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });

                            } else {
                                context.startActivity(new Intent(context, Welcome_To_Login_Signup.class));
                            }
                        }
                    }
                });

                FirebaseDatabase.getInstance().getReference("Products").child("Cart Products").child(number).child("cartProdHelperClass").orderByChild("prodid").equalTo(id).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            for (DataSnapshot ds : snapshot.getChildren()) {
                                CartProdHelperClass cartProdHelperClass = ds.getValue(CartProdHelperClass.class);
                                String prodquan = cartProdHelperClass.getProdquan();

                                holder.add_btn.setVisibility(View.GONE);
                                holder.quantityLayout.setVisibility(View.VISIBLE);

                                holder.quan.setText(prodquan);

                            }
                        } else {
                            holder.add_btn.setVisibility(View.VISIBLE);
                            holder.quantityLayout.setVisibility(View.GONE);
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                FirebaseDatabase.getInstance().getReference("Products").child("Favourite Products").child(number).orderByChild("prodid").equalTo(id).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            holder.uncheck_fvrt.setVisibility(View.GONE);
                            holder.check_fvrt.setVisibility(View.VISIBLE);
                        } else {
                            holder.check_fvrt.setVisibility(View.GONE);
                            holder.uncheck_fvrt.setVisibility(View.VISIBLE);
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }
        }
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

    @Override
    public int getItemCount() {
        return cartProdHelperClasses.size();
    }

    public static class CartAdapterViewHolder extends RecyclerView.ViewHolder {

        RatingBar prodRating;
        RelativeLayout discountPriceRl, discountRl, add_btn, quantityLayout, outOfStockRl;
        ImageView prodImage, minus, plus,check_fvrt,uncheck_fvrt,plusIcon;
        TextView discount, prodName, prodDescription, discountPrice, productPrice, prodQuantity;
        TextView rupeesign2, quan;

        public CartAdapterViewHolder(@NonNull View itemView) {
            super(itemView);

            //hooks
            prodImage = itemView.findViewById(R.id.prodImage);
            prodRating = itemView.findViewById(R.id.prodRating);
            prodName = itemView.findViewById(R.id.prodName);
            prodDescription = itemView.findViewById(R.id.prodDescription);
            productPrice = itemView.findViewById(R.id.productPrice);
            prodQuantity = itemView.findViewById(R.id.prodQuanity);
            discount = itemView.findViewById(R.id.discount);
            discountPrice = itemView.findViewById(R.id.discountPrice);
            discountPriceRl = itemView.findViewById(R.id.DiscountPrice);
            discountRl = itemView.findViewById(R.id.discountRl);
            outOfStockRl = itemView.findViewById(R.id.outOfStockRl);
            add_btn = itemView.findViewById(R.id.AddBtn);
            quantityLayout = itemView.findViewById(R.id.quantitylayout);
            rupeesign2 = itemView.findViewById(R.id.rupeesign2);
            minus = itemView.findViewById(R.id.minu);
            plus = itemView.findViewById(R.id.plus);
            quan = itemView.findViewById(R.id.quan);
            check_fvrt=itemView.findViewById(R.id.check_fvrt);
            uncheck_fvrt=itemView.findViewById(R.id.uncheck_fvrt);
            plusIcon=itemView.findViewById(R.id.plusIcon);

        }
    }
}
