package com.prospector.api;

import lombok.Data;
import java.time.Instant;
import java.util.List;

@Data
public class TimeSeriesData {
    private int itemId;
    private List<TimeSeriesEntry> entries;

    @Data
    public static class TimeSeriesEntry {
        private Instant timestamp;
        private long avgHighPrice;
        private long avgLowPrice;
        private int highPriceVolume;
        private int lowPriceVolume;
    }
}