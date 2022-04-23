package com.example.grocerbasket.Adapters;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
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
import androidx.recyclerview.widget.RecyclerView;

import com.example.grocerbasket.Admin_Edit_Product;
import com.example.grocerbasket.Constructors.ProductHelperClass;
import com.example.grocerbasket.Filters.ProductFilter;
import com.example.grocerbasket.Filters.SearchFilter;
import com.example.grocerbasket.Product_Details;
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

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.SearchAdapterViewHolder> implements Filterable {
    Context context;
    public ArrayList<ProductHelperClass> productHelperClasses,filterList;
    SearchFilter productFilter;

    public SearchAdapter(Context context, ArrayList<ProductHelperClass> productHelperClasses) {
        this.context = context;
        this.productHelperClasses = productHelperClasses;
        this.filterList=productHelperClasses;
    }

    @NonNull
    @Override
    public SearchAdapter.SearchAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_search_layout ,parent, false);
        SearchAdapter.SearchAdapterViewHolder productAdapterViewHolder = new SearchAdapter.SearchAdapterViewHolder(view);

        return productAdapterViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull SearchAdapter.SearchAdapterViewHolder holder, int position) {
        //Get Data
        ProductHelperClass productHelperClass=productHelperClasses.get(position);

        String name=productHelperClass.getProdname();
        String id=productHelperClass.getProdid();
        String subcat=productHelperClass.getSubcat();

        holder.prodName.setText(name);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context, Product_Details.class);
                intent.putExtra("Product ID",id);
                intent.putExtra("Product Subcat",subcat);
                context.startActivity(intent);
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
            productFilter=new SearchFilter(this,filterList);
        }
        return productFilter;
    }

    public static class SearchAdapterViewHolder extends RecyclerView.ViewHolder {

           TextView prodName;

        public SearchAdapterViewHolder(@NonNull View itemView) {
            super(itemView);

            //hooks
            prodName=itemView.findViewById(R.id.prodname);

        }
    }
}
