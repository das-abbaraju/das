package com.picsauditing.report.tables;

import org.approvaltests.Approvals;
import org.approvaltests.reporters.DiffReporter;
import org.approvaltests.reporters.UseReporter;
import org.junit.Test;

@UseReporter(DiffReporter.class)
public class ContractorRenewalPredictionViewTest {

    @Test
    public void testSQL() throws Exception {
        ContractorRenewalPredictionView view = new ContractorRenewalPredictionView();
        Approvals.verify(view.toString());
    }
}
