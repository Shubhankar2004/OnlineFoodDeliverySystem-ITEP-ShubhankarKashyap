package com.itep.project.service.foodItem;

import java.util.List;

import org.springframework.stereotype.Service;

import com.itep.project.dto.FoodItemDto;
import com.itep.project.model.FoodItem;
import com.itep.project.model.FoodType;
import com.itep.project.request.AddFoodItemRequest;
import com.itep.project.request.UpdateFoodItemRequest;

@Service
public interface FoodItemService {
	
	FoodItem saveFoodItem(FoodItem foodItem);

	FoodItem addFoodItem(AddFoodItemRequest request);
	
	List<FoodItem> getAllFoodItems();
	
	FoodItem getFoodItemById(Long id);
	
	void deleteFoodItemById(Long id);
	
	FoodItem updateFoodItem(UpdateFoodItemRequest request,Long foodItemId);
	
	List<FoodItem> getFoodItemsByCategory(String category);
	
	List<FoodItem> getFoodItemsByFoodType(FoodType foodType);
	
	List<FoodItem> getFoodItemsByCategoryAndFoodType(String category,FoodType foodType);
	
	List<FoodItem> getFoodItemByName(String name);
	
	List<FoodItem> getFoodItemByFoodTypeAndName(FoodType foodType,String name);
	
	Long countFoodItems();
	
	Long countFoodItemsByCategory(String category);
	
	Long countFoodItemsByFoodType(FoodType foodType);

	FoodItemDto convertToDto(FoodItem foodItem);

	List<FoodItemDto> getConvertedFoodItems(List<FoodItem> foodItems);
	
	List<FoodItem> searchFoodItems(String name, Long categoryId, String foodType);
	
}
