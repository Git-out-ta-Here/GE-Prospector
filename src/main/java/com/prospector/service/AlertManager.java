package com.prospector.service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.prospector.model.PriceAlert;
import com.prospector.api.PriceApiClient;
import com.prospector.util.ProspectorUtil;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.game.ItemManager;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Singleton
public class AlertManager {
    private static final String CONFIG_GROUP = "geprospector";
    private static final String ALERTS_KEY = "priceAlerts";
    
    private final ConfigManager configManager;
    private final PriceApiClient priceApiClient;
    private final ItemManager itemManager;
    private final ProspectorUtil prospectorUtil;
    private final Gson gson;
    private final Map<Integer, List<PriceAlert>> alertsByItem;
    private final Set<Integer> triggeredAlerts;

    @Inject
    public AlertManager(
            ConfigManager configManager,
            PriceApiClient priceApiClient,
            ItemManager itemManager,
            ProspectorUtil prospectorUtil) {
        this.configManager = configManager;
        this.priceApiClient = priceApiClient;
        this.itemManager = itemManager;
        this.prospectorUtil = prospectorUtil;
        this.gson = new Gson();
        this.alertsByItem = new ConcurrentHashMap<>();
        this.triggeredAlerts = Collections.newSetFromMap(new ConcurrentHashMap<>());
        
        loadAlerts();
    }

    public void addAlert(PriceAlert alert) {
        alertsByItem.computeIfAbsent(alert.getItemId(), k -> new CopyOnWriteArrayList<>())
            .add(alert);
        saveAlerts();
        prospectorUtil.sendNotification("Alert added for " + alert.getItemName());
    }

    public void removeAlert(int itemId, PriceAlert alert) {
        List<PriceAlert> alerts = alertsByItem.get(itemId);
        if (alerts != null) {
            alerts.remove(alert);
            if (alerts.isEmpty()) {
                alertsByItem.remove(itemId);
            }
        }
        triggeredAlerts.remove(alert.hashCode());
        saveAlerts();
    }

    public List<PriceAlert> getAlerts(int itemId) {
        return alertsByItem.getOrDefault(itemId, Collections.emptyList());
    }

    public List<PriceAlert> getAllAlerts() {
        List<PriceAlert> allAlerts = new ArrayList<>();
        alertsByItem.values().forEach(allAlerts::addAll);
        return allAlerts;
    }

    public void checkAlerts() {
        if (alertsByItem.isEmpty()) {
            return;
        }

        priceApiClient.getLatestPrices().thenAccept(prices -> {
            for (Map.Entry<Integer, List<PriceAlert>> entry : alertsByItem.entrySet()) {
                int itemId = entry.getKey();
                Long currentPrice = prices.get(itemId);
                
                if (currentPrice != null) {
                    checkAlertsForItem(itemId, currentPrice);
                }
            }
        });
    }

    private void checkAlertsForItem(int itemId, long currentPrice) {
        List<PriceAlert> alerts = alertsByItem.get(itemId);
        if (alerts == null) return;

        for (PriceAlert alert : alerts) {
            if (!alert.isEnabled()) continue;

            int alertHash = alert.hashCode();
            boolean wasTriggered = triggeredAlerts.contains(alertHash);
            boolean isTriggered = alert.isTriggered(currentPrice);

            if (isTriggered && !wasTriggered) {
                triggeredAlerts.add(alertHash);
                sendAlertNotification(alert, currentPrice);
            } else if (!isTriggered && wasTriggered) {
                triggeredAlerts.remove(alertHash);
            }
        }
    }

    private void sendAlertNotification(PriceAlert alert, long currentPrice) {
        String message = String.format("%s alert: %s is %s %s (Current: %s)",
            alert.getType(),
            alert.getItemName(),
            alert.getCondition().toString().toLowerCase(),
            ProspectorUtil.formatPrice(alert.getTargetPrice()),
            ProspectorUtil.formatPrice(currentPrice));
        
        prospectorUtil.sendNotification(message);
    }

    private void saveAlerts() {
        List<PriceAlert> allAlerts = getAllAlerts();
        String json = gson.toJson(allAlerts);
        configManager.setConfiguration(CONFIG_GROUP, ALERTS_KEY, json);
    }

    private void loadAlerts() {
        String json = configManager.getConfiguration(CONFIG_GROUP, ALERTS_KEY);
        if (json != null && !json.isEmpty()) {
            Type listType = new TypeToken<ArrayList<PriceAlert>>(){}.getType();
            List<PriceAlert> alerts = gson.fromJson(json, listType);
            
            alerts.forEach(alert -> 
                alertsByItem.computeIfAbsent(alert.getItemId(), k -> new CopyOnWriteArrayList<>())
                    .add(alert));
        }
    }
}