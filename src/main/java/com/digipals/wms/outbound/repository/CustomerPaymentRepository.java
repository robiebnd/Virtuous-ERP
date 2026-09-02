package com.digipals.wms.outbound.repository;
import com.digipals.wms.outbound.entity.CustomerPayment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;
public interface CustomerPaymentRepository extends JpaRepository<CustomerPayment, UUID> {
    Optional<CustomerPayment> findByPaymentNumber(String paymentNumber);
}
