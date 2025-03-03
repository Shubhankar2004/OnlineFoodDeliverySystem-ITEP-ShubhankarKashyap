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
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.itep.project.dto.AdminDto;
import com.itep.project.dto.UserDto;
import com.itep.project.model.Admin;
import com.itep.project.model.Cart;
import com.itep.project.model.User;
import com.itep.project.request.AdminLoginRequest;
import com.itep.project.request.CreateAdminRequest;
import com.itep.project.request.CreateUserRequest;
import com.itep.project.request.UpdateUserRequest;
import com.itep.project.request.UserLoginRequest;
import com.itep.project.response.ApiResponse;
import com.itep.project.service.address.AddressService;
import com.itep.project.service.cart.CartServiceImpl;
import com.itep.project.service.email.EmailService;
import com.itep.project.service.user.UserService;
import com.itep.project.model.Address;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Controller
@RequestMapping("/onlinefoodDelivery/users")
public class UserController {
	
	private final UserService serv;
	
	private final AddressService addServ;
	
	private final EmailService email;
	
//	@GetMapping("/dashboard")
//	public String userPage() {
//		return "userDashboard";
//	}
	
	@GetMapping("/login")
    public String showUserLoginPage() {
        return "userLogin";
    }
	
	@GetMapping("/dashboard")
	 public String userPage(Model model,HttpSession session) {
		 User user = (User) session.getAttribute("user");
	     	if (user == null) {
	            return "redirect:/onlinefoodDelivery/users/login";
	        }
	     model.addAttribute("user",user);
	     model.addAttribute("cart",user.getCart());
	     return "userDashboard"; // returns userDashboard.html
	  }
	
	@PostMapping("/login")
	public String loginAdmin(@ModelAttribute("user") UserLoginRequest request,BindingResult bindingResult,Model model, HttpSession session) {
	    if (bindingResult.hasErrors()) {
	        model.addAttribute("error", "Please correct the errors in the form.");
	        return "userLogin";
	    }
	    
	    try {
	        User user = serv.userLogin(request);
	        session.setAttribute("user", user);
            session.setAttribute("cart", user.getCart());
	        return "redirect:/onlinefoodDelivery/users/dashboard";
	    } catch (Exception e) {
	        model.addAttribute("error", e.getMessage());
	        return "userLogin";
	    }
	}
	
	 
	 
//	 @GetMapping("/profile")
//	 public String userProfile(Model model, HttpSession session) {
//	     User user = (User) session.getAttribute("user");
//	        if (user == null) {
//	            return "redirect:/onlinefoodDelivery/users/login";
//	        }
//	     model.addAttribute("user", user);
//	     return "profile"; // create profile.html accordingly
//	 }
	
//	@GetMapping("/profile")
//	public String profilePage(Model model, HttpSession session) {
//		User user = (User) session.getAttribute("user");
//		if(user == null) {
//			return "redirect:/onlinefoodDelivery/users/login";
//		}
//		// Prepare an update request with current data.
//		UpdateUserRequest updateRequest = new UpdateUserRequest();
//		updateRequest.setFirstName(user.getFirstName());
//		updateRequest.setLastName(user.getLastName());
//		updateRequest.setMobile(user.getMobile());
//		// UPDATED: Initialize address if null so the form binds correctly.
//		if(user.getAddresses() != null) {
//			updateRequest.setAddress(user.getAddresses());
//		} else {
//			updateRequest.setAddress(new com.itep.project.model.Address());
//		}
//		
//		model.addAttribute("user", user);
//		model.addAttribute("updateUserRequest", updateRequest);
//		return "profile"; // returns profile.html
//	}
	
	@GetMapping("/profile")
    public String profilePage(Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if(user == null) {
            return "redirect:/onlinefoodDelivery/users/login";
        }
        // Prepare an update request with current data.
        UpdateUserRequest updateRequest = new UpdateUserRequest();
        updateRequest.setFirstName(user.getFirstName());
        updateRequest.setLastName(user.getLastName());
        updateRequest.setMobile(user.getMobile());
        // UPDATED: Initialize the address properly so that Address is not null.
        if(user.getAddresses() != null) {
            updateRequest.setAddress(user.getAddresses());
        } else {
            updateRequest.setAddress(new Address());
        }
        
        model.addAttribute("user", user);
        model.addAttribute("updateUserRequest", updateRequest);
        return "profile"; // returns profile.html
    }
	
