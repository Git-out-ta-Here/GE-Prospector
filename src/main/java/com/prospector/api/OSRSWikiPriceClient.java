package com.prospector.api;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Slf4j
public class OSRSWikiPriceClient implements PriceApiClient {
    private static final String BASE_URL = "https://prices.runescape.wiki/api/v1/osrs";
    private static final String USER_AGENT = "GE-Prospector RuneLite Plugin";

    private final OkHttpClient client;
    private final Gson gson;

    public OSRSWikiPriceClient() {
        this.client = new OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build();
        this.gson = new Gson();
    }

    @Override
    public CompletableFuture<Map<Integer, Long>> getLatestPrices() {
        return makeRequest("/latest", new TypeToken<Map<String, Map<Integer, Long>>>() {}.getType())
            .thenApply(response -> ((Map<String, Map<Integer, Long>>) response).get("data"));
    }

    @Override
    public CompletableFuture<Map<Integer, Long>> get5MinuteAverage() {
        return makeRequest("/5m", new TypeToken<Map<String, Map<Integer, Long>>>() {}.getType())
            .thenApply(response -> ((Map<String, Map<Integer, Long>>) response).get("data"));
    }

    @Override
    public CompletableFuture<Map<Integer, Long>> get1HourAverage() {
        return makeRequest("/1h", new TypeToken<Map<String, Map<Integer, Long>>>() {}.getType())
            .thenApply(response -> ((Map<String, Map<Integer, Long>>) response).get("data"));
    }

    @Override
    public CompletableFuture<Map<Integer, Integer>> getVolumes() {
        return makeRequest("/volumes", new TypeToken<Map<String, Map<Integer, Integer>>>() {}.getType())
            .thenApply(response -> ((Map<String, Map<Integer, Integer>>) response).get("data"));
    }

    @Override
    public CompletableFuture<TimeSeriesData> getTimeSeries(int itemId) {
        return makeRequest("/timeseries?id=" + itemId, TimeSeriesData.class);
    }

    private <T> CompletableFuture<T> makeRequest(String endpoint, Type responseType) {
        return CompletableFuture.supplyAsync(() -> {
            Request request = new Request.Builder()
                .url(BASE_URL + endpoint)
                .header("User-Agent", USER_AGENT)
                .build();

            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    throw new IOException("Unexpected response code: " + response.code());
                }

                String responseBody = response.body().string();
                return gson.fromJson(responseBody, responseType);
            } catch (Exception e) {
                log.error("Error making request to " + endpoint, e);
                throw new RuntimeException(e);
            }
        });
    }
}