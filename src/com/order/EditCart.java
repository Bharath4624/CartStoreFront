package com.order;

import com.google.gson.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.*;
import java.sql.*;

@WebServlet("/cart/edit")
public class EditCart extends HttpServlet {
    public Gson gson = new Gson();
    public static Cart cart=AddToCart.cart;
    public void doPut(HttpServletRequest req, HttpServletResponse res) throws IOException {
        res.setContentType("application/json");
        PrintWriter out = res.getWriter();
        try {
            BufferedReader reader = req.getReader();
            JsonObject jsonObject = gson.fromJson(reader, JsonObject.class);
            int prod_id = jsonObject.get("id").getAsInt();
            String cart_id = CartId.getCartId(req);
            CartItems item = getProductDetails(prod_id, cart_id);
            if (item != null) {
                add(item);
            }
            updateCart(cart_id);
            JsonObject response = new JsonObject();
            response.addProperty("cart_id", cart_id);
            response.addProperty("status", "success");
            System.out.println(cart.getCart_id()+" "+cart.getCus_id()+" "+cart.getShipping_method()+" "+cart.getShipping_charge()+" "+cart.getPayment_mode()+" "+cart.getService_charge()+" "+cart.getSubtotal()+" "+cart.getTotaltax()+" "+cart.getTotalamount());
            out.print(response);
            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void add(CartItems item) throws SQLException, ClassNotFoundException {
        double price = item.getSubtotal() / item.getQuantity();
        item.setQuantity(item.getQuantity() + 1);
        item.setSubtotal(item.getQuantity() * price);
        CartItems.executeQuery(item,"update");
    }

    public void doDelete(HttpServletRequest req, HttpServletResponse res) throws IOException {
        res.setContentType("application/json");
        PrintWriter out = res.getWriter();
        try {
            BufferedReader reader = req.getReader();
            JsonObject jsonObject = gson.fromJson(reader, JsonObject.class);
            int prod_id = jsonObject.get("id").getAsInt();
            String cart_id = CartId.getCartId(req);
            CartItems item = getProductDetails(prod_id, cart_id);
            if (item != null) {
                delete(item);
            }
            updateCart(cart_id);
            JsonObject response = new JsonObject();
            response.addProperty("cart_id", cart_id);
            response.addProperty("status", "success");
            System.out.println(cart.getCart_id()+" "+cart.getCus_id()+" "+cart.getShipping_method()+" "+cart.getShipping_charge()+" "+cart.getPayment_mode()+" "+cart.getService_charge()+" "+cart.getSubtotal()+" "+cart.getTotaltax()+" "+cart.getTotalamount());
            out.print(response);
            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void delete(CartItems item) throws SQLException, ClassNotFoundException {
        double price = item.getSubtotal() / item.getQuantity();
        item.setQuantity(item.getQuantity() - 1);
        if (item.getQuantity() <= 0) {
            CartItems.executeQuery(item,"delete");
        } else {
            item.setSubtotal(item.getQuantity() * price);
            CartItems.executeQuery(item,"update");
        }
    }

    public CartItems getProductDetails(int prod_id, String cart_id) throws SQLException, ClassNotFoundException {
        CartItems item = null;
        String query = "SELECT * FROM cart_items WHERE prod_id=? AND cart_id=?";
        Object[] par = {prod_id, cart_id};
        ResultSet rs = CartItems.persist(query, par);
        if (rs.next()) {
            item = new CartItems(rs.getInt("prod_id"), rs.getString("name"), rs.getInt("quantity"), rs.getDouble("subtotal"), rs.getString("cart_id"));
        }
        return item;
    }

    public void updateCart(String cart_id) throws SQLException, ClassNotFoundException {
        String query = "SELECT SUM(subtotal) FROM cart_items WHERE cart_id=?";
        String query1 = "SELECT * FROM cart WHERE cart_id=?";
        Object[] par = {cart_id};
        ResultSet rs = CartItems.persist(query, par);
        ResultSet result = Cart.persist(query1, par);
        if (rs.next() && result.next()) {
            double subtotal = rs.getDouble(1);
            double totaltax = (rs.getDouble(1) * 12) / 100;
            double totalamount = subtotal + totaltax + result.getDouble("shipping_charge") + result.getDouble("service_charge");
            String query2 = "UPDATE cart SET subtotal=?,totaltax=?,totalamount=? WHERE cart_id=?";
            cart.setSubtotal(subtotal);
            cart.setTotaltax(totaltax);
            cart.setTotalamount(totalamount);
            Object[] par2 = {subtotal, totaltax, totalamount, cart_id};
            Cart.persist(query2, par2);
        }
    }
}