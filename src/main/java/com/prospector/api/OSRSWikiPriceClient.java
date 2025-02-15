package com.prospector.api;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Slf4j
@Singleton
public class OSRSWikiPriceClient implements PriceApiClient {
    private static final String BASE_URL = "https://prices.runescape.wiki/api/v1/osrs";
    private static final String USER_AGENT = "GE-Prospector-RuneLite-Plugin";
    
    private final OkHttpClient client;
    private final Gson gson;

    @Inject
    public OSRSWikiPriceClient() {
        this.client = new OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            .addInterceptor(chain -> chain.proceed(
                chain.request().newBuilder()
                    .header("User-Agent", USER_AGENT)
                    .build()
            ))
            .build();
        
        this.gson = new Gson();
    }

    @Override
    public CompletableFuture<Map<Integer, Long>> getLatestPrices() {
        return makeRequest("/latest")
            .thenApply(json -> {
                JsonObject data = json.getAsJsonObject("data");
                Type type = new TypeToken<Map<Integer, Long>>(){}.getType();
                return gson.fromJson(data, type);
            });
    }

    @Override
    public CompletableFuture<Map<Integer, Long>> get5MinuteAverage() {
        return makeRequest("/5m")
            .thenApply(json -> {
                JsonObject data = json.getAsJsonObject("data");
                Type type = new TypeToken<Map<Integer, Long>>(){}.getType();
                return gson.fromJson(data, type);
            });
    }

    @Override
    public CompletableFuture<Map<Integer, Long>> get1HourAverage() {
        return makeRequest("/1h")
            .thenApply(json -> {
                JsonObject data = json.getAsJsonObject("data");
                Type type = new TypeToken<Map<Integer, Long>>(){}.getType();
                return gson.fromJson(data, type);
            });
    }

    @Override
    public CompletableFuture<Map<Integer, Integer>> getVolumes() {
        return makeRequest("/volumes")
            .thenApply(json -> {
                JsonObject data = json.getAsJsonObject("data");
                Type type = new TypeToken<Map<Integer, Integer>>(){}.getType();
                return gson.fromJson(data, type);
            });
    }

    @Override
    public CompletableFuture<TimeSeriesData> getTimeSeries(int itemId) {
        return makeRequest("/timeseries?id=" + itemId)
            .thenApply(json -> gson.fromJson(json, TimeSeriesData.class));
    }

    private CompletableFuture<JsonObject> makeRequest(String endpoint) {
        return CompletableFuture.supplyAsync(() -> {
            Request request = new Request.Builder()
                .url(BASE_URL + endpoint)
                .build();

            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    throw new IOException("Unexpected response code: " + response);
                }

                String body = response.body().string();
                return gson.fromJson(body, JsonObject.class);
            } catch (IOException e) {
                log.error("Failed to make request to {}: {}", endpoint, e.getMessage());
                throw new RuntimeException("API request failed", e);
            }
        });
    }
}