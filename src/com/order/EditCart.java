package com.order;

import com.google.gson.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.*;
import java.sql.*;
import java.util.ArrayList;

@WebServlet("/cart/edit")
public class EditCart extends HttpServlet {
    public Gson gson = new Gson();

    public void doPut(HttpServletRequest req, HttpServletResponse res) throws IOException {
        res.setContentType("application/json");
        PrintWriter out = res.getWriter();
        try {
            BufferedReader reader = req.getReader();
            JsonObject jsonObject = gson.fromJson(reader, JsonObject.class);
            int prod_id = jsonObject.get("id").getAsInt();
            String cart_id = jsonObject.get("cart_id").getAsString();
            Cart cart = getCart(cart_id);
            add(cart, prod_id);
            updateCart(cart_id, cart);
            JsonObject response = new JsonObject();
            response.addProperty("cart_id", cart_id);
            response.addProperty("status", "success");
            out.print(response);
            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void add(Cart cart, int prod_id) throws SQLException, ClassNotFoundException {
        for (CartItems item : cart.getItems()) {
            if (item.getProd_id() == prod_id) {
                double price = item.getSubtotal() / item.getQuantity();
                item.setQuantity(item.getQuantity() + 1);
                item.setSubtotal(item.getQuantity() * price);
                cart.updateItems();
            }
        }
    }

    public void doDelete(HttpServletRequest req, HttpServletResponse res) throws IOException {
        res.setContentType("application/json");
        PrintWriter out = res.getWriter();
        try {
            BufferedReader reader = req.getReader();
            JsonObject jsonObject = gson.fromJson(reader, JsonObject.class);
            int prod_id = jsonObject.get("id").getAsInt();
            String cart_id = jsonObject.get("cart_id").getAsString();
            Cart cart = getCart(cart_id);
            delete(cart, prod_id);
            updateCart(cart_id, cart);
            JsonObject response = new JsonObject();
            response.addProperty("cart_id", cart_id);
            response.addProperty("status", "success");
            out.print(response);
            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void delete(Cart cart, int prod_id) throws SQLException, ClassNotFoundException {
        for (CartItems item : cart.getItems()) {
            if (item.getProd_id() == prod_id) {
                double price = item.getSubtotal() / item.getQuantity();
                item.setQuantity(item.getQuantity() - 1);
                if (item.getQuantity() < 1) {
                    cart.deleteItems(item);
                    cart.getItems().remove(item);
                } else {
                    item.setSubtotal(item.getQuantity() * price);
                    cart.updateItems();
                }
            }
        }
    }

    public void updateCart(String cart_id, Cart cart) throws SQLException, ClassNotFoundException {
        if (cart.getCart_id().equals(cart_id)) {
            double subtotal = 0;
            for (CartItems i : cart.getItems()) {
                subtotal += i.getSubtotal();
            }
            double totaltax = (subtotal * 12) / 100;
            double totalamount = totaltax + subtotal;
            cart.setSubtotal(subtotal);
            cart.setTotaltax(totaltax);
            cart.setTotalamount(totalamount);
            cart.updateCart();
        }
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