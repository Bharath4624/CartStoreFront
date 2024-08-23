package com.order;

import com.google.gson.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.*;
import java.sql.*;
import java.util.ArrayList;

@WebServlet("/cart/items")
public class UpdatedCart extends HttpServlet {
    public void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
        res.setContentType("application/json");
        PrintWriter out = res.getWriter();
        try {
            String cart_id = CartId.getCartId(req);
            Cart cart = getCart(cart_id);
            JsonArray itemsArray = new JsonArray();
            for (CartItems i : cart.getItems()) {
                JsonObject item = new JsonObject();
                item.addProperty("id", i.getProd_id());
                item.addProperty("name", i.getName());
                item.addProperty("quantity", i.getQuantity());
                item.addProperty("subtotal", i.getSubtotal());
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