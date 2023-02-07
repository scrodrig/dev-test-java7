package com.persequor.broker;

import com.persequor.model.Event;

public interface EventListener {
	void handle(Event event, int deliveryTag);
}
