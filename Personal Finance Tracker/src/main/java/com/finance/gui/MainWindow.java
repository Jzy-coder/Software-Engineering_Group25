package com.finance.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Main window class, serves as the application's main interface
 */
public class MainWindow extends JFrame {
    private JPanel contentPanel;
    private CardLayout cardLayout;
    private JPanel navigationPanel;
    
    public MainWindow() {
        initComponents();
        setupLayout();
    }
    
    private void initComponents() {
        setTitle("个人理财助手");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setPreferredSize(new Dimension(1000, 700));
        
        // Create navigation panel
        navigationPanel = new JPanel();
        navigationPanel.setPreferredSize(new Dimension(200, 0));
        navigationPanel.setBackground(new Color(51, 51, 51));
        navigationPanel.setLayout(new BoxLayout(navigationPanel, BoxLayout.Y_AXIS));
        
        // Create content panel
        contentPanel = new JPanel();
        cardLayout = new CardLayout();
        contentPanel.setLayout(cardLayout);
        
        // Add function buttons and corresponding panels
        addNavigationButton("Income/Expense", "Record your daily income and expenses", createIncomeExpensePanel());
        addNavigationButton("Budget", "Set and track your budget goals", createBudgetPanel());
        addNavigationButton("Financial Analysis", "View your financial statistics and analysis", createAnalysisPanel());
        addNavigationButton("Investment Portfolio", "Manage your investments and assets", createInvestmentPanel());
        addNavigationButton("Settings", "System settings and personal information", createSettingsPanel());
        
        // Set main layout
        setLayout(new BorderLayout());
        add(navigationPanel, BorderLayout.WEST);
        add(contentPanel, BorderLayout.CENTER);
    }
    
    private void addNavigationButton(String text, String tooltip, JPanel panel) {
        JButton button = new JButton(text);
        button.setToolTipText(tooltip);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setMaximumSize(new Dimension(180, 40));
        button.setPreferredSize(new Dimension(180, 40));
        button.setFont(new Font("Microsoft YaHei", Font.PLAIN, 14));
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(51, 51, 51));
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        
        // Add mouse hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(75, 75, 75));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(51, 51, 51));
            }
        });
        
        button.addActionListener(e -> cardLayout.show(contentPanel, text));
        
        // Add button to navigation panel
        navigationPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        navigationPanel.add(button);
        
        // Add panel to content panel
        contentPanel.add(panel, text);
    }
    
    private JPanel createIncomeExpensePanel() {
        JPanel panel = new JPanel();
        panel.setBackground(Color.WHITE);
        panel.add(new JLabel("Income/Expense feature under development..."));
        return panel;
    }
    
    private JPanel createBudgetPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(Color.WHITE);
        panel.add(new JLabel("Budget management feature under development..."));
        return panel;
    }
    
    private JPanel createAnalysisPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(Color.WHITE);
        panel.add(new JLabel("Financial analysis feature under development..."));
        return panel;
    }
    
    private JPanel createInvestmentPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(Color.WHITE);
        panel.add(new JLabel("Investment portfolio feature under development..."));
        return panel;
    }
    
    private JPanel createSettingsPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(Color.WHITE);
        panel.add(new JLabel("Settings feature under development..."));
        return panel;
    }
    
    private void setupLayout() {
        pack();
        setLocationRelativeTo(null);
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(
                    UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            
            MainWindow window = new MainWindow();
            window.setVisible(true);
        });
    }
}