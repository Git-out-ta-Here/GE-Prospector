package com.prospector.model;

import lombok.Data;
import lombok.Builder;
import net.runelite.api.GrandExchangeOffer;
import net.runelite.api.GrandExchangeOfferState;

import java.time.Instant;

@Data
@Builder
public class GEOffer {
    private final int itemId;
    private final int quantity;
    private final int price;
    private final GrandExchangeOfferState state;
    private final boolean isBuy;
    private final Instant timestamp;
    private final int slot;

    public static GEOffer fromRuneliteOffer(GrandExchangeOffer offer, int slot) {
        return GEOffer.builder()
                .itemId(offer.getItemId())
                .quantity(offer.getQuantity())
                .price(offer.getPrice())
                .state(offer.getState())
                .isBuy(offer.isBuy())
                .timestamp(Instant.now())
                .slot(slot)
                .build();
    }
}