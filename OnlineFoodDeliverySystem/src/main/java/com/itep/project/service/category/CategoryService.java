package com.itep.project.service.category;

import java.util.List;

import org.springframework.stereotype.Service;

import com.itep.project.model.Category;

@Service
public interface CategoryService {
	
	Category getCategoryById(Long id);
	
	Category getCategoryByName(String name);
	
	List<Category> getAllCategories();
	
	Category addCategory(Category category);
	
	Category UpdateCategory(Category category,Long categoryId);
	
	void deleteCategoryById(Long id);

	List<Category> searchCategories(String name,String categoryName);
	
}
