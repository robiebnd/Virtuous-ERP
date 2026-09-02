package com.digipals.wms.outbound.repository;
import com.digipals.wms.outbound.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;
public interface CustomerRepository extends JpaRepository<Customer, UUID> {
    Optional<Customer> findByCustomerNumber(String customerNumber);
    boolean existsByCustomerNumber(String customerNumber);
}
