package com.itep.project.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.itep.project.model.FoodItem;
import com.itep.project.model.FoodType;

public interface FoodItemRepository extends JpaRepository<FoodItem,Long>{

	List<FoodItem> findByCategoryName(String category);

	List<FoodItem> findByFoodType(FoodType foodType);

	List<FoodItem> findByCategoryNameAndFoodType(String category, FoodType foodType);

	List<FoodItem> findByName(String name);

	List<FoodItem> findByFoodTypeAndName(FoodType foodType, String name);

	Long countByCategoryName(String category);

	Long countByFoodType(FoodType foodType);
	
	@Query("""
			   SELECT f
			   FROM FoodItem f
			   WHERE
			       (:name IS NULL OR LOWER(f.name) LIKE CONCAT('%', LOWER(:name), '%'))
			       AND (:categoryId IS NULL OR f.category.id = :categoryId)
			       AND (:foodType IS NULL OR f.foodType = :foodType)
			""")
			List<FoodItem> searchFoodItems(
			   @Param("name") String name,
			   @Param("categoryId") Long categoryId,
			   @Param("foodType") FoodType foodType
			);


}
