package com.digipals.wms.common.exception;

public class UnauthorizedException
        extends BusinessException {

    public UnauthorizedException(
            String message) {

        super(message);
    }
}