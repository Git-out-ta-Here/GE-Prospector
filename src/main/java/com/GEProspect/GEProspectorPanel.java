package com.GEProspect;

import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.PluginPanel;
import net.runelite.client.ui.components.ComboBoxListRenderer;
import net.runelite.client.ui.components.materialtabs.MaterialTab;
import net.runelite.client.ui.components.materialtabs.MaterialTabGroup;
import net.runelite.client.util.ImageUtil;

import javax.inject.Inject;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class GEProspectorPanel extends PluginPanel {
    private final MarketDataService marketDataService;
    private final FlipTracker flipTracker;
    private final GEProspectorConfig config;
    
    private final JPanel mainDisplay = new JPanel();
    private final JPanel activeFlipsPanel = new JPanel();
    private final JPanel searchPanel = new JPanel();
    private final MaterialTabGroup tabGroup = new MaterialTabGroup(mainDisplay);
    private final JComboBox<SortOption> sortBox;
    
    private final List<ItemPanel> itemPanels = new ArrayList<>();
    private SortOption currentSort = SortOption.PROFIT_MARGIN;
    
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
        
        // Initialize sort box
        sortBox = new JComboBox<>(SortOption.values());
        sortBox.setForeground(Color.WHITE);
        sortBox.setBackground(ColorScheme.DARKER_GRAY_COLOR);
        sortBox.setRenderer(new ComboBoxListRenderer<>());
        sortBox.setFocusable(false);
        sortBox.addActionListener(e -> {
            currentSort = (SortOption) sortBox.getSelectedItem();
            updateSort();
        });
        
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
        
        // Sort controls
        JPanel controls = new JPanel(new BorderLayout(5, 0));
        controls.setBackground(ColorScheme.DARKER_GRAY_COLOR);
        controls.add(new JLabel("Sort by:"), BorderLayout.WEST);
        controls.add(sortBox, BorderLayout.CENTER);
        
        header.add(title, BorderLayout.WEST);
        header.add(controls, BorderLayout.EAST);
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
    
    private void updateSort() {
        List<Component> components = new ArrayList<>();
        JPanel currentPanel = getCurrentPanel();
        
        // Store all components
        for (Component comp : currentPanel.getComponents()) {
            if (comp instanceof ItemPanel) {
                components.add(comp);
            }
        }
        
        // Sort components
        components.sort((c1, c2) -> {
            ItemPanel panel1 = (ItemPanel) c1;
            ItemPanel panel2 = (ItemPanel) c2;
            return currentSort.getComparator().compare(
                panel1.getItemPrice(),
                panel2.getItemPrice(),
                panel1.getTimeEstimate(),
                panel2.getTimeEstimate()
            );
        });
        
        // Remove all components and add back in sorted order
        currentPanel.removeAll();
        components.forEach(currentPanel::add);
        
        // Refresh display
        currentPanel.revalidate();
        currentPanel.repaint();
    }
    
    private JPanel getCurrentPanel() {
        return tabGroup.getSelectedTab().getContent() == activeFlipsPanel 
            ? activeFlipsPanel 
            : searchPanel;
    }
    
    public void updateActiveFlips() {
        activeFlipsPanel.removeAll();
        
        for (FlipEntry flip : flipTracker.getActiveFlips().values()) {
            ItemPanel itemPanel = new ItemPanel(flip, marketDataService);
            itemPanels.add(itemPanel);
            activeFlipsPanel.add(itemPanel);
        }
        
        updateSort();
        activeFlipsPanel.revalidate();
        activeFlipsPanel.repaint();
    }
    
    public void updateSearchResults(List<ItemPrice> items) {
        searchPanel.removeAll();
        
        for (ItemPrice item : items) {
            EstimatedTime timeEst = flipTracker.getEstimatedTime(item.getItemId(), 1);
            ItemPanel itemPanel = new ItemPanel(item, timeEst, marketDataService);
            itemPanels.add(itemPanel);
            searchPanel.add(itemPanel);
        }
        
        updateSort();
        searchPanel.revalidate();
        searchPanel.repaint();
    }
}