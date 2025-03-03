package com.itep.project.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.itep.project.dto.AdminDto;
import com.itep.project.model.Admin;
import com.itep.project.request.AdminLoginRequest;
import com.itep.project.request.CreateAdminRequest;
import com.itep.project.request.UpdateAdminRequest;
import com.itep.project.response.ApiResponse;
import com.itep.project.service.admin.AdminService;
import com.itep.project.service.email.EmailService;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Controller
@RequestMapping("/onlinefoodDelivery/admins")
public class AdminController {
	
	private final AdminService serv;
	
	private final EmailService email;
	
	 @PostMapping("/register")
	    public String registerAdmin(@ModelAttribute("admin") CreateAdminRequest adminRequest,
	                                BindingResult bindingResult,
	                                Model model,
	                                RedirectAttributes redirectAttributes,HttpSession session) {
	        // Optionally, check for validation errors (if you add @Valid annotations)
	        if (bindingResult.hasErrors()) {
	            model.addAttribute("error", "Please correct the errors in the form.");
	            return "adminRegistration";
	        }
	        try {
	            // Call the service to create the admin
	            Admin admin = serv.createAdmin(adminRequest);
	            session.setAttribute("admin", admin);
	            redirectAttributes.addFlashAttribute("admin", admin);
	            //model.addAttribute("admin",admin);
	            // On success, redirect to the admin dashboard
	            email.sendEmail(admin.getEmail(),"Welcome Admin","Dear "+admin.getFirstName()+",\n Welcome to the Team !!!!. We look forward to work with you !!!!\nThanks and regards,\nOnlineFoodDelivery Team");
	            return "redirect:/onlinefoodDelivery/admins/dashboard";
	        } catch (Exception e) {
	            // On failure, add the error message to the model and show the registration form again
	            model.addAttribute("error", e.getMessage());
	            return "adminRegistration";
	        }
	    }
	 
	 @GetMapping("/dashboard")
		public String adminPage() {
			return "adminDashboard";
		}
	
	@GetMapping("/get/{adminId}")
	public ResponseEntity<ApiResponse> getAdminById(Long adminId){
		try {
			Admin admin = serv.getAdminById(adminId);
			AdminDto adminDto = serv.convertUserToDto(admin);
			return ResponseEntity.ok(new ApiResponse("Found !!!!", adminDto));
		}catch (Exception e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse(e.getMessage(),null));
		}
	}

//	@PostMapping("/create/admin")
//	public ResponseEntity<ApiResponse> createAdmin(@RequestBody CreateAdminRequest request){
//		try {
//			Admin admin = serv.createAdmin(request);
//			AdminDto adminDto = serv.convertUserToDto(admin);
//			return ResponseEntity.ok(new ApiResponse("Created Successfully !!!!", adminDto));
//		}catch (Exception e) {
//			return ResponseEntity.status(HttpStatus.CONFLICT).body(new ApiResponse(e.getMessage(),null));
//		}
//	}
	
	@PostMapping("/login")
	public String loginAdmin(@ModelAttribute("admin") AdminLoginRequest request,BindingResult bindingResult,Model model,RedirectAttributes redirectAttributes) {
	    if (bindingResult.hasErrors()) {
	        model.addAttribute("error", "Please correct the errors in the form.");
	        return "adminLogin";
	    }
	    
	    try {
	        Admin admin = serv.adminLogin(request);
	        redirectAttributes.addFlashAttribute("admin", admin);
	        return "redirect:/onlinefoodDelivery/admins/dashboard";
	    } catch (Exception e) {
	        model.addAttribute("error", e.getMessage());
	        return "adminLogin";
	    }
	}

	
//	@PostMapping("/login/admin")
//	public ResponseEntity<ApiResponse> loginAdmin(@RequestBody AdminLoginRequest request){
//		try {
//			Admin admin = serv.adminLogin(request);
//			AdminDto adminDto = serv.convertUserToDto(admin);
//			return ResponseEntity.ok(new ApiResponse("Logged In Successfully !!!!", adminDto));
//		}catch (Exception e) {
//			return ResponseEntity.status(HttpStatus.CONFLICT).body(new ApiResponse(e.getMessage(),null));
//		}
//	}
	
	@PutMapping("/update/admin/{adminId}")
	public ResponseEntity<ApiResponse> updateAdmin(@RequestBody UpdateAdminRequest request,@PathVariable Long adminId){
		try {
			Admin admin = serv.updateAdmin(request, adminId);
			AdminDto adminDto = serv.convertUserToDto(admin);
			return ResponseEntity.ok(new ApiResponse("Updated Successfully !!!!",adminDto));
		}catch (Exception e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse(e.getMessage(),null));
		}
	}
	
	@DeleteMapping("remove/admin/{adminId}")
	public ResponseEntity<ApiResponse> deleteAdmin(@PathVariable Long adminId){
		try {
			serv.deleteAdmin(adminId);
			return ResponseEntity.ok(new ApiResponse("Deleted Successfully !!!!", adminId));
		}catch (Exception e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse(e.getMessage(),null));
		}
	}
	
//	@GetMapping("/manageCategory")
//	public String manageCategory() {
//		return "manageCategories";
//	}
	
	@GetMapping("/logout")
	public String logout(HttpSession session) {
	    session.invalidate();
	    return "redirect:/onlinefoodDelivery/home"; // Ensure your home page exists (e.g., home.html)
	}
	
}
