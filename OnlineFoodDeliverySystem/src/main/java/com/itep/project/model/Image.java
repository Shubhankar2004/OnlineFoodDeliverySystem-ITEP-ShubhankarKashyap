package com.itep.project.model;

import java.sql.Blob;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
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
public class Image {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String fileName;
	private String fileType;
	
	@Lob
	private Blob image;
	private String downloadUrl;
	
//	@ManyToOne
//	@JoinColumn(name="foodItem_id")
//	private FoodItem foodItems;
	
//	@OneToOne
//    @JoinColumn(name = "food_item_id")
//    private FoodItem foodItem;
	
	@OneToOne(mappedBy = "image")  // mappedBy points to the 'image' property in FoodItem
	private FoodItem foodItem;
	
}
