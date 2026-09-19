package com.digipals.wms.finance.service;

import com.digipals.wms.common.exception.InvalidWorkflowException;
import com.digipals.wms.finance.entity.*;
import com.digipals.wms.finance.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.*;
import java.nio.charset.StandardCharsets;
import java.time.*;
import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional
public class FixedAssetAccountingService {
    private final FixedAssetRepository assets;
    private final AssetDepreciationRunRepository runs;
    private final FinancePostingService postingService;
    private final CostCenterRepository costCenters;

    public FixedAsset depreciate(UUID id, Integer months) {
        if (months != null && months != 1) throw new InvalidWorkflowException("Depreciation must be posted one fiscal period at a time.");
        FixedAsset asset = assets.findById(id).orElseThrow(() -> new InvalidWorkflowException("Fixed asset not found: " + id));
        if (!"ACTIVE".equalsIgnoreCase(asset.getStatus())) throw new InvalidWorkflowException("Asset is not active: " + asset.getAssetNumber());
        if (asset.getUsefulLifeMonths() == null || asset.getUsefulLifeMonths() < 1) throw new InvalidWorkflowException("Asset useful life must be greater than zero.");
        if (asset.getAcquisitionCost() == null || asset.getAcquisitionCost().compareTo(BigDecimal.ZERO) <= 0) throw new InvalidWorkflowException("Asset acquisition cost must be greater than zero.");
        if (asset.getCostCenterCode() != null) costCenters.findByCode(asset.getCostCenterCode()).filter(x -> Boolean.TRUE.equals(x.getActive()))
                .orElseThrow(() -> new InvalidWorkflowException("Active cost center not found: " + asset.getCostCenterCode()));

        LocalDate period = LocalDate.now().withDayOfMonth(1);
        if (runs.existsByAssetIdAndPeriodStart(id, period)) throw new InvalidWorkflowException("Depreciation has already been posted for " + asset.getAssetNumber() + " for " + period + ".");
        BigDecimal depreciable = asset.getAcquisitionCost().subtract(asset.getResidualValue() == null ? BigDecimal.ZERO : asset.getResidualValue()).max(BigDecimal.ZERO);
        BigDecimal remaining = depreciable.subtract(asset.getAccumulatedDepreciation() == null ? BigDecimal.ZERO : asset.getAccumulatedDepreciation()).max(BigDecimal.ZERO);
        if (remaining.compareTo(BigDecimal.ZERO) <= 0) {
            asset.setStatus("FULLY_DEPRECIATED");
            return assets.save(asset);
        }
        BigDecimal monthly = depreciable.divide(BigDecimal.valueOf(asset.getUsefulLifeMonths()), 2, RoundingMode.HALF_UP);
        BigDecimal amount = monthly.min(remaining).setScale(2, RoundingMode.HALF_UP);
        UUID referenceId = UUID.nameUUIDFromBytes(("DEPRECIATION:" + id + ":" + period).getBytes(StandardCharsets.UTF_8));
        AccountingDocument document = postingService.postBalanced(
                "DEPRECIATION", "FIXED_ASSET_DEPRECIATION", referenceId,
                asset.getAssetNumber() + "-" + period, "USD",
                "Monthly depreciation " + asset.getAssetNumber() + " " + period,
                List.of(
                        new FinancePostingService.PostingLine("610000", amount, BigDecimal.ZERO, asset.getCostCenterCode(), null, null, null, "Depreciation expense"),
                        new FinancePostingService.PostingLine("165000", BigDecimal.ZERO, amount, null, null, null, null, "Accumulated depreciation")
                ));
        asset.setAccumulatedDepreciation((asset.getAccumulatedDepreciation() == null ? BigDecimal.ZERO : asset.getAccumulatedDepreciation()).add(amount).min(depreciable));
        asset.setLastDepreciationDate(period);
        if (asset.getAccumulatedDepreciation().compareTo(depreciable) >= 0) asset.setStatus("FULLY_DEPRECIATED");
        assets.save(asset);
        runs.save(AssetDepreciationRun.builder().asset(asset).periodStart(period).amount(amount).accountingDocumentId(document.getId()).build());
        return asset;
    }
}