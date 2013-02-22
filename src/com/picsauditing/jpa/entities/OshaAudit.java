package com.picsauditing.jpa.entities;

import com.picsauditing.PICS.OshaVisitable;
import com.picsauditing.PICS.OshaVisitor;
import com.picsauditing.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Decorator for ContractorAudit, specifically for when ContractorAudit is an OSHa type, that adds OSHA-specific logic.
 */
public class OshaAudit implements OshaVisitable {

    public static final int CAT_ID_OSHA = 2033; // U.S.
    public static final int CAT_ID_OSHA_ADDITIONAL = 2209; // U.S.
    public static final int CAT_ID_MSHA = 2256; // U.S.
    public static final int CAT_ID_COHS = 2086; // Canada
    public static final int CAT_ID_UK_HSE = 2092; // U.K.
    public static final int CAT_ID_FRANCE_NRIS = 1691; // France
    public static final int CAT_ID_MEXICO = 3165; // Mexico
    public static final int CAT_ID_EMR = 152; // EMR
    public static final int CAT_ID_AUSTRALIA = 3325; // Australia

    public static final int CAT_ID_OSHA_PARENT = 1153;
    public static final int CAT_ID_COHS_PARENT = 1155;
    public static final int CAT_ID_UK_HSE_PARENT = 1690;

    // Arrays can have their contents modified during runtime, so make this an unmodifiable set. Since it is only
    // loaded once, there is no runtime performance hit.
    public static final Set<Integer> SAFETY_STATISTICS_CATEGORY_IDS =
            Collections.unmodifiableSet(new HashSet<Integer>(Arrays.asList(CAT_ID_OSHA, CAT_ID_OSHA_ADDITIONAL,
                    CAT_ID_MSHA, CAT_ID_COHS, CAT_ID_UK_HSE)));
    public static final Set<Integer> DISPLAY_SAFETY_STATISTICS_CATEGORY_IDS =
            Collections.unmodifiableSet(new HashSet<Integer>(Arrays.asList(CAT_ID_OSHA,
                    CAT_ID_COHS, CAT_ID_UK_HSE, CAT_ID_EMR, CAT_ID_MEXICO)));

    private static final Logger logger = LoggerFactory.getLogger(OshaAudit.class);

    public static boolean isSafetyStatisticsCategory(int categoryId) {
        for (int safetyStatisticsCategory : SAFETY_STATISTICS_CATEGORY_IDS) {
            if (categoryId == safetyStatisticsCategory)
                return true;
        }

        return false;
    }

    public static OshaType convertCategoryToOshaType(int catId) {
        OshaType type = null;

        switch (catId) {
            case CAT_ID_OSHA:
            case CAT_ID_OSHA_ADDITIONAL:
                type = OshaType.OSHA;
                break;
            case CAT_ID_EMR:
                type = OshaType.EMR;
                break;
            case CAT_ID_MSHA:
                type = OshaType.MSHA;
                break;
            case CAT_ID_COHS:
                type = OshaType.COHS;
                break;
            case CAT_ID_UK_HSE:
                type = OshaType.UK_HSE;
                break;
            case CAT_ID_FRANCE_NRIS:
                type = OshaType.FRANCE_NRIS;
                break;
            case CAT_ID_MEXICO:
                type = OshaType.MEXICO;
                break;
        }

        return type;
    }

    private ContractorAudit contractorAudit;

    private Map<OshaType, SafetyStatistics> safetyStatisticsMap;
    private Map<OshaType, Boolean> displaySafetyStatisticsMap;

    public OshaAudit(ContractorAudit contractorAudit) {
        assert (contractorAudit.getAuditType().isAnnualAddendum());

        this.contractorAudit = contractorAudit;
        displaySafetyStatisticsMap = new HashMap<OshaType, Boolean>();
        initializeDisplaySafetyStatistics();
        safetyStatisticsMap = new HashMap<OshaType, SafetyStatistics>();
        initializeStatistics();
    }

    public ContractorAudit getContractorAudit() {
        return contractorAudit;
    }

    private void initializeDisplaySafetyStatistics() {
        displaySafetyStatisticsMap.put(OshaType.OSHA, false);
        displaySafetyStatisticsMap.put(OshaType.COHS, false);
        displaySafetyStatisticsMap.put(OshaType.UK_HSE, false);
        displaySafetyStatisticsMap.put(OshaType.EMR, false);
        displaySafetyStatisticsMap.put(OshaType.MEXICO, false);
        for (AuditCatData category : getCategories()) {
            if (category.getCategory().getId() == CAT_ID_OSHA_PARENT) {
                displaySafetyStatisticsMap.put(OshaType.OSHA, category.isApplies());
                displaySafetyStatisticsMap.put(OshaType.EMR, category.isApplies());
            }
            if (category.getCategory().getId() == CAT_ID_COHS_PARENT) {
                displaySafetyStatisticsMap.put(OshaType.COHS, category.isApplies());
            }
            if (category.getCategory().getId() == CAT_ID_UK_HSE_PARENT) {
                displaySafetyStatisticsMap.put(OshaType.UK_HSE, category.isApplies());
            }
            if (category.getCategory().getId() == CAT_ID_MEXICO) {
                displaySafetyStatisticsMap.put(OshaType.MEXICO, category.isApplies());
            }
        }
    }

    public String getAuditFor() {
        return contractorAudit.getAuditFor();
    }

    public int getId() {
        return contractorAudit.getId();
    }

    public List<ContractorAuditOperator> getCaos() {
        return contractorAudit.getOperatorsVisible();
    }

    private List<AuditCatData> getCategories() {
        return contractorAudit.getCategories();
    }

