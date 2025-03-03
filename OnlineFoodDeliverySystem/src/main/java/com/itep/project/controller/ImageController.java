package com.itep.project.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.sql.SQLException;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import com.itep.project.dto.ImageDto;
import com.itep.project.model.Image;
import com.itep.project.response.ApiResponse;
import com.itep.project.service.image.ImageService;

@RequiredArgsConstructor
@Controller
@RequestMapping("/onlinefoodDelivery/images")
public class ImageController {
	
	private final ImageService serv;
	
	@PostMapping("/upload")
	public ResponseEntity<ApiResponse> saveImage(@RequestParam("file") MultipartFile file,
	                                               @RequestParam Long foodItemId) {
	    try {
	        ImageDto imageDto = serv.saveImage(file, foodItemId);
	        return ResponseEntity.ok(new ApiResponse("Uploaded Successfully !!!!", imageDto));
	    } catch (Exception e) {
	        return ResponseEntity.status(INTERNAL_SERVER_ERROR)
	                             .body(new ApiResponse("Upload Failed !!!!", e.getMessage()));
	    }
	}

	
//	@GetMapping("/image/download/{imageId}")
//    public ResponseEntity<Resource> downloadImage(@PathVariable Long imageId) throws SQLException {
//        Image image = serv.getImageById(imageId);
//        ByteArrayResource resource = new ByteArrayResource(
//                image.getImage().getBytes(1, (int) image.getImage().length())
//        );
//        return ResponseEntity.ok()
//                .contentType(MediaType.parseMediaType(image.getFileType()))
//                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + image.getFileName() + "\"")
//                .body(resource);
//    }
	
	@GetMapping("/image/download/{imageId}")
	public ResponseEntity<Resource> downloadImage(@PathVariable Long imageId) throws SQLException {
	    Image image = serv.getImageById(imageId);
	    ByteArrayResource resource = new ByteArrayResource(
	        image.getImage().getBytes(1, (int) image.getImage().length())
	    );
	    return ResponseEntity.ok()
	        .contentType(MediaType.parseMediaType(image.getFileType()))
	        // UPDATED: changed "attachment" to "inline"
	        .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + image.getFileName() + "\"")
	        .body(resource);
	}

	
	@PutMapping("/image/{imageId}/update")
	public ResponseEntity<ApiResponse> updateImage(@PathVariable Long imageId, @RequestBody MultipartFile file) {
		try {
			Image image = serv.getImageById(imageId);
			if (image != null) {
				serv.updateImage(file, imageId);
				return ResponseEntity.ok(new ApiResponse("Updated Successfully !!!!", null));
			}
		} catch(Exception e) {
			return ResponseEntity.status(NOT_FOUND)
			                     .body(new ApiResponse(e.getMessage(), null));
		}
		return ResponseEntity.status(INTERNAL_SERVER_ERROR)
		                     .body(new ApiResponse("Update Failed !!!!", INTERNAL_SERVER_ERROR));
	}
	
	@DeleteMapping("/image/{imageId}/delete")
	public ResponseEntity<ApiResponse> deleteImage(@PathVariable Long imageId) {
		try {
			Image image = serv.getImageById(imageId);
			if (image != null) {
				serv.deleteImageById(imageId);
				return ResponseEntity.ok(new ApiResponse("Deleted Successfully !!!!", null));
			}
		} catch(Exception e) {
			return ResponseEntity.status(NOT_FOUND)
			                     .body(new ApiResponse(e.getMessage(), null));
		}
		return ResponseEntity.status(INTERNAL_SERVER_ERROR)
		                     .body(new ApiResponse("Deletion Failed !!!!", INTERNAL_SERVER_ERROR));
	}
}


//package com.itep.project.controller;
//
//import lombok.RequiredArgsConstructor;
//import org.springframework.core.io.ByteArrayResource;
//import org.springframework.core.io.Resource;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.MediaType;
//import org.springframework.http.ResponseEntity;
//import org.springframework.stereotype.Controller;
//import org.springframework.web.bind.annotation.*;
//import org.springframework.web.multipart.MultipartFile;
//
//import java.sql.SQLException;
//import java.util.List;
//
//import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
//import static org.springframework.http.HttpStatus.NOT_FOUND;
//
//
//import com.itep.project.dto.ImageDto;
//import com.itep.project.model.Image;
//import com.itep.project.response.ApiResponse;
//import com.itep.project.service.image.ImageService;
//
//import lombok.RequiredArgsConstructor;
//
//@RequiredArgsConstructor
//@Controller
//@RequestMapping("/onlinefoodDelivery/images")
//public class ImageController {
//	
//	private final ImageService serv;
//	
//	@PostMapping("/upload")
//	public ResponseEntity<ApiResponse> saveImages(@RequestParam List<MultipartFile>files,@RequestParam Long FoodItemId){
//		try {
//			List<ImageDto> imageDtos = serv.saveImage(files, FoodItemId);
//			return ResponseEntity.ok(new ApiResponse("Uploaded SuccessFully !!!!", imageDtos));
//		}catch (Exception e) {
//			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse("Upload Failed !!!!",e.getMessage()));
//		}
//	}
//	
//	@GetMapping("/image/download/{imageId}")
//    public ResponseEntity<Resource> downloadImage(@PathVariable Long imageId) throws SQLException {
//        Image image = serv.getImageById(imageId);
//        ByteArrayResource resource = new ByteArrayResource(image.getImage().getBytes(1, (int) image.getImage().length()));
//        return  ResponseEntity.ok().contentType(MediaType.parseMediaType(image.getFileType()))
//                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" +image.getFileName() + "\"")
//                .body(resource);
//    }
//	
//	@PutMapping("/image/{imageId}/update")
//	public ResponseEntity<ApiResponse> updateImage(@PathVariable Long imageId,@RequestBody MultipartFile file){
//		try {
//			Image image = serv.getImageById(imageId);
//			if(image != null) {
//				serv.updateImage(file, imageId);
//				return ResponseEntity.ok(new ApiResponse("Updated SuccessFully !!!!",null));
//			}
//		}catch(Exception e) {
//			return ResponseEntity.status(NOT_FOUND).body(new ApiResponse(e.getMessage(),null));
//		}
//		return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(new ApiResponse("Update Failed !!!!",INTERNAL_SERVER_ERROR));
//	}
//	
//	@DeleteMapping("/image/{imageId}/delete")
//	public ResponseEntity<ApiResponse> deleteImage(@PathVariable Long imageId){
//		try {
//			Image image = serv.getImageById(imageId);
//			if(image != null) {
//				serv.deleteImageById(imageId);
//				return ResponseEntity.ok(new ApiResponse("Deleted SuccessFully !!!!",null));
//			}
//		}catch(Exception e) {
//			return ResponseEntity.status(NOT_FOUND).body(new ApiResponse(e.getMessage(),null));
//		}
//		return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(new ApiResponse("Deletion Failed !!!!",INTERNAL_SERVER_ERROR));
//	}
//	
//}
