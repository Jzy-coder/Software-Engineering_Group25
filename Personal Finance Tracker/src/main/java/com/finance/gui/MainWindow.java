package com.finance.gui;

import javax.swing.*;
import java.awt.*;

/**
 * 主窗口类，作为应用程序的主界面
 */
public class MainWindow extends JFrame {
    
    public MainWindow() {
        initComponents();
        setupLayout();
    }
    
    private void initComponents() {
        setTitle("个人理财助手");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setPreferredSize(new Dimension(800, 600));
        
        // TODO: 添加菜单栏、工具栏和主要面板
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