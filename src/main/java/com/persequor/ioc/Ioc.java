package com.persequor.ioc;

import com.persequor.EventService;
import com.persequor.broker.IEventListener;
import com.persequor.broker.IEventQueue;
import com.persequor.broker.EventStorageListener;
import com.persequor.broker.StatisticsListener;
import com.persequor.repository.IEventRepository;
import com.persequor.repository.IStatisticsRepository;

public class Ioc {
	public void initializeQueues() {
		IEventQueue eventQueue = getEventQueue();
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

	public IEventListener getSubscriptionListener() {
		return null; // This listener is currently out of scope
	}

	private IStatisticsRepository getStatisticsRepository() {
		return null; // In the real system an actual repository implementation is returned, but for the exercise we just pretend
	}

	private IEventRepository getEventRepository() {
		return null; // In the real system an actual repository implementation is returned, but for the exercise we just pretend
	}

	private IEventQueue getEventQueue() {
		return null; // In the real system an actual queue implementation is returned, but for the exercise we just pretend
	}
}
