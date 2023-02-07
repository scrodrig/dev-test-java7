package com.persequor.repository;

import com.persequor.model.Event;
import com.persequor.model.EventList;
import com.persequor.repository.exceptions.EventRepositoryErrorException;

import java.util.Collection;

/**
 * Do not implement the repository, somebody else will do that. You may change the interface though
 */
public interface EventRepository {
    void persist(Event event) throws EventRepositoryErrorException;

    EventList get(Collection<String> trackedIds);
}
