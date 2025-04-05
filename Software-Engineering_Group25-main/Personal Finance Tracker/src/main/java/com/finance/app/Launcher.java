package com.finance.app;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Launcher class that serves as the main entry point for the application.
 * This class is used to properly handle JavaFX modules when packaged as an executable.
 */
public class Launcher {
    
    /**
     * Main method that launches the JavaFX application.
     * This method sets up the JavaFX module path and then launches the actual application.
     * 
     * @param args command line arguments
     */
    public static void main(String[] args) {
        try {
            // Set up JavaFX module path if running from executable
            setupJavaFXModules();
            
            // Launch the actual JavaFX application
            FinanceApplication.main(args);
        } catch (Exception e) {
            System.err.println("Error launching application: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Sets up JavaFX modules by determining the correct module path.
     * This is necessary when running from an executable where the JavaFX libraries
     * might be in a different location than during development.
     */
    private static void setupJavaFXModules() {
        try {
            // Try to find the lib directory relative to the executable
            File executableDir = new File(System.getProperty("user.dir"));
            File libDir = new File(executableDir, "lib");
            
            if (!libDir.exists()) {
                // If lib directory doesn't exist at the executable location,
                // check if we're running from the target directory
                File targetDir = new File(executableDir.getParentFile(), "lib");
                if (targetDir.exists()) {
                    libDir = targetDir;
                }
            }
            
            if (libDir.exists()) {
                // Set the module path to the lib directory
                System.setProperty("javafx.module.path", libDir.getAbsolutePath());
                
                // Add JavaFX modules
                List<String> jvmArgs = new ArrayList<>();
                jvmArgs.add("--module-path");
                jvmArgs.add(libDir.getAbsolutePath());
                jvmArgs.add("--add-modules=javafx.controls,javafx.fxml,javafx.graphics");
                
                // Log the module path for debugging
                System.out.println("JavaFX module path: " + libDir.getAbsolutePath());
            } else {
                System.out.println("JavaFX lib directory not found. Using default module path.");
            }
        } catch (Exception e) {
            System.err.println("Error setting up JavaFX modules: " + e.getMessage());
            e.printStackTrace();
        }
    }
}