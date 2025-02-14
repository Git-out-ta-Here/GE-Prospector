package com.GEProspect;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.GrandExchangeOffer;
import net.runelite.api.GrandExchangeOfferState;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.*;

@Slf4j
@Singleton
public class FlipTracker {
    private final Map<Integer, GrandExchangeOffer> buyOffers = new HashMap<>();
    private final Map<Integer, GrandExchangeOffer> sellOffers = new HashMap<>();
    private final ProfitTracker profitTracker;

    @Inject
    public FlipTracker(ProfitTracker profitTracker) {
        this.profitTracker = profitTracker;
    }

    public void processOffer(GrandExchangeOffer offer) {
        if (offer == null) return;
        
        // In RuneLite, we need to maintain our own slot tracking
        int slot = offer.getItemId(); // Using itemId as unique identifier since slot isn't available
        GrandExchangeOfferState state = offer.getState();
        
        switch (state) {
            case BUYING:
                buyOffers.put(slot, offer);
                break;
            case SELLING:
                sellOffers.put(slot, offer);
                break;
            case BOUGHT:
            case SOLD:
                checkForCompletedFlip(slot, offer);
                break;
            case CANCELLED_BUY:
                buyOffers.remove(slot);
                break;
            case CANCELLED_SELL:
                sellOffers.remove(slot);
                break;
        }
    }

    private void checkForCompletedFlip(int slot, GrandExchangeOffer offer) {
        if (offer.getState() == GrandExchangeOfferState.BOUGHT) {
            buyOffers.put(slot, offer);
        } else if (offer.getState() == GrandExchangeOfferState.SOLD) {
            GrandExchangeOffer buyOffer = buyOffers.get(slot);
            if (buyOffer != null && buyOffer.getItemId() == offer.getItemId()) {
                int buyPrice = buyOffer.getPrice();
                int sellPrice = offer.getPrice();
                int quantity = Math.min(buyOffer.getTotalQuantity(), offer.getTotalQuantity());
                int profit = (sellPrice - buyPrice) * quantity;
                
                FlipEntry flip = new FlipEntry(
                    offer.getItemId(),
                    buyPrice,
                    sellPrice,
                    quantity,
                    profit
                );
                
                profitTracker.addFlip(flip);
                log.debug("Completed flip: {}", flip);
                
                // Clear the offers
                buyOffers.remove(slot);
                sellOffers.remove(slot);
            }
        }
    }

    public List<FlipEntry> getActiveFlips() {
        List<FlipEntry> activeFlips = new ArrayList<>();
        for (GrandExchangeOffer offer : buyOffers.values()) {
            activeFlips.add(new FlipEntry(
                offer.getItemId(),
                offer.getPrice(),
                0,
                offer.getTotalQuantity(),
                0
            ));
        }
        for (GrandExchangeOffer offer : sellOffers.values()) {
            activeFlips.add(new FlipEntry(
                offer.getItemId(),
                0,
                offer.getPrice(),
                offer.getTotalQuantity(),
                0
            ));
        }
        return activeFlips;
    }

    public List<FlipEntry> getFlips() {
        return profitTracker.getHistoricalFlips();
    }

    public EstimatedTime getEstimatedTime(int itemId, int quantity) {
        // Simple implementation - we'll enhance this later with real volume data
        return EstimatedTime.MEDIUM;
    }
}