package com.prospector.ui;

import com.prospector.model.PriceAlert;
import com.prospector.service.AlertManager;
import com.prospector.util.ProspectorUtil;
import net.runelite.api.ItemComposition;
import net.runelite.client.game.ItemManager;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.components.FlatTextField;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;

public class AlertDialog extends JDialog {
    private final AlertManager alertManager;
    private final ItemManager itemManager;
    private final ProspectorUtil prospectorUtil;
    private final int itemId;
    private final String itemName;

    public AlertDialog(
            Frame parent,
            AlertManager alertManager,
            ItemManager itemManager,
            ProspectorUtil prospectorUtil,
            int itemId) {
        super(parent, "Set Price Alert", true);
        this.alertManager = alertManager;
        this.itemManager = itemManager;
        this.prospectorUtil = prospectorUtil;
        this.itemId = itemId;
        
        ItemComposition item = itemManager.getItemComposition(itemId);
        this.itemName = item.getName();

        init();
    }

    private void init() {
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBorder(new EmptyBorder(10, 10, 10, 10));
        content.setBackground(ColorScheme.DARKER_GRAY_COLOR);

        // Alert type selection
        JPanel typePanel = new JPanel(new BorderLayout(5, 0));
        typePanel.setBackground(ColorScheme.DARKER_GRAY_COLOR);
        JLabel typeLabel = new JLabel("Alert Type:");
        typeLabel.setForeground(Color.WHITE);
        JComboBox<PriceAlert.AlertType> typeCombo = new JComboBox<>(PriceAlert.AlertType.values());
        typePanel.add(typeLabel, BorderLayout.WEST);
        typePanel.add(typeCombo, BorderLayout.CENTER);

        // Condition selection
        JPanel conditionPanel = new JPanel(new BorderLayout(5, 0));
        conditionPanel.setBackground(ColorScheme.DARKER_GRAY_COLOR);
        JLabel conditionLabel = new JLabel("Condition:");
        conditionLabel.setForeground(Color.WHITE);
        JComboBox<PriceAlert.AlertCondition> conditionCombo = new JComboBox<>(PriceAlert.AlertCondition.values());
        conditionPanel.add(conditionLabel, BorderLayout.WEST);
        conditionPanel.add(conditionCombo, BorderLayout.CENTER);

        // Price input
        JPanel pricePanel = new JPanel(new BorderLayout(5, 0));
        pricePanel.setBackground(ColorScheme.DARKER_GRAY_COLOR);
        JLabel priceLabel = new JLabel("Target Price:");
        priceLabel.setForeground(Color.WHITE);
        FlatTextField priceField = new FlatTextField();
        priceField.setBackground(ColorScheme.DARKER_GRAY_COLOR);
        pricePanel.add(priceLabel, BorderLayout.WEST);
        pricePanel.add(priceField, BorderLayout.CENTER);

        // Notes input
        JPanel notesPanel = new JPanel(new BorderLayout(5, 0));
        notesPanel.setBackground(ColorScheme.DARKER_GRAY_COLOR);
        JLabel notesLabel = new JLabel("Notes:");
        notesLabel.setForeground(Color.WHITE);
        FlatTextField notesField = new FlatTextField();
        notesField.setBackground(ColorScheme.DARKER_GRAY_COLOR);
        notesPanel.add(notesLabel, BorderLayout.WEST);
        notesPanel.add(notesField, BorderLayout.CENTER);

        // Buttons
        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 5, 0));
        buttonPanel.setBackground(ColorScheme.DARKER_GRAY_COLOR);
        JButton saveButton = new JButton("Save");
        JButton cancelButton = new JButton("Cancel");

        saveButton.addActionListener(e -> {
            try {
                long targetPrice = Long.parseLong(priceField.getText().replaceAll("[^0-9]", ""));
                PriceAlert alert = PriceAlert.builder()
                    .itemId(itemId)
                    .itemName(itemName)
                    .type((PriceAlert.AlertType) typeCombo.getSelectedItem())
                    .condition((PriceAlert.AlertCondition) conditionCombo.getSelectedItem())
                    .targetPrice(targetPrice)
                    .enabled(true)
                    .notes(notesField.getText())
                    .build();

                alertManager.addAlert(alert);
                dispose();
            } catch (NumberFormatException ex) {
                prospectorUtil.sendNotification("Invalid price format");
            }
        });

        cancelButton.addActionListener(e -> dispose());

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        // Add components
        content.add(Box.createRigidArea(new Dimension(0, 5)));
        content.add(typePanel);
        content.add(Box.createRigidArea(new Dimension(0, 5)));
        content.add(conditionPanel);
        content.add(Box.createRigidArea(new Dimension(0, 5)));
        content.add(pricePanel);
        content.add(Box.createRigidArea(new Dimension(0, 5)));
        content.add(notesPanel);
        content.add(Box.createRigidArea(new Dimension(0, 10)));
        content.add(buttonPanel);

        setContentPane(content);
        pack();
        setLocationRelativeTo(getParent());
    }
}