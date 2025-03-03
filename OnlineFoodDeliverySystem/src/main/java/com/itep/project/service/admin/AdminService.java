package com.itep.project.service.admin;

import org.springframework.stereotype.Service;

import com.itep.project.dto.AdminDto;
import com.itep.project.model.Admin;
import com.itep.project.request.AdminLoginRequest;
import com.itep.project.request.CreateAdminRequest;
import com.itep.project.request.UpdateAdminRequest;

@Service
public interface AdminService {
	
    Admin getAdminById(Long adminId);
	
	Admin createAdmin(CreateAdminRequest request);
	
	Admin updateAdmin(UpdateAdminRequest request,Long adminId);
	
	void deleteAdmin(Long adminId);

	AdminDto convertUserToDto(Admin admin);
	
	Admin adminLogin(AdminLoginRequest request);
	
}
