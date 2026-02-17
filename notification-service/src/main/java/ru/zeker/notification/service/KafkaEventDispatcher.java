package ru.zeker.notification.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.zeker.notification.service.handlers.KafkaEventHandler;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
public class KafkaEventDispatcher {

    private final Map<Class<?>, KafkaEventHandler<?>> handlers;

    public KafkaEventDispatcher(List<KafkaEventHandler<?>> handlers) {
        this.handlers = handlers.stream()
                .collect(Collectors.toMap(
                        KafkaEventHandler::getEventType,
                        Function.identity()
                ));
    }

    @SuppressWarnings("unchecked")
    public <T> void dispatch(T event) {
        log.debug("Event class: {}", event.getClass());

        var handler = (KafkaEventHandler<T>) Optional.ofNullable(handlers.get(event.getClass()))
                .orElseThrow(() ->
                        new IllegalStateException("No handler for event type: " + event.getClass()));
        handler.handle(event);
    }
}

