package com.order;

public class CartItems {
    public int prod_id;
    public String name;
    public int quantity;
    public double subtotal;
    public String cart_id;

    public int getProd_id() {
        return prod_id;
    }

    public void setProd_id(int prod_id) {
        this.prod_id = prod_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(double subtotal) {
        this.subtotal = subtotal;
    }

    public String getCart_id() {
        return cart_id;
    }

    public void setCart_id(String cart_id) {
        this.cart_id = cart_id;
    }

    public CartItems(int prod_id, String name, int quantity, double subtotal, String cart_id) {
        this.prod_id = prod_id;
        this.name = name;
        this.quantity = quantity;
        this.subtotal = subtotal;
        this.cart_id = cart_id;
    }
}
