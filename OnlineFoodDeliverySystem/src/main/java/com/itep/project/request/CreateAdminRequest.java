package com.itep.project.request;

import lombok.Data;

@Data
public class CreateAdminRequest {
	private String firstName;
	private String lastName;
	private String email;
	private Long mobile;
	private String password;
}
