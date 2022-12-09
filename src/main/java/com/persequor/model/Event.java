package com.persequor.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Event {
    private UUID id;
    private LocalDateTime eventTime;
    private LocalDateTime recordTime;
    private String source;
    private EventAction action;
    private List<String> trackedItemIds = new ArrayList<>();

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public LocalDateTime getEventTime() {
        return eventTime;
    }

    public void setEventTime(LocalDateTime eventTime) {
        this.eventTime = eventTime;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }
    public List<String> getTrackedItemIds() {
        return trackedItemIds;
    }

    public void setTrackedItemIds(List<String> trackedItemIds) {
        this.trackedItemIds = trackedItemIds;
    }

    public LocalDateTime getRecordTime() {
        return recordTime;
    }

    public void setRecordTime(LocalDateTime recordTime) {
        this.recordTime = recordTime;
    }

    public EventAction getAction() {
        return action;
    }

    public void setAction(EventAction action) {
        this.action = action;
    }
}
