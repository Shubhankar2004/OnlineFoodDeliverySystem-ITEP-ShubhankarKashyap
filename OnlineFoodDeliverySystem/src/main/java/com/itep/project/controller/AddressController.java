package com.itep.project.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.itep.project.model.Address;
import com.itep.project.service.address.AddressService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Controller
@RequestMapping("/onlinefoodDelivery/addresses")
public class AddressController {
	
	private final AddressService serv;
	
	@GetMapping("/get/all")
	public ResponseEntity<List<Address>> getAllAddresss(){
		List<Address> Addresss = serv.getAllAddresses();
		return ResponseEntity.ok(Addresss);
	}
	
	@GetMapping("/get/{id}")
	public ResponseEntity<Address> getAddressById(@PathVariable Long id){
		Address Address = serv.getAddressById(id);
		return ResponseEntity.ok(Address);
	}
	
	@PostMapping("/add")
	public ResponseEntity<Address> createAddress(@RequestBody Address addr){
		Address createdAddress = serv.saveOrUpdateAddress(addr);
		return ResponseEntity.ok(createdAddress);
	}
	
//	@PutMapping("/update/{id}")
//	public ResponseEntity<Address> updateAddress(@PathVariable Long id,@RequestBody Address addr){
//		Address existingAddress = serv.getAddressById(id);
//		if(existingAddress == null) {
//			return ResponseEntity.notFound().build();
//		}
//		addr.setId(id);
//		serv.saveOrUpdateAddress(addr);
//		return ResponseEntity.ok(addr);
//	}
	
	// UPDATED: New endpoint to update address via form submission
//    @PostMapping("/updateAddress/{id}")
//    public String updateAddress(@PathVariable Long id,
//                                @ModelAttribute("address") Address address,
//                                RedirectAttributes redirectAttributes) {
//        try {
//            address.setId(id);
//            Address updatedAddress = serv.saveOrUpdateAddress(address);
//            redirectAttributes.addFlashAttribute("success", "Address updated successfully!");
//            return "redirect:/onlinefoodDelivery/fooditems/profile";
//        } catch(Exception e) {
//            redirectAttributes.addFlashAttribute("error", e.getMessage());
//            return "redirect:/onlinefoodDelivery/fooditems/profile";
//        }
//    }
	
	// UPDATED: New endpoint to update address via form submission.
		@PostMapping("/updateAddress/{id}")
		public String updateAddress(@PathVariable Long id,
		                            @ModelAttribute("address") Address address,
		                            RedirectAttributes redirectAttributes) {
		    try {
		        address.setId(id);
		        Address updatedAddress = serv.saveOrUpdateAddress(address);
		        redirectAttributes.addFlashAttribute("success", "Address updated successfully!");
		        return "redirect:/onlinefoodDelivery/fooditems/profile";
		    } catch(Exception e) {
		        redirectAttributes.addFlashAttribute("error", e.getMessage());
		        return "redirect:/onlinefoodDelivery/fooditems/profile";
		    }
		}
	
	@DeleteMapping("/delete/{id}")
	public ResponseEntity<Void> removeAddressById(@PathVariable Long id){
		Address addr = serv.getAddressById(id);
		if(addr == null) {
			return ResponseEntity.notFound().build();
		}
		serv.deleteAddressById(id);
		return ResponseEntity.noContent().build();
	}
	
	
}
