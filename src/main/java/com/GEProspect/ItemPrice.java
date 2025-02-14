package com.GEProspect;

import lombok.Data;

@Data
public class ItemPrice {
    private final int itemId;
    private final String highPrice;
    private final String lowPrice;
    private final long timestamp;
    private String name;  // Add name field

    public ItemPrice(int itemId, String highPrice, String lowPrice, long timestamp) {
        this.itemId = itemId;
        this.highPrice = highPrice;
        this.lowPrice = lowPrice;
        this.timestamp = timestamp;
    }

    public int getItemId() {
        return itemId;
    }

    public String getHighPrice() {
        return highPrice;
    }

    public String getLowPrice() {
        return lowPrice;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public int getHighPriceAsInt() {
        try {
            return Integer.parseInt(highPrice);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    public int getLowPriceAsInt() {
        try {
            return Integer.parseInt(lowPrice);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    public int getProfitMargin() {
        return getHighPriceAsInt() - getLowPriceAsInt();
    }

    public double getProfitPercentage() {
        try {
            int low = getLowPriceAsInt();
            if (low == 0) return 0;
            return ((double) getProfitMargin() / low) * 100;
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    public boolean isValidPrice() {
        try {
            int high = getHighPriceAsInt();
            int low = getLowPriceAsInt();
            return high > 0 && low > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}