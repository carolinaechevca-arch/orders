package com.taller.billingkafka.domain.exception;

public class BillingProcessingException extends RuntimeException {
    public BillingProcessingException(String message, Throwable cause) {
        super(message, cause);
    }
}
