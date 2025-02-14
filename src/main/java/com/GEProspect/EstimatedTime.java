package com.GEProspect;

public class EstimatedTime {
    public static final EstimatedTime VERY_HIGH = new EstimatedTime(5, 0.9);
    public static final EstimatedTime HIGH = new EstimatedTime(15, 0.7);
    public static final EstimatedTime MEDIUM = new EstimatedTime(30, 0.5);
    public static final EstimatedTime LOW = new EstimatedTime(60, 0.3);
    public static final EstimatedTime VERY_LOW = new EstimatedTime(120, 0.1);

    private final int minutesToComplete;
    private final double confidence;

    public EstimatedTime(int minutesToComplete, double confidence) {
        this.minutesToComplete = minutesToComplete;
        this.confidence = confidence;
    }

    public int getMinutesToComplete() {
        return minutesToComplete;
    }

    public double getConfidence() {
        return confidence;
    }

    public VolumeCategory getVolumeCategory() {
        if (minutesToComplete <= 5) return VolumeCategory.VERY_HIGH;
        if (minutesToComplete <= 15) return VolumeCategory.HIGH;
        if (minutesToComplete <= 30) return VolumeCategory.MEDIUM;
        if (minutesToComplete <= 60) return VolumeCategory.LOW;
        return VolumeCategory.VERY_LOW;
    }

    public enum VolumeCategory {
        VERY_HIGH("🟢"),
        HIGH("🟡"),
        MEDIUM("🟠"),
        LOW("🔴"),
        VERY_LOW("⚫");

        private final String icon;

        VolumeCategory(String icon) {
            this.icon = icon;
        }

        public String getIcon() {
            return icon;
        }
    }
}