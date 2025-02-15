package com.prospector.api;

import com.google.gson.JsonObject;
import okhttp3.OkHttpClient;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class OSRSWikiPriceClientTest {
    private MockWebServer mockWebServer;
    private OSRSWikiPriceClient client;

    @Before
    public void setUp() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();
        
        // Override base URL for testing
        client = new OSRSWikiPriceClient() {
            @Override
            protected String getBaseUrl() {
                return mockWebServer.url("/").toString();
            }
        };
    }

    @After
    public void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

    @Test
    public void testGetLatestPrices() throws ExecutionException, InterruptedException {
        // Setup mock response
        String mockResponse = "{\"data\":{\"4151\":2000000,\"11802\":1500000}}";
        mockWebServer.enqueue(new MockResponse()
            .setBody(mockResponse)
            .addHeader("Content-Type", "application/json"));

        // Test
        CompletableFuture<Map<Integer, Long>> future = client.getLatestPrices();
        Map<Integer, Long> prices = future.get();

        // Verify
        assertNotNull(prices);
        assertEquals(2, prices.size());
        assertEquals(Long.valueOf(2000000), prices.get(4151));
        assertEquals(Long.valueOf(1500000), prices.get(11802));
    }

    @Test
    public void testGet5MinuteAverage() throws ExecutionException, InterruptedException {
        String mockResponse = "{\"data\":{\"4151\":1950000,\"11802\":1450000}}";
        mockWebServer.enqueue(new MockResponse()
            .setBody(mockResponse)
            .addHeader("Content-Type", "application/json"));

        CompletableFuture<Map<Integer, Long>> future = client.get5MinuteAverage();
        Map<Integer, Long> prices = future.get();

        assertNotNull(prices);
        assertEquals(2, prices.size());
        assertEquals(Long.valueOf(1950000), prices.get(4151));
    }

    @Test
    public void testGet1HourAverage() throws ExecutionException, InterruptedException {
        String mockResponse = "{\"data\":{\"4151\":1900000,\"11802\":1400000}}";
        mockWebServer.enqueue(new MockResponse()
            .setBody(mockResponse)
            .addHeader("Content-Type", "application/json"));

        CompletableFuture<Map<Integer, Long>> future = client.get1HourAverage();
        Map<Integer, Long> prices = future.get();

        assertNotNull(prices);
        assertEquals(2, prices.size());
        assertEquals(Long.valueOf(1900000), prices.get(4151));
    }

    @Test
    public void testGetVolumes() throws ExecutionException, InterruptedException {
        String mockResponse = "{\"data\":{\"4151\":100,\"11802\":50}}";
        mockWebServer.enqueue(new MockResponse()
            .setBody(mockResponse)
            .addHeader("Content-Type", "application/json"));

        CompletableFuture<Map<Integer, Integer>> future = client.getVolumes();
        Map<Integer, Integer> volumes = future.get();

        assertNotNull(volumes);
        assertEquals(2, volumes.size());
        assertEquals(Integer.valueOf(100), volumes.get(4151));
    }

    @Test
    public void testGetTimeSeries() throws ExecutionException, InterruptedException {
        String mockResponse = "{\"itemId\":4151,\"entries\":[{\"avgHighPrice\":2000000,\"avgLowPrice\":1900000,\"highPriceVolume\":50,\"lowPriceVolume\":50,\"timestamp\":1641024000}]}";
        mockWebServer.enqueue(new MockResponse()
            .setBody(mockResponse)
            .addHeader("Content-Type", "application/json"));

        CompletableFuture<TimeSeriesData> future = client.getTimeSeries(4151);
        TimeSeriesData data = future.get();

        assertNotNull(data);
        assertEquals(4151, data.getItemId());
        assertFalse(data.getEntries().isEmpty());
        assertEquals(2000000, data.getEntries().get(0).getAvgHighPrice());
    }

    @Test
    public void testHandleErrorResponse() {
        mockWebServer.enqueue(new MockResponse()
            .setResponseCode(429)
            .setBody("Rate limit exceeded"));

        CompletableFuture<Map<Integer, Long>> future = client.getLatestPrices();
        
        try {
            future.get();
            fail("Expected exception was not thrown");
        } catch (ExecutionException | InterruptedException e) {
            assertTrue(e.getCause() instanceof IOException);
        }
    }

    @Test
    public void testUserAgentHeader() throws ExecutionException, InterruptedException {
        mockWebServer.enqueue(new MockResponse()
            .setBody("{\"data\":{}}")
            .addHeader("Content-Type", "application/json"));

        client.getLatestPrices().get();

        String userAgent = mockWebServer.takeRequest().getHeader("User-Agent");
        assertEquals("GE-Prospector-RuneLite-Plugin", userAgent);
    }
}