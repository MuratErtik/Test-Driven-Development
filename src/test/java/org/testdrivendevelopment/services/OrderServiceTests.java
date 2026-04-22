package org.testdrivendevelopment.services;

import org.junit.jupiter.api.Test;
import org.testdrivendevelopment.dtos.CreateOrderRequest;
import org.testdrivendevelopment.dtos.OrderDto;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static org.assertj.core.api.BDDAssertions.then;

public class OrderServiceTests {

    @Test
    public void it_should_create_a_new_order() {

        //given
        OrderService service = new OrderService();
        CreateOrderRequest request = CreateOrderRequest.builder()
                .amount(5)
                .customerCode("code1")
                .unitPrice(BigDecimal.valueOf(19.99))
                .build();




        //when

        OrderDto order = service.createOrder(request);


        //then

        then(order).isNotNull(); // at first createOrder method returns null that's mean you got a failed test
        //so you can start to develop prod code


        then(order.getTotalPrice()).isEqualTo(BigDecimal.valueOf(19.99).multiply(BigDecimal.valueOf(5)).setScale(2, RoundingMode.HALF_UP));
    }
}
