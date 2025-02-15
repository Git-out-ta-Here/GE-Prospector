package com.prospector.api;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

public interface PriceApiClient {
    CompletableFuture<Map<Integer, Long>> getLatestPrices();
    CompletableFuture<Map<Integer, Long>> get5MinuteAverage();
    CompletableFuture<Map<Integer, Long>> get1HourAverage();
    CompletableFuture<Map<Integer, Integer>> getVolumes();
    CompletableFuture<TimeSeriesData> getTimeSeries(int itemId);
}