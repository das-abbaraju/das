package com.picsauditing.rbic;

import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.InsuranceCriteriaContractorOperatorDAO;
import com.picsauditing.jpa.entities.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class RBIC extends PicsActionSupport {

    private ContractorAccount contractor;
    @Autowired
    private RulesRunner rulesRunner;
    @Autowired
    private InsuranceCriteriaContractorOperatorDAO insuranceCriteriaContractorOperatorDAO;
    @Autowired
    private ContractorAccountDAO contractorAccountDAO;

    public String execute() {
        rulesRunner.setContractor(contractor);

        for (ContractorOperator contractorOperator : contractor.getOperators()) {
            OperatorAccount operatorAccount = contractorOperator.getOperatorAccount();
            rulesRunner.runInsuranceCriteriaRulesForOperator(operatorAccount);
        }

        contractorAccountDAO.save(contractor);

        return SUCCESS;
    }

    public List<InsuranceCriteriaContractorOperator> getInsuranceLimits() {
        return insuranceCriteriaContractorOperatorDAO.findByContractorId(contractor.getId());
    }

    public ContractorAccount getContractor() {
        return contractor;
    }

    public void setContractor(ContractorAccount contractor) {
        this.contractor = contractor;
    }

    public String thing() {
        JSONArray results = new JSONArray();

        for (InsuranceCriteriaContractorOperator insurance: contractor.getInsuranceCriteriaContractorOperators()) {
            if (insurance.getFlagCriteria().getQuestion().equals(question)) {
                JSONObject criteria = new JSONObject();
                criteria.put("criteria", insurance.getFlagCriteria().getLabel());
                criteria.put("limit", insurance.getInsuranceLimit());
                results.add(criteria);
            }
        }

        jsonArray = results;

        return JSON_ARRAY;
    }

    public AuditQuestion question;

    public AuditQuestion getQuestion() {
        return question;
    }

    public void setQuestion(AuditQuestion question) {
        this.question = question;
    }
}
