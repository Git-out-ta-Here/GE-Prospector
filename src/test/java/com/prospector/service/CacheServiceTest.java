package com.prospector.service;

import net.runelite.api.ItemComposition;
import net.runelite.client.game.ItemManager;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class CacheServiceTest {

    @Mock
    private ItemManager itemManager;

    @Mock
    private ItemComposition itemComposition;

    private CacheService cacheService;
    private static final int TEST_ITEM_ID = 4151;

    @Before
    public void setUp() {
        cacheService = new CacheService(itemManager);
        when(itemManager.getItemComposition(TEST_ITEM_ID)).thenReturn(itemComposition);
    }

    @Test
    public void testPriceCaching() throws InterruptedException {
        // Test cache hit
        cacheService.cachePrice(TEST_ITEM_ID, 1000000L);
        assertEquals(Long.valueOf(1000000L), cacheService.getCachedPrice(TEST_ITEM_ID));

        // Test cache expiry
        Thread.sleep(TimeUnit.MINUTES.toMillis(1) + 100);
        assertNull(cacheService.getCachedPrice(TEST_ITEM_ID));
    }

    @Test
    public void testItemHistoryCaching() {
        Map<Long, Long> history = new HashMap<>();
        history.put(1641024000L, 1000000L);

        // Test cache hit
        cacheService.cacheItemHistory(TEST_ITEM_ID, history);
        assertNotNull(cacheService.getCachedHistory(TEST_ITEM_ID));
        assertEquals(history, cacheService.getCachedHistory(TEST_ITEM_ID));
    }

    @Test
    public void testItemCaching() {
        // Test cache hit
        ItemComposition cachedItem = cacheService.getCachedItem(TEST_ITEM_ID);
        assertNotNull(cachedItem);
        assertEquals(itemComposition, cachedItem);

        // Verify ItemManager was called
        verify(itemManager).getItemComposition(TEST_ITEM_ID);

        // Test subsequent cache hit doesn't call ItemManager again
        cacheService.getCachedItem(TEST_ITEM_ID);
        verifyNoMoreInteractions(itemManager);
    }

    @Test
    public void testClearExpiredEntries() throws InterruptedException {
        // Add entries
        cacheService.cachePrice(TEST_ITEM_ID, 1000000L);
        Map<Long, Long> history = new HashMap<>();
        history.put(1641024000L, 1000000L);
        cacheService.cacheItemHistory(TEST_ITEM_ID, history);

        // Wait for price cache to expire
        Thread.sleep(TimeUnit.MINUTES.toMillis(1) + 100);

        // Clear expired entries
        cacheService.clearExpiredEntries();

        // Price should be cleared, but history should remain
        assertNull(cacheService.getCachedPrice(TEST_ITEM_ID));
        assertNotNull(cacheService.getCachedHistory(TEST_ITEM_ID));
    }

    @Test
    public void testCacheReset() {
        // Add entries
        cacheService.cachePrice(TEST_ITEM_ID, 1000000L);
        Map<Long, Long> history = new HashMap<>();
        history.put(1641024000L, 1000000L);
        cacheService.cacheItemHistory(TEST_ITEM_ID, history);
        cacheService.getCachedItem(TEST_ITEM_ID);

        // Reset cache
        cacheService.reset();

        // Verify all caches are cleared
        assertNull(cacheService.getCachedPrice(TEST_ITEM_ID));
        assertNull(cacheService.getCachedHistory(TEST_ITEM_ID));
        
        // Verify ItemManager is called again after reset
        cacheService.getCachedItem(TEST_ITEM_ID);
        verify(itemManager, times(2)).getItemComposition(TEST_ITEM_ID);
    }

    @Test
    public void testConcurrentAccess() throws InterruptedException {
        final int numThreads = 10;
        final int numOperations = 1000;
        Thread[] threads = new Thread[numThreads];
        
        for (int i = 0; i < numThreads; i++) {
            threads[i] = new Thread(() -> {
                for (int j = 0; j < numOperations; j++) {
                    cacheService.cachePrice(j, (long) j);
                    cacheService.getCachedPrice(j);
                }
            });
        }

        // Start all threads
        for (Thread thread : threads) {
            thread.start();
        }

        // Wait for all threads to complete
        for (Thread thread : threads) {
            thread.join();
        }

        // Verify no exceptions were thrown
        for (int i = 0; i < numOperations; i++) {
            assertEquals(Long.valueOf(i), cacheService.getCachedPrice(i));
        }
    }
}