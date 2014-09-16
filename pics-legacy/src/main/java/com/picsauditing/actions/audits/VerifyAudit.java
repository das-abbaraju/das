package com.picsauditing.actions.audits;

import com.picsauditing.jpa.entities.*;
import com.picsauditing.service.AuditDataService;
import com.picsauditing.util.AnswerMap;
import com.picsauditing.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

public class VerifyAudit extends AuditActionSupport {

	private static final long serialVersionUID = -4976847934505647430L;
	private static final ArrayList<String> EMR_PROBLEMS;
	private static final ArrayList<String> OSHA_PROBLEMS;
	private static final ArrayList<String> EMR_EXEMPTION_REASONS;
	private static final ArrayList<String> OSHA_EXEMPTION_REASONS;
    public static final List<Integer> additionalQuestionsToShow = Collections.unmodifiableList(
            Arrays.asList(3565, 3566, 3567, 3568, 3570));
    public static final List<Integer> neverShowQuestionsToShow = Collections.unmodifiableList(
            Arrays.asList(2447, 2448, 15353, 15354));

    @Autowired
    private AuditDataService auditDataService;

    static {
		EMR_PROBLEMS = new ArrayList<>();
		EMR_PROBLEMS.add("");
		EMR_PROBLEMS.add("Need EMR");
		EMR_PROBLEMS.add("Need Loss Run");
		EMR_PROBLEMS.add("Not Insurance Issued");
		EMR_PROBLEMS.add("Incorrect Upload");
		EMR_PROBLEMS.add("Incorrect Year");
		
		EMR_EXEMPTION_REASONS = new ArrayList<>();
		EMR_EXEMPTION_REASONS.add("Insurance premium too small");
		EMR_EXEMPTION_REASONS.add("Does not carry workers comp");
		EMR_EXEMPTION_REASONS.add("Less than 3 years old");
		
		OSHA_PROBLEMS = new ArrayList<>();
		OSHA_PROBLEMS.add("");
		OSHA_PROBLEMS.add("Contradicting Data");
		OSHA_PROBLEMS.add("Missing 300");
		OSHA_PROBLEMS.add("Missing 300a");
		OSHA_PROBLEMS.add("Incomplete");
		OSHA_PROBLEMS.add("Incorrect Form");
		OSHA_PROBLEMS.add("Incorrect Year");
		
		OSHA_EXEMPTION_REASONS = new ArrayList<>();
		OSHA_EXEMPTION_REASONS.add("SIC code");
		OSHA_EXEMPTION_REASONS.add("Number of Employees (10 or less)");
	}
	
	
	private List<AuditData> pqfQuestions = null;
	private Map<OperatorAccount, ContractorAuditOperator> caos;
	private List<Integer> allCaoIDs;
	private OshaAudit oshaAudit;

	private List<AuditData> applicableAuditData = null;

	public String execute() throws Exception {
		this.findConAudit();
		return SUCCESS;
	}

	public ArrayList<String> getOshaProblems() {
		return OSHA_PROBLEMS;
	}

	public ArrayList<String> getEmrProblems() {
		return EMR_PROBLEMS;
	}
	

	public ArrayList<String> getOshaExemptReason() {
		return OSHA_EXEMPTION_REASONS;
	}

	public ArrayList<String> getEmrExemptReason() {
		return EMR_EXEMPTION_REASONS;
	}

	public List<AuditData> getPqfQuestions() {
		if (pqfQuestions == null) {
			pqfQuestions = auditDataDAO.findCustomPQFVerifications(conAudit.getId());
			
			Iterator<AuditData> value = pqfQuestions.iterator();
			while (value.hasNext()) {
				AuditData auditData = value.next();
				if (!auditData.getQuestion().isVisibleInAudit(conAudit)) {
					value.remove();
				}
			}
		}

		return pqfQuestions;
	}

	public void setPqfQuestions(List<AuditData> pqfQuestions) {
		this.pqfQuestions = pqfQuestions;
	}

	public Comparator<AuditData> getDataComparator() {
		return new Comparator<AuditData>() {
			public int compare(AuditData o1, AuditData o2) {
				if (o1 == null)
					return -1;

				return o1.compareTo(o2);
			}
		};
	}

