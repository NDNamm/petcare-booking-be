package com.example.pet_care_booking.service.impl;

import com.example.pet_care_booking.config.VnPayConfig;
import com.example.pet_care_booking.enums.PaymentStatus;
import com.example.pet_care_booking.modal.Order;
import com.example.pet_care_booking.modal.Payments;
import com.example.pet_care_booking.modal.enums.PaymentMethod;
import com.example.pet_care_booking.repository.OrderRepository;
import com.example.pet_care_booking.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class VnPayService {

    private final VnPayConfig vnPayConfig;
    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;

    public String createPaymentUrl(String orderId, long amount) {
        Order order = orderRepository.findById(Long.valueOf(orderId))
                .orElseThrow(() -> new IllegalArgumentException("Not found orderId: " + orderId));
//        Payments payments = new Payments();
//        payments.setPaymentMethod(PaymentMethod.VNPAY);
//        payments.setPaymentDate(LocalDate.now());
//        payments.setAmount(BigDecimal.valueOf(amount));
//        payments.setStatus(PaymentStatus.PENDING);
//        payments.setOrder(order);
        try {
            Map<String, String> params = new HashMap<>();
            params.put("vnp_Version", "2.1.0");
            params.put("vnp_Command", "pay");
            params.put("vnp_TmnCode", vnPayConfig.getTmnCode());
            params.put("vnp_Amount", String.valueOf(amount * 100));
            params.put("vnp_CurrCode", "VND");
            params.put("vnp_TxnRef", orderId);
            params.put("vnp_OrderInfo", "Thanh toan don hang " + orderId);
            params.put("vnp_OrderType", "billpayment");
            params.put("vnp_ReturnUrl", vnPayConfig.getReturnUrl());
            params.put("vnp_CreateDate", new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
            params.put("vnp_IpAddr", "127.0.0.1");
            params.put("vnp_Locale", "vn");

            // Sắp xếp key theo alphabet
            List<String> fieldNames = new ArrayList<>(params.keySet());
            Collections.sort(fieldNames);

            // Tạo hashData (key KHÔNG encode, value CÓ encode)
            StringBuilder hashData = new StringBuilder();
            StringBuilder query = new StringBuilder();

            for (String fieldName : fieldNames) {
                String fieldValue = params.get(fieldName);
                if (fieldValue != null && fieldValue.length() > 0) {
                    // Build hash data - KEY không encode, VALUE có encode
                    if (hashData.length() > 0) {
                        hashData.append('&');
                    }
                    hashData.append(fieldName)
                            .append('=')
                            .append(URLEncoder.encode(fieldValue, "UTF-8"));
                }
            }

            // Tạo secure hash
            String vnpSecureHash = hmacSHA512(vnPayConfig.getHashSecret(), hashData.toString());

            // Build query URL (cả key và value đều encode)
            for (String fieldName : fieldNames) {
                String fieldValue = params.get(fieldName);
                if (fieldValue != null && fieldValue.length() > 0) {
                    if (query.length() > 0) {
                        query.append('&');
                    }
                    query.append(URLEncoder.encode(fieldName, "UTF-8"))
                            .append('=')
                            .append(URLEncoder.encode(fieldValue, "UTF-8"));
                }
            }

            // Thêm secure hash vào query (vnp_SecureHash KHÔNG tham gia vào hash)
            query.append("&vnp_SecureHash=").append(vnpSecureHash);

            String paymentUrl = vnPayConfig.getUrl() + "?" + query.toString();
//            paymentRepository.save(payments);
            return paymentUrl;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean validateSecureHash(Map<String, String> params) throws Exception {
        String vnp_SecureHash = params.get("vnp_SecureHash");
        if (vnp_SecureHash == null) return false;

        // Clone params để không ảnh hưởng dữ liệu gốc
        Map<String, String> validationParams = new HashMap<>(params);
        validationParams.remove("vnp_SecureHash");
        validationParams.remove("vnp_SecureHashType");

        // Sort theo key
        List<String> fieldNames = new ArrayList<>(validationParams.keySet());
        Collections.sort(fieldNames);

        // Build hashData: key=value (value phải encode UTF-8)
        StringBuilder hashData = new StringBuilder();
        for (String fieldName : fieldNames) {
            String fieldValue = validationParams.get(fieldName);
            if (fieldValue != null && fieldValue.length() > 0) {
                if (hashData.length() > 0) hashData.append('&');
                hashData.append(fieldName).append('=')
                        .append(URLEncoder.encode(fieldValue, "UTF-8"));
            }
        }

        String calculatedHash = hmacSHA512(vnPayConfig.getHashSecret(), hashData.toString());
        return calculatedHash.equalsIgnoreCase(vnp_SecureHash);
    }


    private static String hmacSHA512(String key, String data) throws Exception {
        Mac hmac = Mac.getInstance("HmacSHA512");
        SecretKeySpec secretKey = new SecretKeySpec(key.getBytes("UTF-8"), "HmacSHA512");
        hmac.init(secretKey);
        byte[] hashBytes = hmac.doFinal(data.getBytes("UTF-8"));
        StringBuilder sb = new StringBuilder(2 * hashBytes.length);
        for (byte b : hashBytes) {
            sb.append(String.format("%02x", b & 0xff));
        }
        return sb.toString();
    }
}