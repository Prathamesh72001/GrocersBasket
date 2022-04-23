package com.example.grocerbasket.Constructors;

public class SellerHelperClass {

    String firstname,lastname,shopname,shopadd,delfee,email,phoneno,password;

    public SellerHelperClass() {
    }

    public SellerHelperClass(String firstname, String lastname,String shopname,String shopadd,String delfee, String email,String phoneno,String password ){
        this.firstname = firstname;
        this.lastname = lastname;
        this.shopname = shopname;
        this.shopadd = shopadd;
        this.delfee = delfee;
        this.email = email;
        this.phoneno = phoneno;
        this.password = password;

    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneno() {
        return phoneno;
    }

    public void setPhoneno(String phoneno) {
        this.phoneno = phoneno;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getShopname() {
        return shopname;
    }

    public void setShopname(String shopname) {
        this.shopname = shopname;
    }

    public String getShopadd() {
        return shopadd;
    }

    public void setShopadd(String shopadd) {
        this.shopadd = shopadd;
    }

    public String getDelfee() {
        return delfee;
    }

    public void setDelfee(String delfee) {
        this.delfee = delfee;
    }
}
