package com.itep.project.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.itep.project.model.Cart;
import com.itep.project.model.CartItem;
import com.itep.project.model.User;
import com.itep.project.response.ApiResponse;
import com.itep.project.service.cart.CartItemService;
import com.itep.project.service.cart.CartService;
import com.itep.project.service.user.UserService;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Controller
@RequestMapping("/onlinefoodDelivery/cartItems")
public class CartItemController {
	
	private final CartItemService serv;
	
	private final CartService cartServ;
	
	private final UserService userServ;
	
	@PostMapping("/add/Item")
	public ResponseEntity<ApiResponse> addItemToCart(@RequestParam Long userId,@RequestParam Long foodItemId,@RequestParam Integer quantity,HttpSession session){
		try {
//			if(cartId==null) {
//				cartId=cartServ.initializeNewCart();
//			}
			User user = userServ.getUserById(userId);
			//Cart cart = cartServ.initializeNewCart(user);
			Cart cart = cartServ.getCartByUserId(userId);
			serv.addItemToCart(cart.getId(),foodItemId,quantity);
			
			User updatedUser = userServ.getUserById(userId);

	        // 4. Store the updated user (and cart) in session
	        session.setAttribute("user", updatedUser);
			
			return ResponseEntity.ok(new ApiResponse("Added Successfully !!!!",null));
		}catch (Exception e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse(e.getMessage(),null));
		}
	}
	
	@GetMapping("/get/cartId/{cartId}/foodItemId/{foodItemId}")
	public ResponseEntity<ApiResponse> getItem(@PathVariable Long cartId,@PathVariable Long foodItemId){
		try {
			CartItem cartItem = serv.getCartItem(cartId,foodItemId);
			return ResponseEntity.ok(new ApiResponse("Found !!!!",cartItem));
		}catch (Exception e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse(e.getMessage(),null));
		}
	}
	
	@DeleteMapping("removeItem/cartId/{cartId}/foodItemId/{foodItemId}")
	public ResponseEntity<ApiResponse> removeItemFromCart(@PathVariable Long cartId,@PathVariable Long foodItemId,HttpSession session){
		try {
			serv.removeItemFromCart(cartId, foodItemId);
			Cart cart = cartServ.getCart(cartId);
			User updatedUser = userServ.getUserById(cart.getUser().getId());
	        session.setAttribute("user", updatedUser);
			return ResponseEntity.ok(new ApiResponse("Removed Successfully !!!!",null));
		}catch (Exception e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse(e.getMessage(),null));
		}
	}
	
	@PutMapping("/updateItem/cartId/{cartId}/foodItemId/{foodItemId}")
	public ResponseEntity<ApiResponse> updateItem(@PathVariable Long cartId,@PathVariable Long foodItemId,@RequestParam int quantity,HttpSession session){
		try {
			serv.updateItemQuantity(cartId, foodItemId, quantity);
			Cart cart = cartServ.getCart(cartId);
	        User updatedUser = userServ.getUserById(cart.getUser().getId());
	        session.setAttribute("user", updatedUser);
			return ResponseEntity.ok(new ApiResponse("Updated Successfully !!!!",null));
		}catch (Exception e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse(e.getMessage(),null));
		}
	}
	
}
