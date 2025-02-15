package com.prospector.api;

import com.google.gson.annotations.SerializedName;
import lombok.Data;
import java.time.Instant;
import java.util.List;

@Data
public class TimeSeriesData {
    private int itemId;
    private List<TimeSeriesEntry> entries;

    @Data
    public static class TimeSeriesEntry {
        @SerializedName("avgHighPrice")
        private long avgHighPrice;
        
        @SerializedName("avgLowPrice")
        private long avgLowPrice;
        
        @SerializedName("highPriceVolume")
        private int highPriceVolume;
        
        @SerializedName("lowPriceVolume")
        private int lowPriceVolume;
        
        @SerializedName("timestamp")
        private long timestamp;

        public long getAveragePrice() {
            return (avgHighPrice + avgLowPrice) / 2;
        }

        public int getTotalVolume() {
            return highPriceVolume + lowPriceVolume;
        }
    }
}