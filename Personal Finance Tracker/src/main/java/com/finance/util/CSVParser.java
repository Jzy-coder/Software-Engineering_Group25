package com.finance.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.finance.model.Transaction;
import com.finance.service.TransactionService;
import com.opencsv.CSVReader;

public class CSVParser {
    private final TransactionService transactionService;
    
    public CSVParser(TransactionService transactionService) {
        this.transactionService = transactionService;
    }
    
    private static final Logger logger = LoggerFactory.getLogger(CSVParser.class);
    private static String mapChineseType(String chineseType) {
        switch(chineseType) {
            case "餐饮": return "Food";
            case "购物": return "Shopping";
            case "交通": return "Transportation";
            case "住房": return "Housing";
            case "娱乐": return "Entertainment";
            case "工资": return "Salary";
            case "奖金": return "Bonus";
            default: return "Others";
        }
    }

    private static final Set<String> VALID_INCOME_TYPES = Set.of("Salary", "Bonus", "Others");
    private static final Set<String> validExpenseTypes = Set.of("Food", "Shopping", "Transportation", "Housing", "Entertainment", "Others");

    private static boolean isValidTransaction(String category, String type) {
        if (category.equals("Income")) {
            return VALID_INCOME_TYPES.contains(type);
        } else if (category.equals("Expense")) {
            return validExpenseTypes.contains(type);
        }
        return false;
    }

    public List<Transaction> parseWeChatCSV(File file, List<Transaction> existingTransactions) throws Exception {
        List<Transaction> transactions = new ArrayList<>();
        long maxExistingId = transactionService.getAllTransactions().stream().mapToLong(Transaction::getId).max().orElse(0L);

        try (CSVReader reader = new CSVReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
            for (int i = 0; i < 17; i++) {
                reader.readNext();
            }

            String[] nextLine;
            while ((nextLine = reader.readNext()) != null) {
                if (nextLine[0].contains("类型") || nextLine[4].contains("金额")) {
                    logger.debug("跳过疑似表头: {}", Arrays.toString(nextLine));
                    continue;
                }
                logger.debug("解析CSV行: {}", (Object) nextLine);
                if (nextLine.length < 8 || nextLine[4].trim().isEmpty()) {
                    logger.warn("跳过无效的CSV行(列不足或金额字段为空): {}", Arrays.toString(nextLine));
                    continue;
                }                

                String amountStr = nextLine[4].replace("¥", "").trim(); // 微信账单金额列在索引4位置
                try {
                    String type = nextLine[0].trim();
                    logger.debug("原始金额值: {}", nextLine[4]);
                    String cleaned = nextLine[4].replaceAll("[^\\d.,]", "").replace(",", "").trim();
                    if (cleaned.isEmpty()) {
                        throw new NumberFormatException("Empty amount value");
                    }
                    double amount = Math.abs(Double.parseDouble(cleaned));
                    logger.debug("清洗后金额值: {}", cleaned);
                    String description = nextLine[7].trim();
                    String dateStr = nextLine[2].trim();
                    // 只保留日期部分（前10个字符），忽略时间
                    String dateOnlyStr = dateStr.length() >= 10 ? dateStr.substring(0, 10) : dateStr;
                    LocalDateTime date = LocalDate.parse(dateOnlyStr, DateTimeFormatter.ofPattern("yyyy-MM-dd")).atStartOfDay();

                    logger.debug("解析成功: type={}, amount={}, date={}", type, amount, date);

                    String category = type.startsWith("/") ? "Expense" : "Income";
                    String mappedType = mapChineseType(type.replace("/", ""));

                    if (!isValidTransaction(category, mappedType)) {
                        logger.warn("跳过无效交易类型: {} - {}", category, mappedType);
                        continue;
                    }

                    Transaction transaction = new Transaction(
                        category,
                        mappedType,
                        amount,
                        description,
                        date
                    );

//long maxExistingId = transactionService.getAllTransactions().stream().mapToLong(Transaction::getId).max().orElse(0L);
transaction.setId(++maxExistingId);
                    transactions.add(transaction);
// transactionService.syncTransactionId(maxExistingId);
                } catch (NumberFormatException e) {
                    logger.error("金额格式错误: {}", nextLine[4]);
                    throw new Exception("第" + (reader.getLinesRead() + 17) + "行金额格式错误: " + amountStr, e); // 准确计算实际文件行号（17行头+已读行数）
                } catch (Exception e) {
                    logger.error("解析CSV行失败: {}", String.join(",", nextLine), e);
                    throw new Exception("第" + (reader.getLinesRead() + 17) + "行解析失败: " + e.getMessage(), e);
                }
            }
        }
        return transactions;
    }
}