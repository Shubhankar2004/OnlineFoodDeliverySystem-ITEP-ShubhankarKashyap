package com.itep.project.service.address;

import java.util.List;

import org.springframework.stereotype.Service;

import com.itep.project.model.Address;
import com.itep.project.repository.AddressRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AddressServiceImpl implements AddressService{
	
	private final AddressRepository repo;
	
	@Override
	public List<Address> getAllAddresses() {
		return repo.findAll();
	}

	@Override
	public Address getAddressById(Long id) {
		return repo.findById(id).orElse(null);
	}

	@Override
	public Address saveOrUpdateAddress(Address addr) {
		return repo.save(addr);
	}

	@Override
	public void deleteAddressById(Long id) {
		repo.findById(id).orElse(null);
		repo.deleteById(id);
	}
	
}
