package com.GEProspect;

import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.PluginPanel;
import net.runelite.client.ui.components.materialtabs.MaterialTab;
import net.runelite.client.ui.components.materialtabs.MaterialTabGroup;
import net.runelite.client.util.ImageUtil;
import net.runelite.client.game.ItemManager;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.ui.FontManager;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class GEProspectorPanel extends PluginPanel {
    private final MarketDataService marketDataService;
    private final FlipTracker flipTracker;
    private final WatchlistManager watchlistManager;
    private final GEProspectorConfig config;
    private final ItemManager itemManager;
    private final ClientThread clientThread;
    
    private final JPanel mainDisplay = new JPanel();
    private final JPanel activeFlipsPanel = new JPanel();
    private final JPanel searchPanel = new JPanel();
    private final JPanel watchlistPanel = new JPanel();
    private final JPanel profitDashboardPanel;
    private final MaterialTabGroup tabGroup = new MaterialTabGroup(mainDisplay);
    private final JComboBox<SortOption> sortBox;
    
    private final List<ItemPanel> itemPanels = new ArrayList<>();
    private SortOption currentSort = SortOption.PROFIT_MARGIN;

    @Inject
    public GEProspectorPanel(
            MarketDataService marketDataService,
            FlipTracker flipTracker,
            WatchlistManager watchlistManager,
            GEProspectorConfig config,
            ItemManager itemManager,
            ClientThread clientThread) {
        super(false);
        
        this.marketDataService = marketDataService;
        this.flipTracker = flipTracker;
        this.watchlistManager = watchlistManager;
        this.config = config;
        this.itemManager = itemManager;
        this.clientThread = clientThread;
        
        setLayout(new BorderLayout());
        setBackground(ColorScheme.DARK_GRAY_COLOR);
        
        // Initialize sort box
        sortBox = new JComboBox<>(SortOption.values());
        sortBox.setForeground(Color.WHITE);
        sortBox.setBackground(ColorScheme.DARKER_GRAY_COLOR);
        sortBox.setFocusable(false);
        sortBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, 
                    int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                setForeground(Color.WHITE);
                setBackground(isSelected ? ColorScheme.DARKER_GRAY_HOVER_COLOR : ColorScheme.DARKER_GRAY_COLOR);
                setText(((SortOption) value).getDisplayName());
                return this;
            }
        });
        sortBox.addActionListener(e -> {
            currentSort = (SortOption) sortBox.getSelectedItem();
            updateSort();
            refreshPanels();
        });

        // Initialize tab panels
        setupMainDisplay();
        setupTabs();
        
        // Add components
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(ColorScheme.DARKER_GRAY_COLOR);
        topPanel.setBorder(new EmptyBorder(10, 10, 0, 10));
        topPanel.add(sortBox, BorderLayout.EAST);
        
        add(topPanel, BorderLayout.NORTH);
        add(mainDisplay, BorderLayout.CENTER);
        
        // Initial panel updates
        updateActiveFlips();
        updateSearchResults();
        updateWatchlist();
    }
    
    private void setupMainDisplay() {
        mainDisplay.setLayout(new BorderLayout());
        mainDisplay.setBackground(ColorScheme.DARK_GRAY_COLOR);
        
        setupActiveFlipsPanel();
        setupSearchPanel();
        setupWatchlistPanel();
    }
    
    private void setupTabs() {
        MaterialTab activeFlipsTab = new MaterialTab("Active Flips", tabGroup, activeFlipsPanel);
        MaterialTab searchTab = new MaterialTab("Search", tabGroup, searchPanel);
        MaterialTab watchlistTab = new MaterialTab("Watchlist", tabGroup, watchlistPanel);
        
        tabGroup.addTab(activeFlipsTab);
        tabGroup.addTab(searchTab);
        tabGroup.addTab(watchlistTab);
        tabGroup.select(activeFlipsTab);
        
        JPanel tabPanel = new JPanel(new BorderLayout());
        tabPanel.setBackground(ColorScheme.DARKER_GRAY_COLOR);
        tabPanel.add(tabGroup, BorderLayout.NORTH);
        tabPanel.add(mainDisplay, BorderLayout.CENTER);
        
        add(tabPanel, BorderLayout.CENTER);
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
    
    private void setupWatchlistPanel() {
        watchlistPanel.setLayout(new BoxLayout(watchlistPanel, BoxLayout.Y_AXIS));
        watchlistPanel.setBackground(ColorScheme.DARK_GRAY_COLOR);
        
        // Add "Add Item" button
        JButton addButton = new JButton("Add Item");
        addButton.addActionListener(e -> showAddWatchlistDialog());
        
        JPanel buttonPanel = new JPanel(new BorderLayout());
        buttonPanel.setBackground(ColorScheme.DARK_GRAY_COLOR);
        buttonPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        buttonPanel.add(addButton, BorderLayout.EAST);
        
        watchlistPanel.add(buttonPanel);
        updateWatchlist();
    }
    
    private boolean isOnActiveFlipsTab() {
        for (Component c : tabGroup.getComponents()) {
            if (c instanceof MaterialTab) {
                MaterialTab tab = (MaterialTab) c;
                if (tab.isSelected() && tab.getText().equals("Active Flips")) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isOnSearchTab() {
        for (Component c : tabGroup.getComponents()) {
            if (c instanceof MaterialTab) {
                MaterialTab tab = (MaterialTab) c;
                if (tab.isSelected() && tab.getText().equals("Search")) {
                    return true;
                }
            }
        }
        return false;
    }

    private void updateSort() {
        itemPanels.sort((a, b) -> {
            ItemPrice itemA = a.getItemPrice();
            ItemPrice itemB = b.getItemPrice();
            EstimatedTime timeA = flipTracker.getEstimatedTime(itemA.getItemId(), 1);
            EstimatedTime timeB = flipTracker.getEstimatedTime(itemB.getItemId(), 1);
            
            return currentSort.getComparator().compare(itemA, itemB);
        });

        refreshPanels();
    }

    private void refreshPanels() {
        if (isOnActiveFlipsTab()) {
            updateActiveFlips();
        } else if (isOnSearchTab()) {
            updateSearchResults();
        } else {
            updateWatchlist();
        }
    }

    public void updateActiveFlips() {
        List<FlipEntry> activeFlips = flipTracker.getActiveFlips();
        
        activeFlipsPanel.removeAll();
        for (FlipEntry flip : activeFlips) {
            ItemPrice price = marketDataService.getPrice(flip.getItemId());
            if (price != null) {
                ItemPanel panel = new ItemPanel(price, flipTracker.getEstimatedTime(flip.getItemId(), flip.getQuantity()), marketDataService);
                activeFlipsPanel.add(panel);
            }
        }
        
        activeFlipsPanel.revalidate();
        activeFlipsPanel.repaint();
    }
    
    public void updateSearchResults() {
        searchPanel.removeAll();
        
        // Get all items with profit margins above minimum
        List<ItemPrice> items = marketDataService.getAllPrices().stream()
            .filter(item -> item.getProfitMargin() >= config.minimumProfit())
            .collect(Collectors.toList());
        
        // Sort items
        updateSort();
        
        // Create panels for each item
        for (ItemPrice item : items) {
            ItemPanel itemPanel = new ItemPanel(
                item,
                flipTracker.getEstimatedTime(item.getItemId(), 1),
                marketDataService
            );
            searchPanel.add(itemPanel);
        }
        
        searchPanel.revalidate();
        searchPanel.repaint();
    }

    public void updateWatchlist() {
        watchlistPanel.removeAll();
        
        // Add "Add Item" button first
        JButton addButton = new JButton("Add Item");
        addButton.addActionListener(e -> showAddWatchlistDialog());
        watchlistPanel.add(addButton);
        
        // Add watched items
        for (WatchlistManager.WatchedItem item : watchlistManager.getWatchedItems()) {
            ItemPrice price = marketDataService.getPrice(item.getItemId());
            if (price != null) {
                ItemPanel panel = new ItemPanel(price, flipTracker.getEstimatedTime(item.getItemId(), 1), marketDataService);
                panel.addPriceAlert(item.getTargetPrice(), item.isAlertOnAbove());
                watchlistPanel.add(panel);
            }
        }
        
        watchlistPanel.revalidate();
        watchlistPanel.repaint();
    }
    
    private void showAddWatchlistDialog() {
        ItemSearchDialog dialog = new ItemSearchDialog(
            SwingUtilities.getWindowAncestor(this),
            itemManager,
            clientThread
        );
        
        ItemSearchDialog.SearchResult result = dialog.showDialog();
        if (result != null) {
            JTextField priceField = new JTextField(10);
            JComboBox<String> conditionBox = new JComboBox<>(new String[]{"Price above", "Price below"});
            
            JPanel panel = new JPanel(new GridLayout(0, 1));
            panel.add(new JLabel("Item: " + result.name));
            panel.add(new JLabel("Target price:"));
            panel.add(priceField);
            panel.add(new JLabel("Alert when:"));
            panel.add(conditionBox);
            
            int dialogResult = JOptionPane.showConfirmDialog(this, panel, 
                "Set Price Alert", JOptionPane.OK_CANCEL_OPTION);
                
            if (dialogResult == JOptionPane.OK_OPTION) {
                try {
                    int targetPrice = Integer.parseInt(priceField.getText());
                    boolean alertOnAbove = conditionBox.getSelectedIndex() == 0;
                    
                    watchlistManager.addToWatchlist(result.id, result.name, targetPrice, alertOnAbove);
                    updateWatchlist();
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this,
                        "Please enter a valid price",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }
}