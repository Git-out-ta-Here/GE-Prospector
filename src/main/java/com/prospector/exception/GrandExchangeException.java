package com.prospector.exception;

public class GrandExchangeException extends RuntimeException {
    private final ErrorType errorType;

    public enum ErrorType {
        API_ERROR("Failed to fetch price data"),
        RATE_LIMIT("Rate limit exceeded"),
        INVALID_ITEM("Invalid item ID"),
        NETWORK_ERROR("Network connection failed"),
        CACHE_ERROR("Cache operation failed"),
        MARKET_CLOSED("Grand Exchange is not available"),
        INSUFFICIENT_FUNDS("Insufficient funds for operation"),
        TRADE_LIMIT("Trading limit reached"),
        INVALID_PRICE("Invalid price value"),
        CONFIGURATION_ERROR("Configuration error");

        private final String defaultMessage;

        ErrorType(String defaultMessage) {
            this.defaultMessage = defaultMessage;
        }

        public String getDefaultMessage() {
            return defaultMessage;
        }
    }

    public GrandExchangeException(ErrorType errorType) {
        super(errorType.getDefaultMessage());
        this.errorType = errorType;
    }

    public GrandExchangeException(ErrorType errorType, String message) {
        super(message);
        this.errorType = errorType;
    }

    public GrandExchangeException(ErrorType errorType, String message, Throwable cause) {
        super(message, cause);
        this.errorType = errorType;
    }

    public ErrorType getErrorType() {
        return errorType;
    }

    public boolean isRetryable() {
        switch (errorType) {
            case API_ERROR:
            case NETWORK_ERROR:
            case RATE_LIMIT:
                return true;
            default:
                return false;
        }
    }

    public boolean requiresUserAction() {
        switch (errorType) {
            case INSUFFICIENT_FUNDS:
            case TRADE_LIMIT:
            case MARKET_CLOSED:
            case CONFIGURATION_ERROR:
                return true;
            default:
                return false;
        }
    }
}