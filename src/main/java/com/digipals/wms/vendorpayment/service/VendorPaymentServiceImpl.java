package com.digipals.wms.vendorpayment.service;

import com.digipals.wms.common.exception.InvalidWorkflowException;
import com.digipals.wms.common.exception.ResourceNotFoundException;
import com.digipals.wms.security.CurrentUserService;
import com.digipals.wms.supplier.entity.Supplier;
import com.digipals.wms.users.entity.User;
import com.digipals.wms.vendorinvoice.entity.VendorInvoice;
import com.digipals.wms.vendorinvoice.entity.VendorInvoiceStatus;
import com.digipals.wms.vendorinvoice.repository.VendorInvoiceRepository;
import com.digipals.wms.vendorpayment.dto.CreateVendorPaymentRequest;
import com.digipals.wms.vendorpayment.entity.VendorPayment;
import com.digipals.wms.vendorpayment.entity.VendorPaymentStatus;
import com.digipals.wms.vendorpayment.repository.VendorPaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class VendorPaymentServiceImpl implements VendorPaymentService {
    private final VendorPaymentRepository paymentRepository;
    private final VendorInvoiceRepository invoiceRepository;
    private final CurrentUserService currentUserService;

    @Override
    public VendorPayment create(CreateVendorPaymentRequest request) {
        VendorInvoice invoice = invoiceRepository.findWithLinesById(request.getVendorInvoiceId())
                .orElseThrow(() -> new ResourceNotFoundException("Vendor Invoice not found."));
        if (invoice.getStatus() != VendorInvoiceStatus.MATCHED && invoice.getStatus() != VendorInvoiceStatus.POSTED) {
            throw new InvalidWorkflowException("Only matched or posted vendor invoices can be paid.");
        }
        if (request.getAmount().compareTo(invoice.getTotalAmount()) != 0) {
            throw new InvalidWorkflowException("Payment amount must equal the vendor invoice total amount.");
        }
        User user = currentUserService.getCurrentUser();
        Supplier supplier = invoice.getSupplier();
        return paymentRepository.save(VendorPayment.builder()
                .paymentNumber("VP-" + System.currentTimeMillis())
                .supplier(supplier).vendorInvoice(invoice).amount(request.getAmount())
                .currency(request.getCurrency() == null ? invoice.getCurrency() : request.getCurrency())
                .paymentDate(LocalDateTime.now()).reference(request.getReference())
                .remarks(request.getRemarks()).processedBy(user).status(VendorPaymentStatus.DRAFT).build());
    }

    @Override
    public VendorPayment approve(UUID id) {
        VendorPayment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vendor Payment not found."));
        if (payment.getStatus() != VendorPaymentStatus.DRAFT) {
            throw new InvalidWorkflowException("Only DRAFT vendor payments can be approved.");
        }
        VendorInvoice invoice = payment.getVendorInvoice();
        if (invoice.getStatus() != VendorInvoiceStatus.MATCHED && invoice.getStatus() != VendorInvoiceStatus.POSTED) {
            throw new InvalidWorkflowException("Vendor invoice is not payable.");
        }
        payment.setStatus(VendorPaymentStatus.PAID);
        payment.setProcessedBy(currentUserService.getCurrentUser());
        invoice.setStatus(VendorInvoiceStatus.PAID);
        invoiceRepository.save(invoice);
        return paymentRepository.save(payment);
    }

    @Override
    @Transactional(readOnly = true)
    public List<VendorPayment> findByInvoice(UUID invoiceId) {
        return paymentRepository.findAllByVendorInvoiceIdOrderByPaymentDateDesc(invoiceId);
    }
}
