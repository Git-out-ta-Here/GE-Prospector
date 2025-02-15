package com.prospector.model;

import lombok.Data;
import lombok.Builder;

@Data
@Builder
public class FlipOpportunity {
    private int itemId;
    private String itemName;
    private long buyPrice;
    private long sellPrice;
    private long margin;
    private double roi;
    private int estimatedTimeMinutes;
    private int hourlyVolume;
    private FlipRisk risk;
    private TrendDirection trendDirection;

    public enum FlipRisk {
        LOW,
        MEDIUM,
        HIGH
    }

    public enum TrendDirection {
        UPWARD,
        DOWNWARD,
        STABLE,
        VOLATILE
    }

    public double getProfitPerHour() {
        if (estimatedTimeMinutes <= 0) return 0;
        return (margin * (60.0 / estimatedTimeMinutes));
    }

    public boolean meetsThresholds(int minMargin, double minRoi, int maxRisk) {
        return margin >= minMargin &&
               roi >= minRoi &&
               risk.ordinal() <= maxRisk;
    }
}