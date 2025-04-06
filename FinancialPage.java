import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

public class FinancialPage {
    public static void main(String[] args) {
        // 示例数据
        List<FinancialService.Transaction> transactions = Arrays.asList(
            new FinancialService.Transaction(5000, "INCOME", LocalDate.of(2023, 1, 5)),
            new FinancialService.Transaction(300, "EXPENSE", LocalDate.of(2023, 1, 10)),
            new FinancialService.Transaction(2000, "INCOME", LocalDate.of(2023, 2, 15))
        );

        // 创建服务实例
        FinancialService service = new FinancialService();
        
        // 设置时间范围（示例：2023年1月）
        LocalDate start = LocalDate.of(2023, 1, 1);
        LocalDate end = LocalDate.of(2023, 1, 31);

        // 获取计算结果
        double[] totals = service.calculateTotal(transactions, start, end);
        
        // 展示结果（可替换为你的页面显示逻辑）
        System.out.printf("2023年1月财务状况：\n");
        System.out.printf("总收入：¥%.2f\n", totals[0]);
        System.out.printf("总支出：¥%.2f\n", totals[1]);
        System.out.printf("净收益：¥%.2f\n", totals[0] - totals[1]);
    }
}