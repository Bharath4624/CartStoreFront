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
<title>Cart</title>
    <script>
    async function addtocart(button) {
        const buttonvalue ={
        prod_id: button.value
        };
        try {
            const response = await fetch('cart',{
                method: 'PUT',
                headers: {
                'Content-Type': 'application/json'
                },
                body: JSON.stringify(buttonvalue)
            });
            const data = await response.json();
            if (data.status==="success") {
                showPopup("Item added to cart");
            }
            else {
                console.error('Error:',data.message);
                showPopup("Failed to add item to cart",true);
            }
        }
        catch (error) {
            console.error('Error:',error);
            showPopup("Error occurred. Please try again",true);
        }
    }
    function showPopup(message,isError=false) {
        const popup = document.createElement("div");
        popup.className = isError?"popup error":"popup success";
        popup.innerText = message;
        document.body.appendChild(popup);
        setTimeout(()=>{
            popup.style.opacity = 0;
            setTimeout(()=>document.body.removeChild(popup),500);
        },200);
    }
    </script>
    <style>
        .popup {
            position: fixed;
            top: 20px;
            right: 20px;
            background-color: #4CAF50;
            color: white;
            padding: 15px;
            border-radius: 5px;
            opacity: 1;
            transition: opacity 0.5s ease;
            z-index: 1000;
        }
        .popup.error {
            background-color: #f44336;
        }
    </style>
</head>
<body>
    <h3>Products</h3>
    <table border="1px" width="100%">
        <tr>
            <td>Name</td>
            <td>Price</td>
            <td>Cart</td>
        </tr>
        <%
        try {
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/order","root","bharath123@#");
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM products");
            while (rs.next()) {
        %>
        <tr>
            <td><%=rs.getString("name")%></td>
            <td><%=rs.getDouble("price")%></td>
            <td><button name="button" value="<%=rs.getString("prod_id")%>" onclick="addtocart(this)">Add to cart</button></td>
        </tr>
        <%
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        %>
    </table>
    <button onclick="window.location.href='orderpreview.jsp'">Go to Cart</button><br>
    <button onclick="window.location.href='viewcart.jsp'">Edit Cart Items</button><br>
    <button onclick="window.location.href='index.jsp'">Menu</button>
</body>
</html>