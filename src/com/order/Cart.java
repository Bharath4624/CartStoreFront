package com.order;

import java.sql.*;

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

    public static ResultSet persist(String query, Object[] par) throws SQLException, ClassNotFoundException {
        Connection con = DatabaseConnection.getConnection();
        PreparedStatement stmt = con.prepareStatement(query);
        for (int i = 0; i < par.length; i++) {
            stmt.setObject(i + 1, par[i]);
        }
        if (query.trim().toUpperCase().startsWith("SELECT")) {
            return stmt.executeQuery();
        } else {
            stmt.executeUpdate();
            return null;
        }
    }

    public static void executeQuery(Cart cart,String query) throws SQLException, ClassNotFoundException {
        Connection con=DatabaseConnection.getConnection();
        if(query.equalsIgnoreCase("update")){
            PreparedStatement stmt=con.prepareStatement("UPDATE cart SET cus_id=?,totaltax=?,shipping_method=?,shipping_charge=?,payment_mode=?,service_charge=?,totalamount=?,subtotal=? WHERE cart_id=?");
            stmt.setObject(1,cart.getCus_id());
            stmt.setObject(2,cart.getTotaltax());
            stmt.setObject(3,cart.getShipping_method());
            stmt.setObject(4,cart.getShipping_charge());
            stmt.setObject(5,cart.getPayment_mode());
            stmt.setObject(6,cart.getService_charge());
            stmt.setObject(7,cart.getTotalamount());
            stmt.setObject(8,cart.getSubtotal());
            stmt.setObject(9,cart.getCart_id());
            stmt.executeUpdate();
        }
        else{
            PreparedStatement stmt=con.prepareStatement("INSERT INTO cart VALUES (?,?,?,?,?,?,?,?,?)");
            stmt.setObject(1,cart.getCart_id());
            stmt.setObject(2,cart.getCus_id());
            stmt.setObject(3,cart.getTotaltax());
            stmt.setObject(4,cart.getShipping_method());
            stmt.setObject(5,cart.getShipping_charge());
            stmt.setObject(6,cart.getPayment_mode());
            stmt.setObject(7,cart.getService_charge());
            stmt.setObject(8,cart.getTotalamount());
            stmt.setObject(9,cart.getSubtotal());
            stmt.executeUpdate();
        }
    }
}