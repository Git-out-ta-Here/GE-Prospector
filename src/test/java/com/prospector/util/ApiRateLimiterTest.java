package com.prospector.util;

import org.junit.Before;
import org.junit.Test;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

public class ApiRateLimiterTest {

    private ApiRateLimiter rateLimiter;

    @Before
    public void setUp() {
        rateLimiter = new ApiRateLimiter();
    }

    @Test
    public void testRateLimiting() throws ExecutionException, InterruptedException {
        long startTime = System.currentTimeMillis();
        
        // Try to make 10 requests immediately
        List<CompletableFuture<Long>> futures = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            CompletableFuture<Long> future = rateLimiter.acquire(() ->
                CompletableFuture.completedFuture(System.currentTimeMillis())
            );
            futures.add(future);
        }

        // Wait for all futures to complete
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).get();
        
        // Get all timestamps
        List<Long> timestamps = new ArrayList<>();
        for (CompletableFuture<Long> future : futures) {
            timestamps.add(future.get());
        }

        // Verify that requests were properly rate limited
        long totalTime = timestamps.get(timestamps.size() - 1) - startTime;
        assertTrue("Total time should be at least 1.8 seconds for 10 requests at 5 req/sec",
            totalTime >= 1800);

        // Verify spacing between requests
        for (int i = 1; i < timestamps.size(); i++) {
            long timeDiff = timestamps.get(i) - timestamps.get(i - 1);
            assertTrue("Requests should be spaced by at least 180ms",
                timeDiff >= 180);
        }
    }

    @Test
    public void testConcurrentRequests() throws ExecutionException, InterruptedException {
        int numRequests = 20;
        List<CompletableFuture<Integer>> futures = new ArrayList<>();
        
        // Launch requests concurrently
        for (int i = 0; i < numRequests; i++) {
            final int requestNum = i;
            futures.add(rateLimiter.acquire(() ->
                CompletableFuture.completedFuture(requestNum)
            ));
        }

        // Wait for all to complete
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).get();
        
        // Verify all requests completed
        assertEquals(numRequests, futures.size());
        for (int i = 0; i < numRequests; i++) {
            assertTrue(futures.get(i).isDone());
            assertFalse(futures.get(i).isCompletedExceptionally());
        }
    }

    @Test
    public void testFailedRequest() {
        CompletableFuture<Object> future = rateLimiter.acquire(() ->
            CompletableFuture.failedFuture(new RuntimeException("Test failure"))
        );

        try {
            future.get();
            fail("Expected exception was not thrown");
        } catch (ExecutionException | InterruptedException e) {
            assertTrue(e.getCause() instanceof RuntimeException);
            assertEquals("Test failure", e.getCause().getMessage());
        }
    }

    @Test
    public void testReset() throws ExecutionException, InterruptedException {
        // Make some initial requests
        for (int i = 0; i < 5; i++) {
            rateLimiter.acquire(() -> CompletableFuture.completedFuture(null)).get();
        }

        // Reset the rate limiter
        rateLimiter.reset();

        // Verify we can make requests at full rate again
        long startTime = System.currentTimeMillis();
        CompletableFuture<Long> future = rateLimiter.acquire(() ->
            CompletableFuture.completedFuture(System.currentTimeMillis())
        );
        
        long requestTime = future.get();
        assertTrue("Request should complete quickly after reset",
            requestTime - startTime < 100);
    }

    @Test
    public void testBurstBehavior() throws ExecutionException, InterruptedException {
        // Wait to accumulate permits
        Thread.sleep(1000);

        // Make a burst of requests
        long startTime = System.currentTimeMillis();
        List<CompletableFuture<Long>> futures = new ArrayList<>();
        
        for (int i = 0; i < 5; i++) {
            futures.add(rateLimiter.acquire(() ->
                CompletableFuture.completedFuture(System.currentTimeMillis())
            ));
        }

        // Wait for all futures to complete
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).get();
        
        // First few requests should complete very quickly due to accumulated permits
        long firstRequestTime = futures.get(0).get() - startTime;
        assertTrue("First request should complete quickly", firstRequestTime < 100);
    }
}