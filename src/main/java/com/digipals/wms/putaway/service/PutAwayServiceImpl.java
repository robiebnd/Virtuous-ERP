package com.digipals.wms.putaway.service;

import com.digipals.wms.bin.entity.Bin;
import com.digipals.wms.bin.entity.BinStatus;
import com.digipals.wms.bin.repository.BinRepository;
import com.digipals.wms.common.document.DocumentType;
import com.digipals.wms.common.document.service.DocumentNumberService;
import com.digipals.wms.common.exception.DuplicateResourceException;
import com.digipals.wms.common.exception.InvalidWorkflowException;
import com.digipals.wms.common.exception.ResourceNotFoundException;
import com.digipals.wms.common.mapper.PutAwayMapper;
import com.digipals.wms.goodsreceiving.entity.GoodsReceipt;
import com.digipals.wms.goodsreceiving.entity.GoodsReceiptLine;
import com.digipals.wms.goodsreceiving.entity.ReceiptStatus;
import com.digipals.wms.goodsreceiving.repository.GoodsReceiptLineRepository;
import com.digipals.wms.goodsreceiving.repository.GoodsReceiptRepository;
import com.digipals.wms.inventorybin.entity.InventoryBin;
import com.digipals.wms.inventorybin.repository.InventoryBinRepository;
import com.digipals.wms.inventorytransaction.entity.InventoryTransaction;
import com.digipals.wms.inventorytransaction.entity.TransactionType;
import com.digipals.wms.inventorytransaction.repository.InventoryTransactionRepository;
import com.digipals.wms.putaway.entity.PutAwayLineStatus;
import com.digipals.wms.putaway.dto.CreatePutAwayRequest;
import com.digipals.wms.putaway.dto.PutAwayLineResponse;
import com.digipals.wms.putaway.dto.PutAwayResponse;
import com.digipals.wms.putaway.dto.UpdatePutAwayLineRequest;
import com.digipals.wms.putaway.dto.UpdatePutAwayRequest;
import com.digipals.wms.putaway.entity.PutAway;
import com.digipals.wms.putaway.entity.PutAwayLine;
import com.digipals.wms.putaway.entity.PutAwayStatus;
import com.digipals.wms.putaway.repository.PutAwayLineRepository;
import com.digipals.wms.putaway.repository.PutAwayRepository;
import com.digipals.wms.products.Product;
import com.digipals.wms.security.CurrentUserService;
import com.digipals.wms.users.entity.User;
import com.digipals.wms.warehouse.entity.Warehouse;
import com.digipals.wms.warehouse.repository.WarehouseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
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
    private final InventoryBinRepository inventoryBinRepository;
    private final InventoryTransactionRepository inventoryTransactionRepository;
    private final DocumentNumberService documentNumberService;
    private final CurrentUserService currentUserService;

    private PutAway getPutAway(UUID id) {
        return putAwayRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Put-Away not found."));
    }

    private PutAwayLine getPutAwayLine(UUID id) {
        return putAwayLineRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Put-Away Line not found."));
    }

    private GoodsReceipt getGoodsReceipt(UUID id) {
        return goodsReceiptRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Goods Receipt not found."));
    }

    private Bin getBin(UUID id) {
        return binRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Bin not found."));
    }

    private void validateBinInWarehouse(Bin bin, Warehouse warehouse, String label) {
        if (!bin.getWarehouse().getId().equals(warehouse.getId())) {
            throw new InvalidWorkflowException(label + " does not belong to the selected warehouse.");
        }
        if (bin.getStatus() != BinStatus.AVAILABLE) {
            throw new InvalidWorkflowException(label + " is not available.");
        }
    }

    @Override
    public PutAwayResponse create(CreatePutAwayRequest request) {
        GoodsReceipt goodsReceipt = getGoodsReceipt(request.getGoodsReceiptId());
        if (goodsReceipt.getStatus() != ReceiptStatus.APPROVED) {
            throw new InvalidWorkflowException("Only approved Goods Receipts can be put away.");
        }
        Warehouse warehouse = goodsReceipt.getWarehouse();
        Bin fromBin = getBin(request.getFromBinId());
        validateBinInWarehouse(fromBin, warehouse, "Staging bin");

        if (putAwayRepository.existsByGoodsReceiptIdAndStatusNot(goodsReceipt.getId(), PutAwayStatus.CANCELLED)) {
            throw new DuplicateResourceException("A Put-Away already exists for this Goods Receipt.");
        }

        List<GoodsReceiptLine> receiptLines = goodsReceiptLineRepository.findByGoodsReceiptId(goodsReceipt.getId())
                .stream()
                .filter(line -> line.getAcceptedQuantity() != null
                        && line.getAcceptedQuantity().compareTo(BigDecimal.ZERO) > 0)
                .toList();

        if (receiptLines.isEmpty()) {
            throw new ResourceNotFoundException("Goods Receipt has no accepted lines to put away.");
        }

        User currentUser = currentUserService.getCurrentUser();
        PutAway putAway = PutAway.builder()
                .putAwayNumber(documentNumberService.next(DocumentType.PUTAWAY))
                .goodsReceipt(goodsReceipt)
                .warehouse(warehouse)
                .status(PutAwayStatus.DRAFT)
                .remarks(request.getRemarks())
                .initiatedBy(currentUser)
                .assignedTo(null)
                .completedBy(null)
                .completedAt(null)
                .build();
        putAway = putAwayRepository.save(putAway);

        for (GoodsReceiptLine receiptLine : receiptLines) {
            PutAwayLine line = PutAwayLine.builder()
                    .putAway(putAway)
                    .goodsReceiptLine(receiptLine)
                    .product(receiptLine.getProduct())
                    .fromBin(fromBin)
                    .plannedQuantity(receiptLine.getAcceptedQuantity())
                    .completedQuantity(BigDecimal.ZERO)
                    .status(PutAwayLineStatus.PENDING)
                    .build();
            putAwayLineRepository.save(line);
        }

        return PutAwayMapper.toResponse(putAway,
                putAwayLineRepository.findByPutAwayId(putAway.getId()));
    }

    @Override
    public PutAwayResponse update(UUID id, UpdatePutAwayRequest request) {
        PutAway putAway = getPutAway(id);
        if (putAway.getStatus() != PutAwayStatus.DRAFT) {
            throw new InvalidWorkflowException("Only draft Put-Aways can be updated.");
        }
        putAway.setRemarks(request.getRemarks());
        return PutAwayMapper.toResponse(putAwayRepository.save(putAway));
    }

    @Override
    @Transactional(readOnly = true)
    public PutAwayResponse findById(UUID id) {
        PutAway putAway = getPutAway(id);
        return PutAwayMapper.toResponse(putAway,
                putAwayLineRepository.findByPutAwayId(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<PutAwayResponse> findAll() {
        return putAwayRepository.findAll().stream()
                .map(putAway -> PutAwayMapper.toResponse(putAway,
                        putAwayLineRepository.findByPutAwayId(putAway.getId())))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<PutAwayResponse> findByWarehouse(UUID warehouseId) {
        return putAwayRepository.findByWarehouseId(warehouseId).stream()
                .map(putAway -> PutAwayMapper.toResponse(putAway,
                        putAwayLineRepository.findByPutAwayId(putAway.getId())))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<PutAwayResponse> findByGoodsReceipt(UUID goodsReceiptId) {
        return putAwayRepository.findByGoodsReceiptId(goodsReceiptId).stream()
                .map(putAway -> PutAwayMapper.toResponse(putAway,
                        putAwayLineRepository.findByPutAwayId(putAway.getId())))
                .toList();
    }

    @Override
    public PutAwayLineResponse putAwayLine(UUID lineId, UpdatePutAwayLineRequest request) {
        PutAwayLine line = getPutAwayLine(lineId);
        PutAway putAway = line.getPutAway();

        if (putAway.getStatus() != PutAwayStatus.DRAFT && putAway.getStatus() != PutAwayStatus.IN_PROGRESS) {
            throw new InvalidWorkflowException("Put-Away is not in a state that allows put-away actions.");
        }
        if (line.getStatus() != PutAwayLineStatus.PENDING && line.getStatus() != PutAwayLineStatus.IN_PROGRESS) {
            throw new InvalidWorkflowException("Line is not in a state that allows put-away actions.");
        }

        Bin fromBin = line.getFromBin();
        Bin toBin = getBin(request.getToBinId());
        validateBinInWarehouse(fromBin, putAway.getWarehouse(), "Source bin");
        validateBinInWarehouse(toBin, putAway.getWarehouse(), "Destination bin");

        if (fromBin.getId().equals(toBin.getId())) {
            throw new InvalidWorkflowException("Source and destination bins must be different.");
        }

        BigDecimal quantity = request.getQuantity();
        BigDecimal remaining = line.getPlannedQuantity().subtract(line.getCompletedQuantity());
        if (quantity.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidWorkflowException("Put-away quantity must be greater than zero.");
        }
        if (quantity.compareTo(remaining) > 0) {
            throw new InvalidWorkflowException("Quantity exceeds the remaining planned quantity for this line.");
        }

        Product product = line.getProduct();
        InventoryBin sourceInventory = inventoryBinRepository
                .findByWarehouseIdAndBinIdAndProductId(putAway.getWarehouse().getId(), fromBin.getId(), product.getId())
                .orElseThrow(() -> new InvalidWorkflowException(
                        "No inventory exists for " + product.getId() + " in source bin " + fromBin.getCode() + "."));

        BigDecimal sourceBalanceBefore = sourceInventory.getQuantityOnHand();
        if (sourceBalanceBefore.compareTo(quantity) < 0) {
            throw new InvalidWorkflowException("Insufficient stock in source bin. Available: " + sourceBalanceBefore
                    + ", requested: " + quantity + ".");
        }

        InventoryBin destinationInventory = inventoryBinRepository
                .findByWarehouseIdAndBinIdAndProductId(putAway.getWarehouse().getId(), toBin.getId(), product.getId())
                .orElseGet(() -> InventoryBin.builder()
                        .warehouse(putAway.getWarehouse())
                        .bin(toBin)
                        .product(product)
                        .quantityOnHand(BigDecimal.ZERO)
                        .quantityReserved(BigDecimal.ZERO)
                        .build());

        BigDecimal destinationBalanceBefore = destinationInventory.getQuantityOnHand();
        BigDecimal sourceBalanceAfter = sourceBalanceBefore.subtract(quantity);
        BigDecimal destinationBalanceAfter = destinationBalanceBefore.add(quantity);

        sourceInventory.setQuantityOnHand(sourceBalanceAfter);
        destinationInventory.setQuantityOnHand(destinationBalanceAfter);
        inventoryBinRepository.save(sourceInventory);
        inventoryBinRepository.save(destinationInventory);

        fromBin.setUsedCapacity(fromBin.getUsedCapacity().subtract(quantity).max(BigDecimal.ZERO));
        toBin.setUsedCapacity(toBin.getUsedCapacity().add(quantity));
        binRepository.save(fromBin);
        binRepository.save(toBin);

        User currentUser = currentUserService.getCurrentUser();
        InventoryTransaction transaction = InventoryTransaction.builder()
                .inventoryBin(destinationInventory)
                .transactionType(TransactionType.PUTAWAY)
                .quantity(quantity)
                .balanceAfter(destinationBalanceAfter)
                .referenceNumber(putAway.getPutAwayNumber())
                .referenceType("PUT_AWAY")
                .performedBy(currentUser)
                .remarks(request.getRemarks())
                .transactionDate(LocalDateTime.now())
                .fromBin(fromBin)
                .toBin(toBin)
                .build();
        inventoryTransactionRepository.save(transaction);

        line.setToBin(toBin);
        line.setCompletedQuantity(line.getCompletedQuantity().add(quantity));
        line.setStatus(line.getCompletedQuantity().compareTo(line.getPlannedQuantity()) >= 0
                ? PutAwayLineStatus.COMPLETED
                : PutAwayLineStatus.IN_PROGRESS);
        line = putAwayLineRepository.save(line);

        refreshPutAwayStatus(putAway, currentUser);
        return PutAwayMapper.toLineResponse(line);
    }

    private void refreshPutAwayStatus(PutAway putAway, User currentUser) {
        List<PutAwayLine> lines = putAwayLineRepository.findByPutAwayId(putAway.getId());
        boolean allCompleted = lines.stream().allMatch(l -> l.getStatus() == PutAwayLineStatus.COMPLETED);
        if (allCompleted) {
            putAway.setStatus(PutAwayStatus.COMPLETED);
            putAway.setCompletedAt(LocalDateTime.now());
            putAway.setCompletedBy(currentUser);
        } else {
            putAway.setStatus(PutAwayStatus.IN_PROGRESS);
        }
        putAwayRepository.save(putAway);
    }

    @Override
    @Transactional(readOnly = true)
    public PutAwayLineResponse findLineById(UUID lineId) {
        return PutAwayMapper.toLineResponse(getPutAwayLine(lineId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<PutAwayLineResponse> findLinesByPutAway(UUID putAwayId) {
        getPutAway(putAwayId);
        return putAwayLineRepository.findByPutAwayId(putAwayId).stream()
                .map(PutAwayMapper::toLineResponse)
                .toList();
    }

    @Override
    public PutAwayResponse cancel(UUID id) {
        PutAway putAway = getPutAway(id);
        if (putAway.getStatus() != PutAwayStatus.DRAFT) {
            throw new InvalidWorkflowException("Only draft Put-Aways can be cancelled.");
        }
        putAway.setStatus(PutAwayStatus.CANCELLED);
        putAway = putAwayRepository.save(putAway);
        return PutAwayMapper.toResponse(putAway,
                putAwayLineRepository.findByPutAwayId(putAway.getId()));
    }

    @Override
    public void delete(UUID id) {
        PutAway putAway = getPutAway(id);
        if (putAway.getStatus() != PutAwayStatus.DRAFT) {
            throw new InvalidWorkflowException("Only draft Put-Aways can be deleted.");
        }
        putAwayRepository.delete(putAway);
    }
}
