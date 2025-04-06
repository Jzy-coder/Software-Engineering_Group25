import java.time.LocalDate;
import java.util.List;

public class FinancialService {
    // 交易记录实体类
    public static class Transaction {
        private final double amount;
        private final String type; // "INCOME" 或 "EXPENSE"
        private final LocalDate date;

        public Transaction(double amount, String type, LocalDate date) {
            this.amount = amount;
            this.type = type;
            this.date = date;
        }

        // Getters
        public double getAmount() { return amount; }
        public String getType() { return type; }
        public LocalDate getDate() { return date; }
    }

    /**
     * 计算指定时间范围内的总额
     * @param transactions 所有交易记录
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 包含总收入和总支出的数组 [总收入, 总支出]
     */
    public double[] calculateTotal(List<Transaction> transactions, 
                                  LocalDate startDate, 
                                  LocalDate endDate) {
        double totalIncome = 0;
        double totalExpense = 0;

        for (Transaction t : transactions) {
            if (!t.getDate().isBefore(startDate) && !t.getDate().isAfter(endDate)) {
                if ("INCOME".equalsIgnoreCase(t.getType())) {
                    totalIncome += t.getAmount();
                } else if ("EXPENSE".equalsIgnoreCase(t.getType())) {
                    totalExpense += t.getAmount();
                }
            }
        }

        return new double[]{totalIncome, totalExpense};
    }
}