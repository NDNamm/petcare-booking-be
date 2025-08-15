package com.example.pet_care_booking.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;


@Getter
@RequiredArgsConstructor
public enum ErrorCode {

   INVALID_KEY(9999, "ERROR", HttpStatus.BAD_REQUEST),

   EMAIL_EXISTED(1001, "Email already exists", HttpStatus.BAD_REQUEST),
   PHONE_EXISTED(1009, "Phone already exists", HttpStatus.BAD_REQUEST),
   PASSWORD_NOT_MATCH(1002, "Passwords do not match", HttpStatus.BAD_REQUEST),
   USER_NOT_FOUND(1003, "User not found", HttpStatus.NOT_FOUND),
    USER_NAME_EXIST(1011, "username exist", HttpStatus.BAD_REQUEST),
   USERNAME_INVALID(1004, "Full Name must be at least 5 characters", HttpStatus.BAD_REQUEST),
   PASSWORD_INVALID(1005, "PASSWORD must be at least 8 characters", HttpStatus.BAD_REQUEST),
   PHONE_INVALID(1006, "Phone must be at least 10 characters", HttpStatus.BAD_REQUEST),
   EMAIL_INVALID(1008, "Email must be at least 5 characters", HttpStatus.BAD_REQUEST),

   ROLE_NOT_EXISTED(1010, "Role not existed in system", HttpStatus.BAD_REQUEST),

   // CATEGORY
   CATEGORY_NOT_FOUND(2001, "Category not found", HttpStatus.NOT_FOUND),
   CATEGORY_NAME_EXISTED(2002, "Category name already exists", HttpStatus.BAD_REQUEST),
   CATEGORY_NAME_INVALID(2003, "Category name is invalid", HttpStatus.BAD_REQUEST),

   // PRODUCT
   PRODUCT_NOT_FOUND(3001, "Product not found", HttpStatus.NOT_FOUND),
   PRODUCT_NAME_EXISTED(3002, "Product name already exists", HttpStatus.BAD_REQUEST),
   PRODUCT_PRICE_INVALID(3003, "Product price must be greater than 0", HttpStatus.BAD_REQUEST),
   PRODUCT_STATUS_INVALID(3004, "Invalid product status", HttpStatus.BAD_REQUEST),
   PRODUCT_NAME_INVALID(3005, "Product name is invalid", HttpStatus.BAD_REQUEST),
   PRODUCT_OUT_OF_STOCK(3006,"The product is out of stock or discontinued", HttpStatus.BAD_REQUEST),

   //Images
   UPDATE_IMAGE_FAIL(4001, "Error update image", HttpStatus.BAD_REQUEST),
   DELETE_IMAGE_FAIL(4002, "Error delete image", HttpStatus.BAD_REQUEST),

   //Cart_Item
   CART_ITEM_NOT_FOUND(5001, "Cart_item not found", HttpStatus.NOT_FOUND),
   UNAUTHORIZED_OPERATION(5001, "You are not authorized to modify this cart item", HttpStatus.UNAUTHORIZED),

   //Cart
   CART_NOT_FOUND(6001, "Cart not found", HttpStatus.NOT_FOUND),

   //Session
   SESSION_NOT_FOUND(7001, "Session not found", HttpStatus.NOT_FOUND),

   //Order
   NAME_ORDER_INVALID(8001, "Name order must be at least 2 characters", HttpStatus.BAD_REQUEST),
   NO_ORDER(8002, "You have no orders", HttpStatus.BAD_REQUEST),
   ORDER_NOT_FOUND(8003, "Order not found", HttpStatus.NOT_FOUND),
   ORDER_CANNOT_BE_MODIFIED(8004, "Order cannot be modified", HttpStatus.BAD_REQUEST),
   //Payment
   INVALID_PAYMENT_METHOD(9001, "Invalid payment method", HttpStatus.BAD_REQUEST),
   NOT_PAYMENT_METHOD(9002, "Payment method not found", HttpStatus.NOT_FOUND),

   //Authentication
   NOT_FOUND(10001, "Authentication credentials or session not found", HttpStatus.NOT_FOUND),
   UNAUTHORIZED(10002,"You are not authorized to perform this action", HttpStatus.UNAUTHORIZED),
   ;

   private final int code;
   private final String message;
   private final HttpStatus status;

}
