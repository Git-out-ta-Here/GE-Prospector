package com.GEProspect;

import net.runelite.client.ui.ColorScheme;
import net.runelite.client.util.ImageUtil;
import net.runelite.client.util.QuantityFormatter;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.NumberFormat;
import java.util.Locale;

public class ItemPanel extends JPanel {
    private static final Dimension ICON_SIZE = new Dimension(32, 32);
    private static final NumberFormat GP_FORMAT = NumberFormat.getNumberInstance(Locale.US);
    
    private final JPanel container;
    private final JLabel iconLabel;
    private final JLabel nameLabel;
    private final JLabel priceLabel;
    private final JLabel timeLabel;
    private final JLabel profitLabel;
    private final PriceChartPanel chartPanel;
    private final CardLayout cardLayout;
    private final JPanel contentPanel;
    
    public ItemPanel(ItemPrice item, EstimatedTime timeEst) {
        setLayout(new BorderLayout());
        setBackground(ColorScheme.DARKER_GRAY_COLOR);
        
        // Create card layout for switching between summary and chart views
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(ColorScheme.DARKER_GRAY_COLOR);
        
        // Create main container for summary view
        container = new JPanel();
        container.setLayout(new BorderLayout());
        container.setBackground(ColorScheme.DARKER_GRAY_COLOR);
        container.setBorder(new CompoundBorder(
            new MatteBorder(0, 0, 1, 0, ColorScheme.MEDIUM_GRAY_COLOR),
            new EmptyBorder(5, 5, 5, 5)
        ));
        
        // Left side with icon and name
        JPanel leftSide = new JPanel(new BorderLayout(5, 0));
        leftSide.setBackground(ColorScheme.DARKER_GRAY_COLOR);
        
        iconLabel = new JLabel();
        iconLabel.setPreferredSize(ICON_SIZE);
        
        nameLabel = new JLabel();
        nameLabel.setForeground(Color.WHITE);
        
        leftSide.add(iconLabel, BorderLayout.WEST);
        leftSide.add(nameLabel, BorderLayout.CENTER);
        
        // Right side with price and time info
        JPanel rightSide = new JPanel(new GridLayout(2, 1, 0, 2));
        rightSide.setBackground(ColorScheme.DARKER_GRAY_COLOR);
        
        priceLabel = new JLabel();
        priceLabel.setForeground(ColorScheme.GRAND_EXCHANGE_PRICE);
        
        timeLabel = new JLabel();
        timeLabel.setForeground(ColorScheme.LIGHT_GRAY_COLOR);
        
        profitLabel = new JLabel();
        profitLabel.setForeground(getProfitColor(item.getProfitMargin()));
        
        rightSide.add(priceLabel);
        rightSide.add(timeLabel);
        
        container.add(leftSide, BorderLayout.WEST);
        container.add(rightSide, BorderLayout.EAST);
        
        // Create chart panel
        chartPanel = new PriceChartPanel();
        chartPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        // Add both views to card layout
        contentPanel.add(container, "summary");
        contentPanel.add(chartPanel, "chart");
        
        add(contentPanel, BorderLayout.CENTER);
        
        // Update with item data
        updateDisplay(item, timeEst);
        
        // Add click handler for switching views
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    toggleView();
                }
            }
            
            @Override
            public void mouseEntered(MouseEvent e) {
                container.setBackground(ColorScheme.DARKER_GRAY_HOVER_COLOR);
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                container.setBackground(ColorScheme.DARKER_GRAY_COLOR);
            }
        });
    }
    
    private void toggleView() {
        CardLayout cl = (CardLayout) contentPanel.getLayout();
        if (contentPanel.getComponent(0).isVisible()) {
            cl.show(contentPanel, "chart");
        } else {
            cl.show(contentPanel, "summary");
        }
    }
    
    public void updateDisplay(ItemPrice item, EstimatedTime timeEst) {
        nameLabel.setText(item.getItemId() + "");  // TODO: Get item name from ItemManager
        
        String priceText = String.format("%s gp", GP_FORMAT.format(item.getHighPrice()));
        priceLabel.setText(priceText);
        
        String timeText = String.format("%s %s", 
            timeEst.getCategory().getIndicator(),
            timeEst.getCategory().getTimeRange()
        );
        timeLabel.setText(timeText);
        
        String profitText = String.format("%s gp", 
            GP_FORMAT.format(item.getProfitMargin())
        );
        profitLabel.setText(profitText);
        
        // Update chart data
        chartPanel.addPrice(item.getLowPrice(), item.getHighPrice());
        
        // Tooltip with detailed information
        String tooltip = String.format("<html>" +
            "Buy Price: %s gp<br>" +
            "Sell Price: %s gp<br>" +
            "Profit: %s gp<br>" +
            "Est. Time: %s<br>" +
            "Confidence: %.0f%%<br>" +
            "Double-click to view chart</html>",
            GP_FORMAT.format(item.getLowPrice()),
            GP_FORMAT.format(item.getHighPrice()),
            GP_FORMAT.format(item.getProfitMargin()),
            timeEst.getCategory().getTimeRange(),
            timeEst.getConfidence() * 100
        );
        setToolTipText(tooltip);
    }
    
    private Color getProfitColor(int profit) {
        if (profit > 100000) return ColorScheme.PROGRESS_COMPLETE_COLOR;
        if (profit > 50000) return ColorScheme.PROGRESS_INPROGRESS_COLOR;
        if (profit > 10000) return ColorScheme.GRAND_EXCHANGE_ALCH;
        return ColorScheme.LIGHT_GRAY_COLOR;
    }
    
    // Additional constructor for active flips
    public ItemPanel(FlipEntry flip, MarketDataService marketService) {
        this(marketService.getPrice(flip.getItemId()), 
             new EstimatedTime((int)(flip.getDuration() / 60000), 1.0));
        
        // Update profit display for active flips
        if (flip.isFlipComplete()) {
            profitLabel.setText(String.format("%s gp", 
                GP_FORMAT.format(flip.getProfit())
            ));
            profitLabel.setForeground(getProfitColor(flip.getProfit()));
        }
    }
}