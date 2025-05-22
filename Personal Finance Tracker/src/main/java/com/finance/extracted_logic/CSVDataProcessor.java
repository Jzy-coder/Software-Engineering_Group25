package com.finance.extracted_logic;

import com.finance.model.Transaction;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class CSVDataProcessor {
    private static final String[] EXPECTED_HEADERS = {"Category", "Type", "Amount", "Description", "Date"};
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public List<Transaction> parseCSV(File file) throws IOException, IllegalArgumentException, CsvValidationException {
        List<Transaction> transactions = new ArrayList<>();
        
        try (CSVReader reader = new CSVReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
            String[] headers = reader.readNext();
            validateHeaders(headers);
            
            String[] nextLine;
            while ((nextLine = reader.readNext()) != null) {
                transactions.add(parseTransaction(nextLine));
            }
        }
        return transactions;
    }

    private void validateHeaders(String[] headers) {
        if (headers == null || headers.length == 0) {
            return;
        }
        for (String expected : EXPECTED_HEADERS) {
            boolean found = false;
            for (String actual : headers) {
                if (expected.equalsIgnoreCase(actual)) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                throw new IllegalArgumentException("Missing required column: " + expected);
            }
        }
    }

    private Transaction parseTransaction(String[] fields) {
        try {
            return new Transaction(
                fields[0], // Category
                fields[1], // Type
                Double.parseDouble(fields[2]), // Amount
                fields[3], // Description
                LocalDate.parse(fields[4], DATE_FORMATTER).atStartOfDay() // Date
            );
        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to parse CSV row: " + String.join(",", fields), e);
        }
    }
}