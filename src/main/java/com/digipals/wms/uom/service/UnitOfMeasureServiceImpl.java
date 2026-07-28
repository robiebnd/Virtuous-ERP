package com.digipals.wms.uom.service;

import com.digipals.wms.common.exception.DuplicateResourceException;
import com.digipals.wms.common.exception.ResourceNotFoundException;
import com.digipals.wms.common.mapper.UnitOfMeasureMapper;
import com.digipals.wms.uom.dto.CreateUnitOfMeasureRequest;
import com.digipals.wms.uom.dto.UnitOfMeasureResponse;
import com.digipals.wms.uom.dto.UpdateUnitOfMeasureRequest;
import com.digipals.wms.uom.entity.UnitOfMeasure;
import com.digipals.wms.uom.repository.UnitOfMeasureRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UnitOfMeasureServiceImpl
        implements UnitOfMeasureService {

    private final UnitOfMeasureRepository repository;

    private UnitOfMeasure findUnit(UUID id) {

        return repository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Unit of Measure not found."));
    }

    @Override
    public UnitOfMeasureResponse create(
            CreateUnitOfMeasureRequest request) {

        if (repository.existsByCode(request.getCode())) {

            throw new DuplicateResourceException(
                    "Unit code already exists.");
        }

        UnitOfMeasure unit =
                UnitOfMeasure.builder()

                        .code(request.getCode())

                        .name(request.getName())

                        .description(request.getDescription())

                        .active(request.getActive())

                        .build();

        unit = repository.save(unit);

        return UnitOfMeasureMapper.toResponse(unit);
    }

    @Override
    public UnitOfMeasureResponse update(
            UUID id,
            UpdateUnitOfMeasureRequest request) {

        UnitOfMeasure unit = findUnit(id);

        unit.setName(request.getName());

        unit.setDescription(request.getDescription());

        unit.setActive(request.getActive());

        unit = repository.save(unit);

        return UnitOfMeasureMapper.toResponse(unit);
    }

    @Override
    public UnitOfMeasureResponse findById(UUID id) {

        return UnitOfMeasureMapper.toResponse(
                findUnit(id));
    }

    @Override
    public UnitOfMeasureResponse findByCode(
            String code) {

        UnitOfMeasure unit =
                repository.findByCode(code)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Unit of Measure not found."));

        return UnitOfMeasureMapper.toResponse(unit);
    }

    @Override
    public List<UnitOfMeasureResponse> findAll() {

        return repository.findAll()

                .stream()

                .map(UnitOfMeasureMapper::toResponse)

                .toList();
    }

    @Override
    public List<UnitOfMeasureResponse> findActive() {

        return repository.findByActiveTrue()

                .stream()

                .map(UnitOfMeasureMapper::toResponse)

                .toList();
    }

    @Override
    public void delete(UUID id) {

        UnitOfMeasure unit = findUnit(id);

        repository.delete(unit);
    }
}