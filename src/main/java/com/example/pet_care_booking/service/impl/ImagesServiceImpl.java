package com.example.pet_care_booking.service.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.example.pet_care_booking.exception.AppException;
import com.example.pet_care_booking.exception.ErrorCode;
import com.example.pet_care_booking.modal.Images;
import com.example.pet_care_booking.modal.Product;
import com.example.pet_care_booking.repository.ImagesRepository;
import com.example.pet_care_booking.service.ImageService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ImagesServiceImpl implements ImageService {

   private final Cloudinary cloudinary;
   private final ImagesRepository imagesRepository;

   @Override
   @Transactional
   public String uploadCate(MultipartFile file) throws IOException {
      try {
         Map<?, ?> uploadCate = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.asMap(
                "folder", "category"
         ));
         return uploadCate.get("url").toString();
      } catch (Exception e) {
         throw new AppException(ErrorCode.UPDATE_IMAGE_FAIL);
      }
   }

   @Override
   @Transactional
   public List<Images> uploadProduct(MultipartFile[] images, String name, Product product) throws IOException {
      List<Images> imageList = new ArrayList<>();
      for (MultipartFile items : images) {
         try {
            Map<?, ?> uploadProduct = cloudinary.uploader().upload(items.getBytes(), ObjectUtils.asMap(
                   "folder", "products"
            ));
            String url = uploadProduct.get("url").toString();
            String publicId = uploadProduct.get("public_id").toString();

            Images image = Images.builder()
                   .imageUrl(url)
                   .publicId(publicId)
                   .size(items.getSize())
                   .product(product)
                   .build();
            imageList.add(image);
         } catch (Exception e) {
            throw new AppException(ErrorCode.UPDATE_IMAGE_FAIL);
         }
      }
      return imageList;
   }

   @Override
   public void deleteOldImages(Product product) throws IOException {
      List<Images> oldImages = product.getImages();
      if (oldImages != null && !oldImages.isEmpty()) {
         for (Images image : oldImages) {
            cloudinary.uploader().destroy(image.getPublicId(), ObjectUtils.emptyMap());
            imagesRepository.delete(image);
         }
      }
   }
}
