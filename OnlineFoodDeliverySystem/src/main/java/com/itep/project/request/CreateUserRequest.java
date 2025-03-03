package com.itep.project.request;

import com.itep.project.model.Address;

import lombok.Data;

@Data
public class CreateUserRequest {
	private String firstName;
	private String lastName;
	private String email;
	private Long mobile;
	private String password;
	private Address address;
}
