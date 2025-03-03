package com.itep.project.dto;

import org.hibernate.annotations.NaturalId;

import lombok.Data;

@Data
public class AdminDto {
	private String firstName;
	private String lastName;
	@NaturalId
	private String email;
	private Long mobile;
}
