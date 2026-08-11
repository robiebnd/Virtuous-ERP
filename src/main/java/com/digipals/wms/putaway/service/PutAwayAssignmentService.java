package com.digipals.wms.putaway.service;

import com.digipals.wms.putaway.dto.AssignPutAwayRequest;
import com.digipals.wms.putaway.dto.PutAwayResponse;

import java.util.UUID;

public interface PutAwayAssignmentService {

    PutAwayResponse assign(
            UUID putAwayId,
            AssignPutAwayRequest request);
}
