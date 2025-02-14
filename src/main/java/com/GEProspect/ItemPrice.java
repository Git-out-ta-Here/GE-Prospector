package com.GEProspect;

import lombok.Data;

@Data
public class ItemPrice {
    private final int itemId;
    private final int highPrice;
    private final int lowPrice;
    private final long timestamp;

    public int getProfitMargin() {
        return highPrice - lowPrice;
    }

    public double getProfitPercentage() {
        if (lowPrice == 0) return 0;
        return ((double) getProfitMargin() / lowPrice) * 100;
    }

    public boolean isValidPrice() {
        return highPrice > 0 && lowPrice > 0;
    }
}