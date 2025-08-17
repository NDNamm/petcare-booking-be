package com.example.pet_care_booking.service;

import com.example.pet_care_booking.modal.Images;
import com.example.pet_care_booking.modal.Product;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface ImageService {
   String uploadCate(MultipartFile file) throws IOException;
   List<Images> uploadProduct(MultipartFile[] images, String name, Product product) throws IOException;
   void deleteOldImages(Product product) throws IOException;
}
