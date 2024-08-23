package com.order;

import com.google.gson.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.*;
import java.sql.*;
import java.util.ArrayList;

@WebServlet("/cart/customer")
public class AddCustomer extends HttpServlet {
    public Gson gson = new Gson();
    public Cart cart;
    public static Customer customer;
    public void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {
        res.setContentType("application/json");
        PrintWriter out = res.getWriter();
        try {
            BufferedReader reader = req.getReader();
            JsonObject jsonObject = gson.fromJson(reader, JsonObject.class);
            JsonObject customerjson = jsonObject.getAsJsonObject("customer");
            String name = customerjson.get("name").getAsString();
            String address = customerjson.get("address").getAsString();
            String city = customerjson.get("city").getAsString();
            String zipcode = customerjson.get("zipcode").getAsString();
            String country = customerjson.get("country").getAsString();
            String mobile = customerjson.get("mobile").getAsString();
            String email = customerjson.get("email").getAsString();
            String cart_id = CartId.getCartId(req);
            cart=getCart(cart_id);
            customer=new Customer(name, address, city, zipcode, country, mobile, email);
            int cus_id = insertCustomer(customer);
            double[] totals = calculateTotal(cart_id);
            updateCart(cus_id, cart_id, totals);
            JsonObject response = new JsonObject();
            response.addProperty("status", "Customer added");
            response.addProperty("subtotal", totals[2]);
            response.addProperty("totaltax", totals[0]);
            response.addProperty("totalamount", totals[1]);
            System.out.println(cart.getCart_id()+" "+cart.getCus_id()+" "+cart.getShipping_method()+" "+cart.getShipping_charge()+" "+cart.getPayment_mode()+" "+cart.getService_charge()+" "+cart.getSubtotal()+" "+cart.getTotaltax()+" "+cart.getTotalamount());
            out.println(response);
            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int insertCustomer(Customer customer) throws SQLException, ClassNotFoundException {
        String query = "SELECT * FROM customers WHERE name=? AND address=? AND city=? AND zipcode=? AND country=? AND mobile=? AND email=?";
        Object[] par = {customer.getName(), customer.getAddress(), customer.getCity(), customer.getZipcode(), customer.getCountry(), customer.getMobile(), customer.getEmail()};
        ResultSet rs = Customer.persist(query, par);
        if (!rs.next()) {
            String query1 = "INSERT INTO customers(name,address,city,zipcode,country,mobile,email)VALUES(?,?,?,?,?,?,?)";
            Customer.persist(query1, par);
            String query2 = "SELECT cus_id FROM customers ORDER BY cus_id DESC LIMIT 1";
            ResultSet generatedKeys = Customer.persist(query2, new Object[]{});
            if (generatedKeys.next()) {
                return generatedKeys.getInt(1);
            } else {
                throw new SQLException("Creating customer failed");
            }
        }
        return rs.getInt("cus_id");
    }

    public double[] calculateTotal(String cart_id) throws SQLException, ClassNotFoundException {
        String query = "SELECT SUM(subtotal) FROM cart_items WHERE cart_id=?";
        Object[] par = {cart_id};
        ResultSet rs = CartItems.persist(query, par);
        if (rs.next()) {
            double subtotal = rs.getDouble(1);
            double totaltax = (subtotal * 12) / 100;
            double totalamount = subtotal + totaltax;
            return new double[]{totaltax, totalamount, subtotal};
        }
        return new double[]{0, 0, 0};
    }

    public void updateCart(int cus_id, String cart_id, double[] totals) throws SQLException, ClassNotFoundException {
        //String query = "UPDATE cart SET subtotal=?,totalamount=?,cus_id=?,totaltax=? WHERE cart_id=?";
        cart.setSubtotal(totals[2]);
        cart.setTotalamount(totals[1]);
        cart.setCus_id(cus_id);
        cart.setTotaltax(totals[0]);
        cart.updateCart();
        //Object[] par = {totals[2], totals[1], cus_id, totals[0], cart_id};
        //Cart.persist(query, par);
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