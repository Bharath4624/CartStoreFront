package com.order;

import com.google.gson.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.*;
import java.sql.*;

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
            String cart_id = CartId.getCartId(req);
            CartItems item = getProductDetails(prod_id, cart_id);
            if (item != null) {
                add(item);
            }
            updateCart(cart_id);
            JsonObject response = new JsonObject();
            response.addProperty("cart_id", cart_id);
            response.addProperty("status", "success");
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
        Connection con = DatabaseConnection.getConnection();
        PreparedStatement stmt = con.prepareStatement("UPDATE cart_items SET quantity=?,subtotal=? WHERE cart_id=? AND prod_id=?");
        stmt.setInt(1, item.getQuantity());
        stmt.setDouble(2, item.getSubtotal());
        stmt.setString(3, item.getCart_id());
        stmt.setInt(4, item.getProd_id());
        stmt.executeUpdate();
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
            out.print(response);
            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void delete(CartItems item) throws SQLException, ClassNotFoundException {
        double price = item.getSubtotal() / item.getQuantity();
        item.setQuantity(item.getQuantity() - 1);
        Connection con = DatabaseConnection.getConnection();
        if (item.getQuantity() <= 0) {
            PreparedStatement stmt = con.prepareStatement("DELETE FROM cart_items WHERE prod_id=? AND cart_id=?");
            stmt.setInt(1, item.getProd_id());
            stmt.setString(2, item.getCart_id());
            stmt.executeUpdate();
        } else {
            item.setSubtotal(item.getQuantity() * price);
            PreparedStatement stmt = con.prepareStatement("UPDATE cart_items SET quantity=?,subtotal=? WHERE cart_id=? AND prod_id=?");
            stmt.setInt(1, item.getQuantity());
            stmt.setDouble(2, item.getSubtotal());
            stmt.setString(3, item.getCart_id());
            stmt.setInt(4, item.getProd_id());
            stmt.executeUpdate();
        }
    }

    public CartItems getProductDetails(int prod_id, String cart_id) throws SQLException, ClassNotFoundException {
        CartItems item = null;
        Connection con = DatabaseConnection.getConnection();
        PreparedStatement stmt = con.prepareStatement("SELECT * FROM cart_items WHERE prod_id=? AND cart_id=?");
        stmt.setInt(1, prod_id);
        stmt.setString(2, cart_id);
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            item = new CartItems(rs.getInt("prod_id"), rs.getString("name"), rs.getInt("quantity"), rs.getDouble("subtotal"), rs.getString("cart_id"));
        }
        return item;
    }

    public void updateCart(String cart_id) throws SQLException, ClassNotFoundException {
        Connection con = DatabaseConnection.getConnection();
        PreparedStatement stmt = con.prepareStatement("SELECT SUM(subtotal) FROM cart_items WHERE cart_id=?");
        PreparedStatement stmt1 = con.prepareStatement("SELECT * FROM cart WHERE cart_id=?");
        stmt.setString(1, cart_id);
        ResultSet rs = stmt.executeQuery();
        stmt1.setString(1, cart_id);
        ResultSet result = stmt1.executeQuery();
        if (rs.next() && result.next()) {
            double subtotal = rs.getDouble(1);
            double totaltax = (rs.getDouble(1) * 12) / 100;
            double totalamount = subtotal + totaltax + result.getDouble("shipping_charge") + result.getDouble("service_charge");
            stmt = con.prepareStatement("UPDATE cart SET subtotal=?,totaltax=?,totalamount=? WHERE cart_id=?");
            stmt.setDouble(1, subtotal);
            stmt.setDouble(2, totaltax);
            stmt.setDouble(3, totalamount);
            stmt.setString(4, cart_id);
            stmt.executeUpdate();
        }
    }
}