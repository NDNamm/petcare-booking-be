package com.example.pet_care_booking.service.impl;

import com.example.pet_care_booking.dto.ExaminationDTO;
import com.example.pet_care_booking.exception.AppException;
import com.example.pet_care_booking.exception.ErrorCode;
import com.example.pet_care_booking.modal.Examination;
import com.example.pet_care_booking.repository.ExaminationRepository;
import com.example.pet_care_booking.service.ExaminationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;


@Service
@RequiredArgsConstructor
public class ExaminationServiceImpl implements ExaminationService {

   private final ExaminationRepository examinationRepository;

   @Override
   public Page<ExaminationDTO> getAllExaminations(String name, BigDecimal min , BigDecimal  max, int page, int size) {
      Pageable pageable = PageRequest.of(page, size, Sort.by("id").ascending());
      Page<Examination> pageEx;

      boolean noFilter = (name == null || name.isBlank()) && min == null && max == null;
      pageEx = noFilter
             ? examinationRepository.findAll(pageable)
             : examinationRepository.searchExm(name, min, max, pageable);


      return pageEx.map(
             listEx -> ExaminationDTO.builder()
                    .id(listEx.getId())
                    .name(listEx.getName())
                    .price(listEx.getPrice())
                    .description(listEx.getDescription())
                    .createdAt(listEx.getCreatedAt())
                    .build()
      );
   }

   @Override
   public ExaminationDTO createExamination(ExaminationDTO examinationDTO) {

      if(examinationRepository.existsByName(examinationDTO.getName())){
         throw new AppException(ErrorCode.EXAMINATION_NAME_EXISTED);
      }

      Examination examination = Examination.builder()
             .name(examinationDTO.getName())
             .price(examinationDTO.getPrice())
             .description(examinationDTO.getDescription())
             .createdAt(LocalDateTime.now())
             .build();

      examinationRepository.save(examination);

      return getExamination(examination);
   }

   @Override
   public ExaminationDTO updateExamination(Long id, ExaminationDTO examinationDTO) {

      Examination examination = examinationRepository.findById(id)
             .orElseThrow(() -> new AppException(ErrorCode.EXAMINATION_NOT_FOUND));

      examination.setName(examinationDTO.getName());
      examination.setPrice(examinationDTO.getPrice());
      examination.setDescription(examinationDTO.getDescription());
      examinationRepository.save(examination);

      return getExamination(examination);
   }

   @Override
   public void deleteExamination(Long id) {
      Examination examination = examinationRepository.findById(id)
             .orElseThrow(() -> new AppException(ErrorCode.EXAMINATION_NOT_FOUND));

      examinationRepository.delete(examination);
   }

   private ExaminationDTO getExamination(Examination ex) {
      return ExaminationDTO.builder()
             .id(ex.getId())
             .name(ex.getName())
             .price(ex.getPrice())
             .description(ex.getDescription())
             .build();
   }
}
