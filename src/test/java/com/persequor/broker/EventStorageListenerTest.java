package com.persequor.broker;

import com.persequor.broker.exeptions.EventValidationErrorException;
import com.persequor.exceptions.EventServiceErrorException;
import com.persequor.model.Event;
import com.persequor.model.EventList;
import com.persequor.repository.IEventRepository;
import com.persequor.repository.exceptions.EventRepositoryErrorException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;

import static org.mockito.Matchers.any;

@RunWith(MockitoJUnitRunner.class)
public class EventStorageListenerTest {
	@Mock
	private IEventRepository repository;
	@Mock
	private IEventQueue eventQueue;
	@Mock
	private Event event;
	@Mock
	private Event event2;

	// @InjectMocks
	private EventStorageListener listener;

	@Before
	public void setup() throws EventRepositoryErrorException {
		MockitoAnnotations.initMocks(this);
		when(repository.get(any())).thenReturn(new EventList());
		listener = new EventStorageListener(repository, eventQueue);
	}

	/// TODO: Implement tests as needed

	int deliveryTag = 32;

	@Test
	public void shouldVerifyPersistStorageOnRepository() throws EventServiceErrorException{
		doNothing().when(repository).persist(event);
		listener.handle(event, deliveryTag);
		verify(repository).persist(event);
	}

	@Test
	public void shouldVerifyAdditionToSubscription() throws EventServiceErrorException {
		doNothing().when(eventQueue).push("subscription", event);
		listener.handle(event, deliveryTag);
		verify(eventQueue).push("subscription", event);
	}

	@Test
	public void shouldVerifyAdditionToStatistics() throws EventServiceErrorException {
		doNothing().when(eventQueue).push("statistics", event);
		listener.handle(event, deliveryTag);
		verify(eventQueue).push("statistics", event);
	}

	@Test
	public void shouldVerifyNoOtherAdditionCanHappen() throws EventServiceErrorException {
		doNothing().when(eventQueue).push("anyOtherStadistic", event);
		listener.handle(event, deliveryTag);
		verify(eventQueue, never()).push("anyOtherStadistic", event);
	}

	@Test
	public void shouldNeverAcknoledgeEventsOnQueueByWrongDeliveryTag() throws EventServiceErrorException {
		int wrongDeliveryTag = 33;
		doNothing().when(eventQueue).acknowledge(deliveryTag);
		listener.handle(event, wrongDeliveryTag);
		verify(eventQueue, never()).acknowledge(deliveryTag);
	}

	@Test
	public void shouldAcknoledgeEventsOnQueueByDeliveryTag() throws EventServiceErrorException {
		doNothing().when(eventQueue).acknowledge(deliveryTag);
		listener.handle(event, deliveryTag);
		verify(eventQueue).acknowledge(deliveryTag);
	}

	@Test
	public void shouldAcceptAnEventInAEmptyRepository() throws EventServiceErrorException {
		when(repository.get(any())).thenReturn(new EventList());
		listener.handle(event, deliveryTag);
		verify(repository).persist(event);
		verify(eventQueue).push("subscription", event);
		verify(eventQueue).push("statistics", event);
		verify(eventQueue).acknowledge(deliveryTag);
	}

	@Test
	public void shouldAcceptAnEventAsANewEvent() throws EventServiceErrorException {
		EventList eventList = new EventList();
		Event olderEvent = new Event();
		olderEvent.setEventTime(LocalDateTime.now().minusDays(1));
		eventList.add(olderEvent);
		when(event.getEventTime()).thenReturn(LocalDateTime.now());
		when(repository.get(any())).thenReturn(eventList);
		listener.handle(event, deliveryTag);
		verify(repository).persist(event);
		verify(eventQueue).push("subscription", event);
		verify(eventQueue).push("statistics", event);
		verify(eventQueue).acknowledge(deliveryTag);
	}

	@Test
	public void shouldAcceptAnEventAsANewEventWithMoreThanOneEvent() throws EventServiceErrorException {
		EventList eventList = new EventList();
		Event olderEvent = new Event();
		Event olderEventTwo = new Event();
		olderEvent.setEventTime(LocalDateTime.now().minusDays(1));
		olderEventTwo.setEventTime(LocalDateTime.now().minusDays(2));
		eventList.add(olderEvent);
		eventList.add(olderEventTwo);
		when(event.getEventTime()).thenReturn(LocalDateTime.now());
		when(repository.get(any())).thenReturn(eventList);
		listener.handle(event, deliveryTag);
		verify(repository).persist(event);
		verify(eventQueue).push("subscription", event);
		verify(eventQueue).push("statistics", event);
		verify(eventQueue).acknowledge(deliveryTag);
	}

	@Test(expected=EventValidationErrorException.class)
	public void shouldnotAcceptAnEventWhenTheEventIsOlder() throws EventServiceErrorException {
		EventList eventList = new EventList();
		Event newestEvent = new Event();
		newestEvent.setEventTime(LocalDateTime.now());
		eventList.add(newestEvent);
		when(event.getEventTime()).thenReturn(LocalDateTime.now().minusDays(1));
		when(repository.get(any())).thenReturn(eventList);
		listener.handle(event, deliveryTag);
		verify(repository, never()).persist(event);
		verify(eventQueue, never()).push("subscription", event);
		verify(eventQueue, never()).push("statistics", event);
		verify(eventQueue, never()).acknowledge(deliveryTag);
	}

	@Test(expected=EventValidationErrorException.class)
	public void shouldnotAcceptAnEventWhenTheEventIsOlderThanOneEventAndNewerThanOther()
			throws EventServiceErrorException {
		EventList eventList = new EventList();
		Event newestEvent = new Event();
		Event oldestEvent = new Event();
		newestEvent.setEventTime(LocalDateTime.now());
		oldestEvent.setEventTime(LocalDateTime.now().minusDays(2));
		eventList.add(newestEvent);
		eventList.add(oldestEvent);
		when(event.getEventTime()).thenReturn(LocalDateTime.now().minusDays(1));
		when(repository.get(any())).thenReturn(eventList);
		listener.handle(event, deliveryTag);
		verify(repository, never()).persist(event);
		verify(eventQueue, never()).push("subscription", event);
		verify(eventQueue, never()).push("statistics", event);
		verify(eventQueue, never()).acknowledge(deliveryTag);
	}

}
