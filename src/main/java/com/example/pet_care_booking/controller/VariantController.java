package com.example.pet_care_booking.controller;

import com.example.pet_care_booking.service.VariantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/variant/")
public class VariantController {
    @Autowired
    private VariantService variantService;

//    @PutMapping("/edit/:id")
//    private ResponseEntity<Variants> updateVariant(@PathVariable Long id, @RequestBody VariantDTO variantDTO) {
//        return ResponseEntity.ok(variantService.updateVariant(id, variantDTO));
//    }

    @DeleteMapping("/delete/{id}")
    private ResponseEntity<Void> updateVariant(@PathVariable Long id) {
        variantService.deleteVariant(id);
        return ResponseEntity.ok().build();
    }

}
