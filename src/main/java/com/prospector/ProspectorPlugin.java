package com.prospector;

import com.google.inject.Provides;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GrandExchangeOfferChanged;
import net.runelite.api.events.MenuEntryAdded;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.ClientToolbar;
import net.runelite.client.ui.NavigationButton;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.util.ImageUtil;
import com.prospector.ui.PriceAlertOverlay;
import com.prospector.ui.MenuEntryHandler;
import com.prospector.service.FlipAnalysisService;
import com.prospector.util.ProspectorUtil;

import java.awt.image.BufferedImage;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Slf4j
@PluginDescriptor(
    name = "GE Prospector",
    description = "Intelligent Grand Exchange flipping with time estimation",
    tags = {"grand", "exchange", "ge", "flip", "flipping", "merch", "merchant"}
)
public class ProspectorPlugin extends Plugin {
    @Inject
    private Client client;

    @Inject
    private ProspectorConfig config;

    @Inject
    private ClientToolbar clientToolbar;

    @Inject
    private ConfigManager configManager;

    @Inject
    private OverlayManager overlayManager;

    @Inject
    private PriceAlertOverlay priceAlertOverlay;

    @Inject
    private FlipAnalysisService flipAnalysisService;

    @Inject
    private ScheduledExecutorService executorService;

    @Inject
    private MenuEntryHandler menuEntryHandler;

    private NavigationButton navButton;
    private ProspectorPanel panel;

    @Override
    protected void startUp() {
        panel = new ProspectorPanel(this, config);
        
        final BufferedImage icon = ImageUtil.loadImageResource(getClass(), "/icon.png");
        navButton = NavigationButton.builder()
            .tooltip("GE Prospector")
            .icon(icon)
            .priority(6)
            .panel(panel)
            .build();

        clientToolbar.addNavigation(navButton);
        overlayManager.add(priceAlertOverlay);
        menuEntryHandler.startUp();

        // Schedule regular opportunity updates
        executorService.scheduleAtFixedRate(
            this::updateOpportunities,
            0,
            config.updateInterval(),
            TimeUnit.SECONDS
        );

        // Schedule tracked items update
        executorService.scheduleAtFixedRate(
            this::updateTrackedItems,
            0,
            30,
            TimeUnit.SECONDS
        );

        log.info("GE Prospector started!");
    }

    @Override
    protected void shutDown() {
        clientToolbar.removeNavigation(navButton);
        overlayManager.remove(priceAlertOverlay);
        menuEntryHandler.shutDown();
        executorService.shutdown();
        log.info("GE Prospector stopped!");
    }

    @Subscribe
    public void onGameStateChanged(GameStateChanged gameStateChanged) {
        if (gameStateChanged.getGameState() == GameState.LOGGED_IN) {
            updateOpportunities();
        }
    }

    @Subscribe
    public void onGrandExchangeOfferChanged(GrandExchangeOfferChanged event) {
        // Update opportunities when GE offers change
        updateOpportunities();
    }

    @Subscribe
    public void onMenuEntryAdded(MenuEntryAdded event) {
        menuEntryHandler.onMenuEntryAdded(event);
    }

    private void updateOpportunities() {
        if (client.getGameState() != GameState.LOGGED_IN) {
            return;
        }

        flipAnalysisService.analyzeOpportunities()
            .thenAccept(opportunities -> {
                if (client.isClientThread()) {
                    priceAlertOverlay.updateOpportunities(opportunities);
                    panel.updateOpportunities(opportunities);
                } else {
                    client.invokeLater(() -> {
                        priceAlertOverlay.updateOpportunities(opportunities);
                        panel.updateOpportunities(opportunities);
                    });
                }
            })
            .exceptionally(ex -> {
                log.error("Error updating opportunities", ex);
                return null;
            });
    }

    private void updateTrackedItems() {
        if (client.getGameState() != GameState.LOGGED_IN) {
            return;
        }

        Set<Integer> trackedItems = flipAnalysisService.getTrackedItems();
        if (!trackedItems.isEmpty()) {
            trackedItems.forEach(itemId -> 
                flipAnalysisService.analyzeItem(itemId)
                    .thenAccept(opportunity -> {
                        if (opportunity != null && 
                            opportunity.meetsThresholds(
                                config.minProfitMargin(),
                                config.minROI(),
                                config.riskTolerance())) {
                            priceAlertOverlay.updateOpportunities(
                                Collections.singletonList(opportunity));
                            ProspectorUtil.sendNotification(String.format(
                                "Tracked item %s: Margin %s, ROI %.1f%%",
                                opportunity.getItemName(),
                                ProspectorUtil.formatPrice(opportunity.getMargin()),
                                opportunity.getRoi()));
                        }
                    })
            );
        }
    }

    @Provides
    ProspectorConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(ProspectorConfig.class);
    }
}
