package com.prospector.util;

import net.runelite.api.ItemComposition;
import net.runelite.client.game.ItemManager;
import net.runelite.client.util.Text;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class ItemSearchUtil {
    private final ItemManager itemManager;

    @Inject
    public ItemSearchUtil(ItemManager itemManager) {
        this.itemManager = itemManager;
    }

    public List<SearchResult> searchItems(String query) {
        String sanitizedQuery = Text.sanitize(query.toLowerCase());
        List<SearchResult> results = new ArrayList<>();
        
        // Search through all items
        Predicate<ItemComposition> filter = item -> {
            String itemName = Text.sanitize(item.getName().toLowerCase());
            return itemName.contains(sanitizedQuery);
        };

        itemManager.search(filter).forEach(item -> {
            if (!item.isNote() && item.isTradeable()) {
                results.add(new SearchResult(item.getId(), item.getName()));
            }
        });

        // Sort results by relevance
        results.sort((a, b) -> {
            String aName = Text.sanitize(a.name.toLowerCase());
            String bName = Text.sanitize(b.name.toLowerCase());
            
            // Exact matches first
            if (aName.equals(sanitizedQuery) && !bName.equals(sanitizedQuery)) {
                return -1;
            }
            if (!aName.equals(sanitizedQuery) && bName.equals(sanitizedQuery)) {
                return 1;
            }
            
            // Starts with query next
            if (aName.startsWith(sanitizedQuery) && !bName.startsWith(sanitizedQuery)) {
                return -1;
            }
            if (!aName.startsWith(sanitizedQuery) && bName.startsWith(sanitizedQuery)) {
                return 1;
            }
            
            // Alphabetical order for remaining results
            return aName.compareTo(bName);
        });

        return results;
    }

    public static class SearchResult {
        public final int id;
        public final String name;

        public SearchResult(int id, String name) {
            this.id = id;
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }
}