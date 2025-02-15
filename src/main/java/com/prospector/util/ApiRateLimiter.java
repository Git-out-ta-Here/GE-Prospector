package com.prospector.util;

import com.google.common.util.concurrent.RateLimiter;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Singleton;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

@Slf4j
@Singleton
public class ApiRateLimiter {
    // OSRS Wiki API limit is 300 requests per minute
    private static final double REQUESTS_PER_SECOND = 5.0;
    private final RateLimiter rateLimiter;

    public ApiRateLimiter() {
        this.rateLimiter = RateLimiter.create(REQUESTS_PER_SECOND);
    }

    public <T> CompletableFuture<T> acquire(Supplier<CompletableFuture<T>> operation) {
        return CompletableFuture.supplyAsync(() -> {
            double waitTime = rateLimiter.acquire();
            if (waitTime > 0.0) {
                log.debug("Rate limit applied, waited {} seconds", String.format("%.2f", waitTime));
            }
            return operation.get();
        }).thenCompose(future -> future);
    }

    public void reset() {
        // For testing purposes
        rateLimiter.setRate(REQUESTS_PER_SECOND);
    }
}