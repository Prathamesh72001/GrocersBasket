package com.example.grocerbasket.Constructors;

import java.util.Map;

public class CartHelperClass {

    String cartfinalcost,cartorgcost,cartquan,userphoneno;
    Map<String,CartProdHelperClass> cartProdHelperClass;

    public CartHelperClass() {
    }

    public CartHelperClass(Map<String,CartProdHelperClass> cartProdHelperClass,String cartorgcost ,String cartfinalcost, String cartquan,String userphoneno) {
        this.cartfinalcost = cartfinalcost;
        this.cartorgcost = cartorgcost;
        this.cartquan = cartquan;
        this.userphoneno=userphoneno;
        this.cartProdHelperClass=cartProdHelperClass;
    }

    public Map<String, CartProdHelperClass> getCartProdHelperClass() {
        return cartProdHelperClass;
    }

    public void setCartProdHelperClass(Map<String, CartProdHelperClass> cartProdHelperClass) {
        this.cartProdHelperClass = cartProdHelperClass;
    }

    public String getCartorgcost() {
        return cartorgcost;
    }

    public void setCartorgcost(String cartorgcost) {
        this.cartorgcost = cartorgcost;
    }

    public String getCartfinalcost() {
        return cartfinalcost;
    }

    public void setCartfinalcost(String cartfinalcost) {
        this.cartfinalcost = cartfinalcost;
    }

    public String getCartquan() {
        return cartquan;
    }

    public void setCartquan(String cartquan) {
        this.cartquan = cartquan;
    }

    public String getUserphoneno() {
        return userphoneno;
    }

    public void setUserphoneno(String userphoneno) {
        this.userphoneno = userphoneno;
    }
}
