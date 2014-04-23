package com.picsauditing.flagcalculator.entities;

import com.picsauditing.flagcalculator.service.AuditService;

import java.util.*;

/**
 * Decorator for ContractorAudit, specifically for when ContractorAudit is an OSHa type, that adds OSHA-specific logic.
 */
public class OshaAudit /*implements OshaVisitable*/ {
//
    public static final int CAT_ID_OSHA = 2033; // U.S.
    public static final int CAT_ID_OSHA_ADDITIONAL = 2209; // U.S.
    public static final int CAT_ID_MSHA = 2256; // U.S.
    public static final int CAT_ID_COHS = 2086; // Canada
    public static final int CAT_ID_UK_HSE = 2092; // U.K.
    public static final int CAT_ID_FRANCE_NRIS = 1691; // France
    public static final int CAT_ID_MEXICO = 3165; // Mexico
    public static final int CAT_ID_EMR = 152; // EMR
    public static final int CAT_ID_AUSTRALIA = 3325; // Australia
	public static final int CAT_ID_IRElAND = 3428; // Ireland
	public static final int CAT_ID_SOUTH_AFRICA = 2810; // South Africa
	public static final int CAT_ID_SINGAPORE_MOM = 3655;
	public static final int CAT_ID_AUSTRIA = 3752;
	public static final int CAT_ID_ITALY = 3749;
	public static final int CAT_ID_PORTUGAL = 3754;
	public static final int CAT_ID_TURKEY = 3757;
	public static final int CAT_ID_SPAIN = 3748;
	public static final int CAT_ID_SWITZERLAND = 3704;
	public static final int CAT_ID_POLAND = 3756;
	public static final int CAT_ID_DENMARK = 3762;
	public static final int CAT_ID_CZECH = 3764;
	public static final int CAT_ID_HUNGARY = 3760;
	public static final int CAT_ID_GREECE = 3766;
//
    public static final int CAT_ID_OSHA_PARENT = 1153;
    public static final int CAT_ID_COHS_PARENT = 1155;
    public static final int CAT_ID_UK_HSE_PARENT = 1690;

//    // Arrays can have their contents modified during runtime, so make this an unmodifiable set. Since it is only
//    // loaded once, there is no runtime performance hit.
//    public static final Set<Integer> SAFETY_STATISTICS_CATEGORY_IDS =
//            Collections.unmodifiableSet(new HashSet<Integer>(Arrays.asList(CAT_ID_OSHA, CAT_ID_OSHA_ADDITIONAL,
//                    CAT_ID_MSHA, CAT_ID_COHS, CAT_ID_UK_HSE)));
    public static final Set<Integer> DISPLAY_SAFETY_STATISTICS_CATEGORY_IDS =
            Collections.unmodifiableSet(new HashSet<Integer>(Arrays.asList(CAT_ID_OSHA,
                    CAT_ID_COHS, CAT_ID_UK_HSE, CAT_ID_EMR, CAT_ID_MEXICO, CAT_ID_AUSTRALIA, CAT_ID_IRElAND,
                    CAT_ID_SOUTH_AFRICA, CAT_ID_SWITZERLAND, CAT_ID_SINGAPORE_MOM, CAT_ID_TURKEY,
                    CAT_ID_SPAIN, CAT_ID_POLAND, CAT_ID_AUSTRIA, CAT_ID_ITALY, CAT_ID_PORTUGAL, CAT_ID_DENMARK,
                    CAT_ID_CZECH, CAT_ID_HUNGARY, CAT_ID_GREECE)));

//    private static final Logger logger = LoggerFactory.getLogger(OshaAudit.class);
//
//    public static boolean isSafetyStatisticsCategory(int categoryId) {
//        // why not?
//        // return SAFETY_STATISTICS_CATEGORY_IDS.contains(categoryId);
//        for (int safetyStatisticsCategory : SAFETY_STATISTICS_CATEGORY_IDS) {
//            if (categoryId == safetyStatisticsCategory)
//                return true;
//        }
//
//        return false;
//    }
//
    public static OshaType convertCategoryToOshaType(int catId) {
        OshaType type = null;

	    for (OshaType oType:OshaType.values()) {
		    if (catId == oType.categoryId) {
			    type = oType;
		    }
	    }

	    if (catId == CAT_ID_OSHA_ADDITIONAL) {
		    type = OshaType.OSHA;
	    }

        return type;
    }

