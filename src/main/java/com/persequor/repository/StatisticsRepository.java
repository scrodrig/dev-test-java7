package com.persequor.repository;

import java.time.LocalDate;

/**
 * Do not implement the repository, somebody else will do that. You may change the interface though
 */
public interface StatisticsRepository {
	/**
	 * @param day Day of the Event
	 * @param numberOfTrackedItemIds Number of tracked item ids
	 */
	void updateStatistics(LocalDate day, int numberOfTrackedItemIds);
}
