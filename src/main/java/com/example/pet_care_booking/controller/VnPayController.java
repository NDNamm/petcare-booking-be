package com.example.pet_care_booking.controller;

import com.example.pet_care_booking.enums.PaymentStatus;
import com.example.pet_care_booking.modal.Order;
import com.example.pet_care_booking.modal.Payments;
import com.example.pet_care_booking.modal.enums.OrderStatus;
import com.example.pet_care_booking.repository.OrderRepository;
import com.example.pet_care_booking.repository.PaymentRepository;
import com.example.pet_care_booking.service.impl.VnPayService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/vnpay")
@RequiredArgsConstructor
public class VnPayController {
    private final OrderRepository orderRepository;
    private final VnPayService vnPayService;
    private final PaymentRepository paymentRepository;
    private final String URL = "http://localhost:5173/";
    @GetMapping("/create-payment")
    public String createPayment(@RequestParam String orderId, @RequestParam long amount) throws Exception {
        return vnPayService.createPaymentUrl(orderId, amount);
    }

    @GetMapping("/return")
    public void vnPayReturn(@RequestParam Map<String, String> params,
                            HttpServletResponse response) throws Exception {
        if (!vnPayService.validateSecureHash(params)) {
            response.sendRedirect("http://localhost:5173/payment-result?status=failed&message=invalid_signature");
            return;
        }

        String respCode = params.get("vnp_ResponseCode");
        String orderId = params.get("vnp_TxnRef");

        Order order = orderRepository.findById(Long.parseLong(orderId))
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));
        Payments payment = paymentRepository.findByOrderId(order.getId())
                .orElseThrow(() -> new IllegalArgumentException("Payment not found"));

        switch (respCode) {
            case "00":
                payment.setStatus(PaymentStatus.SUCCESS);
                break;
            case "24":
                payment.setStatus(PaymentStatus.CANCELLED);
                order.setStatus(OrderStatus.CANCELED);
                break;
            default:
                payment.setStatus(PaymentStatus.FAILED);
                order.setStatus(OrderStatus.FAILED_PAYMENT);
                break;
        }

        paymentRepository.save(payment);
        orderRepository.save(order);
        // 🔥 Redirect sang frontend (React)
        String redirectUrl = "http://localhost:5173/payment-result"
                + "?orderId=" + orderId
                + "&status=" + respCode;
        response.sendRedirect(redirectUrl);
    }


}
