package com.picsauditing.actions.audits;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.picsauditing.PICS.AuditCategoryRuleCache;
import com.picsauditing.dao.AuditCategoryDataDAO;
import com.picsauditing.dao.AuditDataDAO;
import com.picsauditing.dao.CertificateDAO;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.ContractorAuditDAO;
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

	public VerifyAudit(ContractorAccountDAO accountDao, ContractorAuditDAO auditDao, AuditCategoryDataDAO catDataDao,
			AuditDataDAO auditDataDao, CertificateDAO certificateDao, AuditCategoryRuleCache auditCategoryRuleCache) {
		super(accountDao, auditDao, catDataDao, auditDataDao, certificateDao, auditCategoryRuleCache);
	}

	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN;
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
			pqfQuestions = auditDataDao.findCustomPQFVerifications(conAudit.getId());
		}

		return pqfQuestions;
	}

	public void setPqfQuestions(List<AuditData> pqfQuestions) {
		this.pqfQuestions = pqfQuestions;
	}

	public Comparator<AuditData> getDataComparator() {
		return new Comparator<AuditData>() {

			@Override
			public int compare(AuditData o1, AuditData o2) {
				if (o1 == null)
					return -1;

				return o1.compareTo(o2);
			}
		};
	}

	public OshaAudit getOsha() {
		AuditData auditData = auditDataDao.findAnswerToQuestion(conAudit.getId(), 2064);
		if (auditData != null && "Yes".equals(auditData.getAnswer())) {
			for (OshaAudit oshaAudit : conAudit.getOshas()) {
				if (oshaAudit.getType().equals(OshaType.OSHA) && oshaAudit.isCorporate()) {
					return oshaAudit;
				}
			}
		}
		return null;
	}

	public boolean isShowQuestionToVerify(AuditQuestion auditQuestion, boolean isAnswered) {
		int questionid = auditQuestion.getId();
		if (questionid == 2447 || questionid == 2448)
			return false;
		for (AuditCategory ac : auditQuestion.getCategory().getChildren()) {
			if (ac.getTopParent().getId() != AuditCategory.CITATIONS)
				return true;
			else {
				if (auditQuestion.isRequired() || questionid == 3565 || questionid == 3566
						|| questionid == 3567 || questionid == 3568)
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
				// TODO Should we ignore incomplete and pending statuses, unless percent verified == 100
				if (caos.get(cao.getOperator()) == null && !cao.getStatus().isIncomplete() && !cao.getStatus().isPending())
					caos.put(cao.getOperator(), cao);
				
				allCaoIDs.add(cao.getId());
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
