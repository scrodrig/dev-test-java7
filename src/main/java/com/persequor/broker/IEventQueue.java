package com.persequor.broker;

import com.persequor.model.Event;

public interface IEventQueue {
	void push(String queue, Event event);
	void acknowledge(int deliveryTag);
	void addListener(String queue, IEventListener listener);
}

//TODO:Rename since it's an interface
