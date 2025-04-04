package com.finance.event;

import java.util.EventListener;

/**
 * 交易事件监听器接口
 * 实现此接口的类可以接收交易数据变化的通知
 */
public interface TransactionEventListener extends EventListener {
    
    /**
     * 当交易数据发生变化时调用此方法
     * 
     * @param event 交易事件对象
     */
    void onTransactionChanged(TransactionEvent event);
}