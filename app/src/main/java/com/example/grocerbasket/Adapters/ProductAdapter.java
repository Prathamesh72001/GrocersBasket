package com.example.grocerbasket.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.TextUtils;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.grocerbasket.Admin_Edit_Product;
import com.example.grocerbasket.Constructors.ProductHelperClass;
import com.example.grocerbasket.Filters.ProductFilter;
import com.example.grocerbasket.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductAdapterViewHolder> implements Filterable {

    Context context;
    public ArrayList<ProductHelperClass> productHelperClasses,filterList;
    ProductFilter productFilter;

    public ProductAdapter(Context context, ArrayList<ProductHelperClass> productHelperClasses) {
        this.context = context;
        this.productHelperClasses = productHelperClasses;
        this.filterList=productHelperClasses;
    }

    @NonNull
    @Override
    public ProductAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.peoduct_layout_admin ,parent, false);
        ProductAdapter.ProductAdapterViewHolder productAdapterViewHolder = new ProductAdapter.ProductAdapterViewHolder(view);

        return productAdapterViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ProductAdapterViewHolder holder, int position) {
        //Get Data
        ProductHelperClass productHelperClass=productHelperClasses.get(position);

        String name=productHelperClass.getProdname();
        String description=productHelperClass.getProddescription();
        String quantity=productHelperClass.getProdquantity();
        String discount=productHelperClass.getDiscount();
        String discountprice=productHelperClass.getDiscountprice();
        String price=productHelperClass.getProdprice();
        String isdiscountavailable=productHelperClass.getIsdiscountavail();
        String isinstock=productHelperClass.getIsInStock();
        float rating=productHelperClass.getRating();
        String Image=productHelperClass.getProdimg();

        //Set Data
        holder.prodName.setText(name);
        holder.prodDescription.setText(description);
        holder.prodRating.setRating(rating);
        holder.productPrice.setText(price);
        holder.prodQuantity.setText(quantity);
        holder.discount.setText(discount + "%");
        holder.discountPrice.setText(discountprice);
        if(isdiscountavailable.equalsIgnoreCase("true")) {
            holder.discountPriceRl.setVisibility(View.VISIBLE);
            holder.discountRl.setVisibility(View.VISIBLE);
            holder.productPrice.setPaintFlags(holder.productPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            holder.productPrice.setTextSize(15);
            holder.productPrice.setTextColor(Color.parseColor("#484848"));

            holder.rupeesign2.setPaintFlags(holder.rupeesign2.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            holder.rupeesign2.setTextSize(15);
            holder.rupeesign2.setTextColor(Color.parseColor("#484848"));
        }
        else {
            holder.discountPriceRl.setVisibility(View.GONE);
            holder.discountRl.setVisibility(View.GONE);
        }

        if(isinstock.equalsIgnoreCase("true")) {
            holder.prodImage.setImageAlpha(255);
            holder.outOfStockRl.setVisibility(View.GONE);
        }
        else {
            holder.prodImage.setImageAlpha(50);
            holder.outOfStockRl.setVisibility(View.VISIBLE);
        }

        try {
            Picasso.get().load(Image).placeholder(R.drawable.default_image).into(holder.prodImage);
        }catch(Exception e){
            holder.prodImage.setImageResource(R.drawable.default_image);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                detailsBottomSheet(productHelperClass);
            }
        });

    }

    @SuppressLint("SetTextI18n")
    private void detailsBottomSheet(ProductHelperClass productHelperClass) {
        BottomSheetDialog bottomSheetDialog=new BottomSheetDialog(context);
        View view=LayoutInflater.from(context).inflate(R.layout.bs_product_details_admin,null);
        bottomSheetDialog.setContentView(view);

        //hooks
        ImageView backBtn=view.findViewById(R.id.backbtn);
        ImageView editBtn=view.findViewById(R.id.editbtn);
        ImageView deleteBtn=view.findViewById(R.id.deletebtn);
        ImageView prodImg=view.findViewById(R.id.prodimage);
        TextView prodName=view.findViewById(R.id.prodname);
        TextView productname=view.findViewById(R.id.productname);
        TextView productdesc=view.findViewById(R.id.productdesc);
        TextView productdescdetailed=view.findViewById(R.id.productdescdetailed);
        TextView productquantity=view.findViewById(R.id.productquan);
        TextView productprice=view.findViewById(R.id.productprice);
        TextView productdisc=view.findViewById(R.id.productdisc);
        TextView productdiscprice=view.findViewById(R.id.productdiscprice);
        RelativeLayout discRl=view.findViewById(R.id.DiscountRl);
        RelativeLayout discpriceRl=view.findViewById(R.id.DiscountPriceRl);

        //get data
        String id=productHelperClass.getProdid();
        String name=productHelperClass.getProdname();
        String description=productHelperClass.getProddescription();
        String quantity=productHelperClass.getProdquantity();
        String discount=productHelperClass.getDiscount();
        String discountprice=productHelperClass.getDiscountprice();
        String price=productHelperClass.getProdprice();
        String isdiscountavailable=productHelperClass.getIsdiscountavail();
        String Image=productHelperClass.getProdimg();
        String timeStamp=productHelperClass.getTimeStamp();
        String descriptionDetailed=productHelperClass.getProddescdetailed();

        //set data
        prodName.setText(name);
        productname.setText(name);
        productdesc.setText(description);
        productdescdetailed.setText(descriptionDetailed);
        productquantity.setText(quantity);
        productprice.setText("\u20b9"+price);
        productdisc.setText(discount+"\u0025");
        productdiscprice.setText("\u20b9"+discountprice);
        if(isdiscountavailable.equalsIgnoreCase("true")){
            discRl.setVisibility(View.VISIBLE);
            discpriceRl.setVisibility(View.VISIBLE);
        }
        else{
            discRl.setVisibility(View.GONE);
            discpriceRl.setVisibility(View.GONE);
        }

        try {
            Picasso.get().load(Image).placeholder(R.drawable.default_image).into(prodImg);
        }catch(Exception e){
            prodImg.setImageResource(R.drawable.default_image);
        }

        //showDialogue
        bottomSheetDialog.show();

        //Listeners
        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context, Admin_Edit_Product.class);
                intent.putExtra("prodid",id);
                context.startActivity(intent);
            }
        });

        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder=new androidx.appcompat.app.AlertDialog.Builder(context, androidx.appcompat.R.style.Theme_AppCompat_Dialog);
                builder.setTitle("Delete").setMessage("Are you sure you want to delete this product ?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                deleteProduct(id);
                                bottomSheetDialog.dismiss();
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();

                            }
                        }).show();
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog.dismiss();
            }
        });
    }

    private void deleteProduct(String id) {
        //For All Products
        FirebaseDatabase rootnode = FirebaseDatabase.getInstance();
        DatabaseReference reference1 = rootnode.getReference("Products").child("All Products");

        //VerifySeller
        Query checkproduct1 = reference1.orderByChild("timeStamp").equalTo(id);
        checkproduct1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    reference1.child(id).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(context,"Product Deleted Successfully",Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(context,""+e.getMessage(),Toast.LENGTH_SHORT).show();
                        }
                    });
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //For All Products
        DatabaseReference reference2 = rootnode.getReference("Products").child("Top Offers");

        //VerifySeller
        Query checkproduct2 = reference2.orderByChild("timeStamp").equalTo(id);
        checkproduct2.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    reference2.child(id).removeValue();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //For All Products
        DatabaseReference reference3 = rootnode.getReference("Products").child("Trending");

        //VerifySeller
        Query checkproduct3 = reference3.orderByChild("timeStamp").equalTo(id);
        checkproduct3.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    reference3.child(id).removeValue();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //For All Products
        DatabaseReference reference4 = rootnode.getReference("Products").child("Fruits & Vegetables");

        //VerifySeller
        Query checkproduct4 = reference4.orderByChild("timeStamp").equalTo(id);
        checkproduct4.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    reference4.child(id).removeValue();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //For All Products
        DatabaseReference reference5 = rootnode.getReference("Products").child("Foodgrains, Oil & Masala");

        //VerifySeller
        Query checkproduct5 = reference5.orderByChild("timeStamp").equalTo(id);
        checkproduct5.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    reference5.child(id).removeValue();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //For All Products
        DatabaseReference reference6 = rootnode.getReference("Products").child("Bakery, Cakes & Dairy");

        //VerifySeller
        Query checkproduct6 = reference6.orderByChild("timeStamp").equalTo(id);
        checkproduct6.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    reference6.child(id).removeValue();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //For All Products
        DatabaseReference reference7 = rootnode.getReference("Products").child("Baby Care");

        //VerifySeller
        Query checkproduct7 = reference7.orderByChild("timeStamp").equalTo(id);
        checkproduct7.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    reference7.child(id).removeValue();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //For All Products
        DatabaseReference reference8 = rootnode.getReference("Products").child("Beauty & Hygiene");

        //VerifySeller
        Query checkproduct8 = reference8.orderByChild("timeStamp").equalTo(id);
        checkproduct8.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    reference8.child(id).removeValue();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //For All Products
        DatabaseReference reference9 = rootnode.getReference("Products").child("Cleaning & Household");

        //VerifySeller
        Query checkproduct9 = reference9.orderByChild("timeStamp").equalTo(id);
        checkproduct9.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    reference9.child(id).removeValue();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //For All Products
        DatabaseReference reference10 = rootnode.getReference("Products").child("Gourmet & World Food");

        //VerifySeller
        Query checkproduct10 = reference10.orderByChild("timeStamp").equalTo(id);
        checkproduct10.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    reference10.child(id).removeValue();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //For All Products
        DatabaseReference reference11 = rootnode.getReference("Products").child("Snacks & Branded Foods");

        //VerifySeller
        Query checkproduct11 = reference11.orderByChild("timeStamp").equalTo(id);
        checkproduct11.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    reference11.child(id).removeValue();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //For All Products
        DatabaseReference reference12 = rootnode.getReference("Products").child("Beverages");

        //VerifySeller
        Query checkproduct12 = reference12.orderByChild("timeStamp").equalTo(id);
        checkproduct12.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    reference12.child(id).removeValue();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return productHelperClasses.size();
    }

    @Override
    public Filter getFilter() {
        if(productFilter==null){
            productFilter=new ProductFilter(this,filterList);
        }
        return productFilter;
    }

    public static class ProductAdapterViewHolder extends RecyclerView.ViewHolder {

        RatingBar prodRating;
        RelativeLayout discountPriceRl,discountRl,outOfStockRl;
        ImageView prodImage;
        TextView discount,prodName,prodDescription,discountPrice,productPrice,prodQuantity;
        TextView rupeesign2;

        public ProductAdapterViewHolder(@NonNull View itemView) {
            super(itemView);

            //hooks
            prodImage=itemView.findViewById(R.id.prodImage);
            prodRating=itemView.findViewById(R.id.prodRating);
            prodName=itemView.findViewById(R.id.prodName);
            prodDescription=itemView.findViewById(R.id.prodDescription);
            productPrice=itemView.findViewById(R.id.productPrice);
            prodQuantity=itemView.findViewById(R.id.prodQuanity);
            discount=itemView.findViewById(R.id.discount);
            discountPrice=itemView.findViewById(R.id.discountPrice);
            discountPriceRl=itemView.findViewById(R.id.DiscountPrice);
            discountRl=itemView.findViewById(R.id.discountRl);
            outOfStockRl=itemView.findViewById(R.id.outOfStockRl);

            rupeesign2=itemView.findViewById(R.id.rupeesign2);




        }
    }
}
