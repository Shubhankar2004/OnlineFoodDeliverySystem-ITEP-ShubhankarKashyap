package com.itep.project.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.itep.project.dto.FoodItemDto;
import com.itep.project.model.FoodItem;
import com.itep.project.model.FoodType;
import com.itep.project.request.AddFoodItemRequest;
import com.itep.project.request.UpdateFoodItemRequest;
import com.itep.project.response.ApiResponse;
import com.itep.project.service.category.CategoryService;
import com.itep.project.service.foodItem.FoodItemService;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Controller
@RequestMapping("/onlinefoodDelivery/fooditems")
public class FoodItemController {
	
	private final FoodItemService serv;
	
	private final CategoryService catServ;
	
//	@GetMapping("/browse")
//    public String browseFoodItems(Model model,
//                                  @RequestParam(required = false) String search,
//                                  @RequestParam(required = false) String category,
//                                  @RequestParam(required = false) String foodType) {
//        List<FoodItem> foodItems;
//        if (search != null || category != null || foodType != null) {
//            // For simplicity, assume category filtering is not numeric.
//            foodItems = serv.searchFoodItems(search, null, foodType);
//        } else {
//            foodItems = serv.getAllFoodItems();
//        }
//        List<FoodItemDto> foodItemsDto = serv.getConvertedFoodItems(foodItems);
//        model.addAttribute("foodItems", foodItemsDto);
//        model.addAttribute("allCategories", catServ.getAllCategories());
//        return "browseFoodItem"; // returns browseFoodItems.html
//    }
	
	@GetMapping("/browse")
    public String browseFoodItems(Model model, HttpSession session,
                                  @RequestParam(required = false) String search,
                                  @RequestParam(required = false) String category,
                                  @RequestParam(required = false) String foodType) {
        // Add session attributes for navbar display
        Object userObj = session.getAttribute("user");
        if(userObj != null) {
            model.addAttribute("user", userObj);
            // Assuming user has a cart
            model.addAttribute("cart", ((com.itep.project.model.User)userObj).getCart());
        }
        
        List<FoodItem> foodItems;
        if (search != null || category != null || foodType != null) {
            // For simplicity, assume category filtering is not numeric.
            foodItems = serv.searchFoodItems(search, null, foodType);
        } else {
            foodItems = serv.getAllFoodItems();
        }
        List<FoodItemDto> foodItemsDto = serv.getConvertedFoodItems(foodItems);
        model.addAttribute("foodItems", foodItemsDto);
        model.addAttribute("allCategories", catServ.getAllCategories());
        return "browseFoodItem"; // returns browseFoodItem.html
    }

	
	@GetMapping("/manageFoodItem")
    public String showManageFoodItems(Model model) {
        List<FoodItem> foodItems = serv.getAllFoodItems();
        List<FoodItemDto> convertedFoodItems = serv.getConvertedFoodItems(foodItems);
        model.addAttribute("foodItems", convertedFoodItems);
        model.addAttribute("allCategories", catServ.getAllCategories());
        return "manageFoodItems"; // corresponds to manageFoodItem.html
    }
	
	@GetMapping("/all")
	public ResponseEntity<ApiResponse> getAllFoodItems(){
		List<FoodItem> foodItems = serv.getAllFoodItems();
		List<FoodItemDto> convertedFoodItems = serv.getConvertedFoodItems(foodItems);
		return ResponseEntity.ok(new ApiResponse("Found FoodItems !!!!",convertedFoodItems));
	}
	
