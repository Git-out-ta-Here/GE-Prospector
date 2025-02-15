package com.prospector;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.prospector.api.OSRSWikiPriceClient;
import com.prospector.api.PriceApiClient;
import com.prospector.service.*;
import com.prospector.util.ItemSearchUtil;
import com.prospector.util.ProspectorUtil;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.game.ItemManager;
import net.runelite.client.chat.ChatMessageManager;

public class ModuleConfig extends AbstractModule {
    @Override
    protected void configure() {
        bind(PriceApiClient.class).to(OSRSWikiPriceClient.class).in(Singleton.class);
    }

    @Provides
    @Singleton
    AlertManager provideAlertManager(
            ConfigManager configManager,
            PriceApiClient priceApiClient,
            ItemManager itemManager,
            ProspectorUtil prospectorUtil) {
        return new AlertManager(configManager, priceApiClient, itemManager, prospectorUtil);
    }

    @Provides
    @Singleton
    AlertService provideAlertService(
            AlertManager alertManager,
            ItemManager itemManager,
            ProspectorUtil prospectorUtil) {
        return new AlertService(alertManager, itemManager, prospectorUtil);
    }

    @Provides
    @Singleton
    CacheService provideCacheService(ItemManager itemManager) {
        return new CacheService(itemManager);
    }

    @Provides
    @Singleton
    FlipAnalysisService provideFlipAnalysisService(
            PriceApiClient priceApiClient,
            ItemManager itemManager) {
        return new FlipAnalysisService(priceApiClient, itemManager);
    }

    @Provides
    @Singleton
    ItemSearchUtil provideItemSearchUtil(ItemManager itemManager) {
        return new ItemSearchUtil(itemManager);
    }

    @Provides
    @Singleton
    ProspectorUtil provideProspectorUtil(
            net.runelite.api.Client client,
            ChatMessageManager chatMessageManager) {
        return new ProspectorUtil(client, chatMessageManager);
    }
}