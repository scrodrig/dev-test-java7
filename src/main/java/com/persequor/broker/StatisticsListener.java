package com.persequor.broker;

import java.util.Objects;

import com.persequor.model.Event;
import com.persequor.model.EventList;
import com.persequor.repository.IStatisticsRepository;
import com.persequor.utils.stream.StreamUtils;
import com.persequor.utils.stream.BatchCollector;

public class StatisticsListener implements IEventListener {
	private final IEventQueue eventQueue;
	private final IStatisticsRepository statisticsRepository;
	private EventList eventList = new EventList();
	BatchCollector<Event> batchCollector;

	public StatisticsListener(IEventQueue eventQueue, IStatisticsRepository statisticsRepository) {
		this.eventQueue = eventQueue;
		this.statisticsRepository = statisticsRepository;
	}

	@Override
	public void handle(Event event, int deliveryTag) {
		// TODO: Collect up to 10 events in a batch before processing
		if (this.eventList.size() < 10) {
			this.eventList.add(event);
			return;
		}

		this.eventList.add(event);

		this.batchCollector = StreamUtils.batchCollector(3,
				xs -> xs.forEach(e -> this.proccessEvents(e, deliveryTag)));

		this.eventList.stream().collect(batchCollector);
	}

	private void proccessEvents(Event event, int deliveryTag) {
		this.updateStatistics(event);
		this.acknowledgeEventsWithTag(deliveryTag);
	}

	private void acknowledgeEventsWithTag(int deliveryTag) {
		// TODO: Acknowledge processed events
		this.eventQueue.acknowledge(deliveryTag);
	}

	private void updateStatistics(Event event) {
		// TODO: call updateStatistics as few times as needed
		this.statisticsRepository.updateStatistics(event.getEventTime().toLocalDate(), event.getTrackedItemIds().size());
	}

	public long getNumRecordsProcessed() {
		return Objects.nonNull(this.batchCollector) ? this.batchCollector.getNumRecordsProcessed() : 0;
	}

	public void setEventList(EventList eventList) {
		this.eventList = eventList;
	}

	public EventList getEventList() {
		return eventList;
	}
}
