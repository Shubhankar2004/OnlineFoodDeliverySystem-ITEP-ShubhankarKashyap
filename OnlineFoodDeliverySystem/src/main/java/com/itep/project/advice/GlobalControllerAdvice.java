package com.itep.project.advice;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.ui.Model;

import com.itep.project.model.Cart;
import com.itep.project.model.User;
import com.itep.project.service.cart.CartService;

@ControllerAdvice
public class GlobalControllerAdvice {

    @Autowired
    private CartService cartService;
    
    @ModelAttribute
    public void addCartToModel(Model model, HttpSession session) {
         User user = (User) session.getAttribute("user");
         if(user != null) {
             // If the user does not have a cart, initialize one
             Cart cart = user.getCart();
             if(cart == null) {
                 cart = cartService.initializeNewCart(user);
                 user.setCart(cart);
                 session.setAttribute("user", user); // Update session with cart info
             }
             model.addAttribute("cart", cart);
         }
    }
}
