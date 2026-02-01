package com.example.storeapi.repository;

import com.example.storeapi.domain.Order;
import com.example.storeapi.domain.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;

public interface OrderRepository extends JpaRepository<Order, Long> {
    boolean existsByCustomerId(Long customerId);

    @Query("select count(o) from Order o where o.customer.id = :customerId")
    long countByCustomerId(@Param("customerId") Long customerId);

    @Query("select count(o) from Order o where o.customer.id = :customerId and o.status not in :closedStatuses")
    long countOpenByCustomerId(@Param("customerId") Long customerId, @Param("closedStatuses") Collection<OrderStatus> closedStatuses);
}
