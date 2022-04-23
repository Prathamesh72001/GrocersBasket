package com.example.grocerbasket.Constructors;

public class OrderHelperClass {
    String orderid,ordertime,orderstatus,ordercost,orderby,orderto,payOption,deliveryOption;
    int progress;
    public OrderHelperClass() {
    }

    public OrderHelperClass(String orderid, String ordertime, String orderstatus, String ordercost, String orderby, String orderto ,String payOption, String deliveryOption,int progress) {
        this.orderid = orderid;
        this.ordertime = ordertime;
        this.orderstatus = orderstatus;
        this.ordercost = ordercost;
        this.orderby = orderby;
        this.orderto = orderto;
        this.payOption = payOption;
        this.deliveryOption = deliveryOption;
        this.progress=progress;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public String getPayOption() {
        return payOption;
    }

    public void setPayOption(String payOption) {
        this.payOption = payOption;
    }

    public String getDeliveryOption() {
        return deliveryOption;
    }

    public void setDeliveryOption(String deliveryOption) {
        this.deliveryOption = deliveryOption;
    }

    public String getOrderid() {
        return orderid;
    }

    public void setOrderid(String orderid) {
        this.orderid = orderid;
    }

    public String getOrdertime() {
        return ordertime;
    }

    public void setOrdertime(String ordertime) {
        this.ordertime = ordertime;
    }

    public String getOrderstatus() {
        return orderstatus;
    }

    public void setOrderstatus(String orderstatus) {
        this.orderstatus = orderstatus;
    }

    public String getOrdercost() {
        return ordercost;
    }

    public void setOrdercost(String ordercost) {
        this.ordercost = ordercost;
    }

    public String getOrderby() {
        return orderby;
    }

    public void setOrderby(String orderby) {
        this.orderby = orderby;
    }

    public String getOrderto() {
        return orderto;
    }

    public void setOrderto(String orderto) {
        this.orderto = orderto;
    }
}
