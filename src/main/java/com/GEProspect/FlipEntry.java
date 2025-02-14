package com.GEProspect;

import lombok.Data;
import net.runelite.api.GrandExchangeOfferState;

@Data
public class FlipEntry {
    private final int itemId;
    private final boolean isBuy;
    private int quantity;
    private int totalQuantity;
    private int buyPrice;
    private int sellPrice;
    private long startTime;
    private long completionTime;

    public FlipEntry(int itemId, boolean isBuy) {
        this.itemId = itemId;
        this.isBuy = isBuy;
        this.startTime = System.currentTimeMillis();
    }

    public void updateProgress(int completed, int total) {
        this.quantity = completed;
        this.totalQuantity = total;
    }

    public void complete(int price) {
        if (isBuy) {
            buyPrice = price;
        } else {
            sellPrice = price;
            completionTime = System.currentTimeMillis();
        }
    }

    public boolean isFlipComplete() {
        return buyPrice > 0 && sellPrice > 0;
    }

    public int getProfit() {
        if (!isFlipComplete()) return 0;
        return (sellPrice - buyPrice) * quantity;
    }

    public long getDuration() {
        if (completionTime == 0) return System.currentTimeMillis() - startTime;
        return completionTime - startTime;
    }
}