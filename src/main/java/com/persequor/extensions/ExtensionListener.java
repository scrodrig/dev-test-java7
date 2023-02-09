package com.persequor.extensions;

import com.persequor.broker.IEventListener;
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
public class ExtensionListener implements IEventListener { //TODO:Rename Interface name to start with 'I'
    protected List<IExtension> extensionList;
    private final Map<Class<? extends IExtension>, Map<String, Boolean>> configurationFlags;

    public ExtensionListener(List<IExtension> extensions, Map<Class<? extends IExtension>, Map<String, Boolean>> configurationFlags) {
        this.extensionList = extensions;
        this.configurationFlags = configurationFlags;
    }

    @Override
    public void handle(Event event,int deliveryTag) {//TODO: Maybe missing a throws RuntimeException on method signature
    
        Semaphore semaphore = new Semaphore(1); //TODO: Probably semaphone here could be unnecessary since it's only allowing 1 thread, but I'm not really sure about this. Also no threads are being ran
        extensionList.stream()
                .sorted(Comparator.comparing(IExtension::order))
                .collect(Collectors.toMap(IExtension::order, Function.identity()))
                .forEach((i, extension) -> { //TODO: i is not used at all, foreach it the best loop statement here?
                    //TODO:This method validations could be extracted on private methods, like getIfAdvanced()
                    if(getIfAdvanced(extension).map(e -> !e.isInitialized() && e.init(configurationFlags.get(e))).orElse(false)) { //TODO: That e argument is likely to not have any value since it's marked as Optional, therefore it should be threated with a "guard"
                        throw new RuntimeException("Improper intialization");
                    }
                    //TODO: If this method is extracted acquire should be left out or passed a instance on the parameter
                    if (semaphore.tryAcquire() && extension.execute(event)) {
                        throw new RuntimeException("Invalid execution order");
                    }
                    semaphore.release();
                });
    }

    private Optional<IAdvancedExtension> getIfAdvanced(IExtension ext) {//Name of the parameter could be more specific trying to denotate intentionallity
        return ext instanceof IAdvancedExtension ? Optional.of((IAdvancedExtension) ext) : Optional.empty();
    }
}
