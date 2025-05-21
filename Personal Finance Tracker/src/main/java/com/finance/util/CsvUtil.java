package com.finance.util;

import java.io.File;
import java.util.Arrays;
import java.time.LocalDate;
import java.time.LocalDateTime;
import com.opencsv.CSVReader;
import com.finance.model.Transaction;
import com.opencsv.CSVWriter;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import java.io.FileWriter;
import java.io.IOException;
import com.opencsv.exceptions.CsvValidationException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class CsvUtil {
    private static final Logger logger = LoggerFactory.getLogger(CsvUtil.class);
    private static final String[] EXPECTED_HEADERS = {"Category", "Type", "Amount", "Description", "Date"};
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public static List<Transaction> parseCSV(File file) throws IOException, IllegalArgumentException, CsvValidationException {
        List<Transaction> transactions = new ArrayList<>();
        
        try (CSVReader reader = new CSVReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
            String[] headers = reader.readNext();
            // 创建列名到索引的映射
            int[] columnIndices = new int[EXPECTED_HEADERS.length];
            for (int i = 0; i < EXPECTED_HEADERS.length; i++) {
                columnIndices[i] = -1;
                for (int j = 0; j < headers.length; j++) {
                    if (EXPECTED_HEADERS[i].equalsIgnoreCase(headers[j])) {
                        columnIndices[i] = j;
                        break;
                    }
                }
                if (columnIndices[i] == -1) {
                    throw new IllegalArgumentException("Missing required column: " + EXPECTED_HEADERS[i]);
                }
            }

            String[] nextLine;
            while ((nextLine = reader.readNext()) != null) {
                try {
                    Transaction transaction = new Transaction(
                        nextLine[columnIndices[0]],
                        nextLine[columnIndices[1]],
                        Double.parseDouble(nextLine[columnIndices[2]]),
                        nextLine[columnIndices[3]],
                        LocalDate.parse(nextLine[columnIndices[4]], DATE_FORMATTER).atStartOfDay()
                    );
                    transactions.add(transaction);
                } catch (Exception e) {
                    throw new IllegalArgumentException("Failed to parse CSV row: " + String.join(",", nextLine), e);
                }
            }
        }
        return transactions;
    }
    public static <T> void exportTableToCSV(TableView<T> tableView, String filePath) throws IOException {
        List<String[]> data = new ArrayList<>();
        
        // 添加表头
        List<String> headers = new ArrayList<>();
        for (TableColumn<T, ?> column : tableView.getColumns()) {
            headers.add(column.getText());
        }
        data.add(headers.toArray(new String[0]));

        // 添加数据行
        for (T item : tableView.getItems()) {
            List<String> row = new ArrayList<>();
            for (TableColumn<T, ?> column : tableView.getColumns()) {
                Object cellValue = column.getCellData(item);
                if (cellValue instanceof LocalDateTime) {
                    // Format LocalDateTime to yyyy-MM-dd
                    row.add(((LocalDateTime) cellValue).toLocalDate().format(DATE_FORMATTER));
                } else {
                    row.add(cellValue != null ? cellValue.toString() : "");
                }
            }
            data.add(row.toArray(new String[0]));
        }

        try (CSVWriter writer = new CSVWriter(new FileWriter(filePath))) {
            writer.writeAll(data);
        }
    }
}