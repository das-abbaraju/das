package com.picsauditing.actions.contractors.risk;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class SafetyAssessmentTest {
    @Test
    public void testFindByQuestionID_ExistingQuestion() {
        SafetyAssessment performsHighRisk = SafetyAssessment.PERFORMS_HIGH_RISK;
        assertEquals(performsHighRisk, SafetyAssessment.findByQuestionID(performsHighRisk.getQuestionID()));
        // CONDUCTED_FROM_OFFICE question ID
        assertNotNull(SafetyAssessment.findByQuestionID(12341));
    }

    @Test
    public void testFindByQuestionID_NonExistentQuestion() {
        assertNull(SafetyAssessment.findByQuestionID(0));
        // Currently bogus question ID
        assertNull(SafetyAssessment.findByQuestionID(10000));
    }
}