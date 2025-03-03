package com.itep.project.service.cart;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.stereotype.Service;

import com.itep.project.model.Cart;
import com.itep.project.model.User;
import com.itep.project.repository.CartItemRepository;
import com.itep.project.repository.CartRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService{
	
	private final CartRepository repo;
	
	private final CartItemRepository CartItemrepo;
	
	//private final AtomicLong cartIdGenerator = new AtomicLong(0);

	@Override
	public Cart getCart(Long id) {
		Cart cart = repo.findById(id).orElseThrow(()->new RuntimeException("Cart with ID : "+id+" not found !!!!"));
		BigDecimal totalAmount = cart.getTotalAmount();
		cart.setTotalAmount(totalAmount);
		return repo.save(cart);
	}

	@Transactional
	@Override
	public void clearCart(Long id) {
		Cart cart = getCart(id);	
		CartItemrepo.deleteAllByCartId(id);
		cart.getItems().clear();
		repo.deleteById(id);
	}

	@Override
	public BigDecimal getTotalPrice(Long id) {
		Cart cart = getCart(id);
		return cart.getTotalAmount();
	}
	
	@Override
	public Cart initializeNewCart(User user) {
//		Cart newCart = new Cart();
//		Long newCartId = cartIdGenerator.incrementAndGet();
//		newCart.setId(newCartId);
//		return repo.save(newCart).getId();
		return Optional.ofNullable(getCartByUserId(user.getId()))
				.orElseGet(()->{
					Cart cart = new Cart();
					cart.setUser(user);
					return repo.save(cart);
				});
	}

	@Override
	public Cart getCartByUserId(Long userId) {
		return repo.findByUserId(userId);
	}

}