	@GetMapping("/fooditem/get/{id}")
	public ResponseEntity<ApiResponse> getFoodItemById(@PathVariable Long id){
		try {
			FoodItem foodItem = serv.getFoodItemById(id);
			FoodItemDto convertedFoodItem = serv.convertToDto(foodItem);
			return ResponseEntity.ok(new ApiResponse("Found !!!!", convertedFoodItem));
		}catch (Exception e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse(e.getMessage(),null));
		}
	}
	
	@GetMapping("/addform")
	public String showAddFoodItemForm(Model model) {
	    // Pass all categories so the form can populate the dropdown.
	    model.addAttribute("allCategories", catServ.getAllCategories());
	    // Optionally, you can add an empty AddFoodItemRequest object:
	    model.addAttribute("addFoodItemRequest", new AddFoodItemRequest());
	    return "addFoodItem"; // corresponds to addFoodItem.html
	}
	
	@PostMapping("/add")
	public ResponseEntity<ApiResponse> addFoodItem(@RequestBody AddFoodItemRequest addFoodItem){
		try {
			FoodItem foodItem = serv.addFoodItem(addFoodItem);
			FoodItemDto convertedFoodItem = serv.convertToDto(foodItem);
			return ResponseEntity.ok(new ApiResponse("Added Successfully !!!!", convertedFoodItem));
		}catch(Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse(e.getMessage(),null));
		}
	}
	
	@GetMapping("/updateform/{id}")
	public String showUpdateFoodItemForm(@PathVariable Long id, Model model) {
	    // Retrieve the existing food item
	    FoodItem foodItem = serv.getFoodItemById(id);
	    // Optionally, map it to UpdateFoodItemRequest using ModelMapper or manually:
	    UpdateFoodItemRequest updateRequest = new UpdateFoodItemRequest();
	    updateRequest.setId(foodItem.getId());
	    updateRequest.setName(foodItem.getName());
	    updateRequest.setFoodType(foodItem.getFoodType());
	    updateRequest.setPrice(foodItem.getPrice());
	    updateRequest.setDescription(foodItem.getDescription());
	    updateRequest.setCategory(foodItem.getCategory());
	    
	    model.addAttribute("updateFoodItemRequest", updateRequest);
	    model.addAttribute("allCategories", catServ.getAllCategories());
	    return "updateFoodItem"; // corresponds to updateFoodItem.html
	}
	
