package com.GEProspect;

import lombok.Data;

@Data
public class EstimatedTime {
    private final int minutes;
    private final double confidence;
    
    public VolumeCategory getCategory() {
        if (minutes < 5) return VolumeCategory.VERY_HIGH;
        if (minutes < 15) return VolumeCategory.HIGH;
        if (minutes < 30) return VolumeCategory.MEDIUM;
        if (minutes < 60) return VolumeCategory.LOW;
        return VolumeCategory.VERY_LOW;
    }
    
    public enum VolumeCategory {
        VERY_HIGH("< 5 mins", "🟢"),
        HIGH("5-15 mins", "🟡"),
        MEDIUM("15-30 mins", "🟠"),
        LOW("30-60 mins", "🔴"),
        VERY_LOW("> 1 hour", "⚫");
        
        private final String timeRange;
        private final String indicator;
        
        VolumeCategory(String timeRange, String indicator) {
            this.timeRange = timeRange;
            this.indicator = indicator;
        }
        
        public String getTimeRange() {
            return timeRange;
        }
        
        public String getIndicator() {
            return indicator;
        }
    }
}