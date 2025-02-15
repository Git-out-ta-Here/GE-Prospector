package com.prospector.service;

import com.google.inject.Singleton;
import net.runelite.api.ItemComposition;
import net.runelite.client.game.ItemManager;

import javax.inject.Inject;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Singleton
public class CacheService {
    private static final long PRICE_CACHE_TTL = TimeUnit.MINUTES.toMillis(1);
    private static final long HISTORY_CACHE_TTL = TimeUnit.HOURS.toMillis(1);
    private static final long ITEM_CACHE_TTL = TimeUnit.HOURS.toMillis(24);

    private final ItemManager itemManager;
    
    private final Map<Integer, CacheEntry<Long>> priceCache = new ConcurrentHashMap<>();
    private final Map<Integer, CacheEntry<ItemComposition>> itemCache = new ConcurrentHashMap<>();
    private final Map<Integer, CacheEntry<Map<Long, Long>>> historyCache = new ConcurrentHashMap<>();

    @Inject
    public CacheService(ItemManager itemManager) {
        this.itemManager = itemManager;
    }

    public void cachePrice(int itemId, long price) {
        priceCache.put(itemId, new CacheEntry<>(price));
    }

    public Long getCachedPrice(int itemId) {
        CacheEntry<Long> entry = priceCache.get(itemId);
        if (entry != null && !entry.isExpired(PRICE_CACHE_TTL)) {
            return entry.getValue();
        }
        priceCache.remove(itemId);
        return null;
    }

    public void cacheItemHistory(int itemId, Map<Long, Long> history) {
        historyCache.put(itemId, new CacheEntry<>(history));
    }

    public Map<Long, Long> getCachedHistory(int itemId) {
        CacheEntry<Map<Long, Long>> entry = historyCache.get(itemId);
        if (entry != null && !entry.isExpired(HISTORY_CACHE_TTL)) {
            return entry.getValue();
        }
        historyCache.remove(itemId);
        return null;
    }

    public ItemComposition getCachedItem(int itemId) {
        CacheEntry<ItemComposition> entry = itemCache.get(itemId);
        if (entry != null && !entry.isExpired(ITEM_CACHE_TTL)) {
            return entry.getValue();
        }
        
        ItemComposition item = itemManager.getItemComposition(itemId);
        itemCache.put(itemId, new CacheEntry<>(item));
        return item;
    }

    public void clearExpiredEntries() {
        clearExpired(priceCache, PRICE_CACHE_TTL);
        clearExpired(historyCache, HISTORY_CACHE_TTL);
        clearExpired(itemCache, ITEM_CACHE_TTL);
    }

    private <T> void clearExpired(Map<Integer, CacheEntry<T>> cache, long ttl) {
        cache.entrySet().removeIf(entry -> entry.getValue().isExpired(ttl));
    }

    private static class CacheEntry<T> {
        private final T value;
        private final long timestamp;

        public CacheEntry(T value) {
            this.value = value;
            this.timestamp = System.currentTimeMillis();
        }

        public T getValue() {
            return value;
        }

        public boolean isExpired(long ttl) {
            return System.currentTimeMillis() - timestamp > ttl;
        }
    }

    public void reset() {
        priceCache.clear();
        historyCache.clear();
        itemCache.clear();
    }
}