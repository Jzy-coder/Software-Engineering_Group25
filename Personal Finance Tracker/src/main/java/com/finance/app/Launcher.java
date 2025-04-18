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
            // Try to find the lib directory in target/lib first
            File executableDir = new File(System.getProperty("user.dir"));
            File targetLibDir = new File(executableDir, "target/lib");
            File libDir = targetLibDir;
            
            // If target/lib doesn't exist, check lib directory
            if (!targetLibDir.exists()) {
                libDir = new File(executableDir, "lib");
            }
            
            if (libDir.exists()) {
                // Set the module path to the lib directory
                System.setProperty("javafx.module.path", libDir.getAbsolutePath());
                
                // Add JavaFX modules to system properties
                System.setProperty("java.library.path", libDir.getAbsolutePath());
                System.setProperty("path.separator", File.pathSeparator);
                
                // Set up module arguments
                StringBuilder modulePath = new StringBuilder();
                modulePath.append("--module-path=").append(libDir.getAbsolutePath());
                System.setProperty("jdk.module.path", modulePath.toString());
                System.setProperty("javafx.modules", "javafx.controls,javafx.fxml,javafx.graphics");
                
                // Log the configuration for debugging
                System.out.println("JavaFX Configuration:");
                System.out.println("Module Path: " + libDir.getAbsolutePath());
                System.out.println("Library Path: " + System.getProperty("java.library.path"));
                System.out.println("Modules: " + System.getProperty("javafx.modules"));
            } else {
                throw new RuntimeException("JavaFX libraries not found in expected locations: " + 
                    targetLibDir.getAbsolutePath() + " or " + libDir.getAbsolutePath());
            }
        } catch (Exception e) {
            System.err.println("Failed to setup JavaFX modules: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}