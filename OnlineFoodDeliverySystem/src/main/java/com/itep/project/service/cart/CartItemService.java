package com.itep.project.service.cart;

import org.springframework.stereotype.Service;

import com.itep.project.model.CartItem;

@Service
public interface CartItemService {
	
	void addItemToCart(Long cartId,Long foodItemId,int quantity);
	
	void removeItemFromCart(Long cartId,Long foodItemId);
	
	void updateItemQuantity(Long cartId,Long foodItemId,int quantity);
	
	CartItem getCartItem(Long cartId, Long fooItemId);
	
}
