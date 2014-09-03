package com.picsauditing.auditbuilder;

import com.picsauditing.auditbuilder.dao.ContractorTagDAO;
import com.picsauditing.auditbuilder.dao.DocumentDataDAO;
import com.picsauditing.auditbuilder.entities.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

public class DocumentService {
    @Autowired
    private DocumentDataDAO auditDataDAO;
    @Autowired
    private DocumentTypeRuleCache typeRuleCache;
    @Autowired
    private DocumentCategoryRuleCache categoryRuleCache;
    @Autowired
    private ContractorTagDAO contractorTagDAO;
    @Autowired
    private DocumentTypesBuilder documentTypesBuilder;
    @Autowired
    private DocumentCategoriesBuilder documentCategoriesBuilder;

     public List<Integer> getCategoryIds(int auditId, List<Integer> operatorIds) {
         ContractorDocument audit = auditDataDAO.find(ContractorDocument.class, auditId);

        List<OperatorAccount> operators = auditDataDAO.findByIDs(OperatorAccount.class, operatorIds);

        return getCategoryIds(audit, operators);
    }

    private List<Integer> getCategoryIds(ContractorDocument audit, List<OperatorAccount> operators) {
        List<Integer> categoryIds = new ArrayList<>();

        DocumentCategoriesBuilder builder = initializeCategoriesBuilder(audit.getContractorAccount());
        Set<DocumentCategory> categories = builder.calculate(audit, operators);
        for (DocumentCategory category:categories) {
            categoryIds.add(category.getId());
        }

        return categoryIds;
    }

    public List<Integer> getDocumentRuleIds(int contractorId) {
        DocumentTypesBuilder builder = initializeTypesBuilder(contractorId);
        builder.calculate();
        List<DocumentTypeRule> rules = builder.getRules();

        List<Integer> ids = new ArrayList<>();
        for(DocumentTypeRule rule:rules) {
            ids.add(rule.getId());
        }

        return ids;
    }

    public List<Integer> getCategoryRuleIds(int contractorId, int auditTypeId) {
        ContractorAccount contractor = auditDataDAO.find(ContractorAccount.class, contractorId);
        AuditType auditType = auditDataDAO.find(AuditType.class, auditTypeId);
        List<DocumentCategoryRule> rules = categoryRuleCache.getRules(contractor, auditType);

        List<Integer> ids = new ArrayList<>();
        for(DocumentCategoryRule rule:rules) {
            ids.add(rule.getId());
        }

        return ids;
    }

    public Map<Integer, List<Integer>> getTypeDetailIds(Integer contractorId) {
        Map<Integer, List<Integer>> documentDetailIds = new HashMap<>();

        DocumentTypesBuilder documentTypesBuilder = initializeTypesBuilder(contractorId);

        Set<DocumentTypesBuilder.AuditTypeDetail> requiredAuditTypeDetails = documentTypesBuilder.calculate();
        for (DocumentTypesBuilder.AuditTypeDetail detail:requiredAuditTypeDetails) {
            List<Integer> operatorIds = new ArrayList<>();
            for (OperatorAccount operator:detail.operators) {
                operatorIds.add(operator.getId());
            }
            documentDetailIds.put(detail.rule.getId(), operatorIds);
        }

        return documentDetailIds;
    }

    public List<Integer> getSimulatorCategoryIds(Integer auditTypeId, ContractorAccount contractor, List<Integer> operatorIds) {
        List<Integer> categoryIds = new ArrayList<>();
        List<OperatorAccount> operators = attachOperators(contractor, operatorIds);

        if (auditTypeId == AuditType.SHELL_COMPETENCY_REVIEW) {
            categoryIds.add(0);
            return categoryIds;
        }

        AuditType auditType = auditDataDAO.find(AuditType.class, auditTypeId);

        DocumentCategoriesBuilder builder = initializeCategoriesBuilder(contractor);

        ContractorDocument conAudit = new ContractorDocument();
        conAudit.setContractorAccount(contractor);
        conAudit.setAuditType(auditType);

        Set<DocumentCategory> requiredCategories = builder.calculate(conAudit, operators);

        for(DocumentCategory category:requiredCategories)
            categoryIds.add(category.getId());

        return categoryIds;
    }

    public Map<Integer, List<Integer>> getSimulatorTypeDetailIds(ContractorAccount contractor, List<Integer> operatorIds) {
        attachOperators(contractor, operatorIds);

        DocumentTypesBuilder documentTypesBuilder = initializeTypesBuilder(contractor);

        Set<DocumentTypesBuilder.AuditTypeDetail> requiredAuditTypeDetails = documentTypesBuilder.calculate();

        Map<Integer, List<Integer>> documents = new HashMap<>();

        for (DocumentTypesBuilder.AuditTypeDetail detail : requiredAuditTypeDetails) {
            AuditType auditType = detail.rule.getAuditType();
            List<Integer> ruleIds = collectContractorSimulatorDocumentIds(documentTypesBuilder, auditType);
            documents.put(auditType.getId(), ruleIds);
        }

        return documents;
    }

    public boolean isCategoryApplicable(int categoryId, int auditId, int caoId) {
        DocumentCategory category = auditDataDAO.find(DocumentCategory.class, categoryId);
        ContractorDocument audit = auditDataDAO.find(ContractorDocument.class, auditId);
        ContractorDocumentOperator cao = auditDataDAO.find(ContractorDocumentOperator.class, caoId);

        initializeCategoriesBuilder(audit.getContractorAccount());

        Collection<OperatorAccount> operators = new ArrayList<>();
        for (ContractorDocumentOperatorPermission caop:cao.getCaoPermissions()) {
            operators.add(caop.getOperator());
        }

        documentCategoriesBuilder.calculate(audit, operators);
        return documentCategoriesBuilder.isCategoryApplicable(category, cao);
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

    private List<Integer> collectContractorSimulatorDocumentIds(DocumentTypesBuilder documentTypesBuilder, AuditType auditType) {
        List<Integer> auditTypeIds = new ArrayList<>();
        boolean includeAlways = false;
        for (DocumentTypeRule rule : documentTypesBuilder.getRules()) {
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

    private DocumentTypesBuilder initializeTypesBuilder(ContractorAccount contractor) {
        documentTypesBuilder.setRuleCache(typeRuleCache);
        documentTypesBuilder.setContractor(contractor);

        return documentTypesBuilder;
    }

    private DocumentTypesBuilder initializeTypesBuilder(int contractorId) {
        ContractorAccount contractor = auditDataDAO.find(ContractorAccount.class, contractorId);
       return initializeTypesBuilder(contractor);
    }

    private DocumentCategoriesBuilder initializeCategoriesBuilder(ContractorAccount contractor) {
        documentCategoriesBuilder.setRuleCache(categoryRuleCache);
        documentCategoriesBuilder.setContractor(contractor);
        return documentCategoriesBuilder;
    }

    public DocumentCategoriesBuilder initializeCategoriesBuilder(int contractorId) {
        ContractorAccount contractor = auditDataDAO.find(ContractorAccount.class, contractorId);
        return initializeCategoriesBuilder(contractor);
    }
}