    private ContractorAudit contractorAudit;

    private Map<OshaType, SafetyStatistics> safetyStatisticsMap;
    private Map<OshaType, Boolean> displaySafetyStatisticsMap;
//
    public OshaAudit(ContractorAudit contractorAudit) {
        assert (AuditService.isAnnualAddendum(contractorAudit.getAuditType().getId()));

        this.contractorAudit = contractorAudit;
        displaySafetyStatisticsMap = new HashMap<OshaType, Boolean>();
        initializeDisplaySafetyStatistics();
        safetyStatisticsMap = new HashMap<OshaType, SafetyStatistics>();
        initializeStatistics();
    }

//    public ContractorAudit getContractorAudit() {
//        return contractorAudit;
//    }
//
    private void initializeDisplaySafetyStatistics() {
	    for (OshaType type:OshaType.values()) {
		    displaySafetyStatisticsMap.put(type, false);
	    }
        for (AuditCatData category : getCategories()) {
	        for (OshaType type:OshaType.values()) {
		        if (category.getCategory().getId() == type.categoryId && type.displayStats)
		            displaySafetyStatisticsMap.put(type, category.isApplies());
	        }
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
         }
    }

    public String getAuditFor() {
        return contractorAudit.getAuditFor();
    }

//    public int getId() {
//        return contractorAudit.getId();
//    }
//
//    public List<ContractorAuditOperator> getCaos() {
//        return contractorAudit.getOperatorsVisible();
//    }
//
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
            } else if (oshaType == OshaType.AUSTRALIA) {
                safetyStatistics = new AustraliaStatistics(year, contractorAudit.getData(), category.isApplies());
            } else if (oshaType == OshaType.IRELAND) {
	            safetyStatistics = new IrelandStatistics(year, contractorAudit.getData(), category.isApplies());
            } else if (oshaType == OshaType.SOUTH_AFRICA) {
		        safetyStatistics = new SouthAfricaStatistics(year, contractorAudit.getData(), category.isApplies());
            } else if (oshaType == OshaType.SINGAPORE_MOM) {
	            safetyStatistics = new SingaporeStatistics(year, contractorAudit.getData(), category.isApplies());
            } else if (oshaType == OshaType.SWITZERLAND) {
	            safetyStatistics = new SwitzerlandStatistics(year, contractorAudit.getData(), category.isApplies());
            } else if (oshaType == OshaType.TURKEY) {
	            safetyStatistics = new TurkeyStatistics(year, contractorAudit.getData(), category.isApplies());
            } else if (oshaType == OshaType.SPAIN) {
	            safetyStatistics = new SpainStatistics(year, contractorAudit.getData(), category.isApplies());
            } else if (oshaType == OshaType.POLAND) {
	            safetyStatistics = new PolandStatistics(year, contractorAudit.getData(), category.isApplies());
            } else if (oshaType == OshaType.AUSTRIA) {
	            safetyStatistics = new AustriaStatistics(year, contractorAudit.getData(), category.isApplies());
            } else if (oshaType == OshaType.ITALY) {
	            safetyStatistics = new ItalyStatistics(year, contractorAudit.getData(), category.isApplies());
            } else if (oshaType == OshaType.PORTUGAL) {
	            safetyStatistics = new PortugalStatistics(year, contractorAudit.getData(), category.isApplies());
            } else if (oshaType == OshaType.DENMARK) {
	            safetyStatistics = new DenmarkStatistics(year, contractorAudit.getData(), category.isApplies());
            } else if (oshaType == OshaType.CZECH) {
	            safetyStatistics = new CzechStatistics(year, contractorAudit.getData(), category.isApplies());
            } else if (oshaType == OshaType.HUNGARY) {
	            safetyStatistics = new HungaryStatistics(year, contractorAudit.getData(), category.isApplies());
            } else if (oshaType == OshaType.GREECE) {
	            safetyStatistics = new GreeceStatistics(year, contractorAudit.getData(), category.isApplies());
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

//    public String getSpecificRate(OshaType oshaType, OshaRateType rateType) {
//        if (getSafetyStatistics(oshaType) == null) {
//            return Strings.EMPTY_STRING;
//        }
//
//        return getSafetyStatistics(oshaType).getStats(rateType);
//    }
//
//    // Used in Line 65 in verification_detail.jsp line 298 in verification_audit.jsp
//    public Integer getFileUploadId(OshaType oshaType) {
//        if (getSafetyStatistics(oshaType) == null || getSafetyStatistics(oshaType).getFileUpload() == null
//                || getSafetyStatistics(oshaType).getFileUpload().getQuestion() == null) {
//            return Integer.valueOf(-1);
//        }
//
//        return getSafetyStatistics(oshaType).getFileUpload().getQuestion().getId();
//    }
//
//    public List<AuditData> getQuestionsToVerify(OshaType oshaType) {
//        if (getSafetyStatistics(oshaType) == null) {
//            return Collections.emptyList();
//        }
//
//        return getSafetyStatistics(oshaType).getQuestionsToVerify();
//    }
//
//    public Collection<AuditData> getAllQuestionsInOshaType(OshaType oshaType) {
//        if (getSafetyStatistics(oshaType) == null || getSafetyStatistics(oshaType).getAnswerMap() == null) {
//            return Collections.emptyList();
//        }
//
//        return getSafetyStatistics(oshaType).getAnswerMap().values();
//    }
//
//    public boolean isEmpty(OshaType oshaType) {
//        return (getSafetyStatistics(oshaType) == null);
//    }
//
//    public String getComment(OshaType oshaType) {
//        if (getSafetyStatistics(oshaType) == null || getSafetyStatistics(oshaType).getCommentAuditData() == null) {
//            return Strings.EMPTY_STRING;
//        }
//
//        String comment = getSafetyStatistics(oshaType).getCommentAuditData().getComment();
//        if (Strings.isEmpty(comment))
//            comment = Strings.EMPTY_STRING;
//        return comment;
//    }
//
//    public AuditData stampOshaComment(OshaType oshaType, String comment) {
//        if (getSafetyStatistics(oshaType) == null) {
//            return null;
//        }
//
//        AuditData data = getSafetyStatistics(oshaType).getCommentAuditData();
//        if (data != null) {
//            data.setComment(comment);
//        }
//
//        return data;
//    }
//
//    @Override
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

        return AuditService.isVerified(getSafetyStatistics(oshaType).getAnswerMap().get(OshaRateType.Hours));
    }

//    public Date getVerifiedDate(OshaType oshaType) {
//        if (getSafetyStatistics(oshaType) == null || getSafetyStatistics(oshaType).getAnswerMap() == null
//                || getSafetyStatistics(oshaType).getAnswerMap().get(OshaRateType.Hours) == null) {
//            return null;
//        }
//
//        return getSafetyStatistics(oshaType).getAnswerMap().get(OshaRateType.Hours).getDateVerified();
//    }
//
//    public User getAuditor(OshaType oshaType) {
//        if (getSafetyStatistics(oshaType) == null || getSafetyStatistics(oshaType).getAnswerMap() == null
//                || getSafetyStatistics(oshaType).getAnswerMap().get(OshaRateType.Hours) == null) {
//            return null;
//        }
//
//        return getSafetyStatistics(oshaType).getAnswerMap().get(OshaRateType.Hours).getAuditor();
//    }
}
