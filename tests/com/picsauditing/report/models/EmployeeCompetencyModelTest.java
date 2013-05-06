package com.picsauditing.report.models;

import com.google.common.base.Joiner;
import com.picsauditing.EntityFactory;
import org.approvaltests.Approvals;
import org.junit.Test;

public class EmployeeCompetencyModelTest {
    @Test
    public void testApproval() throws Exception {
        EmployeeCompetencyModel model = new EmployeeCompetencyModel(EntityFactory.makePermission());

        Approvals.verify(Joiner.on("\n").join(model.getAvailableFields().values()));
    }
}
