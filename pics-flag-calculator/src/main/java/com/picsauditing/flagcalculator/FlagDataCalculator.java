package com.picsauditing.flagcalculator;

import com.picsauditing.flagcalculator.dao.FlagCalculatorDAO;
import com.picsauditing.flagcalculator.entities.*;
import com.picsauditing.flagcalculator.messaging.FlagChange;
import com.picsauditing.flagcalculator.messaging.FlagChangePublisher;
import com.picsauditing.flagcalculator.service.*;
import com.picsauditing.flagcalculator.util.DateBean;
import com.picsauditing.flagcalculator.util.Strings;
import com.picsauditing.flagcalculator.util.YearList;
import org.apache.commons.lang.StringUtils;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

public class FlagDataCalculator implements FlagCalculator {
    @Autowired
    private FlagCalculatorDAO flagCalculatorDAO;
    @Autowired
    private FlagChangePublisher flagChangePublisher;

    private Map<FlagCriteria, FlagCriteriaContractor> contractorCriteria = null;
    private Map<FlagCriteria, List<FlagCriteriaOperator>> operatorCriteria = null;
    private Map<FlagCriteria, List<FlagDataOverride>> overrides = null;
    private OperatorAccount operator = null;
    private ContractorOperator contractorOperator = null;
    private Map<Integer, List<Integer>> correspondingMultiYearCriteria = null;

    private boolean worksForOperator = true;
    private boolean shouldPublishChanges = false;
    private final Logger logger = LoggerFactory.getLogger(FlagDataCalculator.class);

    public FlagDataCalculator() {}

    public FlagDataCalculator(Integer contractorOperatorID) {
        initialize(contractorOperatorID, new HashMap<Integer, List<Integer>>());
    }

    public FlagDataCalculator(Integer contractorOperatorID, Map<Integer, List<Integer>> overrides) {
        initialize(contractorOperatorID, overrides);
    }

    public void initialize(Integer contractorOperatorID, Map<Integer, List<Integer>> overrides) {
        contractorOperator = flagCalculatorDAO.find(ContractorOperator.class, contractorOperatorID);
        setContractorCriteria(contractorOperator.getContractorAccount().getFlagCriteria());
        setCorrespondingMultiYearCriteria(flagCalculatorDAO.getCorrespondingMultiscopeCriteriaIds());
        setOperator(contractorOperator.getOperatorAccount());
        setOperatorCriteria(FlagService.getFlagCriteriaInherited(contractorOperator.getOperatorAccount()));
        setOverrides(overrides);
    }

//    public FlagDataCalculator(FlagCriteriaContractor conCriteria, FlagCriteriaOperator opCriteria) {
//        contractorCriteria = new HashMap<>();
//        contractorCriteria.put(conCriteria.getCriteria(), conCriteria);
//        operatorCriteria = new HashMap<>();
//        if (operatorCriteria.get(opCriteria.getCriteria()) == null) {
//            operatorCriteria.put(opCriteria.getCriteria(), new ArrayList<FlagCriteriaOperator>());
//        }
//        operatorCriteria.get(opCriteria.getCriteria()).add(opCriteria);
//        this.flagCalculatorDAO = new FlagCalculatorDAO(em);
//    }
//
//    public FlagDataCalculator() {
//        this.flagCalculatorDAO = new FlagCalculatorDAO(em);
//    }

//    public static void setEntityManager(EntityManager em) {
//        FlagDataCalculator.em = em;
//    }

    public List<FlagData> calculate() {
        Map<FlagCriteria, com.picsauditing.flagcalculator.entities.FlagData> dataSet = new HashMap<>();

        boolean flaggable = isFlaggableContractor();
        for (FlagCriteria key : operatorCriteria.keySet()) {
            for (FlagCriteriaOperator fco : operatorCriteria.get(key)) {
                FlagColor flag = FlagColor.Green;
                if (flaggable && contractorCriteria.containsKey(key)) {
                    Boolean flagged = isFlagged(fco, contractorCriteria.get(key));
                    if (flagged != null) {
                        if (overrides != null) {
                            FlagDataOverride override = hasForceDataFlag(key, operator);
                            if (override != null) {
                                flag = override.getForceflag();
                            } else if (flagged) {
                                flag = fco.getFlag();
                            }
                        } else if (flagged) {
                            flag = fco.getFlag();
                        }

                        com.picsauditing.flagcalculator.entities.FlagData data = new com.picsauditing.flagcalculator.entities.FlagData();
                        data.setCriteria(key);
                        data.setContractor(contractorCriteria.get(key).getContractor());
                        data.setCriteriaContractor(contractorCriteria.get(key));
                        data.setOperator(operator);
                        data.setFlag(flag);
                        data.setAuditColumns(new User(User.SYSTEM));

						/*
						 * This logic is intended, if the criteria is an AU then
						 * we only add if the account is full and not a sole
						 * proprietor
						 */
                        if (data.getCriteria().getAuditType() != null
                                && !AuditService.isAnnualAddendum(data.getCriteria().getAuditType().getId())
                                || (data.getContractor().getAccountLevel().isFull() && !data.getContractor()
                                .getSoleProprietor())) {
                            if (dataSet.get(key) == null) {
                                dataSet.put(key, data);
                            } else if (dataSet.get(key).getFlag().isWorseThan(flag)) {
                                dataSet.put(key, data);
                            }
                        } else if (data.getContractor().getAccountLevel().isFull()) {
                            if (dataSet.get(key) == null) {
                                dataSet.put(key, data);
                            } else if (dataSet.get(key).getFlag().isWorseThan(flag)) {
                                dataSet.put(key, data);
                            }
                        }
                    }
                }
            }
        }

        return new ArrayList<FlagData>(dataSet.values());
    }

