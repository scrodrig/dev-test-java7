package com.persequor.ioc;

import com.persequor.EventService;
import com.persequor.broker.EventListener;
import com.persequor.broker.EventQueue;
import com.persequor.broker.EventStorageListener;
import com.persequor.broker.StatisticsListener;
import com.persequor.repository.EventRepository;
import com.persequor.repository.StatisticsRepository;

public class Ioc {
	public void initializeQueues() {
		EventQueue eventQueue = getEventQueue();
		eventQueue.addListener("persist-queue", getEventStorageListener());
		eventQueue.addListener("statistics", getStatisticsListener());
		eventQueue.addListener("subscription", getSubscriptionListener());
	}

	public EventService getEventService() {
		return new EventService(getEventQueue());
	}

	public EventStorageListener getEventStorageListener() {
		return new EventStorageListener(getEventRepository(), getEventQueue());
	}

	public StatisticsListener getStatisticsListener() {
		return new StatisticsListener(getEventQueue(), getStatisticsRepository());
	}

	public EventListener getSubscriptionListener() {
		return null; // This listener is currently out of scope
	}

	private StatisticsRepository getStatisticsRepository() {
		return null; // In the real system an actual repository implementation is returned, but for the exercise we just pretend
	}

	private EventRepository getEventRepository() {
		return null; // In the real system an actual repository implementation is returned, but for the exercise we just pretend
	}

	private EventQueue getEventQueue() {
		return null; // In the real system an actual queue implementation is returned, but for the exercise we just pretend
	}
}
