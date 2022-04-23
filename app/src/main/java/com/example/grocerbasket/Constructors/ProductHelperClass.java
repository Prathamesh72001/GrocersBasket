package com.example.grocerbasket.Constructors;

public class ProductHelperClass {

    String prodid,prodname,proddescription,prodquantity,prodprice,discount,
            discountprice,prodimg,isdiscountavail,timeStamp,proddescdetailed,isInStock,subcat,cat;

    float rating;

    public ProductHelperClass() {
    }

    public ProductHelperClass(String prodid, String prodname, String proddescription, String prodquantity, String prodprice,
                              String discount, String discountprice, String prodimg, String isdiscountavail, float rating, String timeStamp,String proddescdetailed,String isInStock,String subcat,String cat){
        this.prodid=prodid;
        this.prodname=prodname;
        this.proddescription=proddescription;
        this.prodquantity=prodquantity;
        this.prodprice=prodprice;
        this.discount=discount;
        this.discountprice=discountprice;
        this.prodimg=prodimg;
        this.isdiscountavail=isdiscountavail;
        this.rating=rating;
        this.timeStamp=timeStamp;
        this.proddescdetailed=proddescdetailed;
        this.isInStock=isInStock;
        this.subcat=subcat;
        this.cat=cat;
    }

    public String getCat() {
        return cat;
    }

    public void setCat(String cat) {
        this.cat = cat;
    }

    public String getSubcat() {
        return subcat;
    }

    public void setSubcat(String subcat) {
        this.subcat = subcat;
    }

    public String getProdid() {
        return prodid;
    }

    public void setProdid(String prodid) {
        this.prodid = prodid;
    }

    public String getProdname() {
        return prodname;
    }

    public void setProdname(String prodname) {
        this.prodname = prodname;
    }

    public String getProddescription() {
        return proddescription;
    }

    public void setProddescription(String proddescription) {
        this.proddescription = proddescription;
    }

    public String getProdquantity() {
        return prodquantity;
    }

    public void setProdquantity(String prodquantity) {
        this.prodquantity = prodquantity;
    }

    public String getProdprice() {
        return prodprice;
    }

    public void setProdprice(String prodprice) {
        this.prodprice = prodprice;
    }

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }

    public String getDiscountprice() {
        return discountprice;
    }

    public void setDiscountprice(String discountprice) {
        this.discountprice = discountprice;
    }

    public String getProdimg() {
        return prodimg;
    }

    public void setProdimg(String prodimg) {
        this.prodimg = prodimg;
    }

    public String getIsdiscountavail() {
        return isdiscountavail;
    }

    public void setIsdiscountavail(String isdiscountavail) {
        this.isdiscountavail = isdiscountavail;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getProddescdetailed() {
        return proddescdetailed;
    }

    public void setProddescdetailed(String proddescdetailed) {
        this.proddescdetailed = proddescdetailed;
    }

    public String getIsInStock() {
        return isInStock;
    }

    public void setIsInStock(String isInStock) {
        this.isInStock = isInStock;
    }
}
