package com.finance.gui;

import com.finance.view.LoginFrame;
import com.toedter.calendar.JCalendar;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.chart.ChartRenderingInfo;
import org.jfree.chart.entity.ChartEntity;
import org.jfree.chart.entity.EntityCollection;
import org.jfree.chart.entity.PieSectionEntity;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.DecimalFormat;
import java.awt.geom.Point2D;

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
        panel.setLayout(new BorderLayout());

        // 创建按钮面板
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setLayout(new FlowLayout(FlowLayout.LEFT));

        // 创建图表面板
        JPanel chartPanel = new JPanel(new BorderLayout());
        chartPanel.setBackground(Color.WHITE);
        chartPanel.setPreferredSize(new Dimension(500, 400));

        // 创建Model下拉框
        String[] modelOptions = {"TIME", "AMOUNT", "TRANSATION"};
        JComboBox<String> modelComboBox = new JComboBox<>(modelOptions);
        modelComboBox.setFont(new Font("Microsoft YaHei", Font.PLAIN, 14));
        modelComboBox.setPreferredSize(new Dimension(120, 35));
        buttonPanel.add(modelComboBox);
        buttonPanel.add(Box.createRigidArea(new Dimension(20, 0)));

        // 创建Income/Outcome单选按钮组
        JRadioButton incomeRadio = new JRadioButton("Income");
        JRadioButton outcomeRadio = new JRadioButton("Outcome");
        ButtonGroup radioGroup = new ButtonGroup();
        radioGroup.add(incomeRadio);
        radioGroup.add(outcomeRadio);

        JPanel radioPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        radioPanel.setBackground(Color.WHITE);
        radioPanel.add(incomeRadio);
        radioPanel.add(outcomeRadio);
        buttonPanel.add(radioPanel);
        buttonPanel.add(Box.createRigidArea(new Dimension(20, 0)));

        // 创建日期选择按钮和日期显示标签
        JButton dateButton = new JButton("Time");
        dateButton.setFont(new Font("Microsoft YaHei", Font.PLAIN, 14));
        dateButton.setPreferredSize(new Dimension(100, 35));

        // 创建日期显示标签
        JLabel dateLabel = new JLabel();
        dateLabel.setFont(new Font("Arial", Font.BOLD, 16));
        dateLabel.setHorizontalAlignment(SwingConstants.CENTER);
        dateLabel.setPreferredSize(new Dimension(200, 30));

        dateButton.addActionListener(e -> {
            JDialog dialog = new JDialog();
            dialog.setTitle("Select Date");
            dialog.setModal(true);
            dialog.setLayout(new FlowLayout());

            // 设置日历为英文界面
            JCalendar calendar = new JCalendar();
            calendar.setLocale(new java.util.Locale("en", "US"));
            dialog.add(calendar);

            JButton confirmButton = new JButton("Confirm");
            confirmButton.addActionListener(e1 -> {
                java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd");
                dateLabel.setText(sdf.format(calendar.getDate()));
                dialog.dispose();
            });
            dialog.add(confirmButton);

            dialog.pack();
            dialog.setLocationRelativeTo(null);
            dialog.setVisible(true);
        });
        buttonPanel.add(dateButton);
        buttonPanel.add(dateLabel);

        // 添加内容更新逻辑
        ActionListener contentUpdateListener = e -> {
            panel.remove(1);
            JPanel contentPanel = new JPanel();
            contentPanel.setBackground(Color.WHITE);
            
            String selectedModel = (String) modelComboBox.getSelectedItem();
            String type = incomeRadio.isSelected() ? "Income" : "Outcome";
            String date = dateButton.getText();
            
            // 创建示例数据（后续需要替换为真实数据）
            DefaultPieDataset dataset = new DefaultPieDataset();
            if ("Income".equals(type)) {
                dataset.setValue("工资收入", 60.0);
                dataset.setValue("投资收益", 20.0);
                dataset.setValue("其他收入", 20.0);
            } else {
                dataset.setValue("日常开支", 40.0);
                dataset.setValue("娱乐消费", 20.0);
                dataset.setValue("教育支出", 25.0);
                dataset.setValue("其他支出", 15.0);
            }

            // 创建饼图
            JFreeChart chart = ChartFactory.createPieChart(
                type + " Distribution",
                dataset,
                true,
                true,
                false
            );

            // 设置饼图样式
            PiePlot plot = (PiePlot) chart.getPlot();
            plot.setBackgroundPaint(Color.WHITE);
            plot.setOutlineVisible(false);
            plot.setLabelGenerator(new StandardPieSectionLabelGenerator("{0}: {1}%", new DecimalFormat("0.0"), new DecimalFormat("0.0")));
            plot.setLabelBackgroundPaint(null);
            plot.setLabelOutlinePaint(null);
            plot.setLabelShadowPaint(null);

            // 创建图表面板
            ChartPanel pieChartPanel = new ChartPanel(chart);
            pieChartPanel.setPreferredSize(new Dimension(400, 300));
            
            // 添加点击事件
            pieChartPanel.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    // 获取点击位置对应的数据项
                    Point p = e.getPoint();
                    if (plot.getDataset().getItemCount() > 0) {
                        ChartRenderingInfo info = pieChartPanel.getChartRenderingInfo();
                        if (info != null) {
                            EntityCollection entities = info.getEntityCollection();
                            ChartEntity entity = entities.getEntity(p.x, p.y);
                            if (entity instanceof PieSectionEntity) {
                                PieSectionEntity psEntity = (PieSectionEntity) entity;
                                String category = plot.getDataset().getKey(psEntity.getSectionIndex()).toString();
                                JOptionPane.showMessageDialog(panel,
                                    "将跳转到" + category + "详细页面",
                                    "Category Selected",
                                    JOptionPane.INFORMATION_MESSAGE);
                            }
                        }
                    }
                }
            });

            // 将图表添加到内容面板
            contentPanel.setLayout(new BorderLayout());
            contentPanel.add(dateLabel, BorderLayout.NORTH);
            contentPanel.add(pieChartPanel, BorderLayout.CENTER);
            
            panel.add(contentPanel, BorderLayout.CENTER);
            panel.revalidate();
            panel.repaint();
        };

        modelComboBox.addActionListener(contentUpdateListener);
        incomeRadio.addActionListener(contentUpdateListener);
        outcomeRadio.addActionListener(contentUpdateListener);
        dateButton.addActionListener(contentUpdateListener);

        // 添加按钮面板到主面板的顶部
        panel.add(buttonPanel, BorderLayout.NORTH);
        
        // 添加空白内容面板
        JPanel initialContent = new JPanel();
        initialContent.setBackground(Color.WHITE);
        panel.add(initialContent, BorderLayout.CENTER);

        return panel;
    }
    
    private JPanel createInvestmentPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(Color.WHITE);
        panel.add(new JLabel("Investment portfolio feature under development..."));
        return panel;
    }
    
    private JPanel createSettingsPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        
        GridBagConstraints gbc = new GridBagConstraints();
        
        // 用户名显示区域 (占1/4)
        JPanel userPanel = new JPanel();
        userPanel.setBackground(Color.WHITE);
        JLabel welcomeLabel = new JLabel("Hi~ " + LoginManager.getCurrentUsername());
        welcomeLabel.setFont(new Font("Microsoft YaHei", Font.BOLD, 24));
        userPanel.add(welcomeLabel);
        
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.weightx = 1.0;
        gbc.weighty = 0.25; // 占1/4高度
        gbc.fill = GridBagConstraints.BOTH;
        panel.add(userPanel, gbc);
        
        // 五个功能按钮面板
        JPanel buttonPanel = new JPanel(new GridLayout(5, 1, 8, 20));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 150, 0, 150));
        
        String[] buttonLabels = {"Name", "Gender", "Area", "Occupation", "Change the password"};
        for (String label : buttonLabels) {
            JButton button = new JButton(label);
            button.setPreferredSize(new Dimension(80, 15));
            button.setFont(new Font("Microsoft YaHei", Font.PLAIN, 14));
            
            // 为Name按钮添加点击事件
            if ("Name".equals(label)) {
                button.addActionListener(e -> {
                    NameChangeDialog dialog = new NameChangeDialog(this, welcomeLabel);
                    dialog.setVisible(true);
                });
            } else if ("Gender".equals(label)) {
                button.addActionListener(e -> {
                    GenderDialog dialog = new GenderDialog(this);
                    dialog.setVisible(true);
                });
            } else if ("Area".equals(label)) {
                button.addActionListener(e -> {
                    AreaDialog dialog = new AreaDialog(this);
                    dialog.setVisible(true);
                });
            } else if ("Occupation".equals(label)) {
                button.addActionListener(e -> {
                    OccupationDialog dialog = new OccupationDialog(this);
                    dialog.setVisible(true);
                });
            } else if ("Change the password".equals(label)) {
                button.addActionListener(e -> {
                    PasswordChangeDialog dialog = new PasswordChangeDialog(this);
                    dialog.setVisible(true);
                });
            }
            
            buttonPanel.add(button);
        }
        
        gbc.gridy = 1;
        gbc.weighty = 0.4;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(buttonPanel, gbc);
        
        // 底部登录相关按钮
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        bottomPanel.setBackground(Color.WHITE);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        JButton switchAccountButton = new JButton("Switch accounts");
        JButton logoutButton = new JButton("Log out");
        
        // 设置按钮样式
        Dimension buttonSize = new Dimension(180, 30);
        switchAccountButton.setPreferredSize(buttonSize);
        logoutButton.setPreferredSize(buttonSize);
        
        Font buttonFont = new Font("Microsoft YaHei", Font.PLAIN, 14);
        switchAccountButton.setFont(buttonFont);
        logoutButton.setFont(buttonFont);
        
        // 添加Switch accounts按钮点击事件
        switchAccountButton.addActionListener(e -> {
            dispose(); // 关闭当前窗口
            SwingUtilities.invokeLater(() -> {
                LoginFrame loginFrame = new LoginFrame();
                loginFrame.setVisible(true);
            });
        });
        
        // 添加Log out按钮点击事件
        logoutButton.addActionListener(e -> {
            System.exit(0); // 关闭应用程序
        });
        
        bottomPanel.add(switchAccountButton);
        bottomPanel.add(logoutButton);
        
        gbc.gridy = 2;
        gbc.weighty = 0.35;
        gbc.anchor = GridBagConstraints.PAGE_END;
        gbc.insets = new Insets(50, 0, 30, 0); // 增加上边距
        panel.add(bottomPanel, gbc);
        
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