package com.prospector.service;

import com.google.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.events.GrandExchangeOfferChanged;
import com.prospector.model.GEOffer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.inject.Inject;

@Slf4j
@Singleton
public class GETrackingService {
    private final Map<Integer, GEOffer> activeOffers = new ConcurrentHashMap<>();
    private final List<GEOffer> offerHistory = new ArrayList<>();
    private static final int MAX_HISTORY_SIZE = 100;

    public void trackOffer(GrandExchangeOfferChanged event) {
        GEOffer offer = GEOffer.fromRuneliteOffer(event.getOffer(), event.getSlot());
        activeOffers.put(event.getSlot(), offer);
        addToHistory(offer);
        log.debug("Tracked new GE offer: {}", offer);
    }

    public GEOffer getActiveOffer(int slot) {
        return activeOffers.get(slot);
    }

    public List<GEOffer> getOfferHistory() {
        return new ArrayList<>(offerHistory);
    }

    private void addToHistory(GEOffer offer) {
        offerHistory.add(0, offer);
        if (offerHistory.size() > MAX_HISTORY_SIZE) {
            offerHistory.remove(offerHistory.size() - 1);
        }
    }

    public void reset() {
        activeOffers.clear();
        offerHistory.clear();
    }
}