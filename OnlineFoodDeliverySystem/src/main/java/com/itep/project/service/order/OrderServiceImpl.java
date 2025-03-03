package com.itep.project.service.order;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.itep.project.dto.OrderDto;
import com.itep.project.enums.OrderStatus;
import com.itep.project.enums.PaymentStatus;
import com.itep.project.model.Cart;
import com.itep.project.model.FoodItem;
import com.itep.project.model.Order;
import com.itep.project.model.OrderItem;
import com.itep.project.repository.OrderRepository;
import com.itep.project.service.cart.CartService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderServiceImpl implements OrderService{
	
	private final OrderRepository repo;
	
	private final CartService cartServ;
	
	private final ModelMapper modelMapper;
	
//	@Transactional
//	@Override
//	public Order placeOrder(Long userId) {
//		Cart cart = cartServ.getCartByUserId(userId);
//		Order order = createOrder(cart);
//		List<OrderItem> orderItems = createOrderItems(order, cart);
//		order.setOrderItems(new HashSet<>(orderItems));
//		order.setTotalAmount(calculateTotalAmount(orderItems));
//		Order savedOrder = repo.save(order);
//		cartServ.clearCart(cart.getId());
//		return savedOrder;
//	}
	
	@Transactional
	@Override
	public Order placeOrder(Long userId) {
	    // 1. Find the user's cart
	    Cart cart = cartServ.getCartByUserId(userId);

	    // 2. Build the order from the cart
	    Order order = createOrder(cart);
	    List<OrderItem> orderItems = createOrderItems(order, cart);
	    order.setOrderItems(new HashSet<>(orderItems));
	    order.setTotalAmount(calculateTotalAmount(orderItems));

	    // 3. Save the order
	    Order savedOrder = repo.save(order);

	    // 4. Clear the cart from DB
	    cartServ.clearCart(cart.getId());

	    return savedOrder;
	}
	
	@Transactional
    @Override
    public Order placeOrder(Long userId, String paymentMethod) {
        // 1. Find the user's cart
        Cart cart = cartServ.getCartByUserId(userId);

        // 2. Build the order from the cart
        Order order = createOrder(cart);

        // 3. Set the paymentStatus based on paymentMethod
        if (paymentMethod.equalsIgnoreCase("CASH")) {
            order.setPaymentStatus(PaymentStatus.CASH);
        } else if (paymentMethod.equalsIgnoreCase("ONLINE")) {
            order.setPaymentStatus(PaymentStatus.ONLINE);
        } else {
            order.setPaymentStatus(PaymentStatus.PENDING);
        }

        // 4. Create order items and calculate total
        List<OrderItem> orderItems = createOrderItems(order, cart);
        order.setOrderItems(new HashSet<>(orderItems));
        order.setTotalAmount(calculateTotalAmount(orderItems));

        // 5. Save the order and clear the cart
        Order savedOrder = repo.save(order);
        cartServ.clearCart(cart.getId());

        return savedOrder;
    }
	
	private Order createOrder(Cart cart) {
		Order order = new Order();
		order.setUser(cart.getUser());
		order.setOrderStatus(OrderStatus.PENDING);
		order.setOrderDate(LocalDate.now());
		return order;
	}
	
	private List<OrderItem> createOrderItems(Order order,Cart cart){
		return cart.getItems().stream().map(cartItem->{
			FoodItem foodItem = cartItem.getFoodItem();
			int quantity = cartItem.getQuantity();
			BigDecimal price = cartItem.getUnitPrice();
			return new OrderItem(
					order,
					foodItem,
					quantity,
					price
					);
		}).toList();
	}
	
	private BigDecimal calculateTotalAmount(List<OrderItem> orderItems) {
		return orderItems
				.stream()
				.map(item->item.getPrice()
						.multiply(new BigDecimal(item.getQuantity())))
				.reduce(BigDecimal.ZERO,BigDecimal::add);
	}

	@Override
	public OrderDto getOrder(Long orderId) {
		return repo.findById(orderId).map(this::convertToDto).orElseThrow(()-> new RuntimeException("Order with ID : "+orderId+" not found !!!!"));
	}
	
	@Override
	public List<OrderDto> getUserOrder(Long userId){
		List<Order> orders = repo.findByUserId(userId);
		return orders.stream().map(this::convertToDto).toList();
	}
	
	@Override
	public List<OrderDto> getAllOrders(){
		List<Order> orders = repo.findAll();
		return orders.stream().map(this::convertToDto).toList();
	}
	
	@Override
	public OrderDto convertToDto(Order order) {
		return modelMapper.map(order,OrderDto.class);
	}
	
	@Override
	public List<Order> getAllOrdersAsEntities() {
        return repo.findAll();
    }

    // Example: if search is numeric => treat as userId or mobile
    // If search has '@' => treat as email
    // else treat as firstName
    @Override
    public List<Order> searchOrdersByUserGeneric(String search) {
        // A simplistic approach:
        if (search.contains("@")) {
            // find by user email
            return repo.findByUserEmail(search);
        }
        // check if numeric => userId or mobile
        if (search.matches("\\d+")) {
            // Could interpret as userId or mobile. Let's do userId:
            long userId = Long.parseLong(search);
            return repo.findByUserId(userId);
        }
        // else treat as firstName
        return repo.findByUserFirstNameIgnoreCase(search);
    }
	
	@Override
	public List<Order> searchOrdersByUser(String field, String value) {
        // Example: if field="email", find orders where user.email=?
        // You might do a JPA query or custom logic. For instance:
        switch(field.toLowerCase()) {
            case "email":
                return repo.findByUserEmail(value);
            case "mobile":
                return repo.findByUserMobile(Long.valueOf(value));
            case "firstname":
                return repo.findByUserFirstNameIgnoreCase(value);
            case "id":
                Long userId = Long.valueOf(value);
                return repo.findByUserId(userId);
            default:
                throw new RuntimeException("Invalid search field");
        }
    }

	@Override
    public Order updateOrderStatus(Long orderId, String statusStr) {
        Order order = repo.findById(orderId)
            .orElseThrow(() -> new RuntimeException("Order not found!"));
        OrderStatus newStatus = OrderStatus.valueOf(statusStr.toUpperCase());
        order.setOrderStatus(newStatus);
        return repo.save(order);
    }

}
