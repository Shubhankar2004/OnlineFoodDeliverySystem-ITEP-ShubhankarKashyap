package com.itep.project.service.cart;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;

import com.itep.project.model.Cart;
import com.itep.project.model.User;

@Service
public interface CartService {
	
	Cart getCart(Long id);
	
	void clearCart(Long id);
	
	BigDecimal getTotalPrice(Long id);

	Cart initializeNewCart(User user);

	Cart getCartByUserId(Long userId);

	
}
