package com.picsauditing.audits;

import com.picsauditing.auditbuilder.*;
import com.picsauditing.dao.BasicDAO;
import com.picsauditing.featuretoggle.Features;
import com.picsauditing.jpa.entities.AccountLevel;
import com.picsauditing.jpa.entities.AuditCategory;
import com.picsauditing.jpa.entities.AuditType;
import com.picsauditing.jpa.entities.AuditTypeRule;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.jpa.entities.ContractorOperator;
import com.picsauditing.jpa.entities.LowMedHigh;
import com.picsauditing.jpa.entities.OperatorAccount;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

public class AuditBuilderFactory {

    @Autowired
    DocumentService documentService;
    @Autowired
    AuditBuilder auditBuilder;
    @Autowired
    AuditPercentCalculator auditPercentCalculator;
    @Autowired
    AuditBuilder2 newAuditBuilder;
    @Autowired
    AuditPercentCalculator2 newAuditPercentCalculator;
    @Autowired
    private AuditTypeRuleCache typeRuleCache;
    @Autowired
    private AuditCategoryRuleCache auditCategoryRuleCache;
    @Autowired
    private com.picsauditing.auditbuilder.AuditTypesBuilder auditTypesBuilder2;
    @Autowired
    private com.picsauditing.auditbuilder.dao.AuditDataDAO2 auditDataDao;
    @Autowired
    protected BasicDAO dao;

    List<AuditCategory> categories = new ArrayList<>();

    public void buildAudits(ContractorAccount contractorAccount) {
        if (newAuditBuilderEnabled()) {
            newAuditBuilder.buildAudits(contractorAccount.getId());
        } else {
            auditBuilder.buildAudits(contractorAccount);
        }
    }

    public void percentCalculateComplete(ContractorAudit contractorAudit) {
        if (newAuditPercentCalculatorEnabled()) {
            newAuditPercentCalculator.percentCalculateComplete(contractorAudit.getId(), true);
        } else {
            auditPercentCalculator.percentCalculateComplete(contractorAudit, true);
        }
    }

    public void recalculateCategories(ContractorAudit audit) {
        if (newAuditBuilderEnabled()) {
            newAuditBuilder.recalculateCategories(audit.getId());
        } else {
            auditBuilder.recalculateCategories(audit);
        }
    }

    public List<AuditCategory> getContractorSimulatorCategories(int auditTypeId, ContractorAccount contractor) {
        categories.clear();

        if (newAuditBuilderEnabled()) {
            collectContractorSimulatorCategoriesFromService(auditTypeId, contractor);
        } else {
            collectContractorSimulatorCategories(auditTypeId, contractor);
        }

        return categories;
    }

    private void collectContractorSimulatorCategoriesFromService(int auditTypeId, ContractorAccount contractor) {
        AuditType auditType = dao.find(AuditType.class, auditTypeId);

        Set<AuditCategory> requiredCategories = new HashSet<>();
        List<Integer> categoryIds = documentService.getContractorSimulatorCategoryIds(auditTypeId, createSimulatedContractor(contractor), getClientSiteIds(contractor));
        for(int id:categoryIds) {
            if (id == 0) {
                AuditCategory category = new AuditCategory();
                category.setName("Previewing Categories is not supported for this audit");
                requiredCategories.add(category);
                return;
            } else {
                requiredCategories.add(dao.find(AuditCategory.class, id));
            }
        }

        for (AuditCategory category : auditType.getTopCategories()) {
            addCategory(categories, category, requiredCategories, auditType);
        }
    }

    private void collectContractorSimulatorCategories(int auditTypeId, ContractorAccount contractor) {
        if (auditTypeId == AuditType.SHELL_COMPETENCY_REVIEW) {
            AuditCategory category = new AuditCategory();
            category.setName("Previewing Categories is not supported for this audit");
            categories.add(category);
            return;
        }

        AuditType auditType = dao.find(AuditType.class, auditTypeId);

        AuditCategoriesBuilder builder = new AuditCategoriesBuilder(auditCategoryRuleCache, contractor);

        List<OperatorAccount> operators = collectOperators(contractor);

        ContractorAudit conAudit = new ContractorAudit();
        conAudit.setContractorAccount(contractor);
        conAudit.setAuditType(auditType);

        Set<AuditCategory> requiredCategories = builder.calculate(conAudit, operators);
        for (AuditCategory category : auditType.getTopCategories()) {
            addCategory(categories, category, requiredCategories, auditType);
        }
    }

    private List<OperatorAccount> collectOperators(ContractorAccount contractor) {
        List<OperatorAccount> operators = new ArrayList<>();
        for (ContractorOperator conOp:contractor.getOperators()) {
            operators.add(conOp.getOperatorAccount());
        }
        return operators;
    }

    private void addCategory(List<AuditCategory> categories, AuditCategory category, Set<AuditCategory> requiredCategories, AuditType auditType) {
        if (requiredCategories.contains(category)) {
            categories.add(category);
            category.setSubCategories(new ArrayList<AuditCategory>());
            for (AuditCategory subCategory : auditType.getCategories()) {
                if (category.equals(subCategory.getParent())) {
                    addCategory(category.getSubCategories(), subCategory, requiredCategories, auditType);
                }
            }
        }
    }

