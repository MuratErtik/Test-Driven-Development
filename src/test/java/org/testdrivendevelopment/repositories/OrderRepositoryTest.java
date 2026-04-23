package org.testdrivendevelopment.repositories;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testdrivendevelopment.entites.Order;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.BDDAssertions.then;
import static org.junit.jupiter.api.Assertions.*;


//we will test is layer of the data JPA

@Testcontainers
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE) // it uses to embedded db but we do not want that
class OrderRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private OrderRepository orderRepository;

    @Container
    public static MySQLContainer<?> mysqlContainer = new MySQLContainer<>("mysql:8.0");


    @Test
    public void it_should_find_order(){

        //given
        Order order1 = Order.builder()
                .totalPrice(BigDecimal.valueOf(11))
                .build();

        Order order2 = Order.builder()
                .totalPrice(BigDecimal.valueOf(12))
                .build();

        Object id1 = entityManager.persistAndGetId(order1);
        Object id2 = entityManager.persistAndGetId(order2);
        entityManager.flush();

        //when
        List<Order> orders = orderRepository.findAll();

        //then
        then(orders).isNotEmpty();
         Order o1 = orders.get(0);
         Order o2 = orders.get(1);
         then(o1.getId()).isEqualTo(id1);
         then(o2.getId()).isEqualTo(id2);
    }

    //to make sure is it connect to test container or not
    @DynamicPropertySource
    public static void dynamicProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", mysqlContainer::getJdbcUrl);
        registry.add("spring.datasource.username", mysqlContainer::getUsername);
        registry.add("spring.datasource.password", mysqlContainer::getPassword);
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
        registry.add("spring.datasource.driver-class-name", mysqlContainer::getDriverClassName);
    }






}