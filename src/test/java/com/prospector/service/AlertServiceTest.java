package com.prospector.service;

import com.prospector.model.FlipOpportunity;
import com.prospector.model.PriceAlert;
import com.prospector.util.ProspectorUtil;
import net.runelite.api.events.GrandExchangeOfferChanged;
import net.runelite.client.game.ItemManager;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class AlertServiceTest {

    @Mock
    private AlertManager alertManager;

    @Mock
    private ItemManager itemManager;

    @Mock
    private ProspectorUtil prospectorUtil;

    @Mock
    private GrandExchangeOfferChanged geEvent;

    private AlertService alertService;
    private static final int TEST_ITEM_ID = 4151;
    private static final String TEST_ITEM_NAME = "Abyssal whip";

    @Before
    public void setUp() {
        alertService = new AlertService(alertManager, itemManager, prospectorUtil);
    }

    @Test
    public void testCheckPrice_NoAlerts() {
        when(alertManager.getAlerts(TEST_ITEM_ID)).thenReturn(Collections.emptyList());
        
        alertService.checkPrice(TEST_ITEM_ID, 2000000L);
        
        verify(prospectorUtil, never()).sendNotification(anyString());
    }

    @Test
    public void testCheckPrice_AlertTriggered() {
        PriceAlert alert = createTestAlert(PriceAlert.AlertCondition.ABOVE, 1500000L);
        when(alertManager.getAlerts(TEST_ITEM_ID)).thenReturn(List.of(alert));

        alertService.checkPrice(TEST_ITEM_ID, 2000000L);

        verify(prospectorUtil).sendNotification(contains("above"));
    }

    @Test
    public void testCheckOpportunity_MarginAlert() {
        PriceAlert alert = PriceAlert.builder()
            .itemId(TEST_ITEM_ID)
            .itemName(TEST_ITEM_NAME)
            .type(PriceAlert.AlertType.MARGIN)
            .condition(PriceAlert.AlertCondition.ABOVE)
            .targetPrice(100000L)
            .enabled(true)
            .build();

        when(alertManager.getAlerts(TEST_ITEM_ID)).thenReturn(List.of(alert));

        FlipOpportunity opportunity = FlipOpportunity.builder()
            .itemId(TEST_ITEM_ID)
            .itemName(TEST_ITEM_NAME)
            .buyPrice(2000000L)
            .sellPrice(2200000L)
            .margin(200000L)
            .roi(10.0)
            .build();

        alertService.checkOpportunity(opportunity);

        verify(prospectorUtil).sendNotification(contains("Margin alert"));
    }

    @Test
    public void testHandleGrandExchangeOfferChanged() {
        when(geEvent.getItemId()).thenReturn(TEST_ITEM_ID);
        when(geEvent.getOffer().getPrice()).thenReturn(2000000);
        
        PriceAlert alert = createTestAlert(PriceAlert.AlertCondition.ABOVE, 1500000L);
        when(alertManager.getAlerts(TEST_ITEM_ID)).thenReturn(List.of(alert));

        alertService.handleGrandExchangeOfferChanged(geEvent);

        verify(prospectorUtil).sendNotification(contains("above"));
    }

    @Test
    public void testDisabledAlert() {
        PriceAlert alert = createTestAlert(PriceAlert.AlertCondition.ABOVE, 1500000L);
        alert.setEnabled(false);
        when(alertManager.getAlerts(TEST_ITEM_ID)).thenReturn(List.of(alert));

        alertService.checkPrice(TEST_ITEM_ID, 2000000L);

        verify(prospectorUtil, never()).sendNotification(anyString());
    }

    @Test
    public void testMultipleAlerts() {
        PriceAlert alert1 = createTestAlert(PriceAlert.AlertCondition.ABOVE, 1500000L);
        PriceAlert alert2 = createTestAlert(PriceAlert.AlertCondition.BELOW, 2500000L);
        when(alertManager.getAlerts(TEST_ITEM_ID)).thenReturn(List.of(alert1, alert2));

        alertService.checkPrice(TEST_ITEM_ID, 2000000L);

        verify(prospectorUtil).sendNotification(contains("above"));
        verify(prospectorUtil, never()).sendNotification(contains("below"));
    }

    @Test
    public void testReset() {
        alertService.reset();
        
        // Test that after reset, the same price triggers the alert again
        PriceAlert alert = createTestAlert(PriceAlert.AlertCondition.ABOVE, 1500000L);
        when(alertManager.getAlerts(TEST_ITEM_ID)).thenReturn(List.of(alert));

        alertService.checkPrice(TEST_ITEM_ID, 2000000L);
        verify(prospectorUtil).sendNotification(anyString());

        // Second check should trigger again after reset
        alertService.reset();
        alertService.checkPrice(TEST_ITEM_ID, 2000000L);
        verify(prospectorUtil, times(2)).sendNotification(anyString());
    }

    private PriceAlert createTestAlert(PriceAlert.AlertCondition condition, long targetPrice) {
        return PriceAlert.builder()
            .itemId(TEST_ITEM_ID)
            .itemName(TEST_ITEM_NAME)
            .type(PriceAlert.AlertType.PRICE)
            .condition(condition)
            .targetPrice(targetPrice)
            .enabled(true)
            .build();
    }
}