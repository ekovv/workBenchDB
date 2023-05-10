package com.ekov.workBenchDB.dao;


import java.util.List;

public class RowsAndCols {

    private List<List<String>> rows;
    private List<String> cols;

    public RowsAndCols(List<List<String>> rows, List<String> cols) {
        this.rows = rows;
        this.cols = cols;
    }

    public List<List<String>> getRows() {
        return rows;
    }

    public List<String> getCols() {
        return cols;
    }

    @Override
    public String toString() {
        return "RowsAndCols{" +
                "rows=" + rows +
                ", cols=" + cols +
                '}';
    }
}
