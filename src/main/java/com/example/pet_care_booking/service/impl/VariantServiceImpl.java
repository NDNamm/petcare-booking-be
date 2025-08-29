package com.example.pet_care_booking.service.impl;

import com.example.pet_care_booking.dto.VariantDTO;
import com.example.pet_care_booking.modal.Product;
import com.example.pet_care_booking.modal.Variants;
import com.example.pet_care_booking.repository.ProductRepository;
import com.example.pet_care_booking.repository.VariantRepository;
import com.example.pet_care_booking.service.VariantService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class VariantServiceImpl implements VariantService {
    @Autowired
    private VariantRepository variantRepository;
    @Autowired
    private ProductRepository productRepository;

    @Override
    public void createVariant(VariantDTO variantDTO) {
        Optional<Product> existProduct = productRepository.findById(variantDTO.getProductId());
        if (existProduct.isEmpty()) {
            throw new EntityNotFoundException("product not found");
        }
        Optional<Variants> existVariant = variantRepository.findBySizeAndProduct(variantDTO.getSize(), existProduct.get());
        if (existVariant.isPresent()) {
            throw new EntityNotFoundException("variant not found");
        }
        Variants variants = Variants.builder()
                .product(existProduct.get())
                .price(variantDTO.getPrice())
                .stock(variantDTO.getStock())
                .size(variantDTO.getSize())
                .build();
        variantRepository.save(variants);
    }

    @Override
    public Variants updateVariant(Long variantId, VariantDTO variantDTO) {
        Optional<Variants> existVariant = variantRepository.findById(variantId);
        if (existVariant.isEmpty()) {
            throw new EntityNotFoundException("variant not found");
        }
        if (variantDTO.getSize() != null) {
            existVariant.get().setSize(variantDTO.getSize());
        }
        if (variantDTO.getPrice() != null) {
            existVariant.get().setPrice(variantDTO.getPrice());
        }
        if (variantDTO.getStock() != null) {
            existVariant.get().setStock(variantDTO.getStock());
        }
        return variantRepository.save(existVariant.get());
    }

    @Override
    public void deleteVariant(Long variantId) {
        Optional<Variants> existVariant = variantRepository.findById(variantId);
        if (existVariant.isEmpty()) {
            throw new EntityNotFoundException("variant not found");
        }
        variantRepository.delete(existVariant.get());
    }
}
