package com.order;


import java.sql.*;
import java.util.*;

public class Cart {
    public String cart_id;
    public Integer cus_id;
    public double totaltax;
    public String shipping_method;
    public double shipping_charge;
    public String payment_mode;
    public double service_charge;
    public double totalamount;
    public double subtotal;
    public List<CartItems> items;

    public Cart(String cart_id, Integer cus_id, double totaltax, String shipping_method, double shipping_charge, String payment_mode, double service_charge, double totalamount, double subtotal, List<CartItems> items) {
        this.cart_id = cart_id;
        this.cus_id = cus_id;
        this.totaltax = totaltax;
        this.shipping_method = shipping_method;
        this.shipping_charge = shipping_charge;
        this.payment_mode = payment_mode;
        this.service_charge = service_charge;
        this.totalamount = totalamount;
        this.subtotal = subtotal;
        this.items = items;
    }

    public String getCart_id() {
        return cart_id;
    }

    public void setCart_id(String cart_id) {
        this.cart_id = cart_id;
    }

    public Integer getCus_id() {
        return cus_id;
    }

    public void setCus_id(Integer cus_id) {
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

    public List<CartItems> getItems() {
        return items;
    }

    public void setItems(List<CartItems> items) {
        this.items = items;
    }

    public void setTotalamount(double totalamount) {
        this.totalamount = totalamount;
    }

    public void compareCart(Cart oldCart) throws SQLException, ClassNotFoundException {
        if (oldCart != null && oldCart.getCart_id().equals(this.getCart_id())) {
            List<String> query = new ArrayList<>();
            List<Object> param = new ArrayList<>();
            if (!this.getCus_id().equals(oldCart.getCus_id())) {
                query.add("cus_id=?");
                param.add(this.getCus_id());
            }
            if (!this.getShipping_method().equals(oldCart.getShipping_method())) {
                query.add("shipping_method=?");
                param.add(this.getShipping_method());
            }
            if (this.getShipping_charge() != oldCart.getShipping_charge()) {
                query.add("shipping_charge=?");
                param.add(this.getShipping_charge());
            }
            if (!this.getPayment_mode().equals(oldCart.getPayment_mode())) {
                query.add("payment_mode=?");
                param.add(this.getPayment_mode());
            }
            if (this.getService_charge() != oldCart.getService_charge()) {
                query.add("service_charge=?");
                param.add(this.getService_charge());
            }
            if (this.getSubtotal() != oldCart.getSubtotal()) {
                query.add("subtotal=?");
                param.add(this.getSubtotal());
            }
            if (this.getTotaltax() != oldCart.getTotaltax()) {
                query.add("totaltax=?");
                param.add(this.getTotaltax());
            }
            if (this.getTotalamount() != oldCart.getTotalamount()) {
                query.add("totalamount=?");
                param.add(this.getTotalamount());
            }
            if (!query.isEmpty()) {
                String q = "UPDATE cart SET " + String.join(",", query) + " WHERE cart_id=?";
                param.add(this.getCart_id());
                persist(q, param.toArray());
            }
        }
    }

    public void compareCartItems(Cart oldCart) throws SQLException, ClassNotFoundException {
        if (oldCart != null && oldCart.getCart_id().equals(this.cart_id)){
            List<CartItems> oldItems=oldCart.getItems();
            List<CartItems> newItems=this.getItems();
            if(newItems.size()<oldItems.size()){
                for(CartItems item:oldItems){
                    if(!newItems.contains(item)){
                        deleteItems(item);
                    }
                }
            }
            else{
                this.updateItems();
            }
        }
    }

    public void updateCart() throws SQLException, ClassNotFoundException {
        Connection con = DatabaseConnection.getConnection();
        PreparedStatement stmt;
        if (getCus_id() == null || getCus_id() == 0) {
            stmt = con.prepareStatement("UPDATE cart SET totaltax=?,shipping_method=?,shipping_charge=?,payment_mode=?,service_charge=?,totalamount=?,subtotal=? WHERE cart_id=?");
            stmt.setObject(1, getTotaltax());
            stmt.setObject(2, getShipping_method());
            stmt.setObject(3, getShipping_charge());
            stmt.setObject(4, getPayment_mode());
            stmt.setObject(5, getService_charge());
            stmt.setObject(6, getTotalamount());
            stmt.setObject(7, getSubtotal());
            stmt.setObject(8, getCart_id());
        } else {
            stmt = con.prepareStatement("UPDATE cart SET cus_id=?,totaltax=?,shipping_method=?,shipping_charge=?,payment_mode=?,service_charge=?,totalamount=?,subtotal=? WHERE cart_id=?");
            stmt.setObject(1, getCus_id());
            stmt.setObject(2, getTotaltax());
            stmt.setObject(3, getShipping_method());
            stmt.setObject(4, getShipping_charge());
            stmt.setObject(5, getPayment_mode());
            stmt.setObject(6, getService_charge());
            stmt.setObject(7, getTotalamount());
            stmt.setObject(8, getSubtotal());
            stmt.setObject(9, getCart_id());
        }
        stmt.executeUpdate();
        con.close();

    }

    public void insertCart() throws SQLException, ClassNotFoundException {
        Connection con = DatabaseConnection.getConnection();
        PreparedStatement stmt = con.prepareStatement("INSERT INTO cart (cart_id,cus_id,totaltax,shipping_method,shipping_charge,payment_mode,service_charge,totalamount,subtotal) VALUES (?,?,?,?,?,?,?,?,?)");
        stmt.setObject(1, getCart_id());
        stmt.setObject(2, getCus_id());
        stmt.setObject(3, getTotaltax());
        stmt.setObject(4, getShipping_method());
        stmt.setObject(5, getShipping_charge());
        stmt.setObject(6, getPayment_mode());
        stmt.setObject(7, getService_charge());
        stmt.setObject(8, getTotalamount());
        stmt.setObject(9, getSubtotal());
        stmt.executeUpdate();
        con.close();
    }

    public void updateItems() throws SQLException, ClassNotFoundException {
        Connection con = DatabaseConnection.getConnection();
        String query = "UPDATE cart_items SET quantity=?, subtotal=? WHERE prod_id=? AND cart_id=?";
        PreparedStatement stmt = con.prepareStatement(query);
        for (CartItems i : getItems()) {
            stmt.setInt(1, i.getQuantity());
            stmt.setDouble(2, i.getSubtotal());
            stmt.setInt(3, i.getProd_id());
            stmt.setString(4, i.getCart_id());
            stmt.executeUpdate();
        }
        con.close();
    }

    public void insertItems(CartItems item) throws SQLException, ClassNotFoundException {
        Connection con = DatabaseConnection.getConnection();
        PreparedStatement stmt = con.prepareStatement("INSERT INTO cart_items (prod_id,name,quantity,subtotal,cart_id) VALUES (?,?,?,?,?)");
        stmt.setObject(1, item.getProd_id());
        stmt.setObject(2, item.getName());
        stmt.setObject(3, item.getQuantity());
        stmt.setObject(4, item.getSubtotal());
        stmt.setObject(5, item.getCart_id());
        stmt.executeUpdate();
        con.close();
    }

    public void deleteItems(CartItems item) throws SQLException, ClassNotFoundException {
        Connection con = DatabaseConnection.getConnection();
        PreparedStatement stmt = con.prepareStatement("DELETE FROM cart_items WHERE cart_id=? AND prod_id=?");
        stmt.setObject(1, item.getCart_id());
        stmt.setObject(2, item.getProd_id());
        stmt.executeUpdate();
        con.close();
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
}