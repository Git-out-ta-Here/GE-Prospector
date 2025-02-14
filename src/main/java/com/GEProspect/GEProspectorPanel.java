package com.GEProspect;

import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.PluginPanel;
import net.runelite.client.ui.components.materialtabs.MaterialTab;
import net.runelite.client.ui.components.materialtabs.MaterialTabGroup;
import net.runelite.client.util.ImageUtil;

import javax.inject.Inject;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class GEProspectorPanel extends PluginPanel {
    private final MarketDataService marketDataService;
    private final FlipTracker flipTracker;
    private final GEProspectorConfig config;
    
    private final JPanel mainDisplay = new JPanel();
    private final JPanel activeFlipsPanel = new JPanel();
    private final JPanel searchPanel = new JPanel();
    private final MaterialTabGroup tabGroup = new MaterialTabGroup(mainDisplay);
    
    private final List<ItemPanel> itemPanels = new ArrayList<>();
    
    @Inject
    public GEProspectorPanel(
            MarketDataService marketDataService,
            FlipTracker flipTracker,
            GEProspectorConfig config) {
        super(false);
        
        this.marketDataService = marketDataService;
        this.flipTracker = flipTracker;
        this.config = config;
        
        setLayout(new BorderLayout());
        setBackground(ColorScheme.DARK_GRAY_COLOR);
        
        setupHeader();
        setupTabs();
        setupMainDisplay();
    }
    
    private void setupHeader() {
        JPanel header = new JPanel();
        header.setLayout(new BorderLayout());
        header.setBackground(ColorScheme.DARKER_GRAY_COLOR);
        header.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        // Title
        JLabel title = new JLabel("GE Prospector");
        title.setForeground(ColorScheme.LIGHT_GRAY_COLOR);
        title.setFont(FontManager.getRunescapeBoldFont());
        
        header.add(title, BorderLayout.WEST);
        add(header, BorderLayout.NORTH);
    }
    
    private void setupTabs() {
        MaterialTab activeFlipsTab = new MaterialTab("Active Flips", tabGroup, activeFlipsPanel);
        MaterialTab searchTab = new MaterialTab("Search", tabGroup, searchPanel);
        
        tabGroup.setBorder(new EmptyBorder(5, 0, 0, 0));
        tabGroup.addTab(activeFlipsTab);
        tabGroup.addTab(searchTab);
        tabGroup.select(activeFlipsTab); // Default to active flips
        
        add(tabGroup, BorderLayout.CENTER);
    }
    
    private void setupMainDisplay() {
        mainDisplay.setLayout(new BorderLayout());
        mainDisplay.setBackground(ColorScheme.DARK_GRAY_COLOR);
        
        setupActiveFlipsPanel();
        setupSearchPanel();
        
        add(mainDisplay, BorderLayout.SOUTH);
    }
    
    private void setupActiveFlipsPanel() {
        activeFlipsPanel.setLayout(new BoxLayout(activeFlipsPanel, BoxLayout.Y_AXIS));
        activeFlipsPanel.setBackground(ColorScheme.DARK_GRAY_COLOR);
        activeFlipsPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
    }
    
    private void setupSearchPanel() {
        searchPanel.setLayout(new BoxLayout(searchPanel, BoxLayout.Y_AXIS));
        searchPanel.setBackground(ColorScheme.DARK_GRAY_COLOR);
        searchPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        // Search input
        JTextField searchField = new JTextField();
        searchField.setBackground(ColorScheme.DARKER_GRAY_COLOR);
        searchField.setForeground(ColorScheme.LIGHT_GRAY_COLOR);
        searchField.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        searchPanel.add(searchField);
    }
    
    public void updateActiveFlips() {
        activeFlipsPanel.removeAll();
        
        for (FlipEntry flip : flipTracker.getActiveFlips().values()) {
            ItemPanel itemPanel = new ItemPanel(flip, marketDataService);
            itemPanels.add(itemPanel);
            activeFlipsPanel.add(itemPanel);
        }
        
        activeFlipsPanel.revalidate();
        activeFlipsPanel.repaint();
    }
    
    public void updateSearchResults(List<ItemPrice> items) {
        searchPanel.removeAll();
        
        for (ItemPrice item : items) {
            EstimatedTime timeEst = flipTracker.getEstimatedTime(item.getItemId(), 1);
            ItemPanel itemPanel = new ItemPanel(item, timeEst);
            itemPanels.add(itemPanel);
            searchPanel.add(itemPanel);
        }
        
        searchPanel.revalidate();
        searchPanel.repaint();
    }
}