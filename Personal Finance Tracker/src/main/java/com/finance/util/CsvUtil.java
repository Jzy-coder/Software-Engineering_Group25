package com.finance.util;

import com.opencsv.CSVWriter;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import java.io.FileWriter;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class CsvUtil {
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
                row.add(cellValue != null ? cellValue.toString() : "");
            }
            data.add(row.toArray(new String[0]));
        }

        try (CSVWriter writer = new CSVWriter(new FileWriter(filePath))) {
            writer.writeAll(data);
        }
    }
}