    public Collection<SafetyStatistics> getStatistics() {
        return safetyStatisticsMap.values();
    }

    private void initializeStatistics() {
        int year = new Integer(contractorAudit.getAuditFor());

        for (AuditCatData category : getCategories()) {
            int categoryId = category.getCategory().getId();
            if (!DISPLAY_SAFETY_STATISTICS_CATEGORY_IDS.contains(categoryId)) {
                continue;
            }

            OshaType oshaType = convertCategoryToOshaType(categoryId);
            Boolean shouldDisplay = displaySafetyStatisticsMap.get(oshaType);
            if (shouldDisplay == null || shouldDisplay.equals(Boolean.FALSE)) {
                continue;
            }

            SafetyStatistics safetyStatistics = null;
            if (oshaType == OshaType.OSHA) {
                safetyStatistics = new OshaStatistics(year, contractorAudit.getData(), category.isApplies());
            } else if (oshaType == OshaType.COHS) {
                safetyStatistics = new CohsStatistics(year, contractorAudit.getData(), category.isApplies());
            } else if (oshaType == OshaType.UK_HSE) {
                safetyStatistics = new UkStatistics(year, contractorAudit.getData(), category.isApplies());
            } else if (oshaType == OshaType.EMR) {
                safetyStatistics = new EmrStatistics(year, contractorAudit.getData(), category.isApplies());
            } else if (oshaType == OshaType.MEXICO) {
                safetyStatistics = new MexicoStatistics(year, contractorAudit.getData(), category.isApplies());
            }

            if (safetyStatistics != null) {
                safetyStatisticsMap.put(oshaType, safetyStatistics);
                safetyStatistics.setVerified(isVerified(oshaType));
            }
        }
    }

    public SafetyStatistics getSafetyStatistics(OshaType oshaType) {
        return safetyStatisticsMap.get(oshaType);
    }

    public String getSpecificRate(OshaType oshaType, OshaRateType rateType) {
        if (getSafetyStatistics(oshaType) == null) {
            return Strings.EMPTY_STRING;
        }

        return getSafetyStatistics(oshaType).getStats(rateType);
    }

    // Used in Line 65 in verification_detail.jsp line 298 in verification_audit.jsp
    public Integer getFileUploadId(OshaType oshaType) {
        if (getSafetyStatistics(oshaType) == null || getSafetyStatistics(oshaType).getFileUpload() == null
                || getSafetyStatistics(oshaType).getFileUpload().getQuestion() == null) {
            return Integer.valueOf(-1);
        }

        return getSafetyStatistics(oshaType).getFileUpload().getQuestion().getId();
    }

    public List<AuditData> getQuestionsToVerify(OshaType oshaType) {
        if (getSafetyStatistics(oshaType) == null) {
            return Collections.emptyList();
        }

        return getSafetyStatistics(oshaType).getQuestionsToVerify();
    }

    public Collection<AuditData> getAllQuestionsInOshaType(OshaType oshaType) {
        if (getSafetyStatistics(oshaType) == null || getSafetyStatistics(oshaType).getAnswerMap() == null) {
            return Collections.emptyList();
        }

        return getSafetyStatistics(oshaType).getAnswerMap().values();
    }

    public boolean isEmpty(OshaType oshaType) {
        return (getSafetyStatistics(oshaType) == null);
    }

    public String getComment(OshaType oshaType) {
        if (getSafetyStatistics(oshaType) == null || getSafetyStatistics(oshaType).getFileUpload() == null) {
            return Strings.EMPTY_STRING;
        }

        return getSafetyStatistics(oshaType).getFileUpload().getComment();
    }

    public AuditData stampOshaComment(OshaType oshaType, String comment) {
        if (getSafetyStatistics(oshaType) == null) {
            return null;
        }

        AuditData fileUpload = getSafetyStatistics(oshaType).getFileUpload();
        if (fileUpload != null) {
            fileUpload.setComment(comment);
        }

        return fileUpload;
    }

    @Override
    public void accept(OshaVisitor visitor) {
        for (SafetyStatistics stats : getStatistics()) {
            visitor.gatherData(stats);
        }
    }

    // If one cao status is complete, it is safe to assume it's verified.
    public boolean isVerified() {
        for (ContractorAuditOperator cao : contractorAudit.getOperators()) {
            if (cao.isVisible() && cao.getStatus().isComplete())
                return true;
        }

        return false;
    }

    public boolean isVerified(OshaType oshaType) {
        if (getSafetyStatistics(oshaType) == null || getSafetyStatistics(oshaType).getAnswerMap() == null
                || getSafetyStatistics(oshaType).getAnswerMap().get(OshaRateType.Hours) == null) {
            return false;
        }

        return getSafetyStatistics(oshaType).getAnswerMap().get(OshaRateType.Hours).isVerified();
    }

    public Date getVerifiedDate(OshaType oshaType) {
        if (getSafetyStatistics(oshaType) == null || getSafetyStatistics(oshaType).getAnswerMap() == null
                || getSafetyStatistics(oshaType).getAnswerMap().get(OshaRateType.Hours) == null) {
            return null;
        }

        return getSafetyStatistics(oshaType).getAnswerMap().get(OshaRateType.Hours).getDateVerified();
    }

    public User getAuditor(OshaType oshaType) {
        if (getSafetyStatistics(oshaType) == null || getSafetyStatistics(oshaType).getAnswerMap() == null
                || getSafetyStatistics(oshaType).getAnswerMap().get(OshaRateType.Hours) == null) {
            return null;
        }

        return getSafetyStatistics(oshaType).getAnswerMap().get(OshaRateType.Hours).getAuditor();
    }
}
