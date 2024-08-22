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
                    addToCartItems(item);
                    updateCart(cart_id);
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
        String query = "SELECT * FROM cart WHERE cart_id=?";
        Object[] par = {cart_id};
        ResultSet rs = Cart.persist(query, par);
        if (!rs.next()) {
            String query1 = "INSERT INTO cart(cart_id)VALUES(?)";
            Object[] par1 = {cart_id};
            Cart.persist(query1, par1);
        }
    }

    public CartItems getProductDetails(int prod_id, String cart_id) throws SQLException, ClassNotFoundException {
        String query = "SELECT * FROM products WHERE prod_id=?";
        Object[] par = {prod_id};
        ResultSet rs = Cart.persist(query, par);
        CartItems item = null;
        if (rs.next()) {
            item = new CartItems(prod_id, rs.getString("name"), 1, rs.getDouble("price"), cart_id);
        }
        return item;
    }

    public void addToCartItems(CartItems item) throws SQLException, ClassNotFoundException {
        String query = "SELECT * FROM cart_items WHERE cart_id=? AND prod_id=?";
        Object[] par = {item.getCart_id(), item.getProd_id()};
        ResultSet rs = CartItems.persist(query, par);
        if (rs.next()) {
            item.setQuantity(rs.getInt("quantity") + 1);
            item.setSubtotal(item.getSubtotal() * item.getQuantity());
            String query1 = "UPDATE cart_items SET quantity=?,subtotal=? WHERE cart_id=? AND prod_id=?";
            Object[] par1 = {item.getQuantity(), item.getSubtotal(), item.getCart_id(), item.getProd_id()};
            CartItems.persist(query1, par1);
        } else {
            String query2 = "INSERT INTO cart_items(prod_id,name,quantity,subtotal,cart_id) VALUES (?,?,?,?,?)";
            Object[] par2 = {item.getProd_id(), item.getName(), item.getQuantity(), item.getSubtotal(), item.getCart_id()};
            CartItems.persist(query2, par2);
        }
    }

    public void updateCart(String cart_id) throws SQLException, ClassNotFoundException {
        String query = "SELECT SUM(subtotal) FROM cart_items WHERE cart_id=?";
        Object[] par = {cart_id};
        ResultSet rs = CartItems.persist(query, par);
        if (rs.next()) {
            String query1 = "UPDATE cart SET subtotal=? WHERE cart_id=?";
            Object[] par1 = {rs.getDouble(1), cart_id};
            Cart.persist(query1, par1);
        }
    }
}