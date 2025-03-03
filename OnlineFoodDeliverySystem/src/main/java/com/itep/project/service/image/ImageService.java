package com.itep.project.service.image;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.itep.project.dto.ImageDto;
import com.itep.project.model.Image;

@Service
public interface ImageService {
	
	Image getImageById(Long id);
	
	void deleteImageById(Long id);
	
	ImageDto saveImage(MultipartFile file,Long foodItemId);
	
	void updateImage(MultipartFile file,Long imageId);
	
}
