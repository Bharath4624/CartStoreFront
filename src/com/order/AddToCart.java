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
    public static Cart cart=new Cart("NULL",0,0.0,"NULL",0.0,"NULL",0.0,0.0,0.0);
    public void doPut(HttpServletRequest req, HttpServletResponse res) throws IOException {
        res.setContentType("application/json");
        PrintWriter out = res.getWriter();
        try {
            BufferedReader reader = req.getReader();
            JsonObject jsonObject = gson.fromJson(reader, JsonObject.class);
            int prod_id;
            String cart_id = generateCartId(req, res);
            cart.setCart_id(cart_id);
            insertCartId(cart_id);
            JsonObject response = new JsonObject();
            if (jsonObject.get("prod_id") != null) {
                prod_id = jsonObject.get("prod_id").getAsInt();
                CartItems item = getProductDetails(prod_id, cart_id);
                if (item != null) {
                    response.addProperty("status", "success");
                    addToCartItems(item,getCartItems(cart_id,prod_id));
                    updateCart(cart_id);
                    System.out.println(cart.getCart_id()+" "+cart.getCus_id()+" "+cart.getShipping_method()+" "+cart.getShipping_charge()+" "+cart.getPayment_mode()+" "+cart.getService_charge()+" "+cart.getSubtotal()+" "+cart.getTotaltax()+" "+cart.getTotalamount());
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
            Cart.persist(query1, par);
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

    public CartItems getCartItems(String cart_id,int prod_id) throws SQLException, ClassNotFoundException {
        String query="SELECT * FROM cart_items WHERE cart_id=? AND prod_id=?";
        Object[] par={cart_id,prod_id};
        ResultSet rs=CartItems.persist(query,par);
        if(rs.next()){
            return new CartItems(rs.getInt("prod_id"), rs.getString("name"), rs.getInt("quantity"), rs.getDouble("subtotal"), rs.getString("cart_id"));
        }
        return null;
    }

    public void addToCartItems(CartItems item,CartItems cartitem) throws SQLException, ClassNotFoundException {
        if(cartitem==null){
            CartItems.executeQuery(item,"insert");
        }
        else{
            cartitem.setQuantity(cartitem.getQuantity()+1);
            cartitem.setSubtotal(cartitem.getQuantity()*item.getSubtotal());
            CartItems.executeQuery(cartitem,"update");
        }
    }

    public void updateCart(String cart_id) throws SQLException, ClassNotFoundException {
        String query = "SELECT SUM(subtotal) FROM cart_items WHERE cart_id=?";
        Object[] par = {cart_id};
        ResultSet rs = CartItems.persist(query, par);
        if (rs.next()) {
            String query1 = "UPDATE cart SET subtotal=?,totaltax=?,totalamount=? WHERE cart_id=?";
            double subtotal=rs.getDouble(1);
            double totaltax=(rs.getDouble(1)*12)/100;
            double totalamount=subtotal+totaltax;
            Object[] par1 = {subtotal,totaltax,totalamount,cart_id};
            cart.setSubtotal(subtotal);
            cart.setTotaltax(totaltax);
            cart.setTotalamount(totalamount);
            Cart.persist(query1, par1);
        }
    }
}