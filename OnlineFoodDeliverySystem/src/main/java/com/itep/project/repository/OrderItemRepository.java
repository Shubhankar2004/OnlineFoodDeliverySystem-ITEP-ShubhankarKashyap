package com.itep.project.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.itep.project.model.OrderItem;

public interface OrderItemRepository extends JpaRepository<OrderItem,Long>{

}
