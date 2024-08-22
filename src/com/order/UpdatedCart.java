package com.order;

import com.google.gson.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.*;
import java.sql.*;

@WebServlet("/cart/items")
public class UpdatedCart extends HttpServlet {
    public void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
        res.setContentType("application/json");
        PrintWriter out = res.getWriter();
        try {
            String cart_id = CartId.getCartId(req);
            String query = "SELECT * FROM cart_items WHERE cart_id=?";
            Object[] par = {cart_id};
            ResultSet rs = CartItems.persist(query, par);
            JsonArray itemsArray = new JsonArray();
            while (rs.next()) {
                JsonObject item = new JsonObject();
                item.addProperty("id", rs.getInt("prod_id"));
                item.addProperty("name", rs.getString("name"));
                item.addProperty("quantity", rs.getInt("quantity"));
                item.addProperty("subtotal", rs.getDouble("subtotal"));
                itemsArray.add(item);
            }
            JsonObject response = new JsonObject();
            response.addProperty("cart_id", cart_id);
            response.addProperty("status", "success");
            response.add("items", itemsArray);
            out.print(response);
            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}