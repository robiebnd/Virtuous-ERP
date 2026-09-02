package com.digipals.wms.outbounddelivery.service;

import com.digipals.wms.outbounddelivery.dto.CreateOutboundDeliveryRequest;
import com.digipals.wms.outbounddelivery.entity.OutboundDelivery;

import java.util.List;
import java.util.UUID;

public interface OutboundDeliveryService {
    OutboundDelivery create(CreateOutboundDeliveryRequest request);
    OutboundDelivery findById(UUID id);
    List<OutboundDelivery> findAll();
    List<OutboundDelivery> findBySalesOrder(UUID salesOrderId);
    OutboundDelivery startPicking(UUID id);
    OutboundDelivery confirmPicking(UUID id);
    OutboundDelivery confirmPacking(UUID id);
    OutboundDelivery postGoodsIssue(UUID id);
}
