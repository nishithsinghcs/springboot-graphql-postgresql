package com.example.app.grid;

public final class AgGridPage {
    private AgGridPage() {}

    public static int size(Integer startRow, Integer endRow, int defaultSize) {
        int start = startRow == null ? 0 : startRow;
        int end = endRow == null ? (start + defaultSize) : endRow;
        return Math.max(1, end - start);
    }

    public static int page(Integer startRow, int size) {
        int start = startRow == null ? 0 : startRow;
        return start / size;
    }
}