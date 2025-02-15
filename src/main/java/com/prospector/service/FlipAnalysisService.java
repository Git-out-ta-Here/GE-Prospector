package com.prospector.service;

import com.prospector.api.PriceApiClient;
import com.prospector.api.TimeSeriesData;
import com.prospector.model.FlipOpportunity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ItemComposition;
import net.runelite.client.game.ItemManager;

import javax.inject.Inject;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class FlipAnalysisService {
    private final PriceApiClient priceApiClient;
    private final ItemManager itemManager;
    private final Set<Integer> trackedItems = Collections.newSetFromMap(new ConcurrentHashMap<>());
    
    public CompletableFuture<List<FlipOpportunity>> analyzeOpportunities() {
        return CompletableFuture.allOf(
                priceApiClient.getLatestPrices(),
                priceApiClient.get1HourAverage(),
                priceApiClient.getVolumes()
            )
            .thenCompose(v -> {
                Map<Integer, Long> latestPrices = priceApiClient.getLatestPrices().join();
                Map<Integer, Long> hourlyPrices = priceApiClient.get1HourAverage().join();
                Map<Integer, Integer> volumes = priceApiClient.getVolumes().join();
                
                return processData(latestPrices, hourlyPrices, volumes);
            });
    }

    public CompletableFuture<FlipOpportunity> analyzeItem(int itemId) {
        return CompletableFuture.allOf(
                priceApiClient.getLatestPrices(),
                priceApiClient.get1HourAverage(),
                priceApiClient.getVolumes(),
                priceApiClient.getTimeSeries(itemId)
            )
            .thenApply(v -> {
                Map<Integer, Long> latestPrices = priceApiClient.getLatestPrices().join();
                Map<Integer, Long> hourlyPrices = priceApiClient.get1HourAverage().join();
                Map<Integer, Integer> volumes = priceApiClient.getVolumes().join();
                TimeSeriesData timeSeriesData = priceApiClient.getTimeSeries(itemId).join();

                return analyzeItemData(itemId, latestPrices, hourlyPrices, volumes, timeSeriesData);
            });
    }

    private FlipOpportunity analyzeItemData(
            int itemId,
            Map<Integer, Long> latestPrices,
            Map<Integer, Long> hourlyPrices,
            Map<Integer, Integer> volumes,
            TimeSeriesData timeSeriesData) {

        Long currentPrice = latestPrices.get(itemId);
        Long hourlyPrice = hourlyPrices.get(itemId);
        Integer volume = volumes.get(itemId);

        if (currentPrice == null || hourlyPrice == null || volume == null) {
            return null;
        }

        ItemComposition itemComp = itemManager.getItemComposition(itemId);
        String itemName = itemComp.getName();

        // Calculate basic metrics
        long margin = Math.abs(currentPrice - hourlyPrice);
        double roi = (margin * 100.0) / currentPrice;
        int estimatedMinutes = calculateEstimatedTime(volume);
        FlipOpportunity.FlipRisk risk = calculateRisk(volume, roi, margin);
        FlipOpportunity.TrendDirection trend = determineTrend(currentPrice, hourlyPrice, timeSeriesData);

        return FlipOpportunity.builder()
            .itemId(itemId)
            .itemName(itemName)
            .buyPrice(Math.min(currentPrice, hourlyPrice))
            .sellPrice(Math.max(currentPrice, hourlyPrice))
            .margin(margin)
            .roi(roi)
            .estimatedTimeMinutes(estimatedMinutes)
            .hourlyVolume(volume)
            .risk(risk)
            .trendDirection(trend)
            .build();
    }

    private CompletableFuture<List<FlipOpportunity>> processData(
            Map<Integer, Long> latestPrices,
            Map<Integer, Long> hourlyPrices,
            Map<Integer, Integer> volumes) {
        
        List<FlipOpportunity> opportunities = new ArrayList<>();

        for (Map.Entry<Integer, Long> entry : latestPrices.entrySet()) {
            int itemId = entry.getKey();
            Long hourlyPrice = hourlyPrices.get(itemId);
            Integer volume = volumes.get(itemId);

            if (hourlyPrice != null && volume != null) {
                FlipOpportunity opportunity = analyzeItemData(
                    itemId, latestPrices, hourlyPrices, volumes, null);
                if (opportunity != null) {
                    opportunities.add(opportunity);
                }
            }
        }

        opportunities.sort((a, b) -> Double.compare(b.getRoi(), a.getRoi()));
        return CompletableFuture.completedFuture(opportunities);
    }

    private int calculateEstimatedTime(int volume) {
        if (volume > 100) {
            return 5;  // 5 minutes for high volume items
        } else if (volume > 50) {
            return 15; // 15 minutes for medium volume
        } else {
            return 30; // 30 minutes for low volume
        }
    }

    private FlipOpportunity.FlipRisk calculateRisk(int volume, double roi, long margin) {
        if (volume > 100 && roi > 5.0) {
            return FlipOpportunity.FlipRisk.LOW;
        } else if (volume > 50 && roi > 2.0) {
            return FlipOpportunity.FlipRisk.MEDIUM;
        } else {
            return FlipOpportunity.FlipRisk.HIGH;
        }
    }

    private FlipOpportunity.TrendDirection determineTrend(
            long currentPrice,
            long hourlyPrice,
            TimeSeriesData timeSeriesData) {
        
        double percentChange = Math.abs((currentPrice - hourlyPrice) * 100.0 / hourlyPrice);
        
        if (timeSeriesData != null && !timeSeriesData.getEntries().isEmpty()) {
            // Use time series data for more accurate trend analysis
            List<TimeSeriesData.TimeSeriesEntry> entries = timeSeriesData.getEntries();
            int size = entries.size();
            if (size >= 2) {
                long firstPrice = entries.get(0).getAvgHighPrice();
                long lastPrice = entries.get(size - 1).getAvgHighPrice();
                double trendChange = ((lastPrice - firstPrice) * 100.0) / firstPrice;
                
                if (Math.abs(trendChange) < 1.0) {
                    return FlipOpportunity.TrendDirection.STABLE;
                } else if (Math.abs(trendChange) > 5.0) {
                    return FlipOpportunity.TrendDirection.VOLATILE;
                } else if (trendChange > 0) {
                    return FlipOpportunity.TrendDirection.UPWARD;
                } else {
                    return FlipOpportunity.TrendDirection.DOWNWARD;
                }
            }
        }

        // Fallback to simple trend analysis
        if (percentChange < 1.0) {
            return FlipOpportunity.TrendDirection.STABLE;
        } else if (percentChange > 5.0) {
            return FlipOpportunity.TrendDirection.VOLATILE;
        } else if (currentPrice > hourlyPrice) {
            return FlipOpportunity.TrendDirection.UPWARD;
        } else {
            return FlipOpportunity.TrendDirection.DOWNWARD;
        }
    }

    public void trackItem(int itemId) {
        trackedItems.add(itemId);
    }

    public void untrackItem(int itemId) {
        trackedItems.remove(itemId);
    }

    public boolean isItemTracked(int itemId) {
        return trackedItems.contains(itemId);
    }

    public Set<Integer> getTrackedItems() {
        return Collections.unmodifiableSet(trackedItems);
    }
}