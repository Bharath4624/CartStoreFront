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

    public void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {
        res.setContentType("application/json");
        PrintWriter out = res.getWriter();
        try {
            BufferedReader reader = req.getReader();
            JsonObject jsonObject = gson.fromJson(reader, JsonObject.class);
            String payment_mode = jsonObject.get("method").getAsString();
            String cart_id = CartId.getCartId(req);
            addCharges();
            double service_charge = getCharge(payment_mode);
            double[] totals = calculateTotals(cart_id, service_charge);
            updateCart(totals, service_charge, payment_mode, cart_id);
            JsonObject response = new JsonObject();
            response.addProperty("payment_mode", payment_mode);
            response.addProperty("service_charge", service_charge);
            response.addProperty("totalamount", totals[0]);
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

    public double[] calculateTotals(String cart_id, double service_charge) throws SQLException, ClassNotFoundException {
        String query = "SELECT SUM(cart_items.subtotal),cart.shipping_charge FROM cart INNER JOIN cart_items ON cart_items.cart_id=cart.cart_id WHERE cart.cart_id=?";
        Object[] par = {cart_id};
        ResultSet rs = Cart.persist(query, par);
        if (rs.next()) {
            double subtotal = rs.getDouble(1);
            double shipping_charge = rs.getDouble(2);
            double totaltax = (subtotal * 12) / 100;
            double totalamount = subtotal + shipping_charge + totaltax + service_charge;
            return new double[]{totalamount, subtotal, totaltax};
        }
        return new double[]{0, 0, 0};
    }

    public void updateCart(double[] totals, double service_charge, String payment_mode, String cart_id) throws SQLException, ClassNotFoundException {
        String query = "UPDATE cart SET totaltax=?,subtotal=?,totalamount=?,payment_mode=?,service_charge=? WHERE cart_id=?";
        Object[] par = {totals[2], totals[1], totals[0], payment_mode, service_charge, cart_id};
        Cart.persist(query, par);
    }
}