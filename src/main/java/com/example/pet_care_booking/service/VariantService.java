package com.example.pet_care_booking.service;

import com.example.pet_care_booking.dto.VariantDTO;
import com.example.pet_care_booking.modal.Variants;

public interface VariantService {
    void createVariant(VariantDTO variantDTO);
    Variants updateVariant(Long id,VariantDTO variantDTO);
    void deleteVariant(Long variantId);
}
