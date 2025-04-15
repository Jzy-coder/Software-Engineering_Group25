package com.finance.model;

import java.io.Serializable;

public class Budget implements Serializable {
    private double plannedAmount;
    private double actualAmount;

    public Budget(double plannedAmount, double actualAmount) {
        this.plannedAmount = plannedAmount;
        this.actualAmount = actualAmount;
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
}