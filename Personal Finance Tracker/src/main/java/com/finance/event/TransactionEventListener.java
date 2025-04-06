package com.finance.event;

import java.util.EventListener;

/**
 * Transaction Event Listener Interface
 * Classes implementing this interface can receive notifications of transaction data changes
 */
public interface TransactionEventListener extends EventListener {
    
    /**
     * Called when transaction data changes
     * 
     * @param event Transaction event object
     */
    void onTransactionChanged(TransactionEvent event);
}