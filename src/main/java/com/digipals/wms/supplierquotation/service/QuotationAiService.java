package com.digipals.wms.supplierquotation.service;

import org.springframework.web.multipart.MultipartFile;

import java.util.Map;
import java.util.UUID;

public interface QuotationAiService {

    Map<String, Object> extractLines(UUID supplierId, MultipartFile file);
}
