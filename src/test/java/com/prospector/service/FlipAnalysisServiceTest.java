package com.prospector.service;

import com.prospector.api.PriceApiClient;
import com.prospector.api.TimeSeriesData;
import com.prospector.model.FlipOpportunity;
import net.runelite.api.ItemComposition;
import net.runelite.client.game.ItemManager;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class FlipAnalysisServiceTest {

    @Mock
    private PriceApiClient priceApiClient;

    @Mock
    private ItemManager itemManager;

    @Mock
    private ItemComposition itemComposition;

    private FlipAnalysisService service;
    private static final int TEST_ITEM_ID = 4151; // Abyssal whip

    @Before
    public void setUp() {
        service = new FlipAnalysisService(priceApiClient, itemManager);
        
        // Setup mock data
        Map<Integer, Long> latestPrices = new HashMap<>();
        latestPrices.put(TEST_ITEM_ID, 2000000L);
        
        Map<Integer, Long> hourlyPrices = new HashMap<>();
        hourlyPrices.put(TEST_ITEM_ID, 2100000L);
        
        Map<Integer, Integer> volumes = new HashMap<>();
        volumes.put(TEST_ITEM_ID, 100);

        when(priceApiClient.getLatestPrices())
            .thenReturn(CompletableFuture.completedFuture(latestPrices));
        when(priceApiClient.get1HourAverage())
            .thenReturn(CompletableFuture.completedFuture(hourlyPrices));
        when(priceApiClient.getVolumes())
            .thenReturn(CompletableFuture.completedFuture(volumes));
        
        // Setup item composition mock
        when(itemManager.getItemComposition(TEST_ITEM_ID))
            .thenReturn(itemComposition);
        when(itemComposition.getName())
            .thenReturn("Abyssal whip");
    }

    @Test
    public void testAnalyzeOpportunities() {
        List<FlipOpportunity> opportunities = service.analyzeOpportunities().join();
        
        assertNotNull(opportunities);
        assertFalse(opportunities.isEmpty());
        
        FlipOpportunity opportunity = opportunities.get(0);
        assertEquals(TEST_ITEM_ID, opportunity.getItemId());
        assertEquals("Abyssal whip", opportunity.getItemName());
        assertEquals(100000L, opportunity.getMargin());
        assertTrue(opportunity.getRoi() > 0);
    }

    @Test
    public void testAnalyzeItem() {
        // Setup time series data
        TimeSeriesData timeSeriesData = new TimeSeriesData();
        timeSeriesData.setItemId(TEST_ITEM_ID);
        TimeSeriesData.TimeSeriesEntry entry1 = new TimeSeriesData.TimeSeriesEntry();
        entry1.setAvgHighPrice(2000000L);
        TimeSeriesData.TimeSeriesEntry entry2 = new TimeSeriesData.TimeSeriesEntry();
        entry2.setAvgHighPrice(2100000L);
        timeSeriesData.setEntries(Arrays.asList(entry1, entry2));

        when(priceApiClient.getTimeSeries(TEST_ITEM_ID))
            .thenReturn(CompletableFuture.completedFuture(timeSeriesData));

        FlipOpportunity opportunity = service.analyzeItem(TEST_ITEM_ID).join();
        
        assertNotNull(opportunity);
        assertEquals(TEST_ITEM_ID, opportunity.getItemId());
        assertEquals("Abyssal whip", opportunity.getItemName());
        assertEquals(100000L, opportunity.getMargin());
        assertTrue(opportunity.getRoi() > 0);
        assertEquals(FlipOpportunity.TrendDirection.UPWARD, opportunity.getTrendDirection());
    }

    @Test
    public void testItemTracking() {
        assertFalse(service.isItemTracked(TEST_ITEM_ID));
        
        service.trackItem(TEST_ITEM_ID);
        assertTrue(service.isItemTracked(TEST_ITEM_ID));
        assertEquals(1, service.getTrackedItems().size());
        
        service.untrackItem(TEST_ITEM_ID);
        assertFalse(service.isItemTracked(TEST_ITEM_ID));
        assertTrue(service.getTrackedItems().isEmpty());
    }

    @Test
    public void testAnalyzeItemWithMissingData() {
        when(priceApiClient.getLatestPrices())
            .thenReturn(CompletableFuture.completedFuture(Collections.emptyMap()));
        when(priceApiClient.get1HourAverage())
            .thenReturn(CompletableFuture.completedFuture(Collections.emptyMap()));
        when(priceApiClient.getVolumes())
            .thenReturn(CompletableFuture.completedFuture(Collections.emptyMap()));
        when(priceApiClient.getTimeSeries(TEST_ITEM_ID))
            .thenReturn(CompletableFuture.completedFuture(new TimeSeriesData()));

        FlipOpportunity opportunity = service.analyzeItem(TEST_ITEM_ID).join();
        assertNull(opportunity);
    }

    @Test
    public void testRiskCalculation() {
        // Test low risk scenario
        Map<Integer, Long> latestPrices = new HashMap<>();
        latestPrices.put(TEST_ITEM_ID, 2000000L);
        
        Map<Integer, Long> hourlyPrices = new HashMap<>();
        hourlyPrices.put(TEST_ITEM_ID, 2200000L);
        
        Map<Integer, Integer> volumes = new HashMap<>();
        volumes.put(TEST_ITEM_ID, 150); // High volume

        when(priceApiClient.getLatestPrices())
            .thenReturn(CompletableFuture.completedFuture(latestPrices));
        when(priceApiClient.get1HourAverage())
            .thenReturn(CompletableFuture.completedFuture(hourlyPrices));
        when(priceApiClient.getVolumes())
            .thenReturn(CompletableFuture.completedFuture(volumes));

        FlipOpportunity opportunity = service.analyzeItem(TEST_ITEM_ID).join();
        assertNotNull(opportunity);
        assertEquals(FlipOpportunity.FlipRisk.LOW, opportunity.getRisk());
    }

    @Test
    public void testTimeEstimation() {
        Map<Integer, Integer> volumes = new HashMap<>();
        volumes.put(TEST_ITEM_ID, 150); // High volume
        when(priceApiClient.getVolumes())
            .thenReturn(CompletableFuture.completedFuture(volumes));

        FlipOpportunity opportunity = service.analyzeItem(TEST_ITEM_ID).join();
        assertNotNull(opportunity);
        assertEquals(5, opportunity.getEstimatedTimeMinutes());
    }
}