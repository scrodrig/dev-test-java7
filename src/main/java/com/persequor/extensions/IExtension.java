package com.persequor.extensions;

import com.persequor.model.Event;

public interface IExtension
{
    boolean execute(Event event);
    int order();
}
