package com.finance.result;

public class ImportResult {
    private int importedCount;
    private int skippedCount;

    public ImportResult(int importedCount, int skippedCount) {
        this.importedCount = importedCount;
        this.skippedCount = skippedCount;
    }

    public int getImportedCount() {
        return importedCount;
    }

    public int getSkippedCount() {
        return skippedCount;
    }
}