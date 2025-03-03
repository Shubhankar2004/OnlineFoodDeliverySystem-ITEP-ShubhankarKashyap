package com.itep.project.service.cart;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;

import com.itep.project.model.Cart;
import com.itep.project.model.CartItem;
import com.itep.project.model.FoodItem;
import com.itep.project.repository.CartItemRepository;
import com.itep.project.repository.CartRepository;
import com.itep.project.service.foodItem.FoodItemService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CartItemServiceImpl implements CartItemService{
	
	private final CartItemRepository repo;
	
	private final CartRepository cartRepo;
	
	private final FoodItemService foodItemServ;
	
	private final CartService cartServ;

	
	@Override
	@Transactional
	public void addItemToCart(Long cartId, Long foodItemId, int quantity) {
		//1. Get the cart
        //2. Get the foodItem
        //3. Check if the foodItem already in the cart
        //4. If Yes, then increase the quantity with the requested quantity
        //5. If No, then initiate a new CartItem entry.
		try {
			Cart cart = cartServ.getCart(cartId);
			if(cart==null) {
				throw new RuntimeException("Cart with ID : "+cartId+" not found !!!!");
			}
			FoodItem foodItem = foodItemServ.getFoodItemById(foodItemId);
			if(foodItem==null) {
				throw new RuntimeException("FoodItem with ID : "+foodItemId+" not found !!!!");
			}
			CartItem cartItem = cart.getItems()
					.stream()
					.filter(item->item.getFoodItem().getId().equals(foodItem.getId()))
					.findFirst()
					.orElse(new CartItem());
			
			if(cartItem.getId()==null) {
				cartItem.setCart(cart);
				cartItem.setFoodItem(foodItem);
				cartItem.setQuantity(quantity);
				cartItem.setUnitPrice(foodItem.getPrice());
			}else {
				cartItem.setQuantity(cartItem.getQuantity()+quantity);
			}
			cartItem.setTotalPrice();
			cart.addItem(cartItem);
			repo.save(cartItem);
			cartRepo.save(cart);
		}catch (Exception e) {
			throw new RuntimeException(e.getMessage());
		}
	}

	@Override
	public void removeItemFromCart(Long cartId, Long foodItemId) {
		Cart cart = cartServ.getCart(cartId);
		CartItem cartItem = cart.getItems()
				.stream()
				.filter(item->item.getFoodItem().getId().equals(foodItemId))
				.findFirst()
				.orElseThrow(()->new RuntimeException("FoodItem with ID : "+foodItemId+" not found to delete !!!!"));
		cart.removeItem(cartItem);
		cartRepo.save(cart);
	}

	@Override
	public void updateItemQuantity(Long cartId, Long foodItemId, int quantity) {
		Cart cart = cartServ.getCart(cartId);
        cart.getItems()
                .stream()
                .filter(item -> item.getFoodItem().getId().equals(foodItemId))
                .findFirst()
                .ifPresent(item -> {
                    item.setQuantity(quantity);
                    item.setUnitPrice(item.getFoodItem().getPrice());
                    item.setTotalPrice();
                });
        BigDecimal totalAmount = cart.getItems()
                .stream().map(CartItem ::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        cart.setTotalAmount(totalAmount);
        cartRepo.save(cart);
	}
	
	 @Override
	 public CartItem getCartItem(Long cartId, Long fooItemId) {
	        Cart cart = cartServ.getCart(cartId);
	        return  cart.getItems()
	                .stream()
	                .filter(item -> item.getFoodItem().getId().equals(fooItemId))
	                .findFirst().orElseThrow(() ->new RuntimeException("Item not found"));
	 }

}
