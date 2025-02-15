package com.prospector.ui;

import net.runelite.api.Client;
import net.runelite.api.MenuEntry;
import net.runelite.api.events.MenuEntryAdded;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.game.ItemManager;
import net.runelite.client.menus.MenuManager;
import net.runelite.client.menus.WidgetMenuOption;
import com.prospector.ProspectorConfig;
import com.prospector.service.FlipAnalysisService;
import com.prospector.util.ProspectorUtil;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class MenuEntryHandlerTest {

    @Mock
    private Client client;

    @Mock
    private MenuManager menuManager;

    @Mock
    private ItemManager itemManager;

    @Mock
    private ProspectorConfig config;

    @Mock
    private FlipAnalysisService flipAnalysisService;

    @Mock
    private ProspectorUtil prospectorUtil;

    @Mock
    private Widget widget;

    private MenuEntryHandler handler;

    @Before
    public void setUp() {
        handler = new MenuEntryHandler(client, menuManager, itemManager, config, flipAnalysisService, prospectorUtil);
    }

    @Test
    public void testStartUp() {
        handler.startUp();
        verify(menuManager, times(3)).addManagedCustomMenu(any(WidgetMenuOption.class));
    }

    @Test
    public void testShutDown() {
        handler.shutDown();
        verify(menuManager, times(3)).removeManagedCustomMenu(any(WidgetMenuOption.class));
    }

    @Test
    public void testOnMenuEntryAdded_NonGEWidget() {
        MenuEntryAdded event = new MenuEntryAdded(
            "Test", "Test", 1, 0, 0, 0, true);
        handler.onMenuEntryAdded(event);
        verify(client, never()).setMenuEntries(any());
    }

    @Test
    public void testOnMenuEntryAdded_ValidGEWidget() {
        // Setup widget mock
        when(widget.getItemId()).thenReturn(4151); // Abyssal whip ID
        when(widget.getIndex()).thenReturn(0);

        // Setup menu entry
        MenuEntryAdded event = new MenuEntryAdded(
            "Examine",
            "Abyssal whip",
            1,
            WidgetInfo.GRAND_EXCHANGE_ITEM_CONTAINER.getId(),
            0,
            0,
            true);
        event.setWidget(widget);

        // Setup existing menu entries
        MenuEntry[] existingEntries = new MenuEntry[1];
        when(client.getMenuEntries()).thenReturn(existingEntries);

        // Execute test
        handler.onMenuEntryAdded(event);

        // Verify menu entries were updated
        verify(client).setMenuEntries(any(MenuEntry[].class));
    }

    @Test
    public void testOnMenuEntryAdded_InvalidItemId() {
        when(widget.getItemId()).thenReturn(-1);
        
        MenuEntryAdded event = new MenuEntryAdded(
            "Examine",
            "",
            1,
            WidgetInfo.GRAND_EXCHANGE_ITEM_CONTAINER.getId(),
            0,
            0,
            true);
        event.setWidget(widget);

        handler.onMenuEntryAdded(event);
        verify(client, never()).setMenuEntries(any());
    }

    @Test
    public void testOnMenuEntryAdded_NullWidget() {
        MenuEntryAdded event = new MenuEntryAdded(
            "Examine",
            "",
            1,
            WidgetInfo.GRAND_EXCHANGE_ITEM_CONTAINER.getId(),
            0,
            0,
            true);

        handler.onMenuEntryAdded(event);
        verify(client, never()).setMenuEntries(any());
    }
}