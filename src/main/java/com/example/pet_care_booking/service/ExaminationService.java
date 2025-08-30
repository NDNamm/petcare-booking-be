package com.example.pet_care_booking.service;

import com.example.pet_care_booking.dto.ExaminationDTO;
import org.springframework.data.domain.Page;

import java.math.BigDecimal;


public interface ExaminationService {
   Page<ExaminationDTO> getAllExaminations(String name, BigDecimal  min , BigDecimal max, int page, int size);
   ExaminationDTO createExamination(ExaminationDTO examinationDTO);
   ExaminationDTO updateExamination(Long id,ExaminationDTO examinationDTO);
   void deleteExamination(Long id);
}
