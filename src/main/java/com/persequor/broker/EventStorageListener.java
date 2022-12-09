package com.persequor.broker;

import com.persequor.model.Event;
import com.persequor.repository.EventRepository;

public class EventStorageListener
	implements EventListener
{
	private final EventRepository repository;
	private EventQueue eventQueue;

	public EventStorageListener(
			EventRepository repository
			, EventQueue eventQueue
	) {
		this.repository = repository;
		this.eventQueue = eventQueue;
	}

	@Override
	public void handle(Event incomingEvent, int deliveryTag) {
		//TODO: Validate that the incoming event is not an earlier event than the ones existing in the database (repository)
		//      - As part of this, consider how you think the events should be sorted, and why.
		//      - Handle validation errors the way you believe it should work
		//TODO: Store event
		//TODO: Pass event on to "statistics" queue and to "subscription" queue
		//TODO: Acknowledge processed events
	}
}
