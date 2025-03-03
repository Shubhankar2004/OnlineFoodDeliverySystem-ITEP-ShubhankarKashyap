package com.itep.project.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

@Data
public class OrderDto {
	private Long id;
	private Long userId;
//	private LocalDateTime orderDate;
	@JsonFormat(pattern = "yyyy-MM-dd")
	private LocalDate orderDate;
	private BigDecimal totalAmount;
	private String orderStatus; 
	private String paymentStatus;
	private List<OrderItemDto> items;
	
}