    private boolean isFlaggableContractor() {
        if (contractorCriteria.size() == 0)
            return false;
        Collection<FlagCriteriaContractor> criteriaList = contractorCriteria.values();
        if (criteriaList.size() == 0)
            return false;
        ContractorAccount contractor = criteriaList.iterator().next().getContractor();

        if (contractor == null) {
            return false;
        }

        if (contractor.getStatus().equals(AccountStatus.Pending) ||
                contractor.getStatus().equals(AccountStatus.Deactivated) ||
                contractor.getStatus().equals(AccountStatus.Requested)) {
            return false;
        }
        return true;
    }

    /**
     * Determines whether or not this criteria should be flagged.
     *
     * @param opCriteria
     * @param conCriteria
     * @return True if the flag criteria is not being met (i.e. Red Flagged), or
     *         False if a criteria is met (i.e. Green Flagged). NULL can be
     *         returned from this method when flagging does not apply.
     */
    private Boolean isFlagged(FlagCriteriaOperator opCriteria, FlagCriteriaContractor conCriteria) {
        if (!opCriteria.getCriteria().equals(conCriteria.getCriteria())) {
            throw new RuntimeException("FlagDataCalculator: Operator and Contractor Criteria must be of the same type");
        }

        FlagCriteria criteria = opCriteria.getCriteria();
        String hurdle = criteria.getDefaultValue();
        ContractorAccount con = conCriteria.getContractor();

        if (criteria.isAllowCustomValue() && StringUtils.isNotEmpty(opCriteria.getHurdle())) {
            hurdle = opCriteria.getHurdle();
        }

//        if (criteriaEligibleForRulesBasedInsurance(opCriteria)) {
//            String newHurdle = findRulesBasedInsuranceCriteriaLimit(con, opCriteria);
//            if (newHurdle != null) {
//                hurdle = newHurdle;
//            }
//        }

        // Check if we need to match tags
        if (opCriteria.getTag() != null) {
            boolean found = false;
            for (ContractorTag tag : con.getOperatorTags()) {
                if (tag.getTag().getId() == opCriteria.getTag().getId()) {
                    found = true;
                }
            }

            if (!found) {
                return null;
            }
        }

        String answer = conCriteria.getAnswer();
        if (criteria.getAuditType() != null) {
            if (!worksForOperator || con.getAccountLevel().isBidOnly()) {
                // This is a check for if the contractor doesn't
                // work for the operator (Search for new), or is a bid only
                if (!AuditService.isPicsPqf(criteria.getAuditType().getId())) {
                    // Ignore all audit requirements other than PQF
                    return null;
                }
            }

            if (con.getAudits() == null) {
                return null;
            }

            if (AuditService.isAnnualAddendum(criteria.getAuditType().getId())) {
                // Annual Update Audit
                int count = 0;
                // Check to see if there is any AUs
                boolean hasAnnualUpdate = false;

                // Checking for at least 3 active annual updates
                for (ContractorAudit ca : con.getAudits()) {
                    if (ca.getAuditType().equals(criteria.getAuditType()) && !AuditService.isAuditExpired(ca)
                            && isAuditVisibleToOperator(ca, getOperator())) {
                        hasAnnualUpdate = true;
                        boolean auditIsGood = false;
                        for (ContractorAuditOperator cao : ca.getOperators()) {
                            if (!auditIsGood && AuditService.hasCaop(cao, getOperator().getId())) {
                                if (!cao.getStatus().before(criteria.getRequiredStatus())) {
                                    auditIsGood = true;
                                } else if (cao.getStatus().isSubmitted() && con.getAccountLevel().isBidOnly()) {
									/*
									 * I don't think Bid-only contractors are
									 * going to get AUs anymore So this may not
									 * be needed in the future See above, this
									 * line will never get run. When we do our
									 * rewrite, let's remove this section
									 */
                                    auditIsGood = true;
                                }
                            }
                        }
                        if (!worksForOperator) {
                            if (AuditService.hasCaoStatusAfter(ca, AuditStatus.Incomplete)) {
                                auditIsGood = true;
                            }
                        }

                        if (auditIsGood) {
                            count++;
                        }
                    }
                }

                if (!hasAnnualUpdate) {
                    // There aren't any AUs, so it must not be required
                    return null;
                }

                // Return true if they are missing one of their AUs
                return (count < 3);
            } else if ("number".equals(criteria.getDataType()) && AuditService.isAuditScoreable(criteria.getAuditType())) {
                // Check for Audits with scoring
                ContractorAudit scoredAudit = null;
                for (ContractorAudit ca : con.getAudits()) {
                    if (ca.getAuditType().equals(criteria.getAuditType()) && !AuditService.isAuditExpired(ca)) {
                        scoredAudit = ca;
                        break;
                    }
                }
                if (scoredAudit == null) {
                    return null;
                }
                boolean r = false;

                if (criteria.getRequiredStatus() != null) {
                    if (!AuditService.hasCaoStatus(scoredAudit, criteria.getRequiredStatus())) {
                        return null;
                    }
                }

                if (scoredAudit != null) {
                    try {
                        if (">".equals(criteria.getComparison())) {
                            r = scoredAudit.getScore() > Float.parseFloat(hurdle);
                        } else if ("<".equals(criteria.getComparison())) {
                            r = scoredAudit.getScore() < Float.parseFloat(hurdle);
                        } else if ("=".equals(criteria.getComparison())) {
                            r = scoredAudit.getScore() == Float.parseFloat(hurdle);
                        } else if ("!=".equals(criteria.getComparison())) {
                            r = scoredAudit.getScore() != Float.parseFloat(hurdle);
                        }
                    } catch (NumberFormatException nfe) {
                    }
                }
                return r;
            } else {
                return checkAuditStatus(criteria, con);
            }
        } else {
            if (!auditIsApplicableForThisOperator(criteria, con)) {
                return null;
            }
            if (criteria.getRequiredStatus() != null) {
                if (criteria.getRequiredStatus().after(AuditStatus.Submitted) && !conCriteria.isVerified()
                        && criteria.getQuestion() != null && AuditService.getAuditType(criteria.getQuestion()) != null
                        && AuditService.isHasSubmittedStep(AuditService.getAuditType(criteria.getQuestion()).getWorkFlow())) {
                    if (criteria.isFlaggableWhenMissing()) {
                        return true;
                    } else {
                        return null;
                    }
                }
                else {
                    for (ContractorAudit ca : con.getAudits()) {
                        if (criteria.getQuestion() != null && ca.getAuditType().equals(AuditService.getAuditType(criteria.getQuestion())) && !AuditService.isAuditExpired(ca)) {
                            if (!worksForOperator) {
                                if (AuditService.hasCaoStatusAfter(ca, AuditStatus.Incomplete)) {
                                    return false;
                                }
                            }

                            if (AuditService.getAuditType(criteria.getQuestion()).getId() == AuditType.ANNUALADDENDUM) {
                                ContractorAudit annualUpdate = AuditService.getAfterPendingAnnualUpdates(new YearList(), con).get(criteria.getMultiYearScope());
                                if (annualUpdate == null || !ca.getAuditFor().equals(annualUpdate.getAuditFor())) {
                                    continue;
                                }
                            }

                            List<ContractorAuditOperator> caos = ca.getOperators();
                            if (AuditService.isWCB(ca.getAuditType().getId())) {
                                caos = findCaosForCurrentWCB(con, AuditService.getAuditType(criteria.getQuestion()));
                            }

                            for (ContractorAuditOperator cao : caos) {
                                if (cao.isVisible() && AuditService.hasCaop(cao, getOperator().getId())) {
                                    if (!flagCAO(criteria, cao)) {
                                        return null;
                                    } else {
                                        break;
                                    }
                                }
                            }

                            break;
                        }
                    }
                }
            }

            final String dataType = criteria.getDataType();
            final String comparison = criteria.getComparison();

            try {
                if (dataType.equals("boolean")) {
                    return (Boolean.parseBoolean(answer) == Boolean.parseBoolean(hurdle));
                }

                if (dataType.equals("number")) {
                    float answer2 = Float.parseFloat(answer.replace(",", ""));
                    float hurdle2 = Float.parseFloat(hurdle.replace(",", ""));
                    if (criteria.getOshaRateType() != null) {
                        if (criteria.getOshaRateType().equals(OshaRateType.TrirWIA)) {
                            return answer2 > TradeService.getWeightedIndustryAverage(con) * hurdle2 / 100;
                        }

                        if (criteria.getOshaRateType().equals(OshaRateType.LwcrNaics)) {
                            return answer2 > (IndustryAverageService.getLwcrIndustryAverage(conCriteria.getContractor()) * hurdle2) / 100;
                        }

                        if (criteria.getOshaRateType().equals(OshaRateType.TrirNaics)) {
                            return answer2 > (IndustryAverageService.getTrirIndustryAverage(conCriteria.getContractor()) * hurdle2) / 100;
                        }

                        if (criteria.getOshaRateType().equals(OshaRateType.DartNaics)) {
                            return answer2 > (IndustryAverageService.getDartIndustryAverage(conCriteria.getContractor().getNaics(), flagCalculatorDAO) * hurdle2) / 100;
                        }
                    }

                    if (comparison.equals("=")) {
                        return answer2 == hurdle2;
                    }
                    if (comparison.equals(">")) {
                        return answer2 > hurdle2;
                    }
                    if (comparison.equals("<")) {
                        return answer2 < hurdle2;
                    }
                    if (comparison.equals(">=")) {
                        return answer2 >= hurdle2;
                    }
                    if (comparison.equals("<=")) {
                        return answer2 <= hurdle2;
                    }
                    if (comparison.equals("!=")) {
                        return answer2 != hurdle2;
                    }
                }

                if (dataType.equals("string")) {
                    if (comparison.equals("NOT EMPTY")) {
                        return StringUtils.isEmpty(answer);
                    }
                    if (comparison.equalsIgnoreCase("contains")) {
                        return answer.contains(hurdle);
                    }
                    if (comparison.equals("=")) {
                        return hurdle.equals(answer);
                    }
                }

                if (dataType.equals("date")) {
                    Date conDate = DateBean.parseDate(answer);
                    Date opDate;

                    if (hurdle.equals("Today")) {
                        opDate = new Date();
                    } else {
                        opDate = DateBean.parseDate(hurdle);
                    }

                    if (comparison.equals("<")) {
                        return conDate.before(opDate);
                    }
                    if (comparison.equals(">")) {
                        return conDate.after(opDate);
                    }
                    if (comparison.equals("=")) {
                        return conDate.equals(opDate);
                    }
                }
                return false;
            } catch (Exception e) {
                logger.warn("Datatype is {} but values was not. (FlagCriteriaContractor {})", dataType, conCriteria.getId());
                return true;
            }
        }
    }

