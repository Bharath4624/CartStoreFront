<%@page language="java" contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="java.sql.*"%>
<%@page import="java.util.*"%>
<%@page import="jakarta.servlet.http.*"%>
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
<title>Order Summary</title>
<script>
async function addToOrder() {
    try {
        const response = await fetch('order', {
         method: 'POST'
         });
        getDetails();
        const cartData = await response.json();
        if (cartData.status === "success") {
            console.log(cartData);
        } else {
            console.error('Error:', 'failed');
        }
    } catch (error) {
        console.error('Error:', error);
    }
}
async function getDetails() {
    try {
        const response = await fetch('cart/preview', {
         method: 'GET'
         });
        goNext();
        const cartData = await response.json();
        console.log(cartData);
        console.error('Error:', 'failed');
    } catch (error) {
        console.error('Error:', error);
    }
}
function goNext(){
 window.location.href="order.jsp";
}
</script>
</head>
<body>
    <h4>Billing details</h4>
    <form action="order" method="post">
    <table border="1px" width="100%">
        <%
        try {
            String cart_id=null;
            Cookie[] cookies=request.getCookies();
            if(cookies!=null){
                for(Cookie i:cookies){
                    if("cart_id".equals(i.getName())){
                        cart_id=i.getValue();
                        break;
                    }
                }
            }
            if(cart_id!=null){
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/order","root","bharath123@#");
            PreparedStatement stmt = con.prepareStatement("SELECT * FROM cart WHERE cart_id=?");
            stmt.setString(1,cart_id);
            ResultSet rs = stmt.executeQuery();
            if(rs.next()) {
        %>
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
        <td>Subtotal</td>
        <td><%=rs.getDouble("subtotal")%></td>
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
    <a href='customers.jsp'>Edit</a><br>
    <h4>Product details</h4>
    <table border="1px" width="100%">
    <tr>
        <td>Name</td>
        <td>Quantity</td>
        <td>Subtotal</td>
    </tr>
            <%
                PreparedStatement st=con.prepareStatement("SELECT * FROM cart_items WHERE cart_id=?");
                st.setString(1,cart_id);
                ResultSet result=st.executeQuery();
                while(result.next()) {
            %>
            <tr>
            <td><%=result.getString("name")%></td>
            <td><%=result.getInt("quantity")%></td>
            <td><%=result.getDouble("subtotal")%></td>
            </tr>
            <%
            }
            %>
        </table>
        <a href='viewcart.jsp'>Edit</a>
        <h4>Customer details</h4>
        <table border="1px" width="100%">
        <tr>
           <th>Customer_Name</th>
           <th>Address</th>
           <th>Country</th>
           <th>Mobile</th>
           <th>Email</th>
        </tr>
        <%
           st=con.prepareStatement("SELECT * FROM customers WHERE cus_id=?");
           st.setInt(1,rs.getInt("cus_id"));
           result=st.executeQuery();
           if(result.next()) {
        %>
        <tr>
           <td><%= result.getString("name") %></td>
           <td><%= result.getString("address") %>,<%= result.getString("city") %>-<%= result.getString("zipcode") %></td>
           <td><%= result.getString("country") %></td>
           <td><%= result.getString("mobile") %></td>
           <td><%= result.getString("email") %></td>
        </tr>
        </table>
        <a href='customers.jsp'>Edit</a><br>
        <%
        }
        }
        } catch (Exception e) {
            e.printStackTrace();
        }
        %>
    </form>
    <%
    try {
                  String cart_id=null;
                  Cookie[] cookies=request.getCookies();
                  if(cookies!=null){
                      for(Cookie i:cookies){
                          if("cart_id".equals(i.getName())){
                              cart_id=i.getValue();
                              break;
                          }
                      }
                  }
                  if(cart_id!=null){
                  con = DriverManager.getConnection("jdbc:mysql://localhost:3306/order","root","bharath123@#");
                  PreparedStatement stmt = con.prepareStatement("SELECT * FROM cart WHERE cart_id=?");
                  stmt.setString(1,cart_id);
                  ResultSet rs = stmt.executeQuery();
                  if(rs.next()) {
                  if(rs.getDouble("shipping_charge")!=0 && rs.getDouble("service_charge")!=0 && rs.getInt("cus_id")!=0 && rs.getDouble("totalamount")!=0){
    %>
     <button onclick="addToOrder()">Place order</button><br>
     <a href='payment.jsp'>Go to payment</a><br>
     <%
               }
               }
               }
               } catch (Exception e) {
                   e.printStackTrace();
               }
    %>
     <a href='index.jsp'>Menu</a>
</body>
</html>