package com.order;

import com.google.gson.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.*;
import java.sql.*;

@WebServlet("/cart/preview")
public class OrderPreview extends HttpServlet {
    public void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
        res.setContentType("application/json");
        PrintWriter out = res.getWriter();
        try {
            String cart_id = CartId.getCartId(req);
            JsonObject response = new JsonObject();
            response.addProperty("cart_id", cart_id);
            response.add("items", getCartItems(cart_id));
            response.add("billing_details", getSummary(cart_id));
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
        ResultSet rs = DbOperation.executeQuery(query, par);
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

    public JsonObject getSummary(String cart_id) throws SQLException, ClassNotFoundException {
        JsonObject response = new JsonObject();
        String query = "SELECT * FROM cart WHERE cart_id=?";
        Object[] par = {cart_id};
        ResultSet rs = DbOperation.executeQuery(query, par);
        if (rs.next()) {
            response.addProperty("cus_id", rs.getInt("cus_id"));
            response.addProperty("shipping method", rs.getString("shipping_method"));
            response.addProperty("shipping_charge", rs.getDouble("shipping_charge"));
            response.addProperty("payment_mode", rs.getString("payment_mode"));
            response.addProperty("service_charge", rs.getDouble("Service_charge"));
            response.addProperty("subtotal", rs.getDouble("subtotal"));
            response.addProperty("totaltax", rs.getDouble("totaltax"));
            response.addProperty("totalamount", rs.getDouble("totalamount"));
        }
        return response;
    }

    public JsonObject getCustomer(String cart_id) throws SQLException, ClassNotFoundException {
        JsonObject response = new JsonObject();
        String query = "SELECT * FROM cart WHERE cart_id=?";
        Object[] par = {cart_id};
        ResultSet rs = DbOperation.executeQuery(query, par);
        if (rs.next()) {
            String query1 = "SELECT * FROM customers WHERE cus_id=?";
            Object[] par1 = {rs.getInt("cus_id")};
            ResultSet result = DbOperation.executeQuery(query1, par1);
            if (result.next()) {
                response.addProperty("id", result.getInt("cus_id"));
                response.addProperty("name", result.getString("name"));
                response.addProperty("address", result.getString("address") + "," + result.getString("city") + "-" + result.getString("zipcode"));
                response.addProperty("country", result.getString("country"));
                response.addProperty("mobile", result.getString("mobile"));
                response.addProperty("email", result.getString("email"));
            }
        }
        return response;
    }
}