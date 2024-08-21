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

    public void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {
        res.setContentType("application/json");
        PrintWriter out = res.getWriter();
        try {
            BufferedReader reader = req.getReader();
            JsonObject jsonObject = gson.fromJson(reader, JsonObject.class);
            String shipping_method = jsonObject.get("method").getAsString();
            String cart_id = CartId.getCartId(req);
            addCharges();
            double shipping_charge = getCharge(shipping_method);
            double[] totals = calculateTotals(cart_id, shipping_charge);
            updateCart(shipping_method, shipping_charge, totals, cart_id);
            JsonObject response = new JsonObject();
            response.addProperty("shipping_method", shipping_method);
            response.addProperty("shipping_charge", shipping_charge);
            response.addProperty("totalamount", totals[0]);
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
        Connection con = DatabaseConnection.getConnection();
        PreparedStatement stmt = con.prepareStatement("SELECT SUM(cart_items.subtotal),cart.totaltax,cart.service_charge FROM cart INNER JOIN cart_items ON cart_items.cart_id=cart.cart_id WHERE cart.cart_id=?");
        stmt.setString(1, cart_id);
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            double totalamount = rs.getDouble(1) + rs.getDouble(2) + rs.getDouble(3);
            double totaltax = (rs.getDouble(1) * 12) / 100;
            totalamount += shipping_charge;
            return new double[]{totalamount, rs.getDouble(1), totaltax};
        }
        return new double[]{0, 0};
    }

    public void updateCart(String shipping_method, double shipping_charge, double[] totals, String cart_id) throws SQLException, ClassNotFoundException {
        Connection con = DatabaseConnection.getConnection();
        PreparedStatement stmt = con.prepareStatement("UPDATE cart SET totaltax=?,subtotal=?,totalamount=?,shipping_charge=?,shipping_method=? WHERE cart_id=?");
        stmt.setDouble(1, totals[2]);
        stmt.setDouble(2, totals[1]);
        stmt.setDouble(3, totals[0]);
        stmt.setDouble(4, shipping_charge);
        stmt.setString(5, shipping_method);
        stmt.setString(6, cart_id);
        stmt.executeUpdate();
    }
}
