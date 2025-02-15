package com.prospector;

import lombok.extern.slf4j.Slf4j;
import net.runelite.client.ui.PluginPanel;
import net.runelite.client.ui.components.IconTextField;
import net.runelite.client.ui.components.materialtabs.MaterialTab;
import net.runelite.client.ui.components.materialtabs.MaterialTabGroup;
import net.runelite.client.util.QuantityFormatter;
import net.runelite.client.util.ColorUtil;
import com.prospector.model.FlipOpportunity;
import com.prospector.ui.SettingsPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;
import java.util.ArrayList;
import javax.inject.Inject;

@Slf4j
public class ProspectorPanel extends PluginPanel {
    private static final Color PROFIT_COLOR = new Color(0, 190, 0);
    private static final Color LOSS_COLOR = new Color(190, 0, 0);
    
    private final ProspectorPlugin plugin;
    private final ProspectorConfig config;
    private final IconTextField searchBar;
    private final JPanel mainDisplay;
    private final MaterialTabGroup tabGroup;
    private final JPanel itemsContainer;
    private final JPanel opportunitiesTab;
    private final JPanel alertsTab;
    private final JPanel statsTab;
    private final SettingsPanel settingsPanel;
    private List<FlipOpportunity> opportunities = new ArrayList<>();
    
    @Inject
    public ProspectorPanel(
            ProspectorPlugin plugin,
            ProspectorConfig config,
            SettingsPanel settingsPanel) {
        super(false);
        this.plugin = plugin;
        this.config = config;
        this.settingsPanel = settingsPanel;

        setLayout(new BorderLayout());
        setBackground(ColorScheme.DARKER_GRAY_COLOR);

        // Search bar
        searchBar = new IconTextField();
        searchBar.setIcon(IconTextField.Icon.SEARCH);
        searchBar.setPreferredSize(new Dimension(PluginPanel.PANEL_WIDTH - 20, 30));
        searchBar.setBackground(ColorScheme.DARKER_GRAY_COLOR);
        searchBar.setHoverBackgroundColor(ColorScheme.DARK_GRAY_HOVER_COLOR);
        searchBar.addActionListener(e -> updateFilter());

        // Tabs
        tabGroup = new MaterialTabGroup();
        tabGroup.setLayout(new GridLayout(1, 4));

        opportunitiesTab = new JPanel();
        opportunitiesTab.setLayout(new BorderLayout());
        MaterialTab flipsTab = new MaterialTab("Flips", tabGroup, opportunitiesTab);

        alertsTab = new JPanel();
        alertsTab.setLayout(new BorderLayout());
        MaterialTab alertTab = new MaterialTab("Alerts", tabGroup, alertsTab);

        statsTab = new JPanel();
        statsTab.setLayout(new BorderLayout());
        MaterialTab statTab = new MaterialTab("Stats", tabGroup, statsTab);

        MaterialTab settingsTab = new MaterialTab("Settings", tabGroup, settingsPanel);

        tabGroup.addTab(flipsTab);
        tabGroup.addTab(alertTab);
        tabGroup.addTab(statTab);
        tabGroup.addTab(settingsTab);
        tabGroup.select(flipsTab);

        // Main display
        mainDisplay = new JPanel();
        mainDisplay.setLayout(new BorderLayout());
        mainDisplay.setBackground(ColorScheme.DARKER_GRAY_COLOR);

        // Items container
        itemsContainer = new JPanel();
        itemsContainer.setLayout(new BoxLayout(itemsContainer, BoxLayout.Y_AXIS));
        itemsContainer.setBackground(ColorScheme.DARKER_GRAY_COLOR);

        JScrollPane scrollPane = new JScrollPane(itemsContainer);
        scrollPane.setBackground(ColorScheme.DARKER_GRAY_COLOR);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        // Stats panel
        JPanel statsPanel = createStatsPanel();
        statsTab.add(statsPanel, BorderLayout.CENTER);

        // Setup main layout
        JPanel northPanel = new JPanel(new BorderLayout());
        northPanel.setBackground(ColorScheme.DARKER_GRAY_COLOR);
        northPanel.add(searchBar, BorderLayout.NORTH);
        northPanel.add(tabGroup, BorderLayout.CENTER);

        mainDisplay.add(northPanel, BorderLayout.NORTH);
        mainDisplay.add(scrollPane, BorderLayout.CENTER);

        add(mainDisplay, BorderLayout.CENTER);
    }

    private JPanel createStatsPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(ColorScheme.DARKER_GRAY_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        return panel;
    }

    public void updateOpportunities(List<FlipOpportunity> opportunities) {
        this.opportunities = opportunities;
        SwingUtilities.invokeLater(this::rebuildList);
    }

    private void rebuildList() {
        itemsContainer.removeAll();

        for (FlipOpportunity opportunity : opportunities) {
            if (!matchesFilter(opportunity)) {
                continue;
            }

            JPanel itemPanel = buildItemPanel(opportunity);
            itemsContainer.add(itemPanel);
            itemsContainer.add(Box.createRigidArea(new Dimension(0, 5)));
        }

        itemsContainer.revalidate();
        itemsContainer.repaint();
    }

    private boolean matchesFilter(FlipOpportunity opportunity) {
        String filter = searchBar.getText().toLowerCase();
        if (filter.isEmpty()) {
            return true;
        }

        return opportunity.getItemName().toLowerCase().contains(filter);
    }

    private JPanel buildItemPanel(FlipOpportunity opportunity) {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout(5, 0));
        panel.setBackground(ColorScheme.DARKER_GRAY_COLOR);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, ColorScheme.DARK_GRAY_COLOR),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));

        // Item name and margin
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(ColorScheme.DARKER_GRAY_COLOR);

        JLabel nameLabel = new JLabel(opportunity.getItemName());
        nameLabel.setForeground(Color.WHITE);

        String profitText = QuantityFormatter.formatNumber(opportunity.getMargin()) + " gp";
        JLabel marginLabel = new JLabel(profitText);
        marginLabel.setForeground(opportunity.getMargin() > 0 ? PROFIT_COLOR : LOSS_COLOR);

        headerPanel.add(nameLabel, BorderLayout.WEST);
        headerPanel.add(marginLabel, BorderLayout.EAST);

        // Details
        JPanel detailsPanel = new JPanel(new GridLayout(2, 2, 5, 2));
        detailsPanel.setBackground(ColorScheme.DARKER_GRAY_COLOR);

        detailsPanel.add(new JLabel("ROI:"));
        detailsPanel.add(new JLabel(String.format("%.1f%%", opportunity.getRoi())));
        
        detailsPanel.add(new JLabel("Time:"));
        detailsPanel.add(new JLabel(formatTime(opportunity.getEstimatedTimeMinutes())));

        panel.add(headerPanel, BorderLayout.NORTH);
        panel.add(detailsPanel, BorderLayout.CENTER);

        return panel;
    }

    private String formatTime(int minutes) {
        if (minutes < 60) {
            return minutes + " min";
        }
        return String.format("%dh %dm", minutes / 60, minutes % 60);
    }

    private void updateFilter() {
        rebuildList();
    }
}

class ColorScheme {
    public static final Color DARKER_GRAY_COLOR = new Color(30, 30, 30);
    public static final Color DARK_GRAY_HOVER_COLOR = new Color(60, 60, 60);
}