//	@PutMapping("/fooditem/update/{id}")
//	public String updateFoodItem(@ModelAttribute UpdateFoodItemRequest updateFoodItem,
//	                             @PathVariable Long id,
//	                             RedirectAttributes redirectAttributes) {
//	    try {
//	        FoodItem foodItem = serv.updateFoodItem(updateFoodItem, id);
//	        redirectAttributes.addFlashAttribute("successMessage", "Food item updated successfully!");
//	        return "redirect:/onlinefoodDelivery/fooditems/manageFoodItem";
//	    } catch(Exception e) {
//	        redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
//	        return "redirect:/onlinefoodDelivery/fooditems/updateform/" + id;
//	    }
//	}

	
	@PutMapping("/fooditem/update/{id}")
	public ResponseEntity<ApiResponse> updateFoodItem(@RequestBody UpdateFoodItemRequest updateFoodItem,@PathVariable Long id){
		try {
			FoodItem foodItem = serv.updateFoodItem(updateFoodItem,id);
			FoodItemDto convertedFoodItem = serv.convertToDto(foodItem);
			return ResponseEntity.ok(new ApiResponse("Updation Successfull !!!!", convertedFoodItem));
		}catch(Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse(e.getMessage(),null));
		}
	}
	
	@DeleteMapping("/fooditem/{id}")
	public ResponseEntity<ApiResponse> deleteFoodItem(@PathVariable Long id){
		try {
			serv.deleteFoodItemById(id);
			return ResponseEntity.ok(new ApiResponse("Deletion Successfull !!!", id));
		}catch (Exception e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse(e.getMessage(),null));
		}
	}
	
	@GetMapping("/fooditems/by-foodtype-and-name/{foodType}/{foodItemName}")
	public ResponseEntity<ApiResponse> getFoodItemsByFoodTypeAndName(@PathVariable FoodType foodType,@PathVariable String foodItemName){
		List<FoodItem> foodItems = serv.getFoodItemByFoodTypeAndName(foodType, foodItemName);
		List<FoodItemDto> convertedFoodItems = serv.getConvertedFoodItems(foodItems);
		if(convertedFoodItems.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse("FoodItems not found !!!!",null));
		}
		return ResponseEntity.ok(new ApiResponse("Found !!!!", convertedFoodItems));
	}
	
	@GetMapping("/fooditems/by-category-and-foodtype/{category}/{foodType}")
	public ResponseEntity<ApiResponse> getFoodItemsByCategoryAndFoodType(@PathVariable String category,@PathVariable FoodType foodType){
		List<FoodItem> foodItems = serv.getFoodItemsByCategoryAndFoodType(category, foodType);
		List<FoodItemDto> convertedFoodItems = serv.getConvertedFoodItems(foodItems);
		if(convertedFoodItems.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse("FoodItems not found !!!!",null));
		}
		return ResponseEntity.ok(new ApiResponse("Found !!!!", convertedFoodItems));
	}
	
	@GetMapping("/fooditem/{name}")
	public ResponseEntity<ApiResponse> getFoodItemByName(@PathVariable String name){
		try {
			List<FoodItem> foodItems = serv.getFoodItemByName(name);
			List<FoodItemDto> convertedFoodItems = serv.getConvertedFoodItems(foodItems);
			if(convertedFoodItems.isEmpty()) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse("FoodItems not found !!!!",null));
			}
			return ResponseEntity.ok(new ApiResponse("Found !!!!", convertedFoodItems)); 
		}catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse(e.getMessage(),null));
		}
	}
	
	@GetMapping("/foodType/{foodType}")
	public ResponseEntity<ApiResponse> getFoodItemByFoodType(@PathVariable FoodType foodType){
		try {
			List<FoodItem> foodItems = serv.getFoodItemsByFoodType(foodType);
			List<FoodItemDto> convertedFoodItems = serv.getConvertedFoodItems(foodItems);
			if(convertedFoodItems.isEmpty()) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse("FoodItems not found !!!!",null));
			}
			return ResponseEntity.ok(new ApiResponse("Found !!!!", convertedFoodItems));
		}catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse(e.getMessage(),null));
		}
	}
	
	@GetMapping("/category/{category}")
	public ResponseEntity<ApiResponse> getFoodItemByCategory(@PathVariable String category){
		try {
			List<FoodItem> foodItems = serv.getFoodItemsByCategory(category);
			List<FoodItemDto> convertedFoodItems = serv.getConvertedFoodItems(foodItems);
			if(convertedFoodItems.isEmpty()) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse("FoodItems not found !!!!",null));
			}
			return ResponseEntity.ok(new ApiResponse("Found !!!!", convertedFoodItems));
		}catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse(e.getMessage(),null));
		}
	}
	
	@GetMapping("/fooditem/count")
	public ResponseEntity<ApiResponse> getCountOfAllFoodItems(){
		try {
			Long count = serv.countFoodItems();
			return ResponseEntity.ok(new ApiResponse("Count of Found FoodItems : ", count));
		}catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse(e.getMessage(),null));
		}
	}
	
	@GetMapping("/fooditem/count/by-category/{category}")
	public ResponseEntity<ApiResponse> getCountOfAllFoodItemsByCategory(@PathVariable String category){
		try {
			Long count = serv.countFoodItemsByCategory(category);
			return ResponseEntity.ok(new ApiResponse("Count of Found FoodItems : ", count));
		}catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse(e.getMessage(),null));
		}
	}
	
	@GetMapping("/fooditem/count/by-foodType/{foodType}")
	public ResponseEntity<ApiResponse> getCountOfAllFoodItemsByFoodType(@PathVariable FoodType foodType){
		try {
			Long count = serv.countFoodItemsByFoodType(foodType);
			return ResponseEntity.ok(new ApiResponse("Count of Found FoodItems : ", count));
		}catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse(e.getMessage(),null));
		}
	}
	
	@GetMapping("/search")
	public ResponseEntity<ApiResponse> searchFoodItems(
	        @RequestParam(required = false) String name,
	        @RequestParam(required = false) Long categoryId,
	        @RequestParam(required = false) String foodType
	) {
	    try {
	        // Call the service method that applies the filters
	        List<FoodItem> foodItems = serv.searchFoodItems(name, categoryId, foodType);
	        // Convert to DTO if you have a conversion method
	        List<FoodItemDto> convertedFoodItems = serv.getConvertedFoodItems(foodItems);

	        if (convertedFoodItems.isEmpty()) {
	            return ResponseEntity.status(HttpStatus.NOT_FOUND)
	                                 .body(new ApiResponse("FoodItems not found !!!!", null));
	        }
	        return ResponseEntity.ok(new ApiResponse("Found FoodItems !!!!", convertedFoodItems));
	    } catch (Exception e) {
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                             .body(new ApiResponse(e.getMessage(), null));
	    }
	}
	
}
