package com.itep.project.service.address;

import java.util.List;

import org.springframework.stereotype.Service;

import com.itep.project.model.Address;

@Service
public interface AddressService {
	
	public List<Address> getAllAddresses();
	
	public Address getAddressById(Long id);
	
	public Address saveOrUpdateAddress(Address addr);
	
	public void deleteAddressById(Long id);
	
}
