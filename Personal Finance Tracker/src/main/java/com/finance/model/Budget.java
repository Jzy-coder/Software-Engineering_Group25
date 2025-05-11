package com.finance.model;

import java.io.Serializable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Budget implements Serializable {
    private String name;
    private double plannedAmount;
    private double actualAmount;
    private ObservableList<String> plans = FXCollections.observableArrayList();

    public Budget(String name, double plannedAmount, double actualAmount) {
        this.name = name;
        this.plannedAmount = plannedAmount;
        this.actualAmount = actualAmount;
    }

    // Getter & Setter ����
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

    // �ƻ��б�� Getter & Setter
    public ObservableList<String> getPlans() { 
        return plans; 
    }

    public void setPlans(ObservableList<String> plans) {
        this.plans.clear(); // �����ԭ������
        this.plans.addAll(plans); // ���������
    }
}