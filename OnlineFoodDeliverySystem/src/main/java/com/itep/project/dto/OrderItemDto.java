package com.itep.project.dto;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class OrderItemDto {
	private Long foodItemId;
	private String foodItemName;
	private int quantity;
	private BigDecimal price;
}
