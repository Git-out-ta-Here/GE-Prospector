package com.GEProspect;

import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.FontManager;
import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.*;

public class ItemPanel extends JPanel {
    private final ItemPrice itemPrice;
    private final EstimatedTime timeEst;
    private final MarketDataService marketDataService;
    private JLabel alertLabel;
    private Integer targetPrice;
    private boolean alertOnAbove;

    public ItemPanel(ItemPrice itemPrice, EstimatedTime timeEst, MarketDataService marketDataService) {
        this.itemPrice = itemPrice;
        this.timeEst = timeEst;
        this.marketDataService = marketDataService;
        
        setLayout(new BorderLayout());
        setBackground(ColorScheme.DARKER_GRAY_COLOR);
        setBorder(new CompoundBorder(
            new EmptyBorder(5, 5, 5, 5),
            new LineBorder(ColorScheme.DARKER_GRAY_HOVER_COLOR)
        ));

        setupComponents();
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    showContextMenu(e);
                }
            }
        });
    }

    private void setupComponents() {
        // Main info panel (left side)
        JPanel infoPanel = new JPanel(new GridLayout(2, 1, 0, 2));
        infoPanel.setOpaque(false);

        // Item name
        JLabel nameLabel = new JLabel(itemPrice.getName());
        nameLabel.setForeground(Color.WHITE);
        nameLabel.setFont(FontManager.getRunescapeSmallFont());
        infoPanel.add(nameLabel);

        // Profit info
        JLabel profitLabel = new JLabel(String.format("%,d gp", itemPrice.getProfitMargin()));
        profitLabel.setForeground(getProfitColor(itemPrice.getProfitMargin()));
        profitLabel.setFont(FontManager.getRunescapeSmallFont());
        infoPanel.add(profitLabel);

        // Price panel (center)
        JPanel pricePanel = new JPanel(new GridLayout(2, 1, 0, 2));
        pricePanel.setOpaque(false);

        // Buy price
        JLabel buyLabel = new JLabel(String.format("Buy: %,d", itemPrice.getLowPrice()));
        buyLabel.setForeground(ColorScheme.GRAND_EXCHANGE_PRICE);
        buyLabel.setFont(FontManager.getRunescapeSmallFont());
        pricePanel.add(buyLabel);

        // Sell price
        JLabel sellLabel = new JLabel(String.format("Sell: %,d", itemPrice.getHighPrice()));
        sellLabel.setForeground(ColorScheme.GRAND_EXCHANGE_PRICE);
        sellLabel.setFont(FontManager.getRunescapeSmallFont());
        pricePanel.add(sellLabel);

        // Time estimate panel (right side)
        JPanel timePanel = new JPanel(new GridLayout(2, 1, 0, 2));
        timePanel.setOpaque(false);

        // Volume indicator
        JLabel volumeLabel = new JLabel(timeEst.getVolumeCategory().getIcon());
        volumeLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        timePanel.add(volumeLabel);

        // Time estimate
        JLabel timeLabel = new JLabel(String.format("%d min", timeEst.getMinutesToComplete()));
        timeLabel.setForeground(ColorScheme.LIGHT_GRAY_COLOR);
        timeLabel.setFont(FontManager.getRunescapeSmallFont());
        timeLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        timePanel.add(timeLabel);

        // Add alert label if there's a price target
        alertLabel = new JLabel();
        alertLabel.setForeground(ColorScheme.PROGRESS_ERROR_COLOR);
        alertLabel.setFont(FontManager.getRunescapeSmallFont());
        alertLabel.setVisible(false);
        
        // Layout
        add(infoPanel, BorderLayout.WEST);
        add(pricePanel, BorderLayout.CENTER);
        add(timePanel, BorderLayout.EAST);
        add(alertLabel, BorderLayout.SOUTH);
    }

    private Color getProfitColor(int profit) {
        if (profit > 10000) return ColorScheme.PROGRESS_COMPLETE_COLOR;
        if (profit > 5000) return ColorScheme.PROGRESS_INPROGRESS_COLOR;
        if (profit > 0) return ColorScheme.LIGHT_GRAY_COLOR;
        return ColorScheme.PROGRESS_ERROR_COLOR;
    }

    public void addPriceAlert(int price, boolean above) {
        this.targetPrice = price;
        this.alertOnAbove = above;
        updateAlertLabel();
    }

    public void removePriceAlert() {
        this.targetPrice = null;
        alertLabel.setVisible(false);
    }

    private void updateAlertLabel() {
        if (targetPrice != null) {
            String direction = alertOnAbove ? "▲" : "▼";
            alertLabel.setText(direction + " " + targetPrice);
            alertLabel.setVisible(true);
            
            // Set color based on how close we are to target
            int currentPrice = alertOnAbove ? itemPrice.getHighPrice() : itemPrice.getLowPrice();
            double ratio = (double) currentPrice / targetPrice;
            if (alertOnAbove ? ratio >= 1 : ratio <= 1) {
                alertLabel.setForeground(ColorScheme.PROGRESS_COMPLETE_COLOR);
            } else if (Math.abs(1 - ratio) < 0.05) {
                alertLabel.setForeground(ColorScheme.PROGRESS_INPROGRESS_COLOR);
            } else {
                alertLabel.setForeground(ColorScheme.LIGHT_GRAY_COLOR);
            }
        }
    }

    private void showContextMenu(MouseEvent e) {
        JPopupMenu contextMenu = new ItemContextMenu(itemPrice, timeEst, marketDataService);
        contextMenu.show(e.getComponent(), e.getX(), e.getY());
    }

    public ItemPrice getItemPrice() {
        return itemPrice;
    }
}