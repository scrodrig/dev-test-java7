package com.persequor.broker;

import com.persequor.model.Event;

public interface EventQueue {
	void push(String queue, Event event);
	void acknowledge(int deliveryTag);
	void addListener(String queue, EventListener listener);
}
