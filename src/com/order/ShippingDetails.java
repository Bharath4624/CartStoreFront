package com.order;

import com.google.gson.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.sql.*;
import java.util.*;
import java.io.*;

@WebServlet("/cart/shipping")
public class ShippingDetails extends HttpServlet {
    public Gson gson = new Gson();
    public Map<String, Double> charges = new HashMap<>();
    public Cart cart;
    public void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {
        res.setContentType("application/json");
        PrintWriter out = res.getWriter();
        try {
            BufferedReader reader = req.getReader();
            JsonObject jsonObject = gson.fromJson(reader, JsonObject.class);
            String shipping_method = jsonObject.get("method").getAsString();
            String cart_id = CartId.getCartId(req);
            cart=getCart(cart_id);
            addCharges();
            double shipping_charge = getCharge(shipping_method);
            double[] totals = calculateTotals(cart_id, shipping_charge);
            updateCart(shipping_method, shipping_charge, totals, cart_id);
            JsonObject response = new JsonObject();
            response.addProperty("shipping_method", shipping_method);
            response.addProperty("shipping_charge", shipping_charge);
            response.addProperty("totalamount", totals[0]);
            System.out.println(cart.getCart_id()+" "+cart.getCus_id()+" "+cart.getShipping_method()+" "+cart.getShipping_charge()+" "+cart.getPayment_mode()+" "+cart.getService_charge()+" "+cart.getSubtotal()+" "+cart.getTotaltax()+" "+cart.getTotalamount());
            out.println(response);
            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addCharges() {
        charges.put("Standard Shipping", 200.0);
        charges.put("Fast Shipping", 500.0);
        charges.put("International Shipping", 1500.0);
    }

    public double getCharge(String shipping_method) {
        return charges.get(shipping_method);
    }

    public double[] calculateTotals(String cart_id, double shipping_charge) throws SQLException, ClassNotFoundException {
        String query = "SELECT SUM(cart_items.subtotal),cart.totaltax,cart.service_charge FROM cart INNER JOIN cart_items ON cart_items.cart_id=cart.cart_id WHERE cart.cart_id=?";
        Object[] par = {cart_id};
        ResultSet rs = Cart.persist(query, par);
        if (rs.next()) {
            double totalamount = rs.getDouble(1) + rs.getDouble(2) + rs.getDouble(3);
            double totaltax = (rs.getDouble(1) * 12) / 100;
            totalamount += shipping_charge;
            return new double[]{totalamount, rs.getDouble(1), totaltax};
        }
        return new double[]{0, 0};
    }

    public void updateCart(String shipping_method, double shipping_charge, double[] totals, String cart_id) throws SQLException, ClassNotFoundException {
        //String query = "UPDATE cart SET totaltax=?,subtotal=?,totalamount=?,shipping_charge=?,shipping_method=? WHERE cart_id=?";
        cart.setShipping_method(shipping_method);
        cart.setShipping_charge(shipping_charge);
        cart.setTotaltax(totals[2]);
        cart.setTotalamount(totals[0]);
        cart.setSubtotal(totals[1]);
        cart.updateCart();
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