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
    public Cart newCart;

    public void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {
        res.setContentType("application/json");
        PrintWriter out = res.getWriter();
        try {
            BufferedReader reader = req.getReader();
            JsonObject jsonObject = gson.fromJson(reader, JsonObject.class);
            String shipping_method = jsonObject.get("method").getAsString();
            String cart_id = CartId.getCartId(req);
            cart = getCart(cart_id);
            newCart = getCart(cart_id);
            addCharges();
            double shipping_charge = getCharge(shipping_method);
            double[] totals = calculateTotals(shipping_charge);
            updateCart(shipping_method, shipping_charge, totals);
            newCart.compareCart(cart);
            JsonObject response = new JsonObject();
            response.addProperty("shipping_method", shipping_method);
            response.addProperty("shipping_charge", shipping_charge);
            response.addProperty("totalamount", newCart.getTotalamount());
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

    public double[] calculateTotals(double shipping_charge) {
        double totalamount = newCart.getTotalamount();
        double totaltax = newCart.getTotaltax();
        totalamount += shipping_charge;
        return new double[]{totalamount, newCart.getSubtotal(), totaltax};
    }

    public void updateCart(String shipping_method, double shipping_charge, double[] totals) {
        newCart.setShipping_method(shipping_method);
        newCart.setShipping_charge(shipping_charge);
        newCart.setTotaltax(totals[2]);
        newCart.setTotalamount(totals[0]);
        newCart.setSubtotal(totals[1]);
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