package com.codeWithProject.ecom.services.cart;

import com.codeWithProject.ecom.dto.AddProductInCartDto;
import com.codeWithProject.ecom.dto.CartItemsDto;
import com.codeWithProject.ecom.dto.OrderDto;
import com.codeWithProject.ecom.entity.*;
import com.codeWithProject.ecom.enums.OrderStatus;
import com.codeWithProject.ecom.exceptions.ValidationException;
import com.codeWithProject.ecom.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CartServiceImp  implements  CartService{

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CartItemRepository cartItemRepository;
    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CouponRepository couponRepository;

    public ResponseEntity<?>  addProductToCrt(AddProductInCartDto addProductInCartDto){
        Order activeOrder = orderRepository.findByUserIdAndOrderStatus(addProductInCartDto.getProductId(), OrderStatus.Pending);
        Optional<CartItems> optionalCartItems=cartItemRepository.findByProductIdAndOrderIdAndUserId(addProductInCartDto.getProductId(), activeOrder.getId(), addProductInCartDto.getUserId());


        if(optionalCartItems.isPresent()){
            return  ResponseEntity.status(HttpStatus.CONFLICT).body(null);
        }else{
            Optional<Product> optionalProduct=productRepository.findById(addProductInCartDto.getProductId());
            Optional<User> optionalUser=userRepository.findById(addProductInCartDto.getUserId());

            if(optionalProduct.isPresent() && optionalUser.isPresent()){
                CartItems cart= new CartItems();
                cart.setProduct(optionalProduct.get());
                cart.setPrice(optionalProduct.get().getPrice());
                cart.setQuantity(1L);
                cart.setUser(optionalUser.get());
                cart.setOrder(activeOrder);
                CartItems updatedCart=cartItemRepository.save(cart);

                activeOrder.setTotalAmount(activeOrder.getAmount() + cart.getPrice());
                activeOrder.setAmount(activeOrder.getAmount() + cart.getPrice());
                activeOrder.getCartItems().add(cart);
                orderRepository.save(activeOrder);
                return  ResponseEntity.status(HttpStatus.CREATED).body(cart);
            }else{
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User or product not found");
            }
        }
    }
       public OrderDto getCartByUserId(Long userId){
           Order activeOrder = orderRepository.findByUserIdAndOrderStatus(userId, OrderStatus.Pending);

           List<CartItemsDto> cartItemsDtoList=activeOrder.getCartItems().stream().map(CartItems::getCartDto).collect(Collectors.toList());
           OrderDto  orderDto=new OrderDto();
           orderDto.setAmount(activeOrder.getAmount());
           orderDto.setId(activeOrder.getId());
           orderDto.setOrderStatus(activeOrder.getOrderStatus());
           orderDto.setDiscount(activeOrder.getDiscount());
           orderDto.setTotalAmount(activeOrder.getTotalAmount());
           orderDto.setCartItems(cartItemsDtoList);
           if(activeOrder.getCoupon() != null){
               orderDto.setCouponName(activeOrder.getCoupon().getName());
           }

           return orderDto;
    }

    public OrderDto applyCoupon(Long userId, String code){
        Order activeOrder = orderRepository.findByUserIdAndOrderStatus(userId, OrderStatus.Pending);
        Coupon coupon=couponRepository.findByCode(code).orElseThrow(()-> new ValidationException("Coupon not found"));

        if(couponIsExpired(coupon)){
            throw new ValidationException("Coupon has expired.");
        }

        double discountAmount=  ((coupon.getDiscount()/100.0) * activeOrder.getTotalAmount());
        double netAmount=activeOrder.getTotalAmount() -  discountAmount;

         activeOrder.setAmount((long) netAmount);
         activeOrder.setDiscount((long) discountAmount);
         activeOrder.setCoupon(coupon);
         orderRepository.save(activeOrder);

         return activeOrder.getOrderDto();
    }
    private boolean couponIsExpired(Coupon coupon){
        Date currentdate = new Date();
        Date expirationDate = coupon.getExpirationDate();

        return  expirationDate != null && currentdate.after(expirationDate);
    }

    public OrderDto increaseProductQuantity(AddProductInCartDto addProductInCartDto){
        Order activeOrder = orderRepository.findByUserIdAndOrderStatus(addProductInCartDto.getUserId(), OrderStatus.Pending);
        Optional<Product> optionalProduct=productRepository.findById(addProductInCartDto.getProductId());

        Optional<CartItems> optionalCartItems=cartItemRepository.findByProductIdAndOrderIdAndUserId(
                addProductInCartDto.getProductId(), activeOrder.getId(), addProductInCartDto.getUserId()
        );

        if(optionalProduct.isPresent() && optionalCartItems.isPresent()){
              CartItems cartItems=optionalCartItems.get();
              Product product=optionalProduct.get();

              activeOrder.setAmount(activeOrder.getAmount() + product.getPrice());
              activeOrder.setTotalAmount(activeOrder.getTotalAmount() + product.getPrice());

              cartItems.setQuantity(cartItems.getQuantity() + 1);

              if(activeOrder.getCoupon() != null){
                  double discountAmount=  ((activeOrder.getCoupon().getDiscount()/100.0) * activeOrder.getTotalAmount());
                  double netAmount=activeOrder.getTotalAmount() -  discountAmount;

                  activeOrder.setAmount((long) netAmount);
                  activeOrder.setDiscount((long) discountAmount);
              }

              cartItemRepository.save(cartItems);
              orderRepository.save(activeOrder);
              return activeOrder.getOrderDto();
        }
        return null;
    }
}
