package com.persequor.broker;

import com.persequor.repository.StatisticsRepository;
import org.junit.Before;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class StatisticsListenerTest {
	@Mock
	private StatisticsRepository repository;
	@Mock
	private EventQueue eventQueue;

	private StatisticsListener statisticsListener;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);

		statisticsListener = new StatisticsListener(eventQueue, repository);
	}

	/// TODO: Implement tests as needed
}
