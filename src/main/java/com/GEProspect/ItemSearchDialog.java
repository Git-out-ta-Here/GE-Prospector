package com.GEProspect;

import net.runelite.client.game.ItemManager;
import net.runelite.api.ItemComposition;
import net.runelite.client.callback.ClientThread;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ItemSearchDialog extends JDialog {
    private static final int MAX_RESULTS = 100;
    private final ItemManager itemManager;
    private final ClientThread clientThread;
    private final JTextField searchField;
    private final DefaultListModel<SearchResult> listModel;
    private final JList<SearchResult> resultList;
    private SearchResult selectedItem;
    
    public ItemSearchDialog(Window parent, ItemManager itemManager, ClientThread clientThread) {
        super(parent, "Search Item", ModalityType.APPLICATION_MODAL);
        this.itemManager = itemManager;
        this.clientThread = clientThread;
        
        setLayout(new BorderLayout());
        
        // Search field
        searchField = new JTextField(20);
        searchField.getDocument().addDocumentListener(new SearchListener());
        
        // Results list
        listModel = new DefaultListModel<>();
        resultList = new JList<>(listModel);
        resultList.setCellRenderer(new ItemListRenderer());
        resultList.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    accept();
                }
            }
        });
        
        // Buttons
        JButton okButton = new JButton("OK");
        okButton.addActionListener(e -> accept());
        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> dispose());
        
        // Layout
        JPanel searchPanel = new JPanel(new BorderLayout(5, 5));
        searchPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        searchPanel.add(new JLabel("Search:"), BorderLayout.WEST);
        searchPanel.add(searchField, BorderLayout.CENTER);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);
        
        add(searchPanel, BorderLayout.NORTH);
        add(new JScrollPane(resultList), BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
        
        setPreferredSize(new Dimension(300, 400));
        pack();
        setLocationRelativeTo(parent);
    }
    
    private void accept() {
        selectedItem = resultList.getSelectedValue();
        dispose();
    }
    
    public SearchResult showDialog() {
        selectedItem = null;
        setVisible(true);
        return selectedItem;
    }
    
    private void updateSearch() {
        String search = searchField.getText().toLowerCase();
        if (search.length() < 3) {
            listModel.clear();
            return;
        }

        CompletableFuture.runAsync(() -> {
            clientThread.invoke(() -> {
                listModel.clear();
                List<SearchResult> results = new ArrayList<>();
                
                // Use ItemManager search and properly check tradeable status
                List<ItemComposition> items = itemManager.search(search);
                for (ItemComposition item : items) {
                    if (item != null && item.isTradeable()) {
                        results.add(new SearchResult(item.getId(), item.getName()));
                        if (results.size() >= MAX_RESULTS) {
                            break;
                        }
                    }
                }
                
                SwingUtilities.invokeLater(() -> {
                    for (SearchResult result : results) {
                        listModel.addElement(result);
                    }
                });
            });
        });
    }
    
    private class SearchListener implements DocumentListener {
        public void insertUpdate(DocumentEvent e) { updateSearch(); }
        public void removeUpdate(DocumentEvent e) { updateSearch(); }
        public void changedUpdate(DocumentEvent e) { updateSearch(); }
    }
    
    private class ItemListRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, 
                int index, boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            SearchResult result = (SearchResult) value;
            setText(result.name);
            return this;
        }
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