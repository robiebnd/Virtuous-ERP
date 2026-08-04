package com.digipals.wms.stocktransfer.service;

import com.digipals.wms.bin.entity.Bin;
import com.digipals.wms.bin.repository.BinRepository;
import com.digipals.wms.common.document.DocumentType;
import com.digipals.wms.common.document.service.DocumentNumberService;
import com.digipals.wms.common.mapper.StockTransferMapper;
import com.digipals.wms.inventory.service.InventoryService;
import com.digipals.wms.security.CurrentUserService;
import com.digipals.wms.stocktransfer.dto.CreateStockTransferRequest;
import com.digipals.wms.stocktransfer.dto.StockTransferResponse;
import com.digipals.wms.stocktransfer.entity.StockTransfer;
import com.digipals.wms.stocktransfer.entity.StockTransferLine;
import com.digipals.wms.stocktransfer.entity.StockTransferStatus;
import com.digipals.wms.stocktransfer.repository.StockTransferLineRepository;
import com.digipals.wms.stocktransfer.repository.StockTransferRepository;
import com.digipals.wms.users.entity.User;
import com.digipals.wms.warehouse.entity.Warehouse;
import com.digipals.wms.warehouse.repository.WarehouseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class StockTransferServiceImpl
                implements StockTransferService {

        private final StockTransferRepository repository;

        private final StockTransferLineRepository lineRepository;

        private final WarehouseRepository warehouseRepository;

        private final BinRepository binRepository;

        private final DocumentNumberService documentNumberService;

        private final CurrentUserService currentUserService;

        private final InventoryService inventoryService;

        @Override
        public StockTransferResponse create(
                        CreateStockTransferRequest request) {

                Warehouse source = warehouseRepository.findById(
                                request.getSourceWarehouseId())
                                .orElseThrow(() -> new RuntimeException(
                                                "Source warehouse not found."));

                Warehouse destination = warehouseRepository.findById(
                                request.getDestinationWarehouseId())
                                .orElseThrow(() -> new RuntimeException(
                                                "Destination warehouse not found."));

                if (source.getId().equals(destination.getId())) {

                        throw new RuntimeException(
                                        "Source and destination warehouses cannot be the same.");
                }

                if (!Boolean.TRUE.equals(source.getActive())) {

                        throw new RuntimeException(
                                        "Source warehouse is inactive.");
                }

                if (!Boolean.TRUE.equals(destination.getActive())) {

                        throw new RuntimeException(
                                        "Destination warehouse is inactive.");
                }

                User currentUser = currentUserService.getCurrentUser();

                /*
                 * StockTransfer transfer = StockTransfer.builder()
                 * 
                 * .transferNumber(
                 * documentNumberService.next(
                 * DocumentType.STOCK_TRANSFER))
                 * 
                 * .sourceWarehouse(source)
                 * 
                 * .destinationWarehouse(destination)
                 * 
                 * .status(
                 * StockTransferStatus.DRAFT)
                 * 
                 * .transferDate(
                 * LocalDateTime.now())
                 * 
                 * .createdBy(currentUser)
                 * 
                 * .remarks(request.getRemarks())
                 * 
                 * .build();
                 */

                StockTransfer transfer = StockTransfer.builder()

                                .transferNumber(
                                                documentNumberService.next(
                                                                DocumentType.STOCK_TRANSFER))

                                .sourceWarehouse(source)

                                .destinationWarehouse(destination)

                                .status(StockTransferStatus.DRAFT)

                                .transferDate(LocalDateTime.now())

                                .remarks(request.getRemarks())

                                .build();

                transfer = repository.save(transfer);

                return StockTransferMapper.toResponse(
                                transfer);
        }

        @Override
        @Transactional(readOnly = true)
        public List<StockTransferResponse> findAll() {

                return repository.findAll()

                                .stream()

                                .map(StockTransferMapper::toResponse)

                                .toList();
        }

        @Override
        @Transactional(readOnly = true)
        public StockTransferResponse findById(
                        UUID id) {

                StockTransfer transfer = repository.findById(id)

                                .orElseThrow(() -> new RuntimeException(
                                                "Transfer not found."));

                return StockTransferMapper.toResponse(
                                transfer);
        }

        @Override
        public StockTransferResponse approve(
                        UUID transferId) {

                StockTransfer transfer = repository.findById(transferId)

                                .orElseThrow(() -> new RuntimeException(
                                                "Stock Transfer not found."));

                if (transfer.getStatus() != StockTransferStatus.DRAFT) {

                        throw new RuntimeException(
                                        "Only Draft Stock Transfers can be approved.");
                }

                List<StockTransferLine> lines = lineRepository.findByStockTransferId(
                                transfer.getId());

                if (lines.isEmpty()) {

                        throw new RuntimeException(
                                        "Cannot approve a Stock Transfer without any lines.");
                }

                User currentUser = currentUserService.getCurrentUser();

                transfer.setStatus(
                                StockTransferStatus.APPROVED);

                transfer.setApprovedBy(
                                currentUser);

                transfer.setApprovedAt(
                                LocalDateTime.now());

                transfer = repository.save(
                                transfer);

                return StockTransferMapper.toResponse(
                                transfer);
        }

        @Override
        public void delete(
                        UUID transferId) {

                StockTransfer transfer = repository.findById(
                                transferId)

                                .orElseThrow(() -> new RuntimeException(
                                                "Stock Transfer not found."));

                if (transfer.getStatus() != StockTransferStatus.DRAFT) {

                        throw new RuntimeException(
                                        "Only Draft Stock Transfers can be deleted.");
                }

                repository.delete(
                                transfer);
        }

        @Override
        public StockTransferResponse cancel(
                        UUID transferId) {

                StockTransfer transfer = repository.findById(
                                transferId)

                                .orElseThrow(() -> new RuntimeException(
                                                "Stock Transfer not found."));

                if (transfer.getStatus() == StockTransferStatus.COMPLETED) {

                        throw new RuntimeException(
                                        "Completed transfers cannot be cancelled.");
                }

                transfer.setStatus(
                                StockTransferStatus.CANCELLED);

                transfer = repository.save(
                                transfer);

                return StockTransferMapper.toResponse(
                                transfer);
        }

        @Override
        @Transactional
        public StockTransferResponse issue(
                        UUID transferId) {

                StockTransfer transfer = repository.findById(transferId)

                                .orElseThrow(() -> new RuntimeException(
                                                "Stock Transfer not found."));

                if (transfer.getStatus() != StockTransferStatus.APPROVED) {

                        throw new RuntimeException(
                                        "Only approved Stock Transfers can be issued.");
                }

                List<StockTransferLine> lines = lineRepository.findByStockTransferId(
                                transfer.getId());

                if (lines.isEmpty()) {

                        throw new RuntimeException(
                                        "Transfer contains no lines.");
                }

                User currentUser = currentUserService.getCurrentUser();

                for (StockTransferLine line : lines) {

                        /*
                         * Source warehouse receiving/shipping bin.
                         */
                        Bin sourceBin = binRepository
                                        .findByWarehouseIdAndReceivingBinTrue(
                                                        transfer.getSourceWarehouse().getId())

                                        .orElseThrow(() -> new RuntimeException(
                                                        "Source warehouse receiving bin not configured."));

                        /*
                         * Destination warehouse receiving bin.
                         */
                        Bin destinationBin = binRepository
                                        .findByWarehouseIdAndReceivingBinTrue(
                                                        transfer.getDestinationWarehouse().getId())

                                        .orElseThrow(() -> new RuntimeException(
                                                        "Destination warehouse receiving bin not configured."));

                        /*
                         * Move inventory.
                         */
                        inventoryService.moveStock(

                                        transfer.getSourceWarehouse(),

                                        sourceBin,

                                        destinationBin,

                                        line.getProduct(),

                                        line.getQuantity(),

                                        transfer.getTransferNumber(),

                                        "STOCK_TRANSFER",

                                        "Warehouse Transfer",

                                        currentUser);
                }

                transfer.setStatus(
                                StockTransferStatus.IN_TRANSIT);

                transfer.setIssuedBy(
                                currentUser);

                transfer.setIssuedAt(
                                LocalDateTime.now());

                transfer = repository.save(
                                transfer);

                return StockTransferMapper.toResponse(
                                transfer);
        }

        @Override
        @Transactional
        public StockTransferResponse receive(
                        UUID transferId) {

                StockTransfer transfer = repository.findById(transferId)

                                .orElseThrow(() -> new RuntimeException(
                                                "Stock Transfer not found."));

                if (transfer.getStatus() != StockTransferStatus.IN_TRANSIT) {

                        throw new RuntimeException(
                                        "Only in-transit Stock Transfers can be received.");
                }

                List<StockTransferLine> lines = lineRepository.findByStockTransferId(
                                transfer.getId());

                if (lines.isEmpty()) {

                        throw new RuntimeException(
                                        "Transfer contains no lines.");
                }

                User currentUser = currentUserService.getCurrentUser();

                /*
                 * Inventory has already been moved during Issue().
                 * Receive() simply confirms completion.
                 */

                transfer.setStatus(
                                StockTransferStatus.RECEIVED);

                transfer.setReceivedBy(
                                currentUser);

                transfer.setReceivedAt(
                                LocalDateTime.now());

                transfer = repository.save(
                                transfer);

                return StockTransferMapper.toResponse(
                                transfer);
        }

}