    public Map<AuditType, List<AuditTypeRule>> getContractorSimulatorAudits(ContractorAccount contractor) {
        Map<AuditType, List<AuditTypeRule>> audits = new TreeMap<AuditType, List<AuditTypeRule>>();
        if (newAuditBuilderEnabled()) {
            collectContractorSimulatorAuditsFromService(contractor, audits);
        } else {
            collectContractorSimulatorAudits(contractor, audits);
        }

        return audits;
    }

    private void collectContractorSimulatorAuditsFromService(ContractorAccount contractor, Map<AuditType, List<AuditTypeRule>> audits) {
        Map<Integer, List<Integer>> detailIds = documentService.getContractorSimulatorDocumentTypeDetailIds(createSimulatedContractor(contractor), getClientSiteIds(contractor));

        for (int auditTypeId:detailIds.keySet()) {
            AuditType auditType = dao.find(AuditType.class, auditTypeId);
            List<AuditTypeRule> rules = new ArrayList<>();
            if (detailIds.get(auditTypeId).size() > 0) {
                rules = dao.findByIDs(AuditTypeRule.class, detailIds.get(auditTypeId));
            }
            audits.put(auditType, rules);
        }
    }

    private List<Integer> getClientSiteIds(ContractorAccount contractor) {
        List<Integer> ids = new ArrayList<>();
        for (OperatorAccount operator:contractor.getOperatorAccounts()) {
            ids.add(operator.getId());
        }

        return ids;
    }

    private com.picsauditing.auditbuilder.entities.ContractorAccount createSimulatedContractor(ContractorAccount contractor) {
        com.picsauditing.auditbuilder.entities.ContractorAccount simulatedContractor = new com.picsauditing.auditbuilder.entities.ContractorAccount();

        simulatedContractor.setSafetyRisk(convertLowMedHigh(contractor.getSafetyRisk()));
        simulatedContractor.setProductRisk(convertLowMedHigh(contractor.getProductRisk()));
        simulatedContractor.setTransportationRisk(convertLowMedHigh(contractor.getTransportationRisk()));

        simulatedContractor.setAccountLevel(convertAccountLevel(contractor.getAccountLevel()));

        simulatedContractor.setSoleProprietor(contractor.getSoleProprietor());
        simulatedContractor.setOnsiteServices(contractor.isOnsiteServices());
        simulatedContractor.setOffsiteServices(contractor.isOffsiteServices());
        simulatedContractor.setMaterialSupplier(contractor.isMaterialSupplier());
        simulatedContractor.setTransportationServices(contractor.isTransportationServices());
        simulatedContractor.setSafetySensitive(contractor.isSafetySensitive());

        return simulatedContractor;
    }

    private com.picsauditing.auditbuilder.entities.AccountLevel convertAccountLevel(AccountLevel level) {
        if (level == null)
            return null;
        return com.picsauditing.auditbuilder.entities.AccountLevel.valueOf(level.name());
    }

    private com.picsauditing.auditbuilder.entities.LowMedHigh convertLowMedHigh(LowMedHigh risk) {
        if (risk == null)
            return null;
        return com.picsauditing.auditbuilder.entities.LowMedHigh.valueOf(risk.name());
    }

    private void collectContractorSimulatorAudits(ContractorAccount contractor, Map<AuditType, List<AuditTypeRule>> audits) {
        AuditTypesBuilder builder = new AuditTypesBuilder(typeRuleCache, contractor);
        for (AuditTypesBuilder.AuditTypeDetail detail : builder.calculate()) {
            AuditType auditType = detail.rule.getAuditType();

            List<AuditTypeRule> rules = getAuditTypeRules(builder, auditType);
            audits.put(auditType, rules);
        }
    }

    private List<AuditTypeRule> getAuditTypeRules(AuditTypesBuilder builder, AuditType auditType) {
        boolean includeAlways = false;
        List<AuditTypeRule> rules = new ArrayList<AuditTypeRule>();
        for (AuditTypeRule rule : builder.getRules()) {
            if (rule.getAuditType() == null || rule.getAuditType().equals(auditType)) {
                // We have a matching rule
                if (includeAlways) {
                    // We are already including this auditType always, so we
                    // can ignore any rules after this
                } else {
                    if (rule.getDependentAuditType() != null || rule.getQuestion() != null || rule.getTag() != null
                            || rule.isManuallyAdded()) {
                        rules.add(rule);
                    } else {
                        // We found a rule that will always include this
                        // audit
                        includeAlways = true;
                    }
                }
            }
        }
        return rules;
    }

    private static boolean newAuditBuilderEnabled() {
        try {
            return Features.USE_NEW_AUDIT_BUILDER.isActive();
        } catch (Exception e) {
            return false;
        }
    }

    private static boolean newAuditPercentCalculatorEnabled() {
        try {
            return Features.USE_NEW_AUDIT_PERCENT_CALCULATOR.isActive();
        } catch (Exception e) {
            return false;
        }
    }

}
