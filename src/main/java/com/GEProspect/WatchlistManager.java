package com.GEProspect;

import lombok.extern.slf4j.Slf4j;
import lombok.Data;
import lombok.AllArgsConstructor;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.client.Notifier;
import net.runelite.client.chat.ChatColorType;
import net.runelite.client.chat.ChatMessageBuilder;
import net.runelite.client.chat.ChatMessageManager;
import net.runelite.client.chat.QueuedMessage;
import net.runelite.client.config.ConfigManager;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import javax.inject.Singleton;
import javax.inject.Inject;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.lang.reflect.Type;

@Slf4j
@Singleton
public class WatchlistManager {
    private static final String CONFIG_GROUP = "geprospector";
    private static final String WATCHLIST_KEY = "watchlist";

    private final Map<Integer, WatchedItem> watchlist = new ConcurrentHashMap<>();
    private final MarketDataService marketDataService;
    private final GEProspectorConfig config;
    private final Client client;
    private final Notifier notifier;
    private final ChatMessageManager chatMessageManager;
    private final ConfigManager configManager;
    private final Gson gson;

    @Inject
    public WatchlistManager(
            MarketDataService marketDataService,
            GEProspectorConfig config,
            Client client,
            Notifier notifier,
            ChatMessageManager chatMessageManager,
            ConfigManager configManager) {
        this.marketDataService = marketDataService;
        this.config = config;
        this.client = client;
        this.notifier = notifier;
        this.chatMessageManager = chatMessageManager;
        this.configManager = configManager;
        this.gson = new Gson();
        
        loadWatchlist();
    }

    public void addToWatchlist(int itemId, String itemName, int targetPrice, boolean alertOnAbove) {
        if (watchlist.size() >= config.watchlistSize()) {
            log.debug("Watchlist full (max size: {})", config.watchlistSize());
            return;
        }
        
        WatchedItem item = new WatchedItem(itemId, itemName, targetPrice, alertOnAbove);
        watchlist.put(itemId, item);
        saveWatchlist();
        log.debug("Added item to watchlist: {}", itemName);
    }

    public void removeFromWatchlist(int itemId) {
        WatchedItem removed = watchlist.remove(itemId);
        if (removed != null) {
            saveWatchlist();
            log.debug("Removed item from watchlist: {}", removed.getItemName());
        }
    }

    private void loadWatchlist() {
        String json = configManager.getConfiguration(CONFIG_GROUP, WATCHLIST_KEY);
        if (json != null) {
            Type type = new TypeToken<Map<Integer, WatchedItem>>(){}.getType();
            Map<Integer, WatchedItem> loaded = gson.fromJson(json, type);
            watchlist.putAll(loaded);
            log.debug("Loaded {} items from watchlist", loaded.size());
        }
    }

    private void saveWatchlist() {
        String json = gson.toJson(watchlist);
        configManager.setConfiguration(CONFIG_GROUP, WATCHLIST_KEY, json);
        log.debug("Saved {} items to watchlist", watchlist.size());
    }

    public List<WatchedItem> getWatchedItems() {
        return new ArrayList<>(watchlist.values());
    }

    public void checkAlerts() {
        if (!config.enableAlerts()) {
            return;
        }

        for (WatchedItem item : watchlist.values()) {
            boolean triggered = checkPriceAlert(item);
                
            if (triggered && !item.isAlertTriggered()) {
                item.setAlertTriggered(true);
                sendAlert(item);
            } else if (!triggered && item.isAlertTriggered()) {
                // Reset trigger if price moves back
                item.setAlertTriggered(false);
            }
        }
    }

    private boolean checkPriceAlert(WatchedItem item) {
        ItemPrice currentPrice = marketDataService.getPrice(item.getItemId());
        if (currentPrice == null) return false;
        
        return item.isAlertOnAbove()
            ? currentPrice.getHighPriceAsInt() >= item.getTargetPrice()
            : currentPrice.getLowPriceAsInt() <= item.getTargetPrice();
    }

    private void sendAlert(WatchedItem item) {
        ItemPrice currentPrice = marketDataService.getPrice(item.getItemId());
        if (currentPrice == null) return;

        String message = String.format("%s has %s %,d gp (target: %,d)",
            item.getItemName(),
            item.isAlertOnAbove() ? "risen above" : "fallen below",
            item.isAlertOnAbove() ? currentPrice.getHighPriceAsInt() : currentPrice.getLowPriceAsInt(),
            item.getTargetPrice()
        );

        log.info("Price alert: {}", message);

        if (config.chatAlerts()) {
            String chatMessage = new ChatMessageBuilder()
                .append(ChatColorType.HIGHLIGHT)
                .append("Price Alert: ")
                .append(ChatColorType.NORMAL)
                .append(message)
                .build();

            chatMessageManager.queue(QueuedMessage.builder()
                .type(ChatMessageType.GAMEMESSAGE)
                .runeLiteFormattedMessage(chatMessage)
                .build());
        }

        if (config.alertSound()) {
            notifier.notify(message);
        }
    }

    @Data
    @AllArgsConstructor
    public static class WatchedItem {
        private final int itemId;
        private final String itemName;
        private final int targetPrice;
        private final boolean alertOnAbove;
        private boolean alertTriggered;

        public WatchedItem(int itemId, String itemName, int targetPrice, boolean alertOnAbove) {
            this.itemId = itemId;
            this.itemName = itemName;
            this.targetPrice = targetPrice;
            this.alertOnAbove = alertOnAbove;
            this.alertTriggered = false;
        }
    }
}