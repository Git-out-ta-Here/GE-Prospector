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
        setLayout(new BorderLayout(5, 0));
        setBorder(new CompoundBorder(
            new EmptyBorder(5, 5, 5, 5),
            new LineBorder(ColorScheme.DARKER_GRAY_HOVER_COLOR)
        ));

        JLabel nameLabel = new JLabel(itemPrice.getName() != null ? itemPrice.getName() : String.valueOf(itemPrice.getItemId()));
        nameLabel.setForeground(Color.WHITE);
        
        JLabel priceLabel = new JLabel(itemPrice.getHighPrice());
        priceLabel.setForeground(getProfitColor(itemPrice.getProfitMargin()));
        
        alertLabel = new JLabel();
        updateAlertLabel();

        add(nameLabel, BorderLayout.WEST);
        add(priceLabel, BorderLayout.CENTER);
        add(alertLabel, BorderLayout.EAST);
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