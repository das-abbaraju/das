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

     public List<Integer> getCategoryIds(int auditId, List<Integer> operatorIds) {
         ContractorAudit audit = auditDataDAO.find(ContractorAudit.class, auditId);

        List<OperatorAccount> operators = auditDataDAO.findByIDs(OperatorAccount.class, operatorIds);

        return getCategoryIds(audit, operators);
    }

    private List<Integer> getCategoryIds(ContractorAudit audit, List<OperatorAccount> operators) {
        List<Integer> categoryIds = new ArrayList<>();

        AuditCategoriesBuilder builder = documentCategoriesBuilder(audit.getContractorAccount());
        Set<AuditCategory> categories = builder.calculate(audit, operators);
        for (AuditCategory category:categories) {
            categoryIds.add(category.getId());
        }

        return categoryIds;
    }

    public List<Integer> getDocumentRuleIds(int contractorId) {
        AuditTypesBuilder builder = documentTypesBuilder(contractorId);
        builder.calculate();
        List<AuditTypeRule> rules = builder.getRules();

        List<Integer> ids = new ArrayList<>();
        for(AuditTypeRule rule:rules) {
            ids.add(rule.getId());
        }

        return ids;
    }

    public List<Integer> getCategoryRuleIds(int contractorId, int auditTypeId) {
        ContractorAccount contractor = auditDataDAO.find(ContractorAccount.class, contractorId);
        AuditType auditType = auditDataDAO.find(AuditType.class, auditTypeId);
        List<AuditCategoryRule> rules = categoryRuleCache.getRules(contractor, auditType);

        List<Integer> ids = new ArrayList<>();
        for(AuditCategoryRule rule:rules) {
            ids.add(rule.getId());
        }

        return ids;
    }

    public Map<Integer, List<Integer>> getContractorDocumentTypeDetailIds(Integer contractorId) {
        Map<Integer, List<Integer>> documentDetailIds = new HashMap<>();

        AuditTypesBuilder auditTypesBuilder = documentTypesBuilder(contractorId);

        Set<AuditTypesBuilder.AuditTypeDetail> requiredAuditTypeDetails = auditTypesBuilder.calculate();
        for (AuditTypesBuilder.AuditTypeDetail detail:requiredAuditTypeDetails) {
            List<Integer> operatorIds = new ArrayList<>();
            for (OperatorAccount operator:detail.operators) {
                operatorIds.add(operator.getId());
            }
            documentDetailIds.put(detail.rule.getId(), operatorIds);
        }

        return documentDetailIds;
    }

    public List<Integer> getContractorSimulatorCategoryIds(Integer auditTypeId, ContractorAccount contractor, List<Integer> operatorIds) {
        List<Integer> categoryIds = new ArrayList<>();
        List<OperatorAccount> operators = attachOperators(contractor, operatorIds);

        if (auditTypeId == AuditType.SHELL_COMPETENCY_REVIEW) {
            categoryIds.add(0);
            return categoryIds;
        }

        AuditType auditType = auditDataDAO.find(AuditType.class, auditTypeId);

        AuditCategoriesBuilder builder = documentCategoriesBuilder(contractor);

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

    public void clearCache() {
        typeRuleCache.clear();
        categoryRuleCache.clear();
    }

    private List<OperatorAccount> attachOperators(ContractorAccount contractor, List<Integer> operatorIds) {
        List<OperatorAccount> operators = new ArrayList<>();
        for (int id:operatorIds) {
            ContractorOperator conOp = new ContractorOperator();
            OperatorAccount operator = auditDataDAO.find(OperatorAccount.class, id);
            conOp.setOperatorAccount(operator);
            conOp.setContractorAccount(contractor);
            contractor.getOperators().add(conOp);

            operators.add(operator);
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

    private AuditTypesBuilder documentTypesBuilder(int contractorId) {
        ContractorAccount contractor = auditDataDAO.find(ContractorAccount.class, contractorId);

        return documentTypesBuilder(contractor);
    }

    private AuditCategoriesBuilder documentCategoriesBuilder(ContractorAccount contractor) {
        AuditCategoriesBuilder builder = new AuditCategoriesBuilder(categoryRuleCache, contractor);
        builder.setContractorTagDAO(contractorTagDAO);
        builder.setAuditDataDAO(auditDataDAO);

        return builder;
    }

    public AuditCategoriesBuilder getDocumentCategoriesBuilder(int contractorId) {
        ContractorAccount contractor = auditDataDAO.find(ContractorAccount.class, contractorId);
        return documentCategoriesBuilder(contractor);
    }
}
