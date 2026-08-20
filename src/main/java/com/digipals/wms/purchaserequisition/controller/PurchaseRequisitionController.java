package com.digipals.wms.purchaserequisition.controller;

import com.digipals.wms.common.exception.ResourceNotFoundException;
import com.digipals.wms.purchaserequisition.dto.CreatePurchaseRequisitionRequest;
import com.digipals.wms.purchaserequisition.dto.PurchaseRequisitionResponse;
import com.digipals.wms.purchaserequisition.dto.UpdatePurchaseRequisitionRequest;
import com.digipals.wms.purchaserequisition.entity.PurchaseRequisitionStatus;
import com.digipals.wms.purchaserequisition.service.PurchaseRequisitionPdfService;
import com.digipals.wms.purchaserequisition.service.PurchaseRequisitionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/purchase-requisitions")
@RequiredArgsConstructor
public class PurchaseRequisitionController {
    private final PurchaseRequisitionService service;
    private final PurchaseRequisitionPdfService pdfService;

    @PostMapping public PurchaseRequisitionResponse create(@Valid @RequestBody CreatePurchaseRequisitionRequest request) { return service.create(request); }
    @GetMapping public List<PurchaseRequisitionResponse> findAll() { return service.findAll(); }
    @GetMapping("/{id}") public PurchaseRequisitionResponse findById(@PathVariable UUID id) { return service.findById(id); }
    @GetMapping("/number/{requisitionNumber}") public PurchaseRequisitionResponse findByNumber(@PathVariable String requisitionNumber) { if (requisitionNumber == null || requisitionNumber.isBlank()) throw new IllegalArgumentException("Purchase Requisition number is required."); String normalizedNumber = requisitionNumber.trim(); return service.findAll().stream().filter(requisition -> normalizedNumber.equalsIgnoreCase(requisition.getRequisitionNumber())).findFirst().orElseThrow(() -> new ResourceNotFoundException("Purchase Requisition not found: " + normalizedNumber)); }
    @GetMapping("/number/{requisitionNumber}/pdf") public ResponseEntity<byte[]> pdfByNumber(@PathVariable String requisitionNumber) { return pdfResponse(pdfService.generateByNumber(requisitionNumber), requisitionNumber + ".pdf"); }
    @GetMapping("/{id}/pdf") public ResponseEntity<byte[]> pdf(@PathVariable UUID id) { return pdfResponse(pdfService.generateById(id), "purchase-requisition.pdf"); }
    @GetMapping("/status/{status}") public List<PurchaseRequisitionResponse> findByStatus(@PathVariable PurchaseRequisitionStatus status) { return service.findByStatus(status); }
    @GetMapping("/warehouse/{warehouseId}") public List<PurchaseRequisitionResponse> findByWarehouse(@PathVariable UUID warehouseId) { return service.findByWarehouse(warehouseId); }
    @PutMapping("/{id}") public PurchaseRequisitionResponse update(@PathVariable UUID id, @Valid @RequestBody UpdatePurchaseRequisitionRequest request) { return service.update(id, request); }
    @PostMapping("/{id}/submit") public PurchaseRequisitionResponse submit(@PathVariable UUID id) { return service.submit(id); }
    @PostMapping("/number/{requisitionNumber}/submit") public PurchaseRequisitionResponse submitByNumber(@PathVariable String requisitionNumber) { return service.submitByNumber(requisitionNumber); }
    @PutMapping("/{id}/approve") public PurchaseRequisitionResponse approve(@PathVariable UUID id) { return service.approve(id); }
    @PutMapping("/number/{requisitionNumber}/approve") public PurchaseRequisitionResponse approveByNumber(@PathVariable String requisitionNumber) { return service.approveByNumber(requisitionNumber); }
    @PutMapping("/{id}/reject") public PurchaseRequisitionResponse reject(@PathVariable UUID id, @RequestParam String remarks) { return service.reject(id, remarks); }
    @PutMapping("/number/{requisitionNumber}/reject") public PurchaseRequisitionResponse rejectByNumber(@PathVariable String requisitionNumber, @RequestParam String remarks) { return service.rejectByNumber(requisitionNumber, remarks); }
    @PostMapping("/{id}/cancel") public PurchaseRequisitionResponse cancel(@PathVariable UUID id) { return service.cancel(id); }
    @PostMapping("/number/{requisitionNumber}/cancel") public PurchaseRequisitionResponse cancelByNumber(@PathVariable String requisitionNumber) { return service.cancelByNumber(requisitionNumber); }
    @DeleteMapping("/{id}") public void delete(@PathVariable UUID id) { service.delete(id); }
    @DeleteMapping("/{id}/lines") public ResponseEntity<Void> clearLines(@PathVariable UUID id) { service.clearLines(id); return ResponseEntity.noContent().build(); }
    @PostMapping("/{requisitionId}/import-quotation/{quotationId}") public PurchaseRequisitionResponse importQuotation(@PathVariable UUID requisitionId, @PathVariable UUID quotationId) { return service.importQuotation(requisitionId, quotationId); }
    @PostMapping("/{requisitionId}/import-quotation/number/{quotationNumber}") public PurchaseRequisitionResponse importQuotationByNumber(@PathVariable UUID requisitionId, @PathVariable String quotationNumber) { return service.importQuotationByNumber(requisitionId, quotationNumber); }
    @PostMapping("/number/{requisitionNumber}/import-quotation/number/{quotationNumber}") public PurchaseRequisitionResponse importQuotationByRequisitionNumber(@PathVariable String requisitionNumber, @PathVariable String quotationNumber) { return service.importQuotationByRequisitionNumber(requisitionNumber, quotationNumber); }

    private ResponseEntity<byte[]> pdfResponse(byte[] bytes, String filename) { HttpHeaders headers = new HttpHeaders(); headers.setContentType(MediaType.APPLICATION_PDF); headers.setContentDisposition(ContentDisposition.inline().filename(filename).build()); headers.setContentLength(bytes.length); return ResponseEntity.ok().headers(headers).body(bytes); }
}
