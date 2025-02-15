package com.prospector.ui;

import net.runelite.api.Client;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayPriority;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.TitleComponent;
import com.prospector.ProspectorConfig;
import com.prospector.model.FlipOpportunity;

import javax.inject.Inject;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.util.List;

public class PriceAlertOverlay extends OverlayPanel {
    private final Client client;
    private final ProspectorConfig config;
    private List<FlipOpportunity> currentOpportunities;

    @Inject
    private PriceAlertOverlay(Client client, ProspectorConfig config) {
        super(PriceAlertOverlay.class);
        this.client = client;
        this.config = config;
        setPosition(OverlayPosition.TOP_LEFT);
        setPriority(OverlayPriority.MED);
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        if (!config.showPriceAlerts() || currentOpportunities == null || currentOpportunities.isEmpty()) {
            return null;
        }

        panelComponent.getChildren().add(TitleComponent.builder()
            .text("GE Prospector")
            .color(Color.GREEN)
            .build());

        for (FlipOpportunity opportunity : currentOpportunities) {
            if (opportunity.meetsThresholds(
                    config.minProfitMargin(),
                    config.minROI(),
                    config.riskTolerance())) {
                
                addOpportunityToPanel(opportunity);
            }
        }

        return super.render(graphics);
    }

    private void addOpportunityToPanel(FlipOpportunity opportunity) {
        // Add item name
        panelComponent.getChildren().add(LineComponent.builder()
            .left(opportunity.getItemName())
            .right(formatProfit(opportunity.getMargin()))
            .rightColor(opportunity.getMargin() > 0 ? Color.GREEN : Color.RED)
            .build());

        // Add ROI
        panelComponent.getChildren().add(LineComponent.builder()
            .left("ROI")
            .right(String.format("%.1f%%", opportunity.getRoi()))
            .build());

        // Add time estimate
        panelComponent.getChildren().add(LineComponent.builder()
            .left("Time")
            .right(formatTime(opportunity.getEstimatedTimeMinutes()))
            .build());
    }

    private String formatProfit(long profit) {
        return profit > 1000000 ? 
            String.format("%.1fM", profit / 1000000.0) :
            String.format("%,d", profit);
    }

    private String formatTime(int minutes) {
        return minutes < 60 ? 
            minutes + "m" :
            String.format("%dh %dm", minutes / 60, minutes % 60);
    }

    public void updateOpportunities(List<FlipOpportunity> opportunities) {
        this.currentOpportunities = opportunities;
    }
}