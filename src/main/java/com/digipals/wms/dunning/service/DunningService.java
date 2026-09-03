package com.digipals.wms.dunning.service;

import com.digipals.wms.dunning.dto.CreateDunningRequest;
import com.digipals.wms.dunning.entity.DunningCase;

import java.util.List;
import java.util.UUID;

public interface DunningService {
    DunningCase create(CreateDunningRequest request);
    DunningCase send(UUID id);
    DunningCase resolve(UUID id);
    DunningCase findById(UUID id);
    List<DunningCase> findAll();
    List<DunningCase> findByCustomerCode(String customerCode);
}
