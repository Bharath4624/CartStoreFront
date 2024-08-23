package com.order;

import javax.xml.crypto.Data;
import java.sql.*;

public class Customer {
    public String name;
    public String address;
    public String city;
    public String zipcode;
    public String country;
    public String mobile;
    public String email;

    public Customer(String name, String address, String city, String zipcode, String country, String mobile, String email) {
        this.name = name;
        this.address = address;
        this.city = city;
        this.zipcode = zipcode;
        this.country = country;
        this.mobile = mobile;
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getZipcode() {
        return zipcode;
    }

    public void setZipcode(String zipcode) {
        this.zipcode = zipcode;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public static ResultSet persist(String query, Object[] par) throws SQLException, ClassNotFoundException {
        Connection con = DatabaseConnection.getConnection();
        PreparedStatement stmt = con.prepareStatement(query);
        for (int i = 0; i < par.length; i++) {
            stmt.setObject(i + 1, par[i]);
        }
        if (query.trim().toUpperCase().startsWith("SELECT")) {
            return stmt.executeQuery();
        } else {
            stmt.executeUpdate();
            return null;
        }
    }

    public static void executeQuery(Customer customer) throws SQLException, ClassNotFoundException {
        Connection con = DatabaseConnection.getConnection();
        PreparedStatement stmt = con.prepareStatement("INSERT INTO customers(name,address,city,zipcode,country,mobile,email)VALUES(?,?,?,?,?,?,?)");
        stmt.setObject(1, customer.getName());
        stmt.setObject(2, customer.getAddress());
        stmt.setObject(3, customer.getCity());
        stmt.setObject(4, customer.getZipcode());
        stmt.setObject(5, customer.getCountry());
        stmt.setObject(6, customer.getMobile());
        stmt.setObject(7, customer.getEmail());
        stmt.executeUpdate();
    }
}