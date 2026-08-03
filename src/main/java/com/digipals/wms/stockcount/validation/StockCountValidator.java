package com.digipals.wms.stockcount.validation;

import com.digipals.wms.stockcount.entity.StockCount;
import com.digipals.wms.stockcount.entity.StockCountStatus;
import org.springframework.stereotype.Component;

@Component
public class StockCountValidator {

    public void validateEditable(StockCount stockCount) {

        if (stockCount == null) {
            throw new RuntimeException("Stock Count not found.");
        }

        switch (stockCount.getStatus()) {

            case COUNT_COMPLETED:
            case ADJUSTMENT_CREATED:
            case RECONCILED:

                throw new RuntimeException(
                        "Stock Count "
                                + stockCount.getCountNumber()
                                + " is locked and cannot be modified.");

            default:
                // editable
        }
    }

  public void validateCanGenerateAdjustment(StockCount stockCount) {

    if (stockCount.getStatus() == StockCountStatus.ADJUSTMENT_CREATED) {

        throw new RuntimeException(
                "A Stock Adjustment has already been generated for this Stock Count.");
    }

    if (stockCount.getStatus() == StockCountStatus.RECONCILED) {

        throw new RuntimeException(
                "This Stock Count has already been reconciled.");
    }

    if (stockCount.getStatus() != StockCountStatus.COUNT_COMPLETED) {

        throw new RuntimeException(
                "Only completed Stock Counts can generate Stock Adjustments.");
    }
}

    public void validateCanComplete(StockCount stockCount) {

        if (stockCount.getStatus() == StockCountStatus.RECONCILED) {

            throw new RuntimeException(
                    "A reconciled Stock Count cannot be reopened.");
        }
    }
}