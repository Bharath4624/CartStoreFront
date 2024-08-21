<%@page language="java" contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<title>Shipping</title>
<script>
    function sendShippingMethod(event) {
        event.preventDefault();
        const selectedMethod = document.querySelector('input[name="shipping_method"]:checked');
        if (selectedMethod) {
            const shippingMethod = {
                method: selectedMethod.value,
            };
            fetch('cart/shipping', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(shippingMethod)
            })
            .then(response => response.json())
            .then(data => {
                console.log(data);
                goNext();
            })
            .catch(error => {
                console.error('Error:', error);
                alert('Please try again');
            });
        } else {
            alert('Please select a shipping method.');
        }
        function goNext(){
            window.location.href="payment.jsp";
        }
    }
</script>
</head>
<body>
    <form onsubmit="sendShippingMethod(event)">
        <table border="1px">
            <tr>
                <td>Shipping method</td>
                <td>Shipping charge</td>
            </tr>
            <tr>
                <td>
                    <input type="radio" name="shipping_method" value="Standard Shipping"/> Standard Shipping (3-5 days)
                </td>
                <td>200</td>
            </tr>
            <tr>
                <td>
                    <input type="radio" name="shipping_method" value="Fast Shipping"/> Fast Shipping (1-3 days)
                </td>
                <td>500</td>
            </tr>
            <tr>
                <td>
                    <input type="radio" name="shipping_method" value="International Shipping"/> International Shipping (10-15 days)
                </td>
                <td>1500</td>
            </tr>
        </table>
        <button type="submit">Go to payment</button>
    </form>
    <button onclick="window.location.href='customers.jsp'">Edit customer details</button><br>
    <button onclick="window.location.href='orderpreview.jsp'">Go to Cart</button><br>
</body>
</html>
