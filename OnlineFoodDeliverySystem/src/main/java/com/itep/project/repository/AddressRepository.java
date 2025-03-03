package com.itep.project.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.itep.project.model.Address;

public interface AddressRepository extends JpaRepository<Address,Long>{

}
