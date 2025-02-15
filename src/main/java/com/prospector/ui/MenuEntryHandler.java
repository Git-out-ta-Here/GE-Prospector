package com.prospector.ui;

import net.runelite.api.Client;
import net.runelite.api.MenuAction;
import net.runelite.api.MenuEntry;
import net.runelite.api.events.MenuEntryAdded;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetID;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.game.ItemManager;
import net.runelite.client.menus.MenuManager;
import net.runelite.client.menus.WidgetMenuOption;
import com.prospector.ProspectorConfig;
import com.prospector.service.FlipAnalysisService;
import com.prospector.util.ProspectorUtil;

import javax.inject.Inject;

public class MenuEntryHandler {
    private static final String TRACK_OPTION = "Track";
    private static final String SET_ALERT_OPTION = "Set Alert";
    private static final String ANALYZE_OPTION = "Analyze";

    private final Client client;
    private final MenuManager menuManager;
    private final ItemManager itemManager;
    private final ProspectorConfig config;
    private final FlipAnalysisService flipAnalysisService;
    private final ProspectorUtil prospectorUtil;

    @Inject
    public MenuEntryHandler(
            Client client,
            MenuManager menuManager,
            ItemManager itemManager,
            ProspectorConfig config,
            FlipAnalysisService flipAnalysisService,
            ProspectorUtil prospectorUtil) {
        this.client = client;
        this.menuManager = menuManager;
        this.itemManager = itemManager;
        this.config = config;
        this.flipAnalysisService = flipAnalysisService;
        this.prospectorUtil = prospectorUtil;
    }

    public void startUp() {
        addMenuOptions();
    }

    public void shutDown() {
        removeMenuOptions();
    }

    private void addMenuOptions() {
        menuManager.addManagedCustomMenu(new WidgetMenuOption(TRACK_OPTION, "GE Item", WidgetInfo.GRAND_EXCHANGE_ITEM_CONTAINER));
        menuManager.addManagedCustomMenu(new WidgetMenuOption(SET_ALERT_OPTION, "GE Item", WidgetInfo.GRAND_EXCHANGE_ITEM_CONTAINER));
        menuManager.addManagedCustomMenu(new WidgetMenuOption(ANALYZE_OPTION, "GE Item", WidgetInfo.GRAND_EXCHANGE_ITEM_CONTAINER));
    }

    private void removeMenuOptions() {
        menuManager.removeManagedCustomMenu(new WidgetMenuOption(TRACK_OPTION, "GE Item", WidgetInfo.GRAND_EXCHANGE_ITEM_CONTAINER));
        menuManager.removeManagedCustomMenu(new WidgetMenuOption(SET_ALERT_OPTION, "GE Item", WidgetInfo.GRAND_EXCHANGE_ITEM_CONTAINER));
        menuManager.removeManagedCustomMenu(new WidgetMenuOption(ANALYZE_OPTION, "GE Item", WidgetInfo.GRAND_EXCHANGE_ITEM_CONTAINER));
    }

    public void onMenuEntryAdded(MenuEntryAdded event) {
        if (!isGrandExchangeWidget(event.getActionParam1())) {
            return;
        }

        Widget widget = event.getWidget();
        if (widget == null) {
            return;
        }

        int itemId = widget.getItemId();
        if (itemId <= 0) {
            return;
        }

        MenuEntry[] menuEntries = client.getMenuEntries();
        MenuEntry[] newEntries = new MenuEntry[menuEntries.length + 3];
        System.arraycopy(menuEntries, 0, newEntries, 0, menuEntries.length);

        addTrackEntry(newEntries, menuEntries.length, itemId, widget);
        addAlertEntry(newEntries, menuEntries.length + 1, itemId, widget);
        addAnalyzeEntry(newEntries, menuEntries.length + 2, itemId, widget);

        client.setMenuEntries(newEntries);
    }

    private void addTrackEntry(MenuEntry[] entries, int index, int itemId, Widget widget) {
        MenuEntry entry = createMenuEntry(itemId, widget, TRACK_OPTION, event -> {
            String itemName = itemManager.getItemComposition(itemId).getName();
            prospectorUtil.sendNotification("Now tracking " + itemName);
            flipAnalysisService.trackItem(itemId);
        });
        entries[index] = entry;
    }

    private void addAlertEntry(MenuEntry[] entries, int index, int itemId, Widget widget) {
        MenuEntry entry = createMenuEntry(itemId, widget, SET_ALERT_OPTION, event -> {
            // TODO: Implement alert dialog
            String itemName = itemManager.getItemComposition(itemId).getName();
            prospectorUtil.sendNotification("Alert functionality coming soon for " + itemName);
        });
        entries[index] = entry;
    }

    private void addAnalyzeEntry(MenuEntry[] entries, int index, int itemId, Widget widget) {
        MenuEntry entry = createMenuEntry(itemId, widget, ANALYZE_OPTION, event -> {
            flipAnalysisService.analyzeItem(itemId)
                .thenAccept(opportunity -> {
                    if (opportunity != null) {
                        String message = String.format("%s - Margin: %s, ROI: %.1f%%, Time: %s",
                            opportunity.getItemName(),
                            ProspectorUtil.formatPrice(opportunity.getMargin()),
                            opportunity.getRoi(),
                            ProspectorUtil.formatTime(opportunity.getEstimatedTimeMinutes()));
                        prospectorUtil.sendNotification(message);
                    }
                });
        });
        entries[index] = entry;
    }

    private MenuEntry createMenuEntry(int itemId, Widget widget, String option, MenuAction.MenuHook hook) {
        return client.createMenuEntry(-1)
            .setOption(option)
            .setTarget("GE Item")
            .setIdentifier(itemId)
            .setType(MenuAction.RUNELITE)
            .setParam0(widget.getIndex())
            .setParam1(WidgetInfo.GRAND_EXCHANGE_ITEM_CONTAINER.getId());
    }

    private boolean isGrandExchangeWidget(int widgetId) {
        return widgetId == WidgetInfo.GRAND_EXCHANGE_ITEM_CONTAINER.getId() ||
               widgetId == WidgetInfo.GRAND_EXCHANGE_INVENTORY_ITEMS_CONTAINER.getId();
    }
}