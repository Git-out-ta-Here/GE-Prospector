package com.prospector.model;

import lombok.Builder;
import lombok.Data;
import lombok.AllArgsConstructor;

@Data
@Builder
@AllArgsConstructor
public class PriceAlert {
    private int itemId;
    private String itemName;
    private long targetPrice;
    private AlertType type;
    private AlertCondition condition;
    private boolean enabled;
    private String notes;

    public enum AlertType {
        PRICE("Price"),
        MARGIN("Margin"),
        VOLUME("Volume"),
        ROI("ROI");

        private final String displayName;

        AlertType(String displayName) {
            this.displayName = displayName;
        }

        @Override
        public String toString() {
            return displayName;
        }
    }

    public enum AlertCondition {
        ABOVE("Above"),
        BELOW("Below"),
        EQUALS("Equals");

        private final String displayName;

        AlertCondition(String displayName) {
            this.displayName = displayName;
        }

        @Override
        public String toString() {
            return displayName;
        }
    }

    public boolean isTriggered(long currentValue) {
        switch (condition) {
            case ABOVE:
                return currentValue > targetPrice;
            case BELOW:
                return currentValue < targetPrice;
            case EQUALS:
                return currentValue == targetPrice;
            default:
                return false;
        }
    }
}