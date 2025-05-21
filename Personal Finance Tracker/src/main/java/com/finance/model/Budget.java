package com.finance.model;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javafx.collections.ObservableList;

public class Budget implements Serializable {
    private static final long serialVersionUID = 1L; 
    private String name;
    private double plannedAmount;
    private double actualAmount;
    private transient List<String> plans = new ArrayList<>(); 

    private void writeObject(ObjectOutputStream oos) throws IOException {
        oos.defaultWriteObject();
        oos.writeObject(new ArrayList<>(plans)); 
    }

    @SuppressWarnings("unchecked")
    private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
        ois.defaultReadObject();
        plans = (List<String>) ois.readObject(); 
    }


    public Budget(String name, double plannedAmount, double actualAmount) {
        this.name = name;
        this.plannedAmount = plannedAmount;
        this.actualAmount = actualAmount;
    }

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

    // Plans list Getter & Setter
    public List<String> getPlans() { 
        return plans; 
    }

    public void setPlans(ObservableList<String> plans) {
        this.plans.clear(); // Clear original data
        this.plans.addAll(plans); // Add new data
    }
    
    // Overloaded method to accept List<String>
    public void setPlans(List<String> plans) {
        this.plans.clear();
        this.plans.addAll(plans);
    }
}