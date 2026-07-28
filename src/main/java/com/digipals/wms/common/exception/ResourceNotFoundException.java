package com.digipals.wms.common.exception;

public class ResourceNotFoundException
        extends BusinessException {

    public ResourceNotFoundException(
            String message) {

        super(message);
    }
}