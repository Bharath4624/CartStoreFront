package com.order;

import javax.xml.crypto.Data;
import java.sql.*;

public class Order {
    public int cus_id;
    public String cus_name;
    public String shipping_method;
    public double shipping_charge;
    public String payment_mode;
    public double service_charge;
    public double totaltax;
    public double totalamount;
    public double subtotal;

    public Order(int cus_id, String cus_name, String shipping_method, double shipping_charge, String payment_mode, double service_charge, double totaltax, double totalamount, double subtotal) {
        this.cus_id = cus_id;
        this.cus_name = cus_name;
        this.shipping_method = shipping_method;
        this.shipping_charge = shipping_charge;
        this.payment_mode = payment_mode;
        this.service_charge = service_charge;
        this.totaltax = totaltax;
        this.totalamount = totalamount;
        this.subtotal = subtotal;
    }

    public int getCus_id() {
        return cus_id;
    }

    public void setCus_id(int cus_id) {
        this.cus_id = cus_id;
    }

    public String getCus_name() {
        return cus_name;
    }

    public void setCus_name(String cus_name) {
        this.cus_name = cus_name;
    }

    public String getShipping_method() {
        return shipping_method;
    }

    public void setShipping_method(String shipping_method) {
        this.shipping_method = shipping_method;
    }

    public double getShipping_charge() {
        return shipping_charge;
    }

    public void setShipping_charge(double shipping_charge) {
        this.shipping_charge = shipping_charge;
    }

    public String getPayment_mode() {
        return payment_mode;
    }

    public void setPayment_mode(String payment_mode) {
        this.payment_mode = payment_mode;
    }

    public double getService_charge() {
        return service_charge;
    }

    public void setService_charge(double service_charge) {
        this.service_charge = service_charge;
    }

    public double getTotaltax() {
        return totaltax;
    }

    public void setTotaltax(double totaltax) {
        this.totaltax = totaltax;
    }

    public double getTotalamount() {
        return totalamount;
    }

    public void setTotalamount(double totalamount) {
        this.totalamount = totalamount;
    }

    public double getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(double subtotal) {
        this.subtotal = subtotal;
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

    public static void executeQuery(Order order) throws SQLException, ClassNotFoundException {
        Connection con = DatabaseConnection.getConnection();
        PreparedStatement stmt = con.prepareStatement("INSERT INTO orders(cus_id,shipping_method,shipping_charge,payment_mode,service_charge,totaltax,totalamount,subtotal)VALUES(?,?,?,?,?,?,?,?)");
        stmt.setObject(1, order.getCus_id());
        stmt.setObject(2, order.getShipping_method());
        stmt.setObject(3, order.getShipping_charge());
        stmt.setObject(4, order.getPayment_mode());
        stmt.setObject(5, order.getService_charge());
        stmt.setObject(6, order.getTotaltax());
        stmt.setObject(7, order.getTotalamount());
        stmt.setObject(8, order.getSubtotal());
        stmt.executeUpdate();
    }
}