	@PostMapping("/updateProfile")
	public String updateProfile(
	        @ModelAttribute("updateUserRequest") UpdateUserRequest updateRequest,
	        HttpSession session, 
	        RedirectAttributes redirectAttributes) {
	    try {
	        User currentUser = (User) session.getAttribute("user");
	        User updatedUser = serv.updateUser(updateRequest, currentUser.getId());
	        
	        // Update session user to the new updated user
	        session.setAttribute("user", updatedUser);
	        
	        redirectAttributes.addFlashAttribute("success", "Profile updated successfully!");
	        email.sendEmail(updatedUser.getEmail(),"Profile Updation ","Dear "+updatedUser.getFirstName()+",\n Your profile is updated \nThanks and regards,\nOnlineFoodDelivery Team");
	        // Make sure the redirect matches your @GetMapping for the profile page
	        return "redirect:/onlinefoodDelivery/users/profile"; 
	    } catch(Exception e) {
	        redirectAttributes.addFlashAttribute("error", e.getMessage());
	        return "redirect:/onlinefoodDelivery/users/profile";
	    }
	}

    
//	@PostMapping("/updateProfile")
//	public String updateProfile(@ModelAttribute("updateUserRequest") UpdateUserRequest updateRequest,
//			HttpSession session, RedirectAttributes redirectAttributes) {
//		try {
//			User currentUser = (User) session.getAttribute("user");
//			// Update the user using the service. (Note: updateUser will only update address if provided.)
//			User updatedUser = serv.updateUser(updateRequest, currentUser.getId());
//			session.setAttribute("user", updatedUser);
//			redirectAttributes.addFlashAttribute("success", "Profile updated successfully!");
//			return "redirect:/onlinefoodDelivery/fooditems/profile";
//		} catch(Exception e) {
//			redirectAttributes.addFlashAttribute("error", e.getMessage());
//			return "redirect:/onlinefoodDelivery/fooditems/profile";
//		}
//	}
	
	@GetMapping("/get/{userId}")
	public ResponseEntity<ApiResponse> getUserById(Long userId){
		try {
			User user = serv.getUserById(userId);
			UserDto userDto = serv.convertUserToDto(user);
			return ResponseEntity.ok(new ApiResponse("Found !!!!", userDto));
		}catch (Exception e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse(e.getMessage(),null));
		}
	}
	
	@PostMapping("/register")
    public String registerAdmin(@ModelAttribute("user") CreateUserRequest userRequest,
                                BindingResult bindingResult,
                                Model model,
                                RedirectAttributes redirectAttributes,HttpSession session) {
        // Optionally, check for validation errors (if you add @Valid annotations)
        if (bindingResult.hasErrors()) {
            model.addAttribute("error", "Please correct the errors in the form.");
            return "userRegistration";
        }
        try {
            // Call the service to create the user
            User user = serv.createUser(userRequest);
            session.setAttribute("user", user);
            session.setAttribute("cart", user.getCart());
            redirectAttributes.addFlashAttribute("user", user);
            redirectAttributes.addFlashAttribute("cart", user.getCart());
            email.sendEmail(user.getEmail(),"Welcome to our Application","Dear "+user.getFirstName()+",\n Welcome !!!!. We look forward to serve you, delecious food that you like !!!!\nThanks and regards,\nOnlineFoodDelivery Team");
            // On success, redirect to the admin dashboard
            return "redirect:/onlinefoodDelivery/users/dashboard";
        } catch (Exception e) {
            // On failure, add the error message to the model and show the registration form again
            model.addAttribute("error", e.getMessage());
            return "userRegistration";
        }
    }
	
//	@PostMapping("/create/user")
//	public ResponseEntity<ApiResponse> createUser(@RequestBody CreateUserRequest request){
//		try {
//			User user = serv.createUser(request);
//			UserDto userDto = serv.convertUserToDto(user);
//			return ResponseEntity.ok(new ApiResponse("Created Successfully !!!!", userDto));
//		}catch (Exception e) {
//			return ResponseEntity.status(HttpStatus.CONFLICT).body(new ApiResponse(e.getMessage(),null));
//		}
//	}
	
	

	
//	@PostMapping("/login/user")
//	public ResponseEntity<ApiResponse> loginUser(@RequestBody UserLoginRequest request){
//		try {
//			User user = serv.userLogin(request);
//			UserDto userDto = serv.convertUserToDto(user);
//			return ResponseEntity.ok(new ApiResponse("Logged In Successfully !!!!", userDto));
//		}catch (Exception e) {
//			return ResponseEntity.status(HttpStatus.CONFLICT).body(new ApiResponse(e.getMessage(),null));
//		}
//	}
	
	@PutMapping("/update/user/{userId}")
	public ResponseEntity<ApiResponse> updateUser(@RequestBody UpdateUserRequest request,@PathVariable Long userId){
		try {
			User user = serv.updateUser(request, userId);
			UserDto userDto = serv.convertUserToDto(user);
			return ResponseEntity.ok(new ApiResponse("Updated Successfully !!!!", userDto));
		}catch (Exception e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse(e.getMessage(),null));
		}
	}
	
	@DeleteMapping("remove/user/{userId}")
	public ResponseEntity<ApiResponse> deleteUser(@PathVariable Long userId){
		try {
			serv.deleteUser(userId);
			return ResponseEntity.ok(new ApiResponse("Deleted Successfully !!!!", userId));
		}catch (Exception e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse(e.getMessage(),null));
		}
	}
	
	// >>> NEW: Logout Mapping <<<
		@GetMapping("/logout")
		public String logout(HttpSession session) {
		    session.invalidate();
		    return "redirect:/onlinefoodDelivery/home"; // Ensure your home page exists (e.g., home.html)
		}
	
}
