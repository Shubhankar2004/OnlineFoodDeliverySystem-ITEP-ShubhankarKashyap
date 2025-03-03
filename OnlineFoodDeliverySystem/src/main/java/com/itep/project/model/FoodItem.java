package com.itep.project.model;

import java.math.BigDecimal;
import java.util.List;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class FoodItem {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	private String name;
	
	@Enumerated(EnumType.STRING)
	@JsonDeserialize(using = FoodTypeDeserializer.class)
	private FoodType foodType;
	
	private BigDecimal price;
	
	private String description;
	
	@ManyToOne
	@JoinColumn(name="category_id")
	private Category category;
	
//	@OneToMany(mappedBy = "foodItems",cascade = CascadeType.ALL,orphanRemoval = true)
//	private List<Image> images;
	
	@OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinColumn(name = "image_id")
	private Image image;

	public FoodItem(String name, FoodType foodType, BigDecimal price, String description, Category category) {
		super();
		this.name = name;
		this.foodType = foodType;
		this.price = price;
		this.description = description;
		this.category = category;
	}
	
}
