package com.itep.project.service.image;

import java.util.List;

import javax.sql.rowset.serial.SerialBlob;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.itep.project.dto.ImageDto;
import com.itep.project.model.FoodItem;
import com.itep.project.model.Image;
import com.itep.project.repository.ImageRepository;
import com.itep.project.service.foodItem.FoodItemService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ImageServiceImpl implements ImageService {
    
    private final ImageRepository repo;
    private final FoodItemService foodItemService;
    
    @Override
    public Image getImageById(Long id) {
        return repo.findById(id)
          .orElseThrow(() -> new RuntimeException("Image with ID: " + id + " does not exist!"));
    }
    
    @Override
    public void deleteImageById(Long id) {
        repo.findById(id).ifPresentOrElse(repo::delete, () -> {
            throw new RuntimeException("Image with ID: " + id + " does not exist to delete!");
        });
    }
    
    // Updated: Now returns a single ImageDto (one image per FoodItem)
//    @Override
//    public ImageDto saveImage(List<MultipartFile> files, Long foodItemId) {
//        FoodItem foodItem = foodItemService.getFoodItemById(foodItemId);
//        if (files == null || files.isEmpty()) {
//            throw new RuntimeException("No file provided");
//        }
//        // Use only the first file from the list
//        MultipartFile file = files.get(0);
//        try {
//            Image image = new Image();
//            image.setFileName(file.getOriginalFilename());
//            image.setFileType(file.getContentType());
//            image.setImage(new SerialBlob(file.getBytes()));
//            image.setFoodItem(foodItem);
//            
//            // Save image
//            Image savedImage = repo.save(image);
//            
//            // Build download URL – adjust the base path as needed.
//            String buildDownloadUrl = "/OnlineFoodDeliverySystem/src/main/resources/static/images/foodItemImage/";
//            savedImage.setDownloadUrl(buildDownloadUrl + savedImage.getId());
//            
//            // Set the image in the FoodItem entity
//            foodItem.setImage(savedImage);
//            
//            ImageDto imageDto = new ImageDto();
//            imageDto.setId(savedImage.getId());
//            imageDto.setFileName(savedImage.getFileName());
//            imageDto.setDownloadUrl(savedImage.getDownloadUrl());
//            
//            return imageDto;
//        } catch (Exception e) {
//            throw new RuntimeException(e.getMessage());
//        }
//    }
//    
    
//    @Override
//    public ImageDto saveImage(MultipartFile file, Long foodItemId) {
//        FoodItem foodItem = foodItemService.getFoodItemById(foodItemId);
//        if (file == null || file.isEmpty()) {
//            throw new RuntimeException("No file provided");
//        }
//        try {
//            Image image = new Image();
//            image.setFileName(file.getOriginalFilename());
//            image.setFileType(file.getContentType());
//            image.setImage(new SerialBlob(file.getBytes()));
//            image.setFoodItem(foodItem);
//
//            // Save the image to generate an ID
//            Image savedImage = repo.save(image);
//
//            // Build download URL – pointing to your controller's GET endpoint
//            String buildDownloadUrl = "/onlinefoodDelivery/images/image/download/" + savedImage.getId();
//            savedImage.setDownloadUrl(buildDownloadUrl);
//
//            // Update in DB
//            repo.save(savedImage);
//
//            // Link it to the FoodItem (one-to-one)
//            foodItem.setImage(savedImage);
//
//            ImageDto imageDto = new ImageDto();
//            imageDto.setId(savedImage.getId());
//            imageDto.setFileName(savedImage.getFileName());
//            imageDto.setDownloadUrl(savedImage.getDownloadUrl());
//
//            return imageDto;
//        } catch (Exception e) {
//            throw new RuntimeException(e.getMessage());
//        }
//    }
    
    @Override
    public ImageDto saveImage(MultipartFile file, Long foodItemId) {
        FoodItem foodItem = foodItemService.getFoodItemById(foodItemId);
        if (file == null || file.isEmpty()) {
            throw new RuntimeException("No file provided");
        }
        try {
            Image image = new Image();
            image.setFileName(file.getOriginalFilename());
            image.setFileType(file.getContentType());
            image.setImage(new SerialBlob(file.getBytes()));
            image.setFoodItem(foodItem);

            // Save image to get an ID
            Image savedImage = repo.save(image);

            // IMPORTANT: build a URL that matches your controller's @GetMapping
            // The leading "/" makes it absolute from the context root.
            String buildDownloadUrl = "/onlinefoodDelivery/images/image/download/" + savedImage.getId();
            savedImage.setDownloadUrl(buildDownloadUrl);

            // Save again to update the downloadUrl in DB
            repo.save(savedImage);

            // Link the image to the FoodItem (one-to-one)
            foodItem.setImage(savedImage);
            
            foodItemService.saveFoodItem(foodItem);

            // Return DTO
            ImageDto imageDto = new ImageDto();
            imageDto.setId(savedImage.getId());
            imageDto.setFileName(savedImage.getFileName());
            imageDto.setDownloadUrl(savedImage.getDownloadUrl());
            return imageDto;

        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public void updateImage(MultipartFile file, Long imageId) {
        Image image = getImageById(imageId);
        try {
            image.setFileName(file.getOriginalFilename());
            image.setFileType(file.getContentType());
            image.setImage(new SerialBlob(file.getBytes()));
            repo.save(image);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}



//package com.itep.project.service.image;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import javax.sql.rowset.serial.SerialBlob;
//
//import org.springframework.stereotype.Service;
//import org.springframework.web.multipart.MultipartFile;
//
//import com.itep.project.dto.ImageDto;
//import com.itep.project.model.FoodItem;
//import com.itep.project.model.Image;
//import com.itep.project.repository.FoodItemRepository;
//import com.itep.project.repository.ImageRepository;
//import com.itep.project.service.foodItem.FoodItemService;
//
//import lombok.RequiredArgsConstructor;
//
//@Service
//@RequiredArgsConstructor
//public class ImageServiceImpl implements ImageService {
//	
//	private final ImageRepository repo;
//	
//	private final FoodItemService foodItemService;
//	
//	@Override
//	public Image getImageById(Long id) {
//		return repo.findById(id).orElseThrow(()->new RuntimeException("Image with ID : "+id+" does not exists !!!!"));
//	}
//
//	@Override
//	public void deleteImageById(Long id) {
//		repo.findById(id).ifPresentOrElse(repo::delete,()->{throw new RuntimeException("Image with ID : "+id+" does not exists to delete !!!!");});
//	}
//
//	@Override
//	public List<ImageDto> saveImage(List<MultipartFile> files, Long foodItemId) {
//		FoodItem foodItem = foodItemService.getFoodItemById(foodItemId);
//		List<ImageDto> savedImageDtos = new ArrayList<>();
//		
//		for(MultipartFile file : files) {
//			try {
//				Image image = new Image();
//				image.setFileName(file.getOriginalFilename());
//				image.setFileType(file.getContentType());
//				image.setImage(new SerialBlob(file.getBytes()));
//				image.setFoodItems(foodItem);
//				
//				//String buildDownloadUrl = "api/v1/images/image/download/"; 
//				String buildDownloadUrl = "/OnlineFoodDeliverySystem/src/main/resources/static/images/foodItemImage/";
//				
//				String downloadUrl = buildDownloadUrl+image.getId();
//				image.setDownloadUrl(downloadUrl);
//				Image savedImage = repo.save(image);
//				savedImage.setDownloadUrl(buildDownloadUrl+savedImage.getId());
//				
//				ImageDto imageDto = new ImageDto();
//				imageDto.setId(savedImage.getId());
//				imageDto.setFileName(savedImage.getFileName());
//				imageDto.setDownloadUrl(savedImage.getDownloadUrl());
//				
//				savedImageDtos.add(imageDto);
//				
//			}catch(Exception e) {
//				throw new RuntimeException(e.getMessage());
//			}
//		}
//		
//		return savedImageDtos;
//	}
//
//	@Override
//	public void updateImage(MultipartFile file, Long imageId) {
//		Image image = getImageById(imageId);
//		try {
//			image.setFileName(file.getOriginalFilename());
//			image.setFileType(file.getContentType());
//			image.setImage(new SerialBlob(file.getBytes()));
//			repo.save(image);
//		}catch(Exception e) {
//			throw new RuntimeException(e.getMessage());
//		}
//	}
//
//}
