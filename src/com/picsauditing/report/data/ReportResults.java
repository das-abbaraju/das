package com.picsauditing.report.data;

import com.picsauditing.jpa.entities.Column;
import org.json.simple.JSONArray;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ReportResults {
    private List<Column> columns = new ArrayList<>();
    private List<ReportRow> rows = new ArrayList<>();

    public List<Column> getColumns() {
        return columns;
    }

    public void setColumns(List<Column> columns) {
        this.columns = columns;
    }

    @Deprecated
    public void addRow(ReportRow row) {
        rows.add(row);
    }

    public List<ReportRow> getRows() {
        return rows;
    }

    public int size() {
        if (CollectionUtils.isEmpty(rows)) {
            return 0;
        }

        return rows.size();
    }

    public Collection<ReportCell> getCells() {
        Collection<ReportCell> cells = new ArrayList<ReportCell>();
        for (ReportRow row : rows) {
            cells.addAll(row.getCells());
        }

        return cells;
    }

    @SuppressWarnings("unchecked")
    public JSONArray toJson() {
        JSONArray jsonRows = new JSONArray();
        for (ReportRow row : rows) {
            jsonRows.add(row.toJson());
        }

        return jsonRows;
    }

    @Override
    public String toString() {
        StringBuilder out = new StringBuilder();
        for (Column column : columns) {
            out.append(column.getName());
            out.append("\t");
        }
        out.append("\n");

        if (rows.isEmpty()) {
            out.append("NO DATA");
            return out.toString();
        }

        for (ReportRow row : rows) {
            for (Column column : columns) {
                final ReportCell cell = row.getCellByColumn(column);
                if (cell == null || cell.getValue() == null) {
                    out.append("NULL");
                } else {
                    out.append(cell.getValue().toString());
                }
                out.append("\t");
            }
            out.append("\n");
        }
        return out.toString();
    }
}
