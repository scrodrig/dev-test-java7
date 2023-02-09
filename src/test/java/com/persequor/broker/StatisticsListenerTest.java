package com.persequor.broker;

import com.persequor.model.Event;
import com.persequor.model.EventList;
import com.persequor.repository.IStatisticsRepository;
import com.persequor.utils.stream.BatchCollector;
import com.persequor.utils.stream.StreamUtils;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(MockitoJUnitRunner.class)
public class StatisticsListenerTest {
	@Mock
	private IStatisticsRepository repository;
	@Mock
	private IEventQueue eventQueue;
	@Mock
	private Event event;

	private StatisticsListener statisticsListener;

	int deliveryTag = 32;

	List<String> trackerIds = Arrays.asList("1", "2", "3");

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		statisticsListener = new StatisticsListener(eventQueue, repository);
	}

	/// TODO: Implement tests as needed

	@Test
	public void shouldReturnOneElementWhenInvokedWithEmptyList() {
		statisticsListener.handle(event, deliveryTag);
		assertEquals(1, statisticsListener.getEventList().size(), 0.001);
	}

	@Test
	public void shouldReturnLessThanTenElementsWhenInvoked() {
		EventList eventList = generateEvents(5);
		statisticsListener.setEventList(eventList);
		statisticsListener.handle(event, deliveryTag);
		assertTrue(statisticsListener.getEventList().size() < 10);
	}

	
	@Test
	public void shouldReturnLessThanTenElementsWhenInvokedAndNoneProcceeded() {
		EventList eventList = generateEvents(5);
		statisticsListener.setEventList(eventList);
		statisticsListener.handle(event, deliveryTag);
		assertTrue(statisticsListener.getEventList().size() < 10);
		assertEquals(0, statisticsListener.getNumRecordsProcessed(), 0.001);
	}

	@Test
	public void shouldReturnLessThanTenElementsWhenInvokedAndNeverProcceedTheIncomingEvent() {
		EventList eventList = generateEvents(5);
		statisticsListener.setEventList(eventList);
		statisticsListener.handle(event, deliveryTag);
		when(event.getEventTime()).thenReturn(LocalDateTime.now());
		when(event.getTrackedItemIds()).thenReturn(trackerIds);
		assertTrue(statisticsListener.getEventList().size() < 10);
		assertEquals(0, statisticsListener.getNumRecordsProcessed(), 0.001);
		verify(repository, never()).updateStatistics(event.getEventTime().toLocalDate(),
				event.getTrackedItemIds().size());
		verify(eventQueue, never()).acknowledge(deliveryTag);
	}

	@Test
	public void shouldReturnLessThanTenElementsWhenInvokedAndNeverProcceedNoEvent() {
		EventList eventList = generateEvents(5);
		statisticsListener.setEventList(eventList);
		statisticsListener.handle(event, deliveryTag);
		when(event.getEventTime()).thenReturn(LocalDateTime.now());
		when(event.getTrackedItemIds()).thenReturn(trackerIds);
		assertTrue(statisticsListener.getEventList().size() < 10);
		assertEquals(0, statisticsListener.getNumRecordsProcessed(), 0.001);
		for (Event event : eventList) {
			verify(repository, never()).updateStatistics(event.getEventTime().toLocalDate(), event.getTrackedItemIds().size());
		}
		verify(eventQueue, never()).acknowledge(deliveryTag);
	}

	@Test
	public void shouldNeverAcknoledgeEventsOnQueueByDeliveryTagWithEmptyList() {
		statisticsListener.handle(event, deliveryTag);
		verify(eventQueue, never()).acknowledge(deliveryTag);
	}

	@Test
	public void shouldNeverAcknoledgeEventsOnQueueByWrongDeliveryTagWithEmptyList() {
		int wrongDeliveryTag = 33;
		doNothing().when(eventQueue).acknowledge(deliveryTag);
		statisticsListener.handle(event, wrongDeliveryTag);
		verify(eventQueue, never()).acknowledge(deliveryTag);
	}

	@Test
	public void shouldNeverCallUpdateStatisticsWithEmptyList() {
		
		when(event.getEventTime()).thenReturn(LocalDateTime.now());
		when(event.getTrackedItemIds()).thenReturn(trackerIds);
		statisticsListener.handle(event, deliveryTag);
		verify(repository, never()).updateStatistics(event.getEventTime().toLocalDate(),
				event.getTrackedItemIds().size());
	}

	@Test
	public void numRecordsProcessed() {
		List<Integer> input = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
		List<Integer> output = new ArrayList<>();

		BatchCollector<Integer> batchCollector = StreamUtils.batchCollector(3, xs -> output.addAll(xs));

		input.stream()
				.collect(batchCollector);

		assertEquals(10, batchCollector.getNumRecordsProcessed(), 0.001);
	}

	@Test
	public void shouldMatchProccededEventsWithListOfTwentyElements() {
		EventList eventList = generateEvents(19);
		when(event.getEventTime()).thenReturn(LocalDateTime.now());
		when(event.getTrackedItemIds()).thenReturn(trackerIds);
		statisticsListener.setEventList(eventList);
		statisticsListener.handle(event, deliveryTag);
		assertEquals(20, statisticsListener.getNumRecordsProcessed(), 0.001);
	}

	@Test
	public void shouldCallAcknowledgeEventsWithListOfTenElements() {
		EventList eventList = generateEvents(10);
		when(event.getEventTime()).thenReturn(LocalDateTime.now());
		when(event.getTrackedItemIds()).thenReturn(trackerIds);
		statisticsListener.setEventList(eventList);
		statisticsListener.handle(event, deliveryTag);
		verify(eventQueue, atLeast(10)).acknowledge(deliveryTag);
	}

	@Test
	public void shouldCallOneUpdateStatisticsWithListOfTenElements() {
		EventList eventList = generateEvents(10);
		when(event.getEventTime()).thenReturn(LocalDateTime.now().plusDays(1));
		when(event.getTrackedItemIds()).thenReturn(trackerIds);
		statisticsListener.setEventList(eventList);
		statisticsListener.handle(event, deliveryTag);
		verify(repository).updateStatistics(event.getEventTime().toLocalDate(), event.getTrackedItemIds().size());
	}

	@Test
	public void shouldCallSeveralUpdateStatisticsWithListOfTenElements() {
		EventList eventList = generateEvents(10);
		when(event.getEventTime()).thenReturn(LocalDateTime.now().plusDays(1));
		when(event.getTrackedItemIds()).thenReturn(trackerIds);
		statisticsListener.setEventList(eventList);
		statisticsListener.handle(event, deliveryTag);
		for (Event event : eventList) {
			verify(repository).updateStatistics(event.getEventTime().toLocalDate(), event.getTrackedItemIds().size());
		}
	}

	private EventList generateEvents(int numberOfEvents) {
		EventList eventList = new EventList();
		for (int i = 0; i < numberOfEvents; i++) {
			Event generatedEvent = new Event();
			generatedEvent.setEventTime(LocalDateTime.now().minusDays(i));
			generatedEvent.setTrackedItemIds(trackerIds);
			eventList.add(generatedEvent);
		}
		return eventList;
	}
}
