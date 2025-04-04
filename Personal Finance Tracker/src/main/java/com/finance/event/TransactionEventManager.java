package com.finance.event;

import java.util.ArrayList;
import java.util.List;

/**
 * 交易事件管理器
 * 负责管理交易事件的监听器注册和事件分发
 */
public class TransactionEventManager {
    
    private static final TransactionEventManager INSTANCE = new TransactionEventManager();
    private final List<TransactionEventListener> listeners = new ArrayList<>();
    
    /**
     * 私有构造函数，防止外部实例化
     */
    private TransactionEventManager() {
    }
    
    /**
     * 获取事件管理器的单例实例
     * 
     * @return 事件管理器实例
     */
    public static TransactionEventManager getInstance() {
        return INSTANCE;
    }
    
    /**
     * 添加事件监听器
     * 
     * @param listener 要添加的监听器
     */
    public void addTransactionEventListener(TransactionEventListener listener) {
        if (listener != null && !listeners.contains(listener)) {
            listeners.add(listener);
        }
    }
    
    /**
     * 移除事件监听器
     * 
     * @param listener 要移除的监听器
     */
    public void removeTransactionEventListener(TransactionEventListener listener) {
        listeners.remove(listener);
    }
    
    /**
     * 触发交易事件，通知所有监听器
     * 
     * @param event 要触发的事件
     */
    public void fireTransactionEvent(TransactionEvent event) {
        for (TransactionEventListener listener : new ArrayList<>(listeners)) {
            listener.onTransactionChanged(event);
        }
    }
}