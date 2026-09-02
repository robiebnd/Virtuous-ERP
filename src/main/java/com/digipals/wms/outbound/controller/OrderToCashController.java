package com.digipals.wms.outbound.controller;

import com.digipals.wms.outbound.dto.O2cRequests.*;
import com.digipals.wms.outbound.service.OrderToCashService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController @RequestMapping("/api/outbound") @RequiredArgsConstructor
public class OrderToCashController {
    private final OrderToCashService service;
    @PostMapping("/customers") @ResponseStatus(HttpStatus.CREATED) public Object createCustomer(@RequestBody CustomerRequest r){return service.createCustomer(r);}
    @PostMapping("/sales-orders") @ResponseStatus(HttpStatus.CREATED) public Object createOrder(@RequestBody SalesOrderRequest r){return service.createOrder(r);}
    @PostMapping("/sales-orders/{orderNumber}/release-credit") public Object releaseCredit(@PathVariable String orderNumber){return service.releaseCreditBlock(orderNumber);}
    @PostMapping("/sales-orders/{orderNumber}/confirm") public Object confirmOrder(@PathVariable String orderNumber){return service.confirmOrder(orderNumber);}
    @PostMapping("/deliveries") @ResponseStatus(HttpStatus.CREATED) public Object createDelivery(@RequestBody DeliveryRequest r){return service.createDelivery(r.orderNumber());}
    @PostMapping("/deliveries/{deliveryNumber}/pick") public Object pick(@PathVariable String deliveryNumber){return service.pick(deliveryNumber);}
    @PostMapping("/deliveries/{deliveryNumber}/pack") public Object pack(@PathVariable String deliveryNumber){return service.pack(deliveryNumber);}
    @PostMapping("/deliveries/{deliveryNumber}/goods-issue") public Object goodsIssue(@PathVariable String deliveryNumber){return service.postGoodsIssue(deliveryNumber);}
    @PostMapping("/invoices") @ResponseStatus(HttpStatus.CREATED) public Object invoice(@RequestBody InvoiceRequest r){return service.createInvoice(r.deliveryNumber());}
    @PostMapping("/payments") @ResponseStatus(HttpStatus.CREATED) public Object payment(@RequestBody PaymentRequest r){return service.receivePayment(r);}
}
