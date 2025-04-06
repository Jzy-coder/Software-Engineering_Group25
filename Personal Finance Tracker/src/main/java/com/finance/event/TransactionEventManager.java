package com.finance.event;

import java.util.ArrayList;
import java.util.List;

/**
 * Transaction Event Manager
 * Responsible for managing transaction event listener registration and event dispatching
 */
public class TransactionEventManager {
    
    private static final TransactionEventManager INSTANCE = new TransactionEventManager();
    private final List<TransactionEventListener> listeners = new ArrayList<>();
    
    /**
     * Private constructor to prevent external instantiation
     */
    private TransactionEventManager() {
    }
    
    /**
     * Get the singleton instance of the event manager
     * 
     * @return Event manager instance
     */
    public static TransactionEventManager getInstance() {
        return INSTANCE;
    }
    
    /**
     * Add event listener
     * 
     * @param listener Listener to be added
     */
    public void addTransactionEventListener(TransactionEventListener listener) {
        if (listener != null && !listeners.contains(listener)) {
            listeners.add(listener);
        }
    }
    
    /**
     * Remove event listener
     * 
     * @param listener Listener to be removed
     */
    public void removeTransactionEventListener(TransactionEventListener listener) {
        listeners.remove(listener);
    }
    
    /**
     * Fire transaction event, notify all listeners
     * 
     * @param event Event to be fired
     */
    public void fireTransactionEvent(TransactionEvent event) {
        for (TransactionEventListener listener : new ArrayList<>(listeners)) {
            listener.onTransactionChanged(event);
        }
    }
}