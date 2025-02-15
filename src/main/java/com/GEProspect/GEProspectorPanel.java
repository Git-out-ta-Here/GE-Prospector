package com.geprospect ;

import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.PluginPanel;
import net.runelite.client.ui.components.materialtabs.MaterialTab;
import net.runelite.client.ui.components.materialtabs.MaterialTabGroup;
import net.runelite.client.game.ItemManager;
import net.runelite.client.callback.ClientThread;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class GEProspectorPanel extends PluginPanel {
    // Services
    private final MarketDataService marketDataService;
    private final FlipTracker flipTracker;
    private final WatchlistManager watchlistManager;
    private final GEProspectorConfig config;
    private final ItemManager itemManager;
    private final ClientThread clientThread;

    // UI Components
    private final JPanel mainDisplay;
    private final MaterialTabGroup tabGroup;
    private final JComboBox<SortOption> sortBox;
    private final List<ItemPanel> itemPanels = new ArrayList<>();

    // Tab Panels
    private final JPanel activeFlipsTab;
    private final JPanel searchTab;
    private final JPanel watchlistTab;

    // Current state
    private SortOption currentSort = SortOption.PROFIT_MARGIN;
    private static final int PANEL_BORDER_SIZE = 10;

    public GEProspectorPanel(
            MarketDataService marketDataService,
            FlipTracker flipTracker,
            WatchlistManager watchlistManager,
            GEProspectorConfig config,
            ItemManager itemManager,
            ClientThread clientThread) {
        super(false);
        
        // Initialize services
        this.marketDataService = marketDataService;
        this.flipTracker = flipTracker;
        this.watchlistManager = watchlistManager;
        this.config = config;
        this.itemManager = itemManager;
        this.clientThread = clientThread;

        // Initialize UI components
        this.mainDisplay = new JPanel();
        this.tabGroup = new MaterialTabGroup(mainDisplay);
        this.activeFlipsTab = createTabPanel();
        this.searchTab = createTabPanel();
        this.watchlistTab = createTabPanel();
        this.sortBox = createSortBox();

        // Setup UI
        setupLayout();
        setupTabs();
        
        // Initial data load
        updateAllTabs();
    }

    private JPanel createTabPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(ColorScheme.DARK_GRAY_COLOR);
        panel.setBorder(new EmptyBorder(PANEL_BORDER_SIZE, PANEL_BORDER_SIZE, PANEL_BORDER_SIZE, PANEL_BORDER_SIZE));
        return panel;
    }

    private JComboBox<SortOption> createSortBox() {
        JComboBox<SortOption> box = new JComboBox<>(SortOption.values());
        box.setForeground(Color.WHITE);
        box.setBackground(ColorScheme.DARKER_GRAY_COLOR);
        box.setFocusable(false);
        box.setRenderer(new SortOptionRenderer());
        box.addActionListener(e -> {
            currentSort = (SortOption) box.getSelectedItem();
            updateAllTabs();
        });
        return box;
    }

    private void setupLayout() {
        setLayout(new BorderLayout());
        setBackground(ColorScheme.DARK_GRAY_COLOR);

        // Top panel with sort box
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(ColorScheme.DARKER_GRAY_COLOR);
        topPanel.setBorder(new EmptyBorder(PANEL_BORDER_SIZE, PANEL_BORDER_SIZE, 0, PANEL_BORDER_SIZE));
        topPanel.add(sortBox, BorderLayout.EAST);
        
        add(topPanel, BorderLayout.NORTH);
        add(mainDisplay, BorderLayout.CENTER);
    }

    private void setupTabs() {
        MaterialTab activeFlipsTab = new MaterialTab("Active Flips", tabGroup, this.activeFlipsTab);
        MaterialTab searchTab = new MaterialTab("Search", tabGroup, this.searchTab);
        MaterialTab watchlistTab = new MaterialTab("Watchlist", tabGroup, this.watchlistTab);

        tabGroup.addTab(activeFlipsTab);
        tabGroup.addTab(searchTab);
        tabGroup.addTab(watchlistTab);
        tabGroup.select(activeFlipsTab);

        // Setup search functionality
        setupSearchTab();
        setupWatchlistTab();

        // Add tabs to main panel
        JPanel tabPanel = new JPanel(new BorderLayout());
        tabPanel.setBackground(ColorScheme.DARKER_GRAY_COLOR);
        tabPanel.add(tabGroup, BorderLayout.NORTH);
        tabPanel.add(mainDisplay, BorderLayout.CENTER);

        add(tabPanel, BorderLayout.CENTER);
    }

    private void setupSearchTab() {
        JTextField searchField = new JTextField();
        searchField.setBackground(ColorScheme.DARKER_GRAY_COLOR);
        searchField.setForeground(Color.WHITE);
        searchField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ColorScheme.DARKER_GRAY_HOVER_COLOR),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        
        searchField.addActionListener(e -> updateSearchResults(searchField.getText()));
        
        JPanel searchPanel = new JPanel(new BorderLayout());
        searchPanel.setBackground(ColorScheme.DARK_GRAY_COLOR);
        searchPanel.setBorder(new EmptyBorder(0, 0, PANEL_BORDER_SIZE, 0));
        searchPanel.add(searchField, BorderLayout.CENTER);
        
        searchTab.add(searchPanel, BorderLayout.NORTH);
    }

    private void setupWatchlistTab() {
        JButton addButton = new JButton("Add Item");
        addButton.addActionListener(e -> showAddWatchlistDialog());

        JPanel buttonPanel = new JPanel(new BorderLayout());
        buttonPanel.setBackground(ColorScheme.DARK_GRAY_COLOR);
        buttonPanel.setBorder(new EmptyBorder(0, 0, PANEL_BORDER_SIZE, 0));
        buttonPanel.add(addButton, BorderLayout.EAST);

        watchlistTab.add(buttonPanel, BorderLayout.NORTH);
    }

    public void updateAllTabs() {
        updateActiveFlips();
        updateSearchResults("");
        updateWatchlist();
    }

    public void updateActiveFlips() {
        activeFlipsTab.removeAll();
        
        List<FlipEntry> activeFlips = flipTracker.getActiveFlips();
        for (FlipEntry flip : activeFlips) {
            ItemPrice price = marketDataService.getPrice(flip.getItemId());
            if (price != null) {
                ItemPanel panel = new ItemPanel(
                    price,
                    flipTracker.getEstimatedTime(flip.getItemId(), flip.getQuantity()),
                    marketDataService
                );
                activeFlipsTab.add(panel);
            }
        }
        
        activeFlipsTab.revalidate();
        activeFlipsTab.repaint();
    }

    private void updateSearchResults(String searchTerm) {
        searchTab.removeAll();

        List<ItemPrice> items = marketDataService.getAllPrices().stream()
            .filter(item -> item.getProfitMargin() >= config.minimumProfit())
            .filter(item -> searchTerm.isEmpty() || 
                (item.getName() != null && item.getName().toLowerCase().contains(searchTerm.toLowerCase())))
            .sorted(currentSort.getComparator())
            .collect(Collectors.toList());

        for (ItemPrice item : items) {
            ItemPanel itemPanel = new ItemPanel(
                item,
                flipTracker.getEstimatedTime(item.getItemId(), 1),
                marketDataService
            );
            searchTab.add(itemPanel);
        }

        searchTab.revalidate();
        searchTab.repaint();
    }

    private void updateWatchlist() {
        watchlistTab.removeAll();
        
        // Re-add the "Add Item" button
        JButton addButton = new JButton("Add Item");
        addButton.addActionListener(e -> showAddWatchlistDialog());
        watchlistTab.add(addButton);

        // Add watched items
        for (WatchlistManager.WatchedItem item : watchlistManager.getWatchedItems()) {
            ItemPrice price = marketDataService.getPrice(item.getItemId());
            if (price != null) {
                ItemPanel panel = new ItemPanel(
                    price,
                    flipTracker.getEstimatedTime(item.getItemId(), 1),
                    marketDataService
                );
                panel.addPriceAlert(item.getTargetPrice(), item.isAlertOnAbove());
                watchlistTab.add(panel);
            }
        }

        watchlistTab.revalidate();
        watchlistTab.repaint();
    }

    private void showAddWatchlistDialog() {
        ItemSearchDialog dialog = new ItemSearchDialog(
            SwingUtilities.getWindowAncestor(this),
            itemManager,
            clientThread
        );

        ItemSearchDialog.SearchResult result = dialog.showDialog();
        if (result != null) {
            showPriceAlertDialog(result);
        }
    }

    private void showPriceAlertDialog(ItemSearchDialog.SearchResult result) {
        JTextField priceField = new JTextField(10);
        JComboBox<String> conditionBox = new JComboBox<>(new String[]{"Price above", "Price below"});

        JPanel panel = new JPanel(new GridLayout(0, 1));
        panel.add(new JLabel("Item: " + result.name));
        panel.add(new JLabel("Target price:"));
        panel.add(priceField);
        panel.add(new JLabel("Alert when:"));
        panel.add(conditionBox);

        int dialogResult = JOptionPane.showConfirmDialog(
            this,
            panel,
            "Set Price Alert",
            JOptionPane.OK_CANCEL_OPTION
        );

        if (dialogResult == JOptionPane.OK_OPTION) {
            try {
                int targetPrice = Integer.parseInt(priceField.getText());
                boolean alertOnAbove = conditionBox.getSelectedIndex() == 0;
                watchlistManager.addToWatchlist(result.id, result.name, targetPrice, alertOnAbove);
                updateWatchlist();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(
                    this,
                    "Please enter a valid price",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
                );
            }
        }
    }

    private static class SortOptionRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(
                JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            setForeground(Color.WHITE);
            setBackground(isSelected ? ColorScheme.DARKER_GRAY_HOVER_COLOR : ColorScheme.DARKER_GRAY_COLOR);
            setText(((SortOption) value).getDisplayName());
            return this;
        }
    }
}