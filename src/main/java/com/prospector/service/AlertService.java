package com.prospector.service;

import com.google.inject.Singleton;
import com.prospector.model.PriceAlert;
import com.prospector.model.FlipOpportunity;
import com.prospector.util.ProspectorUtil;
import net.runelite.api.events.GrandExchangeOfferChanged;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.game.ItemManager;

import javax.inject.Inject;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Singleton
public class AlertService {
    private final AlertManager alertManager;
    private final ItemManager itemManager;
    private final ProspectorUtil prospectorUtil;
    private final Map<Integer, Long> lastPrices = new ConcurrentHashMap<>();

    @Inject
    public AlertService(
            AlertManager alertManager,
            ItemManager itemManager,
            ProspectorUtil prospectorUtil) {
        this.alertManager = alertManager;
        this.itemManager = itemManager;
        this.prospectorUtil = prospectorUtil;
    }

    public void checkPrice(int itemId, long currentPrice) {
        List<PriceAlert> alerts = alertManager.getAlerts(itemId);
        if (alerts.isEmpty()) {
            return;
        }

        Long lastPrice = lastPrices.get(itemId);
        lastPrices.put(itemId, currentPrice);

        for (PriceAlert alert : alerts) {
            if (!alert.isEnabled()) {
                continue;
            }

            boolean wasTriggered = alert.isTriggered(lastPrice != null ? lastPrice : currentPrice);
            boolean isTriggered = alert.isTriggered(currentPrice);

            if (isTriggered && !wasTriggered) {
                sendAlert(alert, currentPrice);
            }
        }
    }

    public void checkOpportunity(FlipOpportunity opportunity) {
        List<PriceAlert> alerts = alertManager.getAlerts(opportunity.getItemId());
        if (alerts.isEmpty()) {
            return;
        }

        for (PriceAlert alert : alerts) {
            if (!alert.isEnabled() || alert.getType() != PriceAlert.AlertType.MARGIN) {
                continue;
            }

            if (alert.isTriggered(opportunity.getMargin())) {
                sendMarginAlert(alert, opportunity);
            }
        }
    }

    private void sendAlert(PriceAlert alert, long currentPrice) {
        String message = String.format("%s alert: %s is %s %s (Current: %s)",
            alert.getType(),
            alert.getItemName(),
            alert.getCondition().toString().toLowerCase(),
            ProspectorUtil.formatPrice(alert.getTargetPrice()),
            ProspectorUtil.formatPrice(currentPrice));

        prospectorUtil.sendNotification(message);
    }

    private void sendMarginAlert(PriceAlert alert, FlipOpportunity opportunity) {
        String message = String.format("Margin alert: %s has margin of %s (ROI: %.1f%%)",
            alert.getItemName(),
            ProspectorUtil.formatPrice(opportunity.getMargin()),
            opportunity.getRoi());

        prospectorUtil.sendNotification(message);
    }

    public void handleGrandExchangeOfferChanged(GrandExchangeOfferChanged event) {
        int itemId = event.getItemId();
        long price = event.getOffer().getPrice();
        checkPrice(itemId, price);
    }

    public void reset() {
        lastPrices.clear();
    }
}