package com.itep.project.request;

import lombok.Data;

@Data
public class UpdateAdminRequest {
	private String firstName;
	private String lastName;
	private Long mobile;
}