    private Boolean checkAuditStatus(FlagCriteria criteria, ContractorAccount con) {
        // Any other audit, PQF, or Policy
        int count = 0;
        int notApplicableCount = 0;
        List<ContractorAudit> audits = AuditService.getAuditByAuditType(con, criteria.getAuditType());
        for (ContractorAudit ca : audits) {
            if (!AuditService.isAuditExpired(ca)) {
                if (!worksForOperator) {
                    if (AuditService.hasCaoStatusAfter(ca, AuditStatus.Incomplete)) {
                        count++;
                        continue;
                    }
                }

                List<ContractorAuditOperator> caos = ca.getOperators();
                if (AuditService.isWCB(ca.getAuditType().getId())) {
                    caos = findCaosForCurrentWCB(con, criteria.getAuditType());
                }

                boolean foundApplicableCao = false;
                for (ContractorAuditOperator cao : caos) {
                    if (cao.isVisible() && AuditService.hasCaop(cao, getOperator().getId())) {
                        foundApplicableCao = true;
                        if (flagCAO(criteria, cao)) {
                            count++;
                            break;
                        } else if (cao.getStatus().isSubmitted() && con.getAccountLevel().isBidOnly()) {
                            count++;
                            break;
                        }
                    }
                }
                if (!foundApplicableCao)
                    notApplicableCount++;
            } else
                notApplicableCount++;
        }

        if (audits.size() == 0 || notApplicableCount == audits.size()) {
            if (criteria.isFlaggableWhenMissing()) {
                return true;
            }
            return null;
        } else if (count + notApplicableCount == audits.size()) {
            return false;
        } else if (criteria.getAuditType().isHasMultiple()) {
            return true;
        } else if (count > 0) {
            return false;
        }

        return true;
    }

