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

public class OrderProductAdapter extends RecyclerView.Adapter< OrderProductAdapter. OrderProductAdapterViewHolder> {

    Context context;
    public ArrayList<CartProdHelperClass> productHelperClasses;
    int finalcost, orgcost;


    public OrderProductAdapter(Context context, ArrayList<CartProdHelperClass> productHelperClasses) {
        this.context = context;
        this.productHelperClasses = productHelperClasses;

    }

    @NonNull
    @Override
    public OrderProductAdapter.OrderProductAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_product_layout, parent, false);
        OrderProductAdapter.OrderProductAdapterViewHolder orderProductAdapterViewHolder = new OrderProductAdapter.OrderProductAdapterViewHolder(view);

        return orderProductAdapterViewHolder;
    }


    @Override
    public void onBindViewHolder(@NonNull OrderProductAdapter.OrderProductAdapterViewHolder holder, int position) {
        //Get Data
        CartProdHelperClass productHelperClass = productHelperClasses.get(position);
        String id = productHelperClass.getProdid();
        String name = productHelperClass.getProdname();
        String description = productHelperClass.getProddescription();
        String descriptiondetailed = productHelperClass.getProddescdetailed();
        String quantity = productHelperClass.getProdquantity();
        String discount = productHelperClass.getDiscount();
        String discountprice = productHelperClass.getDiscountprice();
        String price = productHelperClass.getProdprice();
        String isdiscountavailable = productHelperClass.getIsdiscountavail();
        String Image = productHelperClass.getProdimg();
        String isinstock = productHelperClass.getIsInStock();
        String timestamp = productHelperClass.getTimeStamp();
        String Quantity=productHelperClass.getProdquan();


        float rating = productHelperClass.getRating();
        String subcat=productHelperClass.getSubcat();
        String cat=productHelperClass.getCat();

        //Set Data
        holder.prodName.setText(name);
        holder.prodDescription.setText(description);
        holder.prodRating.setRating(rating);
        holder.productPrice.setText(price);
        holder.quan.setText(Quantity);
        holder.prodQuantity.setText(quantity);
        holder.discount.setText(discount + "%");
        holder.discountPrice.setText(discountprice);
        if (isdiscountavailable.equalsIgnoreCase("true")) {
            holder.discountPriceRl.setVisibility(View.VISIBLE);
            holder.discountRl.setVisibility(View.VISIBLE);
            holder.productPrice.setTextSize(13);
            holder.productPrice.setPaintFlags(holder.productPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            holder.productPrice.setTextColor(Color.parseColor("#484848"));

            holder.rupeesign2.setTextSize(13);
            holder.rupeesign2.setPaintFlags(holder.rupeesign2.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            holder.rupeesign2.setTextColor(Color.parseColor("#484848"));
        } else {
            holder.discountPriceRl.setVisibility(View.GONE);
            holder.discountRl.setVisibility(View.GONE);
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



    @Override
    public int getItemCount() {
        return productHelperClasses.size();
    }

    public static class OrderProductAdapterViewHolder extends RecyclerView.ViewHolder {

        RatingBar prodRating;
        RelativeLayout discountPriceRl, discountRl;
        ImageView prodImage,check_fvrt,uncheck_fvrt;
        TextView discount, prodName, prodDescription, discountPrice, productPrice, prodQuantity;
        TextView rupeesign2, quan;

        public OrderProductAdapterViewHolder(@NonNull View itemView) {
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
            rupeesign2 = itemView.findViewById(R.id.rupeesign2);
            quan = itemView.findViewById(R.id.quan);
            check_fvrt=itemView.findViewById(R.id.check_fvrt);
            uncheck_fvrt=itemView.findViewById(R.id.uncheck_fvrt);

        }
    }
}
