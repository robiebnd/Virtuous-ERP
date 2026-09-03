package com.digipals.wms.documentflow.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
public class DocumentFlowEntryResponse {

    private String documentType;
    private UUID documentId;
    private String documentNumber;
    private String status;
    private String relationship;
}
