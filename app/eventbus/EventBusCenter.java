package eventbus;

import java.util.concurrent.Executors;

import com.google.common.eventbus.AsyncEventBus;

public class EventBusCenter {

    private static AsyncEventBus eventBus = new AsyncEventBus(
            Executors.newFixedThreadPool(5)
    );

    public static void register(Object object) {
        eventBus.register(object);
    }

    public static void unRegister(Object object) {
        eventBus.unregister(object);
    }

    public static void post(Object event) {
        eventBus.post(event);
    }
}
