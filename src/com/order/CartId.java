package com.order;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;

public class CartId extends HttpServlet {
    public static String getCartId(HttpServletRequest req) {
        String cart_id = null;
        Cookie[] cookies = req.getCookies();
        if (cookies != null) {
            for (Cookie i : cookies) {
                if ("cart_id".equals(i.getName())) {
                    cart_id = i.getValue();
                    break;
                }
            }
        }
        return cart_id;
    }
}
