package com.itep.project.request;

import java.math.BigDecimal;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.itep.project.model.Category;
import com.itep.project.model.FoodType;
import com.itep.project.model.FoodTypeDeserializer;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;

@Data
public class AddFoodItemRequest {
	private Long id;
	private String name;
	@Enumerated(EnumType.STRING)
	@JsonDeserialize(using = FoodTypeDeserializer.class)
	private FoodType foodType;
	private BigDecimal price;
	private String description;
	private Category category;
}
