package com.itep.project.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.itep.project.model.CartItem;

public interface CartItemRepository extends JpaRepository<CartItem,Long>{

	void deleteAllByCartId(Long id);

}
