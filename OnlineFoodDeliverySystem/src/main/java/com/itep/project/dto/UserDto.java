package com.itep.project.dto;

import java.util.List;

import lombok.Data;

@Data
public class UserDto {
	private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private Long mobile;
    private List<OrderDto> orders;
    private CartDto cart;
}
