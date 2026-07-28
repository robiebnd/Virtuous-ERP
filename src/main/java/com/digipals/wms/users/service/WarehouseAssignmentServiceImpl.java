package com.digipals.wms.users.service;

import com.digipals.wms.common.mapper.WarehouseAssignmentMapper;
import com.digipals.wms.users.dto.CreateWarehouseAssignmentRequest;
import com.digipals.wms.users.dto.WarehouseAssignmentResponse;
import com.digipals.wms.users.entity.User;
import com.digipals.wms.users.entity.WarehouseAssignment;
import com.digipals.wms.users.repository.UserRepository;
import com.digipals.wms.users.repository.WarehouseAssignmentRepository;
import com.digipals.wms.warehouse.entity.Warehouse;
import com.digipals.wms.warehouse.repository.WarehouseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class WarehouseAssignmentServiceImpl
        implements WarehouseAssignmentService {

    private final WarehouseAssignmentRepository repository;

    private final UserRepository userRepository;

    private final WarehouseRepository warehouseRepository;

    @Override
    public WarehouseAssignmentResponse assignWarehouse(
            CreateWarehouseAssignmentRequest request) {

        if (repository.existsByUserIdAndWarehouseId(
                request.getUserId(),
                request.getWarehouseId())) {

            throw new RuntimeException(
                    "Warehouse already assigned to user.");
        }

        User user =
                userRepository.findById(request.getUserId())
                        .orElseThrow(() ->
                                new RuntimeException("User not found."));

        Warehouse warehouse =
                warehouseRepository.findById(request.getWarehouseId())
                        .orElseThrow(() ->
                                new RuntimeException("Warehouse not found."));

        if (Boolean.TRUE.equals(request.getPrimaryWarehouse())) {

            repository.findByUserId(user.getId())
                    .forEach(existing ->
                            existing.setPrimaryWarehouse(false));
        }

        WarehouseAssignment assignment =
                WarehouseAssignment.builder()
                        .user(user)
                        .warehouse(warehouse)
                        .primaryWarehouse(
                                Boolean.TRUE.equals(
                                        request.getPrimaryWarehouse()))
                        .build();

        assignment = repository.save(assignment);

        return WarehouseAssignmentMapper.toResponse(
                assignment);
    }

    @Override
    @Transactional(readOnly = true)
    public List<WarehouseAssignmentResponse> findAll() {

        return repository.findAll()
                .stream()
                .map(WarehouseAssignmentMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<WarehouseAssignmentResponse> findByUser(
            UUID userId) {

        return repository.findByUserId(userId)
                .stream()
                .map(WarehouseAssignmentMapper::toResponse)
                .toList();
    }

    @Override
    public void removeWarehouse(
            UUID userId,
            UUID warehouseId) {

        repository.deleteByUserIdAndWarehouseId(
                userId,
                warehouseId);
    }
}