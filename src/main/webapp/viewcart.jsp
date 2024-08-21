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
<title>Edit</title>
<script>
async function add(button) {
    const cookie=getCookies("cart_id");
    const buttonvalue = {
        id: button.name,
        action: button.value,
        cart_id: cookie
    };
    try {
        const response = await fetch('cart/edit', {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(buttonvalue)
        });
        const data = await response.json();
        if (data.status === "success") {
            console.log(data.status);
            updateCart();
        } else {
            console.error('Error:', data.message);
        }
    } catch (error) {
        console.error('Error:', error);
    }
}
async function reduce(button) {
    const cookie=getCookies("cart_id");
    const buttonvalue = {
        id: button.name,
        action: button.value,
        cart_id: cookie
    };
    try {
        const response = await fetch('cart/edit', {
            method: 'DELETE',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(buttonvalue)
        });
        const data = await response.json();
        if (data.status === "success") {
            console.log(data.status);
            updateCart();
        } else {
            console.error('Error:', data.message);
        }
    } catch (error) {
        console.error('Error:', error);
    }
}
async function updateCart() {
    try {
        const response = await fetch('cart/items', {
         method: 'GET'
         });
        const cartData = await response.json();
        if (cartData.status === "success") {
            addToUrl(cartData.cart_id);
            const cartTable = document.querySelector('table tbody');
            const rows = cartData.items.map(item => `
                <tr>
                    <td>${item.name}</td>
                    <td>${item.quantity}</td>
                    <td>${item.subtotal}</td>
                    <td>
                        <button name="${item.id}" value="add" onclick="add(this)">+</button>
                        <button name="${item.id}" value="delete" onclick="reduce(this)">-</button>
                    </td>
                </tr>
            `).join('');
            cartTable.innerHTML = rows;
        } else {
            console.error('Error:', cartData.message);
        }
    } catch (error) {
        console.error('Error:', error);
    }
}
function addToUrl(data){
        let inputUrl = new URL(window.location.href);
        let inputParams = new URLSearchParams(inputUrl.search);
        inputParams.set('cart_id', data);
        window.history.replaceState(null,'',inputUrl.pathname+'?'+inputParams.toString());
}
function getCookies(Name){
     let cookies=document.cookie;
     let cookieArr=cookies.split("; ");
     for(let i=0;i<cookieArr.length;i++){
          let cookie=cookieArr[i];
          let [name, value]=cookie.split("=");
          if(name===Name){
            return decodeURIComponent(value);
          }
     }
     return null;
}
</script>
</head>
<body>
<%
try {
    con = DriverManager.getConnection("jdbc:mysql://localhost:3306/order","root","bharath123@#");
    PreparedStatement stmt = con.prepareStatement("SELECT * FROM cart_items WHERE cart_id=?");
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
    if(cart_id==null){
%>
    <h3>No items available</h3>
<%
    } else {
        stmt.setString(1,cart_id);
        ResultSet rs=stmt.executeQuery();
%>
    <h3>Products</h3>
    <table border="1px" width="100%">
        <thead>
            <tr>
                <th>Name</th>
                <th>Quantity</th>
                <th>Subtotal</th>
                <th>Edit</th>
            </tr>
        </thead>
        <tbody>
        <%
        while (rs.next()) {
        %>
            <tr>
                <td><%=rs.getString("name")%></td>
                <td><%=rs.getInt("quantity")%></td>
                <td><%=rs.getDouble("subtotal")%></td>
                <td>
                    <button name="<%=rs.getInt("prod_id")%>" value="add" onclick="add(this)">+</button>
                    <button name="<%=rs.getInt("prod_id")%>" value="delete" onclick="reduce(this)">-</button>
                </td>
            </tr>
        <%
        }
        }
    } catch (Exception e) {
        e.printStackTrace();
    }
    %>
        </tbody>
    </table>
    <button onclick="window.location.href='products.jsp'">Add items</button>
    <%
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
    PreparedStatement checkcart=con.prepareStatement("SELECT * FROM cart_items WHERE cart_id=?");
    checkcart.setString(1,cart_id);
    ResultSet cart=checkcart.executeQuery();
    if(cart.next()){
    %>
    <button onclick="window.location.href='customers.jsp'">Continue</button>
    <%
    } else {
    %>
    <h5>Please add products in the cart to continue</h5>
    <%
    }
    %>
    <button onclick="window.location.href='index.jsp'">Menu</button><br>
    <button onclick="window.location.href='orderpreview.jsp'">Go to Cart</button><br>
</body>
</html>