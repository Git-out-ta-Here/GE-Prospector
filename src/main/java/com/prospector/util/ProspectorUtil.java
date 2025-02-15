package com.prospector.util;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.client.chat.ChatColorType;
import net.runelite.client.chat.ChatMessageBuilder;
import net.runelite.client.chat.ChatMessageManager;
import net.runelite.client.chat.QueuedMessage;

import javax.inject.Inject;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

@Slf4j
public class ProspectorUtil {
    private final Client client;
    private final ChatMessageManager chatMessageManager;

    @Inject
    public ProspectorUtil(Client client, ChatMessageManager chatMessageManager) {
        this.client = client;
        this.chatMessageManager = chatMessageManager;
    }

    public void sendNotification(String message) {
        String chatMessage = new ChatMessageBuilder()
            .append(ChatColorType.HIGHLIGHT)
            .append("GE Prospector: ")
            .append(ChatColorType.NORMAL)
            .append(message)
            .build();

        chatMessageManager.queue(QueuedMessage.builder()
            .type(ChatMessageType.CONSOLE)
            .runeLiteFormattedMessage(chatMessage)
            .build());
    }

    public void sendErrorNotification(String message, Throwable error) {
        log.error(message, error);
        
        String chatMessage = new ChatMessageBuilder()
            .append(ChatColorType.HIGHLIGHT)
            .append("GE Prospector Error: ")
            .append(ChatColorType.ERROR)
            .append(message)
            .build();

        chatMessageManager.queue(QueuedMessage.builder()
            .type(ChatMessageType.CONSOLE)
            .runeLiteFormattedMessage(chatMessage)
            .build());
    }

    public <T> CompletableFuture<T> wrapAsync(Supplier<CompletableFuture<T>> operation, String errorMessage) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return operation.get().join();
            } catch (Exception e) {
                sendErrorNotification(errorMessage, e);
                throw e;
            }
        });
    }

    public void ensureClientThread(Runnable action) {
        if (client.isClientThread()) {
            action.run();
        } else {
            client.invokeLater(action);
        }
    }

    public static String formatPrice(long price) {
        if (price >= 1_000_000_000) {
            return String.format("%.1fb", price / 1_000_000_000.0);
        } else if (price >= 1_000_000) {
            return String.format("%.1fm", price / 1_000_000.0);
        } else if (price >= 1_000) {
            return String.format("%.1fk", price / 1_000.0);
        }
        return String.valueOf(price);
    }

    public static String formatTime(int minutes) {
        if (minutes < 60) {
            return minutes + "m";
        }
        return String.format("%dh %dm", minutes / 60, minutes % 60);
    }
}