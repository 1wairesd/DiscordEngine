package com.wairesdindustries.discordengine.common.event;

import com.google.common.collect.SetMultimap;
import com.wairesdindustries.discordengine.api.event.DEEvent;
import com.wairesdindustries.discordengine.api.event.EventBus;
import com.wairesdindustries.discordengine.api.event.Subscriber;
import net.kyori.event.EventSubscriber;
import net.kyori.event.PostResult;
import net.kyori.event.SimpleEventBus;
import net.kyori.event.method.MethodHandleEventExecutorFactory;
import net.kyori.event.method.MethodSubscriptionAdapter;
import net.kyori.event.method.SimpleMethodSubscriptionAdapter;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.jetbrains.annotations.NotNull;

import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;

public class EventBusImpl implements EventBus {

    private final Logger logger;
    private final SimpleEventBus<DEEvent> bus = new SimpleEventBus<>(DEEvent.class);
    private final MethodSubscriptionAdapter<Subscriber> methodAdapter = new SimpleMethodSubscriptionAdapter<>(this, new MethodHandleEventExecutorFactory<>());

    public EventBusImpl(Logger logger) {
        this.logger = logger;
    }

    @Override
    public @NonNull Class<DEEvent> eventType() {
        return bus.eventType();
    }

    @Override
    public @NonNull PostResult post(@NonNull DEEvent event) {
        PostResult postResult = bus.post(event);

        if (!postResult.wasSuccessful()) {
            logger.log(Level.WARNING, "Failed to post event: " + event.getClass().getSimpleName());

            postResult.exceptions().forEach((subscriber, exception) ->
                    logger.log(Level.WARNING, "Subscriber: " + subscriber, exception)
            );
        }

        return postResult;
    }


    @Override
    public void register(@NotNull Subscriber listener) {
        try {
            methodAdapter.register(listener);
        } catch (Exception e) {
            logger.log(Level.WARNING, "Error with event listener " + listener.getClass() + " registration:", e);
        }
    }

    @Override
    public <T extends DEEvent> void register(@NonNull Class<T> clazz, @NonNull EventSubscriber<? super T> subscriber) {
        try {
            bus.register(clazz, subscriber);
        } catch (Exception e) {
            logger.log(Level.WARNING, "Error with event subscriber " + subscriber.getClass() + " registration:", e);
        }
    }

    @Override
    public void unregister(@NotNull Subscriber listener) {
        methodAdapter.unregister(listener);
    }

    @Override
    public void unregister(@NonNull EventSubscriber<?> subscriber) {
        bus.unregister(subscriber);
    }

    @Override
    public void unregister(@NonNull Predicate<EventSubscriber<?>> predicate) {
        bus.unregister(predicate);
    }

    @Override
    public void unregisterAll() {
        bus.unregisterAll();
    }

    @Override
    public <T extends DEEvent> boolean hasSubscribers(@NonNull Class<T> clazz) {
        return bus.hasSubscribers(clazz);
    }

    @Override
    public @NonNull SetMultimap<Class<?>, EventSubscriber<?>> subscribers() {
        return bus.subscribers();
    }

}