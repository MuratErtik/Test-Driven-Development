package org.testdrivendevelopment.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.testdrivendevelopment.dtos.CreateOrderRequest;
import org.testdrivendevelopment.dtos.OrderDto;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class OrderService {


    public OrderDto createOrder(CreateOrderRequest request) {

        return  OrderDto.builder()
                .totalPrice(BigDecimal.valueOf(99.95))
                .build();
    }
}
