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
<title>Customer</title>
<script>
        function sendCustomer(event) {
            event.preventDefault();
            const customerData = {
                name: document.querySelector('input[name="name"]').value,
                address: document.querySelector('input[name="address"]').value,
                city: document.querySelector('input[name="city"]').value,
                zipcode: document.querySelector('input[name="zipcode"]').value,
                country: document.querySelector('input[name="country"]').value,
                mobile: document.querySelector('input[name="mobile"]').value,
                email: document.querySelector('input[name="email"]').value
            };
            const empty=Object.values(customerData).some(value=>value==='');
            if(empty){
            alert("Please fill all the customer details");
            return;
            }
            const formData = {
                customer: customerData,
            };
            fetch('cart/customer', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(formData)
            })
            .then(response => response.json()).then(data => {
                console.log(data);
                goNext();
            })
            .catch(error => {
                console.error('Error:',error);
                alert('Please try again');
            });
        }
        function goNext(){
        window.location.href="shipping.jsp";
        }
    </script>
</head>
<body>
    <form onsubmit="sendCustomer(event)">
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
                con = DriverManager.getConnection("jdbc:mysql://localhost:3306/order","root","bharath123@#");
                PreparedStatement stmt = con.prepareStatement("SELECT cus_id FROM cart WHERE cart_id=?");
                stmt.setString(1,cart_id);
                ResultSet rs = stmt.executeQuery();
                if(rs.next()) {
                int cus_id=rs.getInt("cus_id");
                stmt=con.prepareStatement("SELECT * FROM customers WHERE cus_id=?");
                stmt.setInt(1,cus_id);
                ResultSet result=stmt.executeQuery();
                if(result.next()){
    %>
        Name: <br><input type="text" name="name" value="<%=result.getString("name")%>"/><br>
        Address: <br><input type="text" name="address" value="<%=result.getString("address")%>"/><br>
        City: <br><input type="text" name="city" value="<%=result.getString("city")%>"/><br>
        Zipcode: <br><input type="text" name="zipcode" value="<%=result.getString("zipcode")%>"/><br>
        Country: <br><input type="text" name="country" value="<%=result.getString("country")%>"/><br>
        Mobile: <br><input type="text" name="mobile" value="<%=result.getString("mobile")%>"/><br>
        Email: <br><input type="text" name="email" value="<%=result.getString("email")%>"/><br>
        <%
        }
        else{
        %>
                Name: <br><input type="text" name="name"/><br>
                Address: <br><input type="text" name="address"/><br>
                City: <br><input type="text" name="city"/><br>
                Zipcode: <br><input type="text" name="zipcode"/><br>
                Country: <br><input type="text" name="country"/><br>
                Mobile: <br><input type="text" name="mobile"/><br>
                Email: <br><input type="text" name="email"/><br>
        <%
            }
                }
                } catch (Exception e) {
                    e.printStackTrace();
                }
        %>
        <br>
        <input type="submit" name="Submit" value="Continue"/>
    </form>
    <button onclick="window.location.href='viewcart.jsp'">Edit cart items</button><br>
    <button onclick="window.location.href='orderpreview.jsp'">Go to Cart</button><br>
</body>
</html>