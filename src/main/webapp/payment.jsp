<%@page language="java" contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<title>Payment</title>
<script>
    function paymentMethod(event) {
        event.preventDefault();
        const selectedMethod = document.querySelector('input[name="payment_mode"]:checked');
        if (selectedMethod) {
            const paymentMode = {
                method: selectedMethod.value,
            };
            fetch('cart/payment', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(paymentMode)
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
            alert('Please select a payment mode.');
        }
        function goNext(){
           window.location.href="orderpreview.jsp";
        }
    }
</script>
</head>
<body>
    <form onsubmit="paymentMethod(event)">
        <table border="1px">
            <tr>
                <td>Payment mode</td>
                <td>Service charge</td>
            </tr>
            <tr>
                <td>
                    <input type="radio" name="payment_mode" value="Online"/> Online payment
                </td>
                <td>50</td>
            </tr>
            <tr>
                <td>
                    <input type="radio" name="payment_mode" value="Offline"/> Offline payment
                </td>
                <td>100</td>
            </tr>
        </table>
        <button type="submit">Next</button>
    </form>
    <button onclick="window.location.href='shipping.jsp'">Edit shipping method</button>
</body>
</html>