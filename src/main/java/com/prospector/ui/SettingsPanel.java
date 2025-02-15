package com.prospector.ui;

import lombok.extern.slf4j.Slf4j;
import net.runelite.client.ui.PluginPanel;
import net.runelite.client.ui.components.IconTextField;
import net.runelite.client.game.ItemManager;
import net.runelite.client.callback.ClientThread;
import com.prospector.ProspectorConfig;
import com.prospector.service.FlipAnalysisService;
import com.prospector.util.ProspectorUtil;
import com.prospector.util.ItemSearchUtil;
import com.prospector.util.ItemSearchUtil.SearchResult;

import javax.inject.Inject;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.List;

@Slf4j
public class SettingsPanel extends JPanel {
    private final ProspectorConfig config;
    private final FlipAnalysisService flipAnalysisService;
    private final ItemManager itemManager;
    private final ClientThread clientThread;
    private final ProspectorUtil prospectorUtil;
    private final ItemSearchUtil itemSearchUtil;
    
    private final JPanel trackedItemsPanel;
    private final DefaultListModel<TrackedItem> trackedItemsModel;
    private final JList<TrackedItem> trackedItemsList;
    private JDialog searchDialog;
    private JList<SearchResult> searchResultsList;
    private DefaultListModel<SearchResult> searchResultsModel;
    private IconTextField searchField;

    @Inject
    public SettingsPanel(
            ProspectorConfig config,
            FlipAnalysisService flipAnalysisService,
            ItemManager itemManager,
            ClientThread clientThread,
            ProspectorUtil prospectorUtil,
            ItemSearchUtil itemSearchUtil) {
        this.config = config;
        this.flipAnalysisService = flipAnalysisService;
        this.itemManager = itemManager;
        this.clientThread = clientThread;
        this.prospectorUtil = prospectorUtil;
        this.itemSearchUtil = itemSearchUtil;

        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(10, 10, 10, 10));

        // Create tracked items panel
        trackedItemsPanel = new JPanel(new BorderLayout());
        trackedItemsPanel.setBorder(BorderFactory.createTitledBorder("Tracked Items"));

        // Initialize list model and list
        trackedItemsModel = new DefaultListModel<>();
        trackedItemsList = new JList<>(trackedItemsModel);
        trackedItemsList.setCellRenderer(new TrackedItemRenderer());
        trackedItemsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Add scroll pane for tracked items
        JScrollPane scrollPane = new JScrollPane(trackedItemsList);
        trackedItemsPanel.add(scrollPane, BorderLayout.CENTER);

        // Create buttons panel
        JPanel buttonsPanel = new JPanel(new GridLayout(1, 2, 5, 0));
        JButton addButton = new JButton("Add Item");
        JButton removeButton = new JButton("Remove");

        addButton.addActionListener(e -> showAddItemDialog());
        removeButton.addActionListener(e -> removeSelectedItem());

        buttonsPanel.add(addButton);
        buttonsPanel.add(removeButton);
        trackedItemsPanel.add(buttonsPanel, BorderLayout.SOUTH);

        // Add to main panel
        add(trackedItemsPanel, BorderLayout.CENTER);

        // Load tracked items
        loadTrackedItems();
        
        // Initialize search dialog
        initSearchDialog();
    }

    private void loadTrackedItems() {
        trackedItemsModel.clear();
        flipAnalysisService.getTrackedItems().forEach(itemId -> {
            clientThread.invoke(() -> {
                String itemName = itemManager.getItemComposition(itemId).getName();
                trackedItemsModel.addElement(new TrackedItem(itemId, itemName));
            });
        });
    }

    private void initSearchDialog() {
        searchDialog = new JDialog();
        searchDialog.setTitle("Search Items");
        searchDialog.setModal(true);
        searchDialog.setLayout(new BorderLayout());

        // Search field
        searchField = new IconTextField();
        searchField.setIcon(IconTextField.Icon.SEARCH);
        searchField.setPreferredSize(new Dimension(200, 30));
        searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                updateSearchResults();
            }
        });

        // Search results
        searchResultsModel = new DefaultListModel<>();
        searchResultsList = new JList<>(searchResultsModel);
        searchResultsList.setVisibleRowCount(8);
        searchResultsList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                SearchResult selected = searchResultsList.getSelectedValue();
                if (selected != null) {
                    addTrackedItem(selected);
                    searchDialog.setVisible(false);
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(searchResultsList);
        
        // Layout
        JPanel content = new JPanel(new BorderLayout(5, 5));
        content.setBorder(new EmptyBorder(10, 10, 10, 10));
        content.add(searchField, BorderLayout.NORTH);
        content.add(scrollPane, BorderLayout.CENTER);

        searchDialog.add(content);
        searchDialog.pack();
        searchDialog.setLocationRelativeTo(this);
    }

    private void updateSearchResults() {
        String query = searchField.getText();
        if (query.length() < 2) {
            searchResultsModel.clear();
            return;
        }

        List<SearchResult> results = itemSearchUtil.searchItems(query);
        searchResultsModel.clear();
        results.forEach(searchResultsModel::addElement);
    }

    private void addTrackedItem(SearchResult result) {
        if (!flipAnalysisService.isItemTracked(result.id)) {
            flipAnalysisService.trackItem(result.id);
            trackedItemsModel.addElement(new TrackedItem(result.id, result.name));
            prospectorUtil.sendNotification("Now tracking " + result.name);
        }
    }

    private void showAddItemDialog() {
        searchField.setText("");
        searchResultsModel.clear();
        searchDialog.setVisible(true);
    }

    private void removeSelectedItem() {
        TrackedItem selected = trackedItemsList.getSelectedValue();
        if (selected != null) {
            flipAnalysisService.untrackItem(selected.itemId);
            trackedItemsModel.removeElement(selected);
            prospectorUtil.sendNotification("Stopped tracking " + selected.itemName);
        }
    }

    private static class TrackedItem {
        final int itemId;
        final String itemName;

        TrackedItem(int itemId, String itemName) {
            this.itemId = itemId;
            this.itemName = itemName;
        }

        @Override
        public String toString() {
            return itemName;
        }
    }

    private static class TrackedItemRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(
                JList<?> list,
                Object value,
                int index,
                boolean isSelected,
                boolean cellHasFocus) {
            
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            
            if (value instanceof TrackedItem) {
                TrackedItem item = (TrackedItem) value;
                setText(item.itemName);
            }
            
            return this;
        }
    }
}