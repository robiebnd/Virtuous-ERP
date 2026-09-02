package com.digipals.wms.salesorder.service;

import com.digipals.wms.common.exception.ResourceNotFoundException;
import com.digipals.wms.salesorder.dto.CreateSalesOrderRequest;
import com.digipals.wms.salesorder.dto.CreateSalesOrderItemRequest;
import com.digipals.wms.salesorder.entity.SalesOrder;
import com.digipals.wms.salesorder.entity.SalesOrderItem;
import com.digipals.wms.salesorder.entity.SalesOrderStatus;
import com.digipals.wms.salesorder.repository.SalesOrderRepository;
import com.digipals.wms.salesorder.sap.SapSalesOrderClient;
import com.digipals.wms.salesorder.sap.SapSalesOrderItem;
import com.digipals.wms.salesorder.sap.SapSalesOrderRequest;
import com.digipals.wms.salesorder.sap.SapSalesOrderResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SalesOrderServiceImpl implements SalesOrderService {

    private final SalesOrderRepository salesOrderRepository;
    private final SapSalesOrderClient sapSalesOrderClient;

    @Value("${sap.sales-order.enabled:false}")
    private boolean sapIntegrationEnabled;

    @Override
    @Transactional
    public SalesOrder create(CreateSalesOrderRequest request) {
        SalesOrder order = SalesOrder.builder()
                .orderNumber(generateOrderNumber())
                .customerCode(request.customerCode().trim())
                .salesOrganization(request.salesOrganization().trim())
                .distributionChannel(request.distributionChannel().trim())
                .division(request.division().trim())
                .remarks(request.remarks())
                .status(sapIntegrationEnabled ? SalesOrderStatus.PENDING_SAP : SalesOrderStatus.DRAFT)
                .totalAmount(BigDecimal.ZERO)
                .build();

        int itemNumber = 10;
        BigDecimal total = BigDecimal.ZERO;

        for (CreateSalesOrderItemRequest itemRequest : request.items()) {
            BigDecimal unitPrice = itemRequest.unitPrice() == null
                    ? BigDecimal.ZERO
                    : itemRequest.unitPrice();
            BigDecimal netValue = unitPrice.multiply(itemRequest.quantity());

            SalesOrderItem item = SalesOrderItem.builder()
                    .itemNumber(itemNumber)
                    .materialCode(itemRequest.materialCode().trim())
                    .quantity(itemRequest.quantity())
                    .unitPrice(unitPrice)
                    .netValue(netValue)
                    .build();

            order.addItem(item);
            total = total.add(netValue);
            itemNumber += 10;
        }

        order.setTotalAmount(total);
        SalesOrder saved = salesOrderRepository.save(order);

        if (!sapIntegrationEnabled) {
            return saved;
        }

        try {
            SapSalesOrderRequest sapRequest = new SapSalesOrderRequest(
                    saved.getCustomerCode(),
                    saved.getSalesOrganization(),
                    saved.getDistributionChannel(),
                    saved.getDivision(),
                    saved.getItems().stream()
                            .map(item -> new SapSalesOrderItem(
                                    item.getMaterialCode(),
                                    item.getQuantity(),
                                    item.getUnitPrice()))
                            .toList());

            SapSalesOrderResponse sapResponse = sapSalesOrderClient.createSalesOrder(sapRequest);
            saved.setSapOrderNumber(sapResponse.resolvedSalesOrderNumber());
            saved.setStatus(SalesOrderStatus.CREATED);
            return salesOrderRepository.save(saved);
        } catch (RuntimeException ex) {
            saved.setStatus(SalesOrderStatus.SAP_ERROR);
            salesOrderRepository.save(saved);
            throw ex;
        }
    }

    @Override
    @Transactional
    public SalesOrder findById(UUID id) {
        return salesOrderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sales order not found: " + id));
    }

    @Override
    @Transactional
    public SalesOrder findByOrderNumber(String orderNumber) {
        return salesOrderRepository.findByOrderNumber(orderNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Sales order not found: " + orderNumber));
    }

    @Override
    @Transactional
    public List<SalesOrder> findAll() {
        return salesOrderRepository.findAll();
    }

    @Override
    @Transactional
    public List<SalesOrder> findByCustomerCode(String customerCode) {
        return salesOrderRepository.findByCustomerCodeOrderByOrderDateDesc(customerCode);
    }

    private String generateOrderNumber() {
        return "SO-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}
