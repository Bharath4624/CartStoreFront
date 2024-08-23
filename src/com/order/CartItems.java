package com.order;

import java.sql.*;
import java.util.*;

public class CartItems {
    public int prod_id;
    public String name;
    public int quantity;
    public double subtotal;
    public String cart_id;

    public int getProd_id() {
        return prod_id;
    }

    public void setProd_id(int prod_id) {
        this.prod_id = prod_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(double subtotal) {
        this.subtotal = subtotal;
    }

    public String getCart_id() {
        return cart_id;
    }

    public void setCart_id(String cart_id) {
        this.cart_id = cart_id;
    }

    public CartItems(int prod_id, String name, int quantity, double subtotal, String cart_id) {
        this.prod_id = prod_id;
        this.name = name;
        this.quantity = quantity;
        this.subtotal = subtotal;
        this.cart_id = cart_id;
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

    public static void executeQuery(CartItems i, String query) throws SQLException, ClassNotFoundException {
        Connection con = DatabaseConnection.getConnection();
        if (query.equalsIgnoreCase("insert")) {
            PreparedStatement stmt = con.prepareStatement("INSERT INTO cart_items(prod_id,name,quantity,subtotal,cart_id) VALUES(?,?,?,?,?)");
            stmt.setObject(1, i.getProd_id());
            stmt.setObject(2, i.getName());
            stmt.setObject(3, i.getQuantity());
            stmt.setObject(4, i.getSubtotal());
            stmt.setObject(5, i.getCart_id());
            stmt.executeUpdate();
        } else if (query.equalsIgnoreCase("update")) {
            PreparedStatement stmt = con.prepareStatement("UPDATE cart_items SET prod_id=?,name=?,quantity=?,subtotal=? WHERE cart_id=? AND prod_id=?");
            stmt.setObject(1, i.getProd_id());
            stmt.setObject(2, i.getName());
            stmt.setObject(3, i.getQuantity());
            stmt.setObject(4, i.getSubtotal());
            stmt.setObject(5, i.getCart_id());
            stmt.setObject(6,i.getProd_id());
            stmt.executeUpdate();
        }
    }
}