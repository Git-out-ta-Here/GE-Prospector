package com.prospector.util;

import com.prospector.exception.GrandExchangeException;
import org.junit.Test;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.*;

public class RetryHandlerTest {

    @Test
    public void testSuccessfulOperation() {
        CompletableFuture<String> result = RetryHandler.withRetry(
            () -> CompletableFuture.completedFuture("success"));
        
        assertEquals("success", result.join());
    }

    @Test
    public void testRetryableFailureEventualSuccess() {
        AtomicInteger attempts = new AtomicInteger(0);
        
        CompletableFuture<String> result = RetryHandler.withRetry(
            () -> {
                if (attempts.incrementAndGet() < 3) {
                    CompletableFuture<String> future = new CompletableFuture<>();
                    future.completeExceptionally(new GrandExchangeException(
                        GrandExchangeException.ErrorType.API_ERROR));
                    return future;
                }
                return CompletableFuture.completedFuture("success");
            },
            3,
            10);

        assertEquals("success", result.join());
        assertEquals(3, attempts.get());
    }

    @Test(expected = GrandExchangeException.class)
    public void testNonRetryableFailure() {
        CompletableFuture<String> result = RetryHandler.withRetry(
            () -> {
                CompletableFuture<String> future = new CompletableFuture<>();
                future.completeExceptionally(new GrandExchangeException(
                    GrandExchangeException.ErrorType.INVALID_ITEM));
                return future;
            });

        result.join();
    }

    @Test(expected = GrandExchangeException.class)
    public void testMaxRetriesExceeded() {
        CompletableFuture<String> result = RetryHandler.withRetry(
            () -> {
                CompletableFuture<String> future = new CompletableFuture<>();
                future.completeExceptionally(new GrandExchangeException(
                    GrandExchangeException.ErrorType.API_ERROR));
                return future;
            },
            2,
            10);

        result.join();
    }

    @Test
    public void testExponentialBackoff() {
        AtomicInteger attempts = new AtomicInteger(0);
        long startTime = System.currentTimeMillis();
        
        CompletableFuture<String> result = RetryHandler.withRetry(
            () -> {
                if (attempts.incrementAndGet() < 3) {
                    CompletableFuture<String> future = new CompletableFuture<>();
                    future.completeExceptionally(new GrandExchangeException(
                        GrandExchangeException.ErrorType.API_ERROR));
                    return future;
                }
                return CompletableFuture.completedFuture("success");
            },
            3,
            100);

        result.join();
        long duration = System.currentTimeMillis() - startTime;
        
        assertTrue("Duration should be at least 300ms due to backoff", duration >= 300);
    }

    @Test
    public void testRetryWithRateLimit() {
        AtomicInteger attempts = new AtomicInteger(0);
        
        CompletableFuture<String> result = RetryHandler.withRetry(
            () -> {
                if (attempts.incrementAndGet() < 2) {
                    CompletableFuture<String> future = new CompletableFuture<>();
                    future.completeExceptionally(new GrandExchangeException(
                        GrandExchangeException.ErrorType.RATE_LIMIT));
                    return future;
                }
                return CompletableFuture.completedFuture("success");
            },
            3,
            10);

        assertEquals("success", result.join());
        assertEquals(2, attempts.get());
    }

    @Test
    public void testRetryWithNetworkError() {
        AtomicInteger attempts = new AtomicInteger(0);
        
        CompletableFuture<String> result = RetryHandler.withRetry(
            () -> {
                if (attempts.incrementAndGet() < 2) {
                    CompletableFuture<String> future = new CompletableFuture<>();
                    future.completeExceptionally(new GrandExchangeException(
                        GrandExchangeException.ErrorType.NETWORK_ERROR));
                    return future;
                }
                return CompletableFuture.completedFuture("success");
            },
            3,
            10);

        assertEquals("success", result.join());
        assertEquals(2, attempts.get());
    }

    @Test(expected = RuntimeException.class)
    public void testNonGrandExchangeException() {
        CompletableFuture<String> result = RetryHandler.withRetry(
            () -> {
                CompletableFuture<String> future = new CompletableFuture<>();
                future.completeExceptionally(new RuntimeException("Unexpected error"));
                return future;
            });

        result.join();
    }
}