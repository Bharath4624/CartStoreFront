package com.order;

import java.sql.*;

public class DatabaseConnection {
    public static Connection getConnection() throws ClassNotFoundException, SQLException {
        Class.forName("com.mysql.cj.jdbc.Driver");
        return DriverManager.getConnection("jdbc:mysql://localhost:3306/order", "root", "bharath123@#");
    }
}