package com.itep.project.service.user;

import org.springframework.stereotype.Service;

import com.itep.project.dto.UserDto;
import com.itep.project.model.User;
import com.itep.project.request.CreateUserRequest;
import com.itep.project.request.UpdateUserRequest;
import com.itep.project.request.UserLoginRequest;

@Service
public interface UserService {
	
	User getUserById(Long userId);
	
	User createUser(CreateUserRequest request);
	
	User updateUser(UpdateUserRequest request,Long userId);
	
	void deleteUser(Long userId);

	UserDto convertUserToDto(User user);
	
	User userLogin(UserLoginRequest request);
	
}
