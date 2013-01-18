package com.picsauditing.actions.contractors.risk;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class ProductAssessmentTest {
    @Test
    public void testFindByQuestionID_ExistingQuestion() {
        ProductAssessment failureWorkStoppage = ProductAssessment.FAILURE_WORK_STOPPAGE;
        assertEquals(failureWorkStoppage, ProductAssessment.findByQuestionID(failureWorkStoppage.getQuestionID()));
        // DELIVERY_WORK_STOPPAGE question ID
        assertNotNull(ProductAssessment.findByQuestionID(7661));
    }

    @Test
    public void testFindByQuestionID_NonExistentQuestion() {
        assertNull(ProductAssessment.findByQuestionID(0));
        // Currently bogus question ID
        assertNull(ProductAssessment.findByQuestionID(10000));
    }
}
