package com.GEProspect;

import java.util.Comparator;

public enum SortOption {
    PROFIT_MARGIN("Profit Margin", (a, b) -> Integer.compare(b.getProfitMargin(), a.getProfitMargin())),
    PRICE("Price", (a, b) -> Integer.compare(b.getHighPrice(), a.getHighPrice())),
    PROFIT_PER_HOUR("Profit/Hour", (a, b) -> {
        int profitA = a.getProfitMargin();
        int profitB = b.getProfitMargin();
        return Integer.compare(profitB, profitA);
    }),
    FLIP_TIME("Flip Time", (a, b) -> 0), // Will implement with volume data
    VOLUME("Volume", (a, b) -> 0);  // Will implement with volume data

    private final String displayName;
    private final Comparator<ItemPrice> comparator;

    SortOption(String displayName, Comparator<ItemPrice> comparator) {
        this.displayName = displayName;
        this.comparator = comparator;
    }

    public String getDisplayName() {
        return displayName;
    }

    public Comparator<ItemPrice> getComparator() {
        return comparator;
    }

    @Override
    public String toString() {
        return displayName;
    }
}