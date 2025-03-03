package com.itep.project.request;

import lombok.Data;

@Data
public class AdminLoginRequest {
	private String email;
	private String password;
}
