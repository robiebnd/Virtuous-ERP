package com.digipals.wms.putaway.service;

import com.digipals.wms.putaway.dto.CreatePutAwayRequest;
import com.digipals.wms.putaway.dto.PutAwayLineResponse;
import com.digipals.wms.putaway.dto.PutAwayResponse;
import com.digipals.wms.putaway.dto.UpdatePutAwayLineRequest;
import com.digipals.wms.putaway.dto.UpdatePutAwayRequest;

import java.util.List;
import java.util.UUID;

public interface PutAwayService {

    /**
     * Creates a Put-Away from an approved Goods Receipt, generating one
     * Put-Away Line per accepted Goods Receipt Line.
     */
    PutAwayResponse create(
            CreatePutAwayRequest request);

    PutAwayResponse update(
            UUID id,
            UpdatePutAwayRequest request);

    PutAwayResponse findById(
            UUID id);

    List<PutAwayResponse> findAll();

    List<PutAwayResponse> findByWarehouse(
            UUID warehouseId);

    List<PutAwayResponse> findByGoodsReceipt(
            UUID goodsReceiptId);

    /**
     * Puts away a quantity of a line into a destination bin, updating
     * bin-level inventory and recording the movement.
     */
    PutAwayLineResponse putAwayLine(
            UUID lineId,
            UpdatePutAwayLineRequest request);

    PutAwayLineResponse findLineById(
            UUID lineId);

    List<PutAwayLineResponse> findLinesByPutAway(
            UUID putAwayId);

    PutAwayResponse cancel(
            UUID id);

    void delete(
            UUID id);
}