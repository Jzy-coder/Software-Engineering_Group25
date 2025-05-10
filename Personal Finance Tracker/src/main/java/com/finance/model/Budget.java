package com.finance.model;

import java.io.Serializable;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Budget implements Serializable {
    private String name;
    private double plannedAmount;
    private double actualAmount;

    public Budget(String name, double plannedAmount, double actualAmount) {
        this.name = name;
        this.plannedAmount = plannedAmount;
        this.actualAmount = actualAmount;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    public double getPlannedAmount() {
        return plannedAmount;
    }

    public void setPlannedAmount(double plannedAmount) {
        this.plannedAmount = plannedAmount;
    }

    public double getActualAmount() {
        return actualAmount;
    }

    public void setActualAmount(double actualAmount) {
        this.actualAmount = actualAmount;
    }
    // 新增计划列表
    private ObservableList<String> plans = FXCollections.observableArrayList();

    public ObservableList<String> getPlans() {
        return plans;
    }
}