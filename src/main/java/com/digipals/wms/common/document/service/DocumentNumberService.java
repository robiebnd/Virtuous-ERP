package com.digipals.wms.common.document.service;

import com.digipals.wms.common.document.DocumentType;

public interface DocumentNumberService {

    String next(DocumentType documentType);

}