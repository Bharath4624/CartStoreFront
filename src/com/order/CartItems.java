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

    public static List<CartItems> getItems(String cart_id) throws SQLException, ClassNotFoundException {
        Connection con = DatabaseConnection.getConnection();
        PreparedStatement stmt = con.prepareStatement("SELECT * FROM cart_items WHERE cart_id=?");
        stmt.setString(1, cart_id);
        ResultSet rs = stmt.executeQuery();
        List<CartItems> list = new ArrayList<>();
        while (rs.next()) {
            CartItems item = new CartItems(rs.getInt("prod_id"), rs.getString("name"), rs.getInt("quantity"), rs.getDouble("subtotal"), rs.getString("cart_id"));
            list.add(item);
        }
        return list;
    }
}