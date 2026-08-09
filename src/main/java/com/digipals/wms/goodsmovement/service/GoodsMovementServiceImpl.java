package com.digipals.wms.goodsmovement.service;


import com.digipals.wms.bin.entity.Bin;
import com.digipals.wms.bin.repository.BinRepository;
import com.digipals.wms.common.exception.DuplicateResourceException;
import com.digipals.wms.common.exception.InvalidWorkflowException;
import com.digipals.wms.common.exception.ResourceNotFoundException;
import com.digipals.wms.common.mapper.GoodsMovementMapper;
import com.digipals.wms.goodsmovement.dto.CreateGoodsMovementLineRequest;
import com.digipals.wms.goodsmovement.dto.CreateGoodsMovementRequest;
import com.digipals.wms.goodsmovement.dto.GoodsMovementLineResponse;
import com.digipals.wms.goodsmovement.dto.GoodsMovementResponse;
import com.digipals.wms.goodsmovement.entity.GoodsMovement;
import com.digipals.wms.goodsmovement.entity.GoodsMovementLine;
import com.digipals.wms.goodsmovement.entity.GoodsMovementStatus;
import com.digipals.wms.goodsmovement.entity.GoodsMovementType;
import com.digipals.wms.goodsmovement.repository.GoodsMovementLineRepository;
import com.digipals.wms.goodsmovement.repository.GoodsMovementRepository;
import com.digipals.wms.inventory.service.InventoryService;
import com.digipals.wms.products.Product;
import com.digipals.wms.products.ProductRepository;
import com.digipals.wms.security.CurrentUserService;
import com.digipals.wms.users.entity.User;
import com.digipals.wms.warehouse.entity.Warehouse;
import com.digipals.wms.warehouse.repository.WarehouseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class GoodsMovementServiceImpl
        implements GoodsMovementService {

    private final GoodsMovementRepository goodsMovementRepository;

    private final GoodsMovementLineRepository
            goodsMovementLineRepository;

    private final WarehouseRepository warehouseRepository;

    private final BinRepository binRepository;

    private final ProductRepository productRepository;

    private final InventoryService inventoryService;

    private final CurrentUserService currentUserService;

    /*
     * ============================================================
     * CREATE
     * ============================================================
     */

    @Override
    public GoodsMovementResponse create(
            CreateGoodsMovementRequest request) {

        if (request == null) {
            throw new IllegalArgumentException(
                    "Goods Movement request is required.");
        }

        if (request.getLines() == null
                || request.getLines().isEmpty()) {

            throw new IllegalArgumentException(
                    "At least one movement line is required.");
        }

        Warehouse warehouse =
                warehouseRepository.findById(
                        request.getWarehouseId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Warehouse not found."));

        /*
         * A reference number should not normally be reused.
         */
        if (goodsMovementRepository
                .findByReferenceTypeAndReferenceNumber(
                        request.getReferenceType(),
                        request.getReferenceNumber())
                .isPresent()) {

            throw new DuplicateResourceException(
                    "A Goods Movement already exists for reference"
                            + request.getReferenceType()
                            + "/ "
                            + request.getReferenceNumber());
        }

        validateMovementType(request);

        User currentUser =
                currentUserService.getCurrentUser();
//to be revised
        GoodsMovement movement =
                GoodsMovement.builder()

                        .movementNumber(
                                generateMovementNumber())

                        .movementType(
                                request.getMovementType())

                        .status(
                                GoodsMovementStatus.DRAFT)

                        .warehouse(
                                warehouse)

                        .referenceNumber(
                                request.getReferenceNumber())

                        .referenceType(
                                request.getReferenceType())

                        .performedBy(
                                currentUser)

                        .remarks(
                                request.getRemarks())

                        .build();

        movement = goodsMovementRepository.save(
                        movement);

        for (CreateGoodsMovementLineRequest lineRequest
                : request.getLines()) {

            GoodsMovementLine line =
                    createLine(
                            movement,
                            warehouse,
                            lineRequest);

            goodsMovementLineRepository.save(line);
        }

        List<GoodsMovementLine> lines =
                goodsMovementLineRepository
                        .findByGoodsMovementId(
                                movement.getId());

        return GoodsMovementMapper.toResponse(
                movement,
                lines);
    }

    /*
     * ============================================================
     * POST MOVEMENT
     * ============================================================
     */

    @Override
    public GoodsMovementResponse post(
            UUID id) {

        GoodsMovement movement =
                getMovement(id);

        if (movement.getStatus()
                != GoodsMovementStatus.DRAFT) {

            throw new InvalidWorkflowException(
                    "Only DRAFT Goods Movements can be posted.");
        }

        List<GoodsMovementLine> lines =
                goodsMovementLineRepository
                        .findByGoodsMovementId(id);

        if (lines.isEmpty()) {

            throw new InvalidWorkflowException(
                    "Goods Movement has no lines.");
        }

        validateMovementBeforePosting(
                movement,
                lines);

        User currentUser =
                currentUserService.getCurrentUser();

        /*
         * ========================================================
         * APPLY INVENTORY MOVEMENT
         * ========================================================
         */

        for (GoodsMovementLine line : lines ) {

            processInventoryMovement(
                    movement,
                    line,
                    currentUser);
        }

        /*
         * ========================================================
         * MARK MOVEMENT AS POSTED
         * ========================================================
         */

        movement.setStatus(
                GoodsMovementStatus.POSTED);

        movement.setPerformedBy(
                currentUser);

        movement =
                goodsMovementRepository.save(
                        movement);

        return GoodsMovementMapper.toResponse(
                movement,
                lines);
    }

    /*
     * ============================================================
     * PROCESS INVENTORY
     * ============================================================
     */

    private void processInventoryMovement(
            GoodsMovement movement,
            GoodsMovementLine line,
            User performedBy) {

        Warehouse warehouse =
                movement.getWarehouse();

        Product product =
                line.getProduct();

        BigDecimal quantity =
                line.getQuantity();

        String referenceNumber =
                movement.getMovementNumber();

        String referenceType =
                movement.getMovementType().name();

        String remarks =
                line.getRemarks() != null
                        ? line.getRemarks()
                        : movement.getRemarks();

        switch (movement.getMovementType()) {

            case GOODS_RECEIPT -> {

                /*
                 * Stock enters the warehouse.
                 *
                 * fromBin = null
                 * toBin   = receiving/storage bin
                 */

                if (line.getToBin() == null) {

                    throw new InvalidWorkflowException(
                            "Goods Receipt requires a destination bin.");
                }

                inventoryService.receiveStock(
                        warehouse,
                        line.getToBin(),
                        product,
                        quantity,
                        referenceNumber,
                        referenceType,
                        remarks,
                        performedBy);
            }

            case CUSTOMER_RETURN -> {

                if (line.getToBin() == null) {

                    throw new InvalidWorkflowException(
                            "Customer Return requires a destination bin.");
                }

                inventoryService.receiveStock(
                        warehouse,
                        line.getToBin(),
                        product,
                        quantity,
                        referenceNumber,
                        referenceType,
                        remarks,
                        performedBy);
            }

            case PUT_AWAY,
                 BIN_TRANSFER,
                 STOCK_TRANSFER -> {

                /*
                 * Stock moves from one bin to another.
                 */

                if (line.getFromBin() == null) {

                    throw new InvalidWorkflowException(
                            movement.getMovementType()
                                    + "requires a source bin.");
                }

                if (line.getToBin() == null) {

                    throw new InvalidWorkflowException(
                            movement.getMovementType()
                                    + "requires a destination bin.");
                }

                if (line.getFromBin()
                        .getId()
                        .equals(
                                line.getToBin().getId())) {

                    throw new InvalidWorkflowException(
                            "Source and destination bins cannot be the same.");
                }

                inventoryService.moveStock(
                        warehouse,
                        line.getFromBin(),
                        line.getToBin(),
                        product,
                        quantity,
                        referenceNumber,
                        referenceType,
                        remarks,
                        performedBy);
            }

            case PICK,
                 SHIPMENT,
                 SUPPLIER_RETURN -> {

                /*
                 * Stock leaves the warehouse/bin.
                 */

                if (line.getFromBin() == null) {

                    throw new InvalidWorkflowException(
                            movement.getMovementType()
                                    + "requires a source bin.");
                }

                inventoryService.issueStock(
                        warehouse,
                        line.getFromBin(),
                        product,
                        quantity,
                        referenceNumber,
                        referenceType,
                        remarks,
                        performedBy);
            }

            case STOCK_ADJUSTMENT,
                 STOCK_COUNT -> {

                /*
                 * Adjustment movements require an explicit
                 * inventory operation.
                 *
                 * For now the quantity is interpreted as:
                 *
                 * positive = stock in
                 * negative = stock out
                 */

                if (line.getToBin() != null
                        && quantity.compareTo(
                        BigDecimal.ZERO) > 0) {

                    inventoryService.receiveStock(
                            warehouse,
                            line.getToBin(),
                            product,
                            quantity,
                            referenceNumber,
                            referenceType,
                            remarks,
                            performedBy);

                } else if (line.getFromBin() != null
                        && quantity.compareTo(
                        BigDecimal.ZERO) > 0) {

                    inventoryService.issueStock(
                            warehouse,
                            line.getFromBin(),
                            product,
                            quantity,
                            referenceNumber,
                            referenceType,
                            remarks,
                            performedBy);

                } else {

                    throw new InvalidWorkflowException(
                            "Stock adjustment requires a valid source"
                                    + "or destination bin.");
                }
            }

            case WRITE_OFF -> {

                if (line.getFromBin() == null) {

                    throw new InvalidWorkflowException(
                            "Write-off requires a source bin.");
                }

                inventoryService.issueStock(
                        warehouse,
                        line.getFromBin(),
                        product,
                        quantity,
                        referenceNumber,
                        referenceType,
                        remarks,
                        performedBy);
            }

            default -> throw new InvalidWorkflowException(
                    "Unsupported Goods Movement type: "
                            + movement.getMovementType());
        }
    }

    /*
     * ============================================================
     * CREATE LINE
     * ============================================================
     */

    private GoodsMovementLine createLine(
            GoodsMovement movement,
            Warehouse warehouse,
            CreateGoodsMovementLineRequest request) {

        Product product =
                productRepository.findById(
                        request.getProductId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Product not found."));

        Bin fromBin = null;

        if (request.getFromBinId() != null) {

            fromBin =
                    getBin(
                            request.getFromBinId());

            validateBinInWarehouse(
                    fromBin,
                    warehouse,
                    "Source bin");
        }

        Bin toBin = null;

        if (request.getToBinId() != null) {

            toBin =
                    getBin(
                            request.getToBinId());

            validateBinInWarehouse(
                    toBin,
                    warehouse,
                    "Destination bin");
        }

        validateLine(
                movement.getMovementType(),
                fromBin,
                toBin,
                request.getQuantity());

        return GoodsMovementLine.builder()

                .goodsMovement(
                        movement)

                .product(
                        product)

                .fromBin(
                        fromBin)

                .toBin(
                        toBin)

                .quantity(
                        request.getQuantity())

                .unitCost(
                        request.getUnitCost())

                .remarks(
                        request.getRemarks())

                .build();
    }

    /*
     * ============================================================
     * VALIDATE REQUEST
     * ============================================================
     */

    private void validateMovementType(
            CreateGoodsMovementRequest request) {

        if (request.getMovementType() == null) {

            throw new InvalidWorkflowException(
                    "Movement type is required.");
        }

        if (request.getWarehouseId() == null) {

            throw new InvalidWorkflowException(
                    "Warehouse is required.");
        }

        if (request.getReferenceNumber() == null
                || request.getReferenceNumber().isBlank()) {

            throw new InvalidWorkflowException(
                    "Reference number is required.");
        }

        if (request.getReferenceType() == null
                || request.getReferenceType().isBlank()) {

            throw new InvalidWorkflowException(
                    "Reference type is required.");
        }
    }

    /*
     * ============================================================
     * VALIDATE LINE
     * ============================================================
     */

    private void validateLine(
            GoodsMovementType type,
            Bin fromBin,
            Bin toBin,
            BigDecimal quantity) {

        if (quantity == null
                || quantity.compareTo(
                BigDecimal.ZERO) <= 0) {

            throw new InvalidWorkflowException(
                    "Movement quantity must be greater than zero.");
        }

        switch (type) {

            case GOODS_RECEIPT,
                 CUSTOMER_RETURN -> {

                if (toBin == null) {

                    throw new InvalidWorkflowException(
                            type + " requires a destination bin.");
                }
            }

            case PUT_AWAY,
                 BIN_TRANSFER,
                 STOCK_TRANSFER -> {

                if (fromBin == null) {

                    throw new InvalidWorkflowException(
                            type + "requires a source bin.");
                }

                if (toBin == null) {

                    throw new InvalidWorkflowException(
                            type + "requires a destination bin.");
                }

                if (fromBin.getId()
                        .equals(toBin.getId())) {

                    throw new InvalidWorkflowException(
                            "Source and destination bins cannot be the same.");
                }
            }

            case PICK,
                 SHIPMENT,
                 SUPPLIER_RETURN,
                 WRITE_OFF -> {

                if (fromBin == null) {

                    throw new InvalidWorkflowException(
                            type + " requires a source bin.");
                }
            }

            case STOCK_ADJUSTMENT,
                 STOCK_COUNT -> {

                if (fromBin == null
                        && toBin == null) {

                    throw new InvalidWorkflowException(
                            type
                                    + " requires a source or destination bin.");
                }
            }

            default -> {
                // No additional validation.
            }
        }
    }

    /*
     * ============================================================
     * VALIDATE BEFORE POSTING
     * ============================================================
     */

    private void validateMovementBeforePosting(
            GoodsMovement movement,
            List<GoodsMovementLine> lines) {

        if (movement.getWarehouse() == null) {

            throw new InvalidWorkflowException(
                    "Goods Movement warehouse is required.");
        }

        for (GoodsMovementLine line : lines) {

            if (line.getProduct() == null) {

                throw new InvalidWorkflowException(
                        "Every movement line must have a product.");
            }

            if (line.getQuantity() == null
                    || line.getQuantity()
                    .compareTo(BigDecimal.ZERO) <= 0) {

                throw new InvalidWorkflowException(
                        "Movement quantity must be greater than zero.");
            }
        }
    }

    /*
     * ============================================================
     * FIND BY ID
     * ============================================================
     */

    @Override
    @Transactional(readOnly = true)
    public GoodsMovementResponse findById(
            UUID id) {

        GoodsMovement movement =
                getMovement(id);

        List<GoodsMovementLine> lines =
                goodsMovementLineRepository
                        .findByGoodsMovementId(id);

        return GoodsMovementMapper.toResponse(
                movement,
                lines);
    }

    /*
     * ============================================================
     * FIND ALL
     * ============================================================
     */

    @Override
    @Transactional(readOnly = true)
    public List<GoodsMovementResponse> findAll() {

        return goodsMovementRepository
                .findAll()
                .stream()
                .map(movement -> {

                    List<GoodsMovementLine> lines =
                            goodsMovementLineRepository
                                    .findByGoodsMovementId(
                                            movement.getId());

                    return GoodsMovementMapper.toResponse(
                            movement,
                            lines);
                })
                .toList();
    }

    /*
     * ============================================================
     * FIND BY WAREHOUSE
     * ============================================================
     */

    @Override
    @Transactional(readOnly = true)
    public List<GoodsMovementResponse> findByWarehouse(
            UUID warehouseId) {

        return goodsMovementRepository
                .findByWarehouseId(
                        warehouseId)
                .stream()
                .map(movement -> {

                    List<GoodsMovementLine> lines =
                            goodsMovementLineRepository
                                    .findByGoodsMovementId(
                                            movement.getId());

                    return GoodsMovementMapper.toResponse(
                            movement,
                            lines);
                })
                .toList();
    }

    /*
     * ============================================================
     * FIND BY TYPE
     * ============================================================
     */

    @Override
    @Transactional(readOnly = true)
    public List<GoodsMovementResponse> findByType(
            GoodsMovementType movementType) {

        return goodsMovementRepository
                .findByMovementType(
                        movementType)
                .stream()
                .map(movement -> {

                    List<GoodsMovementLine> lines =
                            goodsMovementLineRepository
                                    .findByGoodsMovementId(
                                            movement.getId());

                    return GoodsMovementMapper.toResponse(
                            movement,
                            lines);
                })
                .toList();
    }

    /*
     * ============================================================
     * FIND BY REFERENCE
     * ============================================================
     */

    @Override
    @Transactional(readOnly = true)
    public List<GoodsMovementResponse> findByReferenceNumber(
            String referenceNumber) {

        return goodsMovementRepository
                .findByReferenceNumber(
                        referenceNumber)
                .stream()
                .map(movement -> {

                    List<GoodsMovementLine> lines =
                            goodsMovementLineRepository
                                    .findByGoodsMovementId(
                                            movement.getId());

                    return GoodsMovementMapper.toResponse(
                            movement,
                            lines);
                })
                .toList();
    }

    /*
     * ============================================================
     * FIND LINES
     * ============================================================
     */

    @Override
    @Transactional(readOnly = true)
    public List<GoodsMovementLineResponse> findLines(
            UUID movementId) {

        /*
         * Make sure the movement exists first.
         */
        getMovement(movementId);

        return goodsMovementLineRepository
                .findByGoodsMovementId(
                        movementId)
                .stream()
                .map(
                        GoodsMovementMapper::toLineResponse)
                .toList();
    }

    /*
     * ============================================================
     * CANCEL
     * ============================================================
     */

    @Override
    public GoodsMovementResponse cancel(
            UUID id) {

        GoodsMovement movement =
                getMovement(id);

        if (movement.getStatus()
                == GoodsMovementStatus.POSTED) {

            throw new InvalidWorkflowException(
                    "A posted Goods Movement cannot be cancelled.");
        }

        if (movement.getStatus()
                == GoodsMovementStatus.CANCELLED) {

            throw new InvalidWorkflowException(
                    "Goods Movement is already cancelled.");
        }

        movement.setStatus(
                GoodsMovementStatus.CANCELLED);

        movement =
                goodsMovementRepository.save(
                        movement);

        List<GoodsMovementLine> lines =
                goodsMovementLineRepository
                        .findByGoodsMovementId(id);

        return GoodsMovementMapper.toResponse(
                movement,
                lines);
    }

    /*
     * ============================================================
     * GET MOVEMENT
     * ============================================================
     */

    private GoodsMovement getMovement(
            UUID id) {

        return goodsMovementRepository
                .findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Goods Movement not found."));
    }

    /*
     * ============================================================
     * GET BIN
     * ============================================================
     */

    private Bin getBin(
            UUID id) {

        return binRepository
                .findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Bin not found."));
    }

    /*
     * ============================================================
     * VALIDATE BIN / WAREHOUSE
     * ============================================================
     */

    private void validateBinInWarehouse(
            Bin bin,
            Warehouse warehouse,
            String description) {

        if (bin.getWarehouse() == null) {

            throw new InvalidWorkflowException(
                    description
                            + " is not assigned to a warehouse.");
        }

        if (!bin.getWarehouse()
                .getId()
                .equals(warehouse.getId())) {

            throw new InvalidWorkflowException(
                    description
                            + " does not belong to the selected warehouse.");
        }
    }

    /*
     * ============================================================
     * GENERATE MOVEMENT NUMBER
     * ============================================================
     */

    private String generateMovementNumber() {

        String number;

        do {

            number =
                    "GM"
                            + System.currentTimeMillis();

        } while (
                goodsMovementRepository
                        .existsByMovementNumber(number));

        return number;
    }
}
