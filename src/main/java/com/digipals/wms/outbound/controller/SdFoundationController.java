package com.digipals.wms.outbound.controller;

import com.digipals.wms.outbound.dto.SdFoundationRequests.*;
import com.digipals.wms.outbound.service.SdFoundationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController @RequestMapping("/api/outbound/sd") @RequiredArgsConstructor
public class SdFoundationController {
    private final SdFoundationService service;
    @PostMapping("/sales-organizations") @ResponseStatus(HttpStatus.CREATED) public Object organization(@RequestBody OrganizationRequest r){return service.createOrganization(r);}
    @PostMapping("/distribution-channels") @ResponseStatus(HttpStatus.CREATED) public Object channel(@RequestBody CodeNameRequest r){return service.createChannel(r);}
    @PostMapping("/divisions") @ResponseStatus(HttpStatus.CREATED) public Object division(@RequestBody CodeNameRequest r){return service.createDivision(r);}
    @PostMapping("/sales-areas") @ResponseStatus(HttpStatus.CREATED) public Object salesArea(@RequestBody SalesAreaRequest r){return service.createSalesArea(r);}
    @PostMapping("/shipping-points") @ResponseStatus(HttpStatus.CREATED) public Object shippingPoint(@RequestBody ShippingPointRequest r){return service.createShippingPoint(r);}
    @PostMapping("/sales-offices") @ResponseStatus(HttpStatus.CREATED) public Object salesOffice(@RequestBody SalesOfficeRequest r){return service.createSalesOffice(r);}
    @PostMapping("/sales-groups") @ResponseStatus(HttpStatus.CREATED) public Object salesGroup(@RequestBody SalesGroupRequest r){return service.createSalesGroup(r);}
    @PostMapping("/customer-sales-areas") @ResponseStatus(HttpStatus.CREATED) public Object customerSalesArea(@RequestBody CustomerSalesAreaRequest r){return service.assignCustomerSalesArea(r);}
    @PostMapping("/product-sales-data") @ResponseStatus(HttpStatus.CREATED) public Object productSalesData(@RequestBody ProductSalesDataRequest r){return service.assignProductSalesData(r);}
    @PostMapping("/customer-material-info") @ResponseStatus(HttpStatus.CREATED) public Object customerMaterial(@RequestBody CustomerMaterialRequest r){return service.createCustomerMaterial(r);}
    @PostMapping("/pricing-conditions") @ResponseStatus(HttpStatus.CREATED) public Object pricing(@RequestBody PricingConditionRequest r){return service.createPricingCondition(r);}
}
