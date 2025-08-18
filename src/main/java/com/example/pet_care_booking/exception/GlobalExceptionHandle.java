package com.example.pet_care_booking.exception;

import com.example.pet_care_booking.dto.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandle {

   private static final String MIN_ATTRIBUTE = "min";

   @ExceptionHandler(value = Exception.class)
   ResponseEntity<Map<String, String>> handleRuntimeException(Exception e, HttpServletRequest request) {
      Map<String, String> map = new HashMap<>();
      map.put("Error", e.getMessage());
      return ResponseEntity.badRequest().body(map);
   }

   @ExceptionHandler(value = AppException.class)
   ResponseEntity<ApiResponse> handleAppException(AppException e) {
      ErrorCode errorCode = e.getErrorCode();

      ApiResponse apiResponse = new ApiResponse();
      apiResponse.setCode(errorCode.getCode());
      apiResponse.setMessage(errorCode.getMessage());

      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiResponse);
   }


   @ExceptionHandler(value = MethodArgumentNotValidException.class)
   ResponseEntity<ApiResponse> handleAppException(MethodArgumentNotValidException e) {
      FieldError fieldError = e.getFieldError();
      String enumKey = (fieldError != null) ? fieldError.getDefaultMessage() : ErrorCode.INVALID_KEY.name();

      ErrorCode errorCode = ErrorCode.INVALID_KEY;
      Map<String, Object> attributes = null;

      try {
         errorCode = ErrorCode.valueOf(enumKey);

         var constraintViolations = e.getBindingResult()
                .getAllErrors().stream()
                .filter(error -> {
                   error.unwrap(ConstraintViolationException.class);
                   return true;
                })
                .findFirst()
                .map(error -> error.unwrap(ConstraintViolationException.class))
                .orElse(null);

         if (constraintViolations != null) {
            Set<ConstraintViolation<?>> violations = constraintViolations.getConstraintViolations();
            if (!violations.isEmpty()) {
               attributes = violations.iterator().next().getConstraintDescriptor().getAttributes();
               log.info(attributes.toString());
            }
         }
      } catch (IllegalArgumentException exception) {
         log.warn("Invalid error code: {}", enumKey, exception);
      }

      ApiResponse apiResponse = new ApiResponse();
      apiResponse.setCode(errorCode.getCode());
      apiResponse.setMessage(Objects.nonNull(attributes) ?
             mapAttribute(errorCode.getMessage(), attributes)
             : errorCode.getMessage());

      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiResponse);
   }

   private String mapAttribute(String message, Map<String, Object> attributes) {
      String minValue = attributes.get(MIN_ATTRIBUTE).toString();

      return message.replace("{" + MIN_ATTRIBUTE + "}", minValue);
   }
}

