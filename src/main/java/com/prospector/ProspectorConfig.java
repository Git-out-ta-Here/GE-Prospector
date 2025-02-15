package com.prospector;

import net.runelite.client.config.*;
import java.awt.Color;

@ConfigGroup("geprospector")
public interface ProspectorConfig extends Config {
    @ConfigSection(
        name = "Price Alerts",
        description = "Settings for price alerts",
        position = 1
    )
    String alertsSection = "alerts";

    @ConfigSection(
        name = "Flipping",
        description = "Settings for flip opportunities",
        position = 2
    )
    String flippingSection = "flipping";

    @ConfigSection(
        name = "Display",
        description = "Display settings",
        position = 3
    )
    String displaySection = "display";

    @ConfigItem(
        keyName = "minProfitMargin",
        name = "Minimum Profit",
        description = "Minimum profit margin to show opportunities",
        section = flippingSection,
        position = 1
    )
    @Units(Units.GP)
    default int minProfitMargin() {
        return 10000;
    }

    @ConfigItem(
        keyName = "minROI",
        name = "Minimum ROI",
        description = "Minimum return on investment percentage",
        section = flippingSection,
        position = 2
    )
    @Units(Units.PERCENT)
    default double minROI() {
        return 2.0;
    }

    @ConfigItem(
        keyName = "riskTolerance",
        name = "Risk Tolerance",
        description = "1: Low risk only, 2: Include medium risk, 3: Include high risk",
        section = flippingSection,
        position = 3
    )
    @Range(min = 1, max = 3)
    default int riskTolerance() {
        return 2;
    }

    @ConfigItem(
        keyName = "updateInterval",
        name = "Update Interval",
        description = "How often to update prices (seconds)",
        section = flippingSection,
        position = 4
    )
    @Range(min = 30, max = 300)
    @Units(Units.SECONDS)
    default int updateInterval() {
        return 60;
    }

    @ConfigItem(
        keyName = "showPriceAlerts",
        name = "Show Alerts Overlay",
        description = "Show price alert overlay",
        section = alertsSection,
        position = 1
    )
    default boolean showPriceAlerts() {
        return true;
    }

    @ConfigItem(
        keyName = "alertSound",
        name = "Alert Sound",
        description = "Play a sound when an alert triggers",
        section = alertsSection,
        position = 2
    )
    default boolean alertSound() {
        return true;
    }

    @ConfigItem(
        keyName = "notifyOnAlert",
        name = "System Notification",
        description = "Show system notification on alert",
        section = alertsSection,
        position = 3
    )
    default boolean notifyOnAlert() {
        return true;
    }

    @ConfigItem(
        keyName = "profitColor",
        name = "Profit Color",
        description = "Color used for profit indicators",
        section = displaySection,
        position = 1
    )
    default Color profitColor() {
        return new Color(0, 190, 0);
    }

    @ConfigItem(
        keyName = "lossColor",
        name = "Loss Color",
        description = "Color used for loss indicators",
        section = displaySection,
        position = 2
    )
    default Color lossColor() {
        return new Color(190, 0, 0);
    }

    @ConfigItem(
        keyName = "showVolume",
        name = "Show Volume",
        description = "Show trading volume indicators",
        section = displaySection,
        position = 3
    )
    default boolean showVolume() {
        return true;
    }

    @ConfigItem(
        keyName = "showTimeEstimate",
        name = "Show Time Estimate",
        description = "Show estimated flip completion time",
        section = displaySection,
        position = 4
    )
    default boolean showTimeEstimate() {
        return true;
    }

    @ConfigItem(
        keyName = "maxDisplayedItems",
        name = "Max Displayed Items",
        description = "Maximum number of items to display in the panel",
        section = displaySection,
        position = 5
    )
    @Range(min = 1, max = 50)
    default int maxDisplayedItems() {
        return 20;
    }

    @ConfigItem(
        keyName = "compactMode",
        name = "Compact Mode",
        description = "Show less information in a compact format",
        section = displaySection,
        position = 6
    )
    default boolean compactMode() {
        return false;
    }
}
