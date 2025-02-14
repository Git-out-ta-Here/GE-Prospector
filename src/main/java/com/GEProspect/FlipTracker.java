package com.GEProspect;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.GrandExchangeOffer;
import net.runelite.api.GrandExchangeOfferState;

import javax.inject.Singleton;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.time.Instant;

@Slf4j
@Singleton
public class FlipTracker {
    private final Map<Integer, FlipEntry> activeFlips;
    private final Map<Integer, TradeVolume> volumeData;

    public FlipTracker() {
        this.activeFlips = new ConcurrentHashMap<>();
        this.volumeData = new ConcurrentHashMap<>();
    }

    public void processOffer(GrandExchangeOffer offer) {
        int itemId = offer.getItemId();
        
        switch (offer.getState()) {
            case BUYING:
            case SELLING:
                trackActiveOffer(offer);
                break;
                
            case BOUGHT:
            case SOLD:
                completeOffer(offer);
                updateTradeVolume(itemId, offer.getTotalQuantity());
                break;
                
            case CANCELLED_BUY:
            case CANCELLED_SELL:
                cancelOffer(offer);
                break;
        }
    }

    private void trackActiveOffer(GrandExchangeOffer offer) {
        FlipEntry entry = activeFlips.computeIfAbsent(
            offer.getItemId(),
            k -> new FlipEntry(offer.getItemId(), offer.getState() == GrandExchangeOfferState.BUYING)
        );
        entry.updateProgress(offer.getQuantitySold(), offer.getTotalQuantity());
    }

    private void completeOffer(GrandExchangeOffer offer) {
        FlipEntry entry = activeFlips.get(offer.getItemId());
        if (entry != null) {
            entry.complete(offer.getPrice());
            if (entry.isFlipComplete()) {
                activeFlips.remove(offer.getItemId());
            }
        }
    }

    private void cancelOffer(GrandExchangeOffer offer) {
        activeFlips.remove(offer.getItemId());
    }

    private void updateTradeVolume(int itemId, int quantity) {
        TradeVolume volume = volumeData.computeIfAbsent(itemId, k -> new TradeVolume());
        volume.addTrade(quantity);
    }

    public EstimatedTime getEstimatedTime(int itemId, int quantity) {
        TradeVolume volume = volumeData.get(itemId);
        if (volume == null) {
            return new EstimatedTime(60, 0.5); // Default 60 minutes with 50% confidence
        }
        return volume.calculateEstimatedTime(quantity);
    }

    public Map<Integer, FlipEntry> getActiveFlips() {
        return new ConcurrentHashMap<>(activeFlips);
    }
}