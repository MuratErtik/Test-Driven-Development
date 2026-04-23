package org.testdrivendevelopment.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.testdrivendevelopment.entites.Order;

public interface OrderRepository extends JpaRepository<Order, Long> {

}