    //    private boolean criteriaEligibleForRulesBasedInsurance(FlagCriteriaOperator opCriteria) {
//        return opCriteria.getCriteria().isInsurance()
//                && RulesRunner.operatorHasRulesBasedInsuranceCriteria(opCriteria.getOperator());
//    }
//
//    private String findRulesBasedInsuranceCriteriaLimit(ContractorAccount contractor, FlagCriteriaOperator operatorCriteria) {
//        for (InsuranceCriteriaContractorOperator criteria: contractor.getInsuranceCriteriaContractorOperators()) {
//            if (criteria.getOperatorAccount().equals(operatorCriteria.getOperator())
//                    && criteria.getFlagCriteria().equals(operatorCriteria.getCriteria())) {
//                return Integer.toString(criteria.getInsuranceLimit());
//            }
//        }
//        return null;
//    }
//
    private boolean auditIsApplicableForThisOperator(FlagCriteria criteria, ContractorAccount con) {
        if (!worksForOperator) {
            return true;
        }

        for (ContractorAudit ca : con.getAudits()) {
            if (criteria.getQuestion() != null && ca.getAuditType().equals(AuditService.getAuditType(criteria.getQuestion())) && !AuditService.isAuditExpired(ca)) {
                for (ContractorAuditOperator cao : ca.getOperators()) {
                    if (cao.isVisible()) {
                        for (ContractorAuditOperatorPermission caop : cao.getCaoPermissions()) {
                            if (caop.getOperator().getId() == operator.getId()) {
                                return true;
                            }
                        }
                    }
                }
                return false;
            }
        }
        return true;
    }