	public List<AuditData> getApplicableAuditData() {
		if (applicableAuditData == null) {
			applicableAuditData = new ArrayList<AuditData>();
			Map<Integer, AuditData> questionAuditData = new HashMap<Integer, AuditData>();
			// Build map of AQ.id to AuditData
			for (AuditData auditData : conAudit.getData()) {
				questionAuditData.put(auditData.getQuestion().getId(), auditData);
			}
			// Iterate over categories, check for isApplies
			for (AuditCatData acd : conAudit.getCategories()) {
				if (acd.isApplies() && !OshaAudit.isSafetyStatisticsCategory(acd.getCategory().getId())) {
					// Iterator over all questions, if exist then we'll add to
					// return result
					for (AuditQuestion aq : acd.getCategory().getQuestions()) {
						if (questionAuditData.containsKey(aq.getId()))
							applicableAuditData.add(questionAuditData.get(aq.getId()));
					}
				}
			}
		}
		return applicableAuditData;
	}
	public OshaAudit getOsha() {
		if (conAudit.getAuditType().isAnnualAddendum()) { 
			if (oshaAudit == null) { 
				oshaAudit = new OshaAudit(conAudit);
			}
			return oshaAudit;
		}
		return null;
	}
	
	public boolean showOsha(OshaType oshaType) {
		if (conAudit == null || !conAudit.getAuditType().isAnnualAddendum())
			return false;
		if (OshaType.OSHA.equals(oshaType)) {
            if (getOsha() != null && getOsha().getSafetyStatistics(OshaType.OSHA) != null)
			    return true;
            else
                return false;
		}
		if (OshaType.COHS.equals(oshaType)) {
			return conAudit.isDataExpectedAnswer(
					AuditQuestion.COHS_KEPT_ID, "Yes");
		}
		if (OshaType.UK_HSE.equals(oshaType)) {
            if (getOsha() != null && getOsha().getSafetyStatistics(OshaType.UK_HSE) != null)
                return true;
            else
                return false;
		}
		
		return true;
	}

    public boolean isOshaFileVisible(OshaType oshaType) {
        String answer = oshaAudit.getSpecificRate(oshaType, OshaRateType.FileUpload);
        return (answer != null && !Strings.EMPTY_STRING.equals(answer));
    }

    public List<AuditData> getVisibleOshaQuestions(OshaType oshaType) {
        List<AuditData> visibleList = new ArrayList<AuditData>();
        for (AuditData data:oshaAudit.getQuestionsToVerify(oshaType)) {
            if (data.getQuestion() != null) {
                AnswerMap answerMap = auditDataService.loadAnswerMap(data);
                if (data.getQuestion().isVisible(answerMap)) {
                    visibleList.add(data);
                }
            }
        }

        return visibleList;
    }

	public boolean isShowQuestionToVerify(AuditQuestion auditQuestion, boolean isAnswered) {
        if (neverShowQuestionsToShow.contains(auditQuestion.getId()))
            return false;

        if ("Calculation".equals(auditQuestion.getQuestionType()))
            return false;

        for (AuditCategory ac : auditQuestion.getCategory().getChildren()) {
            if (ac.getTopParent().getId() != AuditCategory.CITATIONS)
                return true;
            else {
                return showQuestion(auditQuestion);
            }
        }

        return false;
    }

    private boolean showQuestion(AuditQuestion auditQuestion) {
        if (auditQuestion.isRequired()) return true;

        if (additionalQuestionsToShow.contains(auditQuestion.getId()))
            return true;

        return false;
    }

    public Map<OperatorAccount, ContractorAuditOperator> getCaos() {
		if (caos == null) {
			allCaoIDs = new ArrayList<>();
			caos = new HashMap<>();

			for (ContractorAuditOperator cao : conAudit.getOperatorsVisible()) {
				// TODO Should we ignore incomplete and pending statuses, unless
				// percent verified == 100
				if (caos.get(cao.getOperator()) == null
						&& (cao.getStatus().isSubmitted() || cao.getStatus().isResubmitted())) {
					caos.put(cao.getOperator(), cao);
					allCaoIDs.add(cao.getId());
				}
			}
		}

		return caos;
	}

    public boolean allCaosAre(AuditStatus status) {
        for (ContractorAuditOperator cao: caos.values()) {
            if (cao.getStatus() != status) {
                return false;
            }
        }
        return true;
    }

	public String getAllCaoIDs() {
		if (allCaoIDs == null)
			getCaos();

		return Strings.implode(allCaoIDs);
	}
}
