<%@page language="java" contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="java.sql.*"%>
<%
try{
Class.forName("com.mysql.cj.jdbc.Driver");
}
catch(ClassNotFoundException e){
     e.printStackTrace();
}
Connection con=null;
%>
<!DOCTYPE html>
<html>
<head>
<title>Order Success</title>
</head>
<body>
    <h3>Order success</h3>
    <h4>Order details</h4>
    <form action="order" method="post">
    <table border="1px" width="100%">
        <%
        try {
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/order","root","bharath123@#");
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM orders ORDER BY order_id DESC LIMIT 1");
            if(rs.next()) {
        %>
        <tr>
        <td>Order_Id</td>
        <td><%=rs.getInt("order_id")%></td>
        </tr>
        <tr>
        <td>Customer_Id</td>
        <td><%=rs.getInt("cus_id")%></td>
        </tr>
        <tr>
        <td>Customer_Name</td>
        <td><%=rs.getString("cus_name")%></td>
        </tr>
        <tr>
        <td>Shipping_method</td>
        <td><%=rs.getString("shipping_method")%></td>
        </tr>
        <tr>
        <td>Shipping_charge</td>
        <td><%=rs.getDouble("shipping_charge")%></td>
        </tr>
        <tr>
        <td>Payment_mode</td>
        <td><%=rs.getString("payment_mode")%></td>
        </tr>
        <tr>
        <td>Service_charge</td>
        <td><%=rs.getDouble("service_charge")%></td>
        </tr>
        <tr>
        <td>Total_tax</td>
        <td><%=rs.getDouble("totaltax")%></td>
        </tr>
        <tr>
        <td>Total_Amount</td>
        <td><%=rs.getDouble("totalamount")%></td>
        </tr>
        <%
            }
        %>
    </table>
    <h4>Product details</h4>
    <table border="1px" width="100%">
    <tr>
        <td>Product_Id</td>
        <td>Name</td>
        <td>Quantity</td>
        <td>Subtotal</td>
    </tr>
            <%
                PreparedStatement st=con.prepareStatement("SELECT * FROM ordered_items WHERE order_id=?");
                st.setInt(1,rs.getInt("order_id"));
                ResultSet result=st.executeQuery();
                while(result.next()) {
            %>
            <tr>
            <td><%=result.getInt("prod_id")%></td>
            <td><%=result.getString("name")%></td>
            <td><%=result.getInt("quantity")%></td>
            <td><%=result.getDouble("subtotal")%></td>
            </tr>
            <%
            }
            } catch (Exception e) {
                e.printStackTrace();
            }
            %>
        </table>
    </form>
     <button onclick="window.location.href='index.jsp'">Menu</button>
</body>
</html>