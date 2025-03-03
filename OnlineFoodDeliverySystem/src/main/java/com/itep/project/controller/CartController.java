package com.itep.project.controller;

import java.math.BigDecimal;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.itep.project.model.Cart;
import com.itep.project.model.User;
import com.itep.project.response.ApiResponse;
import com.itep.project.service.cart.CartService;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Controller
@RequestMapping("/onlinefoodDelivery/carts")
public class CartController {
	
	private final CartService serv;
	
//	@GetMapping("/view")
//    public String viewCart(Model model, HttpSession session) {
//        User user = (User) session.getAttribute("user");
//        if (user == null) {
//            return "redirect:/onlinefoodDelivery/users/login";
//        }
//        Cart cart = serv.getCartByUserId(user.getId());
//        model.addAttribute("cart", cart);
//        return "cart"; // returns cart.html
//    }
	
	@GetMapping("/view")
    public String viewCart(Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/onlinefoodDelivery/users/login";
        }
        model.addAttribute("user", user);
        Cart cart = serv.getCartByUserId(user.getId());
        model.addAttribute("cart", cart);
        return "cart"; // returns cart.html
    }
	
	@GetMapping("/get/{cartId}")
	public String getCart(@PathVariable Long cartId, Model model, HttpSession session) {
		try {
			Cart cart = serv.getCart(cartId);
			User user = (User) session.getAttribute("user");
			if(user == null){
			    return "redirect:/onlinefoodDelivery/users/login";
			}
			model.addAttribute("user", user);
			model.addAttribute("cart", cart);
			return "cart";
		} catch(Exception e) {
			// Optionally log the error and/or redirect to an error page
			return "";
		}
	}

	
//	@GetMapping("/get/{cartId}")
//	public String getCart(@PathVariable Long cartId){
//		try {
//			Cart cart = serv.getCart(cartId);
////			return ResponseEntity.ok(new ApiResponse("Found !!!!", cart));
//			return "cart";
//		}catch(Exception e) {
////			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse(e.getMessage(),null));
//			return "";
//		}
//	}
	
	@DeleteMapping("/delete/{cartId}")
	public ResponseEntity<ApiResponse> clearCart(@PathVariable Long cartId){
		try {
			serv.clearCart(cartId);
			return ResponseEntity.ok(new ApiResponse("Cart cleared !!!!",null));
		}catch(Exception e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse(e.getMessage(),null));
		}
	}
	
	@GetMapping("/totalAmount/{cartId}")
	public ResponseEntity<ApiResponse> getTotalAmount(@PathVariable Long cartId){
		try {
			BigDecimal totalAmount = serv.getTotalPrice(cartId);
			return ResponseEntity.ok(new ApiResponse("Total Amount : ", totalAmount));
		}catch(Exception e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse(e.getMessage(),null));
		}
	}
	
}
