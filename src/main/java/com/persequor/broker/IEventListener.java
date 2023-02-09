package com.persequor.broker;

import com.persequor.exceptions.EventServiceErrorException;
import com.persequor.model.Event;
import com.persequor.repository.exceptions.EventRepositoryErrorException;

public interface IEventListener {
	void handle(Event event, int deliveryTag) throws EventRepositoryErrorException, EventServiceErrorException;
}


//TODO:Rename since it's an interface
