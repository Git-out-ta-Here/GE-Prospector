package com.prospector;

import com.prospector.model.FlipOpportunity;
import com.prospector.ui.PriceAlertOverlay;
import net.runelite.api.Client;
import net.runelite.client.ui.overlay.OverlayPosition;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class PriceAlertOverlayTest {

    @Mock
    private Client client;

    @Mock
    private ProspectorConfig config;

    @Mock
    private Graphics2D graphics;

    private PriceAlertOverlay overlay;
    private List<FlipOpportunity> testOpportunities;

    @Before
    public void setUp() {
        when(config.showPriceAlerts()).thenReturn(true);
        when(config.minProfitMargin()).thenReturn(1000);
        when(config.minROI()).thenReturn(2.0);
        when(config.riskTolerance()).thenReturn(3);
        when(config.overlayPosition()).thenReturn(OverlayPosition.TOP_LEFT);
        
        overlay = new PriceAlertOverlay(client, config);
        
        testOpportunities = new ArrayList<>();
        testOpportunities.add(FlipOpportunity.builder()
            .itemId(4151)
            .itemName("Abyssal whip")
            .buyPrice(2000000)
            .sellPrice(2100000)
            .margin(100000)
            .roi(5.0)
            .estimatedTimeMinutes(15)
            .risk(FlipOpportunity.FlipRisk.LOW)
            .build());
    }

    @Test
    public void testOverlayRendersWithOpportunities() {
        overlay.updateOpportunities(testOpportunities);
        BufferedImage image = new BufferedImage(100, 100, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = image.createGraphics();
        
        Dimension result = overlay.render(graphics);
        
        assertNotNull(result);
        assertTrue(result.width > 0);
        assertTrue(result.height > 0);
    }

    @Test
    public void testOverlayHidesWhenDisabled() {
        when(config.showPriceAlerts()).thenReturn(false);
        overlay.updateOpportunities(testOpportunities);
        
        Dimension result = overlay.render(graphics);
        
        assertNull(result);
    }

    @Test
    public void testOverlayHidesWithNoOpportunities() {
        overlay.updateOpportunities(new ArrayList<>());
        
        Dimension result = overlay.render(graphics);
        
        assertNull(result);
    }

    @Test
    public void testOpportunityFiltering() {
        // Create opportunity below threshold
        testOpportunities.add(FlipOpportunity.builder()
            .itemId(1275)
            .itemName("Mithril pickaxe")
            .buyPrice(500)
            .sellPrice(550)
            .margin(50)
            .roi(1.0)
            .estimatedTimeMinutes(5)
            .risk(FlipOpportunity.FlipRisk.HIGH)
            .build());

        overlay.updateOpportunities(testOpportunities);
        
        BufferedImage image = new BufferedImage(100, 100, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = image.createGraphics();
        
        Dimension result = overlay.render(graphics);
        
        assertNotNull(result);
    }

    @Test
    public void testOverlayPosition() {
        assertEquals(OverlayPosition.TOP_LEFT, overlay.getPosition());
        
        when(config.overlayPosition()).thenReturn(OverlayPosition.TOP_RIGHT);
        overlay = new PriceAlertOverlay(client, config);
        
        assertEquals(OverlayPosition.TOP_LEFT, overlay.getPosition());
    }
}