package com.persequor;

import com.persequor.broker.EventQueue;
import com.persequor.exceptions.EventServiceException;
import com.persequor.model.Event;


/**
 * Entrypoint for the system
 */
public class EventService {

   private EventQueue eventQueue;

    public EventService(EventQueue eventQueue) {
        this.eventQueue = eventQueue;
    }

    public void handleRequest(Event event) throws EventServiceException {
        eventQueue.push("persist-queue", event);
    }
}
