package com.order;

public class Cart {
    public String cart_id;
    public int cus_id;
    public double totaltax;
    public String shipping_method;
    public double shipping_charge;
    public String payment_mode;
    public double service_charge;
    public double totalamount;
    public double subtotal;

    public Cart(String cart_id, int cus_id, double totaltax, String shipping_method, double shipping_charge, String payment_mode, double service_charge, double totalamount, double subtotal) {
        this.cart_id = cart_id;
        this.cus_id = cus_id;
        this.totaltax = totaltax;
        this.shipping_method = shipping_method;
        this.shipping_charge = shipping_charge;
        this.payment_mode = payment_mode;
        this.service_charge = service_charge;
        this.totalamount = totalamount;
        this.subtotal = subtotal;
    }

    public String getCart_id() {
        return cart_id;
    }

    public void setCart_id(String cart_id) {
        this.cart_id = cart_id;
    }

    public int getCus_id() {
        return cus_id;
    }

    public void setCus_id(int cus_id) {
        this.cus_id = cus_id;
    }

    public double getTotaltax() {
        return totaltax;
    }

    public void setTotaltax(double totaltax) {
        this.totaltax = totaltax;
    }

    public String getShipping_method() {
        return shipping_method;
    }

    public void setShipping_method(String shipping_method) {
        this.shipping_method = shipping_method;
    }

    public double getShipping_charge() {
        return shipping_charge;
    }

    public void setShipping_charge(double shipping_charge) {
        this.shipping_charge = shipping_charge;
    }

    public String getPayment_mode() {
        return payment_mode;
    }

    public void setPayment_mode(String payment_mode) {
        this.payment_mode = payment_mode;
    }

    public double getService_charge() {
        return service_charge;
    }

    public void setService_charge(double service_charge) {
        this.service_charge = service_charge;
    }

    public double getTotalamount() {
        return totalamount;
    }

    public double getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(double subtotal) {
        this.subtotal = subtotal;
    }

    public void setTotalamount(double totalamount) {
        this.totalamount = totalamount;
    }
}

