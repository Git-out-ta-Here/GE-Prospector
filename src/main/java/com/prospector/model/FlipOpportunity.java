package com.prospector.model;

import lombok.Builder;
import lombok.Data;
import lombok.AllArgsConstructor;

@Data
@Builder
@AllArgsConstructor
public class FlipOpportunity {
    private final int itemId;
    private final String itemName;
    private final long buyPrice;
    private final long sellPrice;
    private final long margin;
    private final double roi;
    private final int estimatedTimeMinutes;
    private final int hourlyVolume;
    private final FlipRisk risk;
    private final TrendDirection trendDirection;

    public enum FlipRisk {
        LOW("Low Risk"),
        MEDIUM("Medium Risk"),
        HIGH("High Risk");

        private final String displayName;

        FlipRisk(String displayName) {
            this.displayName = displayName;
        }

        @Override
        public String toString() {
            return displayName;
        }
    }

    public enum TrendDirection {
        UPWARD("↑"),
        DOWNWARD("↓"),
        STABLE("→"),
        VOLATILE("↕");

        private final String symbol;

        TrendDirection(String symbol) {
            this.symbol = symbol;
        }

        @Override
        public String toString() {
            return symbol;
        }
    }

    public boolean meetsThresholds(long minMargin, double minRoi, int riskTolerance) {
        if (margin < minMargin || roi < minRoi) {
            return false;
        }

        switch (risk) {
            case LOW:
                return true;
            case MEDIUM:
                return riskTolerance >= 2;
            case HIGH:
                return riskTolerance >= 3;
            default:
                return false;
        }
    }

    public String getVolumeIndicator() {
        if (hourlyVolume > 100) {
            return "🟢"; // Very High
        } else if (hourlyVolume > 50) {
            return "🟡"; // High
        } else if (hourlyVolume > 25) {
            return "🟠"; // Medium
        } else if (hourlyVolume > 10) {
            return "🔴"; // Low
        } else {
            return "⚫"; // Very Low
        }
    }

    public String getTimeEstimate() {
        if (estimatedTimeMinutes < 60) {
            return estimatedTimeMinutes + "m";
        }
        return (estimatedTimeMinutes / 60) + "h " + (estimatedTimeMinutes % 60) + "m";
    }
}