package com.digipals.wms.putaway.repository;

import com.digipals.wms.putaway.entity.PutAwayLine;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface PutAwayLineRepository
        extends JpaRepository<PutAwayLine, UUID> {

    List<PutAwayLine> findByPutAwayId(
            UUID putAwayId);

    List<PutAwayLine> findByGoodsReceiptLineId(
            UUID goodsReceiptLineId);

    long countByPutAwayId(
            UUID putAwayId);

    void deleteByPutAwayId(
            UUID putAwayId);
}