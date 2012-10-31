package com.picsauditing.actions.audits;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.picsauditing.jpa.entities.AuditCatData;
import com.picsauditing.jpa.entities.AuditCategory;
import com.picsauditing.jpa.entities.AuditData;
import com.picsauditing.jpa.entities.AuditQuestion;
import com.picsauditing.jpa.entities.ContractorAuditOperator;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.jpa.entities.OshaAudit;
import com.picsauditing.jpa.entities.OshaType;
import com.picsauditing.util.Strings;

public class VerifyAudit extends AuditActionSupport {

	private static final long serialVersionUID = -4976847934505647430L;
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
		ArrayList<String> list = new ArrayList<String>();
		list.add("");
		list.add("Contradicting Data");
		list.add("Missing 300");
		list.add("Missing 300a");
		list.add("Incomplete");
		list.add("Incorrect Form");
		list.add("Incorrect Year");
		return list;
	}

	public ArrayList<String> getEmrProblems() {
		ArrayList<String> list = new ArrayList<String>();
		list.add("");
		list.add("Need EMR");
		list.add("Need Loss Run");
		list.add("Not Insurance Issued");
		list.add("Incorrect Upload");
		list.add("Incorrect Year");
		return list;
	}

	public ArrayList<String> getOshaExemptReason() {
		ArrayList<String> list = new ArrayList<String>();
		list.add("SIC code");
		list.add("Number of Employees (10 or less)");
		return list;
	}

	public ArrayList<String> getEmrExemptReason() {
		ArrayList<String> list = new ArrayList<String>();
		list.add("Insurance premium to small");
		list.add("Does not carry workers comp");
		list.add("Less than 3 years old");
		return list;
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
			return conAudit.isDataExpectedAnswer(
					AuditQuestion.OSHA_KEPT_ID, "Yes");
		}
		if (OshaType.COHS.equals(oshaType)) {
			return conAudit.isDataExpectedAnswer(
					AuditQuestion.COHS_KEPT_ID, "Yes");
		}
		if (OshaType.UK_HSE.equals(oshaType)) {
			return conAudit.isDataExpectedAnswer(
					AuditQuestion.UK_HSE_KEPT_ID, "Yes");
		}
		
		return true;
	}

	public boolean isShowQuestionToVerify(AuditQuestion auditQuestion, boolean isAnswered) {
		int questionid = auditQuestion.getId();
		if (questionid == 2447 || questionid == 2448)
			return false;
		for (AuditCategory ac : auditQuestion.getCategory().getChildren()) {
			if (ac.getTopParent().getId() != AuditCategory.CITATIONS)
				return true;
			else {
				if (auditQuestion.isRequired() || questionid == 3565 || questionid == 3566 || questionid == 3567
						|| questionid == 3568)
					return true;
			}
		}

		return false;
	}

	public Map<OperatorAccount, ContractorAuditOperator> getCaos() {
		if (caos == null) {
			allCaoIDs = new ArrayList<Integer>();
			caos = new HashMap<OperatorAccount, ContractorAuditOperator>();

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

	public String getAllCaoIDs() {
		if (allCaoIDs == null)
			getCaos();

		return Strings.implode(allCaoIDs);
	}
}
