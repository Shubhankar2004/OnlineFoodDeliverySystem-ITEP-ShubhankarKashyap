package com.itep.project.service.order;

import java.util.List;

import org.springframework.stereotype.Service;

import com.itep.project.dto.OrderDto;
import com.itep.project.model.Order;

@Service
public interface OrderService {
	
	Order placeOrder(Long userId);
	
	Order placeOrder(Long userId, String paymentMethod);
	
	OrderDto getOrder(Long orderId);
	
	List<OrderDto> getUserOrder(Long userId);

	List<Order> searchOrdersByUser(String field, String value);

	Order updateOrderStatus(Long orderId, String statusStr);

	List<OrderDto> getAllOrders();

	List<Order> searchOrdersByUserGeneric(String search);

	List<Order> getAllOrdersAsEntities();
	
	OrderDto convertToDto(Order order);
	
}
