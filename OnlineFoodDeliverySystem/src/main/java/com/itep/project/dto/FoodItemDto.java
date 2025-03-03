package com.itep.project.dto;

import java.math.BigDecimal;
import java.util.List;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.itep.project.model.Category;
import com.itep.project.model.FoodType;
import com.itep.project.model.FoodTypeDeserializer;
import com.itep.project.model.Image;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;

@Data
public class FoodItemDto {
	private Long id;
	private String name;
	@Enumerated(EnumType.STRING)
	@JsonDeserialize(using = FoodTypeDeserializer.class)
	private FoodType foodType;
	private BigDecimal price;
	private String description;
	private Category category;
//	private List<ImageDto> images;
	private ImageDto image;
}
