package com.codeWithProject.ecom.services.customer.cart;

import com.codeWithProject.ecom.dto.AddProductInCartDto;
import com.codeWithProject.ecom.dto.OrderDto;
import com.codeWithProject.ecom.dto.PlaceOrderDto;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.UUID;

public interface CartService {

    ResponseEntity<?> addProductToCrt(AddProductInCartDto addProductInCartDto);

    OrderDto getCartByUserId(Long userId);

    OrderDto applyCoupon(Long userId, String code);

    OrderDto increaseProductQuantity(AddProductInCartDto addProductInCartDto);

    OrderDto decreaseProductQuantity(AddProductInCartDto addProductInCartDto);

    OrderDto placeOrder(PlaceOrderDto placeOrderDto);

    List<OrderDto> getMyPlaceOrders(Long userId);

    OrderDto searchOrderByTrackingId(UUID trackingId);
}
