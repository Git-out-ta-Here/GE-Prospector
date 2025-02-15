package com.geprospect ;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class FlipEntry {
    private final int itemId;
    private final int buyPrice;
    private final int sellPrice;
    private final int quantity;
    private final int profit;
    private final LocalDateTime timestamp;

    public FlipEntry(int itemId, int buyPrice, int sellPrice, int quantity, int profit) {
        this.itemId = itemId;
        this.buyPrice = buyPrice;
        this.sellPrice = sellPrice;
        this.quantity = quantity;
        this.profit = profit;
        this.timestamp = LocalDateTime.now();
    }

    public int getProfitPerItem() {
        return sellPrice - buyPrice;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }
}