package com.GEProspect;

import lombok.Data;

@Data
public class ItemPrice {
    private final int itemId;
    private final String name;
    private final int lowPrice;
    private final int highPrice;
    private final int profitMargin;

    public ItemPrice(int itemId, String name, int lowPrice, int highPrice) {
        this.itemId = itemId;
        this.name = name;
        this.lowPrice = lowPrice;
        this.highPrice = highPrice;
        this.profitMargin = highPrice - lowPrice;
    }

    public int getItemId() {
        return itemId;
    }

    public String getName() {
        return name;
    }

    public int getLowPrice() {
        return lowPrice;
    }

    public int getHighPrice() {
        return highPrice;
    }

    public int getProfitMargin() {
        return profitMargin;
    }

    public double getProfitPercentage() {
        if (lowPrice == 0) return 0;
        return ((double) getProfitMargin() / lowPrice) * 100;
    }

    public boolean isValidPrice() {
        return highPrice > 0 && lowPrice > 0;
    }
}