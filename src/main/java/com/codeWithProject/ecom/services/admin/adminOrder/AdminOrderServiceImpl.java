package com.codeWithProject.ecom.services.admin.adminOrder;

import com.codeWithProject.ecom.dto.AnalytecsResponse;
import com.codeWithProject.ecom.dto.OrderDto;
import com.codeWithProject.ecom.entity.Order;
import com.codeWithProject.ecom.enums.OrderStatus;
import com.codeWithProject.ecom.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;


@Service
@RequiredArgsConstructor
public class AdminOrderServiceImpl implements AdminOrderService {


    private final OrderRepository orderRepository;

    public List<OrderDto> getAllPlaceOrers() {

        //   Long userId=null;
        // List<Order> orderList=orderRepository.findAllByOrderStatusInn(userId, List.of(OrderStatus.Placed,OrderStatus.Shipped, OrderStatus.Delivered));
        // return orderList.stream().map(Order::getOrderDto).collect(Collectors.toList());
        return null;
    }

    public OrderDto changeOrderStatus(Long orderId, String status) {
        Optional<Order> optionalOrder = orderRepository.findById(orderId);

        if (optionalOrder.isPresent()) {
            Order order = optionalOrder.get();

            if (Objects.equals(status, "Shipped")) {
                order.setOrderStatus(OrderStatus.Shipped);
            } else if (Objects.equals(status, "Delivered")) {
                order.setOrderStatus(OrderStatus.Delivered);
            }

            return orderRepository.save(order).getOrderDto();
        }
        return null;

    }

    public AnalytecsResponse calculateAnalytics(){
        LocalDate currentDate=LocalDate.now();
        LocalDate previousMonthDate=currentDate.minusMonths(1);

        Long currentMonthOrders= getTotalOrdersForMonth(currentDate.getDayOfMonth(), currentDate.getYear());
        Long previousMonthOrders= getTotalOrdersForMonth(previousMonthDate.getMonthValue(),previousMonthDate.getYear());

        Long currentMonthEarnings = getTotalEarningsForMonth(currentDate.getDayOfMonth(), currentDate.getYear());
        Long previousMonthEarnings=getTotalEarningsForMonth(previousMonthDate.getMonthValue(),previousMonthDate.getYear());

        Long placed=orderRepository.countByOrderStatus(OrderStatus.Placed);
        Long shipped=orderRepository.countByOrderStatus(OrderStatus.Shipped);
        Long delivered=orderRepository.countByOrderStatus(OrderStatus.Delivered);

        return new AnalytecsResponse(placed, shipped, delivered, currentMonthOrders,previousMonthOrders,currentMonthEarnings,previousMonthEarnings);


    }

    public  Long getTotalOrdersForMonth(int month, int year){
        Calendar calendar= Calendar.getInstance();
        calendar.set(Calendar.YEAR,year);
        calendar.set(Calendar.MONTH,month-1);
        calendar.set(Calendar.DAY_OF_MONTH,1);
        calendar.set(Calendar.MINUTE,0);
        calendar.set(Calendar.SECOND,0);

        Date startOfMonth=calendar.getTime();

        calendar.set(Calendar.DAY_OF_MONTH,calendar.getActualMaximum(Calendar.DAY_OF_MONTH));

        calendar.set(Calendar.HOUR_OF_DAY,23);
        calendar.set(Calendar.MINUTE,59);
        calendar.set(Calendar.SECOND,59);

        Date endOfMonth=calendar.getTime();

        List<Order> orders=orderRepository.findByDateBetweenAndStatus(startOfMonth,endOfMonth, OrderStatus.Delivered);

        return (long) orders.size();
    }

    public  Long getTotalEarningsForMonth(int month, int year){
        Calendar calendar= Calendar.getInstance();
        calendar.set(Calendar.YEAR,year);
        calendar.set(Calendar.MONTH,month-1);
        calendar.set(Calendar.DAY_OF_MONTH,1);
        calendar.set(Calendar.MINUTE,0);
        calendar.set(Calendar.SECOND,0);

        Date startOfMonth=calendar.getTime();

        calendar.set(Calendar.DAY_OF_MONTH,calendar.getActualMaximum(Calendar.DAY_OF_MONTH));

        calendar.set(Calendar.HOUR_OF_DAY,23);
        calendar.set(Calendar.MINUTE,59);
        calendar.set(Calendar.SECOND,59);

        Date endOfMonth=calendar.getTime();

        List<Order> orders=orderRepository.findByDateBetweenAndStatus(startOfMonth,endOfMonth, OrderStatus.Delivered);

      Long sum=0L;
      for(Order order : orders){
          sum +=order.getAmount();
      }
      return  sum;
    }
}
