package com.example.pet_care_booking.service.impl;

import com.example.pet_care_booking.dto.ExaminationDTO;
import com.example.pet_care_booking.exception.AppException;
import com.example.pet_care_booking.exception.ErrorCode;
import com.example.pet_care_booking.modal.Examination;
import com.example.pet_care_booking.repository.ExaminationRepository;
import com.example.pet_care_booking.service.ExaminationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;


@Service
@RequiredArgsConstructor
public class ExaminationServiceImpl implements ExaminationService {

   private final ExaminationRepository examinationRepository;

   @Override
   public List<ExaminationDTO> getAllExaminations() {
      List<Examination> list = examinationRepository.findAll();

      return list.stream().map(
             listEx -> ExaminationDTO.builder()
                    .id(listEx.getId())
                    .name(listEx.getName())
                    .price(listEx.getPrice())
                    .description(listEx.getDescription())
                    .createdAt(listEx.getCreatedAt())
                    .build()
      ).toList();
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
