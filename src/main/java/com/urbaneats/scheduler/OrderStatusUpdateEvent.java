package com.urbaneats.scheduler;

import org.springframework.context.ApplicationEvent;

public class OrderStatusUpdateEvent extends ApplicationEvent {
    private final Long orderId;

    public OrderStatusUpdateEvent(Object source, Long orderId) {
        super(source);
        this.orderId = orderId;
    }

    public Long getOrderId() {
        return orderId;
    }
}