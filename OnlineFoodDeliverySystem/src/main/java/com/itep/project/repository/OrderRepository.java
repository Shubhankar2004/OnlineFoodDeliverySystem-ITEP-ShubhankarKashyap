package com.itep.project.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.itep.project.model.Order;

public interface OrderRepository extends JpaRepository<Order,Long>{

	List<Order> findByUserId(Long userId);
	
	List<Order> findByUserEmail(String email);
	
	List<Order> findByUserMobile(Long mobile);
	
	List<Order> findByUserFirstNameIgnoreCase(String firstName);
	
}
