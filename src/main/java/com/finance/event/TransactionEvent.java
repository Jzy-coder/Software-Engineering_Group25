package com.finance.event;

import java.util.EventObject;

/**
 * Transaction Data Change Event
 * Used to notify other components in the system when transaction data has changed
 */
public class TransactionEvent extends EventObject {
    
    private final EventType eventType;
    
    /**
     * Event Type Enumeration
     */
public enum EventType {
        ADDED,      // New transaction added
        UPDATED,    // Transaction updated
        DELETED,    // Transaction deleted
        LOADED      // Transaction data loaded
    }
    
    /**
     * Create a new transaction event
     * 
     * @param source Event source object
     * @param eventType Event type
     */
    public TransactionEvent(Object source, EventType eventType) {
        super(source);
        this.eventType = eventType;
    }
    
    /**
     * Get the event type
     * 
     * @return Event type
     */
    public EventType getEventType() {
        return eventType;
    }
}