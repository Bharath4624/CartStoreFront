package com.order;

import com.google.gson.JsonObject;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.*;
import java.sql.*;

@WebServlet("/order")
public class PlaceOrder extends HttpServlet {
    public void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {
        res.setContentType("application/json");
        PrintWriter out = res.getWriter();
        try {
            String cart_id = CartId.getCartId(req);
            Cart cart = getCartDetails(cart_id);
            int[] details = insertIntoOrder(cart);
            insertInOrderedItems(cart_id, details);
            emptyCart(cart_id);
            JsonObject response = new JsonObject();
            response.addProperty("status", "success");
            out.println(response);
            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Cart getCartDetails(String cart_id) throws SQLException, ClassNotFoundException {
        Cart cart = null;
        Connection con = DatabaseConnection.getConnection();
        PreparedStatement stmt = con.prepareStatement("SELECT * FROM cart WHERE cart_id=?");
        stmt.setString(1, cart_id);
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            cart = new Cart(rs.getString("cart_id"), rs.getInt("cus_id"), rs.getDouble("totaltax"), rs.getString("shipping_method"), rs.getDouble("shipping_charge"), rs.getString("payment_mode"), rs.getDouble("service_charge"), rs.getDouble("totalamount"), rs.getDouble("subtotal"));
        }
        return cart;
    }

    public int[] insertIntoOrder(Cart cart) throws SQLException, ClassNotFoundException {
        int[] details = new int[2];
        Connection con = DatabaseConnection.getConnection();
        PreparedStatement stmt = con.prepareStatement("INSERT INTO orders(cus_id,shipping_method,shipping_charge,payment_mode,service_charge,totaltax,totalamount,subtotal)VALUES(?,?,?,?,?,?,?,?)", Statement.RETURN_GENERATED_KEYS);
        stmt.setInt(1, cart.getCus_id());
        stmt.setString(2, cart.getShipping_method());
        stmt.setDouble(3, cart.getShipping_charge());
        stmt.setString(4, cart.getPayment_mode());
        stmt.setDouble(5, cart.getService_charge());
        stmt.setDouble(6, cart.getTotaltax());
        stmt.setDouble(7, cart.getTotalamount());
        stmt.setDouble(8, cart.getSubtotal());
        stmt.executeUpdate();
        ResultSet generatedKeys = stmt.getGeneratedKeys();
        if (generatedKeys.next()) {
            details[0] = generatedKeys.getInt(1);
        } else {
            throw new SQLException("Order cannot be placed");
        }
        stmt = con.prepareStatement("SELECT name FROM customers WHERE cus_id=?");
        stmt.setInt(1, cart.getCus_id());
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            stmt = con.prepareStatement("UPDATE orders SET cus_name=? WHERE cus_id=?");
            stmt.setString(1, rs.getString("name"));
            stmt.setInt(2, cart.getCus_id());
            stmt.executeUpdate();
        }
        details[1] = cart.getCus_id();
        return details;
    }

    public void emptyCart(String cart_id) throws SQLException, ClassNotFoundException {
        Connection con = DatabaseConnection.getConnection();
        PreparedStatement stmt;
        stmt = con.prepareStatement("DELETE FROM cart_items WHERE cart_id=?");
        stmt.setString(1, cart_id);
        stmt.executeUpdate();
        stmt = con.prepareStatement("DELETE FROM cart WHERE cart_id=?");
        stmt.setString(1, cart_id);
        stmt.executeUpdate();
    }

    public void insertInOrderedItems(String cart_id, int[] details) throws SQLException, ClassNotFoundException {
        Connection con = DatabaseConnection.getConnection();
        PreparedStatement stmt = con.prepareStatement("SELECT * FROM cart_items WHERE cart_id=?");
        stmt.setString(1, cart_id);
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            stmt = con.prepareStatement("INSERT INTO ordered_items(cus_id,order_id,prod_id,name,quantity,subtotal)VALUES(?,?,?,?,?,?)");
            stmt.setInt(1, details[1]);
            stmt.setInt(2, details[0]);
            stmt.setInt(3, rs.getInt("prod_id"));
            stmt.setString(4, rs.getString("name"));
            stmt.setInt(5, rs.getInt("quantity"));
            stmt.setDouble(6, rs.getDouble("subtotal"));
            stmt.executeUpdate();
        }
    }

}
