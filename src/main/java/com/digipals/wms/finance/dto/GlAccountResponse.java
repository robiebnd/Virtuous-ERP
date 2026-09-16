package com.digipals.wms.finance.dto;

import com.digipals.wms.finance.entity.GlAccount;

import java.util.UUID;

public record GlAccountResponse(UUID id, String accountCode, String accountName, String accountType, Boolean controlAccount) {
    public static GlAccountResponse from(GlAccount account) {
        return new GlAccountResponse(account.getId(), account.getAccountCode(), account.getAccountName(), account.getAccountType(), account.getControlAccount());
    }
}
