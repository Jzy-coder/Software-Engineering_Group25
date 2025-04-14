package com.finance.service.impl;

import java.util.List;

import com.finance.dao.TransactionDAO;
import com.finance.model.Transaction;
import com.finance.service.TransactionService;

public class TransactionServiceImpl implements TransactionService {
    private TransactionDAO transactionDAO = new TransactionDAO();

    @Override
    public void addTransaction(Transaction transaction) {
        transactionDAO.save(transaction);
    }

    @Override
    public long getNextTransactionId() {
        return transactionDAO.getAllTransactions().stream()
                .mapToLong(Transaction::getId)
                .max()
                .orElse(0L) + 1;
    }

    @Override
    public void batchImport(List<Transaction> transactions) {
        List<Transaction> existing = transactionDAO.getAllTransactions();
        existing.addAll(transactions);
        transactionDAO.batchInsert(existing);
    }

    @Override
    public double calculateBalance() {
        List<Transaction> transactions = getAllTransactions();
        double totalIncome = transactions.stream()
            .filter(t -> "收入".equals(t.getCategory()))
            .mapToDouble(Transaction::getAmount)
            .sum();
        double totalExpense = transactions.stream()
            .filter(t -> "支出".equals(t.getCategory()))
            .mapToDouble(Transaction::getAmount)
            .sum();
        return totalIncome - totalExpense;
    }

    @Override
    public List<Transaction> getAllTransactions() {
        return transactionDAO.getAllTransactions();
    }

    @Override
    public void updateTransaction(Transaction transaction) {
        transactionDAO.update(transaction);
    }

    @Override
    public void deleteTransaction(Long id) {
        transactionDAO.deleteById(id);
    }

    @Override
    public double calculateTotalByCategory(String category) {
        return transactionDAO.getAllTransactions().stream()
            .filter(t -> category.equals(t.getCategory()))
            .mapToDouble(Transaction::getAmount)
            .sum();
    }

    @Override
    public void switchUser(String username, boolean isRename) {
        transactionDAO.switchUser(username, isRename);
        clearCache();
    }

    @Override
    public void clearCache() {
        transactionDAO.clearCache();
    }
}