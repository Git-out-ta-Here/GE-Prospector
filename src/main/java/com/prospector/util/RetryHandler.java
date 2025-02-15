package com.prospector.util;

import com.prospector.exception.GrandExchangeException;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

@Slf4j
public class RetryHandler {
    private static final int MAX_RETRIES = 3;
    private static final long INITIAL_BACKOFF_MS = 1000;
    private static final long MAX_BACKOFF_MS = 10000;

    public static <T> CompletableFuture<T> withRetry(Supplier<CompletableFuture<T>> operation) {
        return withRetry(operation, MAX_RETRIES, INITIAL_BACKOFF_MS);
    }

    public static <T> CompletableFuture<T> withRetry(
            Supplier<CompletableFuture<T>> operation,
            int maxRetries,
            long initialBackoffMs) {
        
        return retry(operation, 0, maxRetries, initialBackoffMs);
    }

    private static <T> CompletableFuture<T> retry(
            Supplier<CompletableFuture<T>> operation,
            int currentAttempt,
            int maxRetries,
            long backoffMs) {
        
        return operation.get()
            .exceptionally(throwable -> {
                if (throwable instanceof GrandExchangeException) {
                    GrandExchangeException geException = (GrandExchangeException) throwable;
                    
                    if (!geException.isRetryable()) {
                        throw geException;
                    }

                    if (currentAttempt >= maxRetries) {
                        log.error("Max retries ({}) exceeded", maxRetries);
                        throw geException;
                    }

                    long nextBackoff = Math.min(backoffMs * 2, MAX_BACKOFF_MS);
                    log.warn("Attempt {} failed, retrying in {}ms: {}", 
                        currentAttempt + 1, backoffMs, geException.getMessage());

                    return CompletableFuture.delayedExecutor(
                            backoffMs, TimeUnit.MILLISECONDS)
                        .supplyAsync(() -> retry(
                            operation,
                            currentAttempt + 1,
                            maxRetries,
                            nextBackoff).join());
                }
                
                throw new RuntimeException(throwable);
            }).thenCompose(result -> {
                if (result instanceof CompletableFuture) {
                    return (CompletableFuture<T>) result;
                }
                return CompletableFuture.completedFuture((T) result);
            });
    }
}