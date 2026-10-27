package com.taller.ordersrabbitmq.domain.exception;

public class OrderPublicationException extends RuntimeException {

    public OrderPublicationException(String message, Throwable cause) {
        super(message, cause);
    }
}
