package com.itep.project.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.itep.project.model.Category;

public interface CategoryRepository extends JpaRepository<Category,Long>{

	Category findByName(String name);

	boolean existsByName(String name);

}
