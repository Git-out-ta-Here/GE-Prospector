package com.prospector.service;

import com.google.gson.Gson;
import com.prospector.api.PriceApiClient;
import com.prospector.model.PriceAlert;
import com.prospector.util.ProspectorUtil;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.game.ItemManager;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class AlertManagerTest {

    @Mock
    private ConfigManager configManager;

    @Mock
    private PriceApiClient priceApiClient;

    @Mock
    private ItemManager itemManager;

    @Mock
    private ProspectorUtil prospectorUtil;

    private AlertManager alertManager;
    private static final int TEST_ITEM_ID = 4151;
    private static final String TEST_ITEM_NAME = "Abyssal whip";

    @Before
    public void setUp() {
        // Setup mock price data
        Map<Integer, Long> mockPrices = new HashMap<>();
        mockPrices.put(TEST_ITEM_ID, 2000000L);
        when(priceApiClient.getLatestPrices())
            .thenReturn(CompletableFuture.completedFuture(mockPrices));

        alertManager = new AlertManager(configManager, priceApiClient, itemManager, prospectorUtil);
    }

    @Test
    public void testAddAlert() {
        PriceAlert alert = createTestAlert();
        alertManager.addAlert(alert);

        List<PriceAlert> alerts = alertManager.getAlerts(TEST_ITEM_ID);
        assertFalse(alerts.isEmpty());
        assertEquals(alert, alerts.get(0));
        verify(prospectorUtil).sendNotification(contains("Alert added"));
    }

    @Test
    public void testRemoveAlert() {
        PriceAlert alert = createTestAlert();
        alertManager.addAlert(alert);
        alertManager.removeAlert(TEST_ITEM_ID, alert);

        List<PriceAlert> alerts = alertManager.getAlerts(TEST_ITEM_ID);
        assertTrue(alerts.isEmpty());
    }

    @Test
    public void testCheckAlerts_AboveCondition() {
        PriceAlert alert = createTestAlert();
        alert.setCondition(PriceAlert.AlertCondition.ABOVE);
        alert.setTargetPrice(1500000L);
        alertManager.addAlert(alert);

        alertManager.checkAlerts();

        verify(prospectorUtil).sendNotification(contains("above"));
    }

    @Test
    public void testCheckAlerts_BelowCondition() {
        PriceAlert alert = createTestAlert();
        alert.setCondition(PriceAlert.AlertCondition.BELOW);
        alert.setTargetPrice(2500000L);
        alertManager.addAlert(alert);

        alertManager.checkAlerts();

        verify(prospectorUtil).sendNotification(contains("below"));
    }

    @Test
    public void testCheckAlerts_DisabledAlert() {
        PriceAlert alert = createTestAlert();
        alert.setEnabled(false);
        alertManager.addAlert(alert);

        alertManager.checkAlerts();

        verify(prospectorUtil, never()).sendNotification(anyString());
    }

    @Test
    public void testPersistence() {
        // Setup mock for config loading
        Gson gson = new Gson();
        PriceAlert alert = createTestAlert();
        String json = gson.toJson(List.of(alert));
        when(configManager.getConfiguration(anyString(), anyString()))
            .thenReturn(json);

        // Create new instance to test loading
        AlertManager newManager = new AlertManager(configManager, priceApiClient, itemManager, prospectorUtil);
        List<PriceAlert> loadedAlerts = newManager.getAlerts(TEST_ITEM_ID);

        assertFalse(loadedAlerts.isEmpty());
        assertEquals(alert.getItemId(), loadedAlerts.get(0).getItemId());
        assertEquals(alert.getTargetPrice(), loadedAlerts.get(0).getTargetPrice());
    }

    @Test
    public void testAlertTriggeredOnlyOnce() {
        PriceAlert alert = createTestAlert();
        alert.setCondition(PriceAlert.AlertCondition.ABOVE);
        alert.setTargetPrice(1500000L);
        alertManager.addAlert(alert);

        // First check should trigger
        alertManager.checkAlerts();
        verify(prospectorUtil, times(1)).sendNotification(anyString());

        // Second check should not trigger again
        alertManager.checkAlerts();
        verify(prospectorUtil, times(1)).sendNotification(anyString());
    }

    @Test
    public void testMultipleAlertsForSameItem() {
        PriceAlert alert1 = createTestAlert();
        alert1.setTargetPrice(1500000L);
        alert1.setCondition(PriceAlert.AlertCondition.ABOVE);

        PriceAlert alert2 = createTestAlert();
        alert2.setTargetPrice(2500000L);
        alert2.setCondition(PriceAlert.AlertCondition.BELOW);

        alertManager.addAlert(alert1);
        alertManager.addAlert(alert2);

        List<PriceAlert> alerts = alertManager.getAlerts(TEST_ITEM_ID);
        assertEquals(2, alerts.size());
    }

    private PriceAlert createTestAlert() {
        return PriceAlert.builder()
            .itemId(TEST_ITEM_ID)
            .itemName(TEST_ITEM_NAME)
            .targetPrice(2000000L)
            .type(PriceAlert.AlertType.PRICE)
            .condition(PriceAlert.AlertCondition.EQUALS)
            .enabled(true)
            .notes("Test alert")
            .build();
    }
}