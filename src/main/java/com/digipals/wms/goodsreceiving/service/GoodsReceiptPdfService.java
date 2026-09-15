package com.digipals.wms.goodsreceiving.service;

import java.util.UUID;

public interface GoodsReceiptPdfService {

    byte[] generateById(UUID id);

    byte[] generateByNumber(String grnNumber);
}
