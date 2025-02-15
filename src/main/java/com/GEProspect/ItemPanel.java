package com.geprospect;

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
    private final PriceChartPanel priceChart;
    private JLabel alertLabel;
    private Integer targetPrice;
    private boolean alertOnAbove;
    private boolean isExpanded = false;
    private final JPanel expandedContent;

    public ItemPanel(ItemPrice itemPrice, EstimatedTime timeEst, MarketDataService marketDataService) {
        this.itemPrice = itemPrice;
        this.timeEst = timeEst;
        this.marketDataService = marketDataService;
        this.priceChart = new PriceChartPanel();
        this.expandedContent = new JPanel(new BorderLayout());
        
        setLayout(new BorderLayout());
        setBackground(ColorScheme.DARKER_GRAY_COLOR);
        setBorder(new CompoundBorder(
            new EmptyBorder(5, 5, 5, 5),
            new LineBorder(ColorScheme.DARKER_GRAY_HOVER_COLOR)
        ));

        setupComponents();
        setupExpandedContent();
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    showContextMenu(e);
                } else if (SwingUtilities.isLeftMouseButton(e)) {
                    toggleExpanded();
                }
            }
        });

        // Initial price update
        updatePriceChart();
    }

    private void setupComponents() {
        JPanel mainContent = new JPanel(new BorderLayout(5, 0));
        mainContent.setBackground(ColorScheme.DARKER_GRAY_COLOR);

        JLabel nameLabel = new JLabel(itemPrice.getName() != null ? itemPrice.getName() : String.valueOf(itemPrice.getItemId()));
        nameLabel.setForeground(Color.WHITE);
        
        JLabel priceLabel = new JLabel(itemPrice.getHighPrice());
        priceLabel.setForeground(getProfitColor(itemPrice.getProfitMargin()));
        
        alertLabel = new JLabel();
        updateAlertLabel();

        mainContent.add(nameLabel, BorderLayout.WEST);
        mainContent.add(priceLabel, BorderLayout.CENTER);
        mainContent.add(alertLabel, BorderLayout.EAST);

        add(mainContent, BorderLayout.NORTH);
    }

    private void setupExpandedContent() {
        expandedContent.setBackground(ColorScheme.DARKER_GRAY_COLOR);
        expandedContent.setBorder(new EmptyBorder(5, 0, 0, 0));
        expandedContent.add(priceChart, BorderLayout.CENTER);
        expandedContent.setVisible(false);
        add(expandedContent, BorderLayout.CENTER);
    }

    private void toggleExpanded() {
        isExpanded = !isExpanded;
        expandedContent.setVisible(isExpanded);
        if (isExpanded) {
            updatePriceChart();
        }
        revalidate();
        repaint();
    }

    private void updatePriceChart() {
        priceChart.clearData();
        priceChart.addPrice(itemPrice.getLowPriceAsInt(), itemPrice.getHighPriceAsInt());
        // TODO: Add historical price data when available
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
        if (targetPrice == null) {
            alertLabel.setVisible(false);
            return;
        }

        int currentPrice = alertOnAbove ? itemPrice.getHighPriceAsInt() : itemPrice.getLowPriceAsInt();
        boolean triggered = alertOnAbove ? currentPrice >= targetPrice : currentPrice <= targetPrice;
        
        alertLabel.setText(triggered ? "⚠" : "⏰");
        alertLabel.setForeground(triggered ? Color.RED : Color.YELLOW);
        alertLabel.setVisible(true);
    }

    private void showContextMenu(MouseEvent e) {
        JPopupMenu contextMenu = new ItemContextMenu(itemPrice, timeEst, marketDataService);
        contextMenu.show(e.getComponent(), e.getX(), e.getY());
    }

    public ItemPrice getItemPrice() {
        return itemPrice;
    }
}