package com.codeWithProject.ecom.services.cart;

import com.codeWithProject.ecom.dto.AddProductInCartDto;
import com.codeWithProject.ecom.dto.OrderDto;
import org.springframework.http.ResponseEntity;

public interface CartService {

    ResponseEntity<?> addProductToCrt(AddProductInCartDto addProductInCartDto);

    OrderDto getCartByUserId(Long userId);

    OrderDto applyCoupon(Long userId, String code);

    OrderDto increaseProductQuantity(AddProductInCartDto addProductInCartDto);
}
