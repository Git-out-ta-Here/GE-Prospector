package com.GEProspect;

import lombok.Data;
import java.time.Instant;
import java.util.LinkedList;
import java.util.Queue;

@Data
public class TradeVolume {
    private static final int WINDOW_SIZE = 24; // 24 hour window
    private static final int HOUR_IN_MILLIS = 3600000;
    
    private final Queue<Trade> tradeHistory;
    private int totalVolume;
    
    public TradeVolume() {
        this.tradeHistory = new LinkedList<>();
        this.totalVolume = 0;
    }
    
    public void addTrade(int quantity) {
        long now = System.currentTimeMillis();
        cleanOldTrades(now);
        
        tradeHistory.offer(new Trade(quantity, now));
        totalVolume += quantity;
    }
    
    private void cleanOldTrades(long currentTime) {
        while (!tradeHistory.isEmpty()) {
            Trade oldestTrade = tradeHistory.peek();
            if (currentTime - oldestTrade.getTimestamp() > WINDOW_SIZE * HOUR_IN_MILLIS) {
                tradeHistory.poll();
                totalVolume -= oldestTrade.getQuantity();
            } else {
                break;
            }
        }
    }
    
    public EstimatedTime calculateEstimatedTime(int quantity) {
        if (tradeHistory.isEmpty()) {
            return new EstimatedTime(60, 0.5); // Default estimate
        }
        
        double hourlyVolume = calculateHourlyVolume();
        if (hourlyVolume <= 0) {
            return new EstimatedTime(120, 0.3); // Low confidence estimate
        }
        
        double estimatedHours = quantity / hourlyVolume;
        double confidence = calculateConfidence(quantity, hourlyVolume);
        
        return new EstimatedTime((int)(estimatedHours * 60), confidence);
    }
    
    private double calculateHourlyVolume() {
        long now = System.currentTimeMillis();
        cleanOldTrades(now);
        
        if (tradeHistory.isEmpty()) return 0;
        
        long oldestTimestamp = tradeHistory.peek().getTimestamp();
        double hoursElapsed = (now - oldestTimestamp) / (double)HOUR_IN_MILLIS;
        
        return hoursElapsed > 0 ? totalVolume / hoursElapsed : 0;
    }
    
    private double calculateConfidence(int quantity, double hourlyVolume) {
        if (hourlyVolume <= 0) return 0.3;
        
        // Confidence based on trade history size and volume ratio
        double historyConfidence = Math.min(tradeHistory.size() / 100.0, 1.0);
        double volumeRatio = Math.min(hourlyVolume / quantity, 1.0);
        
        return (historyConfidence + volumeRatio) / 2.0;
    }
    
    @Data
    private static class Trade {
        private final int quantity;
        private final long timestamp;
    }
}