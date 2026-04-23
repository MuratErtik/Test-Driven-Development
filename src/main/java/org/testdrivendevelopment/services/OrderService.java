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

        BigDecimal totalPrice = request.getUnitPrice().multiply(BigDecimal.valueOf(request.getAmount()));


        return  OrderDto.builder()
                .totalPrice(totalPrice)
                .build();
    }
}
