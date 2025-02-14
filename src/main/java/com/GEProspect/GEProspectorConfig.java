package com.GEProspect;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("geprospector")
public interface GEProspectorConfig extends Config {
    @ConfigItem(
        keyName = "enabled",
        name = "Enable Plugin",
        description = "Enable GE Prospector plugin",
        position = 0
    )
    default boolean isEnabled() {
        return true;
    }

    @ConfigItem(
        keyName = "minimumProfit",
        name = "Minimum Profit",
        description = "Minimum profit margin to show an item",
        position = 1
    )
    default int minimumProfit() {
        return 1000;
    }

    @ConfigItem(
        keyName = "enableAlerts",
        name = "Price Alerts",
        description = "Enable notifications for price targets",
        position = 2
    )
    default boolean enableAlerts() {
        return true;
    }

    @ConfigItem(
        keyName = "updateInterval",
        name = "Update Interval",
        description = "How often to update prices (seconds)",
        position = 3
    )
    default int updateInterval() {
        return 60;
    }

    @ConfigItem(
        keyName = "alertSound",
        name = "Alert Sound",
        description = "Play a sound when price alerts are triggered",
        position = 4
    )
    default boolean alertSound() {
        return true;
    }

    @ConfigItem(
        keyName = "chatAlerts",
        name = "Chat Alerts",
        description = "Show price alerts in chat",
        position = 5
    )
    default boolean chatAlerts() {
        return true;
    }

    @ConfigItem(
        keyName = "watchlistSize",
        name = "Watchlist Size",
        description = "Maximum number of items in watchlist",
        position = 6
    )
    default int watchlistSize() {
        return 20;
    }
}
