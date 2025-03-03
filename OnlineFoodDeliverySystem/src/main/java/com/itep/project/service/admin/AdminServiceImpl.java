package com.itep.project.service.admin;

import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.itep.project.dto.AdminDto;
import com.itep.project.model.Admin;
import com.itep.project.model.User;
import com.itep.project.repository.AdminRepository;
import com.itep.project.request.AdminLoginRequest;
import com.itep.project.request.CreateAdminRequest;
import com.itep.project.request.UpdateAdminRequest;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService{
	
	private final AdminRepository repo;
	
	private final ModelMapper modelMapper;
	
	@Override
	public Admin getAdminById(Long adminId) {
		return repo.findById(adminId).orElseThrow(()->new RuntimeException("Admin with ID : "+adminId+" not found !!!!"));
	}

	@Override
	public Admin createAdmin(CreateAdminRequest request) {
		return Optional.of(request).filter(admin->!repo.existsByEmail(request.getEmail()))
				.map(req->{
					Admin admin = new Admin();
					admin.setEmail(request.getEmail());
					admin.setPassword(request.getPassword());
					admin.setFirstName(request.getFirstName());
					admin.setLastName(request.getLastName());
					admin.setMobile(request.getMobile());
					return repo.save(admin);
				}).orElseThrow(()->new RuntimeException("Admin with email :"+request.getEmail()+" already exists !!!!"));
	}

	@Override
	public Admin updateAdmin(UpdateAdminRequest request, Long adminId) {
		return repo.findById(adminId).map(existingAdmin->{
			existingAdmin.setFirstName(request.getFirstName());
			existingAdmin.setLastName(request.getLastName());
			return repo.save(existingAdmin);
		}).orElseThrow(()->new RuntimeException("Admin with ID : "+adminId+" not found !!!!"));
	}

	@Override
	public void deleteAdmin(Long adminId) {
		repo.findById(adminId).ifPresentOrElse(repo::delete,()->{
			throw new RuntimeException("Admin with ID : "+adminId+" not found to delete !!!!");
		});
	}

	@Override
	public AdminDto convertUserToDto(Admin admin) {
		return modelMapper.map(admin, AdminDto.class);
	}

	@Override
	public Admin adminLogin(AdminLoginRequest request) {
		Admin admin = repo.findByEmail(request.getEmail());
		
		if(admin==null) {
			throw new RuntimeException("Admin does not exists");
		}else {
			if(admin.getPassword().equals(request.getPassword())){
				return admin;
			}else {
				throw new RuntimeException("wrong password");
			}
		}
	}

}
