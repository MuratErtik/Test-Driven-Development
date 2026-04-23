package org.testdrivendevelopment.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.testdrivendevelopment.clients.PaymentClient;
import org.testdrivendevelopment.dtos.CreateOrderRequest;
import org.testdrivendevelopment.dtos.OrderDto;
import org.testdrivendevelopment.entites.Order;
import org.testdrivendevelopment.repositories.OrderRepository;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;

    private final PaymentClient paymentClient;

    public OrderDto createOrder(CreateOrderRequest request) {

        BigDecimal totalPrice = request.getUnitPrice().multiply(BigDecimal.valueOf(request.getAmount()));

        Order order = Order.builder()
                .totalPrice(totalPrice)
                .build();

        paymentClient.pay(order);

        Order savedOrder = orderRepository.save(order);


        return  OrderDto.builder()
                .totalPrice(totalPrice)
                .orderId(savedOrder.getId())
                .build();
    }
}
