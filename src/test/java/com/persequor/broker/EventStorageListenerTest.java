package com.persequor.broker;

import com.persequor.repository.EventRepository;
import org.junit.Before;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class EventStorageListenerTest {
	@Mock
	private EventRepository repository;
	@Mock
	private EventQueue eventQueue;

	private EventStorageListener listener;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);

		listener = new EventStorageListener(repository, eventQueue);
	}

	/// TODO: Implement tests as needed
}
