package com.finance.model;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javafx.collections.ObservableList;

/**
 * Represents a budget with planned and actual amounts, and associated plans.
 * This class is Serializable to allow saving and loading budget data.
 */
public class Budget implements Serializable {
    private static final long serialVersionUID = 1L; 
    private String name;
    private double plannedAmount;
    private double actualAmount;
    private transient List<String> plans = new ArrayList<>(); 

    /**
     * Custom serialization method to handle the transient plans list.
     *
     * @param oos The ObjectOutputStream to write to.
     * @throws IOException If an I/O error occurs during writing.
     */
    private void writeObject(ObjectOutputStream oos) throws IOException {
        oos.defaultWriteObject();
        oos.writeObject(new ArrayList<>(plans)); 
    }

    /**
     * Custom deserialization method to handle the transient plans list.
     *
     * @param ois The ObjectInputStream to read from.
     * @throws IOException If an I/O error occurs during reading.
     * @throws ClassNotFoundException If the class of a serialized object could not be found.
     */
    @SuppressWarnings("unchecked")
    private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
        ois.defaultReadObject();
        plans = (List<String>) ois.readObject(); 
    }


    /**
     * Constructs a new Budget instance.
     *
     * @param name The name of the budget.
     * @param plannedAmount The planned amount for the budget.
     * @param actualAmount The actual amount spent/earned for the budget.
     */
    public Budget(String name, double plannedAmount, double actualAmount) {
        this.name = name;
        this.plannedAmount = plannedAmount;
        this.actualAmount = actualAmount;
    }

    /**
     * Gets the name of the budget.
     *
     * @return The budget name.
     */
    public String getName() { return name; }
    /**
     * Sets the name of the budget.
     *
     * @param name The new budget name.
     */
    public void setName(String name) { this.name = name; }

    /**
     * Gets the planned amount for the budget.
     *
     * @return The planned amount.
     */
    public double getPlannedAmount() { return plannedAmount; }
    /**
     * Sets the planned amount for the budget.
     *
     * @param plannedAmount The new planned amount.
     */
    public void setPlannedAmount(double plannedAmount) { 
        this.plannedAmount = plannedAmount; 
    }

    /**
     * Gets the actual amount spent/earned for the budget.
     *
     * @return The actual amount.
     */
    public double getActualAmount() { return actualAmount; }
    /**
     * Sets the actual amount spent/earned for the budget.
     *
     * @param actualAmount The new actual amount.
     */
    public void setActualAmount(double actualAmount) { 
        this.actualAmount = actualAmount; 
    }

    // Plans list Getter & Setter
    /**
     * Gets the list of plans associated with the budget.
     *
     * @return The list of plans.
     */
    public List<String> getPlans() { 
        return plans; 
    }

    /**
     * Sets the list of plans for the budget from an ObservableList.
     *
     * @param plans The ObservableList of plans to set.
     */
    public void setPlans(ObservableList<String> plans) {
        this.plans.clear(); // Clear original data
        this.plans.addAll(plans); // Add new data
    }
    
    // Overloaded method to accept List<String>
    /**
     * Sets the list of plans for the budget from a List.
     *
     * @param plans The List of plans to set.
     */
    public void setPlans(List<String> plans) {
        this.plans.clear();
        this.plans.addAll(plans);
    }
}