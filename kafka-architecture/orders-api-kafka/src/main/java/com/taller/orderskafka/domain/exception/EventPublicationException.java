package com.taller.orderskafka.domain.exception;

public class EventPublicationException extends RuntimeException {

    public EventPublicationException(String message, Throwable cause) {
        super(message, cause);
    }
}
