package com.example.grocerbasket.Filters;

import android.widget.Filter;

import com.example.grocerbasket.Adapters.ProductAdapter;
import com.example.grocerbasket.Adapters.SearchAdapter;
import com.example.grocerbasket.Constructors.ProductHelperClass;
import com.example.grocerbasket.Constructors.SearchHelperClass;

import java.util.ArrayList;

public class SearchFilter extends Filter {

    SearchAdapter adapter;
    ArrayList<ProductHelperClass> searchHelperClasses;

    public SearchFilter(SearchAdapter adapter, ArrayList<ProductHelperClass> searchHelperClasses) {
        this.adapter = adapter;
        this.searchHelperClasses = searchHelperClasses;
    }

    @Override
    protected FilterResults performFiltering(CharSequence constraint) {
        FilterResults results=new FilterResults();
        if(constraint!=null && constraint.length()>0){
            constraint=constraint.toString().toUpperCase();
            ArrayList<ProductHelperClass> searchHelperClass=new ArrayList<>();
            for(int i=0;i<searchHelperClasses.size();i++){
                if(searchHelperClasses.get(i).getProdname().toUpperCase().contains(constraint)){
                    searchHelperClass.add(searchHelperClasses.get(i));
                }
            }
            results.count=searchHelperClass.size();
            results.values=searchHelperClass;
        }
        else{
            results.count=searchHelperClasses.size();
            results.values=searchHelperClasses;
        }
        return results;
    }

    @Override
    protected void publishResults(CharSequence constraint, FilterResults results) {
        adapter.productHelperClasses=(ArrayList<ProductHelperClass>)results.values;
        adapter.notifyDataSetChanged();
    }
}
