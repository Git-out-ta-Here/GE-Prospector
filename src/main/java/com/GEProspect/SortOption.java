package com.GEProspect;

public enum SortOption {
    PROFIT_MARGIN("Profit Margin", (a, b) -> b.getProfitMargin() - a.getProfitMargin()),
    PRICE("Price", (a, b) -> b.getHighPrice() - a.getHighPrice()),
    PROFIT_PER_HOUR("Profit/Hour", (a, b, timeEstA, timeEstB) -> {
        double profitPerHourA = calculateProfitPerHour(a, timeEstA);
        double profitPerHourB = calculateProfitPerHour(b, timeEstB);
        return Double.compare(profitPerHourB, profitPerHourA);
    }),
    FLIP_TIME("Flip Time", (a, b, timeEstA, timeEstB) -> 
        timeEstA.getMinutes() - timeEstB.getMinutes()),
    VOLUME("Volume", (a, b, timeEstA, timeEstB) -> 
        timeEstB.getCategory().ordinal() - timeEstA.getCategory().ordinal());

    private final String displayName;
    private final ItemComparator comparator;

    SortOption(String displayName, ItemComparator comparator) {
        this.displayName = displayName;
        this.comparator = comparator;
    }

    public String getDisplayName() {
        return displayName;
    }

    public ItemComparator getComparator() {
        return comparator;
    }

    private static double calculateProfitPerHour(ItemPrice item, EstimatedTime timeEst) {
        double hours = timeEst.getMinutes() / 60.0;
        return hours > 0 ? item.getProfitMargin() / hours : 0;
    }

    @FunctionalInterface
    public interface ItemComparator {
        default int compare(ItemPrice a, ItemPrice b, EstimatedTime timeEstA, EstimatedTime timeEstB) {
            return compare(a, b);
        }
        
        default int compare(ItemPrice a, ItemPrice b) {
            return 0;
        }
    }
}