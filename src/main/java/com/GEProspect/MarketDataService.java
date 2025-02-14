package com.GEProspect;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.IOException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.List;
import java.util.ArrayList;

@Slf4j
@Singleton
public class MarketDataService {
    private static final String PRICES_API_URL = "https://prices.runescape.wiki/api/v1/osrs/latest";
    private static final int REFRESH_INTERVAL = 60; // seconds

    private final OkHttpClient httpClient;
    private final Gson gson;
    private final ScheduledExecutorService executor;
    private final GEProspectorConfig config;
    private final Map<Integer, ItemPrice> priceCache;

    @Inject
    public MarketDataService(ScheduledExecutorService executor, GEProspectorConfig config) {
        this.executor = executor;
        this.config = config;
        this.httpClient = new OkHttpClient();
        this.gson = new Gson();
        this.priceCache = new ConcurrentHashMap<>();
        
        scheduleRegularUpdates();
    }

    private void scheduleRegularUpdates() {
        executor.scheduleAtFixedRate(
            this::refreshPrices,
            0,
            REFRESH_INTERVAL,
            TimeUnit.SECONDS
        );
    }

    public void refreshPrices() {
        try {
            Request request = new Request.Builder()
                .url(PRICES_API_URL)
                .header("User-Agent", "GE-Prospector RuneLite Plugin")
                .build();

            try (Response response = httpClient.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    log.warn("Failed to fetch prices: " + response.code());
                    return;
                }

                String responseBody = response.body().string();
                JsonObject data = JsonParser.parseString(responseBody).getAsJsonObject().getAsJsonObject("data");
                
                updatePriceCache(data);
            }
        } catch (IOException e) {
            log.error("Error fetching prices", e);
        }
    }

    private void updatePriceCache(JsonObject data) {
        data.entrySet().forEach(entry -> {
            try {
                int itemId = Integer.parseInt(entry.getKey());
                JsonObject priceData = entry.getValue().getAsJsonObject();
                
                ItemPrice price = new ItemPrice(
                    itemId,
                    priceData.get("high").toString(),
                    priceData.get("low").toString(),
                    System.currentTimeMillis()
                );
                
                priceCache.put(itemId, price);
            } catch (NumberFormatException e) {
                log.warn("Invalid item ID in price data", e);
            }
        });
    }

    public ItemPrice getPrice(int itemId) {
        return priceCache.get(itemId);
    }

    public List<ItemPrice> getAllPrices() {
        return new ArrayList<>(priceCache.values());
    }

    public void shutdown() {
        httpClient.dispatcher().executorService().shutdown();
    }
}