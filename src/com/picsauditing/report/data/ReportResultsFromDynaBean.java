package com.picsauditing.report.data;

import com.picsauditing.jpa.entities.Column;
import com.picsauditing.report.fields.Field;
import com.picsauditing.util.Strings;
import org.apache.commons.beanutils.BasicDynaBean;
import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ReportResultsFromDynaBean {

    static public ReportResults build(List<Column> columns, List<BasicDynaBean> data) {
        ReportResults results = new ReportResults();
        results.setColumns(columns);

        for (BasicDynaBean dynaBean : data) {
            ReportRow row = createRow(columns, dynaBean);
            results.getRows().add(row);
        }
        return results;
    }

    static private ReportRow createRow(List<Column> columns, BasicDynaBean dynaBean) {
        Map<Column, Object> cells = new HashMap<>();

        for (Column column : columns) {
            if (column.getField() != null) {
                try {
                    Object value = dynaBean.get(column.getName());
                    cells.put(column, value);
                } catch (IllegalArgumentException e) {

                }
                addDependentFields(column, dynaBean, cells);
            } else {
                cells.put(column, null);
            }
        }
        return new ReportRow(cells);
    }

    static private void addDependentFields(Column column, BasicDynaBean dynaBean, Map<Column, Object> cells) {
        Set<String> dependentFields = column.getField().getDependentFields();
        for (String fieldName : dependentFields) {
            if (!containsColumnWithFieldName(cells, fieldName)) {
                try {
                    Object value = dynaBean.get(fieldName);
                    Column newColumn = new Column(fieldName);
                    newColumn.setField(new Field(fieldName));

                    cells.put(newColumn, value);
                } catch (IllegalArgumentException e) {
                }
            }
        }
    }

    static private boolean containsColumnWithFieldName(Map<Column, Object> cells, String fieldName) {
        if (CollectionUtils.isEmpty(cells) || Strings.isEmpty(fieldName)) {
            return false;
        }

        for (Column column : cells.keySet()) {
            if (fieldName.equals(column.getName())) {
                return true;
            }
        }

        return false;
    }

}
