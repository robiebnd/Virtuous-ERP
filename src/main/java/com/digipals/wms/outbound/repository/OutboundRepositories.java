package com.digipals.wms.outbound.repository;

import com.digipals.wms.outbound.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;

public interface CustomerRepository extends JpaRepository<Customer, UUID> {
    Optional<Customer> findByCustomerNumber(String customerNumber);
    boolean existsByCustomerNumber(String customerNumber);
}

interface SalesOrderRepository extends JpaRepository<SalesOrder, UUID> {
    Optional<SalesOrder> findByOrderNumber(String orderNumber);
    List<SalesOrder> findByCustomerId(UUID customerId);
}

interface SalesOrderLineRepository extends JpaRepository<SalesOrderLine, UUID> { }

interface OutboundDeliveryRepository extends JpaRepository<OutboundDelivery, UUID> {
    Optional<OutboundDelivery> findByDeliveryNumber(String deliveryNumber);
    List<OutboundDelivery> findBySalesOrderId(UUID salesOrderId);
}

interface OutboundDeliveryLineRepository extends JpaRepository<OutboundDeliveryLine, UUID> { }

interface CustomerInvoiceRepository extends JpaRepository<CustomerInvoice, UUID> {
    Optional<CustomerInvoice> findByInvoiceNumber(String invoiceNumber);
    List<CustomerInvoice> findByCustomerId(UUID customerId);
}

interface CustomerPaymentRepository extends JpaRepository<CustomerPayment, UUID> {
    Optional<CustomerPayment> findByPaymentNumber(String paymentNumber);
}
