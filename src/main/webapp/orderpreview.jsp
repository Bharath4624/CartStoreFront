<%@page language="java" contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<title>Order Summary</title>
<script>
async function fetchOrderDetails() {
    try {
        const response = await fetch('cart/preview');
        const data = await response.json();
        document.getElementById('shipping-method').textContent = data.billing_details["shipping method"];
        document.getElementById('shipping-charge').textContent = data.billing_details["shipping_charge"];
        document.getElementById('payment-mode').textContent = data.billing_details["payment_mode"];
        document.getElementById('service-charge').textContent = data.billing_details["service_charge"];
        document.getElementById('subtotal').textContent = data.billing_details["subtotal"];
        document.getElementById('totaltax').textContent = data.billing_details["totaltax"];
        document.getElementById('total-amount').textContent = data.billing_details["totalamount"];
        const customer = data.customer;
        document.getElementById('customer-name').textContent = customer.name;
        document.getElementById('customer-address').textContent = customer.address;
        document.getElementById('customer-country').textContent = customer.country;
        document.getElementById('customer-mobile').textContent = customer.mobile;
        document.getElementById('customer-email').textContent = customer.email;
        const itemsTable = document.getElementById('items-table');
        data.items.forEach(item => {
            const row = document.createElement('tr');
            row.innerHTML = `
                <td>${item.name}</td>
                <td>${item.quantity}</td>
                <td>${item.subtotal}</td>
            `;
            itemsTable.appendChild(row);
        });
        if (data.billing_details["shipping_charge"] != 0 &&
            data.billing_details["service_charge"] != 0 &&
            data.customer.name) {
            document.getElementById('place-order-button').style.display = 'block';
        }

    } catch (error) {
        console.error('Error:', error);
    }
}
function placeOrder() {
    fetch('order', { method: 'POST' })
        .then(response => response.json())
        .then(data => {
            if (data.status === "success") {
                window.location.href = "order.jsp";
            } else {
                console.error('Error:', 'Order placement failed');
            }
        })
        .catch(error => console.error('Error:', error));
}

window.onload = fetchOrderDetails;
</script>
</head>
<body>
    <h4>Billing details</h4>
    <table border="1px" width="100%">
        <tr>
            <td>Shipping_method</td>
            <td id="shipping-method"></td>
        </tr>
        <tr>
            <td>Shipping_charge</td>
            <td id="shipping-charge"></td>
        </tr>
        <tr>
            <td>Payment_mode</td>
            <td id="payment-mode"></td>
        </tr>
        <tr>
            <td>Service_charge</td>
            <td id="service-charge"></td>
        </tr>
        <tr>
            <td>Subtotal</td>
            <td id="subtotal"></td>
        </tr>
        <tr>
            <td>Total_tax</td>
            <td id="totaltax"></td>
        </tr>
        <tr>
            <td>Total_Amount</td>
            <td id="total-amount"></td>
        </tr>
    </table>
    <a href='customers.jsp'>Edit</a><br>
    <h4>Product details</h4>
    <table border="1px" width="100%" id="items-table">
        <tr>
            <td>Name</td>
            <td>Quantity</td>
            <td>Subtotal</td>
        </tr>
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
        <tr>
            <td id="customer-name"></td>
            <td id="customer-address"></td>
            <td id="customer-country"></td>
            <td id="customer-mobile"></td>
            <td id="customer-email"></td>
        </tr>
    </table>
    <a href='customers.jsp'>Edit</a><br>
    <button id="place-order-button" style="display: none;" onclick="placeOrder()">Place order</button><br>
    <a href='payment.jsp'>Go to payment</a><br>
    <a href='index.jsp'>Menu</a>
</body>
</html>
