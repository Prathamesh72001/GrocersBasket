package com.example.grocerbasket.Filters;

import android.widget.Filter;

import com.example.grocerbasket.Adapters.ProductAdapter;
import com.example.grocerbasket.Constructors.ProductHelperClass;

import java.util.ArrayList;

public class ProductFilter extends Filter {

    ProductAdapter adapter;
    ArrayList<ProductHelperClass> productHelperClasses;

    public ProductFilter(ProductAdapter adapter, ArrayList<ProductHelperClass> productHelperClasses) {
        this.adapter = adapter;
        this.productHelperClasses = productHelperClasses;
    }

    @Override
    protected FilterResults performFiltering(CharSequence constraint) {
        FilterResults results=new FilterResults();
        if(constraint!=null && constraint.length()>0){
            constraint=constraint.toString().toUpperCase();
            ArrayList<ProductHelperClass> productHelperClass=new ArrayList<>();
            for(int i=0;i<productHelperClasses.size();i++){
                if(productHelperClasses.get(i).getProdname().toUpperCase().contains(constraint)){
                    productHelperClass.add(productHelperClasses.get(i));
                }
            }
            results.count=productHelperClass.size();
            results.values=productHelperClass;
        }
        else{
            results.count=productHelperClasses.size();
            results.values=productHelperClasses;
        }
        return results;
    }

    @Override
    protected void publishResults(CharSequence constraint, FilterResults results) {
        adapter.productHelperClasses=(ArrayList<ProductHelperClass>)results.values;
        adapter.notifyDataSetChanged();
    }
}
