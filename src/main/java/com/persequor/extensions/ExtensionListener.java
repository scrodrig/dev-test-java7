package com.persequor.extensions;

import com.persequor.broker.EventListener;
import com.persequor.model.Event;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Semaphore;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * This class contains many parts worthy of a review comment. As part of your review, consider which things
 * you think are most important to address.
 *
 * Please add your review as inline TODO: comments
 */
public class ExtensionListener implements EventListener {
    protected List<IExtension> extensionList;
    private final Map<Class<? extends IExtension>, Map<String, Boolean>> configurationFlags;

    public ExtensionListener(List<IExtension> extensions, Map<Class<? extends IExtension>, Map<String, Boolean>> configurationFlags) {
        this.extensionList = extensions;
        this.configurationFlags = configurationFlags;
    }

    @Override
    public void handle(Event event,int deliveryTag)
    {
        Semaphore semaphore = new Semaphore(1);
        extensionList.stream()
                .sorted(Comparator.comparing(IExtension::order))
                .collect(Collectors.toMap(IExtension::order, Function.identity()))
                .forEach((i, extension) -> {
                    if(getIfAdvanced(extension).map(e -> !e.isInitialized() && e.init(configurationFlags.get(e))).orElse(false)) {
                        throw new RuntimeException("Improper intialization");
                    }

                    if (semaphore.tryAcquire() && extension.execute(event)) {
                        throw new RuntimeException("Invalid execution order");
                    }
                    semaphore.release();
                });
    }

    private Optional<IAdvancedExtension> getIfAdvanced(IExtension ext) {
        return ext instanceof IAdvancedExtension ? Optional.of((IAdvancedExtension) ext) : Optional.empty();
    }
}
