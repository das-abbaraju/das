package com.picsauditing.auditbuilder;

import com.picsauditing.auditbuilder.dao.AuditDataDAO2;
import com.picsauditing.auditbuilder.dao.ContractorTagDAO2;
import com.picsauditing.auditbuilder.entities.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

public class DocumentService {
    @Autowired
    private AuditDataDAO2 auditDataDAO;
    @Autowired
    private AuditTypeRuleCache2 typeRuleCache;
    @Autowired
    private AuditCategoryRuleCache2 categoryRuleCache;
    @Autowired
    private ContractorTagDAO2 contractorTagDAO;

    public List<Integer> getContractorSimulatorCategoryIds(Integer auditTypeId, ContractorAccount contractor, List<Integer> operatorIds) {
        List<Integer> categoryIds = new ArrayList<>();
        List<OperatorAccount> operators = attachOperators(contractor, operatorIds);

        if (auditTypeId == AuditType.SHELL_COMPETENCY_REVIEW) {
            categoryIds.add(0);
            return categoryIds;
        }

        List<AuditCategory> categories = new ArrayList<>();
        AuditType auditType = auditDataDAO.find(AuditType.class, auditTypeId);

        AuditCategoriesBuilder builder = new AuditCategoriesBuilder(categoryRuleCache, contractor);
        builder.setContractorTagDAO(contractorTagDAO);
        builder.setAuditDataDAO(auditDataDAO);

        ContractorAudit conAudit = new ContractorAudit();
        conAudit.setContractorAccount(contractor);
        conAudit.setAuditType(auditType);

        Set<AuditCategory> requiredCategories = builder.calculate(conAudit, operators);

        for(AuditCategory category:requiredCategories)
            categoryIds.add(category.getId());

        return categoryIds;
    }

    public Map<Integer, List<Integer>> getContractorSimulatorDocumentTypeDetailIds(ContractorAccount contractor, List<Integer> operatorIds) {
        attachOperators(contractor, operatorIds);

        AuditTypesBuilder auditTypesBuilder = documentTypesBuilder(contractor);

        Set<AuditTypesBuilder.AuditTypeDetail> requiredAuditTypeDetails = auditTypesBuilder.calculate();

        Map<Integer, List<Integer>> documents = new HashMap<>();

        for (AuditTypesBuilder.AuditTypeDetail detail : requiredAuditTypeDetails) {
            AuditType auditType = detail.rule.getAuditType();
            List<Integer> ruleIds = collectContractorSimulatorDocumentIds(auditTypesBuilder, auditType);
            documents.put(auditType.getId(), ruleIds);
        }

        return documents;
    }

    private List<OperatorAccount> attachOperators(ContractorAccount contractor, List<Integer> operatorIds) {
        List<OperatorAccount> operators = new ArrayList<>();
        for (Integer id:operatorIds) {
            ContractorOperator conOp = new ContractorOperator();
            OperatorAccount operator = auditDataDAO.find(OperatorAccount.class, id);
            operators.add(operator);
            conOp.setOperatorAccount(operator);
            conOp.setContractorAccount(contractor);
            contractor.getOperators().add(conOp);
        }

        return operators;
    }

    private List<Integer> collectContractorSimulatorDocumentIds(AuditTypesBuilder auditTypesBuilder, AuditType auditType) {
        List<Integer> auditTypeIds = new ArrayList<>();
        boolean includeAlways = false;
        for (AuditTypeRule rule : auditTypesBuilder.getRules()) {
            if (rule.getAuditType() == null 
                    || rule.getAuditType().equals(auditType)) {
                // We have a matching rule
                if (includeAlways) {
                    // We are already including this auditType always, so we
                    // can ignore any rules after this
                } else {
                    if (rule.getDependentAuditType() != null || rule.getQuestion() != null || rule.getTag() != null
                            || rule.isManuallyAdded()) {
                        auditTypeIds.add(rule.getId());
                    } else {
                        // We found a rule that will always include this
                        // audit
                        includeAlways = true;
                    }
                }
            }                
        }

    return auditTypeIds;
    }

    private AuditTypesBuilder documentTypesBuilder(ContractorAccount contractor) {
        AuditTypesBuilder auditTypesBuilder = new AuditTypesBuilder(typeRuleCache, contractor);
        auditTypesBuilder.setContractorTagDAO(contractorTagDAO);
        auditTypesBuilder.setAuditDataDAO(auditDataDAO);

        return auditTypesBuilder;
    }

    private DocumentTypesBuilder documentTypesBuilder(int contractorId) {
        ContractorAccount contractor = auditDataDAO.find(ContractorAccount.class, contractorId);

        return documentTypesBuilder(contractor);
    }
}
