package com.example.pet_care_booking.controller;

import com.example.pet_care_booking.dto.ApiResponse;
import com.example.pet_care_booking.dto.AppointmentsDTO;
import com.example.pet_care_booking.dto.OrderDTO;
import com.example.pet_care_booking.service.AppointmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import static com.example.pet_care_booking.controller.OrderController.ANONYMOUS_USER;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/appointment")
public class AppointmentsController {

   private final AppointmentService appointmentService;

   //Admin
   @GetMapping("")
   public ApiResponse<Page<AppointmentsDTO>> getAllAppointments(@RequestParam(required = false) String ownerName,
                                                          @RequestParam(required = false) String email,
                                                          @RequestParam(required = false) String phoneNumber,
                                                          @RequestParam(required = false) String namePet,
                                                          @RequestParam(required = false) String nameVet,
                                                          @RequestParam(required = false) String status,
                                                          @RequestParam(defaultValue = "0") int page,
                                                          @RequestParam(defaultValue = "10") int size) {
      ApiResponse<Page<AppointmentsDTO>> apiResponse = new ApiResponse<>();
      apiResponse.setData(appointmentService.getAppointments(ownerName, email, phoneNumber, namePet, nameVet, status, page, size));
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

//   @PutMapping("/update_client/{orderId}")
//   public ApiResponse<OrderDTO> updateAppointmentClient(@RequestBody OrderDTO orderDTO,
//                                             @PathVariable Long orderId,
//                                             @RequestParam(required = false) String sessionId) {
//      String userName = SecurityContextHolder.getContext().getAuthentication().getName();
//      boolean isAnonymous = userName == null || userName.equals(ANONYMOUS_USER);
//
//      if (isAnonymous && sessionId != null) {
//         orderService.updateOrderByClient(orderDTO, orderId, null, sessionId);
//      } else {
//         orderService.updateOrderByClient(orderDTO, orderId, userName, null);
//      }
//      ApiResponse<OrderDTO> apiResponse = new ApiResponse<>();
//      apiResponse.setMessage("Đã sửa đơn hàng thành công");
//      return apiResponse;
//   }
//
//   @PutMapping("/cancel/{orderId}")
//   public ApiResponse<OrderDTO> cancelOrder(@PathVariable Long orderId,
//                                            @RequestParam(required = false) String sessionId) {
//
//      String email = SecurityContextHolder.getContext().getAuthentication().getName();
//      boolean isAnonymous = email == null || email.equals(ANONYMOUS_USER);
//      if (isAnonymous && sessionId != null) {
//         orderService.cancelOrder(orderId, sessionId, email);
//      } else {
//         orderService.cancelOrder(orderId, null, email);
//      }
//      ApiResponse<OrderDTO> apiResponse = new ApiResponse<>();
//      apiResponse.setMessage("Đã hủy đơn hàng thành công");
//      return apiResponse;
//   }
//
//   @GetMapping("/history")
//   public ApiResponse<Page<OrderDTO>> getOrderHistory(@RequestParam(required = false) String sessionId,
//                                                      @RequestParam(required = false) String status,
//                                                      @RequestParam(defaultValue = "0") int page,
//                                                      @RequestParam(defaultValue = "9") int size) {
//
//      String userNamne = null;
//      Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//      if (authentication != null && authentication.isAuthenticated() && !ANONYMOUS_USER.equals(authentication.getName())) {
//         userNamne = authentication.getName();
//      }
//
//      ApiResponse<Page<OrderDTO>> apiResponse = new ApiResponse<>();
//      apiResponse.setData(orderService.getOrderClient(userNamne, sessionId, status, page, size));
//      return apiResponse;
//   }
}
