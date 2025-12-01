package com.example.pet_care_booking.service.impl;

import com.example.pet_care_booking.dto.ExaminationDTO;
import com.example.pet_care_booking.exception.AppException;
import com.example.pet_care_booking.exception.ErrorCode;
import com.example.pet_care_booking.modal.Appointments;
import com.example.pet_care_booking.modal.Examination;
import com.example.pet_care_booking.repository.AppointmentRepository;
import com.example.pet_care_booking.repository.ExaminationRepository;
import com.example.pet_care_booking.service.ExaminationService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Service
@RequiredArgsConstructor
public class ExaminationServiceImpl implements ExaminationService {

    private final ExaminationRepository examinationRepository;
    private final AppointmentRepository appointmentRepository;

    @Override
    public Page<ExaminationDTO> getAllExaminations(String name, BigDecimal min, BigDecimal max, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").ascending());
        Page<Examination> pageEx;

        boolean noFilter = (name == null || name.isBlank())
                && (min == null || min.compareTo(BigDecimal.ZERO) == 0)
                && (max == null || max.compareTo(BigDecimal.ZERO) == 0);

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
                        .active(listEx.isActivate())
                        .updatedAt(listEx.getUpdatedAt())
                        .build()
        );
    }

    @Override
    public ExaminationDTO createExamination(ExaminationDTO examinationDTO) {

        if (examinationRepository.existsByName(examinationDTO.getName())) {
            throw new AppException(ErrorCode.EXAMINATION_NAME_EXISTED);
        }

        Examination examination = Examination.builder()
                .name(examinationDTO.getName())
                .price(examinationDTO.getPrice())
                .description(examinationDTO.getDescription())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
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
        examination.setUpdatedAt(LocalDateTime.now());
        examinationRepository.save(examination);

        return getExamination(examination);
    }

    @Override
    public void deleteExamination(Long id) {
        Examination examination = examinationRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.EXAMINATION_NOT_FOUND));

        examinationRepository.delete(examination);
    }

    @Override
    @Transactional
    public int[] addExaminationSpecial(int[] examinationIds, Long appointmentId) {
        Appointments appointments = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new AppException(ErrorCode.APPOINTMENT_NOT_FOUND));
        BigDecimal sum = BigDecimal.ZERO;
        if (examinationIds != null && examinationIds.length > 0) {
            List<Examination> exams = new ArrayList<>();
            for(int i : examinationIds){
                Examination examination = examinationRepository.findById((long) i)
                                .orElseThrow(() -> new IllegalArgumentException("Not found"));
                exams.add(examination);
                sum = sum.add(examination.getPrice());
            }
            appointments.setExamination(exams);
        }
        appointments.setTotalPrice(sum);
        appointmentRepository.save(appointments);
        return examinationIds;
    }



    private ExaminationDTO getExamination(Examination ex) {
        return ExaminationDTO.builder()
                .id(ex.getId())
                .name(ex.getName())
                .price(ex.getPrice())
                .description(ex.getDescription())
                .createdAt(ex.getCreatedAt())
                .updatedAt(ex.getUpdatedAt())
                .build();
    }
}
