package com.prospector;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.Range;
import net.runelite.client.config.Units;
import java.awt.Color;

@ConfigGroup("geprospector")
public interface ProspectorConfig extends Config {
    @ConfigItem(
        keyName = "minProfitMargin",
        name = "Minimum Profit Margin",
        description = "Minimum profit margin to highlight items",
        position = 1
    )
    @Range(min = 1)
    default int minProfitMargin() {
        return 1000;
    }

    @ConfigItem(
        keyName = "minROI",
        name = "Minimum ROI %",
        description = "Minimum return on investment percentage",
        position = 2
    )
    @Range(min = 1, max = 100)
    default int minROI() {
        return 2;
    }

    @ConfigItem(
        keyName = "updateInterval",
        name = "Update Interval",
        description = "How often to update prices (seconds)",
        position = 3
    )
    @Range(min = 30, max = 300)
    default int updateInterval() {
        return 60;
    }

    @ConfigItem(
        keyName = "enableAlerts",
        name = "Enable Price Alerts",
        description = "Enable price alert notifications",
        position = 4
    )
    default boolean enableAlerts() {
        return true;
    }

    @ConfigItem(
        keyName = "highlightProfitable",
        name = "Highlight Profitable Items",
        description = "Highlight items meeting profit threshold",
        position = 5
    )
    default boolean highlightProfitable() {
        return true;
    }

    @ConfigItem(
        keyName = "showTimeEstimates",
        name = "Show Time Estimates",
        description = "Show estimated flip completion time",
        position = 6
    )
    default boolean showTimeEstimates() {
        return true;
    }

    @ConfigItem(
        keyName = "cacheEnabled",
        name = "Enable Caching",
        description = "Cache price data to reduce API calls",
        position = 7
    )
    default boolean cacheEnabled() {
        return true;
    }

    @ConfigItem(
        keyName = "riskTolerance",
        name = "Risk Tolerance",
        description = "Risk tolerance level for recommendations (1-5)",
        position = 8
    )
    @Range(min = 1, max = 5)
    default int riskTolerance() {
        return 3;
    }

    @ConfigItem(
        keyName = "maxInvestment",
        name = "Max Investment",
        description = "Maximum investment per item (gp)",
        position = 9
    )
    @Range(min = 1000)
    default int maxInvestment() {
        return 1000000;
    }

    @ConfigItem(
        keyName = "showVolumeIndicators",
        name = "Show Volume Indicators",
        description = "Show trading volume indicators",
        position = 10
    )
    default boolean showVolumeIndicators() {
        return true;
    }

    @ConfigItem(
        keyName = "showPriceAlerts",
        name = "Show Price Alerts",
        description = "Show price alert overlay",
        position = 11
    )
    default boolean showPriceAlerts() {
        return true;
    }

    @ConfigItem(
        keyName = "overlayPosition",
        name = "Overlay Position",
        description = "Position of the price alert overlay",
        position = 12
    )
    default OverlayPosition overlayPosition() {
        return OverlayPosition.TOP_LEFT;
    }

    @ConfigItem(
        keyName = "overlayColor",
        name = "Overlay Color",
        description = "Color of the price alert overlay",
        position = 13
    )
    default Color overlayColor() {
        return Color.GREEN;
    }

    @ConfigItem(
        keyName = "maxAlerts",
        name = "Maximum Alerts",
        description = "Maximum number of alerts to show in overlay",
        position = 14
    )
    @Range(min = 1, max = 10)
    default int maxAlerts() {
        return 5;
    }

    @ConfigItem(
        keyName = "alertDuration",
        name = "Alert Duration",
        description = "How long alerts should remain visible (seconds)",
        position = 15
    )
    @Range(min = 5, max = 300)
    @Units(Units.SECONDS)
    default int alertDuration() {
        return 30;
    }

    @ConfigItem(
        keyName = "flashAlerts",
        name = "Flash New Alerts",
        description = "Flash overlay when new opportunities are found",
        position = 16
    )
    default boolean flashAlerts() {
        return true;
    }
}
