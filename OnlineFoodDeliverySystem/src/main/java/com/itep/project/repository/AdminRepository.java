package com.itep.project.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.itep.project.model.Admin;
import com.itep.project.model.User;

public interface AdminRepository extends JpaRepository<Admin,Long>{
	
	boolean existsByEmail(String email);
	
	Admin findByEmail(String email);
	
}
