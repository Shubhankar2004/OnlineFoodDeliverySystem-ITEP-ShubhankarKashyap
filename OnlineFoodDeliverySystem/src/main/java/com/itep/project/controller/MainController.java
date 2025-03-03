package com.itep.project.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Controller
@RequestMapping("/onlinefoodDelivery")
public class MainController {
	
	@GetMapping("/home")
	public String homePage() {
		return "home";
	}
	
	 @GetMapping("/admin/login")
	 public String showAdminLoginPage() {
		 return "adminLogin";  // This will render adminLogin.html from the templates folder
	 }
	 
	 @GetMapping("/admin/register")
	 public String showAdminRegisterPage() {
		 return "adminRegistration";  // This will render adminLogin.html from the templates folder
	 }
	 
	 @GetMapping("/user/login")
	 public String showUserLoginPage() {
		 return "userLogin";  // This will render adminLogin.html from the templates folder
	 }
	 
	 @GetMapping("/user/register")
	 public String showUserRegisterPage() {
		 return "userRegistration";  // This will render adminLogin.html from the templates folder
	 }

}
