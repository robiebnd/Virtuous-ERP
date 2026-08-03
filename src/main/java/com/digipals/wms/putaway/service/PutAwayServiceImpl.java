package com.digipals.wms.putaway.service;

import com.digipals.wms.bin.entity.Bin;
import com.digipals.wms.bin.repository.BinRepository;
import com.digipals.wms.common.mapper.PutAwayMapper;
import com.digipals.wms.goodsreceiving.entity.GoodsReceipt;
import com.digipals.wms.goodsreceiving.entity.GoodsReceiptLine;
import com.digipals.wms.goodsreceiving.repository.GoodsReceiptLineRepository;
import com.digipals.wms.goodsreceiving.repository.GoodsReceiptRepository;
import com.digipals.wms.inventory.service.InventoryService;
import com.digipals.wms.putaway.dto.CreatePutAwayRequest;
import com.digipals.wms.putaway.dto.PutAwayResponse;
import com.digipals.wms.putaway.dto.UpdatePutAwayRequest;
import com.digipals.wms.putaway.entity.PutAway;
import com.digipals.wms.putaway.entity.PutAwayLine;
import com.digipals.wms.putaway.entity.PutAwayStatus;
import com.digipals.wms.putaway.repository.PutAwayLineRepository;
import com.digipals.wms.putaway.repository.PutAwayRepository;
import com.digipals.wms.users.entity.User;
import com.digipals.wms.users.repository.UserRepository;
import com.digipals.wms.warehouse.entity.Warehouse;
import com.digipals.wms.warehouse.repository.WarehouseRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class PutAwayServiceImpl implements PutAwayService {

    private final PutAwayRepository putAwayRepository;

    private final PutAwayLineRepository putAwayLineRepository;

    private final GoodsReceiptRepository goodsReceiptRepository;

    private final GoodsReceiptLineRepository goodsReceiptLineRepository;

    private final WarehouseRepository warehouseRepository;

    private final BinRepository binRepository;

    private final UserRepository userRepository;

    private final InventoryService inventoryService;