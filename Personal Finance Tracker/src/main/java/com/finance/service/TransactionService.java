package com.finance.service;

import com.finance.dao.TransactionDAO;
import com.finance.model.Transaction;

import java.util.List;

/**
 * 交易记录服务类，处理业务逻辑
 */
public class TransactionService {
    
    private TransactionDAO transactionDAO;
    
    public TransactionService() {
        this.transactionDAO = new TransactionDAO();
    }
    
    /**
     * 获取所有交易记录
     */
    public List<Transaction> getAllTransactions() {
        return transactionDAO.findAll();
    }
    
    /**
     * 添加新交易记录
     */
    public void addTransaction(Transaction transaction) {
        transactionDAO.save(transaction);
    }
    
    /**
     * 删除交易记录
     */
    public void deleteTransaction(Long id) {
        transactionDAO.delete(id);
    }
    
    /**
     * 更新交易记录
     */
    public void updateTransaction(Transaction transaction) {
        transactionDAO.update(transaction);
    }
    
    /**
     * 根据类别统计金额
     */
    public double calculateTotalByCategory(String category) {
        return transactionDAO.findAll().stream()
                .filter(t -> t.getCategory().equals(category))
                .mapToDouble(Transaction::getAmount)
                .sum();
    }
    
    /**
     * 计算收支差额
     */
    public double calculateBalance() {
        double income = calculateTotalByCategory("收入");
        double expense = calculateTotalByCategory("支出");
        return income - expense;
    }
}