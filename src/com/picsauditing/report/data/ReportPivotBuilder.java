package com.picsauditing.report.data;

import com.picsauditing.jpa.entities.Column;
import com.picsauditing.report.ReportPivotDefinition;
import com.picsauditing.report.fields.Field;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.*;

public class ReportPivotBuilder {
    private final Logger logger = LoggerFactory.getLogger(ReportPivotBuilder.class);
    private ReportPivotDefinition definition;
    private Map<Object, Map<Object, List<Object>>> dataTree = new TreeMap<Object, Map<Object, List<Object>>>();
    private List<Column> newReportColumns;

    @SuppressWarnings("unchecked")
    public ReportResults convertToPivot(ReportResults original) {
        definition = new ReportPivotDefinition(original.getColumns());
        if (!definition.isPivotable()) {
            logger.debug("Not a valid pivot table. Don't make any changes");
            return original;
        }

        Map<Object, ReportCell> pivotedColumns = getPivotedColumns(original);
        newReportColumns = getNewReportColumns(pivotedColumns);

        pivotDataIntoDataTree(original);

        List<List<Serializable>> data = buildPivotedData(pivotedColumns);

        return ReportResultsFromArrayLists.build(newReportColumns, data);
    }

    private List<List<Serializable>> buildPivotedData(Map<Object, ReportCell> pivotedColumns) {
        List<List<Serializable>> data = new ArrayList<>();
        for (Object pivotedRow : dataTree.keySet()) {
            logger.debug("Adding row data for {}", pivotedRow);
            List<Serializable> dataRow = new ArrayList<>();
            dataRow.add(pivotedRow.toString());
            data.add(dataRow);
            for (Object pivotedColumn : pivotedColumns.keySet()) {
                logger.debug("Adding cell data for {}", pivotedColumn);
                final List<Object> cellDatas = dataTree.get(pivotedRow).get(pivotedColumn);
                Serializable cellValue = calculatePivotedCellValue(cellDatas);
                dataRow.add(cellValue);
            }
        }
        return data;
    }

    private Serializable calculatePivotedCellValue(List<Object> cellDatas) {
        if (cellDatas != null) {
            for (Object cell : cellDatas) {
                return cell.toString();
            }
        }
        return "0";
    }

    private void pivotDataIntoDataTree(ReportResults original) {
        for (ReportRow originalRow : original.getRows()) {
            final Object rowValue = originalRow.getCellByColumn(definition.getRow()).getValue();
            final Object colValue = originalRow.getCellByColumn(definition.getColumn()).getValue();
            final Object cellValue = originalRow.getCellByColumn(definition.getCell()).getValue();
            logger.debug(rowValue.toString() + colValue.toString() + cellValue.toString());

            fillOutTree(rowValue, colValue);
            dataTree.get(rowValue).get(colValue).add(cellValue);
        }
    }

    private void fillOutTree(Object rowValue, Object colValue) {
        if (!dataTree.containsKey(rowValue)) {
            dataTree.put(rowValue, new HashMap<Object, List<Object>>());
        }
        if (!dataTree.get(rowValue).containsKey(colValue)) {
            dataTree.get(rowValue).put(colValue, new ArrayList<Object>());
        }
    }

    private Map<Object, ReportCell> getPivotedColumns(ReportResults original) {
        Map<Object, ReportCell> pivotedColumns = new TreeMap<>();
        for (ReportRow originalRow : original.getRows()) {
            final ReportCell dataForPivotColumn = originalRow.getCellByColumn(definition.getColumn());
            pivotedColumns.put(dataForPivotColumn.getValue(), dataForPivotColumn);
        }
        return pivotedColumns;
    }

    private List<Column> getNewReportColumns(Map<Object, ReportCell> pivotedColumns) {
        List<Column> columns = new ArrayList<>();
        addFirstColumnBasedOnRow(columns);
        int sortIndex = 10;
        for (Object columnHeadings : pivotedColumns.keySet()) {
            Column column = new Column(columnHeadings.toString());
            column.setId(sortIndex);
            final Field field = definition.getColumn().getField().clone();
            column.setField(field);
            field.setSortable(false);
            field.setText(columnHeadings.toString());
            field.setHelp(definition.getColumn().getField().getText());
            column.setSortIndex(sortIndex++);
            column.setSqlFunction(null);
            logger.debug("Adding pivoted column {}", column);
            columns.add(column);
        }
        return columns;
    }

    private void addFirstColumnBasedOnRow(List<Column> columns) {
        logger.debug("Adding first column based on the row {}", definition.getRow());
        columns.add(definition.getRow());
    }

    public List<Column> getColumns() {
        return newReportColumns;
    }
}
