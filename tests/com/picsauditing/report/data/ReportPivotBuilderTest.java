package com.picsauditing.report.data;

import com.picsauditing.jpa.entities.Column;
import org.approvaltests.Approvals;
import org.approvaltests.reporters.DiffReporter;
import org.approvaltests.reporters.UseReporter;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.List;

@UseReporter(DiffReporter.class)
public class ReportPivotBuilderTest {
    private final Logger logger = LoggerFactory.getLogger(ReportPivotBuilderTest.class);

//    @Test
//    public void testSimplePivot() throws Exception {
//        final List<Column> columns = PivotTestData.getColumnsForFlagChart();
//        final List<List<Serializable>> data = PivotTestData.getDataForFlagChart();
//
//        ReportResults results = ReportResultsFromArrayLists.build(columns, data);
//
//        ReportPivotBuilder builder = new ReportPivotBuilder();
//        ReportResults converted = builder.convertToPivot(results);
//
//        String actual = "Original \n" + results.toString() + "\n\nConverted\n" + converted.toString();
//        Approvals.verify(actual);
//    }
}
