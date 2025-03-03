package com.itep.project.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.itep.project.model.Category;
import com.itep.project.response.ApiResponse;
import com.itep.project.service.category.CategoryService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Controller
@RequestMapping("/onlinefoodDelivery/categories")
public class CategoryController {
	
	private final CategoryService serv;
	
//	@GetMapping("/manageCategory")
//	public String manageCategory() {
//		return "manageCategories";
//	}
	
	@GetMapping("/manageCategory")
    public String showManageCategories(Model model) {
        List<Category> categories = serv.getAllCategories();
        model.addAttribute("categories", categories);
        return "manageCategories"; // This corresponds to manage-categories.html
    }
	
//	@GetMapping("/getall")
//	public ResponseEntity<ApiResponse> getAllCategories(){
//		List<Category> categories = serv.getAllCategories();
//		return ResponseEntity.ok(new ApiResponse("All categories", categories));
//	}
	
	@PostMapping("/add")
	public ResponseEntity<ApiResponse> addCategory(@RequestBody Category name){
		try {
			Category category = serv.addCategory(name);
			return ResponseEntity.ok(new ApiResponse("Success", category));
		}catch(Exception e) {
			return ResponseEntity.status(HttpStatus.CONFLICT).body(new ApiResponse(e.getMessage(),null));
		}
	}
	
	@GetMapping("/category/id/{id}")
	public ResponseEntity<ApiResponse> getCategoryById(@PathVariable Long id){
		try {
			Category category = serv.getCategoryById(id);
			return ResponseEntity.ok(new ApiResponse("Category retreived Successfully !!!!", category));
		}catch (Exception e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse(e.getMessage(),null));
		}
	}
	
	@GetMapping("/category/name/{name}")
	public ResponseEntity<ApiResponse> getCategoryByName(@PathVariable String name){
		try {
			Category category = serv.getCategoryByName(name);
			return ResponseEntity.ok(new ApiResponse("Category retreived Successfully !!!!", category));
		}catch (Exception e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse(e.getMessage(),null));
		}
	}
	
	@DeleteMapping("/category/id/{id}")
	public ResponseEntity<ApiResponse> deleteCategoryById(@PathVariable Long id){
		try {
			serv.deleteCategoryById(id);
			return ResponseEntity.ok(new ApiResponse("Category deleted Successfully !!!!",null));
		}catch (Exception e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse(e.getMessage(),null));
		}
	}
	
	@PutMapping("/category/update/{id}")
	public ResponseEntity<ApiResponse> updateCategory(@PathVariable Long id,@RequestBody Category category){
		try {
			Category updateCategory = serv.UpdateCategory(category, id);
			return ResponseEntity.ok(new ApiResponse("Update Success!!!!", updateCategory));
		}catch(Exception e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse(e.getMessage(),null));
		}
	}
	
	@GetMapping("/search")
    public ResponseEntity<ApiResponse> searchCategories(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String categoryName) {
        try {
            List<Category> categories = serv.searchCategories(name, categoryName);
            if (categories.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                     .body(new ApiResponse("No categories found", null));
            }
            return ResponseEntity.ok(new ApiResponse("Found categories", categories));
        } catch(Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body(new ApiResponse(e.getMessage(), null));
        }
    }
	
}
