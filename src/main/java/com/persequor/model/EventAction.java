package com.persequor.model;

public enum EventAction {
    CREATE(0), PACKAGE(1), SEND(2), RECEIVE(3), SOLD(4);

    private final int actionOrder;

    EventAction(int actionOrder) {
        this.actionOrder = actionOrder;
    }

    public int getActionOrder() {
        return actionOrder;
    }
}
