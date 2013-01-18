package com.picsauditing.actions.contractors.risk;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class TransportationAssessmentTest {
    @Test
    public void testFindByQuestionID_ExistingQuestion() {
        TransportationAssessment deliverToWarehouseOrAdministrativeOffice = TransportationAssessment.DELIVER_TO_WAREHOUSE_OR_ADMINISTRATIVE_OFFICE;
        assertEquals(deliverToWarehouseOrAdministrativeOffice, TransportationAssessment.findByQuestionID(deliverToWarehouseOrAdministrativeOffice.getQuestionID()));
        // OVERSIZED_OR_PERMIT_REQUIRED_LOADS question ID
        assertNotNull(TransportationAssessment.findByQuestionID(14925));
    }

    @Test
    public void testFindByQuestionID_NonExistentQuestion() {
        assertNull(TransportationAssessment.findByQuestionID(0));
        // Currently bogus question ID
        assertNull(TransportationAssessment.findByQuestionID(10000));
    }
}
