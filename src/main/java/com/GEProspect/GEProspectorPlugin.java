package com.GEProspect;

import com.google.inject.Provides;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.swing.SwingUtilities;
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
import net.runelite.client.Notifier;
import net.runelite.client.game.ItemManager;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.chat.ChatMessageManager;
import java.awt.image.BufferedImage;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

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

    @Inject
    private Notifier notifier;

    @Inject
    private ItemManager itemManager;

    @Inject
    private ClientThread clientThread;

    @Inject
    private ChatMessageManager chatMessageManager;

    @Inject
    private ConfigManager configManager;

    private NavigationButton navButton;
    private GEProspectorPanel panel;
    private MarketDataService marketDataService;
    private FlipTracker flipTracker;
    private WatchlistManager watchlistManager;
    private ProfitTracker profitTracker;

    @Override
    protected void startUp() throws Exception {
        log.info("GE-Prospector starting up");
        
        // Initialize services
        marketDataService = new MarketDataService(executor, config);
        profitTracker = new ProfitTracker(configManager);
        flipTracker = new FlipTracker(profitTracker);
        watchlistManager = new WatchlistManager(
            marketDataService, 
            config, 
            client, 
            notifier, 
            chatMessageManager,
            configManager
        );
        
        panel = new GEProspectorPanel(
            marketDataService,
            flipTracker,
            watchlistManager,
            config,
            itemManager,
            clientThread
        );

        // Schedule alert checks
        executor.scheduleAtFixedRate(
            () -> {
                watchlistManager.checkAlerts();
                SwingUtilities.invokeLater(() -> panel.updateWatchlist());
            },
            30,
            30,
            TimeUnit.SECONDS
        );

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
        executor.shutdown();
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
