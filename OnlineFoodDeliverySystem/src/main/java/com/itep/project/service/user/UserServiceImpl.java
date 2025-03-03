package com.itep.project.service.user;

import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.itep.project.dto.UserDto;
import com.itep.project.model.Cart;
import com.itep.project.model.User;
import com.itep.project.repository.AddressRepository;
import com.itep.project.repository.UserRepository;
import com.itep.project.request.CreateUserRequest;
import com.itep.project.request.UpdateUserRequest;
import com.itep.project.request.UserLoginRequest;
import com.itep.project.service.cart.CartServiceImpl;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService{
	
	private final UserRepository repo;
	
	private final AddressRepository addrepo;
	
	private final ModelMapper modelMapper;
	
	private final CartServiceImpl cartServ;

	@Override
	public User getUserById(Long userId) {
		return repo.findById(userId).orElseThrow(()->new RuntimeException("User with ID : "+userId+" not found !!!!"));
	}

	@Override
	public User createUser(CreateUserRequest request) {
		return Optional.of(request).filter(user->!repo.existsByEmail(request.getEmail()))
				.map(req->{
					User user = new User();
					user.setEmail(request.getEmail());
					user.setPassword(request.getPassword());
					user.setFirstName(request.getFirstName());
					user.setLastName(request.getLastName());
					user.setMobile(request.getMobile());
					user.setAddresses(request.getAddress());
					User saveduser = repo.save(user);
					 if (user.getCart() == null) {
			                Cart newCart = cartServ.initializeNewCart(saveduser);
			                user.setCart(newCart);
			            }
					return saveduser;
				}).orElseThrow(()->new RuntimeException("User with email :"+request.getEmail()+" already exists !!!!"));
	}
	
	@Override
	public User updateUser(UpdateUserRequest request, Long userId) {
	    return repo.findById(userId).map(existingUser -> {
	        // Personal info
	        existingUser.setFirstName(request.getFirstName());
	        existingUser.setLastName(request.getLastName());
	        existingUser.setMobile(request.getMobile());
	        
	        // Address
	        if (request.getAddress() != null) {
	            // If address is not null, set it
	            existingUser.setAddresses(request.getAddress());
	        }
	        
	        return repo.save(existingUser);
	    }).orElseThrow(() -> new RuntimeException("User with ID : " + userId + " not found !!!!"));
	}

	
//	@Override
//	public User updateUser(UpdateUserRequest request, Long userId) {
//	    return repo.findById(userId).map(existingUser -> {
//	        // Update personal info
//	        existingUser.setFirstName(request.getFirstName());
//	        existingUser.setLastName(request.getLastName());
//	        existingUser.setMobile(request.getMobile());
//	        
//	        // UPDATED: Only update the address if one is provided in the request.
//	        // If request.getAddress() is null, keep the existing address.
//	        if (request.getAddress() != null) {
//	            existingUser.setAddresses(request.getAddress());
//	        }
//	        
//	        return repo.save(existingUser);
//	    }).orElseThrow(() -> new RuntimeException("User with ID : " + userId + " not found !!!!"));
//	}

	@Override
	public void deleteUser(Long userId) {
		repo.findById(userId).ifPresentOrElse(repo::delete,()->{
			throw new RuntimeException("User with ID : "+userId+" not found to delete !!!!");
		});
	}
	
	 @Override
	 public UserDto convertUserToDto(User user) {
		 return modelMapper.map(user, UserDto.class);
	 }

	@Override
	public User userLogin(UserLoginRequest request) {
		User user = repo.findByEmail(request.getEmail());
		
		if(user==null) {
			throw new RuntimeException("User does not exists");
		}else {
			if(user.getPassword().equals(request.getPassword())){
				return user;
			}else {
				throw new RuntimeException("wrong password");
			}
		}
		
	}

}