    private boolean isAuditVisibleToOperator(ContractorAudit ca, OperatorAccount op) {
        for (ContractorAuditOperator cao : ca.getOperators()) {
            if (cao.isVisible()) {
                for (ContractorAuditOperatorPermission caop : cao.getCaoPermissions()) {
                    if (caop.getOperator().getId() == op.getId()) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    private void setContractorCriteria(Collection<FlagCriteriaContractor> list) {
        contractorCriteria = new HashMap<>();
        for (FlagCriteriaContractor value : list) {
            contractorCriteria.put(value.getCriteria(), value);
        }
    }

    private List<ContractorAuditOperator> findCaosForCurrentWCB(ContractorAccount contractor, AuditType auditType) {
        String auditFor = determineAuditForYear();
        for (ContractorAudit audit : contractor.getAudits()) {
            if (isCurrentYearWCBAudit(auditType, auditFor, audit)) {
                return audit.getOperators();
            }
        }

        return Collections.emptyList();
    }

    private String determineAuditForYear() {
        if (DateBean.isGracePeriodForWCB()) {
            return Integer.toString(DateBean.getPreviousWCBYear());
        }

        return DateBean.getWCBYear();
    }

    private boolean isCurrentYearWCBAudit(AuditType auditType, String auditFor, ContractorAudit audit) {
        return audit != null && audit.getAuditType() != null && auditType.getId() == audit.getAuditType().getId()
                && auditFor.equals(audit.getAuditFor());
    }

    private boolean flagCAO(FlagCriteria criteria, ContractorAuditOperator cao) {
        if (criteria.getRequiredStatus() == null) {
            return true;
        }

        String compare = criteria.getRequiredStatusComparison();
        if (StringUtils.isEmpty(compare)) {
            compare = "<";
        }

        if (compare.equals(">")) {
            return !cao.getStatus().after(criteria.getRequiredStatus());
        }
        if (compare.equals("=")) {
            return !cao.getStatus().equals(criteria.getRequiredStatus());
        }
        if (compare.equals("!=")) {
            return cao.getStatus().equals(criteria.getRequiredStatus());
        }

        return !cao.getStatus().before(criteria.getRequiredStatus());
    }

    public void setOperatorCriteria(Collection<FlagCriteriaOperator> list) {
        operatorCriteria = new HashMap<>();
        for (FlagCriteriaOperator value : list) {
            if (operatorCriteria.get(value.getCriteria()) == null) {
                operatorCriteria.put(value.getCriteria(), new ArrayList<FlagCriteriaOperator>());
            }

            operatorCriteria.get(value.getCriteria()).add(value);
        }
    }

    public Map<FlagCriteria, List<FlagDataOverride>> getOverrides() {
        return overrides;
    }

    public void setOverrides(Map<Integer, List<Integer>> overrides) {
        Map<FlagCriteria, List<FlagDataOverride>> overridesMap = new HashMap<>();

        for (Integer flagCriteriaID : overrides.keySet()) {
            List<Integer> flagDataOverrideIds = overrides.get(flagCriteriaID);
            List<FlagDataOverride> flagDataOverrides = new ArrayList<>();
            for (Integer flagDataOverrideID : flagDataOverrideIds) {

                flagDataOverrides.add(flagCalculatorDAO.find(FlagDataOverride.class, flagDataOverrideID));
            }
            overridesMap.put(flagCalculatorDAO.find(FlagCriteria.class, flagCriteriaID), flagDataOverrides);
        }

        this.overrides = overridesMap;
    }

    public Map<Integer, List<Integer>> getCorrespondingMultiYearCriteria() {
        return correspondingMultiYearCriteria;
    }

    public void setCorrespondingMultiYearCriteria(Map<Integer, List<Integer>> equivalentMultiYearCriteria) {
        this.correspondingMultiYearCriteria = equivalentMultiYearCriteria;
    }

    public boolean isWorksForOperator() {
        return worksForOperator;
    }

    public void setWorksForOperator(boolean worksForOperator) {
        this.worksForOperator = worksForOperator;
    }

    public void setOperator(OperatorAccount operator) {
        this.operator = operator;
    }

    public OperatorAccount getOperator() {
        return operator;
    }

    private FlagDataOverride hasForceDataFlag(FlagCriteria key, OperatorAccount operator) {
        String auditYear = null;

        List<Integer> criteriaIds = new ArrayList<>();
        FlagCriteriaContractor fcc = contractorCriteria.get(key);
        if (correspondingMultiYearCriteria.containsKey(key.getId())) {
            auditYear = extractYear(fcc.getAnswer2());
            criteriaIds.addAll(correspondingMultiYearCriteria.get(key.getId()));
        } else {
            criteriaIds.add(key.getId());
        }

        List<FlagDataOverride> fdos = getApplicableFlagDataOverrides(operator, criteriaIds);

        // if not audit year, then must be plain, no year scope criteria
        if (auditYear == null) {
            if (fdos.size() > 0) {
                return fdos.get(0);
            } else {
                return null;
            }
        }

        // we have multi-year criteria
        FlagDataOverride found = null;
        // find fdo with same year
        for (FlagDataOverride fdo : fdos) {
            if (auditYear.equals(fdo.getYear())) {
                found = fdo;
                break;
            }
        }

        if (found == null) {
            if (searchOverridesByCriteria(fdos, key) != null) {
                shiftOverrides(fdos);
            }
            return null; // no fdo found for year
        }

        if (found.getCriteria().equals(key))
        {
            return found; // found no change
        }

        FlagDataOverride fdo1 = found;
        FlagDataOverride fdo2 = null;
        FlagDataOverride fdo3 = null;

        fdo2 = searchOverridesByCriteria(fdos, key);
        if (fdo2 == null) {
            removeFlagDataOverride(fdo1);
            FlagCriteria nextCriteriaSkip = getNextCriteria(key);
            if (nextCriteriaSkip != null) {
                for (FlagDataOverride fdo : fdos) {
                    int curYear = Integer.parseInt(fdo1.getYear());
                    int previousYear = Integer.parseInt(fdo.getYear());
                    if (fdo.getCriteria().equals(nextCriteriaSkip) && (previousYear != curYear)) {
                        fdo3 = fdo;
                        break;
                    }
                }
            }
            if (fdo3 != null) {
                removeFlagDataOverride(fdo3);
            }
            fdo1.setCriteria(key);
            flagCalculatorDAO.save(fdo1);
            if (fdo3 != null) {
                flagCalculatorDAO.deleteData(FlagDataOverride.class, "id=" + fdo3.getId());
            }
            addFlagDataOverride(fdo1);
            return fdo1;
        }

        FlagCriteria nextCriteria = getNextCriteria(fdo2.getCriteria());
        if (nextCriteria == null) {
            removeFlagDataOverride(fdo1);
            removeFlagDataOverride(fdo2);

            fdo2.copyPayloadFrom(fdo1);
            flagCalculatorDAO.save(fdo2);
            flagCalculatorDAO.deleteData(FlagDataOverride.class, "id=" + fdo1.getId());
            addFlagDataOverride(fdo2);
            return fdo2;
        }

        fdo3 = searchOverridesByCriteria(fdos, nextCriteria);
        if (fdo3 == null) {
            fdo3 = (FlagDataOverride) fdo2.clone();
            fdo3.setCriteria(nextCriteria);
        } else {
            removeFlagDataOverride(fdo3);
        }

        removeFlagDataOverride(fdo1);
        removeFlagDataOverride(fdo2);

        fdo3.copyPayloadFrom(fdo2);
        try {
            flagCalculatorDAO.save(fdo3);
        } catch (Exception e) {
            logger.error(e.toString());
        }

        fdo2.copyPayloadFrom(fdo1);
        flagCalculatorDAO.save(fdo2);
        flagCalculatorDAO.deleteData(FlagDataOverride.class, "id=" + fdo1.getId());
        addFlagDataOverride(fdo3);
        addFlagDataOverride(fdo2);

        return fdo2;
    }

    private FlagDataOverride searchOverridesByCriteria(List<FlagDataOverride> fdos, FlagCriteria key) {
        for (FlagDataOverride fdo : fdos) {
            if (fdo.getCriteria().equals(key)) {
                return fdo;
            }
        }
        return null;
    }

    private void shiftOverrides(List<FlagDataOverride> fdos) {
        // put fdos in reverse MultiYearScope sorted order for easier traversal
        Collections.sort(fdos, new Comparator<FlagDataOverride>() {
            @Override
            public int compare(FlagDataOverride o1, FlagDataOverride o2) {
                return o1.getCriteria().getMultiYearScope().compareTo(o2.getCriteria().getMultiYearScope());
            }
        });
        Collections.reverse(fdos);

        for (FlagDataOverride fdo:fdos) {
            removeFlagDataOverride(fdo);
            FlagCriteria nextCriteria = getNextCriteria(fdo.getCriteria());
            if (nextCriteria != null) {
                fdo.setCriteria(nextCriteria);
                flagCalculatorDAO.save(fdo);
                addFlagDataOverride(fdo);
            } else {
                flagCalculatorDAO.deleteData(FlagDataOverride.class, "id=" + fdo.getId());
            }
        }
    }

    private void addFlagDataOverride(FlagDataOverride fdo) {
        List<FlagDataOverride> list = overrides.get(fdo.getCriteria());
        if (list == null) {
            list = new ArrayList<>();
            overrides.put(fdo.getCriteria(), list);
        }
        list.add(fdo);
    }

    private void removeFlagDataOverride(FlagDataOverride fdo) {
        List<FlagDataOverride> list = overrides.get(fdo.getCriteria());
        if (list != null) {
            list.remove(fdo);
        }
    }

    private List<FlagDataOverride> getApplicableFlagDataOverrides(OperatorAccount operator, List<Integer> criteriaIds) {
        ArrayList<FlagDataOverride> fdos = new ArrayList<>();
        for (int id : criteriaIds) {
            FlagCriteria criteriaKey = new FlagCriteria();
            criteriaKey.setId(id);
            List<FlagDataOverride> flList = overrides.get(criteriaKey);
            if (flList == null) {
                continue;
            }
            if (flList.size() > 0) {
                for (FlagDataOverride flagDataOverride : flList) {
                    if (AccountService.isApplicableFlagOperator(operator, flagDataOverride.getOperator())
                            && FlagService.isInForce(flagDataOverride)) {
                        fdos.add(flagDataOverride);
                    }
                }
            }
        }
        return fdos;
    }

    private FlagCriteria getNextCriteria(FlagCriteria criteria) {
        MultiYearScope nextYear;

        if (criteria.getMultiYearScope().equals(MultiYearScope.LastYearOnly)) {
            nextYear = MultiYearScope.TwoYearsAgo;
        } else if (criteria.getMultiYearScope().equals(MultiYearScope.TwoYearsAgo)) {
            nextYear = MultiYearScope.ThreeYearsAgo;
        } else {
            return null;
        }

        List<Integer> idList = correspondingMultiYearCriteria.get(criteria.getId());

        List<FlagCriteria> criteriaList = flagCalculatorDAO.findWhere("id IN (" + Strings.implode(idList) + ")");

        for (FlagCriteria foundCriteria : criteriaList) {
            if (nextYear.equals(foundCriteria.getMultiYearScope())) {
                return foundCriteria;
            }
        }

        return null;
    }

    private String extractYear(String year) {
        if (StringUtils.isEmpty(year)) {
            return null;
        }

        int index;
        index = year.indexOf(":");
        if (index >= 0) {
            year = year.substring(index + 1);
        }

        index = year.indexOf("<br");
        if (index >= 0) {
            year = year.substring(0, index);
        }

        year = year.trim();

        return year;
    }

    public boolean saveFlagData(List<FlagData> changes) {
        List<com.picsauditing.flagcalculator.entities.FlagData> changedFlagData = new ArrayList<>();
        // Save the FlagDetail to the ContractorOperator as a JSON string
        JSONObject flagJson = new JSONObject();
        for (FlagData data : changes) {
            if (!data.isInsurance()) {
                JSONObject flag = new JSONObject();
                flag.put("category", data.getCriteriaCategory());
                flag.put("label", data.getCriteriaLabel());
                flag.put("flag", data.getFlagColor());

                flagJson.put(data.getCriteriaID(), flag);
            }
            changedFlagData.add((com.picsauditing.flagcalculator.entities.FlagData)data);
        }
        contractorOperator.setFlagDetail(flagJson.toString());

        // Find overall flag color for this operator
        FlagColor overallColor = FlagColor.Green;
        if (contractorOperator.getContractorAccount().getAccountLevel().isBidOnly()
                || contractorOperator.getContractorAccount().getStatus().isPending()
                || contractorOperator.getContractorAccount().getStatus().isDeleted()
                || contractorOperator.getContractorAccount().getStatus().isDeclined()
                || contractorOperator.getContractorAccount().getStatus().isDeactivated()) {
            overallColor = FlagColor.Clear;
        }

        for (FlagData change : changes) {
            FlagColor changeFlag = FlagColor.valueOf(change.getFlagColor());
            if (!change.isInsurance()) {
                FlagColor worst = FlagColor.getWorseColor(overallColor, changeFlag);
                overallColor = worst;
            }
        }

        boolean needNote = false;

        ContractorOperator conOperator = FlagService.getForceOverallFlag(contractorOperator);
        if (conOperator != null) { // operator has a forced flag
            contractorOperator.setFlagColor(conOperator.getForceFlag());
            contractorOperator.setFlagLastUpdated(new Date());
            if (contractorOperator.getForceBegin() != null) {
                Calendar forceFlagCreatedOn = Calendar.getInstance();
                forceFlagCreatedOn.setTime(contractorOperator.getForceBegin());
                Calendar yesterday = Calendar.getInstance();
                yesterday.add(Calendar.DATE, -1);
                Calendar tomorrow = Calendar.getInstance();
                tomorrow.add(Calendar.DATE, 1);
                if (!(forceFlagCreatedOn.after(tomorrow) || forceFlagCreatedOn.before(yesterday))) {
                    if (overallColor.equals(conOperator.getForceFlag())) {
                        contractorOperator.setBaselineFlag(overallColor);
                    }
                }
            }
        } else if (!overallColor.equals(contractorOperator.getFlagColor())) {
            if (shouldPublishChanges) {
                FlagChange flagChange = getFlagChange(contractorOperator, overallColor);
                flagChangePublisher.publish(flagChange);
            }

            if (contractorOperator.getFlagColor() == FlagColor.Clear) {
                contractorOperator.setBaselineFlag(overallColor);
                contractorOperator.setBaselineFlagDetail(flagJson.toString());
            }
            contractorOperator.setFlagColor(overallColor);
            contractorOperator.setFlagLastUpdated(new Date());

            needNote = true;
        }

        // set baselineFlag to clear and baselineFlagDetail for null baselines
        if (contractorOperator.getBaselineFlag() == null) {
            contractorOperator.setBaselineFlag(FlagColor.Clear);
            contractorOperator.setBaselineFlagDetail(flagJson.toString());
        }

        Iterator<com.picsauditing.flagcalculator.entities.FlagData> flagDataList = flagCalculatorDAO.insertUpdateDeleteManaged(contractorOperator.getFlagDatas(), changedFlagData).iterator();
        while (flagDataList.hasNext()) {
            com.picsauditing.flagcalculator.entities.FlagData flagData = flagDataList.next();
            contractorOperator.getFlagDatas().remove(flagData);
            flagCalculatorDAO.remove(flagData);
        }
        contractorOperator.setAuditColumns(new User(User.SYSTEM));
        flagCalculatorDAO.save(contractorOperator);

        return needNote;
    }

    public void setFlagCalculatorDAO(FlagCalculatorDAO flagCalculatorDAO) {
        this.flagCalculatorDAO = flagCalculatorDAO;
    }

    private FlagChange getFlagChange(ContractorOperator co, FlagColor overallColor) {
        FlagChange flagChange = new FlagChange();
        flagChange.setContractor(co.getContractorAccount());
        flagChange.setOperator(co.getOperatorAccount());
        flagChange.setFromColor(co.getFlagColor());
        flagChange.setToColor(overallColor);
        flagChange.setTimestamp(new Date());
        flagChange.setDetails(co.getFlagDetail());
        return flagChange;
    }

    @Override
    public void setShouldPublishChanges(boolean shouldPublishChanges) {
        this.shouldPublishChanges = shouldPublishChanges;
    }
}