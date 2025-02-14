package com.GEProspect;

import com.google.inject.Provides;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.events.GrandExchangeOfferChanged;
import net.runelite.api.events.GameStateChanged;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.ClientToolbar;
import net.runelite.client.ui.NavigationButton;
import net.runelite.client.util.ImageUtil;
import java.awt.image.BufferedImage;
import java.util.concurrent.ScheduledExecutorService;
import javax.inject.Singleton;

@Slf4j
@PluginDescriptor(
    name = "GE-Prospector",
    description = "Analyzes Grand Exchange data for profitable flipping opportunities",
    tags = {"grand", "exchange", "flipping", "money", "making", "merching"}
)
@Singleton
public class GEProspectorPlugin extends Plugin {
    @Inject
    private Client client;

    @Inject
    private GEProspectorConfig config;

    @Inject
    private ClientToolbar clientToolbar;

    @Inject
    private ScheduledExecutorService executor;

    private NavigationButton navButton;
    private GEProspectorPanel panel;
    private MarketDataService marketDataService;
    private FlipTracker flipTracker;

    @Override
    protected void startUp() throws Exception {
        log.info("GE-Prospector starting up");
        
        // Initialize services
        marketDataService = new MarketDataService(executor, config);
        flipTracker = new FlipTracker();
        panel = new GEProspectorPanel(marketDataService, flipTracker, config);

        // Load the icon
        final BufferedImage icon = ImageUtil.loadImageResource(getClass(), "/icon.png");

        navButton = NavigationButton.builder()
            .tooltip("GE-Prospector")
            .icon(icon)
            .priority(5)
            .panel(panel)
            .build();

        clientToolbar.addNavigation(navButton);
    }

    @Override
    protected void shutDown() throws Exception {
        log.info("GE-Prospector shutting down");
        clientToolbar.removeNavigation(navButton);
        marketDataService.shutdown();
    }

    @Subscribe
    public void onGrandExchangeOfferChanged(GrandExchangeOfferChanged event) {
        if (!config.isEnabled()) return;
        flipTracker.processOffer(event.getOffer());
    }

    @Subscribe
    public void onGameStateChanged(GameStateChanged event) {
        if (event.getGameState() == GameState.LOGGED_IN) {
            marketDataService.refreshPrices();
        }
    }

    @Provides
    GEProspectorConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(GEProspectorConfig.class);
    }
}
