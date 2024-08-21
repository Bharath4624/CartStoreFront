package com.order;

import com.google.gson.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.*;
import java.sql.*;

@WebServlet("/cart/customer")
public class AddCustomer extends HttpServlet {
    public Gson gson = new Gson();

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
            Customer customer = new Customer(name, address, city, zipcode, country, mobile, email);
            int cus_id = insertCustomer(customer);
            double[] totals = calculateTotal(cart_id);
            updateCart(cus_id, cart_id, totals);
            JsonObject response = new JsonObject();
            response.addProperty("status", "Customer added");
            response.addProperty("subtotal", totals[2]);
            response.addProperty("totaltax", totals[0]);
            response.addProperty("totalamount", totals[1]);
            out.println(response);
            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int insertCustomer(Customer customer) throws SQLException, ClassNotFoundException {
        Connection con = DatabaseConnection.getConnection();
        PreparedStatement stmt = con.prepareStatement("SELECT * FROM customers WHERE name=? AND address=? AND city=? AND zipcode=? AND country=? AND mobile=? AND email=?");
        stmt.setString(1, customer.getName());
        stmt.setString(2, customer.getAddress());
        stmt.setString(3, customer.getCity());
        stmt.setString(4, customer.getZipcode());
        stmt.setString(5, customer.getCountry());
        stmt.setString(6, customer.getMobile());
        stmt.setString(7, customer.getEmail());
        ResultSet rs = stmt.executeQuery();
        if (!rs.next()) {
            stmt = con.prepareStatement("INSERT INTO customers(name,address,city,zipcode,country,mobile,email)VALUES(?,?,?,?,?,?,?)", Statement.RETURN_GENERATED_KEYS);
            stmt.setString(1, customer.getName());
            stmt.setString(2, customer.getAddress());
            stmt.setString(3, customer.getCity());
            stmt.setString(4, customer.getZipcode());
            stmt.setString(5, customer.getCountry());
            stmt.setString(6, customer.getMobile());
            stmt.setString(7, customer.getEmail());
            stmt.executeUpdate();
            ResultSet generatedKeys = stmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                return generatedKeys.getInt(1);
            } else {
                throw new SQLException("Creating customer failed");
            }
        }
        return rs.getInt("cus_id");
    }

    public double[] calculateTotal(String cart_id) throws SQLException, ClassNotFoundException {
        Connection con = DatabaseConnection.getConnection();
        PreparedStatement stmt = con.prepareStatement("SELECT SUM(subtotal) FROM cart_items WHERE cart_id=?");
        stmt.setString(1, cart_id);
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            double subtotal = rs.getDouble(1);
            double totaltax = (subtotal * 12) / 100;
            double totalamount = subtotal + totaltax;
            return new double[]{totaltax, totalamount, subtotal};
        }
        return new double[]{0, 0, 0};
    }

    public void updateCart(int cus_id, String cart_id, double[] totals) throws SQLException, ClassNotFoundException {
        Connection con = DatabaseConnection.getConnection();
        PreparedStatement stmt = con.prepareStatement("UPDATE cart SET subtotal=?,totalamount=?,cus_id=?,totaltax=? WHERE cart_id=?");
        stmt.setDouble(1, totals[2]);
        stmt.setDouble(2, totals[1]);
        stmt.setInt(3, cus_id);
        stmt.setDouble(4, totals[0]);
        stmt.setString(5, cart_id);
        stmt.executeUpdate();
    }
}
