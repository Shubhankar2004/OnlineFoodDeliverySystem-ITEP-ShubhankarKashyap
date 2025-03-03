package com.itep.project.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.itep.project.model.Cart;

public interface CartRepository extends JpaRepository<Cart,Long>{

	Cart findByUserId(Long userId); 
	
}
