package com.itep.project.service.category;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.itep.project.model.Category;
import com.itep.project.model.FoodType;
import com.itep.project.repository.CategoryRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService{
	
	private final CategoryRepository repo;

	@Override
	public Category getCategoryById(Long id) {
		return repo.findById(id).orElseThrow(()->new RuntimeException("Category with ID : "+id+" not found !!!!"));
	}

	@Override
	public Category getCategoryByName(String name) {
		return repo.findByName(name);
	}

	@Override
	public List<Category> getAllCategories() {
		return repo.findAll();
	}

	@Override
	public Category addCategory(Category category) {
		return Optional.of(category).filter(c->!repo.existsByName(c.getName()))
				.map(repo::save).orElseThrow(()->new RuntimeException("Category with name : "+category.getName()+" already exists !!!!"));
	}

	@Override
	public Category UpdateCategory(Category category,Long categoryId) {
		Category oldCategory = repo.findById(categoryId).orElseThrow(()->new RuntimeException("Category with ID : "+categoryId+" not found !!!!"));
		oldCategory.setName(category.getName());
		return repo.save(oldCategory);
	}

	@Override
	public void deleteCategoryById(Long id) {
		repo.findById(id).ifPresentOrElse(repo::delete,()->{
			throw new RuntimeException("Category with ID :"+id+" not found, to delete !!!!");
		});
	}
	
	@Override
    public List<Category> searchCategories(String name, String categoryName) {
        List<Category> categories = getAllCategories();
        
        if (name != null && !name.isBlank()) {
            categories = categories.stream()
                    .filter(cat -> cat.getName().toLowerCase().contains(name.toLowerCase()))
                    .collect(Collectors.toList());
        }
        
        if (categoryName != null && !categoryName.isBlank()) {
            categories = categories.stream()
                    .filter(cat -> cat.getName().equalsIgnoreCase(categoryName))
                    .collect(Collectors.toList());
        }
        
        return categories;
    }
	
}
