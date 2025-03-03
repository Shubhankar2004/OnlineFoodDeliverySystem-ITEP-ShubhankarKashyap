package com.itep.project.request;

import com.itep.project.model.Address;

import lombok.Data;

@Data
public class UpdateUserRequest {
	private String firstName;
	private String lastName;
	private Long mobile;
	private Address address;
}
