package com.geprospect ;

import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.FontManager;
import net.runelite.client.util.ImageUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;

public class ItemContextMenu extends JPopupMenu {
    private final ItemPrice item;
    private final EstimatedTime timeEst;
    private final MarketDataService marketService;
    
    public ItemContextMenu(ItemPrice item, EstimatedTime timeEst, MarketDataService marketService) {
        this.item = item;
        this.timeEst = timeEst;
        this.marketService = marketService;
        
        setBackground(ColorScheme.DARKER_GRAY_COLOR);
        setBorder(BorderFactory.createLineBorder(ColorScheme.DARKER_GRAY_HOVER_COLOR));
        
        // Add to Watchlist
        JMenuItem watchlistItem = new JMenuItem("Add to Watchlist");
        watchlistItem.setForeground(Color.WHITE);
        watchlistItem.addActionListener(this::addToWatchlist);
        add(watchlistItem);
        
        // Set Price Alert
        JMenuItem alertItem = new JMenuItem("Set Price Alert");
        alertItem.setForeground(Color.WHITE);
        alertItem.addActionListener(this::setPriceAlert);
        add(alertItem);
        
        addSeparator();
        
        // Copy Buy Price
        JMenuItem copyBuyItem = new JMenuItem("Copy Buy Price");
        copyBuyItem.setForeground(Color.WHITE);
        copyBuyItem.addActionListener(e -> copyToClipboard(String.valueOf(item.getLowPrice())));
        add(copyBuyItem);
        
        // Copy Sell Price
        JMenuItem copySellItem = new JMenuItem("Copy Sell Price");
        copySellItem.setForeground(Color.WHITE);
        copySellItem.addActionListener(e -> copyToClipboard(String.valueOf(item.getHighPrice())));
        add(copySellItem);
        
        addSeparator();
        
        // View Trade History
        JMenuItem historyItem = new JMenuItem("View Trade History");
        historyItem.setForeground(Color.WHITE);
        historyItem.addActionListener(this::viewTradeHistory);
        add(historyItem);
        
        // Style all menu items
        for (Component c : getComponents()) {
            if (c instanceof JMenuItem) {
                JMenuItem menuItem = (JMenuItem) c;
                menuItem.setBackground(ColorScheme.DARKER_GRAY_COLOR);
                menuItem.setFont(FontManager.getRunescapeSmallFont());
                menuItem.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
                
                menuItem.addChangeListener(e -> {
                    if (menuItem.isArmed()) {
                        menuItem.setBackground(ColorScheme.DARKER_GRAY_HOVER_COLOR);
                    } else {
                        menuItem.setBackground(ColorScheme.DARKER_GRAY_COLOR);
                    }
                });
            }
        }
    }
    
    private void addToWatchlist(ActionEvent e) {
        // TODO: Implement watchlist functionality
        SwingUtilities.invokeLater(() -> {
            JOptionPane.showMessageDialog(
                this,
                "Added to watchlist",
                "GE Prospector",
                JOptionPane.INFORMATION_MESSAGE
            );
        });
    }
    
    private void setPriceAlert(ActionEvent e) {
        SwingUtilities.invokeLater(() -> {
            String input = JOptionPane.showInputDialog(
                this,
                "Enter target price:",
                "Set Price Alert",
                JOptionPane.PLAIN_MESSAGE
            );
            
            if (input != null && !input.trim().isEmpty()) {
                try {
                    int targetPrice = Integer.parseInt(input.trim());
                    // TODO: Implement price alert system
                    JOptionPane.showMessageDialog(
                        this,
                        "Price alert set for " + targetPrice + " gp",
                        "GE Prospector",
                        JOptionPane.INFORMATION_MESSAGE
                    );
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(
                        this,
                        "Please enter a valid number",
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                    );
                }
            }
        });
    }
    
    private void copyToClipboard(String text) {
        Toolkit.getDefaultToolkit()
            .getSystemClipboard()
            .setContents(new StringSelection(text), null);
    }
    
    private void viewTradeHistory(ActionEvent e) {
        // TODO: Implement trade history view
        SwingUtilities.invokeLater(() -> {
            JOptionPane.showMessageDialog(
                this,
                "Trade history feature coming soon!",
                "GE Prospector",
                JOptionPane.INFORMATION_MESSAGE
            );
        });
    }
}