package com.order;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.*;
import java.sql.*;
import java.util.*;

@WebServlet("/cart/payment")
public class PaymentDetails extends HttpServlet {
    public Gson gson = new Gson();
    public Map<String, Double> servicecharge = new HashMap<>();
    public Cart cart;
    public Cart newCart;

    public void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {
        res.setContentType("application/json");
        PrintWriter out = res.getWriter();
        try {
            BufferedReader reader = req.getReader();
            JsonObject jsonObject = gson.fromJson(reader, JsonObject.class);
            String payment_mode = jsonObject.get("method").getAsString();
            String cart_id = CartId.getCartId(req);
            cart = getCart(cart_id);
            newCart=getCart(cart_id);
            addCharges();
            double service_charge = getCharge(payment_mode);
            double[] totals = calculateTotals(service_charge);
            updateCart(totals, service_charge, payment_mode);
            newCart.compareCart(cart);
            JsonObject response = new JsonObject();
            response.addProperty("payment_mode", payment_mode);
            response.addProperty("service_charge", service_charge);
            response.addProperty("totalamount", newCart.getTotalamount());
            out.println(response);
            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addCharges() {
        servicecharge.put("Online", 50.0);
        servicecharge.put("Offline", 100.0);
    }

    public double getCharge(String payment_mode) {
        return servicecharge.get(payment_mode);
    }

    public double[] calculateTotals(double service_charge) {
        double subtotal = newCart.getSubtotal();
        double shipping_charge = newCart.getShipping_charge();
        double totaltax = newCart.getTotaltax();
        double totalamount = subtotal + shipping_charge + totaltax + service_charge;
        return new double[]{totalamount, subtotal, totaltax};
    }

    public void updateCart(double[] totals, double service_charge, String payment_mode) throws SQLException, ClassNotFoundException {
        newCart.setPayment_mode(payment_mode);
        newCart.setService_charge(service_charge);
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