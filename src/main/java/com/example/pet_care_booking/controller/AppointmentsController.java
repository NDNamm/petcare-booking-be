package com.example.pet_care_booking.controller;

import com.example.pet_care_booking.dto.ApiResponse;
import com.example.pet_care_booking.dto.AppointmentsDTO;
import com.example.pet_care_booking.dto.VeterinariansDTO;
import com.example.pet_care_booking.service.AppointmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

import static com.example.pet_care_booking.controller.OrderController.ANONYMOUS_USER;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/appointment")
public class AppointmentsController {

    private final AppointmentService appointmentService;

    //Admin
    @GetMapping("")
    public ApiResponse<Page<AppointmentsDTO>> getAllAppointments(@RequestParam(required = false) String ownerName,
                                                                 @RequestParam(required = false) String phoneNumber,
                                                                 @RequestParam(required = false) String email,
                                                                 @RequestParam(required = false) String petName,
                                                                 @RequestParam(required = false) String vetName,
                                                                 @RequestParam(required = false) String status,
                                                                 @RequestParam(defaultValue = "0") int page,
                                                                 @RequestParam(defaultValue = "10") int size) {
        ApiResponse<Page<AppointmentsDTO>> apiResponse = new ApiResponse<>();
        apiResponse.setData(appointmentService.getAppointments(ownerName, phoneNumber, email, petName, vetName, status, page, size));
        return apiResponse;
    }

    @PutMapping("/update/{id}")
    public ApiResponse<AppointmentsDTO> updateAppointment(@RequestBody AppointmentsDTO dto,
                                                          @PathVariable Long id) {

        ApiResponse<AppointmentsDTO> apiResponse = new ApiResponse<>();
        appointmentService.updateAppointment(id, dto);
        apiResponse.setMessage("Đã sửa lich kham thành công");
        return apiResponse;
    }

    @DeleteMapping("delete/{id}")
    public ApiResponse<AppointmentsDTO> deleteAppointment(@PathVariable Long id) {

        ApiResponse<AppointmentsDTO> apiResponse = new ApiResponse<>();
        appointmentService.deleteAppointment(id);
        apiResponse.setMessage("Đã xóa đơn hàng thành công");
        return apiResponse;

    }

    //Client
    @PostMapping("/add/{vetId}")
    public ApiResponse<AppointmentsDTO> addAppointmentClient(@PathVariable Long vetId,
                                                             @RequestBody AppointmentsDTO dto,
                                                             @RequestParam(required = false) String sessionId) {
        String userName = SecurityContextHolder.getContext().getAuthentication().getName();
        boolean isAnonymous = userName == null || userName.equals(ANONYMOUS_USER);

        ApiResponse<AppointmentsDTO> apiResponse = new ApiResponse<>();
        if (isAnonymous && sessionId != null) {
            apiResponse.setData(appointmentService.addAppointment(vetId, dto, null, sessionId));
        } else {
            apiResponse.setData(appointmentService.addAppointment(vetId, dto, userName, null));
        }
        apiResponse.setMessage("Đã lich kham thành công");

        return apiResponse;
    }

    @PutMapping("/update_client/{id}")
    public ApiResponse<AppointmentsDTO> updateAppointmentClient(@RequestBody AppointmentsDTO dto,
                                                                @PathVariable Long id,
                                                                @RequestParam(required = false) String sessionId) {
        String userName = SecurityContextHolder.getContext().getAuthentication().getName();
        boolean isAnonymous = userName == null || userName.equals(ANONYMOUS_USER);

        if (isAnonymous && sessionId != null) {
            appointmentService.updateAppointmentByClient(dto, id, null, sessionId);
        } else {
            appointmentService.updateAppointmentByClient(dto, id, userName, null);
        }
        ApiResponse<AppointmentsDTO> apiResponse = new ApiResponse<>();
        apiResponse.setMessage("Đã sửa lich kham thành công");
        return apiResponse;
    }

    @PutMapping("/cancel/{id}")
    public ApiResponse<AppointmentsDTO> cancelOrder(@PathVariable Long id,
                                                    @RequestParam(required = false) String sessionId) {

        String userName = SecurityContextHolder.getContext().getAuthentication().getName();
        boolean isAnonymous = userName == null || userName.equals(ANONYMOUS_USER);
        if (isAnonymous && sessionId != null) {
            appointmentService.cancelAppointment(id, null, sessionId);
        } else {
            appointmentService.cancelAppointment(id, userName, null);
        }
        ApiResponse<AppointmentsDTO> apiResponse = new ApiResponse<>();
        apiResponse.setMessage("Đã hủy lich kham thành công");
        return apiResponse;
    }

    @GetMapping("/history")
    public ApiResponse<Page<AppointmentsDTO>> getOrderHistory(@RequestParam(required = false) String sessionId,
                                                              @RequestParam(required = false) String status,
                                                              @RequestParam(defaultValue = "0") int page,
                                                              @RequestParam(defaultValue = "9") int size) {

        String userNamne = null;
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && !ANONYMOUS_USER.equals(authentication.getName())) {
            userNamne = authentication.getName();
        }

        ApiResponse<Page<AppointmentsDTO>> apiResponse = new ApiResponse<>();
        apiResponse.setData(appointmentService.getAppointmentClient(userNamne, sessionId, status, page, size));
        return apiResponse;
    }

    @GetMapping("/generateInvoice/{id}")
    public ResponseEntity<byte[]> generateInvoice(@PathVariable Long id) {
        byte[] pdfBytes = appointmentService.generateInvoice(id);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("inline", "invoice_" + id + ".pdf");

        return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
    }

    @GetMapping("/check-free-time")
    public ResponseEntity<List<VeterinariansDTO>> checkFreeTime(
            @RequestParam("start") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start) {
        return ResponseEntity.ok(appointmentService.checkFreeTime(start));
    }
}
