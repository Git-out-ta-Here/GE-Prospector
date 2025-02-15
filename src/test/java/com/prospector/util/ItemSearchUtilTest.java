package com.prospector.util;

import net.runelite.api.ItemComposition;
import net.runelite.client.game.ItemManager;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ItemSearchUtilTest {

    @Mock
    private ItemManager itemManager;

    @Mock
    private ItemComposition whip;

    @Mock
    private ItemComposition whipNote;

    @Mock
    private ItemComposition dragonDagger;

    private ItemSearchUtil searchUtil;

    @Before
    public void setUp() {
        searchUtil = new ItemSearchUtil(itemManager);

        // Setup mock items
        when(whip.getId()).thenReturn(4151);
        when(whip.getName()).thenReturn("Abyssal whip");
        when(whip.isNote()).thenReturn(false);
        when(whip.isTradeable()).thenReturn(true);

        when(whipNote.getId()).thenReturn(4152);
        when(whipNote.getName()).thenReturn("Abyssal whip (noted)");
        when(whipNote.isNote()).thenReturn(true);
        when(whipNote.isTradeable()).thenReturn(true);

        when(dragonDagger.getId()).thenReturn(1215);
        when(dragonDagger.getName()).thenReturn("Dragon dagger");
        when(dragonDagger.isNote()).thenReturn(false);
        when(dragonDagger.isTradeable()).thenReturn(true);
    }

    @Test
    public void testExactMatch() {
        when(itemManager.search(any())).thenReturn(Arrays.asList(whip, dragonDagger));

        List<ItemSearchUtil.SearchResult> results = searchUtil.searchItems("Abyssal whip");
        
        assertFalse(results.isEmpty());
        assertEquals("Abyssal whip", results.get(0).name);
        assertEquals(4151, results.get(0).id);
    }

    @Test
    public void testPartialMatch() {
        when(itemManager.search(any())).thenReturn(Arrays.asList(whip, dragonDagger));

        List<ItemSearchUtil.SearchResult> results = searchUtil.searchItems("whip");
        
        assertFalse(results.isEmpty());
        assertTrue(results.stream().anyMatch(r -> r.name.toLowerCase().contains("whip")));
    }

    @Test
    public void testIgnoreNotes() {
        when(itemManager.search(any())).thenReturn(Arrays.asList(whip, whipNote));

        List<ItemSearchUtil.SearchResult> results = searchUtil.searchItems("whip");
        
        assertFalse(results.isEmpty());
        assertTrue(results.stream().noneMatch(r -> r.name.contains("noted")));
    }

    @Test
    public void testSearchCaseInsensitive() {
        when(itemManager.search(any())).thenReturn(Arrays.asList(whip, dragonDagger));

        List<ItemSearchUtil.SearchResult> results = searchUtil.searchItems("WHIP");
        
        assertFalse(results.isEmpty());
        assertTrue(results.stream().anyMatch(r -> r.name.toLowerCase().contains("whip")));
    }

    @Test
    public void testSortingOrder() {
        when(itemManager.search(any())).thenReturn(Arrays.asList(
            whip,
            dragonDagger,
            createMockItem(1234, "Whippy the dragon", false, true),
            createMockItem(1235, "Dragon whip", false, true)
        ));

        List<ItemSearchUtil.SearchResult> results = searchUtil.searchItems("whip");
        
        assertFalse(results.isEmpty());
        // Exact match should be first
        assertEquals("Abyssal whip", results.get(0).name);
        // Items starting with search term should be next
        assertTrue(results.stream()
            .skip(1)
            .anyMatch(r -> r.name.toLowerCase().startsWith("whip")));
    }

    private ItemComposition createMockItem(int id, String name, boolean isNote, boolean isTradeable) {
        ItemComposition item = mock(ItemComposition.class);
        when(item.getId()).thenReturn(id);
        when(item.getName()).thenReturn(name);
        when(item.isNote()).thenReturn(isNote);
        when(item.isTradeable()).thenReturn(isTradeable);
        return item;
    }
}