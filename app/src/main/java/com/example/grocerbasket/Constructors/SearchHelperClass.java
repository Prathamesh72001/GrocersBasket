package com.example.grocerbasket.Constructors;

public class SearchHelperClass {
    String prodname;

    public SearchHelperClass() {
    }

    public SearchHelperClass(String prodname) {
        this.prodname = prodname;
    }

    public String getProdname() {
        return prodname;
    }

    public void setProdname(String prodname) {
        this.prodname = prodname;
    }
}
