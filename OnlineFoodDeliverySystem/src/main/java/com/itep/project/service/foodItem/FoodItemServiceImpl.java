package com.itep.project.service.foodItem;

import java.util.List;
import java.util.Optional;

import javax.management.RuntimeErrorException;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.itep.project.dto.FoodItemDto;
import com.itep.project.dto.ImageDto;
import com.itep.project.model.Category;
import com.itep.project.model.FoodItem;
import com.itep.project.model.FoodType;
import com.itep.project.model.Image;
import com.itep.project.repository.CategoryRepository;
import com.itep.project.repository.FoodItemRepository;
import com.itep.project.repository.ImageRepository;
import com.itep.project.request.AddFoodItemRequest;
import com.itep.project.request.UpdateFoodItemRequest;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FoodItemServiceImpl implements FoodItemService{

	@Autowired
	private FoodItemRepository repo;
	
	@Autowired
	private CategoryRepository catrepo;
	
	private final ImageRepository imageRepo;
	
	private final ModelMapper modelMapper;
	
	@Override
	public FoodItem saveFoodItem(FoodItem foodItem) {
	    return repo.save(foodItem);
	}
	
	@Override
	public FoodItem addFoodItem(AddFoodItemRequest request) {
		// check if category is found in db
		// if yes set it as new fooditem category
		//if no then save it as a new category
		//then set it as new food item category
		Category category = Optional.ofNullable(catrepo.findByName(request.getCategory().getName()))
				.orElseGet(()->{
					Category newCategory = new Category(request.getCategory().getName());
					return catrepo.save(newCategory);
				});
		request.setCategory(category);
		return repo.save(createFoodItem(request, category));
	}
	
	private FoodItem createFoodItem(AddFoodItemRequest request,Category category) {
		return new FoodItem(
				request.getName(),
				request.getFoodType(),
				request.getPrice(),
				request.getDescription(),
				category
		);
	}

	@Override
	public List<FoodItem> getAllFoodItems() {
		return repo.findAll();
	}

	@Override
	public FoodItem getFoodItemById(Long id) {
		return repo.findById(id).orElseThrow(()->new RuntimeException("FoodItem with ID : "+id+" not found !!!"));
	}

	@Override
	public void deleteFoodItemById(Long id) {
		//repo.findById(id).ifPresentOrElse(repo::delete,()->{throw new RuntimeException("FoodItem with ID : "+id+" not found ,to delete !!!");});		
		FoodItem item = repo.findById(id).orElseThrow(
		        () -> new RuntimeException("FoodItem with ID : " + id + " not found, to delete !!!")
		    ); 
//		item.getImages().clear();
		if (item.getImage() != null) {
	        item.setImage(null);
	        repo.save(item);
	    }
		repo.save(item);
		repo.delete(item);
	}

	@Override
	public FoodItem updateFoodItem(UpdateFoodItemRequest request, Long foodItemId) {
		FoodItem existingFoodItem = repo.findById(foodItemId).orElseThrow(()->new RuntimeException("FoodItem with ID : "+foodItemId+" not found !!!"));
		return repo.save(updateExistingFoodItem(existingFoodItem, request));
	}

	private FoodItem updateExistingFoodItem(FoodItem existingFoodItem,UpdateFoodItemRequest request) {
		existingFoodItem.setName(request.getName());
		existingFoodItem.setFoodType(request.getFoodType());
		existingFoodItem.setPrice(request.getPrice());
		existingFoodItem.setDescription(request.getDescription());
		
		Category category = catrepo.findByName(request.getCategory().getName());
		existingFoodItem.setCategory(category);
		return existingFoodItem;
	}
	
	@Override
	public List<FoodItem> getFoodItemsByCategory(String category) {
		return repo.findByCategoryName(category);
	}

	@Override
	public List<FoodItem> getFoodItemsByFoodType(FoodType foodType) {
		return repo.findByFoodType(foodType);
	}

	@Override
	public List<FoodItem> getFoodItemsByCategoryAndFoodType(String category,FoodType foodType) {
		return repo.findByCategoryNameAndFoodType(category, foodType);
	}

	@Override
	public List<FoodItem> getFoodItemByName(String name) {
		return repo.findByName(name);
	}

	@Override
	public List<FoodItem> getFoodItemByFoodTypeAndName(FoodType foodType, String name) {
		return repo.findByFoodTypeAndName(foodType,name);
	}

	@Override
	public Long countFoodItems() {
		return repo.count();
	}

	@Override
	public Long countFoodItemsByCategory(String category) {
		return repo.countByCategoryName(category);
	}

	@Override
	public Long countFoodItemsByFoodType(FoodType foodType) {
		return repo.countByFoodType(foodType);
	}
	
	@Override
	public List<FoodItemDto> getConvertedFoodItems(List<FoodItem> foodItems){
		return foodItems.stream().map(this::convertToDto).toList();
	}
	
//	@Override
//	public FoodItemDto convertToDto(FoodItem foodItem) {
//		FoodItemDto foodItemDto = modelMapper.map(foodItem,FoodItemDto.class);
//		List<Image> images = imageRepo.findByFoodItemsId(foodItem.getId());
//		List<ImageDto> imageDtos = images.stream().map(image->modelMapper.map(image,ImageDto.class)).toList();
//		foodItemDto.setImages(imageDtos);
//		return foodItemDto;
//	}
	
	@Override
	public FoodItemDto convertToDto(FoodItem foodItem) {
	    FoodItemDto foodItemDto = modelMapper.map(foodItem, FoodItemDto.class);
	    if (foodItem.getImage() != null) {
	        // Map the single image to its DTO
	        ImageDto imageDto = modelMapper.map(foodItem.getImage(), ImageDto.class);
	        foodItemDto.setImage(imageDto);
	    }
	    return foodItemDto;
	}

	
	@Override
	public List<FoodItem> searchFoodItems(String name, Long categoryId, String foodTypeStr){
		 FoodType foodType = null;
		    if (foodTypeStr != null && !foodTypeStr.isBlank()) {
		        foodType = FoodType.valueOf(foodTypeStr.toUpperCase());
		    }
		    return repo.searchFoodItems(name, categoryId, foodType);
	}

}
