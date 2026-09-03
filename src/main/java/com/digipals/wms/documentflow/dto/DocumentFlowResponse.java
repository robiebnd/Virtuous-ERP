package com.digipals.wms.documentflow.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
public class DocumentFlowResponse {

    private String rootDocumentType;
    private UUID rootDocumentId;
    private String rootDocumentNumber;
    private String customerCode;
    private List<DocumentFlowEntryResponse> flow;
}
