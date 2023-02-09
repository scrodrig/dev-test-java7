package com.persequor;

import com.persequor.broker.IEventQueue;
import com.persequor.exceptions.EventServiceException;
import com.persequor.model.Event;


/**
 * Entrypoint for the system
 */
public class EventService {

   private IEventQueue eventQueue;

    public EventService(IEventQueue eventQueue) {
        this.eventQueue = eventQueue;
    }

    public void handleRequest(Event event) throws EventServiceException {
        eventQueue.push("persist-queue", event);
    }
}
