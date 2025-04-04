package com.finance.event;

import java.util.EventObject;

/**
 * 交易数据变化事件
 * 用于通知系统中的其他组件交易数据已经发生变化
 */
public class TransactionEvent extends EventObject {
    
    private final EventType eventType;
    
    /**
     * 事件类型枚举
     */
    public enum EventType {
        ADDED,      // 添加了新交易
        UPDATED,    // 更新了交易
        DELETED,    // 删除了交易
        LOADED      // 加载了交易数据
    }
    
    /**
     * 创建一个新的交易事件
     * 
     * @param source 事件源对象
     * @param eventType 事件类型
     */
    public TransactionEvent(Object source, EventType eventType) {
        super(source);
        this.eventType = eventType;
    }
    
    /**
     * 获取事件类型
     * 
     * @return 事件类型
     */
    public EventType getEventType() {
        return eventType;
    }
}