package com.picsauditing.rbic;

import com.picsauditing.jpa.entities.*;
import org.junit.Test;

import java.util.List;

import static junit.framework.Assert.assertEquals;

public class RulesTest {

    @Test
    public void test() {
        ContractorAccount contractor = ContractorAccount.builder()
                .audit(ContractorAudit.builder().data(AuditData.builder().answer("15").question(AuditQuestion.builder().id(16961).build()).build()).build())
                .operator(OperatorAccount.builder().id(34887).flagCriteria(FlagCriteria.builder().id(523).build()).build())
                .build();
        ContractorModel contractorModel = ContractorModel.builder().contractor(contractor).build();

        DroolsUtils.runDroolsFileWith("34887_rbic.drl", contractorModel);
        List<InsuranceCriteriaContractorOperator> insurance = contractor.getInsuranceCriteriaContractorOperators();
        assertEquals(3000123, insurance.get(0).getInsuranceLimit());
    }

}
