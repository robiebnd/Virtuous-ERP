package com.digipals.wms.outbound.controller;

import com.digipals.wms.outbound.dto.PreSalesRequests.*;
import com.digipals.wms.outbound.service.PreSalesService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/outbound/o2c/pre-sales")
@RequiredArgsConstructor
public class PreSalesController {
    private final PreSalesService service;

    @PostMapping("/inquiries")
    @ResponseStatus(HttpStatus.CREATED)
    public Object createInquiry(@Valid @RequestBody CreateInquiryRequest request) {
        return service.createInquiry(request);
    }

    @PostMapping("/quotations")
    @ResponseStatus(HttpStatus.CREATED)
    public Object createQuotation(@Valid @RequestBody CreateQuotationRequest request) {
        return service.createQuotation(request);
    }

    @PostMapping("/quotations/{quotationNumber}/send")
    public Object send(@PathVariable String quotationNumber) {
        return service.sendQuotation(quotationNumber);
    }

    @PostMapping("/quotations/{quotationNumber}/accept")
    public Object accept(@PathVariable String quotationNumber) {
        return service.acceptQuotation(quotationNumber);
    }

    @PostMapping("/quotations/{quotationNumber}/convert-to-order")
    @ResponseStatus(HttpStatus.CREATED)
    public Object convert(@PathVariable String quotationNumber,
                          @Valid @RequestBody ConvertQuotationRequest request) {
        return service.convertQuotation(quotationNumber, request);
    }
}
