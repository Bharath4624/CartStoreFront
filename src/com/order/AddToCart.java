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
    public Cart newCart;
    public Cart cart;

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
                    updateCart();
                    newCart.compareCart(cart);
                    newCart.compareCartItems(cart);
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
            newCart = new Cart("NULL", null, 0.0, "NULL", 0.0, "NULL", 0.0, 0.0, 0.0, new ArrayList<>());
            newCart.setCart_id(cart_id);
            newCart.insertCart();
        } else {
            cart = new Cart("NULL", null, 0.0, "NULL", 0.0, "NULL", 0.0, 0.0, 0.0, new ArrayList<>());
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
            newCart = new Cart("NULL", null, 0.0, "NULL", 0.0, "NULL", 0.0, 0.0, 0.0, new ArrayList<>());
            newCart.setCart_id(cart_id);
            newCart.setCus_id(rs.getInt("cus_id"));
            newCart.setShipping_method(rs.getString("shipping_method"));
            newCart.setShipping_charge(rs.getDouble("shipping_charge"));
            newCart.setPayment_mode(rs.getString("payment_mode"));
            newCart.setService_charge(rs.getDouble("service_charge"));
            newCart.setSubtotal(rs.getDouble("subtotal"));
            newCart.setTotaltax(rs.getDouble("totaltax"));
            newCart.setTotalamount(rs.getDouble("totalamount"));
            newCart.setItems(CartItems.getItems(cart_id));
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
        boolean itemExists = false;
        if (newCart != null) {
            for (CartItems i : newCart.getItems()) {
                if (i.getProd_id() == item.getProd_id()) {
                    itemExists = true;
                    i.setQuantity(i.getQuantity() + 1);
                    i.setSubtotal(i.getQuantity() * item.getSubtotal());
                    break;
                }
            }
        }
        if (!itemExists) {
            newCart.getItems().add(item);
            newCart.insertItems(item);
        }
    }

    public void updateCart() {
        double subtotal = 0;
        for (CartItems i : newCart.getItems()) {
            subtotal += i.getSubtotal();
        }
        newCart.setSubtotal(subtotal);
        newCart.setTotaltax((subtotal * 12) / 100);
        newCart.setTotalamount(newCart.getSubtotal()+newCart.getTotaltax());
    }
}