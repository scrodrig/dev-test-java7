package com.persequor.broker;

import com.persequor.model.Event;
import com.persequor.repository.StatisticsRepository;

public class StatisticsListener implements EventListener {
	private final EventQueue eventQueue;
	private final StatisticsRepository statisticsRepository;

	public StatisticsListener(EventQueue eventQueue, StatisticsRepository statisticsRepository) {
		this.eventQueue = eventQueue;
		this.statisticsRepository = statisticsRepository;
	}

	@Override
	public void handle(Event event, int deliveryTag) {
		// TODO: Collect up to 10 events in a batch before processing
		// TODO: call updateStatistics as few times as needed
		// TODO: acknowledge processed events
	}
}
