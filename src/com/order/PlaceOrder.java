package com.order;

import com.google.gson.JsonObject;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.*;
import java.sql.*;
import java.util.ArrayList;

@WebServlet("/order")
public class PlaceOrder extends HttpServlet {
    public Cart cart;
    public Order order;
    public void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {
        res.setContentType("application/json");
        PrintWriter out = res.getWriter();
        try {
            String cart_id = CartId.getCartId(req);
            cart=getCart(cart_id);
            Cart cart = getCartDetails(cart_id);
            int[] details = insertIntoOrder(cart);
            insertInOrderedItems(cart_id, details);
            emptyCart(cart_id);
            cart=null;
            JsonObject response = new JsonObject();
            response.addProperty("status", "success");
            out.println(response);
            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Cart getCartDetails(String cart_id) throws SQLException, ClassNotFoundException {
        Cart cart = null;
        String query = "SELECT * FROM cart WHERE cart_id=?";
        Object[] par = {cart_id};
        ResultSet rs = Cart.persist(query, par);
        if (rs.next()) {
            cart = new Cart(rs.getString("cart_id"), rs.getInt("cus_id"), rs.getDouble("totaltax"), rs.getString("shipping_method"), rs.getDouble("shipping_charge"), rs.getString("payment_mode"), rs.getDouble("service_charge"), rs.getDouble("totalamount"), rs.getDouble("subtotal"),new ArrayList<>());
        }
        return cart;
    }

    public int[] insertIntoOrder(Cart cart) throws SQLException, ClassNotFoundException {
        int[] details = new int[2];
//        String query = "INSERT INTO orders(cus_id,shipping_method,shipping_charge,payment_mode,service_charge,totaltax,totalamount,subtotal)VALUES(?,?,?,?,?,?,?,?)";
//        Object[] par = {cart.getCus_id(), cart.getShipping_method(), cart.getShipping_charge(), cart.getPayment_mode(), cart.getService_charge(), cart.getTotaltax(), cart.getTotalamount(), cart.getSubtotal()};
//        Order.persist(query, par);
        order=new Order(cart.getCus_id(),"", cart.getShipping_method(), cart.getShipping_charge(), cart.getPayment_mode(), cart.getService_charge(), cart.getTotaltax(), cart.getTotalamount(), cart.getSubtotal());
        Order.executeQuery(order);
        String query1 = "SELECT order_id FROM orders ORDER BY order_id DESC LIMIT 1";
        ResultSet generatedKeys = Order.persist(query1, new Object[]{});
        if (generatedKeys.next()) {
            details[0] = generatedKeys.getInt(1);
        } else {
            throw new SQLException("Order cannot be placed");
        }
        String query2 = "SELECT name FROM customers WHERE cus_id=?";
        Object[] par2 = {cart.getCus_id()};
        ResultSet rs = Customer.persist(query2, par2);
        if (rs.next()) {
            String query3 = "UPDATE orders SET cus_name=? WHERE cus_id=?";
            Object[] par3 = {rs.getString("name"), cart.getCus_id()};
            Order.persist(query3, par3);
        }
        details[1] = cart.getCus_id();
        return details;
    }

    public void emptyCart(String cart_id) throws SQLException, ClassNotFoundException {
        String query = "DELETE FROM cart_items WHERE cart_id=?";
        Object[] par = {cart_id};
        CartItems.persist(query, par);
        String query1 = "DELETE FROM cart WHERE cart_id=?";
        Cart.persist(query1, par);
    }

    public void insertInOrderedItems(String cart_id, int[] details) throws SQLException, ClassNotFoundException {
        String query = "SELECT * FROM cart_items WHERE cart_id=?";
        Object[] par = {cart_id};
        ResultSet rs = CartItems.persist(query, par);
        while (rs.next()) {
            String query1 = "INSERT INTO ordered_items(cus_id,order_id,prod_id,name,quantity,subtotal)VALUES(?,?,?,?,?,?)";
            Object[] par1 = {details[1], details[0], rs.getInt("prod_id"), rs.getString("name"), rs.getInt("quantity"), rs.getDouble("subtotal")};
            Order.persist(query1, par1);
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