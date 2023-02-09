package com.persequor.broker;

import com.persequor.broker.exeptions.EventValidationErrorException;
import com.persequor.exceptions.EventServiceErrorException;
import com.persequor.model.Event;
import com.persequor.model.EventList;
import com.persequor.repository.IEventRepository;
import com.persequor.repository.exceptions.EventRepositoryErrorException;

public class EventStorageListener implements IEventListener {
	private final IEventRepository repository;
	private IEventQueue eventQueue;

	public EventStorageListener(IEventRepository repository, IEventQueue eventQueue) {
		this.repository = repository;
		this.eventQueue = eventQueue;
	}

	@Override
	public void handle(Event incomingEvent, int deliveryTag) throws EventServiceErrorException {
		// - As part of this, consider how you think the events should be sorted, and
		// why
		/*
		 * Answer: EventList could be sorted in a decresent manner since could become
		 * handy to have newest events available or assuming registration (as the
		 * instant when are added to the system) could be useful have latest added
		 * events into the system. However since for validation, I decided to use lambda
		 * expressions (filter) is not mandatory to get an ordered list to begin with
		 */

		EventList eventList = repository.get(incomingEvent.getTrackedItemIds());
		if (eventList.isEmpty()) {
			// No validation needed
			this.storeAndRegistration(incomingEvent, deliveryTag);
			return;
		}

		if (isANewEvent(eventList, incomingEvent)) {
			this.storeAndRegistration(incomingEvent, deliveryTag);
			return;
		}
		// - Handle validation errors the way you believe it should work
		throw new EventValidationErrorException();

	}

	private boolean isANewEvent(final EventList list, final Event incomingEvent) {
		// TODO: Validate that the incoming event is not an earlier event than the ones
		// existing in the database (repository)
		return !list.stream().filter(e -> e.getEventTime().isAfter(incomingEvent.getEventTime())).findAny().isPresent();
	}

	private void storeAndRegistration(Event incomingEvent, int deliveryTag) throws EventRepositoryErrorException {
		this.storeEventInRepository(incomingEvent);
		this.passEventsToQueues(incomingEvent);
		this.acknowledgeEventsWithTag(deliveryTag);
	}

	private void storeEventInRepository(Event incomingEvent) throws EventRepositoryErrorException {
		// TODO: Store event
		this.repository.persist(incomingEvent);
	}

	private void acknowledgeEventsWithTag(int deliveryTag) {
		// TODO: Acknowledge processed events
		this.eventQueue.acknowledge(deliveryTag);
	}

	private void passEventsToQueues(Event incomingEvent) {
		// TODO: Pass event on to "statistics" queue and to "subscription" queue
		this.eventQueue.push("statistics", incomingEvent);
		this.eventQueue.push("subscription", incomingEvent);
	}

}
