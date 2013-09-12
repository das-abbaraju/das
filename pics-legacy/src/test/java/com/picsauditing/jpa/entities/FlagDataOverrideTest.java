package com.picsauditing.jpa.entities;

import org.junit.Test;

import java.util.Date;

import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;

public class FlagDataOverrideTest {
    @Test
    public void CloneTest() throws Exception {
        FlagDataOverride original = makeUpFlagDataOverride();
        FlagDataOverride copy = (FlagDataOverride)original.clone();
        assertNotNull(copy);
        assertNotSame(copy, original);
        assertTrue(copy.equals(original));
        assertTrue(original.equals(copy));
        assertEquals(0,copy.getId());
        assertEquals(3333,original.getId());
    }

    private FlagDataOverride makeUpFlagDataOverride() {
        FlagDataOverride flagDataOverride = new FlagDataOverride();
        flagDataOverride.setId(3333);
        ContractorAccount contractor = new ContractorAccount();
        contractor.setId(2727);
        flagDataOverride.setContractor(contractor);
        FlagCriteria flagCriteria = new FlagCriteria();
        flagCriteria.setId(4444);
        flagDataOverride.setCriteria(flagCriteria);
        OperatorAccount operator = new OperatorAccount();
        operator.setId(5555);
        flagDataOverride.setOperator(operator);
        ContractorOperator contractorOperator = new ContractorOperator();
        contractorOperator.setId(6666);
        contractorOperator.setContractorAccount(contractor);
        contractorOperator.setOperatorAccount(operator);
        flagDataOverride.setContractorOperator(contractorOperator);
        flagDataOverride.setForceEnd(new Date("12/31/2012"));
        flagDataOverride.setForceflag(FlagColor.Red);
        flagDataOverride.setYear("2012");
        return flagDataOverride;
    }
}
