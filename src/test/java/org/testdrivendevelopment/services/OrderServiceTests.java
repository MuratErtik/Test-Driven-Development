package org.testdrivendevelopment.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testdrivendevelopment.dtos.CreateOrderRequest;
import org.testdrivendevelopment.dtos.OrderDto;
import org.testdrivendevelopment.entites.Order;
import org.testdrivendevelopment.repositories.OrderRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.stream.Stream;

import static org.assertj.core.api.BDDAssertions.then;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTests {

    @InjectMocks
    private OrderService orderService;

    @Mock
    private OrderRepository orderRepository;

    // after the mocking we dont need anymore to this method
//    @BeforeEach
//    public void beforeEach() {
//        orderService = new OrderService(orderRepository);
//        OrderRepository orderRepository = new OrderRepository() {
//        }
//
//    }

    @Test
    public void it_should_create_a_new_order_with_5_items() {

        //given

        CreateOrderRequest request = CreateOrderRequest.builder()
                .amount(5)
                .customerCode("code1")
                .unitPrice(BigDecimal.valueOf(19.99))
                .build();




        //when

        OrderDto order = orderService.createOrder(request);


        //then

        then(order).isNotNull(); // at first createOrder method returns null that's mean you got a failed test
        //so you can start to develop prod code


        then(order.getTotalPrice()).isEqualTo(BigDecimal.valueOf(19.99).multiply(BigDecimal.valueOf(5)).setScale(2, RoundingMode.HALF_UP));
    }

    @Test
    public void it_should_create_a_new_order_with_10_items() {

        //given

        CreateOrderRequest request = CreateOrderRequest.builder()
                .amount(10)
                .customerCode("code1")
                .unitPrice(BigDecimal.valueOf(15))
                .build();


        //when

        OrderDto order = orderService.createOrder(request);

        //then

        then(order).isNotNull(); // at first createOrder method returns null that's mean you got a failed test
        //so you can start to develop prod code

        then(order.getTotalPrice()).isEqualTo(BigDecimal.valueOf(150));
    }

    public static Stream<Arguments> order_requests() {

        return Stream.of(
                Arguments.of("code1",5,BigDecimal.valueOf(19.99),BigDecimal.valueOf(99.95)),
                Arguments.of("code2",10,BigDecimal.valueOf(15),BigDecimal.valueOf(150))
        );
    }

    // Having 2 methods which are names different but logics are same that's why using ParameterizedTest
    @ParameterizedTest
    @MethodSource("order_requests")
    public void it_should_create_a_new_orders(String productCode, Integer amount, BigDecimal unitPrice, BigDecimal totalPrice ) {

        //given
        CreateOrderRequest request = CreateOrderRequest.builder()
                .amount(amount)
                .customerCode(productCode)
                .unitPrice(unitPrice)
                .build();

        //after the basic return OrderDto We are able to extend this method like check of Db returns or other things

        Order order = new Order();
        order.setId(111L);

        when(orderRepository.save(any())).thenReturn(order);


        //when
        OrderDto orderDto = orderService.createOrder(request);

        //then
        then(orderDto.getTotalPrice()).isEqualTo(totalPrice);
        
    }
}
