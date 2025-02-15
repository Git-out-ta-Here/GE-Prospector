package com.prospector.util;

import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.client.chat.ChatMessageManager;
import net.runelite.client.chat.QueuedMessage;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.concurrent.CompletableFuture;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ProspectorUtilTest {

    @Mock
    private Client client;

    @Mock
    private ChatMessageManager chatMessageManager;

    private ProspectorUtil util;

    @Before
    public void setUp() {
        util = new ProspectorUtil(client, chatMessageManager);
    }

    @Test
    public void testSendNotification() {
        ArgumentCaptor<QueuedMessage> messageCaptor = ArgumentCaptor.forClass(QueuedMessage.class);
        
        util.sendNotification("Test message");
        
        verify(chatMessageManager).queue(messageCaptor.capture());
        QueuedMessage message = messageCaptor.getValue();
        
        assertNotNull(message);
        assertEquals(ChatMessageType.CONSOLE, message.getType());
        assertTrue(message.getRuneLiteFormattedMessage().contains("Test message"));
    }

    @Test
    public void testSendErrorNotification() {
        Exception testError = new RuntimeException("Test error");
        ArgumentCaptor<QueuedMessage> messageCaptor = ArgumentCaptor.forClass(QueuedMessage.class);
        
        util.sendErrorNotification("Error occurred", testError);
        
        verify(chatMessageManager).queue(messageCaptor.capture());
        QueuedMessage message = messageCaptor.getValue();
        
        assertNotNull(message);
        assertEquals(ChatMessageType.CONSOLE, message.getType());
        assertTrue(message.getRuneLiteFormattedMessage().contains("Error occurred"));
    }

    @Test
    public void testWrapAsync_Success() {
        CompletableFuture<String> future = util.wrapAsync(
            () -> CompletableFuture.completedFuture("success"),
            "Operation failed"
        );

        assertEquals("success", future.join());
        verify(chatMessageManager, never()).queue(any());
    }

    @Test
    public void testWrapAsync_Failure() {
        RuntimeException testException = new RuntimeException("Test failure");
        CompletableFuture<String> future = util.wrapAsync(
            () -> CompletableFuture.failedFuture(testException),
            "Operation failed"
        );

        try {
            future.join();
            fail("Expected exception was not thrown");
        } catch (Exception e) {
            verify(chatMessageManager).queue(any());
        }
    }

    @Test
    public void testEnsureClientThread_OnClientThread() {
        when(client.isClientThread()).thenReturn(true);
        Runnable action = mock(Runnable.class);
        
        util.ensureClientThread(action);
        
        verify(action).run();
        verify(client, never()).invokeLater(any());
    }

    @Test
    public void testEnsureClientThread_NotOnClientThread() {
        when(client.isClientThread()).thenReturn(false);
        Runnable action = mock(Runnable.class);
        
        util.ensureClientThread(action);
        
        verify(action, never()).run();
        verify(client).invokeLater(action);
    }

    @Test
    public void testFormatPrice() {
        assertEquals("100", ProspectorUtil.formatPrice(100));
        assertEquals("1.0k", ProspectorUtil.formatPrice(1000));
        assertEquals("1.5k", ProspectorUtil.formatPrice(1500));
        assertEquals("1.0m", ProspectorUtil.formatPrice(1000000));
        assertEquals("1.5m", ProspectorUtil.formatPrice(1500000));
        assertEquals("1.0b", ProspectorUtil.formatPrice(1000000000));
        assertEquals("1.5b", ProspectorUtil.formatPrice(1500000000));
    }

    @Test
    public void testFormatTime() {
        assertEquals("30m", ProspectorUtil.formatTime(30));
        assertEquals("1h 0m", ProspectorUtil.formatTime(60));
        assertEquals("1h 30m", ProspectorUtil.formatTime(90));
        assertEquals("2h 15m", ProspectorUtil.formatTime(135));
    }
}