package com.GEProspect;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.game.ItemManager;
import net.runelite.client.ui.ColorScheme;
import javax.inject.Singleton;
import javax.swing.*;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Singleton
public class ProfitDashboard extends JPanel {
    private final FlipTracker flipTracker;
    private final ItemManager itemManager;
    private final JLabel totalProfitLabel = new JLabel("0");
    private final JLabel todayProfitLabel = new JLabel("0");
    private final JLabel avgProfitPerFlipLabel = new JLabel("0");
    private final JComboBox<TimeRange> timeRangeSelector;
    private final JPanel topItemsPanel = new JPanel(new GridLayout(0, 1, 0, 5));
    private final JLabel targetProfitLabel = new JLabel("Not set");
    private final JProgressBar progressBar = new JProgressBar(0, 100);
    private int targetProfit = 0;

    public ProfitDashboard(FlipTracker flipTracker, ItemManager itemManager) {
        this.flipTracker = flipTracker;
        this.itemManager = itemManager;
        
        setLayout(new BorderLayout(0, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Time range selector
        timeRangeSelector = new JComboBox<>(TimeRange.values());
        timeRangeSelector.addActionListener(e -> updateStats());
        
        // Stats panel
        JPanel statsPanel = new JPanel(new GridLayout(3, 2, 5, 5));
        statsPanel.add(new JLabel("Total Profit:"));
        statsPanel.add(totalProfitLabel);
        statsPanel.add(new JLabel("Today's Profit:"));
        statsPanel.add(todayProfitLabel);
        statsPanel.add(new JLabel("Avg. Profit/Flip:"));
        statsPanel.add(avgProfitPerFlipLabel);
        
        // Add target profit section
        JPanel targetPanel = new JPanel(new BorderLayout(5, 0));
        targetPanel.setBackground(ColorScheme.DARKER_GRAY_COLOR);
        
        JButton setTargetButton = new JButton("Set Target");
        setTargetButton.addActionListener(e -> showSetTargetDialog());
        
        JPanel targetLabelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        targetLabelPanel.setBackground(ColorScheme.DARKER_GRAY_COLOR);
        targetLabelPanel.add(new JLabel("Target Profit: "));
        targetLabelPanel.add(targetProfitLabel);
        
        targetPanel.add(targetLabelPanel, BorderLayout.CENTER);
        targetPanel.add(setTargetButton, BorderLayout.EAST);
        
        // Progress bar setup
        progressBar.setStringPainted(true);
        progressBar.setForeground(ColorScheme.PROGRESS_COMPLETE_COLOR);
        progressBar.setBackground(ColorScheme.DARKER_GRAY_COLOR);
        
        // Add to stats panel after the existing stats
        statsPanel.add(targetPanel);
        statsPanel.add(progressBar);
        
        // Layout
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.add(new JLabel("Time Range:"), BorderLayout.WEST);
        headerPanel.add(timeRangeSelector, BorderLayout.CENTER);
        
        add(headerPanel, BorderLayout.NORTH);
        add(statsPanel, BorderLayout.CENTER);
        
        JPanel topItemsWrapper = new JPanel(new BorderLayout());
        topItemsWrapper.add(new JLabel("Top Items:"), BorderLayout.NORTH);
        topItemsWrapper.add(topItemsPanel, BorderLayout.CENTER);
        add(topItemsWrapper, BorderLayout.SOUTH);
        
        updateStats();
    }

    private void showSetTargetDialog() {
        JTextField targetField = new JTextField(10);
        JPanel panel = new JPanel(new GridLayout(0, 1));
        panel.add(new JLabel("Enter target profit (gp):"));
        panel.add(targetField);
        
        int result = JOptionPane.showConfirmDialog(this, panel,
            "Set Profit Target", JOptionPane.OK_CANCEL_OPTION);
            
        if (result == JOptionPane.OK_OPTION) {
            try {
                targetProfit = Integer.parseInt(targetField.getText().replaceAll("[^0-9]", ""));
                targetProfitLabel.setText(String.format("%,d gp", targetProfit));
                updateStats(); // This will update the progress bar
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this,
                    "Please enter a valid number",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public void updateStats() {
        TimeRange range = (TimeRange) timeRangeSelector.getSelectedItem();
        LocalDateTime cutoff = LocalDateTime.now().minus(range.duration);
        
        List<FlipEntry> flips = flipTracker.getFlips().stream()
            .filter(flip -> flip.getTimestamp().isAfter(cutoff))
            .collect(Collectors.toList());
        
        // Calculate stats
        int totalProfit = flips.stream()
            .mapToInt(FlipEntry::getProfit)
            .sum();
            
        int todayProfit = flips.stream()
            .filter(flip -> flip.getTimestamp().isAfter(LocalDateTime.now().truncatedTo(ChronoUnit.DAYS)))
            .mapToInt(FlipEntry::getProfit)
            .sum();
            
        double avgProfit = flips.isEmpty() ? 0 : 
            flips.stream().mapToInt(FlipEntry::getProfit).average().getAsDouble();
        
        // Update labels
        totalProfitLabel.setText(String.format("%,d gp", totalProfit));
        todayProfitLabel.setText(String.format("%,d gp", todayProfit));
        avgProfitPerFlipLabel.setText(String.format("%,d gp", (int)avgProfit));
        
        // Update progress bar
        if (targetProfit > 0) {
            int progress = (int) ((totalProfit * 100.0) / targetProfit);
            progress = Math.min(100, progress); // Cap at 100%
            progressBar.setValue(progress);
            progressBar.setString(String.format("%d%% (%,d/%,d gp)", 
                progress, totalProfit, targetProfit));
        } else {
            progressBar.setValue(0);
            progressBar.setString("No target set");
        }
        
        // Highlight when target is reached
        if (totalProfit >= targetProfit && targetProfit > 0) {
            targetProfitLabel.setForeground(ColorScheme.PROGRESS_COMPLETE_COLOR);
        } else {
            targetProfitLabel.setForeground(Color.WHITE);
        }
        
        // Update top items
        updateTopItems(flips);
    }

    private void updateTopItems(List<FlipEntry> flips) {
        topItemsPanel.removeAll();
        
        // Group flips by item and calculate total profit
        Map<Integer, Integer> itemProfits = new HashMap<>();
        for (FlipEntry flip : flips) {
            itemProfits.merge(flip.getItemId(), flip.getProfit(), Integer::sum);
        }
        
        // Sort by profit and take top 5
        itemProfits.entrySet().stream()
            .sorted(Map.Entry.<Integer, Integer>comparingByValue().reversed())
            .limit(5)
            .forEach(entry -> {
                String itemName = itemManager.getItemComposition(entry.getKey()).getName();
                JLabel itemLabel = new JLabel(String.format("%s: %,d gp", itemName, entry.getValue()));
                topItemsPanel.add(itemLabel);
            });
        
        topItemsPanel.revalidate();
        topItemsPanel.repaint();
    }

    public enum TimeRange {
        TODAY("Today", ChronoUnit.DAYS.getDuration()),
        WEEK("Past Week", ChronoUnit.WEEKS.getDuration()),
        MONTH("Past Month", ChronoUnit.MONTHS.getDuration()),
        ALL("All Time", ChronoUnit.FOREVER.getDuration());

        private final String label;
        private final java.time.Duration duration;

        TimeRange(String label, java.time.Duration duration) {
            this.label = label;
            this.duration = duration;
        }

        @Override
        public String toString() {
            return label;
        }
    }
}