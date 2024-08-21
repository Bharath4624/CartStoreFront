package com.order;

import com.google.gson.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.*;
import java.sql.*;
import java.util.*;

@WebServlet("/cart")
public class AddToCart extends HttpServlet {
    public Gson gson = new Gson();

    public void doPut(HttpServletRequest req, HttpServletResponse res) throws IOException {
        res.setContentType("application/json");
        PrintWriter out = res.getWriter();
        try {
            BufferedReader reader = req.getReader();
            JsonObject jsonObject = gson.fromJson(reader, JsonObject.class);
            int prod_id;
            String cart_id = generateCartId(req, res);
            insertCartId(cart_id);
            JsonObject response = new JsonObject();
            if (jsonObject.get("prod_id") != null) {
                prod_id = jsonObject.get("prod_id").getAsInt();
                CartItems item = getProductDetails(prod_id, cart_id);
                if (item != null) {
                    response.addProperty("status", "success");
                    addToCart(item);
                } else {
                    response.addProperty("status", "failed,product does not exist");
                    return;
                }
            } else {
                response.addProperty("status", "failed,product does not exist");
                return;
            }
            out.println(response);
            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String generateCartId(HttpServletRequest req, HttpServletResponse res) {
        String cart_id;
        Cookie[] cookies = req.getCookies();
        if (cookies != null) {
            for (Cookie i : cookies) {
                if ("cart_id".equals(i.getName())) {
                    cart_id = i.getValue();
                    return cart_id;
                }
            }
        }
        cart_id = UUID.randomUUID().toString();
        Cookie cartIdCookie = new Cookie("cart_id", cart_id);
        cartIdCookie.setMaxAge(31536000);
        res.addCookie(cartIdCookie);
        return cart_id;
    }

    public void insertCartId(String cart_id) throws SQLException, ClassNotFoundException {
        Connection con = DatabaseConnection.getConnection();
        PreparedStatement stmt = con.prepareStatement("SELECT * FROM cart WHERE cart_id=?");
        stmt.setString(1, cart_id);
        ResultSet rs = stmt.executeQuery();
        if (!rs.next()) {
            PreparedStatement st = con.prepareStatement("INSERT INTO cart(cart_id)VALUES(?)");
            st.setString(1, cart_id);
            st.executeUpdate();
        }
    }

    public CartItems getProductDetails(int prod_id, String cart_id) throws SQLException, ClassNotFoundException {
        Connection con = DatabaseConnection.getConnection();
        PreparedStatement stmt = con.prepareStatement("SELECT * FROM products WHERE prod_id=?");
        stmt.setInt(1, prod_id);
        ResultSet rs = stmt.executeQuery();
        CartItems item = null;
        if (rs.next()) {
            item = new CartItems(prod_id, rs.getString("name"), 1, rs.getDouble("price"), cart_id);
        }
        return item;
    }

    public void addToCart(CartItems item) throws SQLException, ClassNotFoundException {
        Connection con = DatabaseConnection.getConnection();
        PreparedStatement stmt = con.prepareStatement("SELECT * FROM cart_items WHERE cart_id=? AND prod_id=?");
        stmt.setString(1, item.getCart_id());
        stmt.setInt(2, item.getProd_id());
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            item.setQuantity(rs.getInt("quantity") + 1);
            item.setSubtotal(item.getSubtotal() * item.getQuantity());
            stmt = con.prepareStatement("UPDATE cart_items SET quantity=?,subtotal=? WHERE cart_id=? AND prod_id=?");
            stmt.setInt(1, item.getQuantity());
            stmt.setDouble(2, item.getSubtotal());
            stmt.setString(3, item.getCart_id());
            stmt.setInt(4, item.getProd_id());
            stmt.executeUpdate();
        } else {
            stmt = con.prepareStatement("INSERT INTO cart_items(prod_id,name,quantity,subtotal,cart_id) VALUES (?,?,?,?,?)");
            stmt.setInt(1, item.getProd_id());
            stmt.setString(2, item.getName());
            stmt.setInt(3, item.getQuantity());
            stmt.setDouble(4, item.getSubtotal());
            stmt.setString(5, item.getCart_id());
            stmt.executeUpdate();
        }
    }
}