package com.picsauditing.report.data;

import com.picsauditing.jpa.entities.Column;

public class ReportCell {
    private Column column;
    private Object value;

    public ReportCell(Column column, Object value) {
        this.column = column;
        this.value = value;
    }

    public Column getColumn() {
        return column;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }
}