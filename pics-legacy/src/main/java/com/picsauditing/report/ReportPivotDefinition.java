package com.picsauditing.report;

import com.picsauditing.jpa.entities.Column;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class ReportPivotDefinition {
    private final Logger logger = LoggerFactory.getLogger(ReportPivotDefinition.class);
    private Column row = null;
    private Column column = null;
    private Column cell = null;

    public ReportPivotDefinition(List<Column> columns) {
        // We're going to use "convention" for now
        if (columns.size() == 0) {
            logger.warn("You can't create a Pivot Table without any columns");
            return;
        }
        if (columns.size() > 3) {
            logger.warn("Report has {} columns.", columns.size());
            return;
        }
        cell = columns.get(0);
        logger.info("Set Pivot Table Cell or Value to {} {}", cell, cell.getSqlFunction());
        row = columns.get(1);
        logger.info("Set Pivot Table Row or Label to {}", row);
        if (columns.size() > 2) {
            column = columns.get(2);
            logger.info("Creating Multi Series Pivot Table based on Column or Series {}", column);
        }
    }

    public boolean isPivotable() {
        if (row == null)
            return false;
        if (column == null)
            return false;
        if (cell == null)
            return false;
        return true;
    }

    public Column getRow() {
        return row;
    }

    public Column getColumn() {
        return column;
    }

    public Column getCell() {
        return cell;
    }

}
