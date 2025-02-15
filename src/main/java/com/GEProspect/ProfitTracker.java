package com.geprospect ;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.config.ConfigManager;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Singleton
public class ProfitTracker {
    private static final String CONFIG_GROUP = "geprospector";
    private static final String FLIPS_KEY = "historicalFlips";
    private static final String TARGET_KEY = "profitTarget";
    
    private final ConfigManager configManager;
    private final Gson gson;
    private List<FlipEntry> historicalFlips;
    private int profitTarget;

    @Inject
    public ProfitTracker(ConfigManager configManager) {
        this.configManager = configManager;
        this.gson = new Gson();
        loadData();
    }

    public void addFlip(FlipEntry flip) {
        historicalFlips.add(flip);
        saveFlips();
    }

    public List<FlipEntry> getHistoricalFlips() {
        return new ArrayList<>(historicalFlips);
    }

    public void setProfitTarget(int target) {
        this.profitTarget = target;
        configManager.setConfiguration(CONFIG_GROUP, TARGET_KEY, target);
    }

    public int getProfitTarget() {
        return profitTarget;
    }

    private void loadData() {
        // Load historical flips
        String flipsJson = configManager.getConfiguration(CONFIG_GROUP, FLIPS_KEY);
        if (flipsJson != null) {
            Type listType = new TypeToken<ArrayList<FlipEntry>>(){}.getType();
            historicalFlips = gson.fromJson(flipsJson, listType);
        } else {
            historicalFlips = new ArrayList<>();
        }

        // Load profit target
        String targetStr = configManager.getConfiguration(CONFIG_GROUP, TARGET_KEY);
        profitTarget = targetStr != null ? Integer.parseInt(targetStr) : 0;
    }

    private void saveFlips() {
        String json = gson.toJson(historicalFlips);
        configManager.setConfiguration(CONFIG_GROUP, FLIPS_KEY, json);
    }

    public void clearHistory() {
        historicalFlips.clear();
        saveFlips();
    }
}