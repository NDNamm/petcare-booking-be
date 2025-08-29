package com.example.pet_care_booking.service;

import com.example.pet_care_booking.dto.ExaminationDTO;

import java.util.List;

public interface ExaminationService {
   List<ExaminationDTO> getAllExaminations();
   ExaminationDTO createExamination(ExaminationDTO examinationDTO);
   ExaminationDTO updateExamination(Long id,ExaminationDTO examinationDTO);
   void deleteExamination(Long id);
}
