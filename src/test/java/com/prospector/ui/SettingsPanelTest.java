package com.prospector.ui;

import com.prospector.ProspectorConfig;
import com.prospector.service.FlipAnalysisService;
import com.prospector.util.ProspectorUtil;
import net.runelite.api.ItemComposition;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.game.ItemManager;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import javax.swing.*;
import java.util.HashSet;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class SettingsPanelTest {

    @Mock
    private ProspectorConfig config;

    @Mock
    private FlipAnalysisService flipAnalysisService;

    @Mock
    private ItemManager itemManager;

    @Mock
    private ClientThread clientThread;

    @Mock
    private ProspectorUtil prospectorUtil;

    @Mock
    private ItemComposition itemComposition;

    private SettingsPanel settingsPanel;
    private static final int TEST_ITEM_ID = 4151;
    private static final String TEST_ITEM_NAME = "Abyssal whip";

    @Before
    public void setUp() {
        when(itemManager.getItemComposition(TEST_ITEM_ID)).thenReturn(itemComposition);
        when(itemComposition.getName()).thenReturn(TEST_ITEM_NAME);
        
        Set<Integer> trackedItems = new HashSet<>();
        trackedItems.add(TEST_ITEM_ID);
        when(flipAnalysisService.getTrackedItems()).thenReturn(trackedItems);

        // Mock ClientThread to execute immediately
        doAnswer(invocation -> {
            Runnable runnable = invocation.getArgument(0);
            runnable.run();
            return null;
        }).when(clientThread).invoke(any(Runnable.class));

        settingsPanel = new SettingsPanel(config, flipAnalysisService, itemManager, clientThread, prospectorUtil);
    }

    @Test
    public void testLoadTrackedItems() {
        // Verify that tracked items are loaded correctly
        JList<?> list = (JList<?>) findComponentByType(settingsPanel, JList.class);
        assertNotNull("Tracked items list should exist", list);
        assertEquals("List should have one item", 1, list.getModel().getSize());
        
        Object item = list.getModel().getElementAt(0);
        assertTrue("List item should be a TrackedItem", item instanceof SettingsPanel.TrackedItem);
        assertEquals("Item ID should match", TEST_ITEM_ID, ((SettingsPanel.TrackedItem) item).itemId);
        assertEquals("Item name should match", TEST_ITEM_NAME, ((SettingsPanel.TrackedItem) item).itemName);
    }

    @Test
    public void testRemoveTrackedItem() {
        JList<?> list = (JList<?>) findComponentByType(settingsPanel, JList.class);
        list.setSelectedIndex(0);

        // Find and click remove button
        JButton removeButton = findButtonByText(settingsPanel, "Remove");
        assertNotNull("Remove button should exist", removeButton);
        removeButton.doClick();

        verify(flipAnalysisService).untrackItem(TEST_ITEM_ID);
        verify(prospectorUtil).sendNotification(contains("Stopped tracking"));
        assertEquals("List should be empty", 0, list.getModel().getSize());
    }

    private Component findComponentByType(Container container, Class<?> type) {
        for (Component component : container.getComponents()) {
            if (type.isInstance(component)) {
                return component;
            }
            if (component instanceof Container) {
                Component found = findComponentByType((Container) component, type);
                if (found != null) {
                    return found;
                }
            }
        }
        return null;
    }

    private JButton findButtonByText(Container container, String text) {
        for (Component component : container.getComponents()) {
            if (component instanceof JButton && text.equals(((JButton) component).getText())) {
                return (JButton) component;
            }
            if (component instanceof Container) {
                JButton found = findButtonByText((Container) component, text);
                if (found != null) {
                    return found;
                }
            }
        }
        return null;
    }
}