package com.codeWithProject.ecom.services.admin.adminOrder;

import com.codeWithProject.ecom.dto.OrderDto;
import com.codeWithProject.ecom.entity.Order;
import com.codeWithProject.ecom.enums.OrderStatus;
import com.codeWithProject.ecom.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminOrderServiceImpl implements AdminOrderService {


    private  final OrderRepository orderRepository;
    public List<OrderDto> getAllPlaceOrers(){

     //   Long userId=null;
       // List<Order> orderList=orderRepository.findAllByOrderStatusInn(userId, List.of(OrderStatus.Placed,OrderStatus.Shipped, OrderStatus.Delivered));
      // return orderList.stream().map(Order::getOrderDto).collect(Collectors.toList());
        return null;
    }

public OrderDto changeOrderStatus(Long orderId, String status){
    Optional<Order> optionalOrder=orderRepository.findById(orderId);

    if(optionalOrder.isPresent()){
        Order order =optionalOrder.get();

        if(Objects.equals(status, "Shipped")){
             order.setOrderStatus(OrderStatus.Shipped);
        }else if(Objects.equals(status, "Delivred")){
            order.setOrderStatus(OrderStatus.Delivered);
        }

        return orderRepository.save(order).getOrderDto();
    }
    return  null;

}
}
