package com.finance.model;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javafx.collections.ObservableList;

public class Budget implements Serializable {
    private static final long serialVersionUID = 1L; // 序列化版本号
    private String name;
    private double plannedAmount;
    private double actualAmount;
    private transient List<String> plans = new ArrayList<>(); // 标记为 transient

    // 自定义序列化方法
    private void writeObject(ObjectOutputStream oos) throws IOException {
        oos.defaultWriteObject();
        oos.writeObject(new ArrayList<>(plans)); // 序列化为 ArrayList
    }

    // 自定义反序列化方法
    private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
        ois.defaultReadObject();
        plans = (List<String>) ois.readObject(); // 反序列化为 List
    }


    public Budget(String name, double plannedAmount, double actualAmount) {
        this.name = name;
        this.plannedAmount = plannedAmount;
        this.actualAmount = actualAmount;
    }

    // Getter & Setter 方法
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public double getPlannedAmount() { return plannedAmount; }
    public void setPlannedAmount(double plannedAmount) { 
        this.plannedAmount = plannedAmount; 
    }

    public double getActualAmount() { return actualAmount; }
    public void setActualAmount(double actualAmount) { 
        this.actualAmount = actualAmount; 
    }

    // 计划列表的 Getter & Setter
    public List<String> getPlans() { 
        return plans; 
    }

    public void setPlans(ObservableList<String> plans) {
        this.plans.clear(); // 先清空原有数据
        this.plans.addAll(plans); // 添加新数据
    }
}