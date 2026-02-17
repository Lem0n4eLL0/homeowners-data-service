package ru.zeker.notification.service.handlers;

public interface KafkaEventHandler<T> {

    Class<T> getEventType();

    void handle(T event);
}
