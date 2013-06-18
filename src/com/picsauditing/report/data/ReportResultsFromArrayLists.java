package com.picsauditing.report.data;

import com.picsauditing.jpa.entities.Column;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReportResultsFromArrayLists {

    static public ReportResults build(List<Column> columns, List<List<Serializable>> data) {
        ReportResults results = new ReportResults();
        results.setColumns(columns);

        for (List rowData : data) {
            ReportRow row = createRow(columns, rowData);
            results.getRows().add(row);
        }
        return results;
    }

    static private ReportRow createRow(List<Column> columns, List rowData) {
        Map<Column, Object> cells = new HashMap<>();

        int columnNumber = 0;
        for (Column column : columns) {
            if (column.getField() != null) {
                try {
                    Object value = rowData.get(columnNumber);
                    cells.put(column, value);
                } catch (IllegalArgumentException e) {
                    System.out.println(e.getMessage());
                } catch (IndexOutOfBoundsException e) {
                    System.out.println("Warning: no data for " + column.getField() + " column " + columnNumber);
                }
            } else {
                cells.put(column, null);
            }
            columnNumber++;
        }
        return new ReportRow(cells);
    }
}
