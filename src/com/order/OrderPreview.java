package com.order;

import com.google.gson.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.*;
import java.sql.*;
import java.util.ArrayList;

@WebServlet("/cart/preview")
public class OrderPreview extends HttpServlet {
    public Cart cart;

    public void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
        res.setContentType("application/json");
        PrintWriter out = res.getWriter();
        try {
            String cart_id = CartId.getCartId(req);
            cart = getCart(cart_id);
            JsonObject response = new JsonObject();
            response.addProperty("cart_id", cart_id);
            response.add("items", getCartItems(cart_id));
            response.add("billing_details", getSummary());
            response.add("customer", getCustomer(cart_id));
            out.print(response);
            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public JsonArray getCartItems(String cart_id) throws SQLException, ClassNotFoundException {
        String query = "SELECT * FROM cart_items WHERE cart_id=?";
        Object[] par = {cart_id};
        ResultSet rs = CartItems.persist(query, par);
        JsonArray itemsArray = new JsonArray();
        while (rs.next()) {
            JsonObject item = new JsonObject();
            item.addProperty("name", rs.getString("name"));
            item.addProperty("quantity", rs.getInt("quantity"));
            item.addProperty("subtotal", rs.getDouble("subtotal"));
            itemsArray.add(item);
        }
        return itemsArray;
    }

    public JsonObject getSummary() {
        JsonObject response = new JsonObject();
        response.addProperty("shipping method", cart.getShipping_method());
        response.addProperty("shipping_charge", cart.getShipping_charge());
        response.addProperty("payment_mode", cart.getPayment_mode());
        response.addProperty("service_charge", cart.getService_charge());
        response.addProperty("subtotal", cart.getSubtotal());
        response.addProperty("totaltax", cart.getTotaltax());
        response.addProperty("totalamount", cart.getTotalamount());
        return response;
    }

    public JsonObject getCustomer(String cart_id) throws SQLException, ClassNotFoundException {
        JsonObject response = new JsonObject();
        String query = "SELECT * FROM cart WHERE cart_id=?";
        Object[] par = {cart_id};
        ResultSet rs = Cart.persist(query, par);
        if (rs.next()) {
            String query1 = "SELECT * FROM customers WHERE cus_id=?";
            Object[] par1 = {rs.getInt("cus_id")};
            ResultSet result = Customer.persist(query1, par1);
            if (result.next()) {
                response.addProperty("name", result.getString("name"));
                response.addProperty("address", result.getString("address") + "," + result.getString("city") + "-" + result.getString("zipcode"));
                response.addProperty("country", result.getString("country"));
                response.addProperty("mobile", result.getString("mobile"));
                response.addProperty("email", result.getString("email"));
            }
        }
        return response;
    }

    public Cart getCart(String cart_id) throws SQLException, ClassNotFoundException {
        Cart cart = new Cart("NULL", null, 0.0, "NULL", 0.0, "NULL", 0.0, 0.0, 0.0, new ArrayList<>());
        String query = "SELECT * FROM cart WHERE cart_id=?";
        Object[] par = {cart_id};
        ResultSet rs = Cart.persist(query, par);
        if (rs.next()) {
            cart.setCart_id(cart_id);
            cart.setCus_id(rs.getInt("cus_id"));
            cart.setShipping_method(rs.getString("shipping_method"));
            cart.setShipping_charge(rs.getDouble("shipping_charge"));
            cart.setPayment_mode(rs.getString("payment_mode"));
            cart.setService_charge(rs.getDouble("service_charge"));
            cart.setSubtotal(rs.getDouble("subtotal"));
            cart.setTotaltax(rs.getDouble("totaltax"));
            cart.setTotalamount(rs.getDouble("totalamount"));
            cart.setItems(CartItems.getItems(cart_id));
        }
        return cart;
    